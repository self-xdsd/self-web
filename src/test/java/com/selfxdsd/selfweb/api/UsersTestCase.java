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

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for {@link Users}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class UsersTestCase {

    /**
     * Users.user(...) returns the user's attributes.
     */
    @Test
    public void returnsSelfMap() {
        final User self = Mockito.mock(User.class);
        Mockito.when(self.role()).thenReturn("user");
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(self.provider()).thenReturn(provider);
        final OAuth2User principal = Mockito.mock(OAuth2User.class);
        final Map<String, Object> principalAttr = new HashMap<>();
        principalAttr.put("principal", "from_spring");
        Mockito.when(principal.getAttributes()).thenReturn(principalAttr);

        final Users users = new Users(self);
        final Map<String, Object> attributes = users.user(principal);
        MatcherAssert.assertThat(
            attributes.entrySet(),
            Matchers.iterableWithSize(3)
        );
        MatcherAssert.assertThat(
            attributes.get("provider"),
            Matchers.equalTo(Provider.Names.GITHUB)
        );
        MatcherAssert.assertThat(
            attributes.get("role"),
            Matchers.equalTo("user")
        );
        MatcherAssert.assertThat(
            attributes.get("principal"),
            Matchers.equalTo("from_spring")
        );
    }

}
