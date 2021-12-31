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
import com.selfxdsd.api.Self;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Projects controller.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
//@Controller
public class ProjectsController {

    /**
     * Self's core.
     */
    private final Self self;

    /**
     * Ctor.
     * @param self Self's core.
     */
//    @Autowired
    public ProjectsController(final Self self) {
        this.self = self;
    }

    /**
     * Serve a Github Project page.
     * @param owner The owner's Username.
     * @param name The repo's name.
     * @param model Model.
     * @return Project page.
     */
//    @GetMapping("/github/{owner}/{name}")
    public String githubProject(
        @PathVariable("owner") final String owner,
        @PathVariable("name") final String name,
        final Model model
    ) {
        model.addAttribute("provider", "github");
        return "project.html";
    }

    /**
     * Serve a Gitlab Project page.
     * @param owner The owner's Username.
     * @param name The repo's name.
     * @param model Model.
     * @return Project page.
     */
//    @GetMapping("/gitlab/{owner}/{name}")
    public String gitlabProject(
        @PathVariable("owner") final String owner,
        @PathVariable("name") final String name,
        final Model model
    ) {
        model.addAttribute("provider", "gitlab");
        return "project.html";
    }


    /**
     * Page served when someone clicks on a project badge.
     * It should just say whether the Project is managed by Self or not.
     * @param owner Owner's login.
     * @param name Repos short name.
     * @param provider Project provider (github, gitlab etc).
     * @param model Spring MVC model.
     * @return Project badge page.
     * @checkstyle LineLength (10 lines)
     * @checkstyle ParameterNumber (10 lines)
     */
    @GetMapping("/p/{owner}/{name}")
    public String badgePage(
        @PathVariable("owner") final String owner,
        @PathVariable("name") final String name,
        @RequestParam(name = "provider", required = false) final String provider,
        final Model model
    ) {
        final String prov;
        if(provider == null || provider.isEmpty()) {
            prov = "github";
        } else {
            prov = provider;
        }
        final Project found = this.self.projects().getProjectById(
            owner + "/" + name, prov
        );
        if (found != null) {
            model.addAttribute("managed", true);
        } else {
            model.addAttribute("managed", false);
        }
        return "badge.html";
    }
}
