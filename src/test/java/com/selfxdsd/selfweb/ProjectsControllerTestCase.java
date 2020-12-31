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

import com.selfxdsd.api.Project;
import com.selfxdsd.api.Projects;
import com.selfxdsd.api.Self;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Unit tests for {@link ProjectsController}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.2
 */
public final class ProjectsControllerTestCase {

    /**
     * It sets the "managed" Model flag to false if the
     * given Project is not managed by Self.
     */
    @Test
    public void badgePageNotManaged() {
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(
            all.getProjectById("mihai/test", "gitlab")
        ).thenReturn(null);
        final Self self = Mockito.mock(Self.class);
        Mockito.when(self.projects()).thenReturn(all);

        final ProjectsController controller = new ProjectsController(self);
        final Model model = new ExtendedModelMap();
        final String page = controller.badgePage(
            "mihai", "test", "gitlab", model
        );

        MatcherAssert.assertThat(
            page,
            Matchers.equalTo("badge.html")
        );
        MatcherAssert.assertThat(
            model.getAttribute("managed"),
            Matchers.is(Boolean.FALSE)
        );
    }

    /**
     * It sets the "managed" Model flag to true if the
     * given Project is managed by Self.
     */
    @Test
    public void badgePageManaged() {
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(
            all.getProjectById("mihai/test", "gitlab")
        ).thenReturn(Mockito.mock(Project.class));
        final Self self = Mockito.mock(Self.class);
        Mockito.when(self.projects()).thenReturn(all);

        final ProjectsController controller = new ProjectsController(self);
        final Model model = new ExtendedModelMap();
        final String page = controller.badgePage(
            "mihai", "test", "gitlab", model
        );

        MatcherAssert.assertThat(
            page,
            Matchers.equalTo("badge.html")
        );
        MatcherAssert.assertThat(
            model.getAttribute("managed"),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * The default provider (if none is given) should be "github".
     *
     * It sets the "managed" Model flag to true if the
     * given Project is managed by Self.
     */
    @Test
    public void badgePageManagedDefaultProviderGithub() {
        final Projects all = Mockito.mock(Projects.class);
        Mockito.when(
            all.getProjectById("mihai/test", "github")
        ).thenReturn(Mockito.mock(Project.class));
        final Self self = Mockito.mock(Self.class);
        Mockito.when(self.projects()).thenReturn(all);

        final ProjectsController controller = new ProjectsController(self);
        final Model model = new ExtendedModelMap();
        final String page = controller.badgePage(
            "mihai", "test", null, model
        );

        MatcherAssert.assertThat(
            page,
            Matchers.equalTo("badge.html")
        );
        MatcherAssert.assertThat(
            model.getAttribute("managed"),
            Matchers.is(Boolean.TRUE)
        );
    }
}
