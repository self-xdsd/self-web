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
package com.selfxdsd.selfweb.api.output;

import com.selfxdsd.api.PlatformInvoice;

import javax.json.Json;
import java.math.BigDecimal;

/**
 * PlatformInvoice in JSON.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class JsonPlatformInvoice extends AbstractJsonObject {

    /**
     * Ctor.
     * @param invoice PlatformInvoice to convert to JSON.
     * @checkstyle LineLength (50 lines)
     */
    public JsonPlatformInvoice(final PlatformInvoice invoice) {
        super(
            Json.createObjectBuilder()
                .add("id", invoice.id())
                .add("number", invoice.serialNumber())
                .add("createdAt", invoice.createdAt().toString())
                .add("commission", invoice.commission().divide(BigDecimal.valueOf(100)))
                .add("vat", invoice.vat().divide(BigDecimal.valueOf(100)))
                .add("total", invoice.totalAmount().divide(BigDecimal.valueOf(100)))
                .add("paidAt", invoice.paymentTime().toString())
                .build()
        );
    }
}
