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

import com.selfxdsd.api.Wallet;

import javax.json.Json;
import java.math.BigDecimal;

/**
 * Wallet in JSON.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle LineLength (100 lines)
 * @checkstyle Indentation (100 lines)
 * @checkstyle ReturnCount (100 lines)
 */
public final class JsonWallet extends AbstractJsonObject {

    /**
     * Ctor.
     * @param wallet Wallet to be converted to JSON.
     */
    public JsonWallet(final Wallet wallet) {
        this(wallet, Boolean.TRUE);
    }

    /**
     * Ctor.
     * @param wallet Wallet to be converted to JSON.
     * @param addPaymentMethods Should we also add the PaymentMethods?
     */
    public JsonWallet(final Wallet wallet, final boolean addPaymentMethods) {
        super(
            () -> {
                if(addPaymentMethods) {
                    return Json.createObjectBuilder()
                        .add("type", wallet.type())
                        .add("active", wallet.active())
                        .add("cash", wallet.cash().divide(BigDecimal.valueOf(100)))
                        .add("debt", wallet.debt().divide(BigDecimal.valueOf(100)))
                        .add(
                            "available",
                            wallet.available().divide(BigDecimal.valueOf(100))
                        )
                        .add(
                            "paymentMethods",
                            new JsonPaymentMethods(wallet.paymentMethods())
                        ).build();
                } else {
                    return Json.createObjectBuilder()
                        .add("type", wallet.type())
                        .add("active", wallet.active())
                        .add("cash", wallet.cash().divide(BigDecimal.valueOf(100)))
                        .add("debt", wallet.debt().divide(BigDecimal.valueOf(100)))
                        .add(
                            "available",
                            wallet.available().divide(BigDecimal.valueOf(100))
                        ).build();
                }
            }
        );
    }
}
