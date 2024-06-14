<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="isCountrySupported" value="false"/>
<c:forEach items="${supportedRegisterCountries}" var="registerCountry">
    <c:if test="${registerCountry.isocode eq b2bAddressForm.countryIso}">
        <c:set var="isCountrySupported" value="true"/>
    </c:if>
</c:forEach>

<c:choose>
    <c:when test="${siteUid eq 'distrelec_EX' and isCountrySupported}">
        <spring:message code="register.postalCode.validationPattern_${b2bAddressForm.countryIso}" var="postalCodePattern" />
        <spring:message code="register.postalCode.validationMessage_${b2bAddressForm.countryIso}" var="postalCodeErrorMessage"/>
    </c:when>
    <c:otherwise>
        <spring:message code="register.postalCode.validationPattern" var="postalCodePattern" />
        <spring:message code="register.postalCode.validationMessage" var="postalCodeErrorMessage"/>
    </c:otherwise>
</c:choose>

<div class="o-bf js-inline-validation" data-iv-validate-previous-elements="true"
     data-iv-trigger-success-only-on-page-load="${not showBillingInfoMode}"
     data-iv-trigger-on-page-load="${showBillingInfoMode or isDefaultBillingAddressInvalid}">

    <form:form modelAttribute="b2bAddressForm" class="js-iv-form js-billing-form" data-customer-type="${template}">
        <form:hidden path="addressId" id="addressId_${template}-billing" name="addressId" />
        <form:hidden path="billingAddress" id="billingAddress_${template}-billing" name="billingAddress" />
        <form:hidden path="shippingAddress" id="shippingAddress_${template}-billing" name="shippingAddress" />

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="billingCompanyNameInput"
                                       path="companyName"
                                       labelKey="checkout.deliveryPage.companyName"
                                       inputValue="${companyName}"
                                       maxLength="35"
                                       readonly="true"/>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="billingCompanyName2Input"
                                       inputClass="js-optional-field"
                                       path="companyName2"
                                       labelKey="register.company2"
                                       optionalLabel="true"
                                       maxLength="35"
                                       inputValue="${companyName2}">
                </formUtil:ux-formInput>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="billingAddressLine1Input"
                                       path="line1"
                                       groupClass="js-iv-item"
                                       errorGroupClass="js-iv-errors"
                                       labelKey="addressform.line1"
                                       inputClass="js-iv-field"
                                       inputValue="${line1}"
                                       maxLength="60"
                                       inputAttributes="data-iv-triggers='change' data-iv-errors='empty'">
                    <div id="b2bBillingFieldErrorEmpty_line1" class="js-iv-errors-empty" hidden>
                        <spring:message code="register.strName.invalid"/>
                    </div>
                </formUtil:ux-formInput>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="billingAddressLine2Input"
                                       path="line2"
                                       labelKey="addressform.line2"
                                       inputClass="js-optional-field"
                                       optionalLabel="true"
                                       inputValue="${line2}"
                                       maxLength="10">
                </formUtil:ux-formInput>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="billingTownInput"
                                       path="town"
                                       groupClass="js-iv-item"
                                       errorGroupClass="js-iv-errors"
                                       labelKey="addressform.townCity"
                                       inputClass="js-iv-field"
                                       inputValue="${town}"
                                       maxLength="40"
                                       inputAttributes="data-iv-triggers='change' data-iv-errors='empty'">
                    <div id="b2bBillingFieldErrorEmpty_town" class="js-iv-errors-empty" hidden>
                        <spring:message code="register.town.invalid"/>
                    </div>
                </formUtil:ux-formInput>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <c:choose>
                    <c:when test="${fn:length(countries) gt 1}">
                        <formUtil:ux-formSelect selectId="billingCountrySelect"
                                                path="countryIso"
                                                items="${countries}"
                                                itemValue="isocode"
                                                groupClass="js-iv-item"
                                                errorGroupClass="js-iv-errors"
                                                labelKey="checkout.address.country"
                                                selectClass="js-iv-field js-selectboxit js-libphonenumber-isocode-select js-postalCode-isocode-select"
                                                skipBlank="false"
                                                skipBlankMessageKey="form.country.placeholder"
                                                selectAttributes="data-iv-triggers='change' data-iv-errors='empty'">
                            <div id="b2bBillingFieldErrorEmpty_countries" class="js-iv-errors-empty" hidden>
                                <spring:message code="register.country.invalid"/>
                            </div>
                        </formUtil:ux-formSelect>
                    </c:when>
                    <c:otherwise>
                        <div class="ux-form-group">
                            <c:choose>
                                <c:when test="${not empty b2bAddressForm.countryIso}">
                                    <c:set var="countryIso" value="${b2bAddressForm.countryIso}"/>
                                    <c:forEach items="${billingAddressCountries}" var="billingCountry">
                                        <c:if test="${billingCountry.isocode eq b2bAddressForm.countryIso}">
                                            <c:set var="countryName" value="${billingCountry.name}"/>
                                        </c:if>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="countryIso" value="${countries[0].isocode}"/>
                                    <c:set var="countryName" value="${countries[0].name}"/>
                                </c:otherwise>
                            </c:choose>
                            <label id="b2bBillingCountryLabel" class="ux-form-group__label"><spring:message code="checkout.address.country"/></label>
                            <input name="countryIso" value="${countryIso}" type="hidden">
                            <div>
                                <strong id="b2bBillingCountryName" class="fw-b">${countryName}</strong>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="row">
            <div class="col-6">
                <formUtil:ux-formInput inputId="billingPostalCodeInput"
                                       path="postalCode"
                                       groupClass="js-iv-item"
                                       errorGroupClass="js-iv-errors"
                                       labelKey="addressform.postcode"
                                       inputClass="js-iv-field js-postal-code"
                                       inputValue="${postalCode}"
                                       maxLength="10"
                                       inputAttributes="data-iv-triggers='change' data-iv-errors='postalCode' data-iv-postal-code-pattern='${postalCodePattern}'">
                    <div id="b2bBillingFieldErrorPostalCode" class="js-iv-errors-postalCode" hidden>${postalCodeErrorMessage}</div>
                </formUtil:ux-formInput>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="billingPhoneInput"
                                       path="phoneNumber"
                                       groupClass="js-iv-item"
                                       errorGroupClass="js-iv-errors"
                                       labelKey="checkout.address.phone"
                                       inputClass="js-iv-field js-libphonenumber"
                                       inputType="text"
                                       inputValue="${phoneNumber}"
                                       maxLength="30"
                                       groupInfoKey="checkout.rebuild.billing.phone.info"
                                       inputAttributes="data-iv-triggers='change' data-iv-errors='phonenumber'">
                    <div id="b2bBillingFieldErrorPhoneNumber" class="js-iv-errors-phonenumber" hidden>
                        <spring:message code="form.phone.error.for.country"/>
                    </div>
                </formUtil:ux-formInput>
            </div>
        </div>

        <c:if test="${not empty countries[0].regions}">
            <div class="row">
                <div class="col-12">
                    <formUtil:ux-formSelect selectId="billingRegionSelect"
                                            path="regionIso"
                                            items="${countries[0].regions}"
                                            itemValue="isocode"
                                            selectedValue="${regionIso}"
                                            labelKey="addressform.region"
                                            selectClass="js-selectboxit js-optional-field"
                                            optionalLabel="true"
                                            skipBlank="false"
                                            skipBlankSelectable="true"
                                            skipBlankMessageKey="cart.return.items.return.reason.ps">
                    </formUtil:ux-formSelect>
                </div>
            </div>
        </c:if>

        <div class="row">
            <div class="col-12">
                <div class="o-cr-editable-form__ctas">
                    <button id="b2bBillingSubmitButton" class="ux-btn ux-btn--brand-green is-grey-when-disabled w-auto js-iv-form-submit js-billing-form-submit"
                            type="button" disabled>
                        <spring:message code="checkout.rebuild.billing.form.submit.button" />
                    </button>

                    <c:if test="${showBillingEdit}">
                        <button id="b2bBillingCancelButton" class="ux-btn ux-btn--white-light w-auto js-cr-editableform-cancel is-cancel-btn"
                                type="button">
                            <spring:message code="addressform.buttonCancel" />
                        </button>
                    </c:if>
                </div>
            </div>
        </div>
    </form:form>
</div>
