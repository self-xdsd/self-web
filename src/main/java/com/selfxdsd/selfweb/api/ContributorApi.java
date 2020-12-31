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

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.User;
import com.selfxdsd.selfweb.api.output.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Contributor endpoints.<br><br>
 *
 * These endpoints are mainly used on the 'Contributor Dashboard' page.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #89:60min Implement the Contributor Dashboard HTML page.
 *  If the /contributor endpoint returns NO CONTENT, we should simply
 *  display a message saying "You are not a contributor in any project...".
 *  If the /contributor returns data, we should display the Contracts,
 *  links to the Tasks etc.
 */
@RestController
public class ContributorApi extends BaseApiController {

    /**
     * Authenticated user.
     */
    private final User user;

    /**
     * Ctor.
     * @param user Authenticated user.
     */
    @Autowired
    public ContributorApi(final User user) {
        this.user = user;
    }

    /**
     * Get the authenticated Contributor.
     * @return String JSON.
     */
    @GetMapping(
        value = "/contributor",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> contributor() {
        final ResponseEntity<String> resp;
        final Contributor contributor = this.user.asContributor();
        if(contributor == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            resp = ResponseEntity.ok(
                new JsonContributor(contributor).toString()
            );
        }
        return resp;
    }

    /**
     * Get the authenticated Contributor's Tasks from a given Contract.
     * @param owner Repo owner.
     * @param name Repo name.
     * @param role Contributor role (DEV, REV etc).
     * @return String JSON.
     */
    @GetMapping(
        value = "/contributor/contracts/{owner}/{name}/tasks",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> tasks(
        @PathVariable final String owner,
        @PathVariable final String name,
        @RequestParam("role") final String role
    ) {
        final ResponseEntity<String> resp;
        final Contributor contributor = this.user.asContributor();
        if(contributor == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            final Contract contract = contributor.contract(
                owner + "/" + name,
                this.user.provider().name(),
                role
            );
            if(contract == null) {
                resp = ResponseEntity.badRequest().build();
            } else {
                resp = ResponseEntity.ok(
                    new JsonTasks(contract.tasks()).toString()
                );
            }
        }
        return resp;
    }

    /**
     * Get the authenticated Contributor's Invoices from a given Contract.
     * @param owner Repo owner.
     * @param name Repo name.
     * @param role Contributor role (DEV, REV etc).
     * @return String JSON.
     */
    @GetMapping(
        value = "/contributor/contracts/{owner}/{name}/invoices",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> invoices(
        @PathVariable final String owner,
        @PathVariable final String name,
        @RequestParam("role") final String role
    ) {
        final ResponseEntity<String> resp;
        final Contributor contributor = this.user.asContributor();
        if(contributor == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            final Contract contract = contributor.contract(
                owner + "/" + name,
                this.user.provider().name(),
                role
            );
            if(contract == null) {
                resp = ResponseEntity.badRequest().build();
            } else {
                resp = ResponseEntity.ok(
                    new JsonInvoices(contract.invoices()).toString()
                );
            }
        }
        return resp;
    }

    /**
     * Get one of the authenticated Contributor's invoices.
     * @param owner Repo owner.
     * @param name Repo name.
     * @param invoiceId Invoice ID.
     * @param role Contributor role (DEV, REV etc).
     * @return String JSON.
     * @checkstyle ParameterNumber (20 lines)
     */
    @GetMapping(
        value = "/contributor/contracts/{owner}/{name}/invoices/{invoiceId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> invoice(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final int invoiceId,
        @RequestParam("role") final String role
    ) {
        final ResponseEntity<String> resp;
        final Contributor contributor = this.user.asContributor();
        if(contributor == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            final Contract contract = contributor.contract(
                owner + "/" + name,
                this.user.provider().name(),
                role
            );
            if(contract == null) {
                resp = ResponseEntity.badRequest().build();
            } else {
                final Invoice found = contract.invoices().getById(invoiceId);
                if(found == null){
                    resp = ResponseEntity.badRequest().build();
                } else {
                    resp = ResponseEntity.ok(
                        new JsonInvoice(found, true).toString()
                    );
                }
            }
        }
        return resp;
    }

    /**
     * Get one of the authenticated Contributor's invoices as PDF.
     * @param owner Repo owner.
     * @param name Repo name.
     * @param invoiceId Invoice ID.
     * @param role Contributor role (DEV, REV etc).
     * @throws IOException If something goes wrong.
     * @return Resource PDF.
     * @checkstyle ParameterNumber (20 lines)
     */
    @GetMapping(
        value = "/contributor/contracts/{owner}/{name}/invoices"
            + "/{invoiceId}/pdf",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Resource> invoicePdf(
        @PathVariable final String owner,
        @PathVariable final String name,
        @PathVariable final int invoiceId,
        @RequestParam("role") final String role
    ) throws IOException {
        final ResponseEntity<Resource> resp;
        final Contributor contributor = this.user.asContributor();
        if(contributor == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            final Contract contract = contributor.contract(
                owner + "/" + name,
                this.user.provider().name(),
                role
            );
            if(contract == null) {
                resp = ResponseEntity.badRequest().build();
            } else {
                final Invoice found = contract.invoices().getById(invoiceId);
                if(found == null){
                    resp = ResponseEntity.badRequest().build();
                } else {
                    final File pdf = found.toPdf();
                    final ByteArrayResource resource = new ByteArrayResource(
                        Files.readAllBytes(
                            Paths.get(pdf.getAbsolutePath())
                        )
                    );
                    resp = ResponseEntity.ok()
                        .contentLength(pdf.length())
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(
                            "Content-Disposition",
                            "attachment; filename="
                            + "invoice_slfx_" + found.invoiceId() + ".pdf"
                        )
                        .body(resource);
                }
            }
        }
        return resp;
    }

    /**
     * Mark for removal one contract of the authenticated Contributor.
     * @param owner Repo owner.
     * @param name Repo name.
     * @param role Contributor role (DEV, REV etc).
     * @return JsonResponse.
     * @checkstyle ParameterNumber (10 lines)
     */
    @DeleteMapping(
        value = "/contributor/contracts/{owner}/{name}/mark",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> markContractForRemoval(
        @PathVariable final String owner,
        @PathVariable final String name,
        @RequestParam("role") final String role
    ) {
        final ResponseEntity<String> resp;
        final Contributor contributor = this.user.asContributor();
        if(contributor == null) {
            resp = ResponseEntity.noContent().build();
        } else {
            final Contract contract = contributor.contract(
                owner + "/" + name,
                this.user.provider().name(),
                role
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
}
