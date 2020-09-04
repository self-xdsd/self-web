$(document).ready(
    function () {
        getProject();
    }
);

function getProject() {
    var owner =$("#owner").text();
    var name =$("#name").text();
    $("#loadingProject").show();
    $.get(
        "/api/users/self",
        function(user) {
            console.log(user);
            $.get(
                "/api/projects/"+owner+"/"+name,
                function(project) {
                    $("#loadingProject").hide();
                    if(project === undefined) {
                        $(".project-not-registered").show();
                    } else {
                        displayProject(user.login, project);
                    }
                }
            );
        }
    );
}

function displayProject(userLogin, project) {
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
    if(project.selfOwner == userLogin) {
        $("#ownerCard").hide();
        // Wallet Pie Chart
        var ctx = document.getElementById("walletPieChart");
        var walletChart = new Chart(
            ctx, {
                type: 'doughnut',
                data: {
                    labels: ["Available ($)", "Debt ($)"],
                    datasets: [{
                        data: [project.wallet.available, project.wallet.debt],
                        backgroundColor: ['#701516', '#FFB6C1'],
                        hoverBorderColor: "rgba(234, 236, 244, 1)",
                    }],
                },
                options: {
                    responsive: false,
                    maintainAspectRatio: false,
                    tooltips: {
                        backgroundColor: "rgb(255,255,255)",
                        bodyFontColor: "#858796",
                        borderColor: '#dddfeb',
                        borderWidth: 1,
                        xPadding: 15,
                        yPadding: 15,
                        displayColors: false,
                        caretPadding: 10,
                    },
                    legend: {
                        display: true
                    },
                    cutoutPercentage: 80,
                },
            });
            $("#walletCash").html('$' + project.wallet.cash);
            $("#walletDebt").html('$' + project.wallet.debt);
            $("#walletAvailable").html('$' + project.wallet.available);
            if(project.wallet.type == 'FAKE') {
                $(".fakeWalletInfo").show();
            }
    } else {
        $("#ownerCard .selfOwner").html(project.selfOwner);
        $("#walletCard").hide();
        $(".project-owner-buttons").hide();
    }
    $(".project-buttons").show();
}