package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Fusion.ALLOWED_CHARACTERS_FIELDNAME;
import static de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes.DOUBLE;

import java.text.DecimalFormat;

import com.distrelec.b2b.core.search.data.PimWebUseField;
import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.google.gson.Gson;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.service.unit.UnitConversionService;

import de.hybris.platform.enumeration.EnumerationService;

public abstract class AbstractDistPimWebUseValueResolver extends AbstractDistProductValueResolver {

    protected static final DecimalFormat decimalFormat = new DecimalFormat();

    private UnitConversionService unitConversionService;

    private Gson gson;

    protected AbstractDistPimWebUseValueResolver(DistCMSSiteDao distCMSSiteDao,
                                                 DistProductSearchExportDAO distProductSearchExportDAO,
                                                 UnitConversionService unitConversionService,
                                                 EnumerationService enumerationService,
                                                 Gson gson) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
        this.gson = gson;
        this.unitConversionService = unitConversionService;
    }

    protected String generatePimWebUseFieldKey(final String attributeName) {
        return attributeName != null ? attributeName.replaceAll(ALLOWED_CHARACTERS_FIELDNAME, "").toLowerCase() : null;
    }

    protected PimWebUseField[] parsePimWebUseJson(String json) {
        return gson.fromJson(json, PimWebUseField[].class);
    }

    protected boolean isNumerical(PimWebUseField field) {
        return DOUBLE.getCode().equals(field.getFieldType());
    }

    protected UnitConversionService getUnitConversionService() {
        return unitConversionService;
    }
}
