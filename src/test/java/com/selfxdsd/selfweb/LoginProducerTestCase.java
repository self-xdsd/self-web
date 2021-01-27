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
package com.selfxdsd.selfweb;

import com.selfxdsd.api.Login;
import com.selfxdsd.api.Self;
import com.selfxdsd.api.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

/**
 * Unit tests for {@link LoginProducerTestCase}.
 * @author Nikita Monokov (nmonokov@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class LoginProducerTestCase {

    /**
     * Logins with Github provider.
     */
    @Test
    public void githubLogin() {
        final SecurityContext securityContext = Mockito.mock(
            SecurityContext.class
        );
        final OAuth2AuthenticationToken authentication = Mockito.mock(
            OAuth2AuthenticationToken.class
        );
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication())
                .thenReturn(authentication);
        final OAuth2AuthenticationToken oauthTokenMock =
            (OAuth2AuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();
        final Self core = Mockito.mock(Self.class);
        final User authenticated = Mockito.mock(User.class);
        Mockito.when(core.login(Mockito.any()))
                .thenReturn(authenticated);
        Mockito.when(oauthTokenMock.getAuthorizedClientRegistrationId())
                .thenReturn("github");
        final OAuth2AuthorizedClientService clientService =
                Mockito.mock(OAuth2AuthorizedClientService.class);

        LoginProducer producer = new LoginProducer(core);
        producer.login(clientService);

        MatcherAssert.assertThat(
            producer.login(clientService),
            Matchers.is(authenticated)
        );
        Mockito.verify(core, Mockito.times(2)).login(
            Mockito.any(Login.class)
        );
    }

    /**
     * Logins with Github provider.
     */
    @Test
    public void gitlabLogin() {
        final SecurityContext securityContext = Mockito.mock(
            SecurityContext.class
        );
        final OAuth2AuthenticationToken authentication = Mockito.mock(
            OAuth2AuthenticationToken.class
        );
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication())
            .thenReturn(authentication);
        final OAuth2AuthenticationToken oauthTokenMock =
            (OAuth2AuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();
        final Self core = Mockito.mock(Self.class);
        final User authenticated = Mockito.mock(User.class);
        Mockito.when(core.login(Mockito.any()))
                .thenReturn(authenticated);
        Mockito.when(oauthTokenMock.getAuthorizedClientRegistrationId())
                .thenReturn("gitlab");
        final OAuth2AuthorizedClientService clientService =
                Mockito.mock(OAuth2AuthorizedClientService.class);

        LoginProducer producer = new LoginProducer(core);
        producer.login(clientService);

        MatcherAssert.assertThat(
            producer.login(clientService),
            Matchers.is(authenticated)
        );
        Mockito.verify(core, Mockito.times(2)).login(
            Mockito.any(Login.class)
        );
    }

    /**
     * Failed to login due to unknown provider.
     */
    @Test
    public void failedUnsupportedLogin() {
        final SecurityContext securityContext = Mockito.mock(
            SecurityContext.class
        );
        final OAuth2AuthenticationToken authentication = Mockito.mock(
            OAuth2AuthenticationToken.class
        );
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication())
                .thenReturn(authentication);
        final OAuth2AuthenticationToken oauthTokenMock =
            (OAuth2AuthenticationToken) SecurityContextHolder.getContext()
            .getAuthentication();
        Mockito.when(oauthTokenMock.getAuthorizedClientRegistrationId())
            .thenReturn("bitbucket");
        final Self core = Mockito.mock(Self.class);
        final OAuth2AuthorizedClientService clientService =
            Mockito.mock(OAuth2AuthorizedClientService.class);

        LoginProducer producer = new LoginProducer(core);

        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> producer.login(clientService)
        );
        Mockito.verify(core, Mockito.times(0)).login(
                Mockito.any(Login.class)
        );
    }


}
