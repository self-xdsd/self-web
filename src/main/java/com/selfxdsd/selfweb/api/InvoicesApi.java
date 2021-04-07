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
import com.selfxdsd.selfweb.api.output.JsonPlatformInvoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 * Invoices API. Only for admins.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
@RestController
public class InvoicesApi extends BaseApiController {

    /**
     * Authenticated user.
     */
    private final User user;

    /**
     * Ctor.
     * @param user Authenticated user.
     */
    @Autowired
    public InvoicesApi(final User user) {
        this.user = user;
    }

    /**
     * Get all the PlatformInvoices in Self.
     * @return JsonArray.
     */
    @GetMapping(
        value = "/invoices",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> invoices() {
        final ResponseEntity<String> response;
        if(!"admin".equals(this.user.role())) {
            response = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            final JsonArrayBuilder builder = Json.createArrayBuilder();
            final PlatformInvoices invoices = this.user.asAdmin()
                .platformInvoices();
            for(final PlatformInvoice invoice : invoices) {
                builder.add(new JsonPlatformInvoice(invoice));
            }
            final JsonArray array = builder.build();
            response = ResponseEntity.ok(array.toString());
        }
        return response;
    }

    /**
     * Get a PlatformInvoices as PDF.
     * @param platformInvoiceId Id of the PlatformInvoice.
     * @return PDF Stream.
     */
    @GetMapping(
        "/invoices/platform/{platformInvoiceId}/pdf"
    )
    public ResponseEntity<StreamingResponseBody> platformInvoicePdf(
        @PathVariable final int platformInvoiceId
    ) {
        final ResponseEntity<StreamingResponseBody> response;
        if(!"admin".equals(this.user.role())) {
            response = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            final PlatformInvoice invoice = this.user.asAdmin()
                .platformInvoices()
                .getById(platformInvoiceId);
            if(invoice == null) {
                response = ResponseEntity.noContent().build();
            } else {
                response = ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                        "Content-Disposition",
                        "inline; filename="
                            + "platform_invoice_"
                            + invoice.serialNumber()
                            + ".pdf"
                    )
                    .body(
                        outputStream -> invoice.toPdf(outputStream)
                    );
            }
        }
        return response;
    }

    /**
     * Get an Invoice as PDF (through the PlatformInvoice).
     * @param platformInvoiceId Id of the PlatformInvoice.
     * @return PDF Stream.
     */
    @GetMapping(
        "/invoices/platform/{platformInvoiceId}/project/pdf"
    )
    public ResponseEntity<StreamingResponseBody> invoicePdf(
        @PathVariable final int platformInvoiceId
    ) {
        final ResponseEntity<StreamingResponseBody> response;
        if(!"admin".equals(this.user.role())) {
            response = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            final PlatformInvoice platform = this.user.asAdmin()
                .platformInvoices()
                .getById(platformInvoiceId);
            if(platform == null) {
                response = ResponseEntity.noContent().build();
            } else {
                final Invoice invoice = platform.invoice();
                if(invoice == null) {
                    response = ResponseEntity.noContent().build();
                } else {
                    response = ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(
                            "Content-Disposition",
                            "inline; filename="
                                + "invoice_SLFX_"
                                + invoice.invoiceId()
                                + ".pdf"
                        )
                        .body(
                            outputStream -> invoice.toPdf(outputStream)
                        );
                }
            }
        }
        return response;
    }

}
