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

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Contract.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link JsonContract}.
 * @author Lumi
 */
public class JsonContractTestCase {
    
    /**
     * JsonProject has Id.
     */    
    @Test
    public void hasId(){
    
        final Contract contract = Mockito.mock(Contract.class);
    
        Id id = Mockito.mock(Id.class);
    
        Mockito.when(id.getRepoFullName()).thenReturn("Lumi/Test");
        Mockito.when(id.getContributorUsername()).thenReturn("Lumi3011");
        Mockito.when(id.getProvider()).thenReturn("Github");
        Mockito.when(id.getRole()).thenReturn("dev");
    
        Mockito.when(contract.contractId()).thenReturn(id);
        Mockito.when(contract.hourlyRate())
            .thenReturn(BigDecimal.valueOf(5000));
        Mockito.when(contract.value())
            .thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(contract.markedForRemoval())
            .thenReturn(LocalDateTime.now());
    
        final JsonObject jsonContract = new JsonContract(contract);    

        MatcherAssert.assertThat(
            jsonContract.getJsonObject("id"),
            Matchers.equalTo(Json.createObjectBuilder()
                .add("repoFullName", id.getRepoFullName())
                .add("contributorUsername", id.getContributorUsername())
                .add("provider", id.getProvider())
                .add("role", id.getRole())
                .build())
        );       
    }
    
    /**
     * JsonContract has hourlyRate.
     */
    @Test
    public void hasHourlyRate(){
        final Contract contract = Mockito.mock(Contract.class);
        
        Id id = Mockito.mock(Id.class);
        
        Mockito.when(id.getRepoFullName()).thenReturn("Lumi/Test");
        Mockito.when(id.getContributorUsername()).thenReturn("Lumi3011");
        Mockito.when(id.getProvider()).thenReturn("Github");
        Mockito.when(id.getRole()).thenReturn("dev");
        
        Mockito.when(contract.contractId()).thenReturn(id);
        Mockito.when(contract.hourlyRate())
            .thenReturn(BigDecimal.valueOf(5000));
        Mockito.when(contract.value())
            .thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(contract.markedForRemoval())
            .thenReturn(LocalDateTime.now());
        
        final JsonObject jsonContract = new JsonContract(contract);
        
        MatcherAssert.assertThat(
            jsonContract.getString("hourlyRate"),
            Matchers.equalTo("$50.00")
        );       
    }
    
    /**
     * JsonContract has value.
     */
    @Test
    public void hasValue(){
        final Contract contract = Mockito.mock(Contract.class);
        
        Id id = Mockito.mock(Id.class);
        
        Mockito.when(id.getRepoFullName()).thenReturn("Lumi/Test");
        Mockito.when(id.getContributorUsername()).thenReturn("Lumi3011");
        Mockito.when(id.getProvider()).thenReturn("Github");
        Mockito.when(id.getRole()).thenReturn("dev");
        
        Mockito.when(contract.contractId()).thenReturn(id);
        Mockito.when(contract.hourlyRate())
            .thenReturn(BigDecimal.valueOf(5000));
        Mockito.when(contract.value())
            .thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(contract.markedForRemoval())
            .thenReturn(LocalDateTime.now());
        
        final JsonObject jsonContract = new JsonContract(contract);
        
        MatcherAssert.assertThat(
            jsonContract.getString("value"),
            Matchers.equalTo("$20.00")
        );       
    }
    
    /**
     * JsonContract has markedForRemoval.
     */
    @Test
    public void hasmarkedForRemoval(){
        
        final Contract contract = Mockito.mock(Contract.class);
        
        final LocalDateTime now = LocalDateTime.now();
        
        Id id = Mockito.mock(Id.class);
        
        Mockito.when(id.getRepoFullName()).thenReturn("Lumi/Test");
        Mockito.when(id.getContributorUsername()).thenReturn("Lumi3011");
        Mockito.when(id.getProvider()).thenReturn("Github");
        Mockito.when(id.getRole()).thenReturn("dev");
        
        Mockito.when(contract.contractId()).thenReturn(id);
        Mockito.when(contract.hourlyRate())
            .thenReturn(BigDecimal.valueOf(5000));
        Mockito.when(contract.value())
            .thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(contract.markedForRemoval()).thenReturn(now);
        
        final JsonObject jsonContract = new JsonContract(contract);
        
        MatcherAssert.assertThat(
            jsonContract.getString("markedForRemoval"),
            Matchers.equalTo(now.toString())
        );       
    }
    
    /**
     * JsonContract has null markedForRemoval.
     */
    @Test
    public void hasNullMarkedForRemoval(){
        final Contract contract = Mockito.mock(Contract.class);
        
        Id id = Mockito.mock(Id.class);
        
        Mockito.when(id.getRepoFullName()).thenReturn("Lumi/Test");
        Mockito.when(id.getContributorUsername()).thenReturn("Lumi3011");
        Mockito.when(id.getProvider()).thenReturn("Github");
        Mockito.when(id.getRole()).thenReturn("dev");
        
        Mockito.when(contract.contractId()).thenReturn(id);
        Mockito.when(contract.hourlyRate())
            .thenReturn(BigDecimal.valueOf(5000));
        Mockito.when(contract.value())
            .thenReturn(BigDecimal.valueOf(2000));
        Mockito.when(contract.markedForRemoval()).thenReturn(null);
        
        final JsonObject jsonContract = new JsonContract(contract);
        
        MatcherAssert.assertThat(
            jsonContract.getString("markedForRemoval"),
            Matchers.equalTo("null")
        );       
    }
}
