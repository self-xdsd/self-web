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

import com.selfxdsd.api.InvoicedTask;
import com.selfxdsd.api.Task;
import java.math.BigDecimal;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link JsonInvoicedTask}.
 * @author Lumi
 */
public class JsonInvoicedTaskTestCase {    
    
    /**
     * JsonInvoicedTask has invoicedTaskId.
     */
    @Test
    public void hasInvoicedTaskId(){
        
        final InvoicedTask invoicedTask = Mockito.mock(InvoicedTask.class);
        
        Mockito.when(invoicedTask.invoicedTaskId()).thenReturn(123);
        
        Task task = Mockito.mock(Task.class);
        
        Mockito.when(task.issueId()).thenReturn("123");
        
        Mockito.when(invoicedTask.task()).thenReturn(task);      
        
        Mockito.when(task.estimation()).thenReturn(2);
        
        Mockito.when(invoicedTask.task()).thenReturn(task);
        
        Mockito.when(invoicedTask.value()).thenReturn(BigDecimal.valueOf(100));
        
        Mockito.when(invoicedTask.projectCommission())
            .thenReturn(BigDecimal.valueOf(100));

        Mockito.when(invoicedTask.contributorCommission())
            .thenReturn(BigDecimal.valueOf(30));

        final JsonObject jsonInvoicedTask = new JsonInvoicedTask(invoicedTask);
        
        MatcherAssert.assertThat(
            jsonInvoicedTask.getInt("invoicedTaskId"),
            Matchers.equalTo(123)
        );        
        
    }
    
    /**
     * JsonInvoicedTask has issueId.
     */
    @Test
    public void hasIssueId(){
        final InvoicedTask invoicedTask = Mockito.mock(InvoicedTask.class);
        
        Mockito.when(invoicedTask.invoicedTaskId()).thenReturn(123);
        
        Task task = Mockito.mock(Task.class);
        
        Mockito.when(task.issueId()).thenReturn("123");
        
        Mockito.when(invoicedTask.task()).thenReturn(task);      
        
        Mockito.when(task.estimation()).thenReturn(2);
        
        Mockito.when(invoicedTask.task()).thenReturn(task);
        
        Mockito.when(invoicedTask.value()).thenReturn(BigDecimal.valueOf(100));
        
        Mockito.when(invoicedTask.projectCommission())
            .thenReturn(BigDecimal.valueOf(100));

        Mockito.when(invoicedTask.contributorCommission())
            .thenReturn(BigDecimal.valueOf(30));

        final JsonObject jsonInvoicedTask = new JsonInvoicedTask(invoicedTask);
        
        MatcherAssert.assertThat(
            jsonInvoicedTask.getString("issueId"),
            Matchers.equalTo("123")
        );
    }
    
    /**
     * JsonInvoicedTask has estimation.
     */
    @Test
    public void hasEstimation(){
        final InvoicedTask invoicedTask = Mockito.mock(InvoicedTask.class);
        
        Mockito.when(invoicedTask.invoicedTaskId()).thenReturn(123);
        
        Task task = Mockito.mock(Task.class);
        
        Mockito.when(task.issueId()).thenReturn("123");
        
        Mockito.when(invoicedTask.task()).thenReturn(task);      
        
        Mockito.when(task.estimation()).thenReturn(2);
        
        Mockito.when(invoicedTask.task()).thenReturn(task);
        
        Mockito.when(invoicedTask.value()).thenReturn(BigDecimal.valueOf(100));
        
        Mockito.when(invoicedTask.projectCommission())
            .thenReturn(BigDecimal.valueOf(100));

        Mockito.when(invoicedTask.contributorCommission())
            .thenReturn(BigDecimal.valueOf(30));
        
        final JsonObject jsonInvoicedTask = new JsonInvoicedTask(invoicedTask);
        
        MatcherAssert.assertThat(
            jsonInvoicedTask.getInt("estimation"),
            Matchers.equalTo(2)
        );
    }
    
    /**
     * JsonInvoicedTask has value.
     */
    @Test
    public void hasValue(){
        final InvoicedTask invoicedTask = Mockito.mock(InvoicedTask.class);
        
        Mockito.when(invoicedTask.invoicedTaskId()).thenReturn(123);
        
        Task task = Mockito.mock(Task.class);
        
        Mockito.when(task.issueId()).thenReturn("123");
        
        Mockito.when(invoicedTask.task()).thenReturn(task);      
        
        Mockito.when(task.estimation()).thenReturn(2);
        
        Mockito.when(invoicedTask.task()).thenReturn(task);
        
        Mockito.when(invoicedTask.value()).thenReturn(BigDecimal.valueOf(1000));
        
        Mockito.when(invoicedTask.projectCommission())
            .thenReturn(BigDecimal.valueOf(100));

        Mockito.when(invoicedTask.contributorCommission())
            .thenReturn(BigDecimal.valueOf(30));
        
        final JsonObject jsonInvoicedTask = new JsonInvoicedTask(invoicedTask);
        
        MatcherAssert.assertThat(
            jsonInvoicedTask.getInt("value"),
            Matchers.equalTo(10)
        );
    }
    
    /**
     * JsonInvoicedTask has commission.
     */
    @Test
    public void hasCommission(){
        final InvoicedTask invoicedTask = Mockito.mock(InvoicedTask.class);
        
        Mockito.when(invoicedTask.invoicedTaskId()).thenReturn(123);
        
        Task task = Mockito.mock(Task.class);
        
        Mockito.when(task.issueId()).thenReturn("123");
        
        Mockito.when(invoicedTask.task()).thenReturn(task);      
        
        Mockito.when(task.estimation()).thenReturn(2);
        
        Mockito.when(invoicedTask.task()).thenReturn(task);
        
        Mockito.when(invoicedTask.value()).thenReturn(BigDecimal.valueOf(100));
        
        Mockito.when(invoicedTask.projectCommission())
            .thenReturn(BigDecimal.valueOf(3000));

        Mockito.when(invoicedTask.contributorCommission())
            .thenReturn(BigDecimal.valueOf(30));
        
        final JsonObject jsonInvoicedTask = new JsonInvoicedTask(invoicedTask);
        
        MatcherAssert.assertThat(
            jsonInvoicedTask.getInt("commission"),
            Matchers.equalTo(30)
        );
    }

    /**
     * JsonInvoicedTask has contributor commission.
     */
    @Test
    public void hasContributorCommission(){
        final InvoicedTask invoicedTask = Mockito.mock(InvoicedTask.class);

        Mockito.when(invoicedTask.invoicedTaskId()).thenReturn(123);

        Task task = Mockito.mock(Task.class);

        Mockito.when(task.issueId()).thenReturn("123");

        Mockito.when(invoicedTask.task()).thenReturn(task);

        Mockito.when(task.estimation()).thenReturn(2);

        Mockito.when(invoicedTask.task()).thenReturn(task);

        Mockito.when(invoicedTask.value()).thenReturn(BigDecimal.valueOf(100));

        Mockito.when(invoicedTask.projectCommission())
            .thenReturn(BigDecimal.valueOf(3000));

        Mockito.when(invoicedTask.contributorCommission())
            .thenReturn(BigDecimal.valueOf(2000));

        final JsonObject jsonInvoicedTask = new JsonInvoicedTask(invoicedTask);

        MatcherAssert.assertThat(
            jsonInvoicedTask.getInt("contributorCommission"),
            Matchers.equalTo(20)
        );
    }
}
