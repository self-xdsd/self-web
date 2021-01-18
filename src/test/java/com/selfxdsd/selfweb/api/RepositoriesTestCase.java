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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link Repositories}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ExecutableStatementCount (500 lines)
 */
public final class RepositoriesTestCase {

    /**
     * Organizations repos endpoint returns NO CONTENT when there are
     * no org repos found.
     */
    @Test
    public void returnsEmptyOrgReposArray() {
        final Organizations orgs = Mockito.mock(Organizations.class);
        Mockito.when(orgs.iterator()).thenReturn(
            new ArrayList<Organization>().iterator()
        );
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.organizations()).thenReturn(orgs);
        final User user = Mockito.mock(User.class);
        Mockito.when(user.provider()).thenReturn(provider);

        final Repositories reposApi = new Repositories(user);
        MatcherAssert.assertThat(
            reposApi.organizationRepos().getStatusCode(),
            Matchers.equalTo(HttpStatus.NO_CONTENT)
        );
    }

    /**
     * Organizations repos endpoint returns the found org repos.
     */
    @Test
    public void returnsOrgReposArray() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.json()).thenReturn(
            Json.createObjectBuilder().build()
        );
        final Repos repos = Mockito.mock(Repos.class);
        Mockito.when(repos.iterator()).thenReturn(List.of(repo).iterator());
        final Organization org = Mockito.mock(Organization.class);
        Mockito.when(org.repos()).thenReturn(repos);

        final Organizations orgs = Mockito.mock(Organizations.class);
        Mockito.when(orgs.iterator()).thenReturn(
            List.of(org).iterator()
        );
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.organizations()).thenReturn(orgs);
        final User user = Mockito.mock(User.class);
        Mockito.when(user.provider()).thenReturn(provider);

        final Repositories reposApi = new Repositories(user);
        final ResponseEntity<String> resp = reposApi.organizationRepos();
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
        MatcherAssert.assertThat(
            Json.createReader(new StringReader(resp.getBody())).readArray(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * The managedRepos endpoint returns NO CONTENT if the User has no
     * Projects.
     */
    @Test
    public void returnsNoContentManagedRepos() {
        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(owned.iterator()).thenReturn(
            new ArrayList<Project>().iterator()
        );
        final User user = Mockito.mock(User.class);
        Mockito.when(user.projects()).thenReturn(owned);
        MatcherAssert.assertThat(
            new Repositories(user).managedRepos().getStatusCode(),
            Matchers.equalTo(HttpStatus.NO_CONTENT)
        );
    }

    /**
     * It can return the user's manager repos.
     */
    @Test
    public void returnsManagedRepos() {
        final Project first = Mockito.mock(Project.class);
        Mockito.when(first.repoFullName()).thenReturn("mihai/test");
        Mockito.when(first.provider()).thenReturn("github");
        final Project second = Mockito.mock(Project.class);
        Mockito.when(second.repoFullName()).thenReturn("mihai/test2");
        Mockito.when(second.provider()).thenReturn("github");

        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(owned.iterator()).thenReturn(
            List.of(first, second).iterator()
        );
        final User user = Mockito.mock(User.class);
        Mockito.when(user.projects()).thenReturn(owned);
        final ResponseEntity<String> resp = new Repositories(user)
            .managedRepos();
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
        final JsonArray array = Json.createReader(
            new StringReader(resp.getBody())
        ).readArray();
        MatcherAssert.assertThat(
            array,
            Matchers.iterableWithSize(2)
        );
        final JsonObject firstJson = (JsonObject) array.get(0);
        MatcherAssert.assertThat(
            firstJson.getString("repoFullName"),
            Matchers.equalTo("mihai/test")
        );
        MatcherAssert.assertThat(
            firstJson.getString("provider"),
            Matchers.equalTo("github")
        );
        final JsonObject secondJson = (JsonObject) array.get(1);
        MatcherAssert.assertThat(
            secondJson.getString("repoFullName"),
            Matchers.equalTo("mihai/test2")
        );
        MatcherAssert.assertThat(
            secondJson.getString("provider"),
            Matchers.equalTo("github")
        );
    }

}
