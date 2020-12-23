/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.selfweb.api;

import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.PayoutMethod;
import com.selfxdsd.api.User;
import com.selfxdsd.selfweb.api.input.BillingInfoInput;
import static com.selfxdsd.selfweb.api.input.BillingInfoInput.*;
import com.selfxdsd.selfweb.api.output.JsonPayoutMethods;
import com.stripe.exception.StripeException;
import com.stripe.model.AccountLink;
import com.stripe.model.LoginLink;
import com.stripe.net.RequestOptions;
import com.stripe.param.AccountLinkCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.Valid;
import java.util.HashMap;

/**
 * The authenticated Contributor's PayoutMethods API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
@RestController
public final class PayoutMethodsApi extends BaseApiController {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        PayoutMethodsApi.class
    );

    /**
     * Authenticated user.
     */
    private final User user;

    /**
     * Ctor.
     * @param user Authenticated user.
     */
    @Autowired
    public PayoutMethodsApi(final User user) {
        this.user = user;
    }

    /**
     * Get the authenticated Contributor's PayoutMethods.
     * @return String JSON.
     */
    @GetMapping(
        value = "/contributor/payoutmethods",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> payoutMethods() {
        final ResponseEntity<String> resp;
        final Contributor contributor = this.user.asContributor();
        if(contributor == null) {
            resp = ResponseEntity.badRequest().build();
        } else {
            resp = ResponseEntity.ok(
                new JsonPayoutMethods(
                    contributor.payoutMethods()
                ).toString()
            );
        }
        return resp;
    }

    /**
     * Create a SCA PayoutMethod for the authenticated Contributor.
     * @param billingInfo Billing information form.
     * @return PayoutMethod as JSON string.
     */
    @PostMapping(
        value = "/contributor/payoutmethods/stripe",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createStripeConnectAccount(
        @Valid final BillingInfoInput billingInfo
    ) {
        ResponseEntity<String> resp;
        final Contributor contributor = this.user.asContributor();
        if(contributor == null) {
            resp = ResponseEntity.badRequest().build();
        } else {
            try {
                final PayoutMethod created = contributor.createStripeAccount(
                    new StripeBillingInfo(billingInfo)
                );
                resp = ResponseEntity.ok(
                    Json.createObjectBuilder()
                        .add(
                            "stripeOnboardingLink",
                            this.createStripeOnboardingLink(
                                contributor
                                    .payoutMethods()
                                    .activate(created)
                                    .json()
                            )
                        ).build().toString()
                );
            } catch (final IllegalStateException ex) {
                LOG.error(
                    "Something went wrong while trying to create "
                    + "a Stripe Connect Account for Contributor "
                    + contributor.username()
                    + " (" + contributor.provider() + ").",
                    ex
                );
                resp = ResponseEntity.badRequest().build();
            }
        }
        return resp;
    }

    /**
     * Get the SCA Onboarding link.<br><br>
     *
     * After the contributor creates their account, they are redirected
     * to Stripe, where they have to complete an Onboarding process (provide
     * personal information, bank account etc).<br><br>
     *
     * However, the contributor might fail to complete it, so we have to
     * verify the account's status and display a link where they can go
     * and complete the process.
     * @return String.
     */
    @PostMapping(
        value = "/contributor/payoutmethods/stripe/onboarding",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> stripeConnectOnboardingLink() {
        ResponseEntity<String> resp;
        final Contributor contributor = this.user.asContributor();
        if(contributor == null) {
            resp = ResponseEntity.badRequest().build();
        } else {
            PayoutMethod stripe = null;
            for(final PayoutMethod method : contributor.payoutMethods()){
                if(PayoutMethod.Type.STRIPE.equalsIgnoreCase(method.type())) {
                    stripe = method;
                    break;
                }
            }
            if(stripe == null) {
                resp = ResponseEntity.badRequest().build();
            } else {
                final JsonObject account = stripe.json();
                if(!account.getBoolean("details_submitted")) {
                    resp = ResponseEntity
                        .ok(
                            Json.createObjectBuilder()
                                .add(
                                    "stripeOnboardingLink",
                                    this.createStripeOnboardingLink(account)
                                ).build().toString()
                        );
                } else {
                    resp = ResponseEntity.badRequest().build();
                }
            }
        }
        return resp;
    }

    /**
     * Get the Contributor's SCA login link.
     * @return String.
     */
    @PostMapping(
        value = "/contributor/payoutmethods/stripe/login",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> stripeConnectLoginLink() {
        ResponseEntity<String> resp;
        final Contributor contributor = this.user.asContributor();
        if(contributor == null) {
            resp = ResponseEntity.badRequest().build();
        } else {
            PayoutMethod stripe = null;
            for(final PayoutMethod method : contributor.payoutMethods()){
                if(PayoutMethod.Type.STRIPE.equalsIgnoreCase(method.type())) {
                    stripe = method;
                    break;
                }
            }
            if(stripe == null) {
                resp = ResponseEntity.badRequest().build();
            } else {
                final JsonObject account = stripe.json();
                if(account.getBoolean("details_submitted")) {
                    resp = ResponseEntity
                        .ok(
                            Json.createObjectBuilder()
                                .add(
                                    "stripeLoginLink",
                                    this.createStripeLoginLink(account)
                                ).build().toString()
                        );
                } else {
                    resp = ResponseEntity.badRequest().build();
                }
            }
        }
        return resp;
    }

    /**
     * Create an onboarding link for the given Stripe account.
     * @param account Stripe account in Json.
     * @return String.
     */
    private String createStripeOnboardingLink(final JsonObject account) {
        try {
            return AccountLink.create(
                AccountLinkCreateParams.builder()
                    .setAccount(account.getString("id"))
                    .setRefreshUrl(
                        System.getenv("self_xdsd_base_url")
                            +"/contributor?stripe=aborted"
                    ).setReturnUrl(
                        System.getenv("self_xdsd_base_url")
                        + "/contributor?stripe=finished"
                    ).setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                    .build()
            ).getUrl();
        } catch (final StripeException ex) {
            LOG.error(
                "Stripe threw an exception while trying to get "
                + "the Onboarding Link for Account " + account.getString("id"),
                ex
            );
            throw new IllegalStateException(
                "Stripe threw an exception while trying to get "
                + "the Onboarding Link for Account " + account.getString("id")
            );
        }
    }

    /**
     * Get the Login link for the specified SCA account.
     * @param account SCA account in JSON.
     * @return String.
     */
    private String createStripeLoginLink(final JsonObject account) {
        try {
            return LoginLink.createOnAccount(
                account.getString("id"),
                new HashMap<>(),
                RequestOptions.getDefault()
            ).getUrl();
        } catch (final StripeException ex) {
            LOG.error(
                "Stripe threw an exception while trying to get "
                + "the Login Link for Account " + account.getString("id"),
                ex
            );
            throw new IllegalStateException(
                "Stripe threw an exception while trying to get "
                + "the Login Link for Account " + account.getString("id")
            );
        }
    }
}
