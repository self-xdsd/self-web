$.get(
    "/user",
    function(data) {
      $.get(
          "https://api.github.com/users/" + data.login + "/repos",
          function(repos) {
              console.log(repos);
              repos.sort(
                  function(one, other) {
                      if(one.fork == true) {
                          return 1
                      }
                      if(other.fork == true) {
                          return -1
                      }
                      return 0
                  }
              ).forEach(
                  function(repo){
                      $("#repos").append(repoAsLi(repo));
                  }
              )
          }
      )
    }
);

/**
 * Wrap a repo's information between <li> tags, with anchor.
 */
function repoAsLi(repo) {
    return "<li>" +
        repo.full_name
    + "</li>"
}