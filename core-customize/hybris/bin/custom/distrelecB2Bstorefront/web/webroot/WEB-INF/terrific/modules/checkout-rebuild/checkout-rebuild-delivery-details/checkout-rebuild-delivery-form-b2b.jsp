<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="isCountrySupported" value="false"/>
<c:forEach items="${supportedRegisterCountries}" var="registerCountry">
    <c:if test="${registerCountry.isocode eq shippingItem.country.isocode}">
        <c:set var="isCountrySupported" value="true"/>
    </c:if>
</c:forEach>

<c:choose>
    <c:when test="${siteUid eq 'distrelec_EX' and isCountrySupported}">
        <spring:message code="register.postalCode.validationPattern_${shippingItem.country.isocode}" var="postalCodePattern" />
        <spring:message code="register.postalCode.validationMessage_${shippingItem.country.isocode}" var="postalCodeErrorMessage"/>
    </c:when>
    <c:otherwise>
        <spring:message code="register.postalCode.validationPattern" var="postalCodePattern" />
        <spring:message code="register.postalCode.validationMessage" var="postalCodeErrorMessage"/>
    </c:otherwise>
</c:choose>

<div class="o-bf js-inline-validation" data-iv-validate-previous-elements="true"
     data-iv-trigger-success-only-on-page-load="false"
     data-iv-trigger-on-page-load="false">
    <form class="js-iv-form js-delivery-form" data-customer-type="${template}">
        <input id="shippingAddress_${template}-delivery_${shippingItem.id}" name="shippingAddress" type="hidden" value="true">
        <input name="addressId" type="hidden" value="${shippingItem.id}">

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="deliveryCompanyNameInput_${shippingItem.id}"
                                       inputName="companyName"
                                       groupClass="js-iv-item"
                                       errorGroupClass="js-iv-errors"
                                       labelKey="checkout.deliveryPage.companyName"
                                       inputClass="js-iv-field"
                                       inputValue="${not empty shippingItem.companyName ? shippingItem.companyName : b2bAddressForm.companyName}"
                                       maxLength="35"
                                       inputAttributes="data-iv-triggers='change' data-iv-errors='empty'"/>
                    <div id="b2bDeliveryFieldErrorEmpty_companyName" class="js-iv-errors-empty" hidden>
                        <spring:message code="register.company.invalid"/>
                    </div>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="deliveryCompanyName2Input_${shippingItem.id}"
                                       inputName="companyName2"
                                       inputClass="js-optional-field"
                                       maxLength="35"
                                       labelKey="register.company2"
                                       optionalLabel="true"
                                       inputValue="${shippingItem.companyName2}"/>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="deliveryAddressLine1Input_${shippingItem.id}"
                                       inputName="line1"
                                       groupClass="js-iv-item"
                                       errorGroupClass="js-iv-errors"
                                       labelKey="addressform.line1"
                                       inputClass="js-iv-field"
                                       inputValue="${shippingItem.line1}"
                                       maxLength="60"
                                       inputAttributes="data-iv-triggers='change' data-iv-errors='empty'">
                    <div id="b2bDeliveryFieldErrorEmpty_line1" class="js-iv-errors-empty" hidden>
                        <spring:message code="register.strName.invalid"/>
                    </div>
                </formUtil:ux-formInput>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="deliveryAddressLine2Input_${shippingItem.id}"
                                       inputName="line2"
                                       labelKey="addressform.line2"
                                       inputClass="js-optional-field"
                                       optionalLabel="true"
                                       inputValue="${shippingItem.line2}"
                                       maxLength="10">
                </formUtil:ux-formInput>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="deliveryTownInput_${shippingItem.id}"
                                       inputName="town"
                                       groupClass="js-iv-item"
                                       errorGroupClass="js-iv-errors"
                                       labelKey="addressform.townCity"
                                       inputClass="js-iv-field"
                                       inputValue="${shippingItem.town}"
                                       maxLength="60"
                                       inputAttributes="data-iv-triggers='change' data-iv-errors='empty'">
                    <div id="b2bDeliveryFieldErrorEmpty_town" class="js-iv-errors-empty" hidden>
                        <spring:message code="register.town.invalid"/>
                    </div>
                </formUtil:ux-formInput>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <c:choose>
                    <c:when test="${fn:length(countries) gt 1}">
                        <formUtil:ux-formSelect selectId="deliveryCountrySelect_${shippingItem.id}"
                                                selectName="countryIso"
                                                items="${countries}"
                                                itemValue="isocode"
                                                selectedValue="${not empty shippingItem.country.isocode ? shippingItem.country.isocode : b2bAddressForm.countryIso}"
                                                groupClass="js-iv-item"
                                                errorGroupClass="js-iv-errors"
                                                labelKey="checkout.address.country"
                                                selectClass="js-iv-field js-selectboxit js-libphonenumber-isocode-select"
                                                skipBlank="false"
                                                skipBlankMessageKey="form.country.placeholder"
                                                selectAttributes="data-iv-triggers='change' data-iv-errors='empty'">
                            <div id="b2bDeliveryFieldErrorEmpty_countries" class="js-iv-errors-empty" hidden>
                                <spring:message code="register.country.invalid"/>
                            </div>
                        </formUtil:ux-formSelect>
                    </c:when>
                    <c:otherwise>
                        <div class="ux-form-group">
                            <c:choose>
                                <c:when test="${not empty shippingItem.country.isocode}">
                                    <c:set var="countryIso" value="${shippingItem.country.isocode}"/>
                                    <c:set var="countryName" value="${shippingItem.country.name}"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="countryIso" value="${countries[0].isocode}"/>
                                    <c:set var="countryName" value="${countries[0].name}"/>
                                </c:otherwise>
                            </c:choose>
                            <label id="b2bDeliveryCountryLabel" class="ux-form-group__label"><spring:message code="checkout.address.country"/></label>
                            <input name="countryIso" value="${countryIso}" type="hidden">
                            <div>
                                <strong id="b2bDeliveryCountryName" class="fw-b">${countryName}</strong>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="row">
            <div class="col-6">
                <formUtil:ux-formInput inputId="deliveryPostalCodeInput_${shippingItem.id}"
                                       inputName="postalCode"
                                       groupClass="js-iv-item"
                                       errorGroupClass="js-iv-errors"
                                       labelKey="addressform.postcode"
                                       inputClass="js-iv-field js-postal-code"
                                       inputValue="${shippingItem.postalCode}"
                                       maxLength="10"
                                       inputAttributes="data-iv-triggers='change' data-iv-errors='postalCode' data-iv-postal-code-pattern='${postalCodePattern}'">
                    <div id="b2bDeliveryFieldErrorPostalCode" class="js-iv-errors-postalCode" hidden>${postalCodeErrorMessage}</div>
                </formUtil:ux-formInput>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <formUtil:ux-formInput inputId="deliveryPhoneInput_${shippingItem.id}"
                                       inputName="phoneNumber"
                                       groupClass="js-iv-item"
                                       errorGroupClass="js-iv-errors"
                                       labelKey="checkout.address.phone"
                                       inputClass="js-iv-field js-libphonenumber"
                                       inputType="text"
                                       inputValue="${not empty shippingItem.cellphone ? shippingItem.cellphone : shippingItem.phone1}"
                                       groupInfoKey="checkout.rebuild.billing.phone.info"
                                       inputAttributes="data-iv-triggers='change' data-iv-errors='phonenumber'">
                    <div id="b2bDeliveryFieldErrorPhoneNumber" class="js-iv-errors-phonenumber" hidden>
                        <spring:message code="form.phone.error.for.country"/>
                    </div>
                </formUtil:ux-formInput>
            </div>
        </div>

        <c:if test="${not empty countries[0].regions}">
            <div class="row">
                <div class="col-12">
                    <formUtil:ux-formSelect selectId="deliveryRegionSelect_${shippingItem.id}"
                                            selectName="regionIso"
                                            items="${countries[0].regions}"
                                            itemValue="isocode"
                                            selectedValue="${shippingItem.region.isocode}"
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
                    <button id="b2bDeliverySubmitButton_${shippingItem.id}" class="ux-btn ux-btn--brand-green is-grey-when-disabled w-auto js-iv-form-submit js-delivery-form-submit"
                            type="button" disabled>
                        <spring:message code="checkout.rebuild.billing.form.submit.button" />
                    </button>

                    <button id="b2bDeliveryCancelButton_${shippingItem.id}" class="ux-btn ux-btn--white-light w-auto js-cr-editableform-cancel is-cancel-btn"
                            type="button">
                        <spring:message code="addressform.buttonCancel" />
                    </button>
                </div>
            </div>
        </div>
    </form>
</div>
