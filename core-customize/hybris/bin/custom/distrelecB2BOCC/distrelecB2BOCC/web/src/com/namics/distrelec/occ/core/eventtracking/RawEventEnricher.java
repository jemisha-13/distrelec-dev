/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.eventtracking;

import javax.servlet.http.HttpServletRequest;

/**
 * @author stevo.slavic
 *
 */
public interface RawEventEnricher {
    String enrich(String event, HttpServletRequest req);
}
