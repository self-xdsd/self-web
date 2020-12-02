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
            (repos || []).sort(
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
    return "<tr><td><a href='/github/" + repo.full_name + "'>" + repo.full_name + "</a></td></tr>"
}