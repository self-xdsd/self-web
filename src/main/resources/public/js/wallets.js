/**
 * Global variable to have the wallet type everywhere on the Project
 * page (all tabs).
 */
var walletPieChart;
var activeWallet;

/**
 * Functions handling a Project's Wallets.
 */
function getProjectWallets() {
    $("#loadingWallets").show();
    $("#wallets").hide();
    var owner =$("#owner").text();
    var name =$("#name").text();
    $.get(
        "/api/projects/"+owner+"/"+name +"/wallets",
        function(wallets) {
            $("#loadingWallets").hide();
            if(wallets === undefined) {
                $("#noWallets").show();
                $("#wallets").hide();
            } else {
                $("#noWallets").hide();
                var realWalletFound = false;
                wallets.forEach(function(wallet) {
                    if(wallet.type == "FAKE") {
                        $("#fakeCash").html(formatEuro(wallet.cash));
                        $("#fakeDebt").html(formatEuro(wallet.debt));
                        $("#fakeAvailable").html(formatEuro(wallet.available));
                        if(wallet.active) {
                            $("#fakeWalletBadge").addClass("badge-success")
                            $("#fakeWalletBadge").html("active")
                            $("#activateFakeWallet").hide();
                        } else {
                            $("#activateFakeWallet").show();
                        }
                    }
                    if(wallet.type == "STRIPE") {
                        realWalletFound = true;
                        $("#stripeCash").html(formatEuro(wallet.cash));
                        $("#stripeDebt").html(formatEuro(wallet.debt));
                        $("#stripeAvailable").html(formatEuro(wallet.available));
                        cashLimitColor($("#stripeCash"), wallet);
                        if(wallet.active) {
                            $("#stripeWalletBadge").addClass("badge-success")
                            $("#stripeWalletBadge").html("active")
                            $("#activateStripeWallet").hide();
                        } else {
                            $("#activateStripeWallet").show();
                        }
                        if(wallet.paymentMethods.length == 0) {
                            $("#realPaymentMethods").hide();
                        } else {
                            $("#noRealPaymentMethods").hide();
                            $('#realPaymentMethodsTable > tbody').html('');
                            var activePaymentMethodFound = false;
                            $.each(wallet.paymentMethods, function(index, method) {
                                var active;
                                if(method.self.active) {
                                    activePaymentMethodFound = true;
                                    active = "<input class='pmToggle' type='checkbox' checked data-toggle=\"toggle\">";
                                } else {
                                    active = "<input class='pmToggle' type='checkbox' data-toggle='toggle'>";
                                }
                                var issuer = method.stripe.card.brand;
                                issuer = issuer.substr(0,1).toUpperCase() + issuer.substr(1);
                                $('#realPaymentMethodsTable > tbody').append(
                                    "<tr>"
                                    + "<td>"
                                    + issuer
                                    + "</td>"
                                    + "<td>"
                                    + "****** " + method.stripe.card.last4
                                    + "</td>"
                                    + "<td>"
                                    + method.stripe.card.exp_month + "/" + method.stripe.card.exp_year
                                    + "</td>"
                                    + "<td>"
                                    + active
                                    + "</td>"
                                    + "</tr>"
                                )
                                $($('input.pmToggle')[$('input.pmToggle').length - 1]).on(
                                    'change',
                                    function() {
                                        if($(this).prop('checked')) {
                                            $('input.pmToggle').not(this).prop('checked', false);
                                            let parent = $('input.pmToggle').not(this).parent();
                                            parent.removeClass('btn-primary');
                                            parent.addClass('btn-default');
                                            parent.addClass('off');
                                            activatePaymentMethod(owner, name, method);
                                            $("#activateStripeWalletButton").removeClass("disabled");
                                        } else { //we don't allow manual inactivation of a PaymentMethod
                                            $(this).bootstrapToggle('on');
                                        }
                                    }
                                );
                            });
                            $('.pmToggle').bootstrapToggle({
                                on: 'Active',
                                off: 'Inactive',
                                width: '45%'
                            });
                            $("#realPaymentMethods").show();
                            if(activePaymentMethodFound) {
                                $("#activateStripeWalletButton").removeClass("disabled");
                            }
                        }
                        installUpdateCashLimitPopover(
                            $("#stripeUpdateCashLimitAction"),
                            $("#stripeCash"),
                            wallet.type,
                            (updatedWallet) => {
                                $("#stripeCash").html(formatEuro(updatedWallet.cash));
                                $("#stripeDebt").html(formatEuro(updatedWallet.debt));
                                $("#stripeAvailable").html(formatEuro(updatedWallet.available));
                                cashLimitColor($("#stripeCash"), updatedWallet);
                            }
                        );
                    }
                });
                if(realWalletFound) {
                    $("#realWalletOverview").show();
                    $("#noRealWallet").hide();
                } else {
                    $("#realWalletOverview").hide();
                    $("#noRealWallet").show();
                }
                $("#wallets").show();
            }
        }
    );
}

/**
 * Activate a Project's Wallet.
 * @param owner Owner of the repo/project.
 * @param name Name of the repo.
 * @param type Type of the wallet (fake or stripe).
 * @todo #195#60min On frontend, enable wallet activation. Right now `activateWallet()` function is not used.
 */
function activateWallet(owner, name, type) {
    if(type == 'stripe') {
        $("#activateStripeWalletButton").addClass("disabled");
        $("#loadingActivateStripeWalletButton").show();
    } else if(type == 'fake') {
        $("#activateFakeWalletButton").addClass("disabled");
        $("#loadingActivateFakeWalletButton").show();
    }
    $.ajax(
        {
            type: "PUT",
            contentType: "application/json",
            url: "/api/projects/" + owner + "/" + name +
                "/wallets/" + type + "/activate",
            success: function (activatedWallet) {
                if(type == 'stripe') {
                    $("#fakeWalletBadge").removeClass("badge-success")
                    $("#fakeWalletBadge").html("")
                    $("#activateFakeWallet").show();

                    $("#stripeWalletBadge").addClass("badge-success");
                    $("#stripeWalletBadge").html("active");
                    $("#activateStripeWallet").hide();

                    $("#activateStripeWalletButton").removeClass("disabled");
                    $("#loadingActivateStripeWalletButton").hide();
                } else if(type == 'fake') {
                    $("#stripeWalletBadge").removeClass("badge-success")
                    $("#stripeWalletBadge").html("")
                    $("#activateStripeWallet").show();

                    $("#fakeWalletBadge").addClass("badge-success");
                    $("#fakeWalletBadge").html("active");
                    $("#activateFakeWallet").hide();

                    $("#activateFakeWalletButton").removeClass("disabled");
                    $("#loadingActivateFakeWalletButton").hide();
                }
                walletAsPieChart(activatedWallet);
            },
            error: function(jqXHR, error, errorThrown) {
                if(type == 'stripe') {
                    $("#activateStripeWalletButton").removeClass("disabled");
                    $("#loadingActivateStripeWalletButton").hide();
                } else if(type == 'fake') {
                    $("#activateFakeWalletButton").removeClass("disabled");
                    $("#loadingActivateFakeWalletButton").hide();
                }
                console.log("Server error status: " + jqXHR.status);
                console.log("Server error: " + jqXHR.responseText);
                alert(
                    "Something went wrong (" + jqXHR.status + ")." +
                    "Please, refresh the page and try again."
                );
            }
        }
    );

}

/**
 * Pay an Invoice
 * @param invoice Invoice to be paid.
 * @param contract Contract to which the Invoice belongs.
 * @param payButton Button for payment, which we should hide
 *  if the payment is successful.
 */
function payInvoice(invoice, contract, payButton) {

    /**
     * Turn an Invoice into a table row.
     * @param invoice Invoice.
     */
    function invoiceAsTableRow(invoice) {
        var status;
        var payIcon = "";
        var downloadLink = "";
        if (invoice.paymentTime == "null" && invoice.transactionId == "null") {
            status = "Active";
            if (parseFloat(invoice.totalAmount) > 0.0) {
                payIcon = "<a href='#' title='Pay Invoice' class='payInvoice'>"
                    + "<i class='fa fa-credit-card fa-lg'></i>"
                    + "</a>";
            }
        } else {
            status = "Paid";
        }
        if (parseFloat(invoice.totalAmount) > 0.0) {
            downloadLink = "<a href='#' title='Download Invoice' class='downloadInvoice'>"
                + "<i class='fa fa-file-pdf-o fa-lg'></i>"
                + "</a>  "
        }
        return "<tr>" +
            "<td>" + invoice.id + "</td>" +
            "<td>" + invoice.createdAt.split('T')[0] + "</td>"  +
            "<td>" + invoice.totalAmount + "</td>" +
            "<td>" + status + "</td>" +
            "<td>"
            + downloadLink
            + payIcon
            + "</td>" +
            "</tr>"
    }

    $.ajax(
        {
            type: "PUT",
            contentType: "application/json",
            url: "/api/projects/" + contract.id.repoFullName +
                "/contracts/" + contract.id.contributorUsername + "/invoices/"
                + invoice.id + "?role=" + contract.id.role,
            success: function (json) {
                $("#invoicesBody").hide();
                $("#loadingInvoices").show();
                payButton.hide();
                $('#invoicesTable > tbody  > tr').each(
                    function(index, row) {
                        if($(row).find("td:eq(0)").text() == json.paid){
                            $(row).find("td:eq(3)").text("Paid");
                        }
                    }
                );
                $("#invoicesTable").DataTable().row.add($(invoiceAsTableRow(json.active))[0]).draw();
                $("#loadingInvoices").hide();
                $("#invoicesBody").show();
            },
            error: function(jqXHR, error, errorThrown) {
                console.log("Server error status: " + jqXHR.status);
                console.log("Server error: " + jqXHR.responseText);
                if(jqXHR.status == 412) {
                    alert(JSON.parse(jqXHR.responseText).message);
                } else {
                    alert(
                        "Something went wrong (" + jqXHR.status + ")." +
                        "Please, refresh the page and try again."
                    );
                }
            }
        }
    );
}

/**
 * Sets wallet's cash limit color by using Bootstrap color classes):
 * - red if it's 0 or less than the Debt;
 * - yellow if limit - debt <= 100;
 * - green otherwise.
 * @param cashEl Cash limit DOM element.
 * @param wallet The Wallet.
 */
function cashLimitColor(cashEl, wallet){
    var colorClass = cashEl.data('limitColor') || 'text-success';
    cashEl.removeClass(colorClass);
    if(wallet.cash <= 0 || wallet.cash < wallet.debt ){
        colorClass = 'text-danger';
    }else if (wallet.cash - wallet.debt <= 100.0){
        colorClass = 'text-warning';
    }else{
        colorClass = 'text-success';
    }
    cashEl.addClass(colorClass);
    cashEl.data('limitColor', colorClass);
}

/**
 * Display the wallet as a pie chart on the Project Overview tab.
 * @param wallet Wallet.
 */
function walletAsPieChart(wallet) {
    activeWallet = wallet;
    if(walletPieChart != undefined) {
        walletPieChart.destroy();
        $("#walletPieChart").removeAttr("height");
        $("#walletPieChart").removeAttr("width");
    }
    var ctx = document.getElementById("walletPieChart");
    walletPieChart = new Chart(
        ctx, {
            type: 'doughnut',
            data: {
                labels: ["Available (€)", "Debt (€)"],
                datasets: [{
                    data: [wallet.available, wallet.debt],
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
    $("#walletCash").html(formatEuro(wallet.cash));
    $("#walletDebt").html(formatEuro(wallet.debt));
    $("#walletAvailable").html(formatEuro(wallet.available));
    if(wallet.type == 'FAKE') {
        $("#walletCardTitle").html(
            'Fake Wallet <i class="fa fa-question-circle-o fa-lg fakeWalletInfo"'
                + 'aria-hidden="true"'
                + 'data-toggle="tooltip"'
                + 'data-placement="top"'
                + 'title="We assigned you a fake wallet with 100.000 €. You can work with it'
                + 'until you decide to register a real wallet. The business flow is the same,'
                + 'except the payments are fictive."></i>'
        );
        $('[data-toggle="tooltip"]').tooltip();
    } else {
        $("#walletCardTitle").html("Stripe Wallet");
    }
}