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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link Repositories}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
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

}
