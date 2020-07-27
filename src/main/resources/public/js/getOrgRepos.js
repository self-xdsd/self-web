$("#loadingOrgRepos").show();
$.get(
    "/api/repositories/orgs",
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
                $("#orgReposTable").find("tbody").append(repoAsTableRow(repo));
            }
        )
        $("#loadingOrgRepos").hide();
        $('#orgReposTable').dataTable();
    }
);

/**
 * Wrap a repo's information between <li> tags, with anchor.
 */
function repoAsTableRow(repo) {
    return "<tr><td>" +
        repo.full_name
        + "</td></tr>"
}