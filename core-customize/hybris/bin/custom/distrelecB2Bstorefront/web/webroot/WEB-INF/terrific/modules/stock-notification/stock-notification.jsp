<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code="stock.notification.email.instruction" var="sStockNotificationEmailInstruction" />
<spring:message code="stock.notification.email.placeholder" var="sStockNotificationEmailPlaceholder" />
<spring:message code="stock.notification.email.notifyMe" var="sStockNotificationEmailNotifyMe" />
<spring:message code="stock.notification.email.assuranceText" var="sStockNotificationEmailAssuranceText" />
<spring:message code="stock.notification.email.privacyPolicy" var="sStockNotificationEmailPrivacyPolicy" />
<spring:message code="stock.notification.email.successText" var="sStockNotificationEmailSuccessText" />
<spring:message code="stock.notification.email.existingUserText" var="sStockNotificationEmailExistingUserText" />
<spring:message code="stock.notification.email.blankText" var="sStockNotificationEmailBlankText" />
<spring:message code="stock.notification.email.invalidText" var="sStockNotificationEmailInvalidText" />

<div class="stock-notification">
    <div class="stock-notification__form-content">
        <form:form id="stock_notification" class="stock-notification__form js-stock-notification-form">
            <div class="stock-notification__form-content__instruction">
                <p>
                    ${sStockNotificationEmailInstruction}
                </p>
            </div>
            <input type="text" name="email" id="email" maxlength="255" placeholder="${sStockNotificationEmailPlaceholder}" class="stock-notification__form-content__emailinput" value="${user.email}">

            <span class="stock-notification--error  error-empty hidden">${sStockNotificationEmailBlankText}</span>
            <span class="stock-notification--error error-emailvalid hidden">${sStockNotificationEmailInvalidText}</span>

            <button type="submit" class="mat-button mat-button--action-blue stock-notification__form--cta" data-aainteraction="out of stock submission" data-location="pdp">
                    ${sStockNotificationEmailNotifyMe}
            </button>

            <p class="stock-notification__form-content__instruction-policy stock-notification__form--assurance-text">
                    ${sStockNotificationEmailAssuranceText}
                <a class="stock-notification__form--assurance-text--privacy-policy" href="/data-protection/cms/datenschutz"> ${sStockNotificationEmailPrivacyPolicy}</a>
            </p>

        </form:form>
    </div>

    <div class="stock-notification__response stock-notification__success hidden">
        <span class="icon-wrapper">
            <i class="fa fa-check" aria-hidden="true"></i>
        </span>
        <span class="stock-notification__response__message">${sStockNotificationEmailSuccessText}</span>
    </div>

    <div class="stock-notification__response stock-notification__failure hidden">
        <span class="icon-wrapper">
            <i class="fa fa-check" aria-hidden="true"></i>
        </span>
        <span class="stock-notification__response__message">${sStockNotificationEmailExistingUserText}</span>
    </div>
</div>