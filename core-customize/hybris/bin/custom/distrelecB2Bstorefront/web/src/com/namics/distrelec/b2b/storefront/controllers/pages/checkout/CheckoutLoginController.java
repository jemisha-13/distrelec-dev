package com.namics.distrelec.b2b.storefront.controllers.pages.checkout;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import de.hybris.platform.commercefacades.order.data.CartData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.captcha.CaptchaService;
import com.namics.distrelec.b2b.core.service.captcha.CaptchaType;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.customer.exceptions.DuplicateEmailException;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.storesession.data.ShoppingSettingsCookieData;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractLoginPageController;
import com.namics.distrelec.b2b.storefront.controllers.util.ShopSettingsUtil;
import com.namics.distrelec.b2b.storefront.forms.ForgottenPwdForm;
import com.namics.distrelec.b2b.storefront.forms.GuestForm;
import com.namics.distrelec.b2b.storefront.forms.UpdatePwdForm;
import com.namics.distrelec.b2b.storefront.response.FieldErrorResponse;
import com.namics.distrelec.b2b.storefront.security.GUIDCookieStrategy;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Checkout Login Controller. Handles login and register for the checkout flow.
 */
@Controller
@RequestMapping(value = "/login/checkout")
public class CheckoutLoginController extends AbstractLoginPageController {

    private static final Logger LOG = LoggerFactory.getLogger(CheckoutLoginController.class);

    private static final String REDIRECT_LOGIN_CHECKOUT = "redirect:/login/checkout";

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    @Qualifier("b2bCheckoutFacade")
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    private DistCustomerFacade customerFacade;

    @Autowired
    private GUIDCookieStrategy guidCookieStrategy;

    @Override
    protected String getLoginView() {
        return ControllerConstants.Views.Pages.Checkout.CheckoutLoginPage;
    }

    @Override
    protected String getSuccessRedirect() {
        return "/checkout";
    }

    @Override
    protected AbstractPageModel getLoginCmsPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId("checkout-login");
    }

    @GetMapping
    public String getLogin(@RequestParam(value = "error", defaultValue = "false") final boolean loginError,
                           @RequestParam(value = "qd", required = false, defaultValue = "") final String email, final HttpSession session, final Model model,
                           final HttpServletRequest request) throws CMSItemNotFoundException {
        final Boolean doiWarn = sessionService.getAttribute(DistConstants.Session.DOUBLE_OPT_IN);
        if (BooleanUtils.isTrue(doiWarn)) {
            sessionService.setAttribute(DistConstants.Session.DOUBLE_OPT_IN, Boolean.FALSE);
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        if (null != digitalDatalayer) {
            digitalDatalayer.setEventName(DigitalDatalayer.EventName.CHECKOUT_START);

        }

        addGlobalModelAttributes(model, request);
        return getLogin(loginError, session, model, decodeBase64(email));
    }

    @PostMapping(value = "/pw/request/async", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FieldErrorResponse> requestPasswordAsync(@Valid final ForgottenPwdForm form, final BindingResult bindingResult,
                                                                   final HttpServletRequest request) {
        sessionService.setAttribute(WebConstants.DO_NOT_STORE_REFERRER, Boolean.TRUE);
        return passwordRequestAsync(form, bindingResult, request);
    }

    @GetMapping(value = "/pw/change")
    public String getChangePassword(@RequestParam(required = false) final String token, @RequestParam(value = "lang", required = false) final String language,
                                    final Model model, final HttpServletRequest request, final HttpServletResponse response,
                                    final HttpSession session) throws CMSItemNotFoundException {
        ShoppingSettingsCookieData shoppingSettingsCookieData = getShopSettingsCookie(request);
        if (shoppingSettingsCookieData != null) {
            shoppingSettingsCookieData.setLanguage(language);
            Attributes.SHOP_SETTINGS.setValue(request, response,
                                              ShopSettingsUtil.createCookieWithSessionValues(shoppingSettingsCookieData.getChannel(),
                                                                                             shoppingSettingsCookieData.getLanguage(),
                                                                                             shoppingSettingsCookieData.getCountry(),
                                                                                             shoppingSettingsCookieData.getCookieMessageConfirmed(),
                                                                                             shoppingSettingsCookieData.getUseIconView(),
                                                                                             shoppingSettingsCookieData.getUseListView(),
                                                                                             shoppingSettingsCookieData.getUseDetailView(),
                                                                                             shoppingSettingsCookieData.getAutoApplyFilter(),
                                                                                             shoppingSettingsCookieData.getItemsPerPage()));
        }
        model.addAttribute("isResetPassword", Boolean.TRUE);
        model.addAttribute("token", token);
        model.addAttribute("isTokenInvalid", !customerFacade.validateResetPasswordToken(token));
        addGlobalModelAttributes(model, request);
        return getLogin(false, session, model, StringUtils.EMPTY);
    }

    private ResponseEntity<FieldErrorResponse> passwordRequestAsync(final ForgottenPwdForm form, final BindingResult bindingResult,
                                                                    final HttpServletRequest request) {
        captchaService.increaseCurrentAttempt(CaptchaType.CHECKOUT_LOGIN_PASSWORD_RESET);

        if (bindingResult.hasErrors()) {
            String errorMessage = getLocalizedMessage("forgottenPwd.email.invalid");
            return badRequest().body(new FieldErrorResponse.Builder().withMessage(errorMessage).build());
        }

        if (captchaService.isLimitExceeded(CaptchaType.CHECKOUT_LOGIN_PASSWORD_RESET) && !getCaptchaUtil().validateReCaptcha(request)) {
            String errorMessage = getLocalizedMessage("support.captchaError");
            return new ResponseEntity<>(new FieldErrorResponse.Builder().withMessage(errorMessage).build(), HttpStatus.UNAUTHORIZED);
        }

        try {
            customerFacade.checkoutForgottenPassword(StringUtils.lowerCase(form.getEmail()));
        } catch (final UnknownIdentifierException uie) {
            LOG.error("An error occurred for passwordRequest for Email: {}", form.getEmail());
            return ok().build();
        } catch (final DuplicateEmailException duplicateEmailException) {
            LOG.error("An error occurred due to Duplicate Email: {}", form.getEmail());
            return ok().build();
        }
        return ok().build();
    }

    @PostMapping(value = "/pw/change/async", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FieldErrorResponse> changePassword(@Valid final UpdatePwdForm form, final BindingResult bindingResult) {
        return passwordChangeAsync(form, bindingResult);
    }

    @PostMapping("/guest")
    public String guestCheckout(@Valid GuestForm guestForm, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response,
                                HttpSession session, Model model, RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        captchaService.increaseCurrentAttempt(CaptchaType.GUEST_CHECKOUT);

        if (captchaService.isLimitExceeded(CaptchaType.GUEST_CHECKOUT) && !getCaptchaUtil().validateReCaptcha(request)) {
            session.setAttribute(WebConstants.SHOW_CAPTCHA, Boolean.TRUE);
            redirectAttributes.addFlashAttribute("captchaError", getLocalizedMessage("support.captchaError"));
            return REDIRECT_LOGIN_CHECKOUT;
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("guestCheckoutError", getLocalizedMessage("register.email.invalid"));
            return REDIRECT_LOGIN_CHECKOUT;
        }

        if (getCustomerFacade().doesCustomerExistForUid(guestForm.getEmail())) {
            redirectAttributes.addFlashAttribute("guestCheckoutError", getLocalizedMessage("login.checkout.guest.existing.address.error"));
            return REDIRECT_LOGIN_CHECKOUT;
        }

        if (getOrderFacade().isNumberOfGuestSuccessfulPurchasesExceeded(guestForm.getEmail())) {
            redirectAttributes.addFlashAttribute("guestCheckoutError", getLocalizedMessage("login.checkout.guest.registration.info"));
            return REDIRECT_LOGIN_CHECKOUT;
        }

        try {
            final DistRegisterData data = createRegisterData(guestForm);

            customerFacade.createGuestUserForAnonymousCheckout(getMessageSource().getMessage("text.guest.customer", null, getI18nService().getCurrentLocale()),
                                                               data);
            guidCookieStrategy.setCookie(request, response);
            getSessionService().setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);
        } catch (DuplicateUidException e) {
            return REDIRECT_LOGIN_CHECKOUT;
        }
        return getLogin(false, session, model, StringUtils.EMPTY);
    }

    private DistRegisterData createRegisterData(@Valid GuestForm guestForm) {
        final DistRegisterData data = new DistRegisterData();
        data.setLogin(guestForm.getEmail());
        data.setEmail(guestForm.getEmail());
        final CurrencyData currency = getCurrentCurrency();
        data.setCurrencyCode(currency.getIsocode() == null ? "EUR" : currency.getIsocode());
        return data;
    }

    private ResponseEntity<FieldErrorResponse> passwordChangeAsync(final UpdatePwdForm form, final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            if (CollectionUtils.isNotEmpty(bindingResult.getGlobalErrors())) {
                String errorMessage = getLocalizedMessage("validation.checkPwd.equals");
                return badRequest().body(new FieldErrorResponse.Builder().withMessage(errorMessage).build());
            }
            String errorMessage = getLocalizedMessage("updatePwd.pwd.invalid");
            return badRequest().body(new FieldErrorResponse.Builder().withMessage(errorMessage).build());
        }
        if (StringUtils.isNotBlank(form.getToken())) {
            try {
                getCustomerFacade().updatePassword(form.getToken(), form.getPwd());
                customerFacade.confirmDoubleOptforResetPwd(form.getToken());
            } catch (final TokenInvalidatedException e) {
                String errorMessage = getLocalizedMessage("updatePwd.token.invalidated");
                LOG.debug("Update password failed due to, {}", e.getMessage());
                return badRequest().body(new FieldErrorResponse.Builder().withMessage(errorMessage).build());
            } catch (final RuntimeException e) {
                String errorMessage = getLocalizedMessage("updatePwd.token.invalid");
                LOG.debug("Update password failed due to, {}", e.getMessage());
                return badRequest().body(new FieldErrorResponse.Builder().withMessage(errorMessage).build());
            }
        }
        getSessionService().setAttribute(WebConstants.DO_NOT_STORE_REFERRER, Boolean.TRUE);
        return ok().build();
    }

    @Override
    public String getLogin(final boolean loginError, final HttpSession session, final Model model, final String user) throws CMSItemNotFoundException {
        final CartData cartData = checkoutFacade.getCheckoutCart();
        model.addAttribute("cartData", cartData);

        loadResetPasswordForm(session, model);
        loadGuestForm(model);
        loadCaptcha(session);

        prepareWebtrekkParams(model, cartData);

        return super.getLogin(loginError, session, model, user);
    }

    private void loadCaptcha(HttpSession session) {
        if (captchaService.shouldDisplayCaptcha(CaptchaType.GUEST_CHECKOUT, CaptchaType.CHECKOUT_LOGIN_PASSWORD_RESET)) {
            session.setAttribute(SHOW_CAPTCHA, Boolean.TRUE);
        }
    }

    private void loadGuestForm(Model model) {
        boolean isGuestCheckoutEnabled = checkoutFacade.isGuestCheckoutEnabledForSite();

        if (isGuestCheckoutEnabled && !model.containsAttribute("guestCheckoutForm")) {
            model.addAttribute("guestCheckoutForm", new GuestForm());
            model.addAttribute("isGuestCheckoutEnabled", Boolean.TRUE);
        }
    }

    private void loadResetPasswordForm(HttpSession session, Model model) {
        if (!model.containsAttribute("forgottenPwdForm")) {
            final String username = (String) session.getAttribute("SPRING_SECURITY_LAST_USERNAME");
            final ForgottenPwdForm userForm = new ForgottenPwdForm();
            if (username != null && username.contains("@")) {
                userForm.setEmail(username);
            }
            model.addAttribute(userForm);
        }
    }
}
