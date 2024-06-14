/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.education.data.DistEducationRegistrationData;
import com.namics.distrelec.b2b.core.event.DistEducationRegistrationRequestEvent;
import com.namics.distrelec.b2b.facades.education.DistEducationFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.forms.EducationRegistrationForm;
import com.namics.distrelec.b2b.storefront.forms.util.SelectOption;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * {@code EducationPageController}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 2.0
 */
@Controller
@RequestMapping("/education/register")
public class EducationPageController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(EducationPageController.class);
    private static final String EDUCATION_REGISTRATION_PAGE = "educationRegistrationPage";
    private static final String EDUCATION_REGISTRATION_SUCCESS_PAGE = "educationRegistrationSuccessPage";

    private static final String[] PROFILE_AREAS_KEYS = { "formEducationMeetsExperience.option.electrotech", "formEducationMeetsExperience.option.mechanical",
            "formEducationMeetsExperience.option.processeng", "formEducationMeetsExperience.option.informatik", "formEducationMeetsExperience.option.physics",
            "formEducationMeetsExperience.option.other" };

    @Autowired
    private Validator validator;

    @Autowired
    @Qualifier("b2bCheckoutFacade")
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    private DistEducationFacade educationFacade;

    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);

        if (!model.containsAttribute("educationRegistrationForm")) {
            model.addAttribute("educationRegistrationForm", new EducationRegistrationForm());
        }

        populatePage(model, EDUCATION_REGISTRATION_PAGE);

        return ControllerConstants.Views.Pages.Education.EducationRegistrationPage;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String register(@Valid
    final EducationRegistrationForm educationRegistrationForm, final BindingResult bindingResult, final Model model, final HttpServletRequest request)
            throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);

        boolean result = true;

        if (!educationRegistrationForm.getContactEmail().equals(educationRegistrationForm.getContactEmailRepeat())) {
            bindingResult.rejectValue("contactEmailRepeat", "validate.error.email.notmatch");
        } else if (!educationRegistrationForm.getInstitutionEmail().equals(educationRegistrationForm.getInstitutionEmailRepeat())) {
            bindingResult.rejectValue("institutionEmailRepeat", "validate.error.email.notmatch");
        } else if (!getCaptchaUtil().validateReCaptcha(request)) {
            model.addAttribute("captchaError", Boolean.TRUE);
            return successorfailure(!result);
        }

        // Files upload
        final String prefix = educationRegistrationForm.getContactFirstName() + "_" + educationRegistrationForm.getContactLastName();
        int pos = -1;
        if (educationRegistrationForm.geteTalent2014File() != null
                && StringUtils.isNotBlank(educationRegistrationForm.geteTalent2014File().getOriginalFilename())) {
            final String originalFilename = educationRegistrationForm.geteTalent2014File().getOriginalFilename();
            if ((pos = originalFilename.lastIndexOf('.')) > 0) {
                final String ext = originalFilename.substring(pos);
                uploadFile(educationRegistrationForm.geteTalent2014File(), prefix + "_ETalent2014" + ext);
            }
        }

        if (educationRegistrationForm.getStudyExchangeFile() != null
                && StringUtils.isNotBlank(educationRegistrationForm.getStudyExchangeFile().getOriginalFilename())) {
            final String originalFilename = educationRegistrationForm.getStudyExchangeFile().getOriginalFilename();
            if ((pos = originalFilename.lastIndexOf('.')) > 0) {
                final String ext = originalFilename.substring(pos);
                uploadFile(educationRegistrationForm.getStudyExchangeFile(), prefix + "_ProjectDescription" + ext);
            }
        }

        if (educationRegistrationForm.getMotivationFile() != null
                && StringUtils.isNotBlank(educationRegistrationForm.getMotivationFile().getOriginalFilename())) {
            final String originalFilename = educationRegistrationForm.getMotivationFile().getOriginalFilename();
            if ((pos = originalFilename.lastIndexOf('.')) > 0) {
                final String ext = originalFilename.substring(pos);
                uploadFile(educationRegistrationForm.getMotivationFile(), prefix + "_Motivation" + ext);
            }
        }

        try {
            educationFacade.register(convert(educationRegistrationForm));
        } catch (final Exception exp) {
            LOG.error("{} Education Registeration Exception for UserID: {} \n{}", ErrorLogCode.REGISTRATION_ERROR.getCode(),educationRegistrationForm.getContactEmail(),exp.getMessage(), exp);
            result = false;
        }

        populatePage(model, EDUCATION_REGISTRATION_PAGE);

        return successorfailure(result);
    }

    private String successorfailure(final boolean success) {
        return success ? addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/education/register/success")
                : ControllerConstants.Views.Pages.Education.EducationRegistrationPage;
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String getSuccessView(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        populatePage(model, EDUCATION_REGISTRATION_SUCCESS_PAGE);
        return ControllerConstants.Views.Pages.Education.EducationRegistrationSuccessPage;
    }

    private void uploadFile(final CommonsMultipartFile file, final String filename) {
        try {
            final String completeFileName = getConfigurationService().getConfiguration().getString("education.upload.resources") + File.separator + filename;
            final FileOutputStream fos = new FileOutputStream(completeFileName);
            fos.write(file.getFileItem().get());
            fos.close();
        } catch (final Exception exp) {
            LOG.error("Error in uploading file {}",exp.getMessage(), exp);
        }
    }

    protected void populatePage(final Model model, final String pageId) throws CMSItemNotFoundException {
        final ContentPageModel contentPage = getContentPageForLabelOrId(pageId);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
    }

    @ModelAttribute("countries")
    public Collection<CountryData> getCountries() {
        return getCheckoutFacade().getDeliveryCountriesAndRegions();
    }

    @ModelAttribute("profileAreas")
    private Collection<SelectOption> getProfileAreas() {
        final Collection<SelectOption> profileAreas = new ArrayList<SelectOption>();
        final Locale locale = new Locale(getCurrentLanguage().getIsocode());
        for (final String key : PROFILE_AREAS_KEYS) {
            final String message = getMessageSource().getMessage(key, null, locale);
            profileAreas.add(new SelectOption(message, message));
        }

        return profileAreas;
    }

    /**
     * Convert the {@code EducationRegistrationForm} to a {@code DistEducationRegistrationData}
     *
     * @param source
     *            the {@code EducationRegistrationForm} source data
     */
    private DistEducationRegistrationData convert(final EducationRegistrationForm source) {
        final DistEducationRegistrationData target = new DistEducationRegistrationData();
        // Contact data
        target.setProfileArea(source.getProfileArea());
        target.setContactFirstName(source.getContactFirstName());
        target.setContactLastName(source.getContactLastName());
        target.setContactAddress1(source.getContactAddress1());
        target.setContactAddress2(source.getContactAddress2());
        target.setContactZip(source.getContactZip());
        target.setContactPlace(source.getContactPlace());
        target.setContactCountry(source.getContactCountry());
        target.setContactEmail(source.getContactEmail());
        target.setContactPhoneNumber(source.getContactPhoneNumber());
        target.setContactMobileNumber(source.getContactMobileNumber());

        // Institution data
        target.setInstitutionName(source.getInstitutionName());
        target.setInstitutionAddress1(source.getInstitutionAddress1());
        target.setInstitutionAddress2(source.getInstitutionAddress2());
        target.setInstitutionZip(source.getInstitutionZip());
        target.setInstitutionPlace(source.getInstitutionPlace());
        target.setInstitutionCountry(source.getInstitutionCountry());
        target.setInstitutionPhoneNumber(source.getInstitutionPhoneNumber());
        target.setInstitutionEmail(source.getInstitutionEmail());

        return target;
    }

    protected DistEducationRegistrationRequestEvent createRegistrationEvent(final EducationRegistrationForm educationRegistrationForm) {
        final DistEducationRegistrationRequestEvent educationRegistrationEvent = new DistEducationRegistrationRequestEvent();
        BeanUtils.copyProperties(educationRegistrationForm, educationRegistrationEvent);
        return educationRegistrationEvent;
    }

    // Getters & Setters

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(final Validator validator) {
        this.validator = validator;
    }

    public DistCheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(final DistCheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    public DistEducationFacade getEducationFacade() {
        return educationFacade;
    }

    public void setEducationFacade(final DistEducationFacade educationFacade) {
        this.educationFacade = educationFacade;
    }

    @Override
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Override
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
