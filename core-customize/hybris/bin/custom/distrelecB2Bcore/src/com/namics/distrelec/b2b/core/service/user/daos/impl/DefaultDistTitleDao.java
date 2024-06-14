/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.user.daos.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.service.user.daos.DistTitleDao;

import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.user.daos.impl.DefaultTitleDao;

public class DefaultDistTitleDao extends DefaultTitleDao implements DistTitleDao {

    @Override
    public TitleModel findTitleBySapCode(final String sapCode) {
        return findUnique(Collections.singletonMap(TitleModel.SAPCODE, (Object) sapCode));
    }

    private TitleModel findUnique(final Map<String, Object> params) {
        final List<TitleModel> results = find(params);
        if (results.size() > 1) {
            throw new AmbiguousIdentifierException("Found " + results.size() + " objects from type " + TitleModel._TYPECODE + " with " + params.toString()
                    + "'");
        }
        return results.isEmpty() ? null : results.get(0);
    }
}
