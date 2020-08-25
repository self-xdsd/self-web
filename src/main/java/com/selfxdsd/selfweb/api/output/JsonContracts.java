package com.selfxdsd.selfweb.api.output;

import com.selfxdsd.api.Contracts;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.stream.StreamSupport;

/**
 * Self Contracts as JsonArray.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class JsonContracts extends AbstractJsonArray{

    /**
     * Ctor.
     * @param contracts Contracts to be converted to JSON.
     */
    public JsonContracts(final Contracts contracts) {
        super(StreamSupport
            .stream(contracts.spliterator(), false)
            .map(JsonContract::new)
            .reduce(Json.createArrayBuilder(), JsonArrayBuilder::add,
                (comb, curr) -> comb)
            .build());
    }
}
