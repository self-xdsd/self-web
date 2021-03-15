/**
 * JS for a Contributor's PayoutMethods.
 */
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
        $("#create ConnectAccountForm").submit(
            function(e) {
                e.preventDefault();
                var valid = true;
                $.each($("#createStripeConnectAccountForm .required"), function (index, element) {
                    if($(element).is(":visible")) {
                        if ($(element).val().trim() == '') {
                            $(element).addClass("is-invalid");
                            valid = false;
                        } else {
                            $(element).removeClass("is-invalid");
                        }
                    }
                });
                $.each($("#createStripeConnectAccountForm input"), function (index, element) {
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
                $.each($("#createStripeConnectAccountForm input[type=checkbox]"), function(index, element) {
                    if($(element).is(":visible")) {
                        if (!this.checked) {
                            $(element).addClass("is-invalid");
                            valid = false;
                        } else {
                            $(element).removeClass("is-invalid");
                        }
                    }
                });
                $.each($("#createStripeConnectAccountForm textarea"), function (index, element) {
                    if (hasSpecialChars($(element).val())) {
                        $("." + $(element).attr("name") + "-error").html(
                            "The only special characters allowed are ,.-_&@#'"
                        );
                        $(element).addClass("is-invalid");
                        valid = false;
                    } else {
                        $(element).removeClass("is-invalid");
                    }
                });

                if (valid) {
                    $("#createStripeConnectAccountButton").addClass("disabled");
                    $("#loadingCreateStripeAccount").show();
                    var form = $(this);
                    $.ajax(
                        {
                            type: "POST",
                            url: "/api/contributor/payoutmethods/stripe",
                            data: form.serialize(),
                            success: function (payoutMethod) {
                                console.log("Redirecting to SCA Onboarding page: " + payoutMethod.stripeOnboardingLink)
                                window.location.replace(payoutMethod.stripeOnboardingLink);
                            },
                            error: function (jqXHR, error, errorThrown) {
                                $("#createStripeConnectAccountButton").removeClass("disabled");
                                $("#loadingCreateStripeAccount").hide();
                                if (jqXHR.status && jqXHR.status == 400) {
                                    console.error("Bad Request: " + jqXHR.responseText);
                                    $(".browser-error").show();
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
        $("#completeOnboardingProcessForm").submit(
            function(e) {
                e.preventDefault();
                $("#completeOnboardingProcessButton").addClass("disabled");
                $("#loadingCompleteOnboardingProcess").show();
                var form = $(this);
                $.ajax(
                    {
                        type: "POST",
                        url: "/api/contributor/payoutmethods/stripe/onboarding",
                        data: form.serialize(),
                        success: function (payoutMethod) {
                            console.log("Redirecting to SCA Onboarding page: " + payoutMethod.stripeOnboardingLink)
                            window.location.replace(payoutMethod.stripeOnboardingLink);
                        },
                        error: function(jqXHR, error, errorThrown) {
                            $("#completeOnboardingProcessButton").removeClass("disabled");
                            $("#loadingCompleteOnboardingProcess").hide();
                            if(jqXHR.status && jqXHR.status == 400){
                                console.error("Bad Request: " + jqXHR.responseText);
                                $(".browser-error").show();
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
        $('#stripeDeleteButton').submit(
                function(e) {
                    e.preventDefault();
                    $.ajax({
                        type: "DELETE",
                        url: "/api/contributor/payoutmethods/stripe",
                        data: form.serealize(),
                        success: function(response){
                            $(".payout-methods").hide();
                            $(".no-payout-methods").show();
                        },
                        error: function(jqXHR, error, errorThrown){
                            if(jqXHR.status && jqXHR.status == 400){
                                console.error("Bad Request: " + jqXHR.responseText);
                                $(".browser-error").show();
                            } else {
                                console.log("Server error status: " + jqXHR.status);
                                console.log("Server error: " + jqXHR.responseText);
                                alert(
                                    "Something went wrong (" + jqXHR.status + ")." +
                                    "Please, try again later."
                                );
                            }
                            
                        }
                    });
                }
        );
        $("#stripeDashboardForm").submit(
            function(e) {
                e.preventDefault();
                $("#stripeDashboardButton").addClass("disabled");
                $("#loadingStripeDashboardForm").show();
                var form = $(this);
                $.ajax(
                    {
                        type: "POST",
                        url: "/api/contributor/payoutmethods/stripe/login",
                        data: form.serialize(),
                        success: function (payoutMethod) {
                            console.log("Opening SCA Dashboard page: " + payoutMethod.stripeLoginLink)
                            window.location.replace(payoutMethod.stripeLoginLink);
                        },
                        error: function(jqXHR, error, errorThrown) {
                            $("#stripeDashboardButton").removeClass("disabled");
                            $("#loadingStripeDashboardForm").hide();
                            if(jqXHR.status && jqXHR.status == 400){
                                console.error("Bad Request: " + jqXHR.responseText);
                                $(".browser-error").show();
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
    }
);