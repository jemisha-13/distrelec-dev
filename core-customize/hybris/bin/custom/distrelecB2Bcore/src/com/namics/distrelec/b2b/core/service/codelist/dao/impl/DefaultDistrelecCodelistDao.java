/*
 * Copyright 2000-2009 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.dao.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.namics.distrelec.b2b.core.model.DistCodelistModel;
import com.namics.distrelec.b2b.core.service.codelist.dao.DistrelecCodelistDao;
import com.namics.distrelec.b2b.core.service.exception.NotFoundException;

import de.hybris.platform.servicelayer.exceptions.ModelCreationException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Default implementation for <code>DistrelecCodelistDao</code>.
 * 
 * @author daehusir, Distrelec
 */
public class DefaultDistrelecCodelistDao extends AbstractItemDao implements DistrelecCodelistDao {

    @Override
    public DistCodelistModel getDistrelecCodelistEntry(final String code, final String codeListType) {
        if (!StringUtils.hasText(code)) {
            throw new UnknownIdentifierException(codeListType + "Model with code [" + code + "] not found!");
        }
        final String queryString = "SELECT {" + DistCodelistModel.PK + "} FROM {" + codeListType + "} WHERE {" + DistCodelistModel.CODE + "} = ?"
                + DistCodelistModel.CODE;
        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
        query.addQueryParameter(DistCodelistModel.CODE, code);
        final SearchResult<? extends DistCodelistModel> result = getFlexibleSearchService().search(query);
        final int resultCount = result.getTotalCount();
        if (resultCount == 0) {
            throw new NotFoundException(codeListType + "Model with code [" + code + "] not found!");
        } else if (resultCount > 1) {
            throw new NotFoundException(codeListType + "Model with code [" + code + "] is not unique, " + resultCount + " requests found!");
        }
        return result.getResult().get(0);
    }

    @Override
    public List<? extends DistCodelistModel> getAllCodelistEntries(final String codeListType) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {" + DistCodelistModel.PK + "} FROM {" + codeListType + "}");
        return getFlexibleSearchService().<DistCodelistModel> search(query).getResult();
    }

    @Override
    public void insertOrUpdateDistrelecCodelistEntry(final DistCodelistModel codelistEntry) {
        insertOrUpdateDistrelecCodelistEntry(Collections.singletonList(codelistEntry));
    }

    @Override
    public void insertOrUpdateDistrelecCodelistEntry(final List<? extends DistCodelistModel> codelistEntryList) {
        for (final DistCodelistModel codelistEntry : codelistEntryList) {
            if (!StringUtils.hasText(codelistEntry.getCode())) {
                throw new ModelCreationException("It is not possible to create code list entry with code [" + codelistEntry.getCode() + "]!", null);
            }
        }

        getModelService().saveAll(codelistEntryList);
    }

    @SuppressWarnings("unused")
    private List<? extends DistCodelistModel> getDistrelecCodelistEntryModifiedAfterDate(final Date modifiedAfter, final String codeListType) {
        final String queryString;
        final Map<String, Object> queryParameter = new HashMap<String, Object>();
        if (modifiedAfter != null) {
            queryString = "SELECT {" + DistCodelistModel.PK + "} FROM {" + codeListType + "} WHERE {" + DistCodelistModel.MODIFIEDTIME + "} > ?"
                    + DistCodelistModel.MODIFIEDTIME;
            queryParameter.put(DistCodelistModel.MODIFIEDTIME, modifiedAfter);
        } else {
            queryString = "SELECT {" + DistCodelistModel.PK + "} FROM {" + codeListType + "}";
        }
        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString, queryParameter);
        final SearchResult<? extends DistCodelistModel> result = getFlexibleSearchService().search(query);
        if (result.getTotalCount() == 0) {
            throw new UnknownIdentifierException("No " + codeListType + "Model with modified date after [" + modifiedAfter + "] found!");
        }
        return result.getResult();
    }
}
