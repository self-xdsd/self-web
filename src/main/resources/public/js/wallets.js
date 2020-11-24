/**
 * Functions handling a Project's Wallets.
 */

function getProjectWallets() {
    $("#loadingWallets").show();
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
                        $("#fakeCash").html(wallet.cash + " €");
                        $("#fakeDebt").html(wallet.debt + " €");
                        $("#fakeAvailable").html(wallet.available + " €");
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
                        $("#stripeCash").html(wallet.cash + " €");
                        $("#stripeDebt").html(wallet.debt + " €");
                        $("#stripeAvailable").html(wallet.available + " €");
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
                                $("#stripeCash").html(updatedWallet.cash + " €");
                                $("#stripeDebt").html(updatedWallet.debt + " €");
                                $("#stripeAvailable").html(updatedWallet.available + " €");
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
 * Pay a Contract (its active Invoice).
 * @package repo (e.g. "mihai/test") where the Contract belongs.
 * @param contract Contract.
 */
function payContract(repo, contract) {
    $.ajax(
        {
            type: "PUT",
            contentType: "application/json",
            url: "/api/projects/" + repo +
                "/contracts/" + contract.id.contributorUsername + "/invoice/pay" +
                "?role=" + contract.id.role,
            success: function (json) {
                console.log("Contract PAID: " + JSON.stringify(json));
            },
            error: function(jqXHR, error, errorThrown) {
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