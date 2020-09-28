var usersService = (function ($){

    function findUsersGithub(term){
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
    }

    function existsGithub(username){
        return new Promise(function(resolve, reject){
            $.get({
                url: "https://api.github.com/users/"+username,
                success: function(found){
                    resolve()
                },
                error: function(jqXHR){
                    if(jqXHR.status === 404){
                        reject({
                            validation: {
                                username: "Username doesn't exist!"
                            }
                        })
                    }else{
                        reject(jqXHR.responseText);
                    }
                }
            });
        });
    }


    function exportFindUsers(term, provider, onWait){
        if(onWait){
            onWait();
        }
        var users;
        switch(provider){
            case "github": {
                users = findUsersGithub(term, provider);
                break;
            }
            default: throw new Error("Searching for users of " + provider +" not supported");
        }
        return users;
    }

    function exportExists(username, provider, onWait){
        if(onWait){
            onWait();
        }
        var exists;
        switch(provider){
            case "github": {
                exists = existsGithub(username);
                break;
            }
            default: throw new Error("Checking user of " + provider +" not supported");
        }
        return exists
    }

    return {
        findUsers: exportFindUsers,
        exists: exportExists
    }
})(jQuery)