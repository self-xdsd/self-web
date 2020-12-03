package com.selfxdsd.selfweb.api.output;

import com.selfxdsd.api.Contract;

import javax.json.Json;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Self Contract as JsonObject.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class JsonContract extends AbstractJsonObject{

    /**
     * Ctor.
     * @param contract Contract to be converted to JSON.
     */
    public JsonContract(final Contract contract) {
        super(Json.createObjectBuilder()
            .add("id", Json.createObjectBuilder()
                .add("repoFullName", contract.contractId()
                    .getRepoFullName())
                .add("contributorUsername", contract.contractId()
                    .getContributorUsername())
                .add("provider", contract.contractId().getProvider())
                .add("role", contract.contractId().getRole())
                .build())
            .add("hourlyRate", NumberFormat
                .getCurrencyInstance(Locale.GERMANY)
                .format(contract.hourlyRate().divide(BigDecimal.valueOf(100))))
            .add("value", NumberFormat
                .getCurrencyInstance(Locale.GERMANY)
                .format(contract.value().divide(BigDecimal.valueOf(100))))
            .add("revenue", NumberFormat
                .getCurrencyInstance(Locale.GERMANY)
                .format(contract.revenue().divide(BigDecimal.valueOf(100))))
            .add(
                "markedForRemoval",
                String.valueOf(contract.markedForRemoval())
            ).build());
    }
}
