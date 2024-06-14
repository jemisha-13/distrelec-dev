<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<c:set var="action" value="/login/checkout/guest"/>

<div class="login__card has-margin-top">
    <div class="login__card__title">
        <h2 id="guestCustomerTitle"><spring:theme code="login.checkout.guest.customer.title"/></h2>
        <p id="guestCustomerSubtitle">
            <spring:theme code="login.checkout.guest.customer.subtitle"/><br><spring:theme
                code="login.checkout.guest.customer.subtitle2"/>
        </p>
    </div>

    <c:if test="${isExportShop}">
        <div class="login__card__info" id="guestCheckoutInfo">
            <i class="fa fa-info-circle"></i><spring:theme code="login.checkout.guest.customer.eu"/>
        </div>
    </c:if>

    <form:form class="js-guest-checkout-form" method="post" action="${action}" modelAttribute="guestCheckoutForm">
        <div class="login__card__form-group js-guest-checkout-form-group ${not empty guestCheckoutError or not empty captchaError ? 'is-error' : ''}">
            <formUtil:formLabel idKey="guestCheckoutEmailLabel" labelKey="newcheckout.email.label" path="email"/>
            <div class="p-relative">
                <formUtil:formInputBox idKey="email" path="email"
                                       placeHolderKey="Enter email"
                                       inputCSS="field validate-email js-guest-checkout-email ${not empty guestCheckoutError or not empty captchaError ? 'error' : ''}"
                                       mandatory="true"/>

                <c:if test="${not empty guestCheckoutError}">
                    <div class="field-msgs">
                        <div id="guest.checkout.error" class="error">${guestCheckoutError}</div>
                    </div>
                </c:if>

                <c:if test="${not empty captchaError}">
                    <div class="field-msgs">
                        <div id="guest.checkout.captcha.error" class="error">${captchaError}</div>
                    </div>
                </c:if>

                <i class="js-tickItemError tickItemError fa fa-times"></i>
            </div>
            <small id="guest.checkout.input.note" class="guest-input-note"><spring:theme code="login.checkout.guest.customer.input.note"/></small>
        </div>

        <button id="checkoutAsGuestBtn" class="ux-btn ux-btn--white-light js-guest-checkout-submit" type="submit">
            <spring:theme code="login.checkout.guest.customer.button"/>
        </button>

        <c:if test="${showLoginCaptcha}">
            <div class="recaptcha">
                <mod:captcha/>
            </div>
        </c:if>
    </form:form>
</div>
