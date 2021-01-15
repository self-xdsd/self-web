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

import com.selfxdsd.api.Self;
import com.selfxdsd.api.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

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

}
