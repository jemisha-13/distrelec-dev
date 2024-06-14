/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.core.enums.DistOnlineSurveyQuestionType;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyAnswerData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyQuestionData;
import com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveySectionData;
import com.namics.distrelec.b2b.facades.survey.DistOnlineSurveyFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.OnlineSurveyForm;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * {@code OnlineSurveyPageController}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
@Controller
@RequestMapping("/survey")
public class OnlineSurveyPageController extends AbstractPageController {

    private static final Logger LOG = Logger.getLogger(OnlineSurveyPageController.class);
    private static final String ONLINE_SURVEY_PAGE = "onlineSurveyPage";
    private static final String ONLINE_SURVEY_SUCCESS_PAGE = "onlineSurveySuccessPage";
    private static final String ONLINE_SURVEY_ERROR_PAGE = "onlineSurveyErrorPage";
    private static final EmailValidator EMAIL_VALIDATOR = new EmailValidator();

    @Autowired
    private DistOnlineSurveyFacade onlineSurveyFacade;
    @Autowired
    private CMSSiteService cmsSiteService;

    /**
     * By default we return survey with version {@code v0}.
     *
     * @param model
     * @param request
     * @param response
     * @return the survey page.
     * @throws CMSItemNotFoundException
     * @throws UnsupportedEncodingException
     * @see #getForVersion(String, Model, HttpServletRequest, HttpServletResponse)
     */
    @RequestMapping(method = { RequestMethod.GET })
    public String get(final Model model, final HttpServletRequest request, final HttpServletResponse response)
            throws CMSItemNotFoundException, UnsupportedEncodingException {
        // Return survey with version = v0
        return getForVersion("v0", model, request, response);
    }

    @RequestMapping(method = { RequestMethod.POST })
    public String post(final OnlineSurveyForm surveyForm, final BindingResult bindingResult, final Model model, final HttpServletRequest request,
            final HttpServletResponse response) throws CMSItemNotFoundException, UnsupportedEncodingException {
        return postForVersion("v0", surveyForm, bindingResult, model, request, response);
    }

    /**
     * Return the survey page for the version {@code version}
     *
     * @param version
     * @param model
     * @param request
     * @param response
     * @return the survey page.
     * @throws CMSItemNotFoundException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{version}", method = { RequestMethod.GET })
    public String getForVersion(@PathVariable("version")
    final String version, final Model model, final HttpServletRequest request, final HttpServletResponse response)
            throws CMSItemNotFoundException, UnsupportedEncodingException {

        try {
            final DistOnlineSurveyData survey = onlineSurveyFacade.findByVersion(version, new Date());
            // If the CMS site list is not empty, this means that the survey is available only for some sites, otherwise it is available for
            // all sites.
            if (CollectionUtils.isNotEmpty(survey.getCmsSites()) && !survey.getCmsSites().contains(cmsSiteService.getCurrentSite().getUid())) {
                throw new UnknownIdentifierException("Survey not available for site: " + cmsSiteService.getCurrentSite().getUid());
            }
            addGlobalModelAttributes(model, request);
            model.addAttribute("survey", survey);
            final OnlineSurveyForm surveyForm = new OnlineSurveyForm();
            prepareForm(surveyForm, survey, request);
            model.addAttribute("surveyForm", surveyForm);
            return ControllerConstants.Views.Pages.Survey.OnlineSurveyPage;
        } catch (final Exception exp) {
            LOG.error(exp.getMessage(), exp);
            addGlobalModelAttributes(model, request);
            model.addAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, "online.survey.notfound");
            return ControllerConstants.Views.Pages.Survey.OnlineSurveyErrorPage;
        } finally {
            final ContentPageModel onlineSurveyPage = getContentPageForLabelOrId(ONLINE_SURVEY_PAGE);
            storeCmsPageInModel(model, onlineSurveyPage);
            setUpMetaDataForContentPage(model, onlineSurveyPage);
        }
    }

    /**
     *
     * @param version
     * @param surveyForm
     * @param bindingResult
     * @param model
     * @param request
     * @param response
     * @return
     * @throws CMSItemNotFoundException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{version}", method = { RequestMethod.POST })
    public String postForVersion(@PathVariable("version")
    final String version, @ModelAttribute("surveyForm")
    final OnlineSurveyForm surveyForm, final BindingResult bindingResult, final Model model, final HttpServletRequest request,
            final HttpServletResponse response) throws CMSItemNotFoundException, UnsupportedEncodingException {

        DistOnlineSurveyData survey = null;

        try {
            survey = onlineSurveyFacade.findByVersion(version, new Date());
            // If the CMS site list is not empty, this means that the survey is available only for some sites, otherwise it is available for
            // all sites.
            if (CollectionUtils.isNotEmpty(survey.getCmsSites()) && !survey.getCmsSites().contains(cmsSiteService.getCurrentSite().getUid())) {
                throw new UnknownIdentifierException("Survey not available for site: " + cmsSiteService.getCurrentSite().getUid());
            }

            addGlobalModelAttributes(model, request);
            model.addAttribute("survey", survey);
            prepareForm(surveyForm, survey, request);
            model.addAttribute("surveyForm", surveyForm);

            if (!getCaptchaUtil().validateReCaptcha(request)) {
                model.addAttribute("captchaError", Boolean.TRUE);
                GlobalMessages.addErrorMessage(model, "form.captcha.error");
            }

            // Validation
            validate(survey, surveyForm, request, bindingResult);

            if (bindingResult.hasErrors()) {
                GlobalMessages.addErrorMessage(model, "form.global.error");
                final ContentPageModel onlineSurveyPage = getContentPageForLabelOrId(ONLINE_SURVEY_PAGE);
                storeCmsPageInModel(model, onlineSurveyPage);
                setUpMetaDataForContentPage(model, onlineSurveyPage);
                return ControllerConstants.Views.Pages.Survey.OnlineSurveyPage;
            }

            // Create and persist the survey answers.
            onlineSurveyFacade.create(createSurveyAnswers(surveyForm, survey));

        } catch (final Exception exp) {
            LOG.error(exp.getMessage(), exp);
            addGlobalModelAttributes(model, request);
            model.addAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, "online.survey.notfound");
            return ControllerConstants.Views.Pages.Survey.OnlineSurveyErrorPage;
        }

        model.addAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, "online.survey.success");
        final ContentPageModel onlineSurveyPage = getContentPageForLabelOrId(ONLINE_SURVEY_SUCCESS_PAGE);
        storeCmsPageInModel(model, onlineSurveyPage);
        setUpMetaDataForContentPage(model, onlineSurveyPage);
        return ControllerConstants.Views.Pages.Survey.OnlineSurveySuccessPage;
    }

    /**
     * Validate the submitted form.
     *
     * @param survey
     * @param form
     * @param request
     * @param bindingResult
     * @return {@code true} if the form is valid, {@code false} otherwise.
     */
    private boolean validate(final DistOnlineSurveyData survey, final OnlineSurveyForm form, final HttpServletRequest request,
            final BindingResult bindingResult) {
        boolean valid = true;
        String email_question_uid = null;
        String email_confirm_question_uid = null;
        for (final DistOnlineSurveySectionData section : survey.getSections()) {
            for (final DistOnlineSurveyQuestionData question : section.getQuestions()) {
                if (question.isMandatory() && !form.hasValue(question.getUid())) {
                    bindingResult.rejectValue("questions['" + question.getUid() + "']", "online.survey.input.empty");
                    valid = false;
                } else if (question.getType() == DistOnlineSurveyQuestionType.EMAIL && !EMAIL_VALIDATOR.isValid(form.getValue(question.getUid()), null)) {
                    bindingResult.rejectValue("questions['" + question.getUid() + "']", "validate.error.email");
                    valid = false;
                }

                if (question.getName().equals("email")) {
                    email_question_uid = question.getUid();
                } else if (question.getName().equals("confirm_email")) {
                    email_confirm_question_uid = question.getUid();
                }
            }
        }

        if (email_question_uid != null && email_confirm_question_uid != null && form.hasValue(email_question_uid) && form.hasValue(email_confirm_question_uid)
                && !form.getValue(email_question_uid).equals(form.getValue(email_confirm_question_uid))) {
            bindingResult.rejectValue("questions['" + email_confirm_question_uid + "']", "validate.error.email.notmatch");
            valid = false;
        }

        return valid;
    }

    /**
     * Prepare the form to the front end page.
     *
     * @param form
     *            the form to prepare.
     * @param survey
     *            the source survey.
     */
    private void prepareForm(final OnlineSurveyForm form, final DistOnlineSurveyData survey, final HttpServletRequest request) {
        for (final DistOnlineSurveySectionData section : survey.getSections()) {
            for (final DistOnlineSurveyQuestionData question : section.getQuestions()) {
                if (!form.hasValue(question.getUid())) {
                    form.getQuestions().put(question.getUid(),
                            request.getParameter(question.getUid()) != null ? request.getParameter(question.getUid()) : StringUtils.EMPTY);
                }
            }
        }
    }

    /**
     * Creating answers data from the values of the submitted form.
     *
     * @param form
     *            the submitted form.
     * @param survey
     *            the original survey
     * @return a list of {@code DistOnlineSurveyAnswerData}
     */
    private List<DistOnlineSurveyAnswerData> createSurveyAnswers(final OnlineSurveyForm form, final DistOnlineSurveyData survey) {
        final List<DistOnlineSurveyAnswerData> answers = new ArrayList<DistOnlineSurveyAnswerData>();
        final String language = getCurrentLanguage().getIsocode();
        for (final DistOnlineSurveySectionData section : survey.getSections()) {
            for (final DistOnlineSurveyQuestionData question : section.getQuestions()) {
                if (question.isPersistentAnswer()) {
                    final DistOnlineSurveyAnswerData answer = new DistOnlineSurveyAnswerData();
                    answer.setUid(UUID.randomUUID().toString());
                    answer.setQuestion(question);
                    answer.setSurvey(survey);
                    // Case when the user did not check the checkbox
                    if (question.getType() == DistOnlineSurveyQuestionType.CHECKBOX && !form.hasValue(question.getUid())) {
                        answer.setValue("No");
                    } else {
                        answer.setValue(form.getValue(question.getUid()));
                    }
                    answer.setLanguage(language);
                    answers.add(answer);
                }
            }
        }

        return answers;
    }

}
