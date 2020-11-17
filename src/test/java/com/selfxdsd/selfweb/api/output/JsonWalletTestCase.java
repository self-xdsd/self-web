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
package com.selfxdsd.selfweb.api.output;

import com.selfxdsd.api.PaymentMethod;
import com.selfxdsd.api.PaymentMethods;
import com.selfxdsd.api.Wallet;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link JsonWallet}.
 * @author Lumi
 * @todo #166:60min Continue writing unit tests for the JSON output classes.
 *  We can continue with class JsonInvoicedTask.
 */
public class JsonWalletTestCase {
    
    /**
     * JsonWallet has type.
     */
    @Test
    public void hasType(){
        
        final Wallet wallet = Mockito.mock(Wallet.class);
        
        Mockito.when(wallet.type()).thenReturn("123");
        Mockito.when(wallet.active()).thenReturn(Boolean.TRUE);
        Mockito.when(wallet.cash()).thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(wallet.debt()).thenReturn(BigDecimal.valueOf(500));
        Mockito.when(wallet.available()).thenReturn(BigDecimal.valueOf(1500));
        final PaymentMethods methods = Mockito.mock(PaymentMethods.class);
        Mockito.when(methods.spliterator())
            .thenReturn(new ArrayList<PaymentMethod>().spliterator());
        Mockito.when(wallet.paymentMethods()).thenReturn(methods);
        
        final JsonObject jsonWallet = new JsonWallet(wallet);
        
        MatcherAssert.assertThat(
            jsonWallet.getString("type"),
            Matchers.equalTo("123")
        );
        
    }
    
    /**
     * JsonWallet has cash.
     */
    @Test
    public void hasCash(){
        
        final Wallet wallet = Mockito.mock(Wallet.class);
        
        Mockito.when(wallet.type()).thenReturn("123");
        Mockito.when(wallet.active()).thenReturn(Boolean.TRUE);
        Mockito.when(wallet.cash()).thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(wallet.debt()).thenReturn(BigDecimal.valueOf(500));
        Mockito.when(wallet.available()).thenReturn(BigDecimal.valueOf(1500));
        final PaymentMethods methods = Mockito.mock(PaymentMethods.class);
        Mockito.when(methods.spliterator())
            .thenReturn(new ArrayList<PaymentMethod>().spliterator());
        Mockito.when(wallet.paymentMethods()).thenReturn(methods);

        final JsonObject jsonWallet = new JsonWallet(wallet);
        
        MatcherAssert.assertThat(
            jsonWallet.getInt("cash"),
            Matchers.equalTo(20)
        );
        
    }
    /**
     * JsonWallet has debt.
     */
    @Test
    public void hasDebt(){
        
        final Wallet wallet = Mockito.mock(Wallet.class);
        
        Mockito.when(wallet.type()).thenReturn("123");
        Mockito.when(wallet.active()).thenReturn(Boolean.TRUE);
        Mockito.when(wallet.cash()).thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(wallet.debt()).thenReturn(BigDecimal.valueOf(500));
        Mockito.when(wallet.available()).thenReturn(BigDecimal.valueOf(1500));
        final PaymentMethods methods = Mockito.mock(PaymentMethods.class);
        Mockito.when(methods.spliterator())
            .thenReturn(new ArrayList<PaymentMethod>().spliterator());
        Mockito.when(wallet.paymentMethods()).thenReturn(methods);

        final JsonObject jsonWallet = new JsonWallet(wallet);
        
        MatcherAssert.assertThat(
            jsonWallet.getInt("debt"),
            Matchers.equalTo(5)
        );
        
    }
    
    /**
     * JsonWallet has available.
     */
    @Test
    public void hasAvailable(){
        
        final Wallet wallet = Mockito.mock(Wallet.class);
        
        Mockito.when(wallet.type()).thenReturn("123");
        Mockito.when(wallet.active()).thenReturn(Boolean.TRUE);
        Mockito.when(wallet.cash()).thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(wallet.debt()).thenReturn(BigDecimal.valueOf(500));
        Mockito.when(wallet.available()).thenReturn(BigDecimal.valueOf(1500));
        final PaymentMethods methods = Mockito.mock(PaymentMethods.class);
        Mockito.when(methods.spliterator())
            .thenReturn(new ArrayList<PaymentMethod>().spliterator());
        Mockito.when(wallet.paymentMethods()).thenReturn(methods);

        final JsonObject jsonWallet = new JsonWallet(wallet);
        
        MatcherAssert.assertThat(
            jsonWallet.getInt("available"),
            Matchers.equalTo(15)
        );
        
    }
    
    /**
     * JsonWallet is active.
     */
    @Test
    public void isActive(){
        
        final Wallet wallet = Mockito.mock(Wallet.class);
        
        Mockito.when(wallet.type()).thenReturn("123");
        Mockito.when(wallet.active()).thenReturn(Boolean.TRUE);
        Mockito.when(wallet.cash()).thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(wallet.debt()).thenReturn(BigDecimal.valueOf(500));
        Mockito.when(wallet.available()).thenReturn(BigDecimal.valueOf(1500));
        final PaymentMethods methods = Mockito.mock(PaymentMethods.class);
        Mockito.when(methods.spliterator())
            .thenReturn(new ArrayList<PaymentMethod>().spliterator());
        Mockito.when(wallet.paymentMethods()).thenReturn(methods);

        final JsonObject jsonWallet = new JsonWallet(wallet);
        
        MatcherAssert.assertThat(
            jsonWallet.getBoolean("active"),
            Matchers.equalTo(true)
        );
        
    }
    
}
