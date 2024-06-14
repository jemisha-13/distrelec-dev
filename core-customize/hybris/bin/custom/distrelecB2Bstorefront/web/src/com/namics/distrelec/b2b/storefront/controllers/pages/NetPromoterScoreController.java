/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.enums.NPSReason;
import com.namics.distrelec.b2b.core.enums.NPSSubReason;
import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade;
import com.namics.distrelec.b2b.storefront.constants.MessageConstants;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.FeedbackNPSForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@code NetPromoterScoreController}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
@Controller
@RequestMapping(FeedbackPageController.FEEDBACK_REQUEST_MAPPING_URL + "/nps")
public class NetPromoterScoreController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(NetPromoterScoreController.class);
    private static final String NPS_CMS_PAGE = "netPromoterScorePage";
    private static final String MODEL_KEY_HAS_ERRORS = "hasErrors";
    private static final String DELIVERY = "delivery";
    public static final String DATE_FORMAT = "MM/dd/yyyy";

    @Autowired
    private DistNetPromoterScoreFacade distNetPromoterScoreFacade;

    @Autowired
    private EnumerationService enumService;

    @RequestMapping(method = RequestMethod.GET)
    public String getNPSForm(@Valid final FeedbackNPSForm npsForm, final BindingResult bindingResult, final Model model,
                             final HttpServletRequest request) throws CMSItemNotFoundException {
        // Add the global model attributes.
        model.addAttribute(MODEL_KEY_HAS_ERRORS, bindingResult.hasErrors());
        final String dateFormat = getMessageSource().getMessage(MessageConstants.TEXT_STORE_FORMAT, null, DATE_FORMAT, getI18nService().getCurrentLocale());

        if (bindingResult.hasErrors()) {
            for (final FieldError field : bindingResult.getFieldErrors()) {
                if (DELIVERY.equals(field.getField())) {
                    GlobalMessages.addErrorMessage(model, MessageConstants.VALIDATE_ERROR_DATE_FORMAT, new String[] { dateFormat });
                } else {
                    GlobalMessages.addErrorMessage(model, field.getDefaultMessage());
                }
            }
        }
        // Check whether the customer is allowed to submit a new NPS
        final Date lastNpsDate = getDistNetPromoterScoreFacade().getLastSubmittedNPSDate(npsForm.getEmail());
        if (lastNpsDate != null) {
            model.addAttribute(MODEL_KEY_HAS_ERRORS, Boolean.TRUE);
            GlobalMessages.addErrorMessage(model, MessageConstants.FEEDBACK_NPS_DUPLICATE, new String[] { new SimpleDateFormat(dateFormat).format(lastNpsDate) });
        } else {
            createNPS(npsForm);
        }

        final List<String> reasons = Stream.of(NPSReason.values()).map(NPSReason::getCode).collect(Collectors.toList());
        final List<String> subReasons = Stream.of(NPSSubReason.values()).map(NPSSubReason::getCode).collect(Collectors.toList());

        model.addAttribute(WebConstants.NPS_FORM_REASONS, reasons);
        model.addAttribute(WebConstants.NPS_FORM_SUBREASONS, subReasons);
        model.addAttribute(WebConstants.NPS_FORM , npsForm);

        // CMS Page setup
        final ContentPageModel cmsPage = getContentPageForLabelOrId(NPS_CMS_PAGE);
        storeCmsPageInModel(model, cmsPage);
        setUpMetaDataForContentPage(model, cmsPage);
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.NPSPAGE);
        return ControllerConstants.Views.Pages.Feedback.NetPromoterScorePage;
    }

    /***
     * Creates the first pass of recording an initial NPS Score
     * @param npsForm
     */
    private void createNPS(@Valid final FeedbackNPSForm npsForm) {
        final DistNetPromoterScoreData npsData = createNPSData(npsForm);
        getDistNetPromoterScoreFacade().createNPS(npsData);
        npsForm.setId(npsData.getCode());
    }

    /**
     * POST method for submitting NPS request
     *
     * @param form
     * @param bindingResult
     * @param model
     * @return a JSON string
     */
    @RequestMapping(method = RequestMethod.POST, produces = WebConstants.APPLICATION_JSON)
    public String postNPS(@Valid final FeedbackNPSForm form, final BindingResult bindingResult, final Model model) {
        try {
            if (bindingResult.hasErrors() || StringUtils.isEmpty(form.getId())) {
                throw new SystemException("Some inputs are not valid!");
            }
            final DistNetPromoterScoreData npsData = getDistNetPromoterScoreFacade().getNPSByCode(form.getId());
            model.addAttribute(WebConstants.ALLOWED_TO_SUBMIT_NPS, npsData == null);
            model.addAttribute(WebConstants.LAST_NPS_DATE, npsData);

            if (npsData != null && npsData.getReason() != null) {
                final String dateFormat = getMessageSource().getMessage(MessageConstants.TEXT_STORE_FORMAT, null, DATE_FORMAT, getI18nService().getCurrentLocale());
                model.addAttribute(WebConstants.STATUS, WebConstants.NOK);
                model.addAttribute(WebConstants.MESSAGE_KEY, MessageConstants.FEEDBACK_NPS_DUPLICATE);
                model.addAttribute(WebConstants.MESSAGE_KEY_ARG, new SimpleDateFormat(dateFormat).format(npsData));
            } else {
                getDistNetPromoterScoreFacade().updateNPS(updateNPSData(form));
                model.addAttribute(WebConstants.STATUS, WebConstants.OK);
                model.addAttribute(WebConstants.MESSAGE_KEY, MessageConstants.FEEDBACK_NPS_SUCCESS);
            }
        } catch (final Exception exp) {
            LOG.error("Some error occurs while processing NPS request with code: {}", form.getId(), exp);
            model.addAttribute(WebConstants.STATUS, WebConstants.NOK);
            model.addAttribute(WebConstants.MESSAGE_KEY, MessageConstants.FEEDBACK_NPS_ERROR);
        }
        return ControllerConstants.Views.Fragments.Feedback.NpsFormJson;
    }

    /**
     * Convert the NPS form to a NPS data object.
     *
     * @param form
     *            the source object.
     * @return a new instance of {@code DistNetPromoterScoreData}
     */
    private DistNetPromoterScoreData createNPSData(final FeedbackNPSForm form) {
        final DistNetPromoterScoreData data = new DistNetPromoterScoreData();
        /* Setting the type */
        data.setType(Stream.of(NPSType.values()).filter(type -> type.getCode().equals(form.getType())).findFirst().orElse(NPSType.ORDERCONFIRMATION));
        // Setting other fields
        data.setSalesOrg(getCurrentSalesOrg().getCode());
        data.setEmail(form.getEmail());
        data.setFirstname(form.getFname());
        data.setLastname(form.getNamn());
        data.setCompanyName(form.getCompany());
        data.setErpContactID(form.getContactnum());
        data.setErpCustomerID(form.getCnumber());
        data.setOrderNumber(form.getOrder());
        data.setDeliveryDate(form.getDelivery());
        data.setValue(null == form.getValue() ? WebConstants.DEFAULT_STARTING_NPS_SCORE : form.getValue());
        data.setText(form.getFeedback());
        data.setCode(form.getId());
        return data;
    }

    private DistNetPromoterScoreData updateNPSData(final FeedbackNPSForm form) {
        final DistNetPromoterScoreData data = createNPSData(form);

        // SET NPS REASON
        if (StringUtils.isNotBlank(form.getReason())) {
            try {
                data.setReason(enumService.getEnumerationValue(NPSReason.class, form.getReason()));
            } catch (final Exception exp) {
                LOG.warn("Unknown NPS Reason code", exp);
            }
        }

        if (StringUtils.isNotBlank(form.getSubreason())) {
            try {
                data.setSubreason(enumService.getEnumerationValue(NPSSubReason.class, form.getSubreason()));
            } catch (final Exception exp) {
                LOG.warn("Unknown NPS Sub Reason code", exp);
            }
        }
        return data;
    }

    @InitBinder
    protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) {
        DateFormat dateFormat;
        try {
            dateFormat = new SimpleDateFormat(getMessageSource().getMessage(MessageConstants.TEXT_STORE_FORMAT, null, getI18nService().getCurrentLocale()),
                    getI18nService().getCurrentLocale());
        } catch (final IllegalArgumentException e) {
            dateFormat = new SimpleDateFormat(DATE_FORMAT, getI18nService().getCurrentLocale());
        }
        final CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, dateEditor);
    }

    // Getters & Setters

    public DistNetPromoterScoreFacade getDistNetPromoterScoreFacade() {
        return distNetPromoterScoreFacade;
    }

    public void setDistNetPromoterScoreFacade(final DistNetPromoterScoreFacade distNetPromoterScoreFacade) {
        this.distNetPromoterScoreFacade = distNetPromoterScoreFacade;
    }

}
