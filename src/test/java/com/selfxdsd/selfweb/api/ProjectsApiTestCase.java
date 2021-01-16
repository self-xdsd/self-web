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
import com.selfxdsd.selfweb.api.input.RepoInput;
import com.selfxdsd.selfweb.api.output.JsonProject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Unit tests for {@link ProjectsApi}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ExecutableStatementCount (1000 lines)
 */
public final class ProjectsApiTestCase {

    /**
     * ProjectsApi.project(...) returns an existing Project if it is owned
     * directly by the authenticated user (personal repo).
     */
    @Test
    public void fetchesOwnedProject() {
        final Project found = this.mockActiveProject(
            "mihai", "mihai", "test"
        );
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(all.getProjectById(
            "mihai/test", "github"
        )).thenReturn(found);
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.projects()).thenReturn(all);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(user.provider()).thenReturn(provider);

        final ProjectsApi api = new ProjectsApi(
            user,
            core
        );
        final ResponseEntity<String> resp = api.project("mihai", "test");
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.is(HttpStatus.OK)
        );
        final JsonObject json = Json.createReader(
            new StringReader(resp.getBody())
        ).readObject();

        MatcherAssert.assertThat(
            json,
            Matchers.equalTo(new JsonProject(found))
        );
        MatcherAssert.assertThat(
            json.getString("selfOwner"),
            Matchers.equalTo("mihai")
        );
    }

    /**
     * If the project is owned by an organization to which the
     * User has admin rights (but not owned by the User in Self),
     * it should be returned.
     */
    @Test
    public void fetchesOrgOwnedProject() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.fullName()).thenReturn("self/test");

        final Project found = this.mockActiveProject(
            "vlad", "self", "test"
        );
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(all.getProjectById(
            "self/test", "github"
        )).thenReturn(found);
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.projects()).thenReturn(all);

        final Organizations orgs = Mockito.mock(Organizations.class);
        final Organization self = Mockito.mock(Organization.class);
        Mockito.when(self.repos()).thenReturn(
            () -> Arrays.asList(repo).iterator()
        );
        Mockito.when(orgs.iterator())
            .thenReturn(Arrays.asList(self).iterator());

        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(provider.organizations()).thenReturn(orgs);
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(user.provider()).thenReturn(provider);

        final ProjectsApi api = new ProjectsApi(
            user,
            core
        );
        final ResponseEntity<String> resp = api.project("self", "test");
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.is(HttpStatus.OK)
        );
        final JsonObject json = Json.createReader(
            new StringReader(resp.getBody())
        ).readObject();

        MatcherAssert.assertThat(
            json,
            Matchers.equalTo(new JsonProject(found))
        );
        MatcherAssert.assertThat(
            json.getString("selfOwner"),
            Matchers.equalTo("vlad")
        );
    }

    /**
     * If the project exists, but it is owned by an Organization
     * to which the User has no admin rights, then 204 NO CONTENT
     * should be returned.
     *
     * In this test case, the repo is test, owned by the oracle organization,
     * while the owner in Self is vlad and the authenticated user is mihai,
     * who has no access to the the oracle organization, and therefore cannot
     * see the Project.
     */
    @Test
    public void fetchesOrgOwnedProjectNoContent() {
        final Project found = this.mockActiveProject(
            "vlad", "oracle", "test"
        );
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(all.getProjectById(
            "oracle/test", "github"
        )).thenReturn(found);
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.projects()).thenReturn(all);

        final Organizations orgs = Mockito.mock(Organizations.class);
        final Organization org = Mockito.mock(Organization.class);
        Mockito.when(org.repos()).thenReturn(
            () -> new ArrayList<Repo>().iterator()
        );
        Mockito.when(orgs.iterator())
            .thenReturn(Arrays.asList(org).iterator());

        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.organizations()).thenReturn(orgs);
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(user.provider()).thenReturn(provider);

        final ProjectsApi api = new ProjectsApi(
            user,
            core
        );
        final ResponseEntity<String> resp = api.project("oracle", "test");
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.is(HttpStatus.NO_CONTENT)
        );
    }

    /**
     * ProjectsApi.project(...) returns NO CONTENT if the project
     * does not exist.
     */
    @Test
    public void fetchMissingProject() {
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(all.getProjectById(
            "mihai/test", "github"
        )).thenReturn(null);
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.projects()).thenReturn(all);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(user.provider()).thenReturn(provider);
        final ProjectsApi api = new ProjectsApi(
            user,
            core
        );
        final ResponseEntity<String> resp = api.project("mihai", "test");
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.is(HttpStatus.NO_CONTENT)
        );
    }

    /**
     * ProjectApi.activate(...) can activate a user-owned repo.
     */
    @Test
    public void activatesUserRepo() {
        final Project activated = this.mockActiveProject(
            "mihai", "mihai", "test"
        );

        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.activate()).thenReturn(activated);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.repo("mihai", "test")).thenReturn(repo);
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(user.provider()).thenReturn(provider);

        final ProjectsApi projects = new ProjectsApi(
            user, Mockito.mock(Self.class)
        );
        final RepoInput input = new RepoInput();
        input.setOwner("mihai");
        input.setName("test");

        final ResponseEntity<String> resp = projects.activate(input);
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.is(HttpStatus.CREATED)
        );
        final JsonObject json = Json.createReader(
            new StringReader(resp.getBody())
        ).readObject();
        MatcherAssert.assertThat(
            json,
            Matchers.equalTo(new JsonProject(activated))
        );
        MatcherAssert.assertThat(
            json.getString("selfOwner"),
            Matchers.equalTo("mihai")
        );
    }

    /**
     * ProjectApi.activate(...) returns 412 PRECONDITION FAILED if the
     * repo is not found.
     */
    @Test
    public void activatesOrgRepo() {
        final Project activated = this.mockActiveProject(
            "mihai", "self", "test"
        );

        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.fullName()).thenReturn("self/test");
        Mockito.when(repo.activate()).thenReturn(activated);

        final Organizations orgs = Mockito.mock(Organizations.class);
        final Organization self = Mockito.mock(Organization.class);
        Mockito.when(self.repos()).thenReturn(
            () -> Arrays.asList(repo).iterator()
        );
        Mockito.when(orgs.iterator())
            .thenReturn(Arrays.asList(self).iterator());

        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.organizations()).thenReturn(orgs);
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(user.provider()).thenReturn(provider);

        final ProjectsApi projects = new ProjectsApi(
            user, Mockito.mock(Self.class)
        );
        final RepoInput input = new RepoInput();
        input.setOwner("self");
        input.setName("test");

        final ResponseEntity<String> resp = projects.activate(input);
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.is(HttpStatus.CREATED)
        );
        final JsonObject json = Json.createReader(
            new StringReader(resp.getBody())
        ).readObject();
        MatcherAssert.assertThat(
            json,
            Matchers.equalTo(new JsonProject(activated))
        );
        MatcherAssert.assertThat(
            json.getString("selfOwner"),
            Matchers.equalTo("mihai")
        );
    }

    /**
     * ProjectApi.activate(...) returns 412 PRECONDITION FAILED if the
     * repo is not found.
     */
    @Test
    public void activatesNotFound() {
        final Organizations orgs = Mockito.mock(Organizations.class);
        Mockito.when(orgs.iterator())
            .thenReturn(new ArrayList<Organization>().iterator());
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.organizations()).thenReturn(orgs);
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(user.provider()).thenReturn(provider);

        final ProjectsApi projects = new ProjectsApi(
            user, Mockito.mock(Self.class)
        );
        final RepoInput input = new RepoInput();
        input.setOwner("self");
        input.setName("notfound");

        final ResponseEntity<String> resp = projects.activate(input);
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.is(HttpStatus.PRECONDITION_FAILED)
        );
    }

    /**
     * ProjectApi.contractsCount(...) returns the Project's number of
     * contracts.
     */
    @Test
    public void returnsProjectContractsCount() {
        final Project project = Mockito.mock(Project.class);
        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(contracts.count()).thenReturn(5);
        Mockito.when(project.contracts()).thenReturn(contracts);

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);
        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById(
            "mihai/test", Provider.Names.GITHUB
            )
        ).thenReturn(project);
        Mockito.when(user.projects()).thenReturn(owned);

        final ResponseEntity<String> resp = new ProjectsApi(
            user, Mockito.mock(Self.class)
        ).contractsCount("mihai", "test");

        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
        MatcherAssert.assertThat(
            Json.createReader(
                new StringReader(resp.getBody())
            ).readObject().getInt("contractsCount"),
            Matchers.equalTo(5)
        );
    }

    /**
     * ProjectApi.contractsCount(...) returns BAD REQUEST if the Project
     * is missing.
     */
    @Test
    public void contractsCountProjectMissing() {
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(user.provider()).thenReturn(provider);
        final Projects owned = Mockito.mock(Projects.class);
        Mockito.when(
            owned.getProjectById(
                "mihai/test", Provider.Names.GITHUB
            )
        ).thenReturn(null);
        Mockito.when(user.projects()).thenReturn(owned);

        final ResponseEntity<String> resp = new ProjectsApi(
            user, Mockito.mock(Self.class)
        ).contractsCount("mihai", "test");

        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * Mock an activated project.
     * @param selfOwner Owner username in Self.
     * @param repoOwner Owner username from the provider
     *  (can also be an org name).
     * @param name Repo simple name.
     * @return Project.
     */
    private Project mockActiveProject(
        final String selfOwner,
        final String repoOwner,
        final String name
    ) {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName())
            .thenReturn(repoOwner + "/" + name);
        Mockito.when(project.provider()).thenReturn("github");

        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.id()).thenReturn(1);
        Mockito.when(manager.userId()).thenReturn("123");
        Mockito.when(manager.username()).thenReturn("zoeself");
        Mockito.when(manager.percentage()).thenReturn(6.5);
        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn("github");
        Mockito.when(manager.provider()).thenReturn(prov);

        Mockito.when(project.projectManager()).thenReturn(manager);

        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn(selfOwner);
        Mockito.when(project.owner()).thenReturn(user);

        final Wallet wallet = Mockito.mock(Wallet.class);
        Mockito.when(wallet.available()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(wallet.cash()).thenReturn(BigDecimal.valueOf(1200));
        Mockito.when(wallet.debt()).thenReturn(BigDecimal.valueOf(200));
        Mockito.when(wallet.type()).thenReturn("fake");
        final PaymentMethods methods = Mockito.mock(PaymentMethods.class);
        Mockito.when(methods.spliterator())
            .thenReturn(new ArrayList<PaymentMethod>().spliterator());
        Mockito.when(wallet.paymentMethods()).thenReturn(methods);
        Mockito.when(project.wallet()).thenReturn(wallet);

        return project;
    }

}
