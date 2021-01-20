$(document).ready(
    function () {
        getManagedRepos();
    }
);

function projectAsTableRow(project) {
    var href;
    if(project.provider == 'github') {
        href="/project/github/" + project.repoFullName;
    } else if (project.provider == 'gitlab') {
        href="/project/gitlab/" + project.repoFullName;
    } else {
        href="#";
    }
    return [
        "<a href=" + href + ">" + project.repoFullName + "</a>"
    ]
}

/**
 * Get the authenticated user's Projects.
 */
function getManagedRepos() {
    $("#managedReposTable").dataTable({
        language: {
            loadingRecords: '<img src="/images/loading.svg" height="100">',
            emptyTable: "Seems like you don't have any repos managed by Self XDSD.<br> Go to one of the tabs on the right and activate your first repository!"
        },
        ajax: function (_data, callback) {
            $.ajax("/api/repositories/managed", {
                type: "GET",
                statusCode: {
                    200: function (projects) {
                        callback({ data: projects.map(projectAsTableRow) });
                    },
                    204: function () {
                        callback({ data: [] });
                    }
                }
            });
        },
        drawCallback:function(){
            $('[data-toggle="tooltip"]').tooltip({
                boundary: 'window'
            });
        }
    });
}