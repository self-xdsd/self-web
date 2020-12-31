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

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.WalletAlreadyExistsException;
import com.selfxdsd.selfweb.api.input.BillingInfoInput;
import static com.selfxdsd.selfweb.api.input.BillingInfoInput.*;
import com.selfxdsd.selfweb.api.output.JsonWallet;
import com.selfxdsd.selfweb.api.output.JsonWallets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Project Wallets API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
@RestController
@Validated
public class WalletsApi extends BaseApiController {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        WalletsApi.class
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
    public WalletsApi(final User user) {
        this.user = user;
    }

    /**
     * Get the wallets of one of the authenticated User's projects.
     * @param owner Owner of the repo.
     * @param name Name of the repo.
     * @return Json wallets.
     */
    @GetMapping(
        value = "/projects/{owner}/{name}/wallets",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> wallets(
        @PathVariable final String owner,
        @PathVariable final String name
    ) {
        final Project found = this.user.projects().getProjectById(
            owner + "/" + name, user.provider().name()
        );
        ResponseEntity<String> response = ResponseEntity.noContent().build();
        if(found != null) {
            response = ResponseEntity.ok(
                new JsonWallets(found.wallets()).toString()
            );
        }
        return response;
    }

    /**
     * Create a Stripe Wallet for one of the authenticated user's projects.
     * @param owner Owner of the project (login of user or org name).
     * @param name Repo name.
     * @param billingInfo Billing information.
     * @return Stripe Wallet as JSON string.
     */
    @PostMapping(
        value = "/projects/{owner}/{name}/wallets/stripe",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createStripeWallet(
        @PathVariable final String owner,
        @PathVariable final String name,
        @Valid final BillingInfoInput billingInfo) {
        ResponseEntity<String> response;
        final Project found = this.user.projects().getProjectById(
            owner + "/" + name, user.provider().name()
        );
        if(found == null) {
            response = ResponseEntity.badRequest().build();
        } else {
            try {
                response = ResponseEntity.ok(
                    new JsonWallet(
                        found.createStripeWallet(
                            new StripeBillingInfo(
                                billingInfo
                            )
                        )
                    ).toString()
                );
            } catch (final WalletAlreadyExistsException ex) {
                LOG.error(
                    "WalletAlreadyExistsException when creating "
                    + "a new Stripe wallet!",
                    ex.getMessage()
                );
                response = ResponseEntity.badRequest().body(
                    ex.toString()
                );
            }
        }
        return response;
    }

    /**
     * Updates real wallet with a new cash limit.
     * @param owner Owner of the project (login of user or org name).
     * @param name Repo name.
     * @param type Wallet type (ex: stripe).
     * @param limit New limit in dollars.
     * @return Wallet with updated cash limit.
     * @checkstyle ParameterNumber (40 lines)
     */
    @PutMapping(
        value = "/projects/{owner}/{name}/wallets/{type}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> updateCash(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String type,
        @RequestBody @Positive final float limit) {
        final ResponseEntity<String> response;
        if (type.equals(Wallet.Type.FAKE)) {
            response = ResponseEntity.badRequest()
                .body("Updating cash limit on a fake wallet not allowed.");
        } else {
            final Project found = this.user.projects().getProjectById(
                owner + "/" + name, user.provider().name()
            );
            if (found == null) {
                response = ResponseEntity.badRequest()
                    .body("Project not found");
            } else {
                Wallet wallet = null;
                for (final Wallet search : found.wallets()) {
                    if (search.type().equals(type)) {
                        wallet = search;
                        break;
                    }
                }
                if (wallet == null) {
                    response = ResponseEntity.badRequest()
                        .body("Wallet of type " + type + " not found.");
                } else {
                    final BigDecimal cash = BigDecimal.valueOf(limit)
                        .setScale(2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                    response = ResponseEntity.ok(
                        new JsonWallet(wallet.updateCash(cash)).toString()
                    );
                }
            }
        }
        return response;
    }

    /**
     * Activates a Wallet.
     * @param owner Owner of the project (login of user or org name).
     * @param name Repo name.
     * @param type Wallet type (ex: stripe).
     * @return Activated wallet.
     */
    @PutMapping(
        value = "/projects/{owner}/{name}/wallets/{type}/activate",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> activate(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String type
    ){
        final ResponseEntity<String> response;
        final Project project = this.user.projects().getProjectById(
            owner + "/" + name, user.provider().name()
        );
        if (project == null) {
            response = ResponseEntity.badRequest()
                .body("Project not found");
        } else {
            final Wallets wallets = project.wallets();
            Wallet wallet = null;
            for (final Wallet search : wallets) {
                if (search.type().equalsIgnoreCase(type)) {
                    wallet = search;
                    break;
                }
            }
            if (wallet == null) {
                response = ResponseEntity.badRequest()
                    .body("Wallet of type " + type + " not found.");
            }  else {
                final Wallet activated;
                if(wallet.active()) {
                    activated = wallet;
                } else {
                    activated = wallets.activate(wallet);
                }
                response = ResponseEntity.ok(
                    new JsonWallet(activated, Boolean.FALSE).toString()
                );
            }
        }
        return response;
    }
}
