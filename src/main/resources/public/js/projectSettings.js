/**
 * Add the submit event to the Delete Project button.
 */
function enableDeleteProjectButton() {
    var owner = $("#owner").text();
    var name = $("#name").text();
    $("#deleteProjectForm").submit(
        function(e) {
            e.preventDefault();
            $("#deleteProjectButton").attr("disabled");
            $("#deleteProjectButton").attr("aria-disabled", "true");
            $("#deleteProjectLoading").show();
            var form = $(this);
            $.ajax(
                {
                    type: "DELETE",
                    url: "/api/projects/" + owner + "/" + name,
                    data: form.serialize(),
                    success: function (response) {
                        location.reload();
                    },
                    error: function(jqXHR) {
                        $("#deleteProjectButton").removeAttr("disabled");
                        $("#deleteProjectButton").attr("aria-disabled", "false");
                        $("#deleteProjectLoading").hide();
                        console.log("Server error status: " + jqXHR.status);
                        console.log("Server error: " + jqXHR.responseText);
                        alert(
                            "Something went wrong (" + jqXHR.status + ")." +
                            "Please, try again later."
                        );
                    }
                }
            );
        }
    );
}
/**
 * Check if a Project can be deleted or not.
 * If it can be deleted, enable the "Delete Project" button from the Settings tab.
 */
function canDeleteProject() {
    var owner = $("#owner").text();
    var name = $("#name").text();
    $("#deleteProjectLoading").show();
    $.get({
        url: "/api/projects/" + owner + "/" + name +"/contracts/count",
        success: function(response){
            if(response.contractsCount > 0) {
                $("#deleteProjectButton").attr("disabled");
                $("#deleteProjectButton").attr("aria-disabled", "true");
            } else {
                $("#deleteProjectButton").removeAttr("disabled");
                $("#deleteProjectButton").attr("aria-disabled", "false");
                enableDeleteProjectButton();
            }
            $("#deleteProjectLoading").hide();
        },
        error: function(jqXHR){
            console.error("Contracts count returned status: " + jqXHR.status);
            console.error("Contracts count returned body: " + jqXHR.responseText);
            $("#deleteProjectLoading").hide();
        }
    });
}