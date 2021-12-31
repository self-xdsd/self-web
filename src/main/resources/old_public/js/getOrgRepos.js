$(document).ready(
    function () {
        window.globalProvider.onChange(function(value){
            if(value === "github"){
                $("#github-org-access-info").show();
            }else {
                $("#github-org-access-info").hide();
            }
        });
        getOrgRepos();
    }
);
/**
 * Get Org Repos to which the authenticated user has admin rights.
 */
function getOrgRepos() {
    $("#orgReposTable").find("tbody").html('');
    $("#loadingOrgRepos").show();
    $("#org-access-info").hide();
    $.get(
        "/api/repositories/orgs",
        function(repos) {
            (repos || []).forEach(
                function(repo){
                    $("#orgReposTable").find("tbody").append(repoAsTableRow(repo));
                }
            )
            $("#loadingOrgRepos").hide();
            $("#org-access-info").show();
            $('#orgReposTable').dataTable();
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