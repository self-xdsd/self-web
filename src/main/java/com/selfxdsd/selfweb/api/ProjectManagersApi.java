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
import com.selfxdsd.selfweb.api.input.PmInput;
import com.selfxdsd.selfweb.api.output.JsonProjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * Project Managers.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
@RestController
public class ProjectManagersApi extends BaseApiController {

    /**
     * Authenticated user.
     */
    private final User user;

    /**
     * Self's core.
     */
    private final Self core;

    /**
     * Ctor.
     * @param user Authenticated user.
     * @param core Self's core.
     */
    @Autowired
    public ProjectManagersApi(
        final User user,
        final Self core
    ) {
        this.user = user;
        this.core = core;
    }

    /**
     * Get all the pms working in Self.
     * @return JsonArray.
     */
    @GetMapping(
        value = "/managers",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> managers() {
        final ResponseEntity<String> response;
        if(!"admin".equals(this.user.role())) {
            response = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            final JsonArrayBuilder builder = Json.createArrayBuilder();
            final ProjectManagers managers = this.core.projectManagers();
            for(final ProjectManager manager : managers) {
                builder.add(new JsonProjectManager(manager));
            }
            final JsonArray array = builder.build();
            response = ResponseEntity.ok(array.toString());
        }
        return response;
    }

    /**
     * Register a new PM in Self.
     * @param newPm PM's data.
     * @return JsonObject.
     */
    @PostMapping(
        value = "/managers/new",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> register(@Valid final PmInput newPm) {
        final ResponseEntity<String> response;
        if(!"admin".equals(this.user.role())) {
            response = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            final ProjectManager registered = this.core
                .projectManagers()
                .register(
                    newPm.getUserId(),
                    newPm.getUsername(),
                    newPm.getProvider(),
                    newPm.getToken(),
                    BigDecimal.valueOf(
                        newPm.getCommission() * 100
                    )
                );
            response = ResponseEntity.ok(
                new JsonProjectManager(registered).toString()
            );
        }
        return response;
    }
}
