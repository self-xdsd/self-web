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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * Unit tests for {@link ValidCountryValidator}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ValidCountryValidatorTestCase {

    /**
     * Complains on empty String.
     */
    @Test
    public void emptyStringFails() {
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        final StringCountry string = new StringCountry("");
        MatcherAssert.assertThat(
            validator.validate(string),
            Matchers.not(
                Matchers.emptyIterable()
            )
        );
    }

    /**
     * A country code should work in small case as well.
     */
    @Test
    public void smallCaseWorks() {
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        final StringCountry string = new StringCountry("us");
        MatcherAssert.assertThat(
            validator.validate(string),
            Matchers.emptyIterable()
        );
    }

    /**
     * Some giberish fails.
     */
    @Test
    public void giberishFails() {
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        final StringCountry string = new StringCountry("Bla bla");
        MatcherAssert.assertThat(
            validator.validate(string),
            Matchers.not(
                Matchers.emptyIterable()
            )
        );
    }

    /**
     * Some correct countries are rejected.
     */
    @Test
    public void forbiddenCountriesNotAllowed() {
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        MatcherAssert.assertThat(
            validator.validate(
                new StringCountry("SY")
            ),
            Matchers.not(
                Matchers.emptyIterable()
            )
        );
        MatcherAssert.assertThat(
            validator.validate(
                new StringCountry("IR")
            ),
            Matchers.not(
                Matchers.emptyIterable()
            )
        );
        MatcherAssert.assertThat(
            validator.validate(
                new StringCountry("KP")
            ),
            Matchers.not(
                Matchers.emptyIterable()
            )
        );
        MatcherAssert.assertThat(
            validator.validate(
                new StringCountry("CU")
            ),
            Matchers.not(
                Matchers.emptyIterable()
            )
        );
    }

    /**
     * Some correct country codes are passing.
     */
    @Test
    public void allowsCountries() {
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        MatcherAssert.assertThat(
            validator.validate(
                new StringCountry("RO")
            ),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            validator.validate(
                new StringCountry("US")
            ),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            validator.validate(
                new StringCountry("GB")
            ),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            validator.validate(
                new StringCountry("FR")
            ),
            Matchers.emptyIterable()
        );
    }

    /**
     * Class with a String country for test.
     * @checkstyle JavadocVariable (100 lines)
     * @checkstyle JavadocMethod (100 lines)
     */
    private static final class StringCountry {

        @ValidCountry
        private final String country;

        private StringCountry(final String country) {
            this.country = country;
        }

    }
}
