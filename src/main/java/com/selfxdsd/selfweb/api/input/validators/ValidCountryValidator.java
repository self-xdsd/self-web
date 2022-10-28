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
package com.selfxdsd.selfweb.api.input.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Validator for {@link ValidCountry}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @checkstyle ReturnCount (200 lines)
 */
public final class ValidCountryValidator
    implements ConstraintValidator<ValidCountry, String> {

    /**
     * Stripe countries. We can only work with countries supported by Stripe.
     */
    private List<String> stripeCountries = List.of(
        "AU", "AT", "BE", "BG", "CA", "CY", "CZ", "DK", "EE", "FI", "FR",
        "DE", "GR", "HK", "HU", "IE", "IT", "JP", "LV", "LT", "LU", "MT",
        "NL", "NZ", "NO", "PL", "PT", "RO", "SG", "SK", "SI", "ES", "SE",
        "CH", "GB", "US", "MY", "MX", "BR", "HR", "GI", "LI", "TH"
    );

    @Override
    public boolean isValid(
        final String value,
        final ConstraintValidatorContext context
    ) {
        for(final String country : this.stripeCountries) {
            if(country.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
