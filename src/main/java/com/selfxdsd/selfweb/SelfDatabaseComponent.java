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
package com.selfxdsd.selfweb;

import com.selfxdsd.core.Env;
import com.selfxdsd.storage.Database;
import com.selfxdsd.storage.MySql;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

/**
 * Self Database component.
 * @author criske
 * @version $Id$
 * @since 0.0.6
 */
@Component
@RequestScope
public class SelfDatabaseComponent implements Database {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        SelfDatabaseComponent.class
    );

    /**
     * Database delegate.
     */
    private MySql delegate;

    /**
     * Http request endpoint used for logging only.
     */
    private final String requestEndpoint;

    /**
     * Default constructor for Spring.
     * @param request Http request endpoint used for logging.
     */
    @Autowired
    public SelfDatabaseComponent(final HttpServletRequest request) {
        this.requestEndpoint = request.getRequestURL().toString();
        LOG.debug("Opening MySql connection for http request {} ...",
            this.requestEndpoint);
        delegate = new MySql(
            System.getenv(Env.DB_URL),
            System.getenv(Env.DB_USER),
            System.getenv(Env.DB_PASSWORD)
        ).connect();
    }

    @Override
    public Database connect() {
        this.delegate = this.delegate.connect();
        return this;
    }

    @Override
    public DSLContext jooq() {
        return this.delegate.jooq();
    }

    @Override
    public void close() {
        LOG.debug("Http request {} has finished, closing MySql connection...",
            this.requestEndpoint);
        this.delegate.close();
    }

    @Override
    public String dbms() {
        return this.delegate.dbms();
    }

}
