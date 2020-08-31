var contractsService = (function($) {
         return {
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
                             }else{
                                reject(jqXHR.responseText)
                             }
                        }
                    });
                });
            },
            add: function(project, form, onWait){
                return new Promise(function(resolve, reject){
                    if(onWait){ onWait();}
                    $.post({
                        url: "/api/projects/"+project.owner+"/"+project.name+"/contracts",
                        data: form,
                        success: resolve,
                        error: function(jqXHR){
                            if(jqXHR.status === 400){
                                var errors = JSON.parse(jqXHR.responseText);
                                reject({validation: errors});
                            }else if(jqXHR.status === 412){
                                var error = JSON.parse(jqXHR.responseText);
                                reject(error.reason)
                            }else{
                                reject(jqXHR.responseText);
                            }
                        }
                    });
                });
            }
         };
})(jQuery);