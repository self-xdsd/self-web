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

import com.selfxdsd.api.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link WalletsApi}.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class WalletsApiTestCase {

    /**
     * WalletsApi.updateCash(...) can update the cash limit.
     * @checkstyle ExecutableStatementCount (100 lines)
     */
    @Test
    public void cashLimitIsUpdated(){
        final Self self = Mockito.mock(Self.class);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects projects = Mockito.mock(Projects.class);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(self.projects()).thenReturn(projects);
        Mockito.when(projects.getProjectById(
            "john/test",
            Provider.Names.GITHUB
        )).thenReturn(project);

        final Wallets wallets = Mockito.mock(Wallets.class);
        Mockito.when(project.wallets()).thenReturn(wallets);

        final Wallet wallet = Mockito.mock(Wallet.class);
        Mockito.when(wallet.type()).thenReturn(Wallet.Type.STRIPE);
        Mockito.when(wallet.updateCash(Mockito.any(BigDecimal.class)))
            .thenAnswer(inv -> {
                final Wallet answer = Mockito.mock(Wallet.class);
                final BigDecimal cash = inv.getArgument(0);
                Mockito.when(answer.cash()).thenReturn(cash);
                Mockito.when(answer.type()).thenReturn(Wallet.Type.STRIPE);
                Mockito.when(answer.active()).thenReturn(false);
                Mockito.when(answer.project()).thenReturn(project);
                Mockito.when(answer.debt()).thenReturn(BigDecimal.ZERO);
                Mockito.when(answer.available()).thenReturn(cash);
                return answer;
            });
        final List<Wallet> walletsSrc = List.of(wallet);
        Mockito.when(wallets.iterator()).thenReturn(walletsSrc.iterator());

        final WalletsApi api = new WalletsApi(user, self);

        final ResponseEntity<String> resp = api
            .updateCash("john", "test", Wallet.Type.STRIPE, 10.504f);
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.OK));
        final JsonObject body = Json.createReader(
            new StringReader(resp.getBody())
        ).readObject();
        MatcherAssert.assertThat(body, Matchers.equalTo(
            Json.createObjectBuilder()
                .add("type", Wallet.Type.STRIPE)
                .add("active", false)
                .add("cash", BigDecimal.valueOf(10.5)
                    .setScale(2, RoundingMode.HALF_UP))
                .add("debt", 0)
                .add("available", BigDecimal.valueOf(10.5)
                    .setScale(2, RoundingMode.HALF_UP))
                .build()
        ));
    }

    /**
     * WalletsApi.updateCash(...) ignores fake Wallet type.
     */
    @Test
    public void cashLimitIgnoresFakeWallet(){
        final Self self = Mockito.mock(Self.class);

        final WalletsApi api = new WalletsApi(Mockito.mock(User.class), self);

        final ResponseEntity<String> resp = api
            .updateCash("john", "test", Wallet.Type.FAKE, 10.5f);
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.BAD_REQUEST));
    }

    /**
     * WalletsApi.updateCash(...) returns error if project is not found.
     */
    @Test
    public void cashLimitReturnsErrorIfProjectNotFound(){
        final Self self = Mockito.mock(Self.class);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects projects = Mockito.mock(Projects.class);
        Mockito.when(self.projects()).thenReturn(projects);

        final WalletsApi api = new WalletsApi(user, self);

        final ResponseEntity<String> resp = api
            .updateCash("john", "test", Wallet.Type.STRIPE, 10.5f);
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.BAD_REQUEST));
    }

    /**
     * WalletsApi.updateCash(...) returns error if wallet is not found.
     */
    @Test
    public void cashLimitReturnsErrorIfWalletNotFound(){
        final Self self = Mockito.mock(Self.class);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects projects = Mockito.mock(Projects.class);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(self.projects()).thenReturn(projects);
        Mockito.when(projects.getProjectById(
            "john/test",
            Provider.Names.GITHUB
        )).thenReturn(project);

        final Wallets wallets = Mockito.mock(Wallets.class);
        Mockito.when(wallets.iterator())
            .thenReturn(List.<Wallet>of().iterator());
        Mockito.when(project.wallets()).thenReturn(wallets);

        final WalletsApi api = new WalletsApi(user, self);

        final ResponseEntity<String> resp = api
            .updateCash("john", "test", Wallet.Type.STRIPE, 10.5f);
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.BAD_REQUEST));
    }

    /**
     * WalletsApi.activate(...) activates a wallet.
     * @checkstyle ExecutableStatementCount (100 lines)
     */
    @Test
    public void activateWalletWorks(){
        final Self self = Mockito.mock(Self.class);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects projects = Mockito.mock(Projects.class);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(user.projects()).thenReturn(projects);
        Mockito.when(projects.getProjectById(
            "john/test",
            Provider.Names.GITHUB
        )).thenReturn(project);

        final Wallet fake = Mockito.mock(Wallet.class);
        Mockito.when(fake.type()).thenReturn(Wallet.Type.FAKE);
        Mockito.when(fake.cash()).thenReturn(BigDecimal.TEN);
        Mockito.when(fake.available()).thenReturn(BigDecimal.TEN);
        Mockito.when(fake.debt()).thenReturn(BigDecimal.ZERO);
        final Wallet stripe = Mockito.mock(Wallet.class);
        Mockito.when(stripe.type()).thenReturn(Wallet.Type.STRIPE);
        Mockito.when(stripe.cash()).thenReturn(BigDecimal.TEN);
        Mockito.when(stripe.available()).thenReturn(BigDecimal.TEN);
        Mockito.when(stripe.debt()).thenReturn(BigDecimal.ZERO);

        final Wallets wallets = Mockito.mock(Wallets.class);
        final List<Wallet> walletsSrc = new ArrayList<>(List.of(fake, stripe));
        Mockito.when(wallets.spliterator())
            .thenReturn(walletsSrc.spliterator());
        Mockito.when(wallets.iterator())
            .thenReturn(walletsSrc.iterator());
        Mockito.when(project.wallets()).thenReturn(wallets);
        Mockito.when(wallets.activate(stripe)).thenAnswer(inv -> {
            final Wallet activated = Mockito.mock(Wallet.class);
            Mockito.when(activated.type()).thenReturn(Wallet.Type.STRIPE);
            Mockito.when(activated.cash()).thenReturn(BigDecimal.TEN);
            Mockito.when(activated.available()).thenReturn(BigDecimal.TEN);
            Mockito.when(activated.debt()).thenReturn(BigDecimal.ZERO);
            Mockito.when(activated.active()).thenReturn(true);
            walletsSrc.set(1, activated);
            return activated;
        });

        final WalletsApi api = new WalletsApi(user, self);

        final ResponseEntity<String> resp =
            api.activate("john", "test", Wallet.Type.STRIPE);

        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.OK));
        final JsonArray body = Json.createReader(
            new StringReader(resp.getBody())
        ).readArray();
        MatcherAssert.assertThat(body, Matchers.equalTo(
            Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                    .add("type", Wallet.Type.FAKE)
                    .add("active", false)
                    .build())
                .add(Json.createObjectBuilder()
                    .add("type", Wallet.Type.STRIPE)
                    .add("active", true)
                    .build())
                .build()
        ));

        Mockito.verify(wallets, Mockito.times(1))
            .activate(stripe);
    }

    /**
     * WalletsApi.activate(...) returns error if project is not found.
     */
    @Test
    public void activateReturnsErrorIfProjectNotFound(){
        final Self self = Mockito.mock(Self.class);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects projects = Mockito.mock(Projects.class);
        Mockito.when(user.projects()).thenReturn(projects);

        final WalletsApi api = new WalletsApi(user, self);

        final ResponseEntity<String> resp = api
            .activate("john", "test", Wallet.Type.STRIPE);
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.BAD_REQUEST));
    }

    /**
     * WalletsApi.activate(...) returns error if wallet is not found.
     */
    @Test
    public void activateReturnsErrorIfWalletNotFound(){
        final Self self = Mockito.mock(Self.class);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects projects = Mockito.mock(Projects.class);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(user.projects()).thenReturn(projects);
        Mockito.when(projects.getProjectById(
            "john/test",
            Provider.Names.GITHUB
        )).thenReturn(project);

        final Wallets wallets = Mockito.mock(Wallets.class);
        Mockito.when(wallets.iterator())
            .thenReturn(List.<Wallet>of().iterator());
        Mockito.when(project.wallets()).thenReturn(wallets);

        final WalletsApi api = new WalletsApi(user, self);

        final ResponseEntity<String> resp = api
            .activate("john", "test", Wallet.Type.STRIPE);
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.BAD_REQUEST));
    }

    /**
     * WalletsApi.activate(...) returns error if wallet is already active.
     */
    @Test
    public void activateReturnsErrorIfWalletIsAlreadyActive(){
        final Self self = Mockito.mock(Self.class);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects projects = Mockito.mock(Projects.class);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(user.projects()).thenReturn(projects);
        Mockito.when(projects.getProjectById(
            "john/test",
            Provider.Names.GITHUB
        )).thenReturn(project);

        final Wallet stripe = Mockito.mock(Wallet.class);
        Mockito.when(stripe.type()).thenReturn(Wallet.Type.STRIPE);
        Mockito.when(stripe.active()).thenReturn(true);

        final Wallets wallets = Mockito.mock(Wallets.class);
        final List<Wallet> walletsSrc = List.of(stripe);
        Mockito.when(wallets.spliterator())
            .thenReturn(walletsSrc.spliterator());
        Mockito.when(wallets.iterator())
            .thenReturn(walletsSrc.iterator());
        Mockito.when(project.wallets()).thenReturn(wallets);

        final WalletsApi api = new WalletsApi(user, self);

        final ResponseEntity<String> resp = api
            .activate("john", "test", Wallet.Type.STRIPE);
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.BAD_REQUEST));
    }

}
