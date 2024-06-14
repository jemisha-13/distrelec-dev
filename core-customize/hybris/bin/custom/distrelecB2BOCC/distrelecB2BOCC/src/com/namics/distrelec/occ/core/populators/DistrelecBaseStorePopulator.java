package com.namics.distrelec.occ.core.populators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants.SearchExperienceConfig;
import com.namics.distrelec.b2b.core.enums.SearchExperience;
import com.namics.distrelec.b2b.facades.search.data.SearchExperienceDTO;

import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.commercefacades.basestores.converters.populator.BaseStorePopulator;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.DeliveryModesData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Overriding the OOTB populator to set additional values
 */
public class DistrelecBaseStorePopulator extends BaseStorePopulator {

    private Converter<SearchExperience, SearchExperienceDTO> searchExperienceDTOConverter;

    @Override
    public void populate(final BaseStoreModel source, final BaseStoreData target) throws ConversionException {

        if (source != null && target != null) {
            target.setName(source.getName());
            target.setExternalTaxEnabled(Boolean.TRUE.equals(source.getExternalTaxEnabled()));
            target.setPaymentProvider(source.getPaymentProvider());
            target.setCreateReturnProcessCode(source.getCreateReturnProcessCode());
            target.setMaxRadiusForPosSearch(source.getMaxRadiusForPoSSearch());
            target.setSubmitOrderProcessCode(source.getSubmitOrderProcessCode());
            if (source.getDefaultCurrency() != null) {
                target.setDefaultCurrency(getCurrencyConverter().convert(source.getDefaultCurrency()));
            }
            if (source.getDefaultDeliveryOrigin() != null) {
                target.setDefaultDeliveryOrigin(getPointOfServiceConverter().convert(source.getDefaultDeliveryOrigin()));
            }
            if (source.getDefaultLanguage() != null) {
                target.setDefaultLanguage(getLanguageConverter().convert(source.getDefaultLanguage()));
            }

            target.setCurrencies(new ArrayList<>());
            source.getCurrencies().forEach(currency -> target.getCurrencies().add(getCurrencyConverter().convert(currency)));

            target.setDeliveryCountries(new ArrayList<>());
            source.getDeliveryCountries()
                  .forEach(country -> target.getDeliveryCountries().add(getCountryConverter().convert(country)));

            final List<DeliveryModeData> result = new ArrayList<>();
            source.getDeliveryModes().forEach(deliveryMode -> result.add(getDeliveryModeConverter().convert(deliveryMode)));
            DeliveryModesData deliveryModesData = new DeliveryModesData();
            deliveryModesData.setDeliveryModes(result);
            target.setDeliveryModes(deliveryModesData);

            target.setLanguages(new ArrayList<>());
            source.getLanguages().forEach(language -> target.getLanguages().add(getLanguageConverter().convert(language)));

            target.setPointsOfService(new ArrayList<>());
            source.getPointsOfService().forEach(pos -> target.getPointsOfService().add(getPointOfServiceConverter().convert(pos)));
            target.setBackorderAllowed(Boolean.TRUE.equals(source.getBackorderAllowed()));
            target.setQuotationsEnabled(Boolean.TRUE.equals(source.getQuotationsEnabled()));
            target.setOrderApprovalEnabled(Boolean.TRUE.equals(source.getOrderApprovalEnabled()));

            populateSearchExperienceMap(source, target);
        }
    }

    protected void populateSearchExperienceMap(final BaseStoreModel source, final BaseStoreData target) {
        Map<String, SearchExperienceDTO> searchExperienceMap = new HashMap<>();

        for (LanguageModel lang : source.getLanguages()) {
            String langIsoCode = lang.getIsocode();

            SearchExperience searchExperience = source.getSearchExperienceMap().get(langIsoCode);
            if (searchExperience == null) {
                searchExperience = SearchExperienceConfig.DEFAULT_HEADLESS_SEARCH_EXPERIENCE;
            }

            SearchExperienceDTO searchExperienceDTO = getSearchExperienceDTOConverter().convert(searchExperience);
            searchExperienceMap.put(langIsoCode, searchExperienceDTO);
        }

        target.setSearchExperienceMap(searchExperienceMap);
    }

    protected Converter<SearchExperience, SearchExperienceDTO> getSearchExperienceDTOConverter() {
        return searchExperienceDTOConverter;
    }

    public void setSearchExperienceDTOConverter(Converter<SearchExperience, SearchExperienceDTO> searchExperienceDTOConverter) {
        this.searchExperienceDTOConverter = searchExperienceDTOConverter;
    }
}
