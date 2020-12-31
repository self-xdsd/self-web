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

import com.selfxdsd.api.BillingInfo;
import com.selfxdsd.selfweb.api.input.validators.NoSpecialChars;
import com.selfxdsd.selfweb.api.input.validators.ValidCountry;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Input for the BillingInfo of a Project or of a Contributor.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle HiddenField (200 lines)
 * @checkstyle JavadocVariable (200 lines)
 * @checkstyle JavadocMethod (200 lines)
 */
public final class BillingInfoInput {

    @NoSpecialChars
    @NotBlank(message = "Business type is mandatory!")
    @Size(max=32)
    private String businessType;

    @NoSpecialChars
    @Size(max=256)
    private String legalName;

    @NoSpecialChars
    @Size(max=256)
    private String firstName;

    @NoSpecialChars
    @Size(max=256)
    private String lastName;

    @NotBlank(message = "Country is mandatory!")
    @NoSpecialChars
    @ValidCountry
    private String country;

    @NotBlank(message = "Address is mandatory!")
    @NoSpecialChars
    @Size(max=256)
    private String address;

    @NotBlank(message = "City is mandatory!")
    @NoSpecialChars
    @Size(max=256)
    private String city;

    @NotBlank(message = "Zipcode is mandatory!")
    @NoSpecialChars
    @Size(max=32)
    private String zipcode;

    @NotBlank(message = "E-Mail is mandatory!")
    @Email(message = "Please provide a valid e-mail address.")
    @NoSpecialChars
    @Size(max=256)
    private String email;

    @NoSpecialChars
    @Size(max=512)
    private String taxId;

    @NoSpecialChars
    @Size(max=512)
    private String other;

    @AssertTrue(message = "Business type either individual or company!")
    public boolean isValidBusinessType() {
        final boolean valid;
        if("company".equalsIgnoreCase(this.businessType)
            || "individual".equalsIgnoreCase(this.businessType)) {
            valid = true;
        } else {
            valid = false;
        }
        return valid;
    }


    @AssertTrue(message = "Company must have a legal name!")
    public boolean isValidCompany() {
        final boolean valid;
        if("company".equalsIgnoreCase(this.businessType)) {
            valid = this.legalName != null && !this.legalName.isEmpty();
        } else {
            valid = true;
        }
        return valid;
    }

    @AssertTrue(message = "Individual must have a first name and a last name!")
    public boolean isValidIndividual() {
        final boolean valid;
        if("individual".equalsIgnoreCase(this.businessType)) {
            valid = this.firstName != null && !this.firstName.isEmpty()
                && this.lastName != null && !this.lastName.isEmpty();
        } else {
            valid = true;
        }
        return valid;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(final String businessType) {
        this.businessType = businessType;
    }

    public String getLegalName() {
        return this.legalName;
    }

    public void setLegalName(final String legalName) {
        this.legalName = legalName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
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

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(final String taxId) {
        this.taxId = taxId;
    }

    public String getOther() {
        return this.other;
    }

    public void setOther(final String other) {
        this.other = other;
    }

    /**
     * Billing info for Stripe.
     */
    public static final class StripeBillingInfo implements BillingInfo {

        /**
         * Input from the Stripe wallet formular.
         */
        private BillingInfoInput input;

        /**
         * Ctor.
         * @param input Input from the Stripe wallet form.
         */
        public StripeBillingInfo(final BillingInfoInput input) {
            this.input = input;
        }

        @Override
        public boolean isCompany() {
            return "company".equalsIgnoreCase(this.input.getBusinessType());
        }

        @Override
        public String legalName() {
            final String legalName;
            if(this.isCompany()) {
                legalName = this.input.getLegalName();
            } else {
                legalName = "";
            }
            return legalName;
        }

        @Override
        public String firstName() {
            final String firstName;
            if(!this.isCompany()) {
                firstName = this.input.getFirstName();
            } else {
                firstName = "";
            }
            return firstName;
        }

        @Override
        public String lastName() {
            final String lastName;
            if(!this.isCompany()) {
                lastName = this.input.getLastName();
            } else {
                lastName = "";
            }
            return lastName;
        }

        @Override
        public String country() {
            return this.input.getCountry();
        }

        @Override
        public String address() {
            return this.input.getAddress();
        }

        @Override
        public String city() {
            return this.input.getCity();
        }

        @Override
        public String zipcode() {
            return this.input.getZipcode();
        }

        @Override
        public String email() {
            return this.input.getEmail();
        }

        @Override
        public String taxId() {
            return this.input.getTaxId();
        }

        @Override
        public String other() {
            return this.input.getOther();
        }
    }
}
