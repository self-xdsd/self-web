var contractsService = (function($) {

       return {
            /**
             * Get all Project contracts from Self Contracts.
             * @param {Object} project - project info (owner, name)
             */
            getAll: function(project,onWait){
                return new Promise(function(resolve, reject){
                    if(onWait){ onWait();}
                    $.get({
                        url: "/api/projects/"+project.owner+"/"+project.name+"/contracts",
                        success: resolve,
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
            }, //end of add
            update: function(project, form, onWait) {
                return new Promise(function(resolve, reject){
                    if(onWait){ onWait();}
                    var username = $("#updateContractUsername").val();
                    var role = $("#updateContractRole").val();
                    $.post({
                        url: "/api/projects/"+project.owner+"/"+project.name+"/contracts/"
                         + username + "/update?role=" + role,
                        data: form,
                        success: resolve,
                        error: function(jqXHR){
                            alert("RESPONSE STATUS:" + jqXHR.status);
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
            } //end of update
         };
})(jQuery);