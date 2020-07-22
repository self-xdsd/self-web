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
package com.selfxdsd.selfweb;

import com.selfxdsd.api.Login;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Produces the Login object.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #25:30min Make sure to extract the username and email from the
 *  principal's attributes, according to the provider (Github, Gitlab etc).
 *  At the moment, it assumes only Github as provider.
 */
@Configuration
public class LoginProducer {

    /**
     * Produce the Login.
     * @param clientService Spring OAuth2 client service.
     * @return Login.
     */
    @Bean
    @SessionScope
    public Login login(
        final OAuth2AuthorizedClientService clientService
    ) {
        return new Login() {
            private final OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) SecurityContextHolder
                    .getContext()
                    .getAuthentication();
            @Override
            public String username() {
                return this.oauthToken.getPrincipal().getAttribute("login");
            }

            @Override
            public String email() {
                return this.oauthToken.getPrincipal().getAttribute("email");
            }

            @Override
            public String accessToken() {
                return clientService.loadAuthorizedClient(
                    this.provider(), this.oauthToken.getName()
                ).getAccessToken().getTokenValue();
            }

            @Override
            public String provider() {
                return this.oauthToken.getAuthorizedClientRegistrationId();
            }
        };
    }
}
