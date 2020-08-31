var contractsService = (function($) {
         return {
            getAll: function(project, onWait){
                if(onWait){
                    onWait();
                }
                return new Promise(function(resolve, reject){
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
            },
            add: function(project, form, onWait){
                if(onWait){
                    onWait();
                }
                return new Promise(function(resolve, reject){
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