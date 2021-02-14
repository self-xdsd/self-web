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
package com.selfxdsd.selfweb.api.input;

import com.selfxdsd.selfweb.api.input.validators.Role;

import javax.validation.constraints.*;

/**
 * Input for a Contract.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 * @checkstyle HiddenField (200 lines)
 */
public final class ContractInput {

    /**
     * Contributor username.
     */
    @NotBlank(message = "Contributor's username is mandatory!")
    @Pattern(regexp = "^[a-zA-Z0-9-_\\.]{1,256}$")
    private String username;

    /**
     * Contributor hourly rate in dollars.
     */
    @Min(value = 15)
    @Max(value = 300)
    private double hourlyRate;

    /**
     * Contributor's role.
     */
    @Role(oneOf = {"DEV", "REV", "PO", "ARCH", "QA"})
    private String role;

    /**
     * Set username.
     * @param username Username.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Set hourlyRate in dollars.
     * @param hourlyRate Hourly rate.
     */
    public void setHourlyRate(final double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    /**
     * Set role. Should be "DEV" or "REV".
     * @param role Role.
     */
    public void setRole(final String role) {
        this.role = role.toUpperCase();
    }

    /**
     * Contributor username.
     * @return String.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Contributor hourly rate in dollars.
     * @return Double.
     */
    public double getHourlyRate() {
        return this.hourlyRate;
    }

    /**
     * Contributor's role.
     * @return String.
     */
    public String getRole() {
        return this.role;
    }

}
