$(document).ready(
    function () {
        getProject();
    }
);

function getProject() {
    var repoFullName = $("#repoFullName").text();
    $("#loadingProject").show();
    $.get(
        "/api/users/self",
        function(user) {
            console.log(user);
            $.get(
                "/api/projects/github/" + repoFullName,
                function(project) {
                    $("#loadingProject").hide();
                    if(project === undefined) {
                        $(".project-not-registered").show();
                    } else {
                        console.log(project);
                        $(".managedBy").html(
                            "Project managed by: "
                        ).append(
                            $('<a></a>')
                                .attr("href","https://github.com/" + project.manager.username)
                                .attr("target", "_blank")
                                .html("@" + project.manager.username)
                        );
                        $("#projectOverview").addClass("show");
                        if(project.selfOwner == user.login) {
                           $("#ownerCard").hide();
                        } else {
                            $("#ownerCard .selfOwner").html(project.selfOwner);
                            $("#walletCard").hide();
                            $(".project-owner-buttons").hide();
                        }
                        $(".project-buttons").show();
                    }
                }
            );
        }
    );
}