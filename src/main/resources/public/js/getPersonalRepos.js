$(document).ready(
    function () {
        getPersonalRepos();
    }
);
/**
 * Get the user's personal repos.
 */
function getPersonalRepos() {
    $("#repos").find("tbody").html('');
    $("#loadingPersonalRepos").show();
    $("#personal-repos-info").hide();
    $.get(
        "/api/repositories/personal",
        function(repos) {
            (repos || []).forEach(
                function(repo){
                    $("#repos").find("tbody").append(repoAsTableRow(repo));
                }
            )
            $("#loadingPersonalRepos").hide();
            $("#personal-repos-info").show();
            $('#repos').dataTable();
        }
    );
}

/**
 * Wrap a repo's information between <li> tags, with anchor.
 */
function repoAsTableRow(repo) {
    return "<tr><td><a href='/" + repo.provider + "/"+ repo.repoFullName+"'>"
        + repo.repoFullName + "</a></td></tr>"
}
