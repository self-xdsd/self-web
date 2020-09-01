var contractsService = (function($) {

        /**
         * Fallback to call all contracts until Self Contracts Page API is implemented.
         * This is a temporary solution until then.
         * To simulate paging we take the contracts response
         * and extract the paged data using the info provided as argument.
         * @param {Object} project - project info (owner, name)
         * @param {Object} page - page info (number, size)
         */
       function getAllFallback(project, page){
            return new Promise(function(resolve, reject){
                $.get({
                    url: "/api/projects/"+project.owner+"/"+project.name+"/contracts",
                    success: function(contracts){
                        var start = (page.no - 1) * page.size;
                        var end = Math.min(start + page.size);
                        var contractsPage = contracts.splice(start, end);
                        var totalPages = Math.ceil((contracts.length + page.size - 1) / page.size)
                        resolve({
                            paged: {
                                current: page,
                                totalPages: totalPages
                            },
                            data: contractsPage
                        })
                    },
                    error: function(jqXHR){
                         if(jqXHR.status === 412){
                            var reason = JSON.parse(jqXHR.responseText).reason;
                            reject(reason);
                         }else{
                            reject(jqXHR.responseText)
                         }
                    }
                });
            });
       }

       return {
            /**
            * Get a page of contracts from server. If fail it tries to
            * fallback to get all project contracts.
            * @param {Object} project - project info (owner, name)
            * @param {Object} page - page info (number, size)
            * @param {Function} onWait - invoked before request. This function
            *  usually triggers a loading indicator on UI.
            */
            getAll: function(project, page, onWait){
                return new Promise(function(resolve, reject){
                    if(onWait){ onWait();}
                    $.get({
                        url: "/api/projects/"+project.owner+"/"+project.name
                            +"/contracts/"+page.no+"/"+page.size,
                        success: resolve,
                        error: function(jqXHR){
                             if(jqXHR.status === 412){
                                var reason = JSON.parse(jqXHR.responseText).reason;
                                reject(reason);
                             }if(jqXHR.status === 404){
                                //not found, mark error as fallback
                                reject({
                                    fallback: true,
                                    data: {
                                        project: project,
                                        page: page
                                    },
                                });
                             }else{
                                reject(jqXHR.responseText)
                             }
                        }
                    });
                })
                .catch(function(error){
                    if(error.fallback){
                        //we fallback to all contracts.
                        return getAllFallback(error.data.project, error.data.page);
                    }else{
                        //other error - just reject as it should
                        return Promise.reject(error);
                    }
                });
            },//end of getAll

            /**
            * Post a new contributor contract to project.
            * @param {Object} project - project info (owner, name)
            * @param {String} form - serialized form input
            * @param {Function} onWait - invoked before request. This function
            *  usually triggers a loading indicator on UI.
            */
            add: function(project, form, onWait){
                return new Promise(function(resolve, reject){
                    if(onWait){ onWait();}
                    $.post({
                        url: "/api/projects/"+project.owner+"/"+project.name+"/contracts",
                        data: form,
                        success: resolve,
                        error: function(jqXHR){
                            if(jqXHR.status === 400){
                                //we have a validation error
                                var errors = JSON.parse(jqXHR.responseText);
                                reject({validation: errors});
                            }else if(jqXHR.status === 412){
                                //contract was not created
                                var error = JSON.parse(jqXHR.responseText);
                                reject(error.reason)
                            }else{
                                reject(jqXHR.responseText);
                            }
                        }
                    });
                });
            }//end of add
         };
})(jQuery);