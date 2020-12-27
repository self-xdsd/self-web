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

/**
 * Projects.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
@RestController
@Validated
public class ProjectsApi extends BaseApiController {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        ProjectsApi.class
    );

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
        final ResponseEntity<String> resp;
        LOG.debug("Activating repo " + repo.fullName() + "... ");
        final Repo found = this.getRepo(repo.getOwner(), repo.getName());
        if(found == null) {
            LOG.error(
                "Repo " + repo.fullName()
                + " not found! Precondition failed."
            );
            resp = ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .build();
        } else {
            final Project activated = found.activate();
            LOG.debug("Repo " + repo.fullName() + " successfully activated.");
            resp = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new JsonProject(activated).toString());
        }
        return resp;
    }

    /**
     * Get the number of Contracts for this Project.
     * @param owner Login or organization name.
     * @param name Repository name.
     * @return Response.
     */
    @GetMapping(
        value = "/projects/{owner}/{name}/contracts/count",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> contractsCount(
        @PathVariable("owner") final String owner,
        @PathVariable("name") final String name
    ) {
        LOG.debug(
            "Fetching contracts count for Project "
            + owner + "/" + name + "... "
        );
        final ResponseEntity<String> response;
        final Project project = this.user.projects().getProjectById(
            owner + "/" + name, this.user.provider().name()
        );
        if(project == null) {
            LOG.error(
                "Project " + owner + "/" + name + " not found! Bad request."
            );
            response = ResponseEntity.badRequest().build();
        } else {
            final Contracts contracts = project.contracts();
            final int count = contracts.count();
            LOG.debug(
                "Project " + owner + "/" + name + " has "
                + count + " contracts."
            );
            response = ResponseEntity.ok(
                Json.createObjectBuilder()
                    .add("contractsCount", count)
                    .build()
                    .toString()
            );
        }
        return response;
    }

    /**
     * Get the number of Contracts for this Project.
     * @param owner Login or organization name.
     * @param name Repository name.
     * @return Response.
     */
    @DeleteMapping("/projects/{owner}/{name}")
    public ResponseEntity<String> deleteProject(
        @PathVariable("owner") final String owner,
        @PathVariable("name") final String name
    ) {
        ResponseEntity<String> response;
        LOG.debug("Deleting Project " + owner + "/" + name + "... ");
        final Project project = this.user.projects().getProjectById(
            owner + "/" + name, this.user.provider().name()
        );
        if(project == null) {
            LOG.error(
                "Project " + owner + "/" + name + " not found! Bad request."
            );
            response = ResponseEntity.badRequest().body(
                "Project " + owner + "/" + name + " not found."
            );
        } else {
            final Repo repo = this.getRepo(owner, name);
            if(repo == null) {
                LOG.error(
                    "Repository " + owner + "/" + name + " not found! "
                    + "Bad request."
                );
                response = ResponseEntity.badRequest().body(
                    "Repository " + owner + "/" + name + " not found."
                );
            } else {
                try {
                    project.deactivate(repo);
                    LOG.debug(
                        "Project " + owner + "/" + name
                        + " successfully deleted!"
                    );
                    response = ResponseEntity.ok().build();
                } catch (final IllegalStateException ex) {
                    LOG.error(
                        "IllegalStateException while deleting Project "
                        + owner + "/" + name + ". ", ex.getMessage()
                    );
                    response = ResponseEntity.badRequest()
                        .body(ex.getMessage());
                }
            }
        }
        return response;
    }

    /**
     * Get the Repo. It can be a personal repo or a repo
     * from an Organization where the authenticated user has admin rights.
     * @param owner Repo owner.
     * @param name Repo name.
     * @return Repo.
     */
    private Repo getRepo(final String owner, final String name) {
        final String username = this.user.username();
        Repo found = null;
        if(owner.equalsIgnoreCase(username)) {
            found = this.user.provider().repo(owner, name);
        } else {
            final Organizations orgs = this.user.provider().organizations();
            for(final Organization org : orgs) {
                for(final Repo orgRepo : org.repos()) {
                    final String fullName = owner + "/" + name;
                    if(orgRepo.fullName().equalsIgnoreCase(fullName)) {
                        found = orgRepo;
                        break;
                    }
                }
                if(found != null) {
                    break;
                }
            }
        }
        return found;
    }
}
