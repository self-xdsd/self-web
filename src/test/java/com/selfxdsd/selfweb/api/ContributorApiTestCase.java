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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.json.Json;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link ContributorApi}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ContributorApiTestCase {

    /**
     * If the authenticated User is not a contributor,
     * it should return 204 NO CONTENT.
     */
    @Test
    public void returnsNoContentOnMissingContributor() {
        final User authenticated = Mockito.mock(User.class);
        Mockito.when(authenticated.asContributor()).thenReturn(null);
        final ContributorApi api = new ContributorApi(authenticated);
        MatcherAssert.assertThat(
            api.contributor().getStatusCode(),
            Matchers.equalTo(HttpStatus.NO_CONTENT)
        );
    }

    /**
     * If the authenticated User is a contributor,
     * it should return 200 OK.
     */
    @Test
    public void returnsOkOnFoundContributor() {
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.username()).thenReturn("mihai");
        Mockito.when(contributor.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(contributor.contracts()).thenReturn(
            new Contracts.Empty()
        );

        final User authenticated = Mockito.mock(User.class);
        Mockito.when(authenticated.asContributor()).thenReturn(
            contributor
        );
        final ContributorApi api = new ContributorApi(authenticated);
        final ResponseEntity<String> resp = api.contributor();
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
        MatcherAssert.assertThat(
            Json.createReader(
                new StringReader(resp.getBody())
            ).readObject(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add("username", "mihai")
                    .add("provider", Provider.Names.GITHUB)
                    .add("contracts", Json.createArrayBuilder())
                    .build()
            )
        );
    }

    /**
     * If the authenticated User is not a contributor,
     * the tasks() method should return 204 NO CONTENT.
     */
    @Test
    public void tasksReturnsNoContentOnMissingContributor() {
        final User authenticated = Mockito.mock(User.class);
        Mockito.when(authenticated.asContributor()).thenReturn(null);
        final ContributorApi api = new ContributorApi(authenticated);
        MatcherAssert.assertThat(
            api.tasks(
                "amihaiemil",
                "docker-java-api",
                Contract.Roles.DEV
            ).getStatusCode(),
            Matchers.equalTo(HttpStatus.NO_CONTENT)
        );
    }

    /**
     * If the authenticated User is a Contributor but does not have
     * the specified Contract, the tasks() method
     * should return 400 BAD REQUEST.
     */
    @Test
    public void tasksReturnsBadRequestOnMissingContract() {
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(
            contributor.contract(
                "amihaiemil/docker-java-api",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            )
        ).thenReturn(null);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);

        final User authenticated = Mockito.mock(User.class);
        Mockito.when(authenticated.asContributor()).thenReturn(contributor);
        Mockito.when(authenticated.provider()).thenReturn(provider);

        final ContributorApi api = new ContributorApi(authenticated);
        MatcherAssert.assertThat(
            api.tasks(
                "amihaiemil",
                "docker-java-api",
                Contract.Roles.DEV
            ).getStatusCode(),
            Matchers.equalTo(HttpStatus.BAD_REQUEST)
        );
    }

    /**
     * An empty JsonArray is returne if the Contract has no Tasks
     * assigned.
     */
    @Test
    public void tasksReturnsEmptyArray() {
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.spliterator()
        ).thenReturn(new ArrayList<Task>().spliterator());
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.tasks()).thenReturn(tasks);
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(
            contributor.contract(
                "amihaiemil/docker-java-api",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            )
        ).thenReturn(contract);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);

        final User authenticated = Mockito.mock(User.class);
        Mockito.when(authenticated.asContributor()).thenReturn(contributor);
        Mockito.when(authenticated.provider()).thenReturn(provider);

        final ContributorApi api = new ContributorApi(authenticated);
        final ResponseEntity<String> resp = api.tasks(
            "amihaiemil",
            "docker-java-api",
            Contract.Roles.DEV
        );
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
        MatcherAssert.assertThat(
            resp.getBody(),
            Matchers.equalTo("[]")
        );
    }

    /**
     * A Contributor's tasks from a certain Contract can be returned
     * as JsonArray.
     */
    @Test
    public void tasksReturnsArray() {
        final List<Task> list = new ArrayList<>();
        list.add(
            this.mockTask("1", LocalDateTime.now(), 30)
        );
        list.add(
            this.mockTask("2", LocalDateTime.now(), 60)
        );
        list.add(
            this.mockTask("3", LocalDateTime.now(), 90)
        );
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.spliterator()
        ).thenReturn(list.spliterator());
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.tasks()).thenReturn(tasks);
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(
            contributor.contract(
                "amihaiemil/docker-java-api",
                Provider.Names.GITHUB,
                Contract.Roles.DEV
            )
        ).thenReturn(contract);
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn(Provider.Names.GITHUB);

        final User authenticated = Mockito.mock(User.class);
        Mockito.when(authenticated.asContributor()).thenReturn(contributor);
        Mockito.when(authenticated.provider()).thenReturn(provider);

        final ContributorApi api = new ContributorApi(authenticated);
        final ResponseEntity<String> resp = api.tasks(
            "amihaiemil",
            "docker-java-api",
            Contract.Roles.DEV
        );
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
        MatcherAssert.assertThat(
            Json.createReader(
                new StringReader(resp.getBody())
            ).readArray(),
            Matchers.iterableWithSize(3)
        );
    }

    /**
     * Mock a Task for test.
     * @param issueId Issue Id.
     * @param assigned Assignment datetime.
     * @param estimation Estimation in minutes.
     * @return Mocked Task.
     */
    public Task mockTask(
        final String issueId,
        final LocalDateTime assigned,
        final int estimation
    ) {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.issueId()).thenReturn(issueId);
        Mockito.when(task.assignmentDate()).thenReturn(assigned);
        Mockito.when(task.deadline()).thenReturn(assigned.plusDays(10));
        Mockito.when(task.estimation()).thenReturn(estimation);
        Mockito.when(task.value()).thenReturn(BigDecimal.TEN);
        return task;
    }
}
