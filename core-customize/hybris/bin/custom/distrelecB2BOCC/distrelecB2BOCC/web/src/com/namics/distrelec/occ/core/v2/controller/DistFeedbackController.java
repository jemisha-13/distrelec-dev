package com.namics.distrelec.occ.core.v2.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.namics.distrelec.b2b.core.event.DistFeedbackEvent;
import com.namics.distrelec.b2b.core.event.FeedbackDataDto;
import com.namics.distrelec.b2b.facades.feedback.DistFeedbackFacade;
import com.namics.distrelec.occ.core.v2.forms.FeedbackForm;
import com.namics.distrelec.occ.core.v2.forms.ZeroResultSearchFeedbackForm;
import com.namics.hybris.ffsearch.util.XSSFilterUtil;

import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Feedback")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/feedback")
public class DistFeedbackController extends BaseCommerceController {

    @Autowired
    private DistFeedbackFacade distFeedbackFacade;

    @PostMapping(value = "/zeroResultSearch")
    @Operation(operationId = "zeroResultSearchFeedback", summary = "Zero Result Search Feedback", description = "Zero Result Search Feedback")
    @ApiBaseSiteIdParam
    @ResponseBody
    public ResponseEntity<String> submitZeroSearchFeedback(
            @Parameter(description = "Zero Result Search Feedback Form", required = true) @RequestBody @Valid ZeroResultSearchFeedbackForm feedbackForm,
            @PathVariable String baseSiteId) {
        FeedbackDataDto feedbackData = new FeedbackDataDto(feedbackForm.getEmail(),
                                                           feedbackForm.getManufacturer(),
                                                           feedbackForm.getManufacturerTypeOtherName(),
                                                           feedbackForm.getManufacturerType(),
                                                           feedbackForm.getProductName(),
                                                           feedbackForm.getTellUsMore(),
                                                           feedbackForm.getSearchTerm());
        distFeedbackFacade.submitFeedbackData(feedbackData);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/general")
    @Operation(operationId = "General Feedback", summary = "General Feedback", description = "General Feedback")
    @ApiBaseSiteIdParam
    @ResponseBody
    public ResponseEntity<String> submitGeneralFeedback(
            @Parameter(description = "General Feedback Form", required = true) @RequestBody @Valid FeedbackForm feedbackForm,
            @PathVariable String baseSiteId) {
        distFeedbackFacade.sendFeedback(createEvent(feedbackForm));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private DistFeedbackEvent createEvent(FeedbackForm feedbackForm) {
        DistFeedbackEvent event = new DistFeedbackEvent(XSSFilterUtil.filter(feedbackForm.getName()),
                                                        XSSFilterUtil.filter(feedbackForm.getEmail()),
                                                        XSSFilterUtil.filter(feedbackForm.getPhone()),
                                                        XSSFilterUtil.filter(feedbackForm.getFeedback()));
        event.setEmailDisplayName(getMessageSource().getMessage("feedback.displayName", null, "Feedback", getI18nService().getCurrentLocale()));
        event.setEmailSubjectMsg(getMessageSource().getMessage("feedback.emailSubject", null, "Feedback", getI18nService().getCurrentLocale()));
        event.setFromDisplayName(getMessageSource().getMessage("feedback.fromDisplayName", null, "Feedback from", getI18nService().getCurrentLocale()));
        return event;
    }
}
