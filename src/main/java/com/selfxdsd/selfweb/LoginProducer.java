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
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.Self;
import com.selfxdsd.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Produces the Login object.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #45:60min At the moment only the "amihaiemil" user has the admin role.
 *  Everyone who is an admin in the Github or GitLab self-xdsd Organization,
 *  should have the admin role.
 */
@Configuration
public class LoginProducer {

    /**
     * Self.
     */
    private final Self self;

    /**
     * Ctor.
     * @param self Self.
     */
    @Autowired
    public LoginProducer(final Self self){
        this.self = self;
    }

    /**
     * Authenticate and return the User.
     * @param clientService Spring OAuth2 client service.
     * @return Login.
     */
    @Bean
    @SessionScope
    public User login(
        final OAuth2AuthorizedClientService clientService
    ) {
        final OAuth2AuthenticationToken oauthToken =
            (OAuth2AuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();
        final String provider = oauthToken.getAuthorizedClientRegistrationId();
        final Login login;
        if (provider.equalsIgnoreCase(Provider.Names.GITHUB)) {
            login = new GithubLogin(oauthToken, clientService);
        } else if(provider.equalsIgnoreCase(Provider.Names.GITLAB)) {
            login = new GitlabLogin(oauthToken, clientService);
        } else{
            throw new UnsupportedOperationException("Unsupported provider "
                + provider +" for login.");
        }
        return this.self.login(login);
    }

    /**
     * Base class for Login implementations that are using
     * a OAuth2 authentication token.
     */
    private abstract static class OAuth2Login implements Login {

        /**
         * The OAuth2 token.
         */
        private final OAuth2AuthenticationToken oauthToken;

        /**
         * Service used to obtain the access token.
         */
        private final OAuth2AuthorizedClientService clientService;

        /**
         * Ctor.
         * @param oauthToken The OAuth2 token.
         * @param clientService Service used to obtain the access token.
         */
        OAuth2Login(final OAuth2AuthenticationToken oauthToken,
                    final OAuth2AuthorizedClientService clientService) {
            this.oauthToken = oauthToken;
            this.clientService = clientService;
        }

        @Override
        public String accessToken() {
            return this.clientService
                .loadAuthorizedClient(this.provider(),
                    this.oauthToken.getName())
                .getAccessToken()
                .getTokenValue();
        }

        @Override
        public String provider() {
            return this.oauthToken.getAuthorizedClientRegistrationId()
                .toLowerCase();
        }

        /**
         * The oauth token Principal.
         * <br/>
         * Should be used by implementations to extract oauth token attributes.
         * @return OAuth2User.
         */
        protected OAuth2User getPrincipal(){
            return this.oauthToken.getPrincipal();
        }

    }

    /**
     * Github Login implementation.
     */
    private static final class GithubLogin extends OAuth2Login{

        /**
         * Ctor.
         *
         * @param oauthToken The OAuth2 token.
         * @param clientService Service used to obtain the access token.
         */
        GithubLogin(final OAuth2AuthenticationToken oauthToken,
                    final OAuth2AuthorizedClientService clientService) {
            super(oauthToken, clientService);
        }

        @Override
        public String username() {
            return super.getPrincipal().getAttribute("login");
        }

        @Override
        public String email() {
            return super.getPrincipal().getAttribute("email");
        }

        @Override
        public String role() {
            final String role;
            if("amihaiemil".equals(this.username())) {
                role = "admin";
            } else {
                role = "user";
            }
            return role;
        }
    }

    /**
     * Gitlab Login implementation.
     */
    private static final class GitlabLogin extends OAuth2Login{

        /**
         * Ctor.
         *
         * @param oauthToken The OAuth2 token.
         * @param clientService Service used to obtain the access token.
         */
        GitlabLogin(final OAuth2AuthenticationToken oauthToken,
                    final OAuth2AuthorizedClientService clientService) {
            super(oauthToken, clientService);
        }

        @Override
        public String username() {
            return super.getPrincipal().getAttribute("username");
        }

        @Override
        public String email() {
            return super.getPrincipal().getAttribute("email");
        }

        @Override
        public String role() {
            final String role;
            if("amihaiemil".equals(this.username())) {
                role = "admin";
            } else {
                role = "user";
            }
            return role;
        }
    }
}
