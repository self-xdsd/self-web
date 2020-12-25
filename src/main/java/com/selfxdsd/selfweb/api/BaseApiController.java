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
package com.selfxdsd.selfweb.api;

import com.selfxdsd.api.exceptions.InvoiceException;
import com.selfxdsd.api.exceptions.WalletPaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * Base API Controller.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
@RequestMapping("/api")
public class BaseApiController {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        BaseApiController.class
    );

    /**
     * Handle validation errors, send back a map
     * of the fields and their errors.
     * @param exception Validation exception.
     * @return Map of errors.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Map<String, String> handleValidationExceptions(
        final BindException exception) {
        final Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(
            error -> errors.put(
                ((FieldError) error).getField(),
                error.getDefaultMessage()
            )
        );
        return errors;
    }

    /**
     * Handle constraint violation errors, send back a map
     * of the fields and their errors.
     * @param exception Constraint violation exception.
     * @return Map of errors.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleConstraintViolationExceptions(
        final ConstraintViolationException exception) {
        final Map<String, String> errors = new HashMap<>();
        exception.getConstraintViolations().forEach(
            error -> {
                //the "field" is last the last node in path
                //ex: for path ".updateCash.limit", field will be "limit"
                final String field = StreamSupport
                    .stream(error.getPropertyPath().spliterator(), false)
                    .map(Path.Node::getName)
                    .reduce((prev, curr) -> curr)
                    .orElse("unknown_field_" + error.hashCode());
                errors.put(
                    field,
                    error.getMessage()
                );
            }
        );
        return errors;
    }

    /**
     * Return any WalletPaymentException as PRECONDITION FAILED.
     * @param exception Exception.
     * @return Exception toString returns it in JSON format.
     */
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(WalletPaymentException.class)
    public String handlePaymentException(
        final WalletPaymentException exception
    ) {
        return exception.toString();
    }

    /**
     * Invoice already paid exception as PRECONDITION FAILED.
     * @param exception Exception.
     * @return Exception toString returns it in JSON format.
     */
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(InvoiceException.AlreadyPaid.class)
    public String handleInvoiceAlreadyPaidException(
        final InvoiceException.AlreadyPaid exception
    ) {
        return exception.toString();
    }
    /**
     * Custom exception message for internal server errors (500) to prevent
     * leaking to frontend exception messages that might contain sensitive
     * information (like database internals).
     * @param exception Caught exception.
     * @return String.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleInternalSeverExceptions(
        final Exception exception
    ){
        LOG.error("Caught unexpected exception", exception);
        return "Something went wrong while executing this request.";
    }

}
