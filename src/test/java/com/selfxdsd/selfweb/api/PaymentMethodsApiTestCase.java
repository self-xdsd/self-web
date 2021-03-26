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
import com.selfxdsd.api.PaymentMethods;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Projects;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import com.selfxdsd.api.Wallet;
import com.selfxdsd.api.Wallets;
import com.stripe.model.SetupIntent;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Unit tests for {@link PaymentMethodsApi}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ExecutableStatementCount (1000 lines)
 */
public final class PaymentMethodsApiTestCase {

    /**
     * Creating a SetupIntent for a PaymentMethod returns BAD REQUEST
     * if the Project is missing.
     */
    @Test
    public void setupIntentProjectMissing() {
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(null);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .createStripePaymentMethodSetupIntent("mihai", "test")
                .getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * Creating a SetupIntent for a PaymentMethod returns BAD REQUEST
     * if the Project doesn't have a Stripe wallet.
     */
    @Test
    public void setupIntentStripeWalletMissing() {
        final Wallet fake = Mockito.mock(Wallet.class);
        Mockito.when(fake.type()).thenReturn("FAKE");
        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(fake).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .createStripePaymentMethodSetupIntent("mihai", "test")
                .getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * Creating a SetupIntent for a PaymentMethod works if the Project exists
     * and it has a Stripe Wallet.
     */
    @Test
    public void setupIntentStripeWorks() {
        final Wallet stripeWallet = Mockito.mock(Wallet.class);
        Mockito.when(stripeWallet.type()).thenReturn("STRIPE");
        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(stripeWallet).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);

        final SetupIntent created = new SetupIntent();
        created.setClientSecret("createdIntentSecret");
        Mockito.when(stripeWallet.paymentMethodSetupIntent()).thenReturn(
            created
        );

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        final ResponseEntity<String> resp = new PaymentMethodsApi(user)
            .createStripePaymentMethodSetupIntent("mihai", "test");

        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
        MatcherAssert.assertThat(
            Json.createReader(
                new StringReader(resp.getBody())
            ).readObject().getString("clientSecret"),
            Matchers.equalTo("createdIntentSecret")
        );
    }

    /**
     * Saving a new PaymentMethod returns BAD REQUEST
     * if the Project is missing.
     */
    @Test
    public void savePaymentMethodProjectMissing() {
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(null);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .saveStripePaymentMethod(
                    "mihai",
                    "test",
                    "{\"paymentMethodId\":\"pm123\"}"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * Saving a new PaymentMethod returns BAD REQUEST
     * if the Project doesn't have a Stripe wallet.
     */
    @Test
    public void savePaymentMethodStripeWalletMissing() {
        final Wallet fake = Mockito.mock(Wallet.class);
        Mockito.when(fake.type()).thenReturn("FAKE");
        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(fake).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .saveStripePaymentMethod(
                    "mihai",
                    "test",
                    "{\"paymentMethodId\":\"pm123\"}"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * Saving a new PaymentMethod works if the Project exists
     * and it has a Stripe Wallet.
     */
    @Test
    public void saveStripePaymentMethodWorks() {
        final PaymentMethod saved = Mockito.mock(PaymentMethod.class);
        Mockito.when(saved.identifier()).thenReturn("pm123");
        Mockito.when(saved.active()).thenReturn(Boolean.FALSE);
        Mockito.when(saved.json()).thenReturn(
            Json.createObjectBuilder().build()
        );

        final PaymentMethods ofWallet = Mockito.mock(PaymentMethods.class);
        final Iterator<PaymentMethod> iterator = Mockito.mock(Iterator.class);
        Mockito.when(ofWallet.iterator()).thenReturn(iterator);
        Mockito.when(iterator.hasNext()).thenReturn(true);

        final Wallet stripeWallet = Mockito.mock(Wallet.class);
        Mockito.when(stripeWallet.type()).thenReturn("STRIPE");
        Mockito.when(stripeWallet.paymentMethods()).thenReturn(ofWallet);

        Mockito.when(
            ofWallet.register(stripeWallet, "pm123")
        ).thenReturn(saved);

        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(stripeWallet).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);


        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        final ResponseEntity<String> resp = new PaymentMethodsApi(user)
            .saveStripePaymentMethod(
                "mihai",
                "test",
                "{\"paymentMethodId\":\"pm123\"}"
            );

        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );

        final JsonObject jsonResp = Json.createReader(
            new StringReader(resp.getBody())
        ).readObject();

        MatcherAssert.assertThat(
            jsonResp.getJsonObject("self").getString("paymentMethodId"),
            Matchers.equalTo("pm123")
        );
        MatcherAssert.assertThat(
            jsonResp.getJsonObject("self").getBoolean("active"),
            Matchers.is(Boolean.FALSE)
        );
        MatcherAssert.assertThat(
            jsonResp.getJsonObject("stripe").keySet(),
            Matchers.emptyIterable()
        );
    }
    /**
     * Saving a new PaymentMethod works if the Project exists
     * and it has a Stripe Wallet and activate the method if there are no other
     * PaymentMethods.
     */
    @Test
    public void saveStripePaymentMethodAndActivateWorks() {
        final PaymentMethod saved = Mockito.mock(PaymentMethod.class);
        Mockito.when(saved.identifier()).thenReturn("pm123");
        Mockito.when(saved.active()).thenReturn(Boolean.FALSE);
        Mockito.when(saved.json()).thenReturn(
            Json.createObjectBuilder().build()
        );
        final PaymentMethod activated = Mockito.mock(PaymentMethod.class);
        Mockito.when(saved.identifier()).thenReturn("pm123");
        Mockito.when(saved.active()).thenReturn(Boolean.TRUE);
        Mockito.when(saved.json()).thenReturn(
            Json.createObjectBuilder().build()
        );
        Mockito.when(saved.activate()).thenReturn(activated);

        final PaymentMethods ofWallet = Mockito.mock(PaymentMethods.class);
        final List<PaymentMethod> ofWalletSrc = new ArrayList<>();
        Mockito.when(ofWallet.iterator())
            .thenAnswer(inv -> ofWalletSrc.iterator());

        final Wallet stripeWallet = Mockito.mock(Wallet.class);
        Mockito.when(stripeWallet.type()).thenReturn("STRIPE");
        Mockito.when(stripeWallet.paymentMethods()).thenReturn(ofWallet);

        Mockito.when(ofWallet.register(stripeWallet, "pm123"))
            .thenAnswer(inv -> {
                ofWalletSrc.add(saved);
                return saved;
            });

        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator())
            .thenAnswer(inv -> List.of(stripeWallet).iterator());
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);


        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        final ResponseEntity<String> resp = new PaymentMethodsApi(user)
            .saveStripePaymentMethod(
                "mihai",
                "test",
                "{\"paymentMethodId\":\"pm123\"}"
            );

        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );

        final JsonObject jsonResp = Json.createReader(
            new StringReader(resp.getBody())
        ).readObject();

        MatcherAssert.assertThat(
            jsonResp.getJsonObject("self").getString("paymentMethodId"),
            Matchers.equalTo("pm123")
        );
        MatcherAssert.assertThat(
            jsonResp.getJsonObject("self").getBoolean("active"),
            Matchers.is(Boolean.TRUE)
        );
        MatcherAssert.assertThat(
            jsonResp.getJsonObject("stripe").keySet(),
            Matchers.emptyIterable()
        );
    }

    /**
     * Activating a Stripe PaymentMethod returns BAD REQUEST
     * if the Project is missing.
     */
    @Test
    public void activatePaymentMethodProjectMissing() {
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(null);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .activateStripePaymentMethod(
                    "mihai",
                    "test",
                    "paymentMethod123"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * Activating a PaymentMethod returns BAD REQUEST
     * if the Project doesn't have a Stripe wallet.
     */
    @Test
    public void activatePaymentMethodStripeWalletMissing() {
        final Wallet fake = Mockito.mock(Wallet.class);
        Mockito.when(fake.type()).thenReturn("FAKE");
        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(fake).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .activateStripePaymentMethod(
                    "mihai",
                    "test",
                    "paymentMethod123"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * Trying to activate a missing PaymentMethod returns BAD REQUEST.
     */
    @Test
    public void activateMissingPaymentMethod() {
        final PaymentMethod other = Mockito.mock(PaymentMethod.class);
        Mockito.when(other.identifier()).thenReturn("other123");
        final PaymentMethods ofWallet = Mockito.mock(PaymentMethods.class);
        Mockito.when(ofWallet.iterator()).thenReturn(
            List.of(other).iterator()
        );
        final Wallet stripe = Mockito.mock(Wallet.class);
        Mockito.when(stripe.type()).thenReturn("STRIPE");
        Mockito.when(stripe.paymentMethods()).thenReturn(ofWallet);

        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(stripe).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .activateStripePaymentMethod(
                    "mihai",
                    "test",
                    "paymentMethod123"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * Trying to activate a PaymentMethod which is already active should be OK.
     */
    @Test
    public void activateAlreadyActivePaymentMethod() {
        final PaymentMethod active = Mockito.mock(PaymentMethod.class);
        Mockito.when(active.identifier()).thenReturn("paymentMethod123");
        Mockito.when(active.active()).thenReturn(Boolean.TRUE);

        final PaymentMethods ofWallet = Mockito.mock(PaymentMethods.class);
        Mockito.when(ofWallet.iterator()).thenReturn(
            List.of(active).iterator()
        );
        final Wallet stripe = Mockito.mock(Wallet.class);
        Mockito.when(stripe.type()).thenReturn("STRIPE");
        Mockito.when(stripe.paymentMethods()).thenReturn(ofWallet);

        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(stripe).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .activateStripePaymentMethod(
                    "mihai",
                    "test",
                    "paymentMethod123"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
    }

    /**
     * Trying to activate a PaymentMethod works.
     */
    @Test
    public void activateInactivePaymentMethod() {
        final PaymentMethod activated = Mockito.mock(PaymentMethod.class);
        Mockito.when(activated.identifier()).thenReturn("paymentMethod123");
        Mockito.when(activated.active()).thenReturn(Boolean.TRUE);

        final PaymentMethod original = Mockito.mock(PaymentMethod.class);
        Mockito.when(original.identifier()).thenReturn("paymentMethod123");
        Mockito.when(original.active()).thenReturn(Boolean.FALSE);
        Mockito.when(original.activate()).thenReturn(activated);

        final PaymentMethods ofWallet = Mockito.mock(PaymentMethods.class);
        Mockito.when(ofWallet.iterator()).thenReturn(
            List.of(original).iterator()
        );
        final Wallet stripe = Mockito.mock(Wallet.class);
        Mockito.when(stripe.type()).thenReturn("STRIPE");
        Mockito.when(stripe.paymentMethods()).thenReturn(ofWallet);

        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(stripe).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .activateStripePaymentMethod(
                    "mihai",
                    "test",
                    "paymentMethod123"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
    }

    /**
     * Trying to deactivate a PaymentMethod works.
     */
    @Test
    public void deactivateInactivePaymentMethod() {
        final PaymentMethod deactivated = Mockito.mock(PaymentMethod.class);
        Mockito.when(deactivated.identifier()).thenReturn("paymentMethod123");
        Mockito.when(deactivated.active()).thenReturn(Boolean.FALSE);

        final PaymentMethod original = Mockito.mock(PaymentMethod.class);
        Mockito.when(original.identifier()).thenReturn("paymentMethod123");
        Mockito.when(original.active()).thenReturn(Boolean.TRUE);
        Mockito.when(original.deactivate()).thenReturn(deactivated);

        final PaymentMethods ofWallet = Mockito.mock(PaymentMethods.class);
        Mockito.when(ofWallet.iterator()).thenReturn(
            List.of(original).iterator()
        );
        final Wallet stripe = Mockito.mock(Wallet.class);
        Mockito.when(stripe.type()).thenReturn("STRIPE");
        Mockito.when(stripe.paymentMethods()).thenReturn(ofWallet);

        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(stripe).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .deactivateStripePaymentMethod(
                    "mihai",
                    "test",
                    "paymentMethod123"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
    }

    /**
     * Removing an inactive PaymentMethod works.
     */
    @Test
    public void removeInactivePaymentMethod() {
        final PaymentMethod paymentMethod = Mockito.mock(PaymentMethod.class);
        final Wallet stripe = Mockito.mock(Wallet.class);
        final PaymentMethods ofWallet = Mockito.mock(PaymentMethods.class);

        Mockito.when(paymentMethod.identifier()).thenReturn("paymentMethod123");
        Mockito.when(paymentMethod.active()).thenReturn(Boolean.FALSE);
        Mockito.when(paymentMethod.remove()).thenReturn(Boolean.TRUE);
        Mockito.when(stripe.type()).thenReturn("STRIPE");
        Mockito.when(stripe.paymentMethods()).thenReturn(ofWallet);
        Mockito.when(ofWallet.iterator()).thenReturn(
            List.of(paymentMethod).iterator()
        );

        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(stripe).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .removeStripePaymentMethod(
                    "mihai",
                    "test",
                    "paymentMethod123"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.NO_CONTENT)
        );
    }

    /**
     * Removing an inactive PaymentMethod fails due to an unexpected
     * persistence error.
     */
    @Test
    public void removeInactivePaymentMethodFails() {
        final PaymentMethod paymentMethod = Mockito.mock(PaymentMethod.class);
        final Wallet stripe = Mockito.mock(Wallet.class);
        final PaymentMethods ofWallet = Mockito.mock(PaymentMethods.class);

        Mockito.when(paymentMethod.identifier()).thenReturn("paymentMethod123");
        Mockito.when(paymentMethod.active()).thenReturn(Boolean.FALSE);
        Mockito.when(paymentMethod.remove()).thenReturn(Boolean.FALSE);
        Mockito.when(stripe.type()).thenReturn("STRIPE");
        Mockito.when(stripe.paymentMethods()).thenReturn(ofWallet);
        Mockito.when(ofWallet.iterator()).thenReturn(
            List.of(paymentMethod).iterator()
        );
        Mockito.when(ofWallet.remove(paymentMethod))
            .thenReturn(Boolean.FALSE);

        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(stripe).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .removeStripePaymentMethod(
                    "mihai",
                    "test",
                    "paymentMethod123"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * Removing an inactive PaymentMethod fails due a not found exception.
     * (Project, Wallet etc...).
     */
    @Test
    public void removeInactivePaymentMethodFailsDueToNotFound() {
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);
        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenThrow(new IllegalStateException("Project not found"));
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .removeStripePaymentMethod(
                    "mihai",
                    "test",
                    "paymentMethod123"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * Removing an active PaymentMethod fails.
     */
    @Test
    public void removeFailsOnActivePaymentMethod() {
        final PaymentMethod paymentMethod = Mockito.mock(PaymentMethod.class);
        final Wallet stripe = Mockito.mock(Wallet.class);
        final PaymentMethods ofWallet = Mockito.mock(PaymentMethods.class);

        Mockito.when(paymentMethod.identifier()).thenReturn("paymentMethod123");
        Mockito.when(paymentMethod.active()).thenReturn(Boolean.TRUE);
        Mockito.when(stripe.type()).thenReturn("STRIPE");
        Mockito.when(stripe.paymentMethods()).thenReturn(ofWallet);
        Mockito.when(ofWallet.iterator()).thenReturn(
            List.of(paymentMethod).iterator()
        );

        final Wallets ofProject = Mockito.mock(Wallets.class);
        Mockito.when(ofProject.iterator()).thenReturn(
            List.of(stripe).iterator()
        );
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.wallets()).thenReturn(ofProject);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById("mihai/test", Provider.Names.GITHUB)
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        MatcherAssert.assertThat(
            new PaymentMethodsApi(user)
                .removeStripePaymentMethod(
                    "mihai",
                    "test",
                    "paymentMethod123"
                ).getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }
}
