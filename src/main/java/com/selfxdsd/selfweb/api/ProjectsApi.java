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
import com.selfxdsd.core.managers.InvitePm;
import com.selfxdsd.selfweb.api.input.PmInput;
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

import javax.json.Json;
import javax.validation.Valid;

/**
 * Projects.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
@RestController
public class ProjectsApi extends BaseApiController {

    /**
     * Authenticated user.
     */
    private final User user;

    /**
     * Ctor.
     * @param user Authenticated user.
     */
    @Autowired
    public ProjectsApi(final User user) {
        this.user = user;
    }

    /**
     * Get a Github project in JSON format.
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
        final Project found = this.user.projects().getProjectById(
            owner + "/" + name, Provider.Names.GITHUB
        );
        final ResponseEntity<String> response;
        if(found == null) {
            response = ResponseEntity.noContent().build();
        } else {
            response = ResponseEntity.ok(new JsonProject(found).toString());
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
