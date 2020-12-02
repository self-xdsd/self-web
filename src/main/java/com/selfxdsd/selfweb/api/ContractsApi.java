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
import com.selfxdsd.selfweb.api.input.ContractInput;
import com.selfxdsd.selfweb.api.output.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.json.Json;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Function;

/**
 * This controller offers HTTP endpoints regarding a Project's contracts.
 * It's used primarily by the forms on the Project's "Contracts" tab.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 * @todo #84:30min. Start implementing ContributorsApi method contributors GET.
 *  This will list all contributors.
 * @todo #175:15min Once self-core 'Restore Contract' functionality for
 *  a Project Contract is available, remove the placeholder `restoreContractApi`
 *  and use the real api call. Also test constructor needs to be removed and
 *  "restore contract" unit tests updated.
 * @todo #200:30min On frontend. in `getAndAddContracts.js`, implement
 *  `restoreContract(...)` function. This will call
 *  `DELETE "/projects/{owner}/{name}/contracts/{username}/mark" endpoint,
 *  and on success will reset the corresponding contract table row.
 */
@RestController
@Validated
public class ContractsApi extends BaseApiController {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        ContractsApi.class
    );

    /**
     * Authenticated user.
     */
    private final User user;

    /**
     * Placeholder until Contract/Contracts API has a way to
     * restore a Contract from removal.
     */
    private final Function<Contract, Contract> restoreContractApi;

    /**
     * Ctor.
     * @param user Authenticated user.
     */
    @Autowired
    public ContractsApi(final User user) {
        this(user, contract -> {
            LOG.warn("Contract " + contract.contractId()
                + " can't be restored from removal."
                +" API method not available yet.");
            return contract;
        });
    }

    /**
     * Ctor.
     * @param user Authenticated user.
     * @param restoreContractApi Placeholder until Contract/Contracts API
     * has a way to restore a Contract from removal.
     */
    ContractsApi(final User user,
                 final Function<Contract, Contract> restoreContractApi) {
        this.user = user;
        this.restoreContractApi = restoreContractApi;
    }

    /**
     * Get contracts of an owned project in JSON format.<br><br>
     * @param owner Owner of the project (username or org name).
     * @param name Simple name of the project.
     * @return JsonArray.
     */
    @GetMapping(
        value = "/projects/{owner}/{name}/contracts",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> contracts(
        @PathVariable("owner") final String owner,
        @PathVariable("name") final String name) {
        final Project project = this.user.projects().getProjectById(
            owner + "/" + name, this.user.provider().name()
        );
        final JsonContracts contracts;
        if (project == null) {
            contracts = new JsonContracts(new Contracts.Empty());
        } else {
            contracts = new JsonContracts(project.contracts());
        }
        return ResponseEntity.ok(contracts.toString());
    }

    /**
     * Get the Tasks of a specific Contract.
     * @param owner Owner of the project (username or org name).
     * @param name Simple name of the project.
     * @param username Contributor's username.
     * @param role Contributor's role.
     * @return JsonArray.
     * @checkstyle ParameterNumber (10 lines)
     */
    @GetMapping(
        value = "/projects/{owner}/{name}/contracts/{username}/tasks",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> tasks(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String username,
        @RequestParam("role") final String role
    ) {
        final ResponseEntity<String> resp;
        final Project project = this.user.projects().getProjectById(
            owner + "/" + name, this.user.provider().name()
        );
        if(project == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            final Contract contract = project.contracts().findById(
                new Contract.Id(
                    owner + "/" + name,
                    username,
                    project.provider(),
                    role
                )
            );
            if(contract == null) {
                resp = ResponseEntity.noContent().build();
            } else {
                final Tasks tasks = contract.tasks();
                resp = ResponseEntity.ok(
                    new JsonTasks(tasks).toString()
                );
            }
        }
        return resp;
    }

    /**
     * Get the Invoices of a specific Contract.
     * @param owner Owner of the project (username or org name).
     * @param name Simple name of the project.
     * @param username Contributor's username.
     * @param role Contributor's role.
     * @return JsonArray.
     * @checkstyle ParameterNumber (10 lines)
     */
    @GetMapping(
        value = "/projects/{owner}/{name}/contracts/{username}/invoices",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> invoices(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String username,
        @RequestParam("role") final String role
    ) {
        final ResponseEntity<String> resp;
        final Project project = this.user.projects().getProjectById(
            owner + "/" + name, this.user.provider().name()
        );
        if(project == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            final Contract contract = project.contracts().findById(
                new Contract.Id(
                    owner + "/" + name,
                    username,
                    project.provider(),
                    role
                )
            );
            if(contract == null) {
                resp = ResponseEntity.noContent().build();
            } else {
                final Invoices invoices = contract.invoices();
                resp = ResponseEntity.ok(
                    new JsonInvoices(invoices).toString()
                );
            }
        }
        return resp;
    }

    /**
     * Get an Invoice of a specific Contract.
     * @param owner Owner of the project (username or org name).
     * @param name Simple name of the project.
     * @param username Contributor's username.
     * @param invoiceId If od the Invoice.
     * @param role Contributor's role.
     * @return JsonArray.
     * @checkstyle ParameterNumber (10 lines)
     */
    @GetMapping(
        value = "/projects/{owner}/{name}/contracts/{username}/invoices"
        + "/{invoiceId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> invoice(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String username,
        @PathVariable final int invoiceId,
        @RequestParam("role") final String role) {
        final ResponseEntity<String> resp;
        final Project project = this.user.projects().getProjectById(
            owner + "/" + name, this.user.provider().name()
        );
        if(project == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            final Contract contract = project.contracts().findById(
                new Contract.Id(
                    owner + "/" + name,
                    username,
                    project.provider(),
                    role
                )
            );
            if(contract == null) {
                resp = ResponseEntity.noContent().build();
            } else {
                final Invoice found = contract.invoices().getById(invoiceId);
                if(found == null){
                    resp = ResponseEntity.noContent().build();
                } else {
                    resp = ResponseEntity.ok(
                        new JsonInvoice(found, Boolean.TRUE).toString()
                    );
                }
            }
        }
        return resp;
    }

    /**
     * Add new contributor for project Contract in Self.<br><br>
     * @param owner Owner of the project (username or org name).
     * @param name Simple name of the project.
     * @param input Contract form input.
     * @return Created Contract as JSON.
     */
    @PostMapping(
        value = "/projects/{owner}/{name}/contracts",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> contracts(
        @PathVariable("owner") final String owner,
        @PathVariable("name") final String name,
        @Valid final ContractInput input){

        final String repoFullName = owner + "/" + name;
        final String provider = this.user.provider().name();
        final BigDecimal hourlyRate = BigDecimal
            .valueOf(input.getHourlyRate())
            .multiply(BigDecimal.valueOf(100));

        ResponseEntity<String> response;
        try {
            final Project project = this.user
                .projects()
                .getProjectById(repoFullName, provider);
            if (project != null) {
                final Contract contract = project
                    .contracts()
                    .addContract(repoFullName, input.getUsername(),
                        provider, hourlyRate, input.getRole());
                response = ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new JsonContract(contract).toString());
            } else {
                response = ResponseEntity
                    .status(HttpStatus.PRECONDITION_FAILED)
                    .body(Json.createObjectBuilder()
                        .add("reason", "Project '" + repoFullName
                            + "(" + provider + ")' was not found.")
                        .build()
                        .toString());
            }
        } catch (final IllegalStateException exception) {
            response = ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .body(Json.createObjectBuilder()
                    .add("reason", "Something went wrong.")
                    .build()
                    .toString());
        }

        return response;
    }

    /**
     * Update a specific Contract.
     * @param owner Owner of the project (username or org name).
     * @param name Simple name of the project.
     * @param username Contributor's username.
     * @param role Contributor's role.
     * @param newHourlyRate New Hourly rate. At the moment, this is the
     *  only Contract attribute which can be updated.
     * @return JsonArray.
     * @checkstyle ParameterNumber (10 lines)
     */
    @PostMapping(
        value = "/projects/{owner}/{name}/contracts/{username}/update",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> updateContract(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String username,
        @RequestParam("role") final String role,

        @RequestParam("newHourlyRate")
        @Min(value = 8)
        @Max(value = 300)
        final double newHourlyRate
    ) {
        final ResponseEntity<String> resp;
        final Project project = this.user.projects().getProjectById(
            owner + "/" + name, this.user.provider().name()
        );
        if(project == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            final Contract contract = project.contracts().findById(
                new Contract.Id(
                    owner + "/" + name,
                    username,
                    project.provider(),
                    role
                )
            );
            if(contract == null) {
                resp = ResponseEntity.noContent().build();
            } else {
                final Contract updated = contract.update(
                    BigDecimal
                        .valueOf(newHourlyRate)
                        .multiply(BigDecimal.valueOf(100))
                );
                resp = ResponseEntity.ok(
                    Json.createObjectBuilder()
                        .add("id", Json.createObjectBuilder()
                            .add("repoFullName", updated.contractId()
                                .getRepoFullName())
                            .add("contributorUsername", updated.contractId()
                                .getContributorUsername())
                            .add("provider", updated.contractId().getProvider())
                            .add("role", updated.contractId().getRole())
                            .build())
                        .add("hourlyRate", NumberFormat
                            .getCurrencyInstance(Locale.GERMANY)
                            .format(
                                updated.hourlyRate()
                                    .divide(BigDecimal.valueOf(100))
                            )
                        ).build()
                        .toString()
                );
            }
        }
        return resp;
    }

    /**
     * Mark a Project's Contract for deletion.
     * @param owner Owner of the project (username or org name).
     * @param name Simple name of the project.
     * @param username Contributor's username.
     * @param role Contributor's role.
     * @return JsonResponse.
     * @checkstyle ParameterNumber (10 lines)
     */
    @PutMapping(
        value = "/projects/{owner}/{name}/contracts/{username}/mark",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> markContractForRemoval(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String username,
        @RequestParam("role") final String role
    ) {
        final ResponseEntity<String> resp;
        final Project project = this.user.projects().getProjectById(
            owner + "/" + name, this.user.provider().name()
        );
        if(project == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            final Contract contract = project.contracts().findById(
                new Contract.Id(
                    owner + "/" + name,
                    username,
                    project.provider(),
                    role
                )
            );
            if(contract == null) {
                resp = ResponseEntity.noContent().build();
            } else {
                final Contract marked = contract.markForRemoval();
                resp = ResponseEntity.ok(
                    new JsonContract(marked).toString()
                );
            }
        }
        return resp;
    }

    /**
     * Restores a Contract marked for removal.
     * @param owner Owner of the project (username or org name).
     * @param name Simple name of the project.
     * @param username Contributor's username.
     * @param role Contributor's role.
     * @return JsonResponse.
     * @checkstyle ParameterNumber (10 lines)
     */
    @DeleteMapping(
        value = "/projects/{owner}/{name}/contracts/{username}/mark",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> restoreContract(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String username,
        @RequestParam("role") final String role
    ) {
        final ResponseEntity<String> resp;
        final Project project = this.user.projects().getProjectById(
            owner + "/" + name, this.user.provider().name()
        );
        if(project == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            final Contract contract = project.contracts().findById(
                new Contract.Id(
                    owner + "/" + name,
                    username,
                    project.provider(),
                    role
                )
            );
            if(contract == null || contract.markedForRemoval() == null) {
                resp = ResponseEntity.noContent().build();
            } else {
                final Contract restored = this.restoreContractApi
                    .apply(contract);
                resp = ResponseEntity.ok(
                    new JsonContract(restored).toString()
                );
            }
        }
        return resp;
    }

    /**
     * Pay an Invoice of a specific Contract.
     * @param owner Owner of the project (username or org name).
     * @param name Simple name of the project.
     * @param username Contributor's username.
     * @param invoiceId Id of the Invoice.
     * @param role Contributor's role.
     * @return JsonArray.
     * @checkstyle ParameterNumber (10 lines)
     */
    @PutMapping(
        value = "/projects/{owner}/{name}/contracts/{username}/invoices"
        + "/{invoiceId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> payInvoice(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final String username,
        @PathVariable final int invoiceId,
        @RequestParam("role") final String role
    ) {
        final ResponseEntity<String> resp;
        final Project project = this.user.projects().getProjectById(
            owner + "/" + name, this.user.provider().name()
        );
        if(project == null) {
            resp = ResponseEntity.badRequest().build();
        } else {
            final Contract contract = project.contracts().findById(
                new Contract.Id(
                    owner + "/" + name,
                    username,
                    project.provider(),
                    role
                )
            );
            if(contract == null) {
                resp = ResponseEntity.badRequest().build();
            } else {
                final Invoice found = contract.invoices().getById(invoiceId);
                if(found == null){
                    resp = ResponseEntity.noContent().build();
                } else {
                    if (!found.isPaid()) {
                        final Wallet wallet = project.wallets().active();
                        wallet.pay(found);
                    }
                    resp = ResponseEntity.ok(
                        Json.createObjectBuilder()
                            .add("invoiceId", found.invoiceId())
                            .add("paid", Boolean.TRUE)
                            .build()
                            .toString()
                    );
                }
            }
        }
        return resp;
    }
}
