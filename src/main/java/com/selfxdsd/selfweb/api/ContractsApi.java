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
import com.selfxdsd.selfweb.api.output.JsonContract;
import com.selfxdsd.selfweb.api.output.JsonContracts;
import com.selfxdsd.selfweb.api.output.JsonTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.json.Json;
import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * Contracts.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 * @todo #84:30min. Start implementing ContributorsApi method contributors GET.
 *  This will list all contributors.
 */
@RestController
public class ContractsApi extends BaseApiController {

    /**
     * Authenticated user.
     */
    private final User user;

    /**
     * Self's core.
     */
    private final Self self;

    /**
     * Ctor.
     * @param user Authenticated user.
     * @param self Self's core.
     */
    @Autowired
    public ContractsApi(final User user, final Self self) {
        this.user = user;
        this.self = self;
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

}
