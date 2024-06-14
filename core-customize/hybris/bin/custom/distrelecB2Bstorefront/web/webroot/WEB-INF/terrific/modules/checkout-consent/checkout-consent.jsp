<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<section class="card-wrapper">
    <h2 id="checkout.consent.stayTuned"><spring:message code="checkout.consent.stayTuned"/></h2>
    <p id="checkout.consent.signUpInfo"><spring:message code="checkout.consent.signUpInfo"/></p>

    <div class="checkout-consent-form">
        <div class="checkbox-group">
            <input id="checkout.consent.personalRecommendation" class="checkbox js-checkoutConsentCheckbox" type="checkbox">
            <label for="checkout.consent.personalRecommendation">
                <spring:message code="checkout.consent.personalRecommendation"/>
            </label>
        </div>

        <div class="form-group inline-form-group">
            <input required value="${user.email}" placeholder="<spring:theme code="text.preferences.newsletter.email" text="" var="sPlaceholder" />" class="html-validate-email field js-checkoutConsentEmail" type="email">
            <button type="button" class="mat-button mat-button--action-green btn-success js-submitCheckoutConsent" id="checkout.consent.signUpNow" data-aainteraction="newsletter subscribe"><spring:message code="checkout.consent.signUpNow"/></button>
        </div>
        <p id="checkout.consent.thankyou" class="js-checkoutConsentSuccessMessage success-message hidden">
            <spring:message code="subscribe.popup.thankyou"/>
        </p>
    </div>

    <c:url value="/data-protection/cms/datenschutz" var="privacyLink"/>
    <p id="checkout.consent.privacy" class="small-note"><spring:message code="checkout.consent.privacy" arguments="${privacyLink}"/></p>
</section>

<script class="js-checkoutConsentEmailError" type="text/template">
    <spring:message code="validate.error.email" />
</script>

<mod:captcha htmlClasses="js-checkoutConsentRecaptcha"/>
