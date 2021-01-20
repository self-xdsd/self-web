$(document).ready(
    function () {
        getOrgRepos();
    }
);
/**
 * Get Org Repos to which the authenticated user has admin rights.
 */
function getOrgRepos() {
    $("#orgReposTable").find("tbody").html('');
    $("#loadingOrgRepos").show();
    $("#github-org-info").hide();
    $.get(
        "/api/repositories/orgs",
        function(repos) {
            (repos || []).forEach(
                function(repo){
                    $("#orgReposTable").find("tbody").append(repoAsTableRow(repo));
                }
            )
            $("#loadingOrgRepos").hide();
            $("#github-org-info").show();
            $('#orgReposTable').dataTable();
        }
    );

}

/**
 * Wrap a repo's information between <li> tags, with anchor.
 */
function repoAsTableRow(repo) {
    return "<tr><td><a href='/project/" + repo.provider + "/"+ repo.repoFullName+"'>"
        + repo.repoFullName + "</a></td></tr>"
}