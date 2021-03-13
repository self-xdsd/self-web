/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
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

import com.selfxdsd.api.PaymentMethod;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.User;
import com.selfxdsd.api.Wallet;
import com.selfxdsd.selfweb.api.output.JsonPaymentMethod;
import com.stripe.model.SetupIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.Pattern;
import java.io.StringReader;

/**
 * API for Payment methods of a Wallet.<br><br>
 *
 * The methods here are only for Stripe wallets at the moment,
 * because those are the only real wallets which actually have
 * payment methods.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.4
 */
@RestController
@Validated
public class PaymentMethodsApi extends BaseApiController {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        PaymentMethodsApi.class
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
    public PaymentMethodsApi(final User user) {
        this.user = user;
    }

    /**
     * Create a SetupIntent for a new PaymentMethod of a Stripe Wallet.<br><br>
     *
     * This endpoint returns a Stripe clientSecret which is used together with
     * the Stripe JS Library to display a widget, take the payment information
     * and send it to Stripe.
     *
     * @param owner Owner of the project (login of user or org name).
     * @param name Repo name.
     * @see <a href='https://stripe.com/docs/payments/save-and-reuse'>docs</a>
     * @return SetupIntent.
     */
    @PostMapping(
        value = "/projects/{owner}/{name}/wallets/stripe/paymentMethods/setup",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createStripePaymentMethodSetupIntent(
        @PathVariable final String owner,
        @PathVariable final String name
    ) {
        LOG.debug(
            "Creating Stripe PaymentMethod SetupIntent for "
            + owner + "/" + name + "... "
        );
        ResponseEntity<String> response;
        final Project found = this.user.projects().getProjectById(
            owner + "/" + name, user.provider().name()
        );
        if(found == null) {
            LOG.error(
                "Project " + owner + "/" + name + " not found! "
                + "Bad request."
            );
            response = ResponseEntity.badRequest().build();
        } else {
            Wallet wallet = null;
            for (final Wallet search : found.wallets()) {
                if (search.type().equals(Wallet.Type.STRIPE)) {
                    wallet = search;
                    break;
                }
            }
            if (wallet == null) {
                LOG.error("Stripe Wallet missing! Bad Request.");
                response = ResponseEntity.badRequest()
                    .body("Stripe Wallet not found.");
            } else {
                final SetupIntent intent = wallet.paymentMethodSetupIntent();
                LOG.debug("SetupIntent successfully created!");
                response = ResponseEntity.ok(
                    Json.createObjectBuilder()
                        .add("clientSecret", intent.getClientSecret())
                        .build()
                        .toString()
                );
            }
        }
        return response;
    }

    /**
     * Save a PaymentMethod in a Stripe wallet. Before using this endpoint,
     * you have to call /createStripePaymentMethodSetupIntent, send the card
     * data to Stripe using StripeJS and the clientSecret, and receive the
     * paymentMethodId.
     * @param owner Owner of the project (login of user or org name).
     * @param name Repo name.
     * @param body PaymentMethod data in JSON.
     * @return PaymentMethod.
     */
    @PostMapping(
        value = "/projects/{owner}/{name}/wallets/stripe/paymentMethods",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> saveStripePaymentMethod(
        @PathVariable final String owner,
        @PathVariable final String name,
        @RequestBody
        @Pattern(regexp = "[a-zA-Z0-9\\-_\\{\\}\" :,]{1,256}")
        final String body
    ) {
        LOG.debug(
            "Saving new Stripe PaymentMethod for Project "
            + owner + "/" + name + "... "
        );
        ResponseEntity<String> response;
        final Project found = this.user.projects().getProjectById(
            owner + "/" + name, user.provider().name()
        );
        if(found == null) {
            LOG.error(
                "Project " + owner + "/" + name
                 + " not found! Bad request."
            );
            response = ResponseEntity.badRequest().build();
        } else {
            Wallet wallet = null;
            for (final Wallet search : found.wallets()) {
                if (search.type().equals(Wallet.Type.STRIPE)) {
                    wallet = search;
                    break;
                }
            }
            if (wallet == null) {
                LOG.error("Stripe Wallet missing! Bad Request.");
                response = ResponseEntity.badRequest()
                    .body("Stripe Wallet not found.");
            } else {
                final JsonObject jsonBody = Json.createReader(
                    new StringReader(body)
                ).readObject();
                final PaymentMethod paymentMethod = wallet.paymentMethods()
                    .register(
                        wallet,
                        jsonBody.getString("paymentMethodId")
                    );
                LOG.debug("PaymentMethod successfully saved!");
                response = ResponseEntity.ok(
                    new JsonPaymentMethod(paymentMethod).toString()
                );
            }
        }
        return response;
    }

    /**
     * This will activate the specified Stripe PaymentMethod and will
     * deactivate all the others (there can be only one active PaymentMethod).
     * @param owner Owner of the project (login of user or org name).
     * @param name Repo name.
     * @param paymentMethodId Id of the PaymentMethod to be activated.
     * @return Activated PaymentMethod in JSON.
     */
    @PutMapping(
        value = "/projects/{owner}/{name}/wallets/stripe/paymentMethods/"
            + "{paymentMethodId}/activate",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> activateStripePaymentMethod(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String paymentMethodId
    ) {
        LOG.debug(
            "Activating Stripe PaymentMethod " + paymentMethodId
                + " of Project " + owner + "/" + name + "... "
        );
        ResponseEntity<String> response;
        try {
            final PaymentMethod paymentMethod = this
                .getStripePaymentMethod(owner, name, paymentMethodId);
            final JsonObject json;
            if (paymentMethod.active()) {
                LOG.debug(
                    "PaymentMethod already active. Not doing anything."
                );
                json = new JsonPaymentMethod(paymentMethod, Boolean.FALSE);
            } else {
                final PaymentMethod active = paymentMethod.activate();
                LOG.debug("PaymentMethod successfully activated!");
                json = new JsonPaymentMethod(active, Boolean.FALSE);
            }
            response = ResponseEntity.ok(json.toString());
        } catch (final IllegalStateException exception) {
            final String message = exception.getMessage();
            if (message != null) {
                response = ResponseEntity.badRequest().body(message);
            } else {
                response = ResponseEntity.badRequest().build();
            }
        }
        return response;
    }

    /**
     * This will deactivate the specified Stripe PaymentMethod.
     *
     * @param owner Owner of the project (login of user or org name).
     * @param name Repo name.
     * @param paymentMethodId Id of the PaymentMethod to be deactivated.
     * @return Deactivated PaymentMethod in JSON.
     */
    @PutMapping(
        value = "/projects/{owner}/{name}/wallets/stripe/paymentMethods/"
            + "{paymentMethodId}/deactivate",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deactivateStripePaymentMethod(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String paymentMethodId
    ) {
        LOG.debug(
            "Deactivating Stripe PaymentMethod " + paymentMethodId
                + " of Project " + owner + "/" + name + "... "
        );
        ResponseEntity<String> response;
        try {
            final PaymentMethod paymentMethod = this
                .getStripePaymentMethod(owner, name, paymentMethodId)
                .deactivate();
            LOG.debug("PaymentMethod successfully deactivated!");
            final JsonPaymentMethod json = new JsonPaymentMethod(
                paymentMethod,
                Boolean.FALSE
            );
            response = ResponseEntity.ok(json.toString());
        } catch (final IllegalStateException exception) {
            final String message = exception.getMessage();
            if (message != null) {
                response = ResponseEntity.badRequest().body(message);
            } else {
                response = ResponseEntity.badRequest().build();
            }
        }
        return response;
    }

    /**
     * Get a PaymentMethod from STRIPE wallet.
     * @param owner Owner of the project (login of user or org name).
     * @param name Repo name.
     * @param paymentMethodId Id of the PaymentMethod to be deactivated.
     * @return PaymentMethod.
     * @throws IllegalStateException if method is not found.
     * @checkstyle ExecutableStatementCount (60 lines).
     */
    private PaymentMethod getStripePaymentMethod(
        final String owner,
        final String name,
        final String paymentMethodId
    ) throws IllegalStateException {
        final PaymentMethod result;
        final Project found = this.user.projects().getProjectById(
            owner + "/" + name, user.provider().name()
        );
        if (found == null) {
            LOG.error(
                "Project " + owner + "/" + name + " not found! "
                    + "Bad Request."
            );
            throw new IllegalStateException();
        } else {
            Wallet wallet = null;
            for (final Wallet search : found.wallets()) {
                if (search.type().equals(Wallet.Type.STRIPE)) {
                    wallet = search;
                    break;
                }
            }
            if (wallet == null) {
                LOG.error("Stripe Wallet missing! Bad Request.");
                throw new IllegalStateException("Stripe Wallet not found.");
            } else {
                PaymentMethod paymentMethod = null;
                for (final PaymentMethod method : wallet.paymentMethods()) {
                    final String methodId = method.identifier();
                    if (methodId.equalsIgnoreCase(paymentMethodId)) {
                        paymentMethod = method;
                        break;
                    }
                }
                if (paymentMethod == null) {
                    LOG.error(
                        "PaymentMethod " + paymentMethodId + " not found! "
                            + "Bad Request."
                    );
                    throw new IllegalStateException(
                        "Payment Method not found."
                    );
                }
                result = paymentMethod;
            }
        }
        return result;
    }
}
