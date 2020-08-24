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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Projects.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #56:60min Implement and test the Contracts tab of the project
 *  page. It should list all contributors/contracts and offer forms
 *  to register a new contributor/contract.
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
        value = "/projects/github/{owner}/{name}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> project(
        @PathVariable("owner") final String owner,
        @PathVariable("name") final String name
    ) {
        final Project found = this.self.projects().getProjectById(
            owner + "/" + name, Provider.Names.GITHUB
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
}
