/**
 * Check if a Project can be deleted or not.
 * If it can be deleted, enable the "Delete Project" button from the Settings tab.
 */
function canDeleteProject() {
    $("#deleteProjectLoading").show();
    $.get({
        url: "/api/projects/"+$("#owner").text()+"/"+$("#name").text()+"/contracts/count",
        success: function(response){
            if(response.contractsCount > 0) {
                $("#deleteProjectButton").attr("disabled");
                $("#deleteProjectButton").attr("aria-disabled", "true");
            } else {
                $("#deleteProjectButton").removeAttr("disabled");
                $("#deleteProjectButton").attr("aria-disabled", "false");
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