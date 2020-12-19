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
package com.selfxdsd.selfweb.api.input.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Locale;

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
     * Forbidden countries (Stripe regulations): Syria, Iran, N. Korea, Cuba.
     */
    private List<String> forbiddenCountries = List.of(
        "SY", "IR", "KP", "CU"
    );

    @Override
    public boolean isValid(
        final String value,
        final ConstraintValidatorContext context
    ) {
        if(this.forbiddenCountries.contains(value)) {
            return false;
        }
        for(final String country : Locale.getISOCountries()) {
            if(value.equalsIgnoreCase(country)) {
                return true;
            }
        }
        return false;
    }
}
