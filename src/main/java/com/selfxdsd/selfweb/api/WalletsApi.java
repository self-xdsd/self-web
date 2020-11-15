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

import com.selfxdsd.api.Project;
import com.selfxdsd.api.Self;
import com.selfxdsd.api.User;
import com.selfxdsd.api.Wallet;
import com.selfxdsd.api.Wallets;
import com.selfxdsd.api.exceptions.WalletAlreadyExistsException;
import com.selfxdsd.selfweb.api.output.JsonWallet;
import com.selfxdsd.selfweb.api.output.JsonWallets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Project Wallets API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #135:60min Once the PaymentMethod logic is implemented in the core,
 *  we should add a form in the Real Wallet widget, where the User will add
 *  payment methods.
 * @todo #178:30min On frontend, in `getProject.js`, integrate the wallet
 *  activation feature with the backend.
 * @todo #179:15min Update WalletsApi endpoints to use `this.user.projects` when
 *  searching for a project (where is the case). Right now most of them are
 *  using `this.self.projects` meaning that any user can have access to other
 *  users wallets.
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
     * Self's core.
     */
    private final Self self;

    /**
     * Ctor.
     * @param user Authenticated user.
     * @param self Self's core.
     */
    @Autowired
    public WalletsApi(final User user, final Self self) {
        this.user = user;
        this.self = self;
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
        final Project found = this.self.projects().getProjectById(
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
     * @return Stripe Wallet as JSON string.
     */
    @PostMapping(
        value = "/projects/{owner}/{name}/wallets/stripe",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createStripeWallet(
        @PathVariable final String owner,
        @PathVariable final String name
    ) {
        ResponseEntity<String> response;
        final Project found = this.self.projects().getProjectById(
            owner + "/" + name, user.provider().name()
        );
        if(found == null) {
            response = ResponseEntity.badRequest().build();
        } else {
            try {
                response = ResponseEntity.ok(
                    new JsonWallet(
                        found.createStripeWallet()
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
            final Project found = this.self.projects().getProjectById(
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
     * Activates a Wallet. Returns an array of wallet types which includes the
     * activated one and the other types deactivated.
     * @param owner Owner of the project (login of user or org name).
     * @param name Repo name.
     * @param type Wallet type (ex: stripe).
     * @return Array of wallet types which includes the activated one and the
     * other types deactivated.
     */
    @PutMapping(
        value = "/projects/{owner}/{name}/wallets/{type}/activate",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> activate(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String type){

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
                if (search.type().equals(type)) {
                    wallet = search;
                    break;
                }
            }
            if (wallet == null) {
                response = ResponseEntity.badRequest()
                    .body("Wallet of type " + type + " not found.");
            } else if (wallet.active()){
                response = ResponseEntity.badRequest()
                    .body("Wallet of type " + type + " already active");
            } else {
                wallets.activate(wallet);
                final JsonArray walletTypes = new JsonWallets(wallets)
                    .stream()
                    .map(w -> Json.createObjectBuilder()
                        .add("type", ((JsonWallet) w).get("type"))
                        .add("active", ((JsonWallet) w).get("active"))
                        .build())
                    .reduce(
                        Json.createArrayBuilder(),
                        JsonArrayBuilder::add,
                        (comb, curr) -> comb
                    ).build();
                response = ResponseEntity.ok(walletTypes.toString());
            }
        }
        return response;
    }
}
