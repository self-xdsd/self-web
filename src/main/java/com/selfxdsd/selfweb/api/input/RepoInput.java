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
package com.selfxdsd.selfweb.api.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Input for registering a repo as a new project in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle JavadocMethod (200 lines)
 */
public class RepoInput {

    /**
     * Repo's owner.
     */
    @NotBlank(message = "Repo owner is mandatory.")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{1,256}$")
    private String owner;

    /**
     * Repo's name.
     */
    @NotBlank(message = "Repo name is mandatory.")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_\\.]{1,256}$")
    private String name;

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(final String newOwner) {
        this.owner = newOwner;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String newName) {
        this.name = newName;
    }

    public String fullName() {
        return this.owner + "/" + this.name;
    }
}
