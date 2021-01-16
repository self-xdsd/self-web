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

import com.selfxdsd.api.*;
import com.selfxdsd.selfweb.api.input.PmInput;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.json.Json;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

/**
 * Unit tests for {@link ProjectManagersApi}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ProjectManagersApiTestCase {

    /**
     * GET /managers works for admin users.
     */
    @Test
    public void getManagersWorksForAdmin() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("admin");
        final Self core = Mockito.mock(Self.class);
        final ProjectManagers all = Mockito.mock(ProjectManagers.class);
        final Iterator<ProjectManager> iterator = List.of(
            this.mockManager(1),
            this.mockManager(2),
            this.mockManager(3)
        ).iterator();
        Mockito.when(all.iterator()).thenReturn(iterator);
        Mockito.when(core.projectManagers()).thenReturn(all);

        final ResponseEntity<String> managers = new ProjectManagersApi(
            user, core
        ).managers();
        MatcherAssert.assertThat(
            managers.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
        MatcherAssert.assertThat(
            Json.createReader(
                new StringReader(managers.getBody())
            ).readArray(),
            Matchers.iterableWithSize(3)
        );
    }

    /**
     * GET /managers is forbidden for non-admin users.
     */
    @Test
    public void getManagersForbiddenToNonAdmin() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("user");
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.projectManagers()).thenThrow(
            new IllegalStateException("Should not be called.")
        );
        MatcherAssert.assertThat(
            new ProjectManagersApi(user, core).managers().getStatusCode(),
            Matchers.equalTo(HttpStatus.FORBIDDEN)
        );
    }

    /**
     * POST /managers/new is works for admin users.
     */
    @Test
    public void registerManagerWorksForAdmin() {
        final ProjectManager registered = this.mockManager(1);

        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("admin");
        final Self core = Mockito.mock(Self.class);
        final ProjectManagers all = Mockito.mock(ProjectManagers.class);
        Mockito.when(
            all.register(
                "123",
                "zoe",
                "github",
                "token123",
                8.0
            )
        ).thenReturn(registered);
        Mockito.when(core.projectManagers()).thenReturn(all);

        final PmInput input = new PmInput();
        input.setUserId("123");
        input.setUsername("zoe");
        input.setProvider("github");
        input.setToken("token123");
        input.setCommission(8.0);

        final ResponseEntity<String> resp = new ProjectManagersApi(user, core)
            .register(input);

        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
        MatcherAssert.assertThat(
            Json.createReader(
                new StringReader(resp.getBody())
            ).readObject().getInt("id"),
            Matchers.equalTo(1)
        );
    }

    /**
     * POST /managers/new is forbidden for non-admin users.
     */
    @Test
    public void registerManagerForbiddenToNonAdmin() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("user");
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.projectManagers()).thenThrow(
            new IllegalStateException("Should not be called.")
        );
        MatcherAssert.assertThat(
            new ProjectManagersApi(user, core)
                .register(new PmInput())
                .getStatusCode(),
            Matchers.equalTo(HttpStatus.FORBIDDEN)
        );
    }

    /**
     * Mock a PM for test.
     * @param id Integer id.
     * @return ProjectManager.
     */
    private ProjectManager mockManager(final int id) {
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.id()).thenReturn(id);
        Mockito.when(manager.userId()).thenReturn("123");
        Mockito.when(manager.username()).thenReturn("zoe");
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(manager.provider()).thenReturn(provider);
        Mockito.when(manager.percentage()).thenReturn(8.0);
        return manager;
    }
}
