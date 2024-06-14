/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.util.ws;

public interface SearchQueryCodec<QUERY> {
    QUERY decodeQuery(String query);

    String encodeQuery(QUERY query);
}
