package com.namics.distrelec.b2b.storefront.controllers.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.forms.ContactForm;

@Controller
public class DistContactNumberController {

    private static final String CONTACT_CONTROLLER_MAPPING = "/number/validatecontact";

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private Validator validator;

    private static final PhoneNumberUtil PHONENUMBERUTIL = PhoneNumberUtil.getInstance();

    @RequestMapping(value = CONTACT_CONTROLLER_MAPPING, method = RequestMethod.GET, produces = { "application/json" })
    String validateContactNumber(@RequestParam(value = "inputNumber", required = true)
    final String inputNumber, @RequestParam(value = "numberType", required = true)
    final String numberType, @RequestParam(value = "countryCode", required = true)
    final String countryCode, final Model model) throws NumberParseException {
        storeSessionFacade.setCurrentCountry(countryCode);
        ContactForm contactForm = new ContactForm();
        if (PhoneType.MOBILE.toString().equalsIgnoreCase(numberType)) {
            contactForm.setMobileNumber(inputNumber);
        }
        if (PhoneType.LANDLINE.toString().equalsIgnoreCase(numberType)) {
            contactForm.setPhoneNumber(inputNumber);
        }
        Errors errors = new BeanPropertyBindingResult(contactForm, "contactForm");
        ValidationUtils.invokeValidator(getValidator(), contactForm, errors);
        if (errors.hasErrors()) {
            model.addAttribute("status", ValidationStatus.NOK.toString());
            model.addAttribute("suggestedNumber", inputNumber);
            if (PhoneType.LANDLINE.toString().equalsIgnoreCase(numberType)) {
                model.addAttribute("message", errors.getFieldError("phoneNumber").getDefaultMessage());
            }
            if (PhoneType.MOBILE.toString().equalsIgnoreCase(numberType)) {
                model.addAttribute("message", errors.getFieldError("mobileNumber").getDefaultMessage());
            }
        } else {
            model.addAttribute("status", ValidationStatus.OK.toString());
            final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(inputNumber, countryCode);
            model.addAttribute("suggestedNumber", phoneNumberToString(phoneNumber));
            model.addAttribute("message", countryCode);
        }
        return ControllerConstants.Views.Fragments.General.ContactValidation;
    }

    public static String phoneNumberToString(final PhoneNumber phoneNumber) {
        return PHONENUMBERUTIL.format(phoneNumber, PhoneNumberFormat.INTERNATIONAL);
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    private enum PhoneType {

        MOBILE("mobile"), LANDLINE("landline");

        private final String type;

        PhoneType(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
    
    private enum ValidationStatus {

        OK("ok"), NOK("nok");

        private final String text;

        ValidationStatus(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
