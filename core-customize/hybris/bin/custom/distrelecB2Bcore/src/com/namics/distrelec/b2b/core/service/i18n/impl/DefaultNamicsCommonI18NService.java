package com.namics.distrelec.b2b.core.service.i18n.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.core.service.i18n.NamicsI18NConstants;
import com.namics.distrelec.b2b.core.service.i18n.dao.DistLanguageDao;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

/**
 * Default implementation of {@link NamicsCommonI18NService}
 * 
 * @author rhusi
 * 
 */
public class DefaultNamicsCommonI18NService extends AbstractBusinessService implements NamicsCommonI18NService {

    private DistLanguageDao languageDao;
    
    private static final String DEFAULT_PIM_LOCAL="eng";

    @Override
    public CountryModel getCurrentCountry() {
        return getSessionService().getAttribute(NamicsI18NConstants.COUNTRY_SESSION_ATTR_KEY);
    }

    @Override
    public void setCurrentCountry(final CountryModel country) {
        getSessionService().setAttribute(NamicsI18NConstants.COUNTRY_SESSION_ATTR_KEY, country);
        if (!country.getRegions().isEmpty()) {
            final Collection<RegionModel> regionsCol = country.getRegions();
            final List<RegionModel> regions = new ArrayList<RegionModel>(regionsCol);
            Collections.sort(regions, new Comparator<RegionModel>() {

                @Override
                public int compare(final RegionModel region1, final RegionModel region2) {
                    if (region1 == null || region2 == null) {
                        return 0;
                    }

                    if (region1.getName() == null || region2.getName() == null) {
                        return 0;
                    }

                    return region1.getIsocode().compareTo(region2.getIsocode());
                }

            });
            setCurrentRegion(regions.get(0));
        } else {
            // The current country has no regions. So reset the region.
            setCurrentRegion(null);
        }
    }

    @Override
    public RegionModel getCurrentRegion() {
        return getSessionService().getAttribute(NamicsI18NConstants.REGION_SESSION_ATTR_KEY);
    }

    @Override
    public void setCurrentRegion(final RegionModel region) {
        getSessionService().setAttribute(NamicsI18NConstants.REGION_SESSION_ATTR_KEY, region);
    }

    @Override
    public LanguageModel getLanguageModelByIsocodePim(final String isocodePim) {
        return getLanguageDao().findLanguageByIsocodePim(isocodePim);
    }
    
    @Override
    public LanguageModel getLanguageModelByIsocode(final String isocode) {
    	List<LanguageModel> languages= getLanguageDao().findLanguagesByCode(isocode);
    	return CollectionUtils.isNotEmpty(languages)?languages.get(0): getLanguageModelByIsocodePim(DEFAULT_PIM_LOCAL);
    }
    

    @Required
    public void setLanguageDao(final DistLanguageDao languageDao) {
        this.languageDao = languageDao;
    }

    public DistLanguageDao getLanguageDao() {
        return languageDao;
    }
}