var usersService = (function ($){

    var providers = {
        "github": {
            findUsers: function(term){
                return new Promise(function(resolve, reject){
                    $.get({
                        url: "https://api.github.com/search/users?per_page=100&q="+term,
                        success: function(users){
                            resolve(users.items.map(function(user){return user.login}));
                        },
                        error: function(jqXHR){
                            reject(jqXHR.responseText);
                        }
                    });
                });
            },
            exists: function(username) {
                return new Promise(function(resolve, reject){
                    $.get({
                        url: "https://api.github.com/users/"+username,
                        success: function(){
                            resolve();
                        },
                        error: function(jqXHR){
                            if(jqXHR.status === 404){
                                reject({
                                    validation: {
                                        username: "This doesn't seem to be a Github user."
                                    }
                                })
                            }else{
                                reject(jqXHR.responseText);
                            }
                        }
                    });
                });
            }
        },
        "gitlab": {
            findUsers: function(term){
                return new Promise(function(resolve){
                    //for now searching function is stubbed, since it Gitlab require authenticated calls for Search API.
                    resolve([]);
                });
            },
            exists: function(username){
                return new Promise(function(resolve, reject){
                    $.get({
                        url: "https://gitlab.com/api/v4/users?username="+username,
                        success: function (found) {
                            if (found.length === 0) {
                                reject({
                                    validation: {
                                        username: "This doesn't seem to be a Gitlab user."
                                    }
                                });
                            } else {
                                resolve();
                            }
                        },
                        error: function(jqXHR){
                           reject(jqXHR.responseText);
                        }
                    });
                });
            }
        }
    };

    function exportFindUsers(term, provider, onWait){
        if(onWait){
            onWait();
        }
        return providers[provider].findUsers(term);
    }

    function exportExists(username, provider, onWait){
        if(onWait){
            onWait();
        }
        return providers[provider].exists(username);
    }

    return {
        findUsers: exportFindUsers,
        exists: exportExists
    };
})(jQuery);