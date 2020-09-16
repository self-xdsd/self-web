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
                $("#contributorCardFooter").show();
                $("#contributorDashboard").removeClass("d-none");
                $("#contributorDashboard").addClass("show");
                contributor.contracts.forEach(
                    function(contract){
                        $("#contractsTable").find("tbody").append(
                            contractAsTableRow(contract)
                        );
                        $($(".contractAgenda")[$(".contractAgenda").length -1]).on(
                            "click",
                            function(event) {
                                getTasksOfContract(contract);
                                getInvoicesOfContract(contract);
                            }
                        );
                    }
                )
                if(contributor.contracts.length > 0) {
                    $("#tasks").show();
                    getTasksOfContract(contributor.contracts[0]);
                    $("#invoices").show();
                    getInvoicesOfContract(contributor.contracts[0]);
                }
                $("#contractsTable").dataTable();
                console.log(contributor);
            },
            204: function (data) {
                $("#loadingContributor").hide();
                $("#notContributorCardFooter").show();
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
        "<td><a href='#tasks' class='contractAgenda'>" +
            "<i class='fa fa-laptop fa-lg'></i>" +
        "</a></td>" +
        "</tr>"
}

/**
 * Get a Contract's tasks.
 * @param contract Contract.
 */
function getTasksOfContract(contract) {
    $("#tasksTable").dataTable().fnDestroy();
    $("#tasksTable").find("tbody").html('');
    $("#tasksTitle").html(
        " Tasks in " + contract.id.repoFullName + " (" + contract.id.role + ")"
    )
    $("#loadingTasks").show();
    $.ajax( //API call to get the Tasks.
        "/api/contributor/contracts/"
        + contract.id.repoFullName
        + "/tasks?role=" + contract.id.role,
        {
            type: "GET",
            statusCode: {
                200: function (tasks) {
                    $("#loadingTasks").hide();
                    tasks.forEach(
                        function(task) {
                            $("#tasksTable").find("tbody").append(
                                taskAsTableRow(contract, task)
                            );
                        }
                    );
                    $("#tasksTable").dataTable();
                    $("#tasksBody").show();
                    console.log(tasks);
                },
                204: function (data) {
                    $("#loadingTasks").hide();
                    $("#loadingTasksHidden").show();
                    $("#tasksTable").dataTable();
                },

            }
        }
    );
}

/**
 * Turn a Task into a table row.
 * @param task Task.
 */
function taskAsTableRow(contract, task) {
    var issueLink;
    if(contract.id.provider == 'github') {
        issueLink = 'https://github.com/'
            + contract.id.repoFullName
            + "/issues/"
            + task.issueId;
    } else if(contract.id.provider == 'gitlab') {
        issueLink = 'https://gitlab.com/'
            + contract.id.repoFullName
            + "/issues/"
            + task.issueId;
    } else {
        issueLink = '#';
    }
    return "<tr>" +
        "<td><a href='" + issueLink + "' target='_blank'>#" + task.issueId + "</a></td>" +
        "<td>" + task.assignmentDate + "</td>"  +
        "<td>" + task.deadline + "</td>" +
        "<td>" + task.estimation + "min</td>" +
        "</tr>"
}

/**
 * Get a Contract's Invoices.
 * @param contract Contract.
 */
function getInvoicesOfContract(contract) {
    $("#invoicesTable").dataTable().fnDestroy();
    $("#invoicesTable").find("tbody").html('');
    $("#invoicesTitle").html(
        "Invoices of " + contract.id.repoFullName + " (" + contract.id.role + ")"
    )
    $("#loadingInvoices").show();
    $.ajax( //API call to get the Tasks.
        "/api/contributor/contracts/"
        + contract.id.repoFullName
        + "/invoices?role=" + contract.id.role,
        {
            type: "GET",
            statusCode: {
                200: function (invoices) {
                    $("#loadingInvoices").hide();
                    var nr = 0;
                    invoices.forEach(
                        function(invoice) {
                            nr++;
                            $("#invoicesTable").find("tbody").append(
                                invoiceAsTableRow(contract, invoice, nr)
                            );
                        }
                    );
                    $("#invoicesTable").dataTable();
                    $("#invoicesBody").show();
                    console.log(invoices);
                },
                204: function (data) {
                    $("#loadingInvoices").hide();
                    $("#invoicesTable").dataTable();
                },

            }
        }
    );
}

/**
 * Turn an Invoice into a table row.
 * @param invoice Invoice.
 */
function invoiceAsTableRow(contract, invoice, number) {
    var paymentTimestamp;
    if(invoice.paymentTime == "null") {
        paymentTimestamp = "-";
    } else {
        paymentTimestamp = invoice.paymentTime;
    }
    var transactionId;
    if(invoice.transactionId == "null") {
        transactionId = "-";
    } else {
        transactionId = invoice.transactionId;
    }
    var status;
    if(transactionId == "-") {
        status = "Active";
    } else {
        status = "Paid";
    }
    return "<tr>" +
        "<td>#" + number + "</td>" +
        "<td>" + invoice.createdAt + "</td>"  +
        "<td>$" + invoice.totalAmount + "</td>" +
        "<td>" + status + "</td>" +
        "<td><a href='#' class='downloadInvoice'>" + "<i class='fa fa-file-pdf-o fa-lg'></i>" + "</a></td>" +
        "</tr>"
}