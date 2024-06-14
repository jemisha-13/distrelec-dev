package com.namics.distrelec.occ.core.search;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.SearchExperience;
import com.namics.distrelec.b2b.facades.search.data.SearchExperienceDTO;

import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.servicelayer.config.ConfigurationService;

public class SearchExperienceDTOConverter extends AbstractConverter<SearchExperience, SearchExperienceDTO> {

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void populate(SearchExperience source, SearchExperienceDTO target) {
        target.setCode(source.getCode());

        switch (source) {
            case FACTFINDER:
                populateFactFinder(target);
                break;
            case FUSION:
                populateFusion(target);
                break;
        }
    }

    private void populateFusion(SearchExperienceDTO target) {
        target.setSearchUrl(getConfigurationService().getConfiguration().getString(DistConstants.Fusion.FUSION_SEARCH_URL));
    }

    protected void populateFactFinder(SearchExperienceDTO target) {
        target.setSearchUrl(getConfigurationService().getConfiguration().getString(DistConstants.FactFinder.WEBSERVICE_SEARCH_URL));
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
