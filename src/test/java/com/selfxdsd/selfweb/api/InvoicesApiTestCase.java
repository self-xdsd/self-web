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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.json.Json;
import javax.json.JsonArray;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

/**
 * Unit tests for {@link InvoicesApi}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class InvoicesApiTestCase {

    /**
     * GET /invoices is forbidden for non-admin users.
     */
    @Test
    public void getInvoicesForbiddenToNonAdmin() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("user");
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.platformInvoices()).thenThrow(
            new IllegalStateException("Should not be called.")
        );
        MatcherAssert.assertThat(
            new InvoicesApi(user, core).invoices().getStatusCode(),
            Matchers.equalTo(HttpStatus.FORBIDDEN)
        );
    }

    /**
     * GET PlatformInvoice as PDF is forbidden for non-admin users.
     */
    @Test
    public void getPlatformInvoiceForbiddenToNonAdmin() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("user");
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.platformInvoices()).thenThrow(
            new IllegalStateException("Should not be called.")
        );
        MatcherAssert.assertThat(
            new InvoicesApi(user, core).platformInvoicePdf(1).getStatusCode(),
            Matchers.equalTo(HttpStatus.FORBIDDEN)
        );
    }

    /**
     * GET Invoice as PDF is forbidden for non-admin users.
     */
    @Test
    public void getInvoiceForbiddenToNonAdmin() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("user");
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.platformInvoices()).thenThrow(
            new IllegalStateException("Should not be called.")
        );
        MatcherAssert.assertThat(
            new InvoicesApi(user, core).invoicePdf(1).getStatusCode(),
            Matchers.equalTo(HttpStatus.FORBIDDEN)
        );
    }

    /**
     * It can return the platform invoices as json array.
     */
    @Test
    public void getPlatformInvoicesAsJsonArray() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("admin");

        final PlatformInvoices all = Mockito.mock(PlatformInvoices.class);
        final Iterator<PlatformInvoice> iterator = List.of(
            this.mockPlatformInvoice(1),
            this.mockPlatformInvoice(2),
            this.mockPlatformInvoice(3)
        ).iterator();
        Mockito.when(all.iterator()).thenReturn(iterator);
        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.platformInvoices()).thenReturn(all);

        final JsonArray array = Json.createReader(
            new StringReader(
                new InvoicesApi(user, core).invoices().getBody()
            )
        ).readArray();

        MatcherAssert.assertThat(array, Matchers.iterableWithSize(3));
    }

    /**
     * Returns NO CONTENT if the platform invoice is missing.
     */
    @Test
    public void missingPlatformInvoice() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("admin");

        final PlatformInvoices all = Mockito.mock(PlatformInvoices.class);
        Mockito.when(all.getById(1)).thenReturn(null);

        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.platformInvoices()).thenReturn(all);

        MatcherAssert.assertThat(
            new InvoicesApi(user, core).platformInvoicePdf(1).getStatusCode(),
            Matchers.equalTo(HttpStatus.NO_CONTENT)
        );
    }

    /**
     * Returns the found PlatformInvoice.
     */
    @Test
    public void foundPlatformInvoice() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("admin");

        final PlatformInvoices all = Mockito.mock(PlatformInvoices.class);
        final PlatformInvoice found = this.mockPlatformInvoice(1);
        Mockito.when(all.getById(1)).thenReturn(found);

        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.platformInvoices()).thenReturn(all);

        final ResponseEntity<StreamingResponseBody> resp = new InvoicesApi(
            user, core
        ).platformInvoicePdf(1);
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
    }

    /**
     * Returns NO CONTENT if the parent PlatformInvoice of an Invoice
     * is missing.
     */
    @Test
    public void missingParentPlatformInvoice() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("admin");

        final PlatformInvoices all = Mockito.mock(PlatformInvoices.class);
        Mockito.when(all.getById(1)).thenReturn(null);

        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.platformInvoices()).thenReturn(all);

        MatcherAssert.assertThat(
            new InvoicesApi(user, core).invoicePdf(1).getStatusCode(),
            Matchers.equalTo(HttpStatus.NO_CONTENT)
        );
    }

    /**
     * Returns NO CONTENT if the Invoice
     * is missing.
     */
    @Test
    public void missingInvoice() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("admin");

        final PlatformInvoices all = Mockito.mock(PlatformInvoices.class);
        final PlatformInvoice platformInvoice = this.mockPlatformInvoice(1);
        Mockito.when(platformInvoice.invoice()).thenReturn(null);
        Mockito.when(all.getById(1)).thenReturn(platformInvoice);

        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.platformInvoices()).thenReturn(all);

        MatcherAssert.assertThat(
            new InvoicesApi(user, core).invoicePdf(1).getStatusCode(),
            Matchers.equalTo(HttpStatus.NO_CONTENT)
        );
    }

    /**
     * Returns the found Invoice.
     */
    @Test
    public void foundInvoice() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.role()).thenReturn("admin");

        final PlatformInvoices all = Mockito.mock(PlatformInvoices.class);
        final PlatformInvoice platformInvoice = this.mockPlatformInvoice(1);
        final Invoice found = Mockito.mock(Invoice.class);
        Mockito.when(platformInvoice.invoice()).thenReturn(found);
        Mockito.when(all.getById(1)).thenReturn(platformInvoice);

        final Self core = Mockito.mock(Self.class);
        Mockito.when(core.platformInvoices()).thenReturn(all);

        final ResponseEntity<StreamingResponseBody> resp = new InvoicesApi(
            user, core
        ).invoicePdf(1);
        MatcherAssert.assertThat(
            resp.getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
    }

    /**
     * Mock a PlatformInvoice.
     * @param id Integer ID.
     * @return PlatformInvoice.
     */
    private PlatformInvoice mockPlatformInvoice(final int id) {
        final PlatformInvoice invoice = Mockito.mock(PlatformInvoice.class);
        Mockito.when(invoice.id()).thenReturn(id);
        Mockito.when(invoice.serialNumber()).thenReturn("SLF" + id);
        Mockito.when(invoice.createdAt()).thenReturn(LocalDateTime.now());
        Mockito.when(invoice.billedTo()).thenReturn("mihai");
        Mockito.when(invoice.billedBy()).thenReturn("Self XDSD");
        Mockito.when(invoice.commission()).thenReturn(
            BigDecimal.valueOf(100)
        );
        Mockito.when(invoice.vat()).thenReturn(
            BigDecimal.valueOf(19)
        );
        Mockito.when(invoice.totalAmount()).thenReturn(
            BigDecimal.valueOf(119)
        );
        Mockito.when(invoice.transactionId()).thenReturn("transaction123");
        Mockito.when(invoice.paymentTime()).thenReturn(LocalDateTime.now());
        return invoice;
    }
}
