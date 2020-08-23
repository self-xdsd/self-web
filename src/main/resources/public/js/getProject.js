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
            }
        }
    );
}