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
import com.selfxdsd.selfweb.api.input.ContractInput;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonValue;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Unit tests for {@link ContractsApi}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ExecutableStatementCount (1000 lines)
 */
public final class ContractsApiTestCase {

    /**
     * ContractsApi.contracts(...) returns Project Contracts
     * if Project is owned directly by the authenticated user (personal repo).
     */
    @Test
    public void fetchesOwnedProjectContracts(){
        final Project project = this.mockActiveProject(
            "mihai", "mihai", "test"
        );
        final Projects projects = Mockito.mock(Projects.class);
        Mockito.when(projects.getProjectById(
            "mihai/test", "github"
        )).thenReturn(project);

        final Contracts contracts = this.mockContracts(
            this.mockContract(
                new Contract.Id("mihai/test",
                    "vlad", "github", "DEV"),
                project,
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(10000)
            )
        );
        Mockito.when(project.contracts()).thenReturn(contracts);

        final User user = Mockito.mock(User.class);

        Mockito.when(user.username()).thenReturn("mihai");
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(user.provider()).thenReturn(provider);
        Mockito.when(user.projects()).thenReturn(projects);

        final ContractsApi api = new ContractsApi(user);

        ResponseEntity<String> resp = api.contracts("mihai", "test");
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.is(HttpStatus.OK)
        );
        final JsonArray json = Json.createReader(
            new StringReader(Objects.requireNonNull(resp.getBody()))
        ).readArray();
        MatcherAssert.assertThat(
            json,
            Matchers.equalTo(Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                    .add("id", Json.createObjectBuilder()
                        .add("repoFullName", "mihai/test")
                        .add("contributorUsername", "vlad")
                        .add("provider", "github")
                        .add("role", "DEV")
                        .build())
                    .add(
                        "hourlyRate",
                        NumberFormat
                            .getCurrencyInstance(Locale.GERMANY)
                            .format(5)
                    ).add(
                        "value",
                        NumberFormat
                            .getCurrencyInstance(Locale.GERMANY)
                            .format(100)
                    ).add(
                        "revenue",
                        NumberFormat
                            .getCurrencyInstance(Locale.GERMANY)
                            .format(90)
                    )
                    .add("markedForRemoval", "null")
                    .build())
                .build())
        );

    }

    /**
     * Returns an empty json array if project not found.
     */
    @Test
    public void fetchesEmptyContractsIfProjectNotFound(){
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("mihai");
        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(user.provider()).thenReturn(provider);
        Mockito.when(user.projects()).thenReturn(Mockito.mock(Projects.class));

        final ContractsApi api = new ContractsApi(user);

        ResponseEntity<String> resp = api.contracts("mihai", "test");
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.is(HttpStatus.OK)
        );
        final JsonArray json = Json.createReader(
            new StringReader(Objects.requireNonNull(resp.getBody()))
        ).readArray();
        MatcherAssert.assertThat(
            json,
            Matchers.emptyIterable()
        );
    }

    /**
     * Adds a new Contributor Contract.
     */
    @Test
    public void addsNewContributorContract(){
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        final Projects projects = Mockito.mock(Projects.class);
        final Project project = this.mockActiveProject("mihai",
            "mihai", "test");
        final Contracts contracts = Mockito.mock(Contracts.class);

        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(user.provider()).thenReturn(provider);
        Mockito.when(user.projects()).thenReturn(projects);
        Mockito.when(projects
            .getProjectById(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(project);
        Mockito.when(project.contracts()).thenReturn(contracts);
        Mockito.when(contracts.addContract(Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(BigDecimal.class),
            Mockito.anyString()))
            .thenAnswer(inv -> {
                final String role = inv.getArgument(4);
                final Contract.Id id = new Contract.Id(
                    inv.getArgument(0),
                    inv.getArgument(1),
                    inv.getArgument(2),
                    role
                );
                final BigDecimal hourlyRate = inv.getArgument(3);
                return this.mockContract(id, project, hourlyRate,
                    BigDecimal.valueOf(25));
            });

        final ContractsApi api = new ContractsApi(user);

        final ContractInput input = new ContractInput();
        input.setUsername("john");
        input.setHourlyRate(16.33);
        input.setRole(Contract.Roles.DEV);

        MatcherAssert.assertThat(api
                .contracts("mihai", "test", input).getStatusCode(),
            Matchers.is(HttpStatus.CREATED));
        Mockito.verify(contracts)
            .addContract(
                "mihai/test",
                "john",
                "github",
                BigDecimal.valueOf(1633).setScale(2),
                "DEV"
            );
    }

    /**
     * Returns HttpStatus.PRECONDITION_FAILED if contract was not created.
     */
    @Test
    public void contractIsNotAdded(){
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        final Projects projects = Mockito.mock(Projects.class);
        final Project project = this.mockActiveProject("mihai",
            "mihai", "test");
        final Contracts contracts = Mockito.mock(Contracts.class);

        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(user.provider()).thenReturn(provider);
        Mockito.when(user.projects()).thenReturn(projects);
        Mockito.when(projects
            .getProjectById(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(project);
        Mockito.when(project.contracts()).thenReturn(contracts);
        Mockito.when(contracts.addContract(Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(BigDecimal.class),
            Mockito.anyString()))
            .thenThrow(new IllegalStateException("Contract not created!"));

        final ContractsApi api = new ContractsApi(user);

        final ContractInput input = new ContractInput();
        input.setUsername("john");
        input.setHourlyRate(10);
        input.setRole(Contract.Roles.DEV);
        MatcherAssert.assertThat(api
                .contracts("mihai", "test", input).getStatusCode(),
            Matchers.is(HttpStatus.PRECONDITION_FAILED));

    }

    /**
     * Returns HttpStatus.PRECONDITION_FAILED if contract was not created
     * due to project not found.
     */
    @Test
    public void contractIsNotAddedIfProjectNotFound(){
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        final Projects projects = Mockito.mock(Projects.class);
        final Contracts contracts = Mockito.mock(Contracts.class);

        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(user.provider()).thenReturn(provider);
        Mockito.when(user.projects()).thenReturn(projects);

        Mockito.when(contracts.addContract(Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(BigDecimal.class),
            Mockito.anyString()))
            .thenThrow(new IllegalStateException("Contract not created!"));

        final ContractsApi api = new ContractsApi(user);

        final ContractInput input = new ContractInput();
        input.setUsername("john");
        input.setHourlyRate(10);
        input.setRole(Contract.Roles.DEV);
        MatcherAssert.assertThat(api
                .contracts("mihai", "test", input).getStatusCode(),
            Matchers.is(HttpStatus.PRECONDITION_FAILED));

    }

    /**
     * ContractsApi.restoreContract(...) ignores restoring if Contract is not
     * marked for removal.
     */
    @Test
    public void restoreIsIgnoreIfContractNotMarked(){
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        final Projects projects = Mockito.mock(Projects.class);
        final Project project = this.mockActiveProject("mihai",
            "mihai", "test");
        final Contracts contracts = Mockito.mock(Contracts.class);
        final Contract.Id contractId = new Contract.Id(
            "mihai/test",
            "john",
            "github",
            "DEV"
        );
        final Contract contract = this.mockContract(
            contractId,
            project,
            BigDecimal.TEN,
            BigDecimal.TEN
        );

        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(user.provider()).thenReturn(provider);
        Mockito.when(user.projects()).thenReturn(projects);
        Mockito.when(projects.getProjectById("mihai/test", "github"))
            .thenReturn(project);
        Mockito.when(project.contracts()).thenReturn(contracts);
        Mockito.when(contracts.findById(contractId)).thenReturn(contract);

        final ContractsApi api = new ContractsApi(user);

        ResponseEntity<String> resp = api
            .restoreContract("mihai", "test", "john", "DEV");
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.NO_CONTENT));
    }

    /**
     * ContractsApi.restoreContract(...) ignores restoring if Contract is not
     * found.
     */
    @Test
    public void restoreIsIgnoredIfContractNotFound(){
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        final Projects projects = Mockito.mock(Projects.class);
        final Project project = this.mockActiveProject("mihai",
            "mihai", "test");
        final Contracts contracts = Mockito.mock(Contracts.class);

        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(user.provider()).thenReturn(provider);
        Mockito.when(user.projects()).thenReturn(projects);
        Mockito.when(projects.getProjectById("mihai/test", "github"))
            .thenReturn(project);
        Mockito.when(project.contracts()).thenReturn(contracts);

        final ContractsApi api = new ContractsApi(user);

        ResponseEntity<String> resp = api
            .restoreContract("mihai", "test", "john", "DEV");
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.NO_CONTENT));
    }

    /**
     * ContractsApi.restoreContract(...) ignores restoring if Project is not
     * found.
     */
    @Test
    public void restoreIsIgnoredIfProjectNotFound(){
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        final Projects projects = Mockito.mock(Projects.class);

        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(user.provider()).thenReturn(provider);
        Mockito.when(user.projects()).thenReturn(projects);

        final ContractsApi api = new ContractsApi(user);

        ResponseEntity<String> resp = api
            .restoreContract("mihai", "test", "john", "DEV");
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.NO_CONTENT));
    }

    /**
     * ContractsApi.tasks(...) list of all tasks active and closed.
     */
    @Test
    public void listsAllTasks(){
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        final Projects projects = Mockito.mock(Projects.class);
        final Project project = this
            .mockActiveProject("mihai", "mihai", "test");
        final Contracts contracts = Mockito.mock(Contracts.class);
        final Contract contract = Mockito.mock(Contract.class);
        final Tasks activeTasks = Mockito.mock(Tasks.class);
        final Invoices invoices = Mockito.mock(Invoices.class);

        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(user.provider()).thenReturn(provider);
        Mockito.when(user.projects()).thenReturn(projects);
        Mockito.when(projects
            .getProjectById("mihai/test", "github"))
            .thenReturn(project);
        Mockito.when(project.contracts()).thenReturn(contracts);
        Mockito.when(contracts.findById(new Contract.Id(
            "mihai/test",
            "mihai",
            "github",
            "DEV"
        ))).thenReturn(contract);
        Mockito.when(contract.tasks()).thenReturn(activeTasks);
        final List<Task> activeTasksSrc = List
            .of(this.mockTask(), this.mockTask());
        Mockito.when(activeTasks.spliterator()).thenReturn(
            activeTasksSrc.spliterator()
        );
        Mockito.when(contract.invoices()).thenReturn(invoices);
        //invoice has 5 invoiced tasks.
        final List<Invoice> invoicesSrc = List.of(this.mockInvoice(1, 5));
        Mockito.when(invoices.spliterator()).thenReturn(
            invoicesSrc.spliterator()
        );

        final ContractsApi api = new ContractsApi(user);
        final ResponseEntity<String> resp = api
            .tasks("mihai", "test", "mihai", "DEV");
        final JsonArray json = Json.createReader(
            new StringReader(Objects.requireNonNull(resp.getBody()))
        ).readArray();
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.OK));
        MatcherAssert.assertThat(
            "Should have 7 tasks in total",
            json,
            Matchers.iterableWithSize(2 + 5)
        );
        MatcherAssert.assertThat(
            "Should have 2 active tasks",
            () -> json
                .stream()
                .filter(j -> j
                    .asJsonObject()
                    .getString("status")
                    .equals("active"))
                .iterator(),
            Matchers.<JsonValue>iterableWithSize(2)
        );
        MatcherAssert.assertThat(
            "Should have 5 closed tasks",
            () -> json
                .stream()
                .filter(j -> j
                    .asJsonObject()
                    .getString("status")
                    .equals("closed"))
                .iterator(),
            Matchers.<JsonValue>iterableWithSize(5)
        );
    }

    /**
     * ContractsApi.tasks(...) returns no content if Project was not found.
     */
    @Test
    public void listsAllTasksHasNoContentIfProjectNotFound(){
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        final Projects projects = Mockito.mock(Projects.class);

        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(user.provider()).thenReturn(provider);
        Mockito.when(user.projects()).thenReturn(projects);

        final ContractsApi api = new ContractsApi(user);
        final ResponseEntity<String> resp = api
            .tasks("mihai", "test", "mihai", "DEV");
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.NO_CONTENT));
    }

    /**
     * ContractsApi.tasks(...) returns no content if Project Contract
     * was not found.
     */
    @Test
    public void listsAllTasksHasNoContentIfContractNotFound(){
        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        final Projects projects = Mockito.mock(Projects.class);
        final Contracts contracts = Mockito.mock(Contracts.class);
        final Project project = this
            .mockActiveProject("mihai", "mihai", "test");

        Mockito.when(user.username()).thenReturn("mihai");
        Mockito.when(provider.name()).thenReturn("github");
        Mockito.when(user.provider()).thenReturn(provider);
        Mockito.when(user.projects()).thenReturn(projects);
        Mockito.when(projects
            .getProjectById("mihai/test", "github"))
            .thenReturn(project);
        Mockito.when(project.contracts()).thenReturn(contracts);

        final ContractsApi api = new ContractsApi(user);
        final ResponseEntity<String> resp = api
            .tasks("mihai", "test", "mihai", "DEV");
        MatcherAssert.assertThat(resp.getStatusCode(),
            Matchers.is(HttpStatus.NO_CONTENT));
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
        Mockito.when(manager.projectPercentage()).thenReturn(6.5);
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
        Mockito.when(project.wallet()).thenReturn(wallet);

        return project;
    }

    /**
     * Mock a Contract.
     * @param id Contract.Id.
     * @param project Project.
     * @param hourlyRate Hourly Rate.
     * @param value Value.
     * @return Contract.
     * @checkstyle ParameterNumber (10 lines).
     */
    private Contract mockContract(
        final Contract.Id id,
        final Project project,
        final BigDecimal hourlyRate,
        final BigDecimal value){
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contractId()).thenReturn(id);
        Mockito.when(contract.project()).thenReturn(project);
        Mockito.when(contract.hourlyRate()).thenReturn(hourlyRate);
        Mockito.when(contract.value()).thenReturn(value);
        Mockito.when(contract.revenue()).thenReturn(BigDecimal.valueOf(9000));
        return contract;
    }

    /**
     * Mocks Contracts.
     * @param contract Contracts list.
     * @return Contracts.
     */
    private Contracts mockContracts(final Contract... contract){
        final Contracts contracts = Mockito.mock(Contracts.class);
        Mockito.when(contracts.iterator())
            .thenReturn(Arrays.asList(contract).iterator());
        return contracts;
    }

    /**
     * Mocks a Contract Task.
     * @return Task.
     */
    private Task mockTask(){
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.issueId()).thenReturn("null");
        Mockito.when(task.assignmentDate()).thenReturn(LocalDateTime.now());
        Mockito.when(task.deadline()).thenReturn(null);
        Mockito.when(task.estimation()).thenReturn(60);
        Mockito.when(task.value()).thenReturn(BigDecimal.valueOf(100));
        return task;
    }

    /**
     * Mocks an InvoicedTask.
     * @return InvoicedTask.
     */
    private InvoicedTask mockInvoicedTask(){
        final InvoicedTask invoicedTask = Mockito.mock(InvoicedTask.class);
        final Task task = this.mockTask();
        Mockito.when(invoicedTask.task()).thenReturn(task);
        return invoicedTask;
    }

    /**
     * Mocks an Invoice.
     * @param invoiceId Invoice id.
     * @param invoicedTasksSize Number of invoiced tasks to have.
     * @return Invoice.
     */
    private Invoice mockInvoice(final int invoiceId,
                                final int invoicedTasksSize){
        final Invoice invoice = Mockito.mock(Invoice.class);
        final InvoicedTasks tasks = Mockito.mock(InvoicedTasks.class);
        final List<InvoicedTask> tasksSrc = IntStream
            .rangeClosed(1, invoicedTasksSize)
            .mapToObj(i -> this.mockInvoicedTask())
            .collect(Collectors.toList());
        Mockito.when(invoice.invoiceId()).thenReturn(invoiceId);
        Mockito.when(invoice.tasks()).thenReturn(tasks);
        Mockito.when(tasks.spliterator()).thenReturn(tasksSrc.spliterator());
        return invoice;
    }
}
