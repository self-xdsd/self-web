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
package com.selfxdsd.selfweb.api;

import com.selfxdsd.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Lists Contract tasks and label them as active or closed, ordered by newest
 * assignment date.
 * <br/>
 * Should be used only for listing via its iterator, otherwise will
 * throw UnsupportedOperationException.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class StatusTasks implements Tasks {

    /**
     * Contract.
     */
    private final Contract ofContract;

    /**
     * Strategy about how Contract tasks will be iterated.
     */
    private final Function<Contract, Iterator<Task>> iteratorStrategy;

    /**
     * Ctor.
     *
     * @param ofContract Contract.
     * @param iteratorStrategy Strategy about how Contract tasks will be
     *                         iterated.
     */
    private StatusTasks(final Contract ofContract,
                       final
                       Function<Contract, Iterator<Task>> iteratorStrategy) {
        this.ofContract = ofContract;
        this.iteratorStrategy = iteratorStrategy;
    }

    /**
     * Lists all Contract tasks active and closed.
     * @param ofContract Contract.
     * @return All tasks.
     */
    static Tasks all(final Contract ofContract){
        return new StatusTasks(ofContract,
            contract -> Stream.concat(
                StreamSupport.stream(ofContract
                    .tasks().spliterator(), false)
                    .<Task>map(StatusTask.Active::new),
                StreamSupport.stream(ofContract
                    .invoices().spliterator(), false)
                    .flatMap(invoice -> StreamSupport
                        .stream(invoice.tasks().spliterator(), false)
                        .map(invoicedTask -> new StatusTask
                            .Closed(invoicedTask.task(), invoice.invoiceId()))))
                .sorted(Comparator.comparing(Task::assignmentDate))
                .iterator());
    }

    /**
     * Lists active Contract tasks.
     * @param ofContract Contract.
     * @return Active tasks.
     */
    static Tasks active(final Contract ofContract){
        return new StatusTasks(ofContract,
            contract -> StreamSupport.stream(ofContract
                .tasks().spliterator(), false)
                .<Task>map(StatusTask.Active::new)
                .sorted(Comparator.comparing(Task::assignmentDate))
                .iterator());
    }

    /**
     * Lists closed Contract tasks.
     * @param ofContract Contract.
     * @return Closed tasks.
     */
    static Tasks closed(final Contract ofContract){
        return new StatusTasks(ofContract,
            contract -> StreamSupport.stream(ofContract
                .invoices().spliterator(), false)
                .flatMap(invoice -> StreamSupport
                    .stream(invoice.tasks().spliterator(), false)
                    .<Task>map(invoicedTask -> new StatusTask
                        .Closed(invoicedTask.task(), invoice.invoiceId())))
                .sorted(Comparator.comparing(Task::assignmentDate))
                .iterator());
    }

    @Override
    public Task getById(final String issueId,
                        final String repoFullName,
                        final String provider) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Task register(final Issue issue) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Task assign(final Task task,
                       final Contract contract,
                       final int days) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Task unassign(final Task task) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Tasks ofProject(final String repoFullName,
                           final String repoProvider) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Tasks ofContributor(final String username, final String provider) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Tasks ofContract(final Contract.Id id) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Tasks unassigned() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean remove(final Task task) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Iterator<Task> iterator() {
        return this.iteratorStrategy.apply(this.ofContract);
    }

    /**
     * Status task. This can be active or closed.
     */
    public abstract static class StatusTask implements Task {

        /**
         * Delegate.
         */
        private final Task delegate;

        /**
         * Ctor.
         *
         * @param delegate Delegate.
         */
        protected StatusTask(final Task delegate) {
            this.delegate = delegate;
        }

        /**
         * Task status (active or closed).
         * @return String.
         */
        public abstract String status();

        /**
         * Invoice number related to the delegated task.
         * @return String
         */
        public abstract String invoiceNumber();

        @Override
        public String issueId() {
            return delegate.issueId();
        }

        @Override
        public String role() {
            return delegate.role();
        }

        @Override
        public Issue issue() {
            return delegate.issue();
        }

        @Override
        public Project project() {
            return delegate.project();
        }

        @Override
        public Contributor assignee() {
            return delegate.assignee();
        }

        @Override
        public Contract contract() {
            return delegate.contract();
        }

        @Override
        public Task assign(final Contributor contributor) {
            return delegate.assign(contributor);
        }

        @Override
        public Task unassign() {
            return delegate.unassign();
        }

        @Override
        public Resignations resignations() {
            return delegate.resignations();
        }

        @Override
        public LocalDateTime assignmentDate() {
            return delegate.assignmentDate();
        }

        @Override
        public LocalDateTime deadline() {
            return delegate.deadline();
        }

        @Override
        public BigDecimal value() {
            return delegate.value();
        }

        @Override
        public int estimation() {
            return delegate.estimation();
        }

        /**
         * Active task status.
         */
        public static final class Active extends StatusTask {

            /**
             * Ctor.
             *
             * @param delegate Delegate.
             */
            public Active(final Task delegate) {
                super(delegate);
            }

            @Override
            public String status() {
                return "active";
            }

            @Override
            public String invoiceNumber() {
                return "-";
            }

        }

        /**
         * Closed task status.
         */
        public static final class Closed extends StatusTask {


            /**
             * Invoice id.
             */
            private final int invoiceId;

            /**
             * Ctor.
             *
             * @param delegate Delegate.
             * @param invoiceId Invoice id.
             */
            public Closed(final Task delegate, final int invoiceId) {
                super(delegate);
                this.invoiceId = invoiceId;
            }

            @Override
            public String status() {
                return "closed";
            }

            @Override
            public String invoiceNumber() {
                return Integer.toString(invoiceId);
            }
        }
    }
}
