/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.nps.impl;

import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.core.event.DistNetPromoterScoreEvent;
import com.namics.distrelec.b2b.core.event.NetPromoterScoreHelper;
import com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel;
import com.namics.distrelec.b2b.core.model.process.DistNpsProcessModel;
import com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;
import com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * {@code DefaultDistNetPromoterScoreFacade}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @author <a href="abhinay.jadhav@datwyler.com">Abhinay Jadhav</a>, Datwyler
 * @since Distrelec 3.4,6.4
 */
public class DefaultDistNetPromoterScoreFacade implements DistNetPromoterScoreFacade {

    @Autowired
    private Converter<DistNetPromoterScoreModel, DistNetPromoterScoreData> distNetPromoterScoreConverter;
    @Autowired
    private DistNetPromoterScoreService distNetPromoterScoreService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private CMSSiteService cmsSiteService;
    @Autowired
    private EventService eventService;
    @Autowired
    private BaseStoreService baseStoreService;
    @Autowired
    private BaseSiteService baseSiteService;
    @Autowired
    private UserService userService;
    @Autowired
    private BusinessProcessService businessProcessService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ModelService modelService;

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade#createNPS(com.namics.distrelec.b2b.core.service.nps.data.
     * DistNetPromoterScoreData )
     */
    @Override
    public void createNPS(final DistNetPromoterScoreData npsData) {
        npsData.setDomain(configurationService.getConfiguration().getString("website." + cmsSiteService.getCurrentSite().getUid() + ".http", "unkown"));
        getDistNetPromoterScoreService().createNPS(npsData);

        // If the feedback message is not empty or the ranking is less or equal 6, then we send the email.
        publishEventIfRequired(npsData);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade#updateNPS(com.namics.distrelec.b2b.core.service.nps.data.
     * DistNetPromoterScoreData)
     */
    @Override
    public void updateNPS(final DistNetPromoterScoreData npsData) {
        npsData.setDomain(configurationService.getConfiguration().getString("website." + cmsSiteService.getCurrentSite().getUid() + ".http", "unkown"));
        getDistNetPromoterScoreService().updateNPS(npsData);

        final String processCode = "netPromoterScore_" + npsData.getCode();
        final DistNpsProcessModel npsProcess = getBusinessProcessService().<DistNpsProcessModel> getProcess(processCode);
        if (npsProcess != null) {
            // 1- Update the business process with the new data.
            NetPromoterScoreHelper.updateNPS(npsData, npsProcess);
            getModelService().save(npsProcess);
            // 2- Trigger the event to interrupt the wait step.
            getBusinessProcessService().triggerEvent(processCode + "_DistNetPromoterScoreEvent");
        } else {
            // If the feedback message is not empty or the ranking is less or equal 6, then we send the email.
            publishEventIfRequired(npsData);
        }
    }

    /**
     * If the feedback message is not empty or the ranking is less or equal 6, then we send the email.
     *
     * @param npsData
     */
    private void publishEventIfRequired(final DistNetPromoterScoreData npsData) {
        if (npsData != null && (StringUtils.isNotBlank(npsData.getText()) || npsData.getValue() <= 6)) {
            // Create the event and publish it.
            final DistNetPromoterScoreEvent event = createNPSEvent(npsData);
            if (event != null) {
                getEventService().publishEvent(event);
            }
        }
    }

    /**
     * Create the NPS Event from the source NPS data object
     *
     * @param npsData
     * @return a new instance of {@code DistNetPromoterScoreEvent}
     */
    private DistNetPromoterScoreEvent createNPSEvent(final DistNetPromoterScoreData npsData) {
        String toEmail = getConfigurationService().getConfiguration().getString("nps.email." + cmsSiteService.getCurrentSite().getUid());
        if (StringUtils.isBlank(toEmail)) {
            toEmail = getConfigurationService().getConfiguration().getString("nps.email.default");
        }
        if (StringUtils.isBlank(toEmail)) {
            return null;
        }
        final DistNetPromoterScoreEvent event = new DistNetPromoterScoreEvent();
        // Basic Data
        event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
        event.setSite(getBaseSiteService().getCurrentBaseSite());
        event.setCustomer((CustomerModel) getUserService().getCurrentUser());
        // NPS data
        event.setCode(npsData.getCode());
        event.setErpContactID(npsData.getErpContactID());
        event.setErpCustomerID(npsData.getErpCustomerID());
        event.setFirstname(npsData.getFirstname());
        event.setLastname(npsData.getLastname());
        event.setEmail(npsData.getEmail());
        event.setDeliveryDate(npsData.getDeliveryDate());
        event.setCompanyName(npsData.getCompanyName());
        event.setOrderNumber(npsData.getOrderNumber());
        event.setSalesOrg(npsData.getSalesOrg());
        event.setDomain(npsData.getDomain());
        event.setType(npsData.getType());
        event.setReason(npsData.getReason());
        event.setSubreason(npsData.getSubreason());
        event.setText(npsData.getText());
        event.setValue(npsData.getValue());
        event.setToEmail(toEmail);
        event.setEmailSubjectMsg(getMessageSource().getMessage("nps.email.subject", null, "NPS Survey", new Locale("en")));

        return event;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade#findAll()
     */
    @Override
    public List<DistNetPromoterScoreData> findAll() {
        return Converters.convertAll(getDistNetPromoterScoreService().findAll(), getDistNetPromoterScoreConverter());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade#findByType(com.namics.distrelec.b2b.core.enums.NPSType)
     */
    @Override
    public List<DistNetPromoterScoreData> findByType(final NPSType type) {
        return Converters.convertAll(getDistNetPromoterScoreService().findByType(type), getDistNetPromoterScoreConverter());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade#findExported(boolean)
     */
    @Override
    public List<DistNetPromoterScoreData> findExported(final boolean exported) {
        return Converters.convertAll(getDistNetPromoterScoreService().findExported(exported), getDistNetPromoterScoreConverter());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade#findByCustomer(java.lang.String)
     */
    @Override
    public List<DistNetPromoterScoreData> findByCustomer(final String erpCustomerID) {
        return Converters.convertAll(getDistNetPromoterScoreService().findByCustomer(erpCustomerID), getDistNetPromoterScoreConverter());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade#findByEmailAfterDate(java.lang.String, java.util.Date)
     */
    @Override
    public List<DistNetPromoterScoreData> findByEmailAfterDate(final String email, final Date date) {
        return Converters.convertAll(getDistNetPromoterScoreService().findByEmailSinceDate(email, date), getDistNetPromoterScoreConverter());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade#isAllowedToSubmitNPS(java.lang.String)
     */
    @Override
    public boolean isAllowedToSubmitNPS(final String email) {
        return getDistNetPromoterScoreService().findByEmailSinceDate(email, DateUtils.addDays(new Date(), -30)).isEmpty();
    }

    @Override
    public Date getLastSubmittedNPSDate(final String email) {
        return getDistNetPromoterScoreService().getLastSubmittedNPSDate(email, DateUtils.addDays(new Date(), -30));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade#getNPSByCode(java.lang.String)
     */
    @Override
    public DistNetPromoterScoreData getNPSByCode(final String code) {
        return getDistNetPromoterScoreConverter().convert(getDistNetPromoterScoreService().getNPSByCode(code));
    }

    public Converter<DistNetPromoterScoreModel, DistNetPromoterScoreData> getDistNetPromoterScoreConverter() {
        return distNetPromoterScoreConverter;
    }

    public void setDistNetPromoterScoreConverter(final Converter<DistNetPromoterScoreModel, DistNetPromoterScoreData> distNetPromoterScoreConverter) {
        this.distNetPromoterScoreConverter = distNetPromoterScoreConverter;
    }

    public DistNetPromoterScoreService getDistNetPromoterScoreService() {
        return distNetPromoterScoreService;
    }

    public void setDistNetPromoterScoreService(final DistNetPromoterScoreService distNetPromoterScoreService) {
        this.distNetPromoterScoreService = distNetPromoterScoreService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
}
