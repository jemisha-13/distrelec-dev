package com.namics.distrelec.mapping.converters;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

import java.io.IOException;
import java.util.function.Predicate;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachExportException;
import com.namics.distrelec.b2b.core.model.DistB2BRequestQuotationPermissionModel;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.bloomreach.DistBloomreachFacade;
import com.namics.distrelec.b2b.facades.bloomreach.data.DistBloomreachConsentData;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;

import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.cmsoccaddon.mapping.converters.DataToWsConverter;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import ma.glasnost.orika.MapperFactory;

public class CustomerDataToWsDTOConverter implements DataToWsConverter<CustomerData, UserWsDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerDataToWsDTOConverter.class);

    private static final String TIMEOUT_CONFIG = "oauth2.refreshTokenValiditySeconds";

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Autowired
    private DistBloomreachFacade distBloomreachFacade;

    @Autowired
    @Qualifier("userFacade")
    protected DistUserFacade userFacade;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public Class getDataClass() {
        return CustomerData.class;
    }

    @Override
    public Class getWsClass() {
        return UserWsDTO.class;
    }

    @Override
    public Predicate<Object> canConvert() {
        return null;
    }

    @Autowired
    private DistUserService userService;

    @Override
    public UserWsDTO convert(final CustomerData source, final String fields) {

        final UserWsDTO target = dataMapper.map(source, UserWsDTO.class, fields);
        if (CollectionUtils.isNotEmpty(source.getPermissions())) {
            for (final B2BPermissionData permission : source.getPermissions()) {
                if (DistB2BRequestQuotationPermissionModel._TYPECODE.equals(permission.getB2BPermissionTypeData().getCode()) && permission.isActive()) {
                    target.setRequestQuotationPermission(true);
                }
            }
        }
        target.setNewsletterPopup(isShowSubscribePopup(source));
        target.setBudgetWithoutLimit(source.getBudget() == null);
        target.setTimeout(configurationService.getConfiguration().getString(TIMEOUT_CONFIG));
        return target;
    }

    private boolean isShowSubscribePopup(final CustomerData source) {
        // The cookie to display newsletter popup only may be set only for anonymous customers, and customers who did not opt in for the Distrelec Newsletters
        DistBloomreachConsentData bloomreachPreferences = null;
        if (userService.getCurrentUser().getUid().equals(source.getEmail())) {
            bloomreachPreferences = getBloomreachPreferencesForCurrentUser(source);
        }
        if (bloomreachPreferences == null) {
            return true;
        }

        final boolean isSubscribedToAnySubscription = bloomreachPreferences.isNewBrandsConsent() || // Distrelec.XX (newsletter, for example Distrelec.DE)
                bloomreachPreferences.isContentNewsConsent() || // Latest Industry news & updates
                bloomreachPreferences.isNpsConsent() || // NPS
                bloomreachPreferences.isPersonalisedRecommendationsConsent() || // Personalised recommendations
                bloomreachPreferences.isSalesAndClearanceConsent(); // Unmissable promotions, sales & clearance
        return isFalse(isSubscribedToAnySubscription);
    }

    private DistBloomreachConsentData getBloomreachPreferencesForCurrentUser(final CustomerData customerData) {
        if (isFalse(userFacade.isAnonymousUser())) {
            return getBloomreachPreferencesForUserEmail(customerData.getEmail());
        }
        return null;
    }

    private DistBloomreachConsentData getBloomreachPreferencesForUserEmail(final String email) {
        try {
            return distBloomreachFacade.exportCustomerConsentsFromBloomreach(email);
        } catch (IOException | DistBloomreachExportException e) {
            LOG.error("Consents could not be exported from Bloomreach for customer: {}", email, e);
            return null;
        }
    }

    @Override
    public void customize(final MapperFactory factory) {

    }

    protected DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(final DistrelecStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }
}
