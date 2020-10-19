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

import com.selfxdsd.api.Task;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.json.JsonObject;
import java.time.LocalDateTime;

/**
 * Unit tests for {@link JsonTask}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class JsonTaskTestCase {

    /**
     * JsonTask has the issueId attribute.
     */
    @Test
    public void hasIssueId() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.issueId()).thenReturn("123");
        Mockito.when(task.assignmentDate()).thenReturn(LocalDateTime.now());
        Mockito.when(task.deadline()).thenReturn(LocalDateTime.now());
        Mockito.when(task.estimation()).thenReturn(60);

        final JsonObject jsonTask = new JsonTask(task);

        MatcherAssert.assertThat(
            jsonTask.getString("issueId"),
            Matchers.equalTo("123")
        );
    }

    /**
     * JsonTask has assignment date.
     */
    @Test
    public void hasAssignmentDate() {
        final LocalDateTime now = LocalDateTime.now();

        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.issueId()).thenReturn("123");
        Mockito.when(task.assignmentDate()).thenReturn(now);
        Mockito.when(task.deadline()).thenReturn(LocalDateTime.now());
        Mockito.when(task.estimation()).thenReturn(60);

        final JsonObject jsonTask = new JsonTask(task);

        MatcherAssert.assertThat(
            jsonTask.getString("assignmentDate"),
            Matchers.equalTo(now.toString())
        );
    }
    
    /**
     * JsonTask has deadline
     */
    
    @Test
    public void hasDeadline(){
    
        final LocalDateTime now = LocalDateTime.now();
        
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.issueId()).thenReturn("123");
        Mockito.when(task.assignmentDate()).thenReturn(LocalDateTime.now());
        Mockito.when(task.deadline()).thenReturn(now);
        Mockito.when(task.estimation()).thenReturn(60);
        
        final JsonObject jsonTask = new JsonTask(task);
        MatcherAssert.assertThat(
            jsonTask.getString("deadline"),
            Matchers.equalTo(now.toString())
        );
        
    }
    
    /**
     * JsonTask has estimation
     */
    
    @Test 
    public void hasEstimation(){
        
        
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.issueId()).thenReturn("123");
        Mockito.when(task.assignmentDate()).thenReturn(LocalDateTime.now());
        Mockito.when(task.deadline()).thenReturn(LocalDateTime.now());
        Mockito.when(task.estimation()).thenReturn(60);
        
        final JsonObject jsonTask = new JsonTask(task);
        MatcherAssert.assertThat(
            jsonTask.getInt("estimation"),
            Matchers.equalTo(60)
        );
        
    }
    
    
    /**
     * JsonTask hasn't the issueId attribute
     */
    
    @Test
    public void hasNullIssueId() {
        final Task task = Mockito.mock(Task.class);
        
        Mockito.when(task.issueId()).thenReturn("null");
        Mockito.when(task.assignmentDate()).thenReturn(LocalDateTime.now());
        Mockito.when(task.deadline()).thenReturn(LocalDateTime.now());
        Mockito.when(task.estimation()).thenReturn(60);

        final JsonObject jsonTask = new JsonTask(task);

        MatcherAssert.assertThat(
            jsonTask.getString("issueId"),
            Matchers.equalTo("null")
        );
    }
    
    /**
     * JsonTask hasn't assignmentDate
     */
    
    @Test
    public void hasNullAssignmentDate() {
        
        final Task task = Mockito.mock(Task.class);
        
        Mockito.when(task.issueId()).thenReturn("null");
        Mockito.when(task.assignmentDate()).thenReturn(null);
        Mockito.when(task.deadline()).thenReturn(LocalDateTime.now());
        Mockito.when(task.estimation()).thenReturn(60);

        final JsonObject jsonTask = new JsonTask(task);

        MatcherAssert.assertThat(
            jsonTask.getString("assignmentDate"),
            Matchers.equalTo("null")
        );
    }
    
     /**
     * JsonTask hasn't deadline
     */
    
        @Test
        public void hasNullDeadline() {
            
        final Task task = Mockito.mock(Task.class);
        
        Mockito.when(task.issueId()).thenReturn("null");
        Mockito.when(task.assignmentDate()).thenReturn(LocalDateTime.now());
        Mockito.when(task.deadline()).thenReturn(null);
        Mockito.when(task.estimation()).thenReturn(60);

        final JsonObject jsonTask = new JsonTask(task);

        MatcherAssert.assertThat(
            jsonTask.getString("deadline"),
            Matchers.equalTo("null")
        );
    }
        
        /**
     * JsonTask hasn't estimation
     */
    
        
        @Test
        public void hasNullEstimation() {
            
        final Task task = Mockito.mock(Task.class);
        
        Mockito.when(task.issueId()).thenReturn("null");
        Mockito.when(task.assignmentDate()).thenReturn(LocalDateTime.now());
        Mockito.when(task.deadline()).thenReturn(LocalDateTime.now());
        Mockito.when(task.estimation()).thenReturn(0);

        final JsonObject jsonTask = new JsonTask(task);

        MatcherAssert.assertThat(
            jsonTask.getInt("estimation"),
            Matchers.equalTo(0)
        );
    }

}
