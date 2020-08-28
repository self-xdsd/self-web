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

import com.selfxdsd.api.Contract;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of a role validator. It checks
 * if input matches existing {@link Contract.Roles}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class RoleValidator implements
    ConstraintValidator<Role, String> {

    /**
     * Allowed {@link Contract.Roles}.
     */
    private final List<String> allowedRoles;

    /**
     * All {@link Contract.Roles}.
     */
    private final List<String> allRoles;

    /**
     * Constructor.
     */
    public RoleValidator() {
        this.allowedRoles = new ArrayList<>();
        this.allRoles = Arrays
            .stream(Contract.Roles.class.getDeclaredFields())
            .map(field -> {
                try {
                    return (String) field.get(null);
                } catch (final IllegalAccessException exception) {
                    throw new RuntimeException(exception);
                }
            }).collect(Collectors.toList());
    }

    @Override
    public void initialize(final Role constraintAnnotation) {
        final String[] oneOf = constraintAnnotation.oneOf();
        if (oneOf.length == 0) {
            this.allowedRoles.addAll(allRoles);
        } else {
            for (final String role : oneOf) {
                final String formattedRole = role.trim().toUpperCase();
                if (this.allRoles.contains(formattedRole)) {
                    this.allowedRoles.add(formattedRole);
                } else {
                    throw new IllegalArgumentException("The allowed role '"
                        + formattedRole + "' does not exist!");
                }
            }
        }
    }

    @Override
    public boolean isValid(final String role,
                           final ConstraintValidatorContext context) {
        return allowedRoles.contains(role.trim().toUpperCase());
    }

}
