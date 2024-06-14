<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<spring:theme code="rma.guest.returnPage.formTitle" var="sFormTitle"/>
<spring:theme code="rma.guest.returnPage.formParagraph" var="sFormParagraph"/>
<spring:theme code="rma.guest.returnPage.formName" var="sFormName"/>
<spring:theme code="rma.guest.returnPage.formEmail" var="sFormEmail"/>
<spring:theme code="rma.guest.returnPage.formArticle" var="sFormArticle"/>
<spring:theme code="rma.guest.returnPage.orderNumber" var="sOrderNumber"/>
<spring:theme code="rma.guest.returnPage.formMoreInformation" var="sFormMoreInformation"/>
<spring:theme code="rma.guest.returnPage.formPhone" var="sFormPhone"/>
<spring:theme code="rma.guest.returnPage.formQuantity" var="sFormQty"/>
<spring:theme code="rma.guest.returnPage.formReason" var="sFormReason"/>
<spring:theme code="rma.guest.returnPage.submitButton" var="sSubmitBtn"/>
<spring:message code="validate.error.email" var="sEmailValidation" />
<spring:message code="validate.error.required" var="sRequiredValidation" />

<c:set var="returnReasonCodes" value="001,002,003,005,006,018,019" />

<section class="content-three">
    <h2>${sFormTitle}</h2>
    <p>${sFormParagraph}</p>
    <form:form modelAttribute="guestRMACreateRequestForm" class="rma-guest-return js-rmag-form" method="post" action="/returns/guest">
        <div class="input-holder js-input-holder">
            <label for="name">${sFormName} *</label>
            <input id="name" class="js-validate-empty" name="customerName" type="text" value=""/>
            <div class="input-holder__error-msg">${sRequiredValidation}</div>
        </div>
        <div class="input-holder js-input-holder">
            <label for="orderOrInvoiceNr">${sOrderNumber} *</label>
            <input id="orderOrInvoiceNr" class="js-validate-empty" name="orderNumber" type="text" value=""/>
            <div class="input-holder__error-msg">${sRequiredValidation}</div>
        </div>
        <div class="input-holder js-input-holder">
            <label for="email">${sFormEmail} *</label>
            <input id="email" class="validate-email js-validate-email js-validate-empty" name="emailAddress" type="text" value=""/>
            <div class="input-holder__error-msg">${sEmailValidation}</div>
        </div>
        <div class="input-holder js-input-holder">
            <label for="phone">${sFormPhone}</label>
            <input id="phone" name="phoneNumber" type="text" value=""/>
        </div>
        <div class="input-holder js-input-holder">
            <label for="article">${sFormArticle} *</label>
            <input id="article" class="js-validate-empty js-validate-article" name="articleNumber" type="text" value=""/>
            <div class="input-holder__error-msg">${sRequiredValidation}</div>
        </div>
        <div class="input-holder js-input-holder">
            <label for="quantity">${sFormQty} *</label>
            <input id="quantity" name="quantity" class="js-validate-empty js-rmag-qty" type="number" value="" min="1"/>
            <div class="input-holder__error-msg">${sRequiredValidation}</div>
        </div>

        <div class="input-holder js-input-holder js-rmag-mainreason">
            <label for="reasonsMain">${sFormReason} *</label>

            <select id="reasonsMain" class="selectpicker js-selectpicker js-rmag-main-reason js-validate-empty">
                <option value=""><spring:message code="cart.return.items.return.reason.ps"/></option>

                <c:forEach items="${returnReasons}" var="returnReasonMain">
                    <c:set var="englishTranslation">
                        <spring:eval expression="@messageSource.getMessage('${returnReasonMain.mainReasonText}' ,null, T(java.util.Locale).ENGLISH)" />
                    </c:set>

                    <c:set var="returnReasonSubCodes">
                        <c:forEach items="${returnReasonMain.subReasons}" var="returnReasonSub">${returnReasonSub.subReasonId}|</c:forEach>
                    </c:set>

                    <c:if test="${not empty returnReasonMain.defaultSubReasonId}">
                        <c:set var="returnReasonSubCodes" value="${returnReasonMain.defaultSubReasonId}" />
                    </c:if>

                    <option value="${returnReasonSubCodes}"
                            data-english-translation="${englishTranslation}"><spring:message code="${returnReasonMain.mainReasonText}"/></option>
                </c:forEach>
            </select>

            <div class="input-holder__error-msg">${sRequiredValidation}</div>
        </div>

        <div class="input-holder input-holder--subreason js-input-holder js-rmag-subreason hidden">
            <label for="reasonsSub">&nbsp;</label>

            <select id="reasonsSub" class="selectpicker js-selectpicker js-rmag-sub-reason js-validate-empty">
                <option value=""><spring:message code="cart.return.items.return.reason.ps"/></option>

                <c:forEach items="${returnReasons}" var="returnReasonMain">
                    <c:forEach items="${returnReasonMain.subReasons}" var="returnReasonSub">
                        <c:forEach items="${returnReasonSub.subReasonMessages}" var="returnReasonSubItems" varStatus="loop">
                            <c:set var="englishTranslation">
                                <spring:eval expression="@messageSource.getMessage('${returnReasonSubItems}' ,null, T(java.util.Locale).ENGLISH)" />
                            </c:set>

                            <option value="${returnReasonSub.subReasonId}_${loop.index + 1}"
                                    data-english-translation="${englishTranslation}"
                                    data-code="${returnReasonSub.subReasonId}">
                                <spring:message code="${returnReasonSubItems}"/>
                            </option>
                        </c:forEach>
                    </c:forEach>
                </c:forEach>
            </select>

            <div class="input-holder__error-msg">${sRequiredValidation}</div>

            <input class="js-reason-for-BE-id" name="returnReason" type="hidden">
            <input class="js-reason-for-BE-subreason" name="returnSubReason" type="hidden">
        </div>

        <div class="input-holder input-holder--has-counter js-input-holder js-char-counter">
            <c:set var="commentMaxLength" value="50" />
            <label for="moreinformation">${sFormMoreInformation}</label>
            <textarea id="moreinformation" class="js-char-counter-element" rows="3" cols="28" maxlength="50" name="customerText"></textarea>

            <div class="input-holder__counter js-char-counter-output">0&nbsp;/&nbsp;${commentMaxLength}</div>
        </div>
        <div class="recaptcha-holder recaptcha">
            <mod:captcha template="rma" skin="rma" callback="onCaptchaSubmitReturnsForm" />
        </div>
        <div class="btn-holder">
            <button id="returnSubmit" class="btn btn-primary btn-return-items js-rmag-submit-button" type="button">${sSubmitBtn}</button>
        </div>
    </form:form>
</section>