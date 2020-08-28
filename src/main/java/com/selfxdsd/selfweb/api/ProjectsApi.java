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
import com.selfxdsd.selfweb.api.input.RepoInput;
import com.selfxdsd.selfweb.api.output.JsonContract;
import com.selfxdsd.selfweb.api.output.JsonContracts;
import com.selfxdsd.selfweb.api.output.JsonProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * Projects.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #70:60min Implement and test the Contracts tab of the project
 *  page. Start implementing the JavaScript interactor withProjectsAPI
 *  contracts endpoints GET and POST.
 */
@RestController
public class ProjectsApi extends BaseApiController {

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
    public ProjectsApi(final User user, final Self self) {
        this.user = user;
        this.self = self;
    }

    /**
     * Get a Github project in JSON format.<br><br>
     *
     * If the Project owner does not match the authenticated User, we have
     * to check the User's organizations to see if the project
     * is part of an organization where the User has admin rights.
     *
     * @param owner Owner of the repo (username or org name).
     * @param name Simple name of the repo.
     * @return Json response or NO CONTENT if the project is not found.
     */
    @GetMapping(
        value = "/projects/{owner}/{name}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> project(
        @PathVariable("owner") final String owner,
        @PathVariable("name") final String name
    ) {
        final Project found = this.self.projects().getProjectById(
            owner + "/" + name, user.provider().name()
        );
        ResponseEntity<String> response = ResponseEntity.noContent().build();
        if(found != null) {
            if(owner.equalsIgnoreCase(this.user.username())) {
                response = ResponseEntity.ok(
                    new JsonProject(found).toString()
                );
            } else {
                final Organizations orgs = this.user.provider()
                    .organizations();
                for(final Organization org : orgs) {
                    for(final Repo repo : org.repos()) {
                        if(repo.fullName().equals(found.repoFullName())) {
                            response = ResponseEntity.ok(
                                new JsonProject(found).toString()
                            );
                            break;
                        }
                    }
                    if(response.getStatusCode().equals(HttpStatus.OK)) {
                        break;
                    }
                }
            }
        }
        return response;
    }

    /**
     * Register a new project in Self.
     * @param repo Repo's data.
     * @return JsonObject.
     */
    @PostMapping(
        value = "/projects/new",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> activate(@Valid final RepoInput repo) {
        final String username = this.user.username();
        Repo found = null;
        if(repo.getOwner().equalsIgnoreCase(username)) {
            found = this.user.provider().repo(
                repo.getOwner(),
                repo.getName()
            );
        } else {
            final Organizations orgs = this.user.provider().organizations();
            for(final Organization org : orgs) {
                for(final Repo orgRepo : org.repos()) {
                    if(orgRepo.fullName().equalsIgnoreCase(repo.fullName())) {
                        found = orgRepo;
                        break;
                    }
                }
                if(found != null) {
                    break;
                }
            }
        }
        final ResponseEntity<String> resp;
        if(found == null) {
            resp = ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .build();
        } else {
            final Project activated = found.activate();
            resp = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new JsonProject(activated).toString());
        }
        return resp;
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
