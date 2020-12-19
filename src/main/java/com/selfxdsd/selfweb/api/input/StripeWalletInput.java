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

import com.selfxdsd.selfweb.api.input.validators.NoSpecialChars;
import com.selfxdsd.selfweb.api.input.validators.ValidCountry;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Input for a new Stripe wallet (project billing info).
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle HiddenField (200 lines)
 * @checkstyle JavadocVariable (200 lines)
 * @checkstyle JavadocMethod (200 lines)
 */
public final class StripeWalletInput {

    @NotBlank(message = "Legal name is mandatory!")
    @NoSpecialChars
    private String legalName;

    @NotBlank(message = "Country is mandatory!")
    @NoSpecialChars
    @ValidCountry
    private String country;

    @NotBlank(message = "Address is mandatory!")
    @NoSpecialChars
    private String address;

    @NotBlank(message = "City is mandatory!")
    @NoSpecialChars
    private String city;

    @NotBlank(message = "Zipcode is mandatory!")
    @NoSpecialChars
    private String zipcode;

    @NotBlank(message = "E-Mail is mandatory!")
    @Email(message = "Please provide a valid e-mail address.")
    @NoSpecialChars
    private String email;

    @NoSpecialChars
    private String other;

    public String getLegalName() {
        return this.legalName;
    }

    public void setLegalName(final String legalName) {
        this.legalName = legalName;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getZipcode() {
        return this.zipcode;
    }

    public void setZipcode(final String zipcode) {
        this.zipcode = zipcode;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getOther() {
        return this.other;
    }

    public void setOther(final String other) {
        this.other = other;
    }
}
