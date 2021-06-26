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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 * Repositories.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
@RestController
public class Repositories extends BaseApiController {

    /**
     * Self XDSD user.
     */
    private User user;

    /**
     * Ctor.
     * @param user Authenticatd user.
     */
    @Autowired
    public Repositories(final User user) {
        this.user = user;
    }

    /**
     * Get the user's personal repos (both public and private).
     * @return ResponseEntity.
     * @todo #443:60min Modify the front-end part of personal repos. We
     *  should call this endpoint instead of Github directly.
     */
    @GetMapping(
        value = "/repositories/personal",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> personalRepos() {
        JsonArrayBuilder reposBuilder = Json.createArrayBuilder();
        final Repos personal = this.user.provider().repos();
        for(final Repo repo : personal) {
            reposBuilder = reposBuilder.add(Json.createObjectBuilder()
                .add("repoFullName", repo.fullName())
                .add("provider", repo.provider())
                .build());
        }
        final JsonArray repos = reposBuilder.build();
        final ResponseEntity<String> response;
        if(repos.isEmpty()){
            response = ResponseEntity.noContent().build();
        } else {
            response = ResponseEntity.ok(repos.toString());
        }
        return response;
    }

    /**
     * Get the user's organization repos (repos in an organization
     * to which the user has admin rights).
     * @return ResponseEntity.
     */
    @GetMapping(
        value = "/repositories/orgs",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> organizationRepos() {
        JsonArrayBuilder reposBuilder = Json.createArrayBuilder();
        final Organizations orgs = this.user
            .provider()
            .organizations();
        for(final Organization org : orgs) {
            for(final Repo repo : org.repos()) {
                reposBuilder = reposBuilder.add(Json.createObjectBuilder()
                    .add("repoFullName", repo.fullName())
                    .add("provider", repo.provider())
                    .build());
            }
        }
        final JsonArray repos = reposBuilder.build();
        final ResponseEntity<String> response;
        if(repos.isEmpty()){
            response = ResponseEntity.noContent().build();
        } else {
            response = ResponseEntity.ok(repos.toString());
        }
        return response;
    }

    /**
     * Get the User's Projects (repos managed by Self XDSD).
     * @return ResponseEntity.
     */
    @GetMapping(
        value = "/repositories/managed",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> managedRepos() {
        JsonArrayBuilder reposBuilder = Json.createArrayBuilder();
        for(final Project project : this.user.projects()) {
            reposBuilder = reposBuilder.add(
                Json.createObjectBuilder()
                    .add("repoFullName", project.repoFullName())
                    .add("provider", project.provider())
                    .build()
            );
        }
        final JsonArray repos = reposBuilder.build();
        final ResponseEntity<String> response;
        if(repos.isEmpty()){
            response = ResponseEntity.noContent().build();
        } else {
            response = ResponseEntity.ok(repos.toString());
        }
        return response;
    }
}
