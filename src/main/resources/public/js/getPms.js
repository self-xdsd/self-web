$(document).ready(
    function () {
        getPms();
    }
);
/**
 * Get the Project Managers
 */
function getPms() {
    $("#pmsTable").find("tbody").html('');
    $("#loadingPmsTable").show();
    $.get(
        "/api/managers",
        function(managers) {
            managers.forEach(
                function(manager){
                    $("#pmsTable").find("tbody").append(managerAsTableRow(manager));
                }
            )
            $("#loadingPmsTable").hide();
            $("#pmsTable").dataTable();
        }
    );
}

/**
 * Wrap a managers's information between table row tags.
 */
function managerAsTableRow(manager) {
    var profile;
    if(manager.provider == 'github') {
        profile = 'https://github.com/' + manager.username;
    } else if(manager.provider == 'gitlab') {
        profile = 'https://gitlab.com/' + manager.username;
    } else {
        profile = '#';
    }
    return "<tr>" +
        "<td><a href='" + profile + "' target='_blank'>@" + manager.username + "</a></td>" +
        "<td>" + manager.provider + "</td>"  +
        "<td>" + manager.userId + "</td>"  +
        "</tr>"
}