/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.event.DistSeminarRegistrationRequestEvent;
import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarData;
import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData;
import com.namics.distrelec.b2b.facades.infocenter.DistInfoCenterFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.forms.SeminarRegistrationForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/info-center/seminar/register")
public class SeminarPageController extends AbstractPageController {

    private static final Logger LOG = Logger.getLogger(SeminarPageController.class);
    private static final String SEMINARREGISTRATION_PAGE = "seminarRegistrationPage";
    private static final String STATUS = "status";

    @Autowired
    private DistInfoCenterFacade distInfoCenterFacade;

    @Autowired
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private Validator validator;

    @ModelAttribute("countries")
    public List<CountryData> getCountries() {
        return getCheckoutFacade().getDeliveryCountriesAndRegions();
    }

    @ModelAttribute("titles")
    public Collection<TitleData> getTitles() {
        return getUserFacade().getTitles();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String registerPage(@RequestParam(value = "suid", required = false)
    final String suid, @RequestParam(value = "topic", required = false)
    final String topic, @RequestParam(value = "date", required = false)
    final String dateStr, final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        final SeminarRegistrationForm seminarRegistrationForm = new SeminarRegistrationForm();
        boolean success = false;
        if (StringUtils.isNotBlank(suid)) {
            try {
                final DistSeminarData seminarData = getDistInfoCenterFacade().getSeminarForUID(suid);
                seminarRegistrationForm.setSeminar(seminarData.getUid());
                seminarRegistrationForm.setDate(seminarData.getDate());
                seminarRegistrationForm.setTopic(seminarData.getTopic());
                success = true;
            } catch (final Exception e) {
                LOG.warn("No Seminar object found for code " + suid);
            }
        }

        if (!success) {
            if (StringUtils.isNotBlank(dateStr)) {
                try {
                    final String datePattern = getDatePattern();
                    if (datePattern != null) {
                        final DateFormat dateFormat = new SimpleDateFormat(datePattern, getI18nService().getCurrentLocale());
                        seminarRegistrationForm.setDate(dateFormat.parse(dateStr));
                    }
                } catch (final Exception e) {
                    LOG.error("Bad date format : " + dateStr, e);
                }
            }

            if (StringUtils.isNotBlank(topic)) {
                seminarRegistrationForm.setTopic(topic);
            }
        }

        model.addAttribute(seminarRegistrationForm);
        populatePage(model);
        return ControllerConstants.Views.Pages.InfoCenter.SeminarRegistrationPage;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String registerSeminar(final SeminarRegistrationForm seminarRegistrationForm, final BindingResult bindingResult, final Model model,
            final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);

        boolean success = true;

        ValidationUtils.invokeValidator(getValidator(), seminarRegistrationForm, bindingResult);

        if (bindingResult.hasErrors()) {
            if (bindingResult.getFieldError("date") != null) {
                bindingResult.rejectValue("date", "validate.error.date.format", new Object[] { getDatePattern() },
                        "The date must be in the format " + getDatePattern());
            }

            model.addAttribute(seminarRegistrationForm);
            success = false;
        } else {
            try {
                if (StringUtils.isNotBlank(seminarRegistrationForm.getSeminar())) {
                    getDistInfoCenterFacade().registerForSeminar(seminarRegistrationForm.getSeminar(), createRegistrationEvent(seminarRegistrationForm));
                } else {
                    getDistInfoCenterFacade().registerForSeminar(createRegistrationData(seminarRegistrationForm));
                    getDistInfoCenterFacade().registerForSeminar(createRegistrationEvent(seminarRegistrationForm));
                }
            } catch (final Exception e) {
                success = false;
                model.addAttribute(seminarRegistrationForm);
                LOG.warn(e.getMessage(), e);
            }
        }

        model.addAttribute(STATUS, Boolean.valueOf(success));

        populatePage(model);
        return success ? addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/info-center/seminar/register/success")
                : ControllerConstants.Views.Pages.InfoCenter.SeminarRegistrationPage;
    }

    @ModelAttribute("datePattern")
    public String getDatePattern() {
        try {
            return getMessageSource().getMessage("text.store.dateformat", null, getI18nService().getCurrentLocale());
        } catch (final Exception exp) {
            // GlobalMessages.addErrorMessage(model, "form.global.error");
        }
        return null;
    }

    @InitBinder
    protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) {
        final DateFormat dateFormat = new SimpleDateFormat(getMessageSource().getMessage("text.store.dateformat", null, getI18nService().getCurrentLocale()),
                getI18nService().getCurrentLocale());
        final CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, dateEditor);
    }

    protected DistSeminarRegistrationData createRegistrationData(final SeminarRegistrationForm seminarRegistrationForm) {
        final DistSeminarRegistrationData seminarRegistration = new DistSeminarRegistrationData();
        BeanUtils.copyProperties(seminarRegistrationForm, seminarRegistration);
        return seminarRegistration;
    }

    protected DistSeminarRegistrationRequestEvent createRegistrationEvent(final SeminarRegistrationForm seminarRegistrationForm) {
        final DistSeminarRegistrationRequestEvent seminarRegistrationEvent = new DistSeminarRegistrationRequestEvent();
        BeanUtils.copyProperties(seminarRegistrationForm, seminarRegistrationEvent);
        return seminarRegistrationEvent;
    }

    protected void populatePage(final Model model) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId(SEMINARREGISTRATION_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SEMINARREGISTRATION_PAGE));
    }

    public DistInfoCenterFacade getDistInfoCenterFacade() {
        return distInfoCenterFacade;
    }

    public void setDistInfoCenterFacade(final DistInfoCenterFacade distInfoCenterFacade) {
        this.distInfoCenterFacade = distInfoCenterFacade;
    }

    public DistCheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(final DistCheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(final UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(final Validator validator) {
        this.validator = validator;
    }

}
