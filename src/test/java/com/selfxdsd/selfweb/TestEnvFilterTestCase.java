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
package com.selfxdsd.selfweb;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Unit tests for {@link TestEnvFilter}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class TestEnvFilterTestCase {

    /**
     * Sets the testEnvironment attribute to true if
     * we are in test environment.
     * @throws IOException If something goes wrong.
     * @throws ServletException If something goes wrong.
     */
    @Test
    public void testEnvTrue() throws IOException, ServletException {
        final ServletRequest request = Mockito.mock(ServletRequest.class);
        final ServletResponse response = Mockito.mock(ServletResponse.class);
        final FilterChain chain = Mockito.mock(FilterChain.class);
        final TestEnvFilter filter = new TestEnvFilter(() -> true);
        filter.doFilter(request, response, chain);
        Mockito.verify(request, Mockito.times(1)).setAttribute(
            "testEnvironment", "true"
        );
        Mockito.verify(chain, Mockito.times(1)).doFilter(
            request, response
        );
    }

    /**
     * Does not set anything on the request
     * when we are NOT in test environment.
     * @throws IOException If something goes wrong.
     * @throws ServletException If something goes wrong.
     */
    @Test
    public void testEnvFalse() throws IOException, ServletException {
        final ServletRequest request = Mockito.mock(ServletRequest.class);
        final ServletResponse response = Mockito.mock(ServletResponse.class);
        final FilterChain chain = Mockito.mock(FilterChain.class);
        final TestEnvFilter filter = new TestEnvFilter(() -> false);
        filter.doFilter(request, response, chain);

        Mockito.verify(request, Mockito.times(0)).setAttribute(
            Mockito.anyString(), Mockito.anyString()
        );
        Mockito.verify(chain, Mockito.times(1)).doFilter(
            request, response
        );
    }

}
