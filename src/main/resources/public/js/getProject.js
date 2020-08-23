$(document).ready(
    function () {
        getProject();
    }
);

function getProject() {
    var repoFullName = $("#repoFullName").text();
    $("#loadingProject").show();
    $.get(
        "/api/projects/github/" + repoFullName,
        function(project) {
            $("#loadingProject").hide();
            if(project === undefined) {
                $(".project-not-registered").show();
            } else {
                $(".managedBy").html(
                    "Project managed by: "
                ).append(
                    $('<a></a>')
                        .attr("href","https://github.com/" + project.manager.username)
                        .attr("_target", "blank")
                        .html("@" + project.manager.username)
                );
                $("#projectOverview").addClass("show");
                $(".project-buttons").show();
            }
        }
    );
}