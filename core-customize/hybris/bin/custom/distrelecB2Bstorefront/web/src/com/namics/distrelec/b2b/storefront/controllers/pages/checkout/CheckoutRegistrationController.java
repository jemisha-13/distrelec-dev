package com.namics.distrelec.b2b.storefront.controllers.pages.checkout;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.service.captcha.CaptchaService;
import com.namics.distrelec.b2b.core.service.captcha.CaptchaType;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.pages.CustomerRegistrationPageController;
import com.namics.distrelec.b2b.storefront.forms.CheckoutGuestRegisterForm;
import com.namics.distrelec.b2b.storefront.forms.RegisterB2BForm;
import com.namics.distrelec.b2b.storefront.forms.RegisterForm;
import com.namics.distrelec.b2b.storefront.response.FieldErrorResponse;
import com.namics.distrelec.b2b.storefront.security.AutoLoginStrategy;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;

@Controller
@RequestMapping(value = "/registration/checkout")
public class CheckoutRegistrationController extends CustomerRegistrationPageController {

    @Autowired
    private DistB2BOrderFacade distB2BOrderFacade;

    @Autowired
    private DistCustomerFacade distCustomerFacade;

    @Autowired
    private AutoLoginStrategy autoLoginStrategy;

    @Autowired
    private CaptchaService captchaService;

    @Override
    protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId("checkout-registration");
    }

    @Override
    protected String getView() {
        return ControllerConstants.Views.Pages.Checkout.CheckoutRegistrationPage;
    }

    @Override
    protected RegisterForm getRegistrationForm(boolean b2b) {
        final RegisterForm form = b2b ? new RegisterB2BForm() : new RegisterForm();

        form.setRegistrationType(RegistrationType.CHECKOUT.toString());
        final CurrencyData currency = getStoreSessionFacade().getDefaultCurrency();
        if (currency != null) {
            form.setCurrencyCode(currency.getIsocode());
        }
        return form;
    }

    @PostMapping(value = "/register/guest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FieldErrorResponse> registerGuest(@Valid CheckoutGuestRegisterForm checkoutGuestRegisterForm, BindingResult bindingResult,
                                                            HttpServletRequest request, HttpServletResponse response) {
        captchaService.increaseCurrentAttempt(CaptchaType.GUEST_CHECKOUT_REGISTRATION);

        if (captchaService.isLimitExceeded(CaptchaType.GUEST_CHECKOUT_REGISTRATION) && !getCaptchaUtil().validateReCaptcha(request)) {
            String errorMessage = getLocalizedMessage("support.captchaError");
            return badRequest().body(new FieldErrorResponse.Builder().withMessage(errorMessage).build());
        }

        if (bindingResult.hasErrors()) {
            if (CollectionUtils.isNotEmpty(bindingResult.getGlobalErrors())) {
                String errorMessage = getLocalizedMessage("validation.checkPwd.equals");
                return badRequest().body(new FieldErrorResponse.Builder().withMessage(errorMessage).build());
            }
            String errorMessage = getLocalizedMessage("updatePwd.pwd.invalid");
            return badRequest().body(new FieldErrorResponse.Builder().withMessage(errorMessage).build());
        }

        OrderData order = distB2BOrderFacade.getOrderDetailsForGUID(checkoutGuestRegisterForm.getOrderCode());
        if (order == null || !StringUtils.equals(checkoutGuestRegisterForm.getEmail(), order.getB2bCustomerData().getEmail())) {
            String errorMessage = getLocalizedMessage("lightboxreturnrequest.error");
            return badRequest().body(new FieldErrorResponse.Builder().withMessage(errorMessage).build());
        }

        try {
            distCustomerFacade.convertGuestToB2CAndRegisterInSMC(checkoutGuestRegisterForm.getPassword(), checkoutGuestRegisterForm.getOrderCode());
            autoLoginStrategy.login(order.getB2bCustomerData().getEmail(), checkoutGuestRegisterForm.getPassword(), request, response,
                                    RegistrationType.CHECKOUT);
        } catch (Exception e) {
            LOG.error("Error happened during converting guest user to B2C", e);
            revertSessionToAnonymous();
            String errorMessage = getLocalizedMessage("lightboxreturnrequest.error");
            return badRequest().body(new FieldErrorResponse.Builder().withMessage(errorMessage).build());
        }

        getSessionService().setAttribute(WebConstants.LOGIN_SUCCESS, Boolean.TRUE);
        return ok().build();
    }

    private void revertSessionToAnonymous() {
        // this had to be done, in case there is an error in registration or marketing subscription the customer would be rollbacked
        // this allows the user to try again, as if he had been left in the session this request would be blocked by one of the filters
        getUserService().setCurrentUser(getUserService().getAnonymousUser());
    }
}
