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
package com.selfxdsd.selfweb.api;

import com.selfxdsd.api.Contributor;
import com.selfxdsd.api.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

/**
 * Unit tests for {@link ContributorApi}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ContributorsApiTestCase {

    /**
     * If the authenticated User is not a contributor,
     * it should return 204 NO CONTENT.
     */
    @Test
    public void returnsNoContentOnMissingContributor() {
        final User authenticated = Mockito.mock(User.class);
        Mockito.when(authenticated.asContributor()).thenReturn(null);
        final ContributorApi api = new ContributorApi(authenticated);
        MatcherAssert.assertThat(
            api.contributor().getStatusCode(),
            Matchers.equalTo(HttpStatus.NO_CONTENT)
        );
    }

    /**
     * If the authenticated User is a contributor,
     * it should return 200 OK.
     */
    @Test
    public void returnsOkOnFoundContributor() {
        final User authenticated = Mockito.mock(User.class);
        Mockito.when(authenticated.asContributor()).thenReturn(
            Mockito.mock(Contributor.class)
        );
        final ContributorApi api = new ContributorApi(authenticated);
        MatcherAssert.assertThat(
            api.contributor().getStatusCode(),
            Matchers.equalTo(HttpStatus.OK)
        );
    }
}
