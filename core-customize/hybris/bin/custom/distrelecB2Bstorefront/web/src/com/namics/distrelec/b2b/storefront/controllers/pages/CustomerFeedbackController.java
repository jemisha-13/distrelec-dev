/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.namics.distrelec.b2b.core.customer.error.feedback.data.DistCustomerErrorFeedbackData;
import com.namics.distrelec.b2b.facades.errorFeedback.DistCustomerFeedbackFacade;
import com.namics.distrelec.b2b.storefront.forms.ErrorFeedbackForm;

/*
 * This controller eventually calls the INSERT SERVICE. Accepts Error Report, Error reason and description. Saves it to INSERT MODEL. RETURNS OK OR ERROR
 */
@Controller
@RequestMapping("/errorfeedback")
public class CustomerFeedbackController extends AbstractPageController {

    @Autowired
    private DistCustomerFeedbackFacade feedbackFacade;

    private static final String DESCRIPTION_LENGTH_CONTROL = "length.of.error.feedback.description";
    private static final String NAME_LENGTH_CONTROL = "length.of.error.feedback.description";

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody ErrorFeedbackForm returnProducts(@RequestParam(value = "errorfeedback", required = true)
    final String errorfeedback) throws Exception {
        final ErrorFeedbackForm errorFeedbackForm = new ObjectMapper().readValue(errorfeedback, ErrorFeedbackForm.class);
        final DistCustomerErrorFeedbackData distCustomerErrorFeedbackData = new DistCustomerErrorFeedbackData();
        populate(errorFeedbackForm, distCustomerErrorFeedbackData);
        final ErrorFeedbackForm errorFeedbackResponse = new ErrorFeedbackForm();
        if (getFeedbackFacade().saveFeedbackReport(distCustomerErrorFeedbackData)) {
            errorFeedbackResponse.setStatus(HttpStatus.OK.toString());
        } else {
            errorFeedbackResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        getFeedbackFacade().disseminateFeedbackRepor(distCustomerErrorFeedbackData);
        return errorFeedbackForm;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorFeedbackForm> handleMethodArgumentNotValidException(MethodArgumentNotValidException error) {
        ErrorFeedbackForm errorFeedbackResponse = new ErrorFeedbackForm();
        if (error.getBindingResult().hasErrors()) {
            List<String> errorList = new ArrayList<String>();
            for (ObjectError object : error.getBindingResult().getAllErrors()) {
                String errorMessage = object.getDefaultMessage();
                errorList.add(errorMessage);
            }
            errorFeedbackResponse.setErrors(errorList);
            errorFeedbackResponse.setStatus(HttpStatus.BAD_REQUEST.toString());
        }
        ResponseEntity<ErrorFeedbackForm> errorFeedbackFormResponse = new ResponseEntity<ErrorFeedbackForm>(errorFeedbackResponse, HttpStatus.BAD_REQUEST);
        return errorFeedbackFormResponse;
    }

    private void populate(final ErrorFeedbackForm request, final DistCustomerErrorFeedbackData data) {
        data.setProductId(normalizeProductCode(request.getProductID()));
        data.setErrorReason(request.getErrorReason());
        data.setErrorDescription(request.getErrorDescription());
        data.setCustomerEmailId(request.getCustomerEmailId());
        data.setCustomerName(request.getCustomerName());
    }

    public DistCustomerFeedbackFacade getFeedbackFacade() {
        return feedbackFacade;
    }

    public void setFeedbackFacade(final DistCustomerFeedbackFacade feedbackFacade) {
        this.feedbackFacade = feedbackFacade;
    }
}
