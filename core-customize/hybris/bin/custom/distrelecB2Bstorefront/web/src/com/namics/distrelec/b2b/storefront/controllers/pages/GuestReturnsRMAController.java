package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.namics.distrelec.b2b.facades.rma.DistReturnRequestFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.rma.GuestRMACreateRequestForm;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.servicelayer.event.EventService;

/**
 * Controller for Return request submission mail for Guest and Registered User.
 *
 * @author <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar@datwyler</a>, Distrelec
 *
 */
@Controller
@RequestMapping("/returns")
public class GuestReturnsRMAController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(GuestReturnsRMAController.class);

    private static final String RETURN_AND_CLAIMS_CMS_PAGE = "returns-and-claims";

    @Autowired
    private Validator validator;

    @Autowired
    private EventService eventService;

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private DistReturnRequestFacade distReturnRequestFacade;

    @RequestMapping(value = "/claim", method = RequestMethod.GET)
    public String getReturnAndRepair(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);

        // Webtrekk
        prepareWebtrekkParams(model, null);
        getDistWebtrekkFacade().addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);

        storeCmsPageInModel(model, getContentPageForLabelOrId(RETURN_AND_CLAIMS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(RETURN_AND_CLAIMS_CMS_PAGE));

        final ContentPageModel contentPage = getContentPageForLabelOrId(RETURN_AND_CLAIMS_CMS_PAGE);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getSimpleBreadcrumbBuilder().getBreadcrumbs(contentPage.getTitle()));

        return ControllerConstants.Views.Pages.Account.ReturnAndClaimPage;

    }

    /**
     * This method is used to send the email to support SAP team for the Guest User.
     *
     * @param guestRMACreateRequestForm
     * @return Boolean
     */
    @PostMapping(value = "/guest")
    public @ResponseBody Boolean submitReturnClaimForGuest(@Valid @ModelAttribute final GuestRMACreateRequestForm guestRMACreateRequestForm, final BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                LOG.error("Binding ERRORS: {}" , bindingResult.getFieldErrors());
                return Boolean.FALSE;
            } else {
                distReturnRequestFacade.sendGuestReturnRequestEmail(guestRMACreateRequestForm);
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            LOG.error("An error has occurred while processing your request", e);
            return Boolean.FALSE;
        }
    }

    /**
     * This method is used to send the email to support SAP team for the Registered User.
     *
     * @param createRMARequestForm
     * @param createRMAResponseData
     * @param model
     * @param request
     * @return Boolean
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Boolean submitReturnClaimForUser(
            @Valid @RequestBody final DistReturnRequestFacade.UserRMARequestDataWrapper data, BindingResult bindingResult, final HttpServletRequest request)
            throws JsonParseException, JsonMappingException, IOException {

        boolean isSubmitSuccess = false;

        try{
            if (bindingResult.hasErrors()) {
                // If error exists
                LOG.error("Binding ERRORS: {}", bindingResult.getFieldErrors());
            } else {

                // In case of no error
                distReturnRequestFacade.sendUserReturnRequestEmail(data);
                isSubmitSuccess = true;
            }
        } catch (Exception e){
            LOG.error("Unexpected exception occured when sending RMA request assistance email", e);
        }

        return isSubmitSuccess;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public SimpleBreadcrumbBuilder getSimpleBreadcrumbBuilder() {
        return simpleBreadcrumbBuilder;
    }

    public void setSimpleBreadcrumbBuilder(final SimpleBreadcrumbBuilder simpleBreadcrumbBuilder) {
        this.simpleBreadcrumbBuilder = simpleBreadcrumbBuilder;
    }
}
