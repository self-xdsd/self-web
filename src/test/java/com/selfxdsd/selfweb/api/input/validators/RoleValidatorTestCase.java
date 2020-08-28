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

import com.selfxdsd.selfweb.api.input.ContractInput;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * Unit tests for {@link ContractInput}.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class RoleValidatorTestCase {


    /**
     * Should pass if role is DEV or REV.
     */
    @Test
    public void shouldPassIfRoleIsValid(){
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        final RoleRevDev formDev = new RoleRevDev("dev");
        MatcherAssert.assertThat(validator.validate(formDev),
            Matchers.emptyIterable());
        final RoleRevDev formRev = new RoleRevDev("ReV");
        MatcherAssert.assertThat(validator.validate(formRev),
            Matchers.emptyIterable());
    }

    /**
     * Should not pass if role does not exist.
     */
    @Test
    public void shouldNotPassIfRoleNotExist() {
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        final RoleRevDev form = new RoleRevDev("badRole");
        final Set<ConstraintViolation<RoleRevDev>> errors = validator
            .validate(form);
        MatcherAssert.assertThat(errors, Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(errors.iterator().next()
            .getMessage(), Matchers
            .equalTo("Role 'badRole' doesn't exist or is not allowed!"));
    }

    /**
     * Should not pass if role is not allowed.
     */
    @Test
    public void shouldNotPassIfRoleNotAllowed() {
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        final RoleRevDev form = new RoleRevDev("PM");
        final Set<ConstraintViolation<RoleRevDev>> errors = validator
            .validate(form);
        MatcherAssert.assertThat(errors, Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(errors.iterator().next()
            .getMessage(), Matchers
            .equalTo("Role 'PM' doesn't exist or is not allowed!"));
    }

    /**
     * Throws IllegalArgumentException wrapped in a ValidationException
     * if one of the allowed roles doesn't exist.
     */
    @Test
    public void shouldThrowIfRoleAllowedNotExists(){
        final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        final RoleNotExist form = new RoleNotExist("DEV");
        Assertions.assertThrows(ValidationException.class,
            () -> validator.validate(form));
    }

    /**
     * Form that allows REV or DEV roles.
     * @checkstyle JavadocVariable (100 lines)
     * @checkstyle JavadocMethod (100 lines)
     */
    private static final class RoleRevDev {

        @Role(oneOf = {"DEV", "REV"})
        private final String role;

        private RoleRevDev(final String role) {
            this.role = role;
        }

    }
    /**
     * Form that allows all roles.
     */
    private static final class RoleAll {

        @Role
        private final String role;

        private RoleAll(final String role) {
            this.role = role;
        }

    }
    /**
     * Form that allows a non-existent role.
     */
    private static final class RoleNotExist {

        @Role(oneOf = {"FOO", "DEV"})
        private final String role;

        private RoleNotExist(final String role) {
            this.role = role;
        }

    }
}