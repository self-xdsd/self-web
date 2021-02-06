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

import com.selfxdsd.api.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link SelfCoreComponent}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfCoreComponentTestCase {

    /**
     * It delegates the login to the encapsulated core.
     */
    @Test
    public void delegatesLogin() {
        final User authenticated = Mockito.mock(User.class);
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.login(Mockito.any()))
            .thenReturn(authenticated);

        final SelfCoreComponent component = new SelfCoreComponent(core);

        MatcherAssert.assertThat(
            component.login(Mockito.mock(Login.class)),
            Matchers.is(authenticated)
        );
        Mockito.verify(core, Mockito.times(1)).login(
            Mockito.any(Login.class)
        );
    }

    /**
     * It delegates the project managers to the encapsulated core.
     */
    @Test
    public void delegatesProjectManagers() {
        final ProjectManagers pms = Mockito.mock(ProjectManagers.class);
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.projectManagers()).thenReturn(pms);

        final SelfCoreComponent component = new SelfCoreComponent(core);

        MatcherAssert.assertThat(
            component.projectManagers(),
            Matchers.is(pms)
        );
        Mockito.verify(core, Mockito.times(1)).projectManagers();
    }

    /**
     * It delegates the projects to the encapsulated core.
     */
    @Test
    public void delegatesProjects() {
        final Projects projects = Mockito.mock(Projects.class);
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.projects()).thenReturn(projects);

        final SelfCoreComponent component = new SelfCoreComponent(core);

        MatcherAssert.assertThat(
            component.projects(),
            Matchers.is(projects)
        );
        Mockito.verify(core, Mockito.times(1)).projects();
    }

    /**
     * It delegates the contributors to the encapsulated core.
     */
    @Test
    public void delegatesContributors() {
        final Contributors contributors = Mockito.mock(Contributors.class);
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.contributors()).thenReturn(contributors);

        final SelfCoreComponent component = new SelfCoreComponent(core);

        MatcherAssert.assertThat(
            component.contributors(),
            Matchers.is(contributors)
        );
        Mockito.verify(core, Mockito.times(1)).contributors();
    }

    /**
     * It delegates the token authentication to the encapsulated core.
     */
    @Test
    public void delegatesAuthenticate() {
        final User user = Mockito.mock(User.class);
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.authenticate("token")).thenReturn(user);

        final SelfCoreComponent component = new SelfCoreComponent(core);

        MatcherAssert.assertThat(
            component.authenticate("token"),
            Matchers.is(user)
        );
        Mockito.verify(core, Mockito.times(1)).authenticate("token");
    }

    /**
     * It delegates the closing to the encapsulated core.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void delegatesClose() throws Exception {
        final Self core = Mockito.mock(Self.class);
        final SelfCoreComponent component = new SelfCoreComponent(core);

        component.close();

        Mockito.verify(core, Mockito.times(1)).close();
    }

}
