package com.selfxdsd.selfweb.api.output;

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Contracts;

import javax.json.Json;
import javax.json.JsonArrayBuilder;

/**
 * Self Contracts as JsonArray.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 * @checkstyle Indentation (100 lines)
 */
public final class JsonContracts extends AbstractJsonArray{

    /**
     * Ctor.
     * @param contracts Contracts to be converted to JSON.
     */
    public JsonContracts(final Contracts contracts) {
        this(contracts, Boolean.FALSE);
    }

    /**
     * Ctor.
     * @param contracts Contracts to be turned to JSON.
     * @param withWalletType Add the Project wallet type of each Contract? This
     *  was introduced for the ContributorApi, on the front-end we want to tell
     *  the Contributor what kind of Wallet is used in each of the Projects
     *  they are working in.
     */
    public JsonContracts(
        final Contracts contracts,
        final boolean withWalletType
    ){
        super(
            () -> {
                JsonArrayBuilder builder = Json.createArrayBuilder();
                for(final Contract contract : contracts) {
                    builder = builder.add(
                        new JsonContract(contract, withWalletType)
                    );
                }
                return builder.build();
            }
        );
    }

}
