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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * Unit tests for {@link NoSpecialCharsValidator}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class NoSpecialCharsValidatorTestCase {

    /**
     * An empty-string should pass the validation.
     */
    @Test
    public void emptyStringOk() {
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        final StringClass string = new StringClass("");
        MatcherAssert.assertThat(
            validator.validate(string),
            Matchers.emptyIterable()
        );
    }

    /**
     * A string with no special chars is ok.
     */
    @Test
    public void noSpecialCharsOk() {
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        final StringClass string = new StringClass("some string here");
        MatcherAssert.assertThat(
            validator.validate(string),
            Matchers.emptyIterable()
        );
    }

    /**
     * Dashes, hashes and some other special chars are ok.
     */
    @Test
    public void dashAndHashOk() {
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        final StringClass string = new StringClass(
            "#some chars @re ok, really."
        );
        MatcherAssert.assertThat(
            validator.validate(string),
            Matchers.emptyIterable()
        );
    }

    /**
     * Forbids code, for example markup script.
     */
    @Test
    public void forbidsCode() {
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        final StringClass string = new StringClass(
            "<script>forbidden</script>"
        );
        MatcherAssert.assertThat(
            validator.validate(string),
            Matchers.not(
                Matchers.emptyIterable()
            )
        );
    }

    /**
     * Class with a String for test.
     * @checkstyle JavadocVariable (100 lines)
     * @checkstyle JavadocMethod (100 lines)
     */
    private static final class StringClass {

        @NoSpecialChars
        private final String value;

        private StringClass(final String value) {
            this.value = value;
        }

    }

}
