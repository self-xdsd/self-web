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

import com.selfxdsd.api.PayoutMethod;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link JsonPayoutMethod}.
 * @author Lumi
 */
public class JsonPayoutMethodTestCase {
    
    /**
     * JsonPayoutMethod has type.
     */
    @Test
    public void hasType(){
      
        final PayoutMethod payout = Mockito.mock(PayoutMethod.class);
        
        Mockito.when(payout.type()).thenReturn("Cash");
        Mockito.when(payout.identifier()).thenReturn("identifier1");
        Mockito.when(payout.json())
            .thenReturn(
                Json.createObjectBuilder()
                       .add("accountName", "Stefan Andreea")
                       .add("accountNumber", 1234567890)
                       .build()
            ); 
        
        final JsonObject jsonPayoutMethod = new JsonPayoutMethod(payout);
        
        MatcherAssert.assertThat(
            jsonPayoutMethod.getString("type"),
            Matchers.equalTo("Cash"));
    }
    
    /**
     * JsonPayoutMethod has identifier.
     */
    @Test
    public void hasIdentifier(){
        
        final PayoutMethod payout = Mockito.mock(PayoutMethod.class);
        
        Mockito.when(payout.type()).thenReturn("Cash");
        Mockito.when(payout.identifier()).thenReturn("identifier1");
        Mockito.when(payout.json())
            .thenReturn(
                Json.createObjectBuilder()
                       .add("accountName", "Stefan Andreea")
                       .add("accountNumber", 1234567890)
                       .build()
            ); 
        
        final JsonObject jsonPayoutMethod = new JsonPayoutMethod(payout);
        
        MatcherAssert.assertThat(
            jsonPayoutMethod.getString("identifier"),
            Matchers.equalTo("identifier1"));
    }
    
    /**
     * JsonPayoutMethod has json.
     */
    @Test
    public void hasJson(){
        
        final PayoutMethod payout = Mockito.mock(PayoutMethod.class);
        
        Mockito.when(payout.type()).thenReturn("Cash");
        Mockito.when(payout.identifier()).thenReturn("identifier1");
        Mockito.when(payout.json())
            .thenReturn(
                Json.createObjectBuilder()
                       .add("accountName", "Stefan Andreea")
                       .add("accountNumber", 1234567890)
                       .build()
            );        
        final JsonObject jsonPayoutMethod = new JsonPayoutMethod(payout);
        
        MatcherAssert.assertThat(
            jsonPayoutMethod.getJsonObject("account"),
            Matchers.equalTo(Json.createObjectBuilder()
                .add("accountName", "Stefan Andreea")
                .add("accountNumber", 1234567890)
                .build())
        );
    }
}
