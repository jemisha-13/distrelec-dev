package com.namics.distrelec.b2b.core.pim.config.dao.impl;

import com.namics.distrelec.b2b.core.model.pim.config.PimImportConfigModel;
import com.namics.distrelec.b2b.core.pim.config.dao.PimImportConfigDao;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.PIM_IMPORT_CONFIG_CODE;

public class DefaultPimImportConfigDao implements PimImportConfigDao {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPimImportConfigDao.class);

    private static final String GET_PIM_IMPORT_CONFIG = "SELECT {" + PimImportConfigModel.PK + "} FROM {" + PimImportConfigModel._TYPECODE + "}"
                                                          + " WHERE {" + PimImportConfigModel.CODE + "}=?" + PimImportConfigModel.CODE;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public PimImportConfigModel getPimImportConfig() {
        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(GET_PIM_IMPORT_CONFIG);
        searchQuery.addQueryParameter(PimImportConfigModel.CODE, PIM_IMPORT_CONFIG_CODE);
        try {
            return getFlexibleSearchService().searchUnique(searchQuery);
        } catch (ModelNotFoundException e) {
            return null;
        } catch (AmbiguousIdentifierException e) {
            LOG.error("More than one PIM import config found!");
            return null;
        }
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
