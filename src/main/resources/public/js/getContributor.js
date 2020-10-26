function getContributorDashboard() {
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
                        $($(".removeContract")[$(".removeContract").length -1]).on(
                            "click",
                            function(event) {
                                event.preventDefault();
                                markContractForRemoval(contract, $(this));
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

                //if the user is redirected by Stripe, we should display the "Payout Methods" tab
                if(getUrlVars().includes("stripe")) {
                    $("#payoutMethodsButton").trigger("click");
                }
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
        "<td><a href='#tasks' title='See Tasks & Invoices' class='contractAgenda'>" +
            "<i class='fa fa-laptop fa-lg'></i>" +
        "</a>  "
        +"<a href='#' title='Mark Contract For Removal' class='removeContract'>"
        +"<i class='fa fa-trash fa-lg'></i>"
        +"</a>"
        "</td>" +
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
                    $("#tasksBody").show();
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
    $.ajax( //API call to get the Invoices.
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
                            $($(".downloadInvoice")[$(".downloadInvoice").length -1]).on(
                                "click",
                                function(event) {
                                    event.preventDefault();
                                    invoiceToPdf(invoice, contract);
                                }
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

function invoiceToPdf(invoice, contract) {
    window.jsPDF = window.jspdf.jsPDF; // add this line of code
    $.ajax( //API call to get the full invoice.
        "/api/contributor/contracts/"
        + contract.id.repoFullName
        + "/invoices/" + invoice.id
        +"?role=" + contract.id.role,
        {
            type: "GET",
            statusCode: {
                200: function (fullInvoice) {
                    const doc = new jsPDF();

                    doc.addImage("/images/self-xdsd.png", "png", 170, 10, 20, 20);
                    doc.text("Invoice", 105, 20, null, null, "center");
                    doc.text("Invoice #" + fullInvoice.id + " from " + fullInvoice.createdAt, 20, 35);
                    doc.text("Contributor: " + contract.id.contributorUsername, 20, 42);
                    doc.text("Project: " + contract.id.repoFullName + " at " + contract.id.provider, 20, 49);
                    doc.text("Role: " + contract.id.role, 20, 56);
                    doc.text("Hourly Rate: " + contract.hourlyRate, 20, 63);
                    doc.text("______________________________", 20, 67);

                    doc.text("Total Due: $" + fullInvoice.totalAmount, 20, 74);
                    var toContributor = function() {
                        var toContributor = 0;
                        fullInvoice.tasks.forEach(
                            function(task){
                                toContributor += task.value
                            }
                        )
                        return toContributor;
                    }.call();
                    doc.text("to Contributor: $" + toContributor, 23, 81);
                    var toPm = function() {
                        var toPm = 0;
                        fullInvoice.tasks.forEach(
                            function (task) {
                                toPm += task.commission
                            }
                        )
                        return toPm;
                    }.call();
                    doc.text("to Project Manager: $" + toPm, 23, 88);
                    if(fullInvoice.isPaid) {
                        doc.text("Status: paid", 20, 95)
                        doc.text("paid at: " + fullInvoice.paymentTime, 23, 102)
                        doc.text("transaction id: " + fullInvoice.transactionId, 23, 109)
                    } else {
                        doc.text("Status: active (not paid)", 20, 95)
                        doc.text("payment due: 1st day of next month,", 23, 102)
                        doc.text("or when more than $100 are cumulated ", 23, 109)
                    }
                    doc.text("Invoiced tasks bellow.", 20, 116);

                    doc.text("______________________________", 20, 120);

                    function createHeaders(keys) {
                        var result = [];
                        for (var i = 0; i < keys.length; i += 1) {
                            result.push({
                                id: keys[i],
                                name: keys[i],
                                prompt: keys[i],
                                width: 30,
                                align: "center",
                                padding: 0
                            });
                        }
                        return result;
                    }

                    var headers = createHeaders([
                        "Issue ID",
                        "Estimation",
                        "Value",
                        "Commission"
                    ]);

                    var generateData = function(invoicedTasks) {
                        var result = [];
                        for (var i = 0; i < invoicedTasks.length; i += 1) {
                            var task = {
                                "Issue ID": invoicedTasks[i].issueId,
                                Estimation: invoicedTasks[i].estimation + "min",
                                Value: "$" + invoicedTasks[i].value,
                                Commission: "$" + invoicedTasks[i].commission
                            };
                            result.push(Object.assign({}, task));
                        }
                        return result;
                    };
                    doc.table(
                        55, 127,
                        generateData(fullInvoice.tasks),
                        headers,
                        {
                            autoSize: true
                        }
                    );

                    doc.save("self_invoice_" + fullInvoice.id + ".pdf");
                }
            }
        }
    );

}

/**
 * Mark a Contract for deletion.
 * @param contract Contract.
 * @param removeButton Remove button for which we have to change
 *  the icon and add the "Restore Contract" listener.
 */
function markContractForRemoval(contract, removeButton) {
    removeButton.off("click");
    $.ajax(
        "/api/contributor/contracts/"
        + contract.id.repoFullName
        + "/mark?role=" + contract.id.role,
        {
            type: "DELETE",
            success: function() {
                removeButton.html(
                    "<i class='fa fa-hourglass' style='color: red;'></i>"
                );
                removeButton.attr("title", "Restore the Contract");
                removeButton.on(
                    "click",
                    function(event) {
                        event.preventDefault();
                        alert("Contract restored API call!");
                    }
                )
            },
            error: function () {
                alert("Something went wrong. Please refresh the page and try again.")
                removeButton.on(
                    "click",
                    function(event) {
                        event.preventDefault();
                        markContractForRemoval(contract, removeButton);
                    }
                );
            }
        }
    );
}

/**
 * Get the Contributor's Payout methods.
 */
function getPayoutMethods() {
    $("#loadingPayoutMethods").show();
    $.ajax(
        "/api/contributor/payoutmethods/",
        {
            type: "GET",
            statusCode: {
                200: function (payoutMethods) {
                    $("#loadingPayoutMethods").hide();
                    if(payoutMethods.length == 0) {
                        $(".no-payout-methods").show();
                    } else {
                        var method = payoutMethods[0];
                        $("#accountId").html(method.identifier);
                        var requirements = method.account.requirements.currently_due;
                        if(method.account.details_submitted && requirements.length == 0) {
                            $("#stripeActive").show();
                            $("#stripeDashboardFormDiv").show();
                            $("#onboardingNeeded").hide();
                            $("#stripeRequirements").hide();
                            $("#accountBadge").addClass("badge-success")
                            $("#accountBadge").html("active")
                        } else {
                            $("#accountBadge").addClass("badge-danger")
                            $("#accountBadge").html("action required")
                            if(method.account.details_submitted == false) {
                                $("#onboardingNeeded").show();
                                $("#stripeRequirements").hide();
                                $("#stripeActive").hide();
                                $("#stripeDashboardFormDiv").hide()
                            } else {
                                $("#onboardingNeeded").hide();
                                $("#stripeActive").hide();
                                $("#stripeRequirements").show();
                                $("#stripeDashboardFormDiv").show()
                            }
                        }
                        $(".payout-methods").show();
                    }
                }
            }
        }
    );
}