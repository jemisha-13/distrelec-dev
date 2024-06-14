package com.namics.distrelec.b2b.core.quotation.service.impl;

import com.namics.distrelec.b2b.core.model.DistB2BQuoteLimitModel;
import com.namics.distrelec.b2b.core.quotation.dao.DistQuotationDao;
import com.namics.distrelec.b2b.core.quotation.service.DefaultDistQuotationService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.SelfServiceQuotation.ATTRIBUTE_DEFAULT_CUSTOMER_QUOTE_SUBMIT_LIMIT;

public class DistQuotationService implements DefaultDistQuotationService {

    private static final Logger LOG = LoggerFactory.getLogger(DistQuotationService.class);

    @Autowired
    private DistQuotationDao quotationDao;

    @Autowired
    @Qualifier("defaultDistUserService")
    private UserService userService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private ConfigurationService configurationService;

    private static final Integer DEFAULT_LIMIT = 100;

    @Override
    public boolean isCustomerOverQuotationLimit(String customerNumber) {
        final UserModel userModel = getUserForId(customerNumber);
        final DistB2BQuoteLimitModel quotation = getQuotationDao().getQuotationCount(userModel);
        return (quotation != null) && !isCustomerUnderDailyLimit(quotation);
    }

    @Override
    public boolean incrementQuotationCounter(String uid) {
        boolean hasUpdated = false;
        final UserModel userModel = getUserForId(uid);

        if (userModel != null) {
            DistB2BQuoteLimitModel quotation = getQuotationDao().getQuotationCount(userModel);

            if (quotation == null) {
                quotation = getModelService().create(DistB2BQuoteLimitModel.class);
            }

            if (isCustomerUnderDailyLimit(quotation)) {
                quotation.setUser(userModel);
                quotation.setCurrentQuotationCount(quotation.getCurrentQuotationCount() + 1);
                getModelService().save(quotation);
                hasUpdated = true;
            } else {
                LOG.warn("Could not update Quotation Count for customer. Customer is over daily limit: Contact ID [{}]: Current Count:[{}]", uid, quotation.getCurrentQuotationCount());
           }
        } else {
            LOG.warn("Could not update Quotation Count for customer. Contact ID [{}]", uid);
        }
        return hasUpdated;
    }

    private boolean isCustomerUnderDailyLimit(final DistB2BQuoteLimitModel count) {
        final Integer currentCount = count.getCurrentQuotationCount();
        final Integer limit = getConfigurationService().getConfiguration().getInteger(ATTRIBUTE_DEFAULT_CUSTOMER_QUOTE_SUBMIT_LIMIT, DEFAULT_LIMIT);
        return currentCount != null && currentCount <= limit;
    }

    private UserModel getUserForId(final String customer) {
        return getUserService().getUserForUID(customer);
    }

    public DistQuotationDao getQuotationDao() {
        return quotationDao;
    }

    public void setQuotationDao(final DistQuotationDao quotationDao) {
        this.quotationDao = quotationDao;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
