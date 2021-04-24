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

import com.selfxdsd.api.Invoice;
import com.selfxdsd.api.Payment;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Invoice as JSON.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class JsonInvoice extends AbstractJsonObject {

    /**
     * Ctor.
     * @param invoice Invoice to be converted to JsonObject.
     */
    public JsonInvoice(final Invoice invoice) {
        this(invoice, false);
    }

    /**
     * Ctor.
     * @param invoice Invoice to be converted to JsonObject.
     * @param full Get the full Invoice or just the overview?
     */
    public JsonInvoice(final Invoice invoice, final boolean full) {
        super(() -> {
            final JsonObjectBuilder builder = Json.createObjectBuilder();
            if(!full) {
                builder
                    .add("id", invoice.invoiceId())
                    .add("createdAt", String.valueOf(invoice.createdAt()))
                    .add("isPaid", invoice.isPaid())
                    .add(
                        "amount",
                        NumberFormat
                            .getCurrencyInstance(Locale.GERMANY)
                            .format(
                                invoice.amount()
                                    .divide(BigDecimal.valueOf(100))
                            )
                    ).add(
                        "totalAmount",
                        NumberFormat
                            .getCurrencyInstance(Locale.GERMANY)
                            .format(
                                invoice.totalAmount()
                                    .divide(BigDecimal.valueOf(100))
                            )
                    );
            } else {
                builder
                    .add("id", invoice.invoiceId())
                    .add("createdAt", String.valueOf(invoice.createdAt()))
                    .add("isPaid", invoice.isPaid())
                    .add("tasks", new JsonInvoicedTasks(invoice.tasks()))
                    .add(
                        "amount",
                        NumberFormat
                            .getCurrencyInstance(Locale.GERMANY)
                            .format(
                                invoice.amount()
                                    .divide(BigDecimal.valueOf(100))
                            )
                    )
                    .add(
                        "totalAmount",
                        NumberFormat
                            .getCurrencyInstance(Locale.GERMANY)
                            .format(
                                invoice.totalAmount()
                                    .divide(BigDecimal.valueOf(100))
                            )
                    );
            }
            final Payment latest = invoice.latest();
            // attach only if the latest payment is successfully paid.
            // this way the fail reason will not leak to client;
            if (latest != null && latest.status()
                .equals(Payment.Status.SUCCESSFUL)) {
                builder.add(
                    "latestPayment",
                    Json.createObjectBuilder()
                        .add("transactionId", latest.transactionId())
                        .add(
                            "timestamp",
                            String.valueOf(latest.paymentTime())
                        ).build()
                );
            }
            return builder.build();
        });
    }
}
