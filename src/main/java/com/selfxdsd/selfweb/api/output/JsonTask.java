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
package com.selfxdsd.selfweb.api.output;
import com.selfxdsd.api.Task;
import com.selfxdsd.selfweb.api.StatusTasks;

import javax.json.Json;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Self Task as JSON.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class JsonTask extends AbstractJsonObject {

    /**
     * Ctor.
     * @param task Task to be converted to JSON.
     */
    public JsonTask(final Task task) {
        super(
            Json.createObjectBuilder()
                .add("issueId", task.issueId())
                .add("invoiceNumber", Optional.of(task)
                    .filter(t -> t instanceof StatusTasks.StatusTask)
                    .map(t -> ((StatusTasks.StatusTask) t).invoiceNumber())
                    .orElse("null"))
                .add("assignmentDate", String.valueOf(task.assignmentDate()))
                .add("deadline", String.valueOf(task.deadline()))
                .add("estimation", task.estimation())
                .add("value", task.value().divide(BigDecimal.valueOf(100)))
                .add("status", Optional.of(task)
                    .filter(t -> t instanceof StatusTasks.StatusTask)
                    .map(t -> ((StatusTasks.StatusTask) t).status())
                    .orElse("null"))
                .build()
        );
    }
}
