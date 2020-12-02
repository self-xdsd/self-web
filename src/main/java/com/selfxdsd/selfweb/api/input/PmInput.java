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
import javax.validation.constraints.Positive;

/**
 * Input for registering a new PM.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle JavadocMethod (200 lines)
 */
public class PmInput {

    /**
     * User ID from the Provider.
     */
    @NotBlank(message = "PM's userId is mandatory!")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{1,256}$")
    private String userId;

    /**
     * User name from the Provider.
     */
    @NotBlank(message = "PM's username is mandatory!")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{1,256}$")
    private String username;

    /**
     * Provider name.
     */
    @NotBlank(message = "PM's provider is mandatory!")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{1,256}$")
    private String provider;

    /**
     * Provider access token.
     */
    @NotBlank(message = "PM's token is mandatory!")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{1,256}$")
    private String token;

    /**
     * PM's commission.
     */
    @Positive(message = "PM's commission must be a positive number!")
    private double commission;

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String usrId) {
        this.userId = usrId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String usrname) {
        this.username = usrname;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(final String prov) {
        this.provider = prov;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String tkn) {
        this.token = tkn;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(final double comm) {
        this.commission = comm;
    }
}
