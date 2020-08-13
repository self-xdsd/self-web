$(document).ready(
    function () {
        getPublicRepos();
    }
);
/**
 * Get the authenticated user and call Github's API to get the
 * public repos.
 */
function getPublicRepos() {
    $("#repos").find("tbody").html('');
    $("#loadingPersonalRepos").show();
    $.get(
        "/api/users/self",
        function(data) {
            $.get(
                "https://api.github.com/users/" + data.login + "/repos",
                function(repos) {
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
                            $("#repos").find("tbody").append(repoAsTableRow(repo));
                        }
                    )
                    $("#loadingPersonalRepos").hide();
                    $('#repos').dataTable();
                }
            )
        }
    );
}


/**
 * Wrap a repo's information between <li> tags, with anchor.
 */
function repoAsTableRow(repo) {
    return "<tr><td>" +
        repo.full_name
    + "</td></tr>"
}