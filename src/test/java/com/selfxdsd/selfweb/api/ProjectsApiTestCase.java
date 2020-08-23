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
import com.selfxdsd.selfweb.api.input.RepoInput;
import com.selfxdsd.selfweb.api.output.JsonProject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.json.Json;
import java.io.StringReader;
import java.math.BigDecimal;

/**
 * Unit tests for {@link ProjectsApi}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ProjectsApiTestCase {

    /**
     * ProjectApi.activate(...) can activate a user-owned repo.
     */
    @Test
    public void activatesUserRepo() {
        final Project activated = this.mockActiveProject("mihai", "test");
        
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.activate()).thenReturn(activated);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.repo("mihai", "test")).thenReturn(repo);
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(user.provider()).thenReturn(provider);

        final ProjectsApi projects = new ProjectsApi(user);
        final RepoInput input = new RepoInput();
        input.setOwner("mihai");
        input.setName("test");

        final ResponseEntity<String> resp = projects.activate(input);
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.is(HttpStatus.CREATED)
        );
        MatcherAssert.assertThat(
            Json.createReader(new StringReader(resp.getBody())).readObject(),
            Matchers.equalTo(new JsonProject(activated))
        );
    }

    /**
     * Mock an activated project.
     * @param owner Owner username.
     * @param name Repo simple name.
     * @return Project.
     */
    private Project mockActiveProject(final String owner, final String name) {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName())
            .thenReturn(owner + "/" + name);
        Mockito.when(project.provider()).thenReturn("github");

        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.id()).thenReturn(1);
        Mockito.when(manager.userId()).thenReturn("123");
        Mockito.when(manager.username()).thenReturn("zoeself");
        Mockito.when(manager.commission()).thenReturn(BigDecimal.valueOf(25));
        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn("github");
        Mockito.when(manager.provider()).thenReturn(prov);

        Mockito.when(project.projectManager()).thenReturn(manager);

        return project;
    }

}
