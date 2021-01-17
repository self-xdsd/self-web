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

import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.PayoutMethod;
import com.selfxdsd.api.PayoutMethods;
import com.selfxdsd.api.User;
import com.selfxdsd.selfweb.api.input.BillingInfoInput;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import javax.json.Json;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link PayoutMethodsApi}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class PayoutMethodsApiTestCase {

    /**
     * If the User is not a Contributor, then the GET PayoutMethods
     * endpoint should return BAD REQUEST.
     */
    @Test
    public void getPayoutMethodsNotContributor() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.asContributor()).thenReturn(null);
        MatcherAssert.assertThat(
            new PayoutMethodsApi(user).payoutMethods().getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * It can return a Contributor's PayoutMethods.
     */
    @Test
    public void getPayoutMethods() {
        final PayoutMethods ofContributor = Mockito.mock(PayoutMethods.class);
        Mockito.when(ofContributor.spliterator()).thenReturn(
            new ArrayList<PayoutMethod>().spliterator()
        );
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.payoutMethods()).thenReturn(ofContributor);
        final User user = Mockito.mock(User.class);
        Mockito.when(user.asContributor()).thenReturn(contributor);
        MatcherAssert.assertThat(
            new PayoutMethodsApi(user).payoutMethods().getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
    }

    /**
     * If the User is not a Contributor, then the createStripeAccount
     * endpoint should return BAD REQUEST.
     */
    @Test
    public void createStripeAccountNotContributor() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.asContributor()).thenReturn(null);
        MatcherAssert.assertThat(
            new PayoutMethodsApi(user)
                .createStripeConnectAccount(new BillingInfoInput())
                .getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * It returns BAD REQUEST because Contributor.createStripeAccount(...)
     * throws IllegalStateException.
     */
    @Test
    public void createStripeAccountThrowsIse() {
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.createStripeAccount(Mockito.any()))
            .thenThrow(new IllegalStateException("Can't create SCA!"));
        final User user = Mockito.mock(User.class);
        Mockito.when(user.asContributor()).thenReturn(contributor);
        MatcherAssert.assertThat(
            new PayoutMethodsApi(user)
                .createStripeConnectAccount(new BillingInfoInput())
                .getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * If the User is not a Contributor, then the stripeConnectOnboardingLink
     * endpoint should return BAD REQUEST.
     */
    @Test
    public void createStripeOnboardingLinkNotContributor() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.asContributor()).thenReturn(null);
        MatcherAssert.assertThat(
            new PayoutMethodsApi(user)
                .stripeConnectOnboardingLink()
                .getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * If the Contributor has no SCA (Stripe PayoutMethod), then the
     * stripeConnectOnboardingLink endpoint should return BAD REQUEST.
     */
    @Test
    public void createStripeOnboardingLinkNoStripeAccount() {
        final PayoutMethods ofContributor = Mockito.mock(PayoutMethods.class);
        final PayoutMethod fake = Mockito.mock(PayoutMethod.class);
        Mockito.when(fake.type()).thenReturn("FAKE");
        Mockito.when(ofContributor.iterator()).thenReturn(
            List.of(fake).iterator()
        );
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.payoutMethods()).thenReturn(ofContributor);

        final User user = Mockito.mock(User.class);
        Mockito.when(user.asContributor()).thenReturn(contributor);
        MatcherAssert.assertThat(
            new PayoutMethodsApi(user)
                .stripeConnectOnboardingLink()
                .getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * If the details are already submitted to Stripe, then the
     * stripeConnectOnboardingLink endpoint should return BAD REQUEST.
     */
    @Test
    public void createStripeOnboardingLinkDetailsSubmitted() {
        final PayoutMethods ofContributor = Mockito.mock(PayoutMethods.class);
        final PayoutMethod stripe = Mockito.mock(PayoutMethod.class);
        Mockito.when(stripe.type()).thenReturn("STRIPE");
        Mockito.when(stripe.json()).thenReturn(
            Json.createObjectBuilder()
                .add("details_submitted", true)
                .build()
        );

        Mockito.when(ofContributor.iterator()).thenReturn(
            List.of(stripe).iterator()
        );
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.payoutMethods()).thenReturn(ofContributor);

        final User user = Mockito.mock(User.class);
        Mockito.when(user.asContributor()).thenReturn(contributor);
        MatcherAssert.assertThat(
            new PayoutMethodsApi(user)
                .stripeConnectOnboardingLink()
                .getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * If the User is not a Contributor, then the stripeConnectLoginLink
     * endpoint should return BAD REQUEST.
     */
    @Test
    public void createStripeLoginLinkNotContributor() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.asContributor()).thenReturn(null);
        MatcherAssert.assertThat(
            new PayoutMethodsApi(user)
                .stripeConnectLoginLink()
                .getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * If the Contributor has no SCA (Stripe PayoutMethod), then the
     * stripeConnectLoginLink endpoint should return BAD REQUEST.
     */
    @Test
    public void createStripeLoginLinkNoStripeAccount() {
        final PayoutMethods ofContributor = Mockito.mock(PayoutMethods.class);
        final PayoutMethod fake = Mockito.mock(PayoutMethod.class);
        Mockito.when(fake.type()).thenReturn("FAKE");
        Mockito.when(ofContributor.iterator()).thenReturn(
            List.of(fake).iterator()
        );
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.payoutMethods()).thenReturn(ofContributor);

        final User user = Mockito.mock(User.class);
        Mockito.when(user.asContributor()).thenReturn(contributor);
        MatcherAssert.assertThat(
            new PayoutMethodsApi(user)
                .stripeConnectLoginLink()
                .getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * If there are still details to submit Stripe, then the
     * stripeConnectOnboardingLink endpoint should return BAD REQUEST.
     */
    @Test
    public void createStripeLoginLinkDetailsSubmitted() {
        final PayoutMethods ofContributor = Mockito.mock(PayoutMethods.class);
        final PayoutMethod stripe = Mockito.mock(PayoutMethod.class);
        Mockito.when(stripe.type()).thenReturn("STRIPE");
        Mockito.when(stripe.json()).thenReturn(
            Json.createObjectBuilder()
                .add("details_submitted", false)
                .build()
        );

        Mockito.when(ofContributor.iterator()).thenReturn(
            List.of(stripe).iterator()
        );
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.payoutMethods()).thenReturn(ofContributor);

        final User user = Mockito.mock(User.class);
        Mockito.when(user.asContributor()).thenReturn(contributor);
        MatcherAssert.assertThat(
            new PayoutMethodsApi(user)
                .stripeConnectLoginLink()
                .getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

}
