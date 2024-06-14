<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>

<spring:message code="text.setDefault" var="sSetDefault" />

<c:set var="billingAddressSelected" value="false"/>
<c:set var="billingAddressClass" value=""/>

<c:if test="${customerType eq 'GUEST' and siteUid eq 'distrelec_EX'}">
    <div class="js-postalCode-isocode-translations"
         data-postal-validation-pattern_BG="<spring:message code="register.postalCode.validationPattern_BG"/>"
         data-postal-validation-msg_BG="<spring:message code="register.postalCode.validationMessage_BG"/>"
         data-postal-validation-pattern_HR="<spring:message code="register.postalCode.validationPattern_HR"/>"
         data-postal-validation-msg_HR="<spring:message code="register.postalCode.validationMessage_HR"/>"
         data-postal-validation-pattern_CY="<spring:message code="register.postalCode.validationPattern_CY"/>"
         data-postal-validation-msg_CY="<spring:message code="register.postalCode.validationMessage_CY"/>"
         data-postal-validation-pattern_FR="<spring:message code="register.postalCode.validationPattern_FR"/>"
         data-postal-validation-msg_FR="<spring:message code="register.postalCode.validationMessage_FR"/>"
         data-postal-validation-pattern_GR="<spring:message code="register.postalCode.validationPattern_GR"/>"
         data-postal-validation-msg_GR="<spring:message code="register.postalCode.validationMessage_GR"/>"
         data-postal-validation-pattern_IE="<spring:message code="register.postalCode.validationPattern_IE"/>"
         data-postal-validation-msg_IE="<spring:message code="register.postalCode.validationMessage_IE"/>"
         data-postal-validation-pattern_LU="<spring:message code="register.postalCode.validationPattern_LU"/>"
         data-postal-validation-msg_LU="<spring:message code="register.postalCode.validationMessage_LU"/>"
         data-postal-validation-pattern_MT="<spring:message code="register.postalCode.validationPattern_MT"/>"
         data-postal-validation-msg_MT="<spring:message code="register.postalCode.validationMessage_MT"/>"
         data-postal-validation-pattern_PT="<spring:message code="register.postalCode.validationPattern_PT"/>"
         data-postal-validation-msg_PT="<spring:message code="register.postalCode.validationMessage_PT"/>"
         data-postal-validation-pattern_SI="<spring:message code="register.postalCode.validationPattern_SI"/>"
         data-postal-validation-msg_SI="<spring:message code="register.postalCode.validationMessage_SI"/>"
         data-postal-validation-pattern_ES="<spring:message code="register.postalCode.validationPattern_ES"/>"
         data-postal-validation-msg_ES="<spring:message code="register.postalCode.validationMessage_ES"/>"
         data-postal-validation-pattern_GB="<spring:message code="register.postalCode.validationPattern_GB"/>"
         data-postal-validation-msg_GB="<spring:message code="register.postalCode.validationMessage_GB"/>"
         data-postal-validation-pattern_XI="<spring:message code="register.postalCode.validationPattern_XI"/>"
         data-postal-validation-msg_XI="<spring:message code="register.postalCode.validationMessage_XI"/>"
         data-postal-validation-pattern_default="<spring:message code="register.postalCode.validationPattern"/>"
         data-postal-validation-msg_default="<spring:message code="register.postalCode.validationMessage"/>"
    ></div>
</c:if>

<c:choose>
    <c:when test="${multipleBillingAddresses}">
        <ul class="o-cr-radio-list is-address-list js-ba-list">
            <c:forEach items="${billingAddresses}" var="currentBillingAddress">
                <li class="o-cr-radio-list__item js-ba-list-item ${currentBillingAddress.defaultBilling ? 'is-default-address' : ''}">
                    <div class="o-cr-radio-item">
                        <input id="billingDetail_${currentBillingAddress.id}"
                               value="${currentBillingAddress.id}"
                               data-address-type="billing"
                               class="o-cr-radio-item__radio js-multiple-address-radio js-ba-input" type="radio"
                               ${currentBillingAddress.id eq billingAddress.id ? 'checked' : ''}
                               name="billingDetail">
                        <label class="o-cr-radio-item__radio-label"
                               for="billingDetail_${currentBillingAddress.id}"></label>

                        <c:if test="${currentBillingAddress.id eq billingAddress.id}">
                            <c:set var="billingAddressSelected" value="true"/>
                        </c:if>

                        <div class="o-cr-radio-item__content">
                            <div class="o-cr-radio-item__content__main">
                                <div class="o-cr-radio-item__info">
                                    <div class="o-cr-radio-item__radio-icon">
                                        <i class="material-icons-round radio_button_unchecked is-radio-unchecked">radio_button_unchecked</i>
                                        <i class="material-icons-round radio_button_unchecked is-radio-unchecked-hover">radio_button_unchecked</i>
                                        <i class="material-icons-round radio_button_checked is-radio-checked">radio_button_checked</i>
                                    </div>

                                    <div class="o-cr-radio-item__status-icon">
                                        <i class="material-icons-round location_on">location_on</i>
                                    </div>

                                    <div id="checkoutBillingAddressInfo${currentBillingAddress.id}" class="o-cr-radio-item__info-text fw-m">
                                        <c:choose>
                                            <c:when test="${customerType eq 'B2B' || customerType eq 'B2B_KEY_ACCOUNT'}">
                                                <c:out value="${currentBillingAddress.companyName}"/>, <c:if
                                                    test="${not empty currentBillingAddress.companyName2}"><c:out
                                                    value="${currentBillingAddress.companyName2}"/>, </c:if><c:out
                                                    value="${currentBillingAddress.line1}"/><c:if test="${not empty currentBillingAddress.line2}"> <c:out
                                                    value="${currentBillingAddress.line2}"/></c:if>,<br>
                                                <c:out value="${currentBillingAddress.postalCode}"/> <c:out
                                                    value="${currentBillingAddress.town}"/>, <c:if
                                                    test="${not empty currentBillingAddress.region}"><c:out
                                                    value="${currentBillingAddress.region.name}"/> </c:if><c:out
                                                    value="${currentBillingAddress.country.name}"/> <c:choose><c:when
                                                    test="${not empty currentBillingAddress.cellphone}"><c:out
                                                    value="${currentBillingAddress.cellphone}"/></c:when><c:otherwise><c:out
                                                    value="${currentBillingAddress.phone1}"/></c:otherwise></c:choose>
                                            </c:when>
                                            <c:when test="${customerType eq 'B2C'}">
                                                <c:out value="${currentBillingAddress.title}"/> <c:out
                                                    value="${currentBillingAddress.firstName}"/> <c:out
                                                    value="${currentBillingAddress.lastName}"/>, <c:out
                                                    value="${currentBillingAddress.line1}"/><c:if test="${not empty currentBillingAddress.line2}"> <c:out
                                                    value="${currentBillingAddress.line2}"/></c:if>,<br>
                                                <c:out value="${currentBillingAddress.postalCode}"/> <c:out
                                                    value="${currentBillingAddress.town}"/>, <c:if
                                                    test="${not empty currentBillingAddress.region}"><c:out
                                                    value="${currentBillingAddress.region.name}"/> </c:if><c:out
                                                    value="${currentBillingAddress.country.name}"/> <c:choose><c:when
                                                    test="${not empty currentBillingAddress.cellphone}"><c:out
                                                    value="${currentBillingAddress.cellphone}"/></c:when><c:otherwise><c:out
                                                    value="${currentBillingAddress.phone1}"/></c:otherwise></c:choose>
                                            </c:when>
                                            <c:when test="${customerType eq 'B2E'}">
                                                <c:out value="${currentBillingAddress.title}"/> <c:out
                                                    value="${currentBillingAddress.firstName}"/> <c:out
                                                    value="${currentBillingAddress.lastName}"/>, <c:out
                                                    value="${currentBillingAddress.line1}"/><c:if test="${not empty currentBillingAddress.line2}"> <c:out
                                                    value="${currentBillingAddress.line2}"/></c:if>,<br>
                                                <c:out value="${currentBillingAddress.postalCode}"/> <c:out
                                                    value="${currentBillingAddress.town}"/>, <c:if
                                                    test="${not empty currentBillingAddress.region}"><c:out
                                                    value="${currentBillingAddress.region.name}"/> </c:if><c:out
                                                    value="${currentBillingAddress.country.name}"/> <c:if
                                                    test="${not empty currentBillingAddress.phone1}"><c:out
                                                    value="${currentBillingAddress.phone1}"/></c:if>
                                            </c:when>
                                            <c:when test="${customerType eq 'GUEST'}">
                                                <c:out value="${currentBillingAddress.title}"/> <c:out
                                                    value="${currentBillingAddress.firstName}"/> <c:out
                                                    value="${currentBillingAddress.lastName}"/>, <c:out
                                                    value="${currentBillingAddress.line1}"/><c:if test="${not empty currentBillingAddress.line2}"> <c:out
                                                    value="${currentBillingAddress.line2}"/></c:if>,<br>
                                                <c:out value="${currentBillingAddress.postalCode}"/> <c:out
                                                    value="${currentBillingAddress.town}"/>, <c:if
                                                    test="${not empty currentBillingAddress.region}"><c:out
                                                    value="${currentBillingAddress.region.name}"/> </c:if><c:out
                                                    value="${currentBillingAddress.country.name}"/> <c:choose><c:when
                                                    test="${not empty currentBillingAddress.cellphone}"><c:out
                                                    value="${currentBillingAddress.cellphone}"/></c:when><c:otherwise><c:out
                                                    value="${currentBillingAddress.phone1}"/></c:otherwise></c:choose>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                </div>

                                <div class="o-cr-radio-item__cta">
                                    <button id="checkoutBillingAddressSetDefault${currentBillingAddress.id}" class="ux-link ux-link--clean is-set-default js-ba-set-default">${sSetDefault}</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </c:when>
    <c:otherwise>
        <c:if test="${showBillingInfoMode}">
            <c:set var="billingAddressSelected" value="true"/>
            <c:set var="billingAddressClass" value="is-info-mode is-cancellable"/>

            <c:if test="${param.edit eq 'billing'}">
                <c:set var="billingAddressClass" value="is-cancellable"/>
            </c:if>
        </c:if>

        <div class="o-cr-editable-form ${billingAddressClass} js-cr-editableform">
            <div class="o-cr-editable-form__form js-cr-editableform-form">
                <mod:checkout-rebuild-billing-form template="${customerType}" skin="${customerType}"/>
            </div>

            <div class="o-cr-editable-form__info js-cr-editableform-info">
                <mod:checkout-rebuild-billing-form-info template="${customerType}" skin="${customerType}" />

                <c:if test="${isBillingAddressShippable}">
                    <div class="o-cr-editable-form__billingAndDelivery">
                        <spring:message code="checkout.rebuild.billing.details" var="sBillingTitle" />
                        <spring:message code="checkout.rebuild.billing.and.delivery" var="sBillingAndDeliveryTitle" />

                        <div class="js-billing-form-dynamic-title" data-billing-title="${sBillingTitle}"
                             data-billing-delivery-title="${sBillingAndDeliveryTitle}">
                            <formUtil:ux-formCheckboxPath idKey="sameBillingAndDeliveryDetails"
                                                          isChecked="${isBillingAndShippingAddress}"
                                                          formGroupClass="ux-form-group mb-0"
                                                          inputCSS="js-same-billing-and-delivery"
                                                          labelKey="checkout.rebuild.delivery.billingAndShipping"/>
                        </div>
                    </div>
                </c:if>

                <c:if test="${showBillingEdit}">
                    <div class="o-cr-editable-form__edit">
                        <button id="billingAddressEditButton" class="ux-link js-cr-editableform-edit">
                            <spring:message code="text.edit"/><i class="material-icons-round mode_edit">&#xe254;</i>
                        </button>
                    </div>
                </c:if>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<input class="js-billing-address-selected" type="hidden" value="${billingAddressSelected}">
