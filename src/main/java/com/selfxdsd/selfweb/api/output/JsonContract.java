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
 * @todo #89:30min Remove the project attribute from the JsonContract.
 *  When we are displaying a Contract we are either on the Project Overview
 *  page of the PO or on the Contributor Dashboard of the Contributor.
 *  The PO already has the wallet and all the project info on their page and
 *  the Contributor should not see more info other than the Project name which
 *  is already on the Contract.
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
                .getCurrencyInstance(Locale.US)
                .format(contract.hourlyRate().divide(BigDecimal.valueOf(100))))
            .add("value", NumberFormat
                .getCurrencyInstance(Locale.US)
                .format(contract.value().divide(BigDecimal.valueOf(100))))
            .build());
    }
}
