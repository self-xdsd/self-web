function getContributorDashboard() {
    $("#loadingContributor").show();
    $("#contractsTable").dataTable({
        language: {
            loadingRecords: '<img src="/images/loading.svg" height="100">'
        },
        ajax: function (_data, callback) {
            $.ajax("/api/contributor", {
                type: "GET",
                statusCode: {
                    200: function (contributor) {
                        callback({ data: contributor.contracts.map(contractAsTableRow) });
                        $("#loadingContributor").hide();
                        $("#contributorCardFooter").show();
                        $("#contributorDashboard").removeClass("d-none");
                        $("#contributorDashboard").addClass("show");
                        if (contributor.contracts.length > 0) {
                            $("#tasks").show();
                            getTasksOfContract(contributor.contracts[0]);
                            $("#invoices").show();
                            getInvoicesOfContract(contributor.contracts[0]);
                        }
                        //if the user is redirected by Stripe, we should display the "Payout Methods" tab
                        if (getUrlVars().includes("stripe")) {
                            $("#payoutMethodsButton").trigger("click");
                        }
                    },
                    204: function () {
                        callback({ data: [] });
                        $("#loadingContributor").hide();
                        $("#notContributorCardFooter").show();
                    }
                }
            });
        },
        drawCallback:function(){
            $("#contractsTable .contractAgenda").off();
            $("#contractsTable .removeContract").off();
            $('[data-toggle="tooltip"]').tooltip({
                boundary: 'window'
            });
            $("#contractsTable .contractAgenda").each(function () {
                $(this).on("click", function() {
                    var data = $("#contractsTable").DataTable().row($(this).parents('tr')).data();
                    var contract = {
                        id: {
                            repoFullName: $(data[0]).text(),
                            role: data[1],
                            provider: $(data[0]).attr("data-provider")
                        }
                    }
                    getTasksOfContract(contract);
                    getInvoicesOfContract(contract);
                });
            });
            $("#contractsTable .removeContract").each(function () {
                $(this).on("click", function(event){
                    event.preventDefault();
                    var removeButton = $(this);
                    var data = $("#contractsTable").DataTable().row($(this).parents('tr')).data();
                    var contract = {
                        id: {
                            repoFullName: $(data[0]).text(),
                            role: data[1],
                            provider: $(data[0]).attr("data-provider")
                        }
                    };
                    confirmDialog
                        .create("Are you sure you want to remove this contract?", "Warning", "Yes")
                        .then(() => markContractForRemoval(contract, removeButton));
                });
            });
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
    var removeIcon;
    if(contract.markedForRemoval == 'null') {
        removeIcon = "<a href='#' title='Mark Contract For Removal' class='removeContract'>"
            +"<i class='fa fa-trash fa-lg'></i>"
            +"</a>";
    } else {
        var toolTipMessage = "This contract has been marked for removal on "
            + contract.markedForRemoval.split('T')[0] + ". "
            + "No more tasks will be assigned to it and it will be removed after 30 days. "
            + "Contact the PO if you want to have it reactivated.";
        removeIcon = "<i class='fa fa-exclamation-circle fa-lg' style='color:red;' aria-hidden='true' "
            + "data-toggle='tooltip' data-placement='top' "
            + "data-original-title='" + toolTipMessage + "'>"
            +"</i>"
    }
    var walletIcon;
    if(contract.projectWalletType === 'STRIPE') {
        var toolTipMessage = "The project uses a real Stripe Wallet. "
            + "Make sure to completely set up your Payout Method in order to be paid."
        walletIcon = "<i class='fa fa-euro fa-lg' style='color:green;' aria-hidden='true' "
            + "data-toggle='tooltip' data-placement='top' "
            + "data-original-title='" + toolTipMessage + "'>"
            +"</i>"
    } else {
        var toolTipMessage = "The project uses a fake Wallet, you will not be paid.";
        walletIcon = "<i class='fa fa-euro fa-lg' style='color:red;' aria-hidden='true' "
            + "data-toggle='tooltip' data-placement='top' "
            + "data-original-title='" + toolTipMessage + "'>"
            +"</i>"
    }
    return [
        "<a href='" + link + "' target='_blank' data-provider='" + contract.id.provider + "'>" + contract.id.repoFullName + "</a>",
        contract.id.role,
        contract.hourlyRate,
        contract.revenue,
        "<a href='#tasks' title='See Tasks & Invoices' class='contractAgenda'>" +
        "<i class='fa fa-laptop fa-lg'></i>" +
        "</a>  " + removeIcon + " " + walletIcon
    ]
}

/**
 * Get a Contract's tasks.
 * @param contract Contract.
 */
function getTasksOfContract(contract) {
    $("#tasksTable").dataTable().fnDestroy();
    $("#tasksTitle").html(
        " Tasks in " + contract.id.repoFullName + " (" + contract.id.role + ")"
    )
    $("#tasksTable").dataTable({
        language: {
            loadingRecords: '<img src="/images/loading.svg" height="100">',
            emptyTable: "You don't have any tasks assigned."
        },
        ajax: function (_data, callback) {
            $.ajax( //API call to get the Tasks.
                "/api/contributor/contracts/"
                + contract.id.repoFullName
                + "/tasks?role=" + contract.id.role,
                {
                    type: "GET",
                    statusCode: {
                        200: function (tasks) {
                            callback({ data: tasks.map(taskAsTableRow(contract)) });
                        }
                    }
                }
            );
        }
    });

}

/**
 * Turn a Task into a table row.
 * @param task Task.
 */
function taskAsTableRow(contract) {
    return function (task) {
        var issueLink;
        if (contract.id.provider == 'github') {
            issueLink = 'https://github.com/'
                + contract.id.repoFullName
                + "/issues/"
                + task.issueId;
        } else if (contract.id.provider == 'gitlab') {
            issueLink = 'https://gitlab.com/'
                + contract.id.repoFullName
                + "/issues/"
                + task.issueId;
        } else {
            issueLink = '#';
        }
        return [
            "<a href='" + issueLink + "' target='_blank'>#" + task.issueId + "</a>",
            task.assignmentDate.split('T')[0],
            task.deadline.split('T')[0],
            task.estimation + " min.",
            formatEuro(task.value),
        ]
    };
}

/**
 * Get a Contract's Invoices.
 * @param contract Contract.
 */
function getInvoicesOfContract(contract) {
    $("#invoicesTable").dataTable().fnDestroy();
    $("#invoicesTitle").html(
        "Invoices of " + contract.id.repoFullName + " (" + contract.id.role + ")"
    )
    $("#invoicesTable").dataTable({
        language: {
            loadingRecords: '<img src="/images/loading.svg" height="100">'
        },
        ajax: function (_data, callback) {
            $.ajax( //API call to get the Invoices.
                "/api/contributor/contracts/"
                + contract.id.repoFullName
                + "/invoices?role=" + contract.id.role,
                {
                    type: "GET",
                    statusCode: {
                        200: function (invoices) {
                            callback({ data: invoices.map(invoiceAsTableRow(contract)) });
                        }
                    }
                }
            );
        },
        drawCallback: function () {
            $('[data-toggle="tooltip"]').tooltip();
        }
    });
}

/**
 * Turn an Invoice into a table row.
 * @param invoice Invoice.
 */
function invoiceAsTableRow(contract) {
    return function (invoice) {
        var status;
        var downloadIcons;
        var invoicePdfHref = "/api/contributor/contracts/"
            + contract.id.repoFullName
            + "/invoices/" + invoice.id
            + "/pdf?role=" + contract.id.role;

        if (!invoice.isPaid) {
            var latestPayment = invoice.latestPayment;
            if(latestPayment === undefined) {
                status = "Active";
            } else {
                var timestamp = latestPayment.timestamp;
                var failMessage = "Something went wrong, please try again.";
                if(timestamp.length > 0) {
                    timestamp = timestamp.split('T')[0];
                    failMessage = timestamp + ": " + failMessage;
                }

                status = "Payment failed " + "<i class='fa fa-exclamation-triangle fa-lg' style='color:red;' aria-hidden='true' "
                    + "data-toggle='tooltip' data-placement='top' "
                    + "data-original-title=\"" + failMessage + "\">"
                    +"</i>";
            }
            downloadIcons="<a href='" + invoicePdfHref + "' target='_blank' class='downloadInvoice'>" + "<i title='Your Invoice To The Project' class='fa fa-file-pdf-o fa-lg'></i></a>"
        } else {
            status = "Paid";
            if(invoice.latestPayment.transactionId.startsWith("fake_payment")) {
                downloadIcons="<a href='" + invoicePdfHref + "' target='_blank' class='downloadInvoice'>" + "<i title='Your Invoice To The Project' class='fa fa-file-pdf-o fa-lg'></i></a>"
            } else {
                var platformInvoicePdfHref = "/api/contributor/contracts/"
                    + contract.id.repoFullName
                    + "/invoices/" + invoice.id
                    + "/platform/pdf?role=" + contract.id.role;
                downloadIcons="<a href='" + invoicePdfHref + "'  target='_blank' class='downloadInvoice'>" + "<i title='Your Invoice To The Project' class='fa fa-file-pdf-o fa-lg'></i></a>"
                + "  " + "<a href='" + platformInvoicePdfHref + "'  target='_blank' class='downloadInvoice'>" + "<i title='Commission Invoice From Self XDSD' style='color: #701516;' class='fa fa-file-pdf-o fa-lg'></i></a>"
            }
        }
        return [
            invoice.id,
            invoice.createdAt.split('T')[0],
            invoice.amount,
            status,
            downloadIcons
        ];
    }
}

/**
 * Mark a Contract for deletion.
 * @param contract Contract.
 * @param removeButton Remove button for which we have to change
 *  the icon to "Contract Marked For Removal" tooltip.
 */
function markContractForRemoval(contract, removeButton) {
    removeButton.off("click");
    $.ajax(
        "/api/contributor/contracts/"
        + contract.id.repoFullName
        + "/mark?role=" + contract.id.role,
        {
            type: "DELETE",
            success: function (marked) {
                var toolTipMessage = "This contract has been marked for removal on " + marked.markedForRemoval + ". "
                    + "No more tasks will be assigned to it and it will be removed after 30 days. "
                    + "Contact the PO if you want to have it reactivated."
                removeButton.replaceWith("<i class='fa fa-exclamation-circle fa-lg' style='color:red;' aria-hidden='true' "
                    + "data-toggle='tooltip' data-placement='top' "
                    + "data-original-title='" + toolTipMessage + "'>"
                    + "</i>");
                $('[data-toggle="tooltip"]').tooltip({
                    boundary: 'window'
                });
            },
            error: function () {
                alert("Something went wrong. Please refresh the page and try again.")
                removeButton.on(
                    "click",
                    function (event) {
                        event.preventDefault();
                        confirmDialog
                            .create("Are you sure you want to remove this contract?", "Warning", "Yes")
                            .then(() => markContractForRemoval(contract, removeButton));
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
                    if (payoutMethods.length == 0) {
                        $(".no-payout-methods").show();
                    } else {
                        var method = payoutMethods[0];
                        $("#accountId").html(method.identifier);
                        var requirements = method.account.requirements.currently_due;
                        if (method.account.details_submitted && requirements.length == 0) {
                            $("#stripeActive").show();
                            $("#stripeDashboardFormDiv").show();
                            $("#onboardingNeeded").hide();
                            $("#stripeRequirements").hide();
                            $("#accountBadge").addClass("badge-success")
                            $("#accountBadge").html("active")
                        } else {
                            $("#accountBadge").addClass("badge-danger")
                            $("#accountBadge").html("action required")
                            if (method.account.details_submitted == false) {
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
