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

import com.selfxdsd.selfweb.api.input.RepoInput;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * Unit tests for {@link RepoInput}.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public class RepoValidatorTestCase {
    /**
     * Should accept dot in repo owner's name.
     */
    @Test
    public void acceptOwnerWithDot(){
        final Validator validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

        final RepoInput contract = new RepoInput();
        contract.setOwner("fellahi.ali");
        contract.setName("test");

        MatcherAssert.assertThat(
            validator.validate(contract),
            Matchers.emptyIterable()
        );
    }

    /**
     * Should not accept special chars in repo owner's name.
     * @param specialChar Special character.
     */
    @ParameterizedTest
    @ValueSource(strings = {"+", "$", "*", "%", "#"})
    public void shouldNotAcceptInvalidChars(final String specialChar){
        final Validator validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

        final RepoInput repo = new RepoInput();
        repo.setOwner("owner" + specialChar);
        repo.setName("test");

        MatcherAssert.assertThat(
            validator.validate(repo),
            Matchers.iterableWithSize(1)
        );
    }
}
