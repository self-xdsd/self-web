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

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;

/**
 * Unit tests for {@link UserController}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class UserControllerTestCase {

    /**
     * UserController models a Github user.
     */
    @Test
    public void modelsGithubUser() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(user.email()).thenReturn("test@gmail.com");
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Model model = Mockito.mock(Model.class);
        Mockito.when(
            model.addAttribute(
                Mockito.anyString(),
                Mockito.anyString()
            )
        ).thenReturn(model);

        final UserController controller = new UserController(user);
        MatcherAssert.assertThat(
            controller.index(model),
            Matchers.equalTo("user.html")
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "username", "@mihai"
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "email", "test@gmail.com"
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "provider", "Github"
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "providerProfile", "https://github.com/mihai"
        );
    }

    /**
     * UserController models a GitLab user.
     */
    @Test
    public void modelsGitlabUser() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(user.email()).thenReturn("");
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITLAB);
        Mockito.when(user.provider()).thenReturn(provider);

        final Model model = Mockito.mock(Model.class);
        Mockito.when(
            model.addAttribute(
                Mockito.anyString(),
                Mockito.anyString()
            )
        ).thenReturn(model);

        final UserController controller = new UserController(user);
        MatcherAssert.assertThat(
            controller.index(model),
            Matchers.equalTo("user.html")
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "username", "@mihai"
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "email", "-"
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "provider", "Gitlab"
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "providerProfile", "https://gitlab.com/mihai"
        );
    }

    /**
     * UserController models a user from an unknown provider.
     */
    @Test
    public void modelsOtherUser() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(user.email()).thenReturn("");
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn("Unknown");
        Mockito.when(user.provider()).thenReturn(provider);

        final Model model = Mockito.mock(Model.class);
        Mockito.when(
            model.addAttribute(
                Mockito.anyString(),
                Mockito.anyString()
            )
        ).thenReturn(model);

        final UserController controller = new UserController(user);
        MatcherAssert.assertThat(
            controller.index(model),
            Matchers.equalTo("user.html")
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "username", "@mihai"
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "email", "-"
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "provider", "Unknown"
        );
        Mockito.verify(model, Mockito.times(1)).addAttribute(
            "providerProfile", "#"
        );
    }

}
