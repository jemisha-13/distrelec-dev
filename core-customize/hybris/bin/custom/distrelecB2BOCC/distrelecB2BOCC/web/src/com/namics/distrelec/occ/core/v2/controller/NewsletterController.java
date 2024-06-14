package com.namics.distrelec.occ.core.v2.controller;

import java.util.Collection;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.facades.newsletter.DistNewsletterFacade;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.occ.core.v2.forms.NewsletterSubscribeForm;
import com.namics.distrelec.occ.core.v2.response.NewsletterSubscribeResponse;
import com.namics.distrelec.occ.core.v2.response.ws.dto.NewsletterSubscribeResponseWsDTO;

import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Newsletter")
@RequestMapping(value = "/{baseSiteId}/newsletter")
public class NewsletterController extends BaseController {

    private static final Logger LOG = LogManager.getLogger(NewsletterController.class);

    @Autowired
    private DistNewsletterFacade newsletterFacade;

    @ModelAttribute("titles")
    public Collection<TitleData> getTitles() {
        return getUserFacade().getTitles();
    }

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    @Operation(operationId = "subscribeNewsletter", summary = "subscribe newsletter ", description = "Functionality of the subscripting of newsletter")
    @ApiBaseSiteIdParam
    public ResponseEntity<NewsletterSubscribeResponseWsDTO> subscribeNewsletter(@Valid NewsletterSubscribeForm form, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                String errorMessage = getMessageSource().getMessage("form.global.error", null, getI18nService().getCurrentLocale());
                NewsletterSubscribeResponse response = new NewsletterSubscribeResponse.Builder().withErrorMessage(errorMessage).build();
                NewsletterSubscribeResponseWsDTO responseWsDTO = getDataMapper().map(response, NewsletterSubscribeResponseWsDTO.class);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWsDTO);
            }

            boolean isNewsletterSubscriptionHandled = handleBloomreachNewsletterSubscription(form);

            if (isNewsletterSubscriptionHandled) {
                return ResponseEntity.status(HttpStatus.OK).body(getDataMapper().map(
                                                                                     new NewsletterSubscribeResponse.Builder().isDoubleOptIn(isShowDoubleOptInPopup())
                                                                                                                              .withErrorMessage("").build(),
                                                                                     NewsletterSubscribeResponseWsDTO.class));
            } else {
                String errorMessage = getMessageSource().getMessage("newsletter.error.general", null, getI18nService().getCurrentLocale());
                NewsletterSubscribeResponse response = new NewsletterSubscribeResponse.Builder().withErrorMessage(errorMessage).build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getDataMapper().map(response, NewsletterSubscribeResponseWsDTO.class));
            }
        } catch (Exception e) {
            String errorMessage = getMessageSource().getMessage("newsletter.error.general", null, getI18nService().getCurrentLocale());
            NewsletterSubscribeResponse response = new NewsletterSubscribeResponse.Builder().withErrorMessage(errorMessage).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getDataMapper().map(response, NewsletterSubscribeResponseWsDTO.class));
        }
    }

    private boolean handleBloomreachNewsletterSubscription(final NewsletterSubscribeForm form) {
        final String email = form.getEmail();
        DistConsentData consentData = new DistConsentData();
        consentData.setUid(email);
        consentData.setIsAnonymousUser(userFacade.isAnonymousUser());
        consentData.setActiveSubscription(Boolean.TRUE);
        consentData.setIsRegistration(true);
        consentData.setPersonalisationSubscription(form.isPersonalization());
        consentData.setPlacement(form.getPlacement());
        boolean newsletterSubscribed = newsletterFacade.handleBloomreachNewsletterSubscription(consentData);
        if (newsletterSubscribed) {
            newsletterFacade.optInForAllObsolescenceEmailsForExistingUser(email);
            return true;
        }
        return false;
    }

}
