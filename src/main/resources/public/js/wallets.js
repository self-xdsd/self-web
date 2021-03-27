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
                            $("#fakeWalletDiv").remove();
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
                                if (method.self.active) {
                                    activePaymentMethodFound = true;
                                }
                               renderPaymentMethodRow(method);
                            });
                            $('.pmToggle').bootstrapToggle({
                                on: 'Active',
                                off: 'Inactive',
                                width: '30%'
                            });
                            $("#realPaymentMethods").show();
                            if(activePaymentMethodFound) {
                                $("#activateStripeWalletButton").removeClass("disabled");
                            }
                            if(arePaymentMethodsDeactivated($('input.pmToggle'))){
                                $("#realPaymentMethodsWarning").show();
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
                                walletAsPieChart(updatedWallet);
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
 * Renders a payment method row in the payment methods table.
 * @param {Object} method Payment method.
 */
function renderPaymentMethodRow(method) {
    var owner = $("#owner").text();
    var name = $("#name").text();
    /**
     * Creates html template for active toggle button and remove inactive method button.
     * @param {Boolean} active Is method active?
     * @returns Html template.
     */
    function activeToggleTemplate(active){
        var checked = (active) ? "checked" : "";
        var showRemove = (active) ? "style='visibility:hidden;'" : "style='visibility:visible;'";
        return "<input class='pmToggle' type='checkbox' " +
            checked + " data-toggle='toggle'/> " +
            "<button " + showRemove +
            " type='button' class='btn btn-danger px-3 btn-pmToggle'><i class='fa fa-times' aria-hidden='true'></i></button>";
    }

    var active = activeToggleTemplate(method.self.active);
    var issuer = method.stripe.card.brand;
    issuer = issuer.substr(0, 1).toUpperCase() + issuer.substr(1);
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
    );
    $($('input.pmToggle')[$('input.pmToggle').length - 1]).on(
        'change',
        function () {
            var parent = $('input.pmToggle').not(this).parent();
            var otherRemoveBtns = parent.siblings();
            var thisRemoveBtn = $(this).parent().next();
            if ($(this).prop('checked')) {
                $('input.pmToggle').not(this).prop('checked', false);
                parent.removeClass('btn-primary');
                parent.addClass('btn-default');
                parent.addClass('off');
                activatePaymentMethod(owner, name, method);
                $("#activateStripeWalletButton").removeClass("disabled");
                $("#realPaymentMethodsWarning").hide();
                otherRemoveBtns.css('visibility','visible');
                thisRemoveBtn.css('visibility','hidden');
            } else {
                deactivatePaymentMethod(owner, name, method);
                thisRemoveBtn.css('visibility','visible');
                if (arePaymentMethodsDeactivated($('input.pmToggle'))) {
                    $("#realPaymentMethodsWarning").show();
                }
            }
        }
    );
    $($('button.btn-pmToggle')[$('button.btn-pmToggle').length - 1]).on(
        "click",
        function () {
            var thisBtn = $(this);
            thisBtn.prop('disabled', true);
            confirmDialog
                .create("Are you sure you want to remove this payment method?", "Warning", "Yes")
                .then(() => removePaymentMethod(owner, name, method))
                .then(() => thisBtn.closest("tr").remove())
                .catch((jqXHR) => {
                    if (jqXHR) {
                        confirmDialog
                            .create("Something went wrong while removing the payment method. Please try again.", "Error", "Yes")
                            .then(() => { });
                    }
                    thisBtn.prop('disabled', false);
                });
        }
    );
}

/**
 * Checks if payment methods are deactivated.
 * @param toggleButtons Active/Inactive payment methods toggle buttons.
 * @returns True if all payment methods are deactivated.
 */
function arePaymentMethodsDeactivated(toggleButtons) {
    var allDeactivated = true;
    toggleButtons.each(function () {
        allDeactivated = allDeactivated & !$(this).prop('checked');
    });
    return allDeactivated;
}

/**
 * Activate a Project's Wallet.
 * @param owner Owner of the repo/project.
 * @param name Name of the repo.
 * @param type Type of the real wallet (ex: stripe).
 */
function activateWallet(owner, name, type) {
    if(type == 'stripe') {
        $("#activateStripeWalletButton").addClass("disabled");
        confirmDialog
            .create("Are you sure you want to activate the Real Wallet? The Fake Wallet will be deleted, there's no going back.", "Warning", "Yes")
            .then(function () {
                $("#loadingActivateStripeWalletButton").show();
                return $.when($.ajax({
                    type: "PUT",
                    contentType: "application/json",
                    url: "/api/projects/" + owner + "/" + name +
                        "/wallets/" + type + "/activate"
                }));
            })
            .then(function (activatedWallet) {
                $("#fakeWalletDiv").hide();
                $("#stripeWalletBadge").addClass("badge-success");
                $("#stripeWalletBadge").html("active");
                $("#activateStripeWallet").hide();
                walletAsPieChart(activatedWallet);
            })
            .catch(function (jqXHR) {
                if (jqXHR) {
                    confirmDialog
                        .create("Something went wrong (" + jqXHR.responseText + "). Please try again.", "Error")
                        .then(function () {
                            console.log("Server error status: " + jqXHR.status);
                            console.log("Server error: " + jqXHR.responseText);
                        });
                }
            })
            .finally(function () {
                $("#activateStripeWalletButton").removeClass("disabled");
                $("#loadingActivateStripeWalletButton").hide();
            });
    } 
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
     * Turn a new Invoice (active) into a table row.
     * @param invoice Invoice.
     */
    function newInvoiceAsTableRow(invoice) {
        var status = "Active"
        var downloadLink ="<a href='#' title='Download Invoice' class='downloadInvoice'>"
            + "<i class='fa fa-file-pdf-o fa-lg'></i>"
            + "</a>";
        return "<tr>" +
            "<td>" + invoice.id + "</td>" +
            "<td>" + invoice.createdAt.split('T')[0] + "</td>"  +
            "<td>" + invoice.totalAmount + "</td>" +
            "<td>" + status + "</td>" +
            "<td>"
            + downloadLink
            + "</td>" +
            "</tr>"
    }
    payButton.unbind("click");
    payButton.on(
        "click",
        function(event) {event.preventDefault();}
    );
    payButton.html('<img src="/images/loading.svg" width="25" height="25">');
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
                if(json.payment.status == 'SUCCESSFUL') {
                    payButton.hide();
                    $('#invoicesTable > tbody  > tr').each(
                        function (index, row) {
                            if ($(row).find("td:eq(0)").text() == json.paid) {
                                $(row).find("td:eq(3)").text("Paid");
                            }
                        }
                    );
                    $("#invoicesTable").DataTable().row.add(
                        $(newInvoiceAsTableRow(json.active))[0]
                    ).draw();
                } else {
                    $('#invoicesTable > tbody  > tr').each(
                        function (index, row) {
                            if ($(row).find("td:eq(0)").text() == json.paid) {
                                var timestamp = json.payment.timestamp;
                                var failMessage;
                                if(timestamp.length > 0) {
                                    timestamp = timestamp.split('T')[0];
                                    failMessage = timestamp + ": " + json.payment.failReason;
                                } else {
                                    failMessage = json.payment.failReason;
                                }
                                var status = "Payment failed " + "<i class='fa fa-exclamation-triangle fa-lg' style='color:red;' aria-hidden='true' "
                                    + "data-toggle='tooltip' data-placement='top' "
                                    + "data-original-title=\"" + failMessage.replaceAll('"', '\'') + "\">"
                                    +"</i>";

                                $(row).find("td:eq(3)").html(status);
                                $('[data-toggle="tooltip"]').tooltip();
                            }
                        }
                    );
                    payButton.html('<i class="fa fa-credit-card fa-lg"></i>');
                    payButton.on(
                        "click",
                        function(event) {
                            event.preventDefault();
                            var message = "Are you sure you want to make this payment?"
                            if(activeWallet.type == 'FAKE') {
                                message += ' You are using a fake wallet, the payment will be fictive.'
                            }
                            confirmDialog
                                .create(message, "Warning", "Yes")
                                .then(
                                    () => payInvoice(invoice, contract, $(this))
                                );
                        }
                    );
                }
                $("#loadingInvoices").hide();
                $("#invoicesBody").show();
            },
            error: function(jqXHR, error, errorThrown) {
                console.log("Server error status: " + jqXHR.status);
                console.log("Server error: " + jqXHR.responseText);
                payButton.html('<i class="fa fa-credit-card fa-lg"></i>');
                payButton.on(
                    "click",
                    function(event) {
                        event.preventDefault();
                        var message = "Are you sure you want to make this payment?"
                        if(activeWallet.type == 'FAKE') {
                            message += ' You are using a fake wallet, the payment will be fictive.'
                        }
                        confirmDialog
                            .create(message, "Warning", "Yes")
                            .then(
                                () => payInvoice(invoice, contract, $(this))
                            );
                    }
                );
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
                + 'title="We assigned your project a fake wallet with 100.000 €. You can work with it '
                + 'until you decide to register a real wallet. The business flow is the same, '
                + 'except the payments are fictive."></i>'
        );
        $('[data-toggle="tooltip"]').tooltip();
    } else {
        $("#walletCardTitle").html("Stripe Wallet");
    }
}

$(document).ready(
    function() {
        $('input:radio[name="businessType"]').change(
            function(){
                if ($(this).is(':checked') && $(this).val() == 'company') {
                    $("#legalNameDiv").show();
                    $("#firstNameDiv").hide();
                    $("#lastNameDiv").hide();
                } else if ($(this).is(':checked') && $(this).val() == 'individual') {
                    $("#firstNameDiv").show();
                    $("#lastNameDiv").show();
                    $("#legalNameDiv").hide();
                }
            }
        );
        $("#stripeCustomerForm").submit(
            function(e) {
                e.preventDefault();

                var valid = true;
                $.each($("#stripeCustomerForm .required"), function(index, element) {
                    if($(element).is(":visible")) {
                        if ($(element).val().trim() == '') {
                            $(element).addClass("is-invalid");
                            valid = false;
                        } else {
                            $(element).removeClass("is-invalid");
                        }
                    }
                });
                $.each($("#stripeCustomerForm input"), function(index, element) {
                    if($(element).is(":visible")) {
                        if (hasSpecialChars($(element).val())) {
                            $("." + $(element).attr("name") + "-error").html(
                                "The only special characters allowed are ,.-_&@#'"
                            );
                            $(element).addClass("is-invalid");
                            valid = false;
                        }
                    }
                });
                $.each($("#stripeCustomerForm input[type=checkbox]"), function(index, element) {
                    if($(element).is(":visible")) {
                        if (!this.checked) {
                            $(element).addClass("is-invalid");
                            valid = false;
                        } else {
                            $(element).removeClass("is-invalid");
                        }
                    }
                });
                $.each($("#stripeCustomerForm textarea"), function(index, element) {
                    if(hasSpecialChars($(element).val())) {
                        $("." + $(element).attr("name") + "-error").html(
                            "The only special characters allowed are ,.-_&@#'"
                        );
                        $(element).addClass("is-invalid");
                        valid =  false;
                    } else {
                        $(element).removeClass("is-invalid");
                    }
                });

                if(valid) {
                    $("#stripeCustomerButton").addClass("disabled");
                    $("#loadingStripeCustomerForm").show();
                    var owner = $("#owner").text();
                    var name = $("#name").text();
                    var form = $(this);
                    $.ajax(
                        {
                            type: "POST",
                            url: "/api/projects/" + owner + "/" + name + "/wallets/stripe",
                            data: form.serialize(),
                            success: function (wallet) {
                                $("#noRealWallet").hide();

                                $("#stripeCash").html(formatEuro(wallet.cash));
                                $("#stripeDebt").html(formatEuro(wallet.debt));
                                $("#stripeAvailable").html(formatEuro(wallet.available));
                                cashLimitColor($("#stripeCash"), wallet);
                                if (wallet.active) {
                                    $("#stripeWalletBadge").addClass("badge-success")
                                    $("#stripeWalletBadge").html("active")
                                    $("#activateStripeWallet").hide();
                                } else {
                                    $("#activateStripeWallet").show();
                                }

                                $("#stripeCustomerButton").removeClass("disabled");
                                $("#loadingStripeCustomerForm").hide();

                                $("#realWalletOverview").show();

                                installUpdateCashLimitPopover(
                                    $("#stripeUpdateCashLimitAction"),
                                    $("#stripeCash"),
                                    wallet.type,
                                    (updatedWallet) => {
                                        $("#stripeCash").html(formatEuro(updatedWallet.cash));
                                        $("#stripeDebt").html(formatEuro(updatedWallet.debt));
                                        $("#stripeAvailable").html(formatEuro(updatedWallet.available));
                                        cashLimitColor($("#stripeCash"), updatedWallet);
                                        walletAsPieChart(updatedWallet);
                                    }
                                );
                            },
                            error: function (jqXHR, error, errorThrown) {
                                $("#stripeCustomerButton").removeClass("disabled");
                                $("#loadingStripeCustomerForm").hide();
                                if (jqXHR.status && jqXHR.status == 400) {
                                    console.error("Bad Request: " + jqXHR.responseText);
                                    $("#setupCustomerError").show();
                                } else {
                                    console.log("Server error status: " + jqXHR.status);
                                    console.log("Server error: " + jqXHR.responseText);
                                    alert(
                                        "Something went wrong (" + jqXHR.status + ")." +
                                        "Please, try again later."
                                    );
                                }
                            }
                        }
                    );
                }
            }
        );

        $("#addStripePaymentMethodForm").submit(
            function(e) {
                e.preventDefault();
                $("#addStripePaymentMethodButton").addClass("disabled");
                $("#loadingStripePaymentForm").show();
                var owner =$("#owner").text();
                var name =$("#name").text();
                var form = $(this);
                $.ajax(
                    {
                        type: "POST",
                        url: "/api/projects/" + owner + "/" + name +
                            "/wallets/stripe/paymentMethods/setup",
                        data: form.serialize(),
                        success: function (setupIntent) {
                            $("#addStripePaymentMethodButton").removeClass("disabled");
                            $("#loadingStripePaymentForm").hide();
                            var stripe = Stripe(stripePublicKey);

                            var elements = stripe.elements();
                            var cardElement = elements.create('card');
                            cardElement.mount('#payment-method-element');
                            $("#addStripePaymentMethodButton").hide();
                            $("#payment-method-element-card").show();


                            $("#addNewCardButton").on(
                                "click",
                                function(event) {
                                    event.preventDefault();
                                    $("#addNewCardButton").addClass("disabled");
                                    $("#cancelNewCardButton").addClass("disabled");
                                    $("#loadingAddNewCard").show();

                                    stripe.confirmCardSetup(
                                        setupIntent.clientSecret,
                                        {
                                            payment_method: {
                                                card: cardElement,
                                                billing_details: {},
                                            },
                                        }
                                    ).then(function(result) {
                                        console.log("STRIPE RESULT: " + JSON.stringify(result));
                                        if(result.error) {
                                            var message;
                                            if(result.error.message) {
                                                message = result.error.message;
                                            } else {
                                                message = "Something went wrong. Please refresh the page and try again.";
                                            }
                                            $("#addNewCardErrorMessage").html(message);
                                            $("#addNewCardError").show();
                                        } else {
                                            $("#addNewCardErrorMessage").html("");
                                            $("#addNewCardError").hide();

                                            console.log('Send paymentmethodid to server...');
                                            var paymentMethodInfo = {
                                                paymentMethodId: result.setupIntent.payment_method
                                            }
                                            $.ajax(
                                                {
                                                    type: "POST",
                                                    contentType: "application/json",
                                                    url: "/api/projects/" + owner + "/" + name +
                                                        "/wallets/stripe/paymentMethods",
                                                    data: JSON.stringify(paymentMethodInfo),
                                                    success: function (paymentMethod) {
                                                        renderPaymentMethodRow(paymentMethod);
                                                        var showWarning = arePaymentMethodsDeactivated($('input.pmToggle'));
                                                        $("#realPaymentMethodsWarning").toggle(showWarning);
                                                        $('.pmToggle').bootstrapToggle({
                                                            on: 'Active',
                                                            off: 'Inactive',
                                                            width: '30%'
                                                        });
                                                        $("#noRealPaymentMethods").hide();
                                                        $("#realPaymentMethods").show();
                                                    },
                                                    error: function(jqXHR, error, errorThrown) {
                                                        $("#addStripePaymentMethodButton").removeClass("disabled");
                                                        $("#loadingStripePaymentForm").hide();
                                                        if(jqXHR.status && jqXHR.status == 400){
                                                            console.error("Bad Request: " + jqXHR.responseText);
                                                            $("#stripePaymentMethodFormError").show();
                                                        } else {
                                                            console.log("Server error status: " + jqXHR.status);
                                                            console.log("Server error: " + jqXHR.responseText);
                                                            alert(
                                                                "Something went wrong (" + jqXHR.status + ")." +
                                                                "Please, try again later."
                                                            );
                                                        }
                                                    }
                                                }
                                            );


                                            $("#cancelNewCardButton").trigger("click");
                                        }
                                        $("#addNewCardButton").removeClass("disabled");
                                        $("#cancelNewCardButton").removeClass("disabled");
                                        $("#loadingAddNewCard").hide();
                                    });
                                }
                            )

                        },
                        error: function(jqXHR, error, errorThrown) {
                            $("#addStripePaymentMethodButton").removeClass("disabled");
                            $("#loadingStripePaymentForm").hide();
                            if(jqXHR.status && jqXHR.status == 400){
                                console.error("Bad Request: " + jqXHR.responseText);
                                $("#stripePaymentMethodFormError").show();
                            } else {
                                console.log("Server error status: " + jqXHR.status);
                                console.log("Server error: " + jqXHR.responseText);
                                alert(
                                    "Something went wrong (" + jqXHR.status + ")." +
                                    "Please, try again later."
                                );
                            }
                        }
                    }
                );
            }
        );

        $("#cancelNewCardButton").on(
            "click",
            function(event) {
                event.preventDefault();
                $("#payment-method-element").html('');
                $("#addNewCardErrorMessage").html("");
                $("#addNewCardError").hide();
                $("#addStripePaymentMethodButton").show();
                $("#payment-method-element-card").hide();
            }
        )
        $("#activateStripeWalletButton").on(
            "click",
            function(event) {
                if(!$(this).hasClass("disabled")) {
                    event.preventDefault();
                    activateWallet(
                        $("#owner").text(),
                        $("#name").text(),
                        "stripe"
                    )
                }
            }
        )

        // $("#addContractForm").submit(
        //     function(e) {
        //         e.preventDefault();
        //         var valid = true;
        //         $.each($("#addContractForm .required"), function(index, element) {
        //             if($(element).val() == '') {
        //                 $(element).addClass("is-invalid");
        //                 valid = valid && false;
        //             } else {
        //                 $(element).removeClass("is-invalid");
        //                 valid = valid && true;
        //             }
        //         });
        //         if(valid) {
        //             var formData = $(this).serialize();
        //             //check if username exists before submit
        //             usersService.exists(
        //                 $("#username").val(),
        //                 "github",
        //                 function(){
        //                     $("#addContractLoading").show();
        //                     clearFormErrors();
        //                     disableForm(true);
        //                 }
        //             ).then(
        //                 function(){
        //                     return contractsService.add(project, formData)
        //                 }
        //             ).then(
        //                 function(contract){
        //                     $("#addContractForm input").val('');
        //                     $('#addContractForm option:first').prop('selected',true);
        //                     //we check the current page (0 based) displayed in table.
        //                     //if is last page, we're adding the contract to table.
        //                     //since it's the latest contract created.
        //                     loadContracts();
        //                 }
        //             ).catch(handleError)
        //                 .finally(
        //                     function(){
        //                         disableForm(false);
        //                         $("#addContractLoading").hide();
        //                     });
        //             return false;
        //         }
        //     }
        // );
    }
)