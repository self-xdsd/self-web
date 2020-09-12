$(document).ready(
    function () {
        getContributor();
    }
);
function getContributor() {
    $("#loadingContributor").show();
    $.ajax("/api/contributor", {
        type: "GET",
        statusCode: {
            200: function (contributor) {
                $("#loadingContributor").hide();
                $("#contributorDashboard").show();
                contributor.contracts.forEach(
                    function(contract){
                        $("#contractsTable").find("tbody").append(
                            contractAsTableRow(contract)
                        );
                    }
                )
                $("#contractsTable").dataTable();
                console.log(contributor);
            },
            204: function (data) {
                $("#loadingContributor").hide();
                $("#notContributor").show();
            },

        }
    });
}

/**
 * Wrap a Contract's information between table row tags.
 */
function contractAsTableRow(contract) {
    var link;
    if(contract.id.provider == 'github') {
        link = 'https://github.com/' + contract.id.repoFullName;
    } else if(contract.id.provider == 'gitlab') {
        link = 'https://gitlab.com/' + contract.id.repoFullName;
    } else {
        link = '#';
    }
    return "<tr>" +
        "<td><a href='" + link + "' target='_blank'>" + contract.id.repoFullName + "</a></td>" +
        "<td>" + contract.id.role + "</td>"  +
        "<td>" + contract.hourlyRate + "</td>"  +
        "<td>" + contract.value + "</td>" +
        "<td><a href='#'>" +
            "<i class='fa fa-laptop fa-lg'></i>" +
        "</a></td>" +
        "</tr>"
}