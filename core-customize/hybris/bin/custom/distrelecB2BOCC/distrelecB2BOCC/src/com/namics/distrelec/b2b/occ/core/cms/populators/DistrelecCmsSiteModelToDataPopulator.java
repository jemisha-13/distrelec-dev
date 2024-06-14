package com.namics.distrelec.b2b.occ.core.cms.populators;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.occ.core.basesite.data.DistSiteTrackingSettingsData;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistrelecCmsSiteModelToDataPopulator implements Populator<CMSSiteModel, BaseSiteData> {

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void populate(final CMSSiteModel source, final BaseSiteData target) throws ConversionException {

        if (source != null) {
            target.setWtTrackId(source.getWtTrackId());
            target.setWtTrackDomain(source.getWtTrackDomain());
            target.setWtDomain(source.getWtDomain());
            target.setWtActivateEventTracking(source.getWtActivateEventTracking());
            target.setWtDefaultAreaCode(source.getWtDefaultAreaCode());
            target.setPrintFooterContent(source.getPrintFooterContent());
            target.setExclusiveFastDeliveryTime(source.getExclusiveFastDeliveryTime());
            target.setExclusiveSlowDeliveryTime(source.getExclusiveSlowDeliveryTime());
            target.setExternalFastDeliveryTime(source.getExternalFastDeliveryTime());
            target.setExternalSlowDeliveryTime(source.getExternalSlowDeliveryTime());
            target.setFastDeliveryTime(source.getFastDeliveryTime());
            target.setSlowDeliveryTime(source.getSlowDeliveryTime());
            target.setBackorderDeliveryTime(source.getBackorderDeliveryTime());
            target.setRedirect404(source.getRedirect404());
            target.setRequestedDeliveryDateMaxDays(source.getRequestedDeliveryDateMaxDays());
            target.setReevooPurchaserDuration(source.getReevooPurchaserDuration());
            target.setUseProductFeatures(Boolean.TRUE.equals(source.getUseProductFeatures()));
            target.setDoNotCalcuteTaxForB2B(Boolean.TRUE.equals(source.isDoNotCalcuteTaxForB2B()));
            target.setShippingOptionsEditable(Boolean.TRUE.equals(source.isShippingOptionsEditable()));
            target.setPaymentOptionsEditable(Boolean.TRUE.equals(source.isPaymentOptionsEditable()));
            target.setRequestedDeliveryDateEnabled(Boolean.TRUE.equals(source.isRequestedDeliveryDateEnabled()));
            target.setHttpsOnly(Boolean.TRUE.equals(source.isHttpsOnly()));
            target.setHidden(Boolean.TRUE.equals(source.isHidden()));
            target.setReevooActivated(Boolean.TRUE.equals(source.isReevooActivated()));
            target.setReevooBrandVisible(Boolean.TRUE.equals(source.isReevooBrandVisible()));
            target.setGuestCheckoutEnabled(Boolean.TRUE.equals(source.getGuestCheckoutEnabled()));
            target.setTracking(getTrackingSettingsData());
            target.setCheckoutDeliveryMethodPricesShown(source.getCheckoutDeliveryMethodPricesShown());
            target.setMaintenanceActive(source.isMaintenanceActive());
            target.setBloomreachScriptToken(configurationService.getConfiguration().getString(DistConfigConstants.Bloomreach.SCRIPT_TOKEN));
            target.setEnableAddToCart(source.isEnableAddToCart());
            target.setEnableNewsletter(source.isEnableNewsletter());
            target.setEnableMyAccountRedirect(source.isEnableMyAccountRedirect());
            target.setEnableProductReturn(source.isEnableProductReturn());
            target.setEnableSubUserManagement(source.isEnableSubUserManagement());
        }
    }

    protected DistSiteTrackingSettingsData getTrackingSettingsData() {
        final DistSiteTrackingSettingsData data = new DistSiteTrackingSettingsData();
        data.setGtmAuth(configurationService.getConfiguration().getString(DistConfigConstants.Tracking.GTM_AUTH));
        data.setGtmCookiesWin(
                              configurationService.getConfiguration().getString(DistConfigConstants.Tracking.GTM_COOKIES_WIN));
        data.setGtmPreview(configurationService.getConfiguration().getString(DistConfigConstants.Tracking.GTM_PREVIEW));
        data.setGtmTagId(configurationService.getConfiguration().getString(DistConfigConstants.Tracking.GTM_TAG_ID));
        return data;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
