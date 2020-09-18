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
import com.selfxdsd.selfweb.api.output.JsonPayoutMethod;
import com.selfxdsd.selfweb.api.output.JsonPayoutMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @return PayoutMethod as JSON string.
     */
    @PostMapping(
        value = "/contributor/payoutmethods/stripe",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createStripeConnectAccount() {
        ResponseEntity<String> resp;
        final Contributor contributor = this.user.asContributor();
        if(contributor == null) {
            resp = ResponseEntity.badRequest().build();
        } else {
            try {
                resp = ResponseEntity.ok(
                    new JsonPayoutMethod(
                        contributor.createStripeAccount()
                    ).toString()
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
        value = "/contributor/payoutmethods/stripe",
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
                if(stripe.json().getString("") == null) {
                    resp = ResponseEntity.ok().build();
                } else {
                    resp = ResponseEntity.badRequest().build();
                }
            }
        }
        return resp;
    }
}
