$(document).ready(
    function () {
        window.globalProvider.onChange(function(value){
            var grantLink;
            if(value === "github"){
                grantLink = "https://github.com/settings/connections/applications/a55dd23908b4dffe8df6";
            }else if(value === "gitlab"){
                grantLink = "https://gitlab.com/-/profile/applications";
            }else{
                grantLink = "#";
            }
            $('.provider-grant').attr('href', grantLink);
            var capitalized = value.charAt(0).toUpperCase() + value.slice(1);
            $('.provider').text(capitalized);
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
    return "<tr><td><a href='/" + repo.provider + "/"+ repo.repoFullName+"'>"
        + repo.repoFullName + "</a></td></tr>"
}