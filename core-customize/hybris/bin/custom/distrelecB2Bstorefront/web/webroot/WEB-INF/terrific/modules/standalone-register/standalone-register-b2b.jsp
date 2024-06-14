<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="checkoutregister.b2boption" var="sB2Boption" />
<spring:message code="checkoutregister.b2coption" var="sB2Coption" />
<spring:message code="checkoutregister.customerNumber" var="sCustomerNumber" />
<spring:message code="validation.checkPwd.equals" var="sPasswordNoMatch" />
<spring:message code="checkoutregister.titleHelp" var="sTitleHelp" />
<spring:message code="checkoutregister.addNumbers" var="sMoreNumber" />
<spring:message code="checkoutregister.fax" var="sFax" />
<spring:message code="checkoutregister.continue" var="sContinue" />
<spring:message code="logindata.Newsletter.privacyInfo" var="sPrivacy" />
<spring:message code="register.newsletter" var="sMarketingConset" />
<spring:message code="checkoutregister.terms" var="sTerms" arguments="/gtc/cms/agb" />
<spring:message code="checkoutregister.helpText" var="sHelpText" />
<spring:message code="checkout.address.company" var="sCompany" />
<spring:message code="checkout.address.company2" var="sCompany2" />
<spring:message code="checkoutregister.currency" var="sCurrency" />
<spring:message code="checkoutregister.B2B.title" var="sTitle" />
<spring:message code="checkoutregister.vat" var="sCompanyVat" />
<spring:message code="companyinformation.vatId.optional" var="sCompanyVatOptional" />
<spring:message code="checkoutregister.org.prefill.hint" var="sOrgHint" />
<spring:theme code="register.vatId.validationPattern" text="" var="sVatIdLength" />
<spring:message code="register.vatId.length.infoText" var="sVatIdInfoText" />
<spring:message code="form.select.empty" var="sSelectEmpty" />
<spring:theme code="register.organizationalNumber" text="" var="sOrganizationalNumber" />
<spring:theme code="register.organizationalNumber.length" text=""  var="sOrganizationalNumberLength" />
<spring:theme code="register.organizationalNumber.infoText" text="" arguments="${sOrganizationalNumberLength}" var="sOrganizationalNumberInfoText" />
<spring:message code="register.organizationalNumber.company" var="sFindCompany" />
<spring:message code="register.organizationalNumber.change.details" var="sChangeDetails" />
<spring:message code="register.organizationalNumber.no.results" var="sNoResults" />
<spring:message code="register.organizationalNumber.additional" var="sAdditionalChanges" />
<spring:message code="register.organizationalNumber.codice.question" var="sCodiceQ" text="Sei in possesso di un Codice Destinatario?" />
<spring:message code="vat.reg.codice.dest.title" var="sCodice" text="Codice Destinatario" />
<spring:message code="vat.reg.codice.cert.title" var="sPostCert" text="Posta Elettroinca Certificata" />
<spring:message code="vat.reg.label" var="sCodicePopup" />
<spring:message code="checkoutregister.radio.yes" var="sRadioYes" />
<spring:message code="checkoutregister.radio.no" var="sRadioNo" />
<spring:message code="standalone.reg.step2.notfound.company" text="Do you have a Distrelec customer ID number?" var="sCustNum"/>
<spring:message code="register.organizationalNumber.confirm" text="Confirm number" var="sConfirm"/>
<spring:message code="online.survey.input.empty" var="sError"/>
<spring:message code="register.organizationalNumber.customerID" var="sCustomerIdError"/>
<spring:message code="register.organizationalNumber.new.customerID" var="sCustomerNewIdError"/>
<spring:message code="register.organizationalNumber.customerNotFound" var="sCustomerNotFound"/>
<spring:message code="register.organizationalNumber.company.results" var="sCompanyResults"/>
<spring:message code="register.organizationalNumber.company.in" var="sCompanyIn"/>
<spring:message code="register.organizationalNumber.register.title" var="sRegisteredTitle"/>
<spring:message code="register.organizationalNumber.suggested.address" var="sSuggestedAddress"/>
<spring:message code="register.organizationalNumber.already.register" var="sAlreadyRegistered"/>
<spring:message code="register.organizationalNumber.connect.account" var="sConnectAccount"/>
<spring:message code="register.organizationalNumber.results.for" var="sShowResultsFor"/>
<spring:message code="register.organizationalNumber.results.in" var="sShowResultsIn"/>
<spring:message code="register.organizationalNumber.helpText" text="You can find this on a letter or something here say where." var="sHelpText"/>
<spring:message code="register.organizationalNumber.step3.title" text="Your details" var="sStep3"/>
<spring:message code="register.organizationalNumber.suggested" var="sSuggestedAddress"/>
<spring:message code="register.organizationalNumber.confirm.address" var="sConfirmAddress"/>
<spring:message code="register.organizationalNumber.customer.number" var="sCustomerNumber"/>
<spring:message code="register.organizationalNumber.find.account" var="sFindAccount"/>
<spring:message code="register.organizationlNumber.existing.account" var="sExisting"/>
<spring:message code="standalone.reg.step2.title" var="sStep2" />
<spring:message code="shoppinglist.bulkAction.default" var="sPleaseSelectOption" />
<spring:message code="vat.registration.codiceDest" text="e.g. 0123456"  var="sCodiceDest"/>
<spring:message code="register.organizationalNumber.org" text="Organisational Number" var="sOrg"/>
<spring:message code="checkoutregister.org.error" var="sOrgError" />
<spring:message code="validate.error.required" var="sRequiredError"/>
<spring:message code="checkoutregister.vat.prefill.hint" var="sCompanyHintText" />
<spring:message code="addressform.title.mr" var="regMr" text="Mr" />
<spring:message code="addressform.title.ms" var="regMs" text="Ms" />
<spring:message code="address.title.invalid" var="sTitleInvalid" text="Please select a title" />

<c:url value="registration/b2b/async" var="sSubmitB2bAction" />
<c:set value="${registerB2BForm.registrationType eq 'CHECKOUT' ? '/login/checkout' : '/login'}" var="loginUrl" />

<c:if test="${isExportShop}">
    <spring:message code="checkoutregister.vat.prefill.hint-${currentCountry.isocode}" var="sCompanyHintText" />
</c:if>

<form:form method="post" class="form-b2b js-formB2b js-form" modelAttribute="registerB2BForm" action="${sSubmitB2bAction}" autocomplete="off">
    <mod:loading-state skin="loading-state" template="high-priority"/>
    <section class="form-b2b__companydetails card-wrapper col-12 <c:if test="${isExportShop}">isBiz</c:if>">
        <div class="row">
            <div class="form-b2b__companydetails__title">
                <h3>2. ${sStep2} </h3>
            </div>
            <div class="form-b2b__companydetails__input">
                <div class="form-b2b__companydetails__input__bisnode col-12 js-b2b-company-details-bisnode">
                    <div class="row">

                        <div class="col-12 col-lg-10 form-b2b__country-wrapper no-pad">
                            <c:if test="${currentSalesOrg.code eq '7801'}">
                                <c:choose>
                                    <c:when test="${isExportShop}">
                                        <formUtil:formLabel idKey="register.countryCode" labelKey="register.country" path="countryCode"  labelCSS="col-12 col-lg-8"/>
                                        <div class="inputPopup">
                                            <div class="inputPopup__center">
                                                <div class="inputPopup__popup" id="countryList">
                                                    <spring:message code="checkoutregister.countryNotInList"/>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="p-relative js-p-relative">
                                            <formUtil:formSelectBox idKey="register.countryCode" path="countryCode" mandatory="true" skipBlank="false"
                                                                    selectedValue="${currentCountry.isocode}" selectCSSClass="countryPicker selectpicker ux-selectpicker js-validate js-validate-select js-country-select ${currentSalesOrg.code eq '7801' ? 'countrySelectExportShop' : ''}"
                                                                    skipBlankMessageKey="form.select.empty" items="${countriesB2B}" itemValue="isocode" />
                                            <i class="ux-selectpicker__angle-down fa fa-angle-down"></i>
                                            <i class="tickItem fa fa-check hidden"></i>
                                            <i class="tickItemError fa fa-times hidden"></i>
                                        </div>
                                        <div class="field-msgs" id="registerForm-error-countryCode-b2b">
                                            <div class="error hidden">
                                                <span id="registerForm-error-countryCode-b2b-txt"><spring:message code="validate.error.required" /></span>
                                            </div>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <input id="countryCode" type="hidden" value="${currentCountry.isocode}" name="countryCode" />
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </div>
                        <label for="bisnode" class="col-12">
                            <spring:message code="accountlist.addresses.companyName"/><br>
                        </label>

                        <input class="col-12 col-lg-10 js-validate" <c:if test="${isExportShop}">disabled="disabled"</c:if>
                               type="search" name="bisnode" id="bisnode" data-bisnode-timeout="${jalosession.tenant.config.getParameter('bisnode.connect.timeout')}"
                               placeholder="<spring:message code="standalone.reg.step2.company.placeholder" />" autocomplete="off" />

                        <div id="bisnoderesults" class="bisnode__results js-bisnode-results d-none col-12 col-lg-10 <c:if test="${isExportShop}">isBiz</c:if>">
                            <div class="row">
                                <p id="showresults" v-if="items.company !== undefined">
                                        ${sShowResultsFor}<span class="catchtext" v-html="items.companyname"></span>${sShowResultsIn} <span id="countryOption">{{countryCode}}</span>
                                </p>
                                <div class="bisnode__results__item__noresult" v-if="items.company === undefined">
                                    <p>${sNoResults}</p>
                                </div>
                                <div class="bisnode__results__wrapper">
                                    <div class="bisnode__results__wrapper__item" v-for="(item, index) in items.company" :ref="'item-' + index" v-on:click="itemClick(index)">
                                        <div class="address_section">
                                            <p v-if="item.primaryaddress.primaryname.length" class="primaryActive" v-html="item.primaryaddress.primaryname"></p>
                                            <p v-if="item.primaryaddress.line1.length" v-html="item.primaryaddress.line1 + ', ' + item.primaryaddress.locality"></p>
                                        </div>
                                        <i class="tickItem fa fa-check duplicate hidden"></i>
                                        <p class="dunsNum hidden">{{item.duns}}</p>
                                        <a class="changeDetails hidden" href="#" v-on:click.stop="resetItems(index)" :ref="'change'+index">${sChangeDetails}</a>
                                    </div>
                                </div>
                                <div class="bisnode__results__duplicate" v-show="duns.exist === true ">
                                    <div class="infobox">
                                        <div class="infobox__text">
                                            <i class="tickItem fa fa-check"></i>
                                            <p>${sAlreadyRegistered}<br />${sConnectAccount}</p>
                                        </div>
                                    </div>
                                    <div class="customernumber">
                                        <label for="customerNum">${sCustomerNumber}</label>
                                        <input v-on:keydown="stopSubmit($event)" class="customerNumberField" id="customerNum" ref="customernum"/>
                                        <i class="tickItem fa fa-check hidden"></i>
                                        <i class="tickItemError fa fa-times hidden"></i>
                                        <small id="customerNumber_B2B_helptext"><spring:message code="standalone.reg.step2.custNo.help"/></small>
                                        <div class="error error-message hidden">
                                            <i class="fas fa-exclamation-triangle"></i>
                                            <p>${sCustomerNewIdError}</p>
                                        </div>
                                        <a class="mat-button mat-button--action-green findAccountButton" v-on:click="findAccount($event)" data-aainteraction="continue from company" href="#" title="Find account">${sFindAccount}</a>
                                    </div>
                                </div>

                                <a id="findCompany" v-on:click="findCompany" href="#">${sFindCompany}</a>
                            </div>
                        </div>

                        <div class="col-12 bisnode__results__existing hidden" id="existingview">
                            <div class="row bisnode__companynotfound hidden" id="companynotfound">
                                <div class="col-12 col-lg-10" id="companynotfound-inner">
                                    <div class="validCountCompany">
                                        <div class="form-group">
                                            <label for="bisnode">
                                                <spring:message code="accountlist.addresses.companyName"/><br>
                                            </label>
                                            <formUtil:formInputBox idKey="register.companyB2B" path="company" inputCSS="companyInp" mandatory="true" />
                                            <div class="error-length hidden">
                                                <p><spring:message code="register.company.invalid.length" /></p>
                                            </div>
                                            <div class="error error-message hidden">
                                                <i class="fa fa-times tick"></i>
                                                <p>${sError}</p>
                                            </div>
                                        </div>
                                        <div class="bisnode__results__wrapper hidden existingWrapper">
                                            <div class="bisnode__results__wrapper__item active">
                                                <p class="searchTermCompany"></p>
                                                <a class="changeDetails js-changeDetails" href="#" v-on:click="reset($event)">${sChangeDetails}</a>
                                                <i class="tickItem fa fa-check"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <h4>${sExisting}</h4>
                            <div class="form-b2b__form__field__custno row">
                                <div class="col-6 col-lg-3">
                                    <div class="form-check form-check-inline existingBtn" data-value="No">
                                        <input type="radio" class="radio form-check-input" name="existingCust" id="radioSelectNoExisting" value="false" checked>
                                        <label for="radioSelectNoExisting">
                                                ${sRadioNo}
                                        </label>
                                    </div>
                                </div>
                                <div class="col-6 col-lg-3">
                                    <div class="form-check form-check-inline existingBtn" data-value="Yes">
                                        <input type="radio" class="radio form-check-input" name="existingCust" id="radioSelectYesExisting" value="false">
                                        <label for="radioSelectYesExisting">
                                                ${sRadioYes}
                                        </label>
                                    </div>
                                </div>
                                <div class="col-12 col-lg-8 customerNumberContainer hidden">
                                    <div class="error customerNumberError hidden">
                                        <i class="fa fa-exclamation-circle"></i>
                                        <p>${sError}</p>
                                    </div>
                                    <label for="customerNumber">${sCustomerNumber}</label>

                                    <input class="customerNumberFieldv2" id="customerNumber"/>
                                    <i class="tickItem fa fa-check hidden"></i>
                                    <i class="tickItem fa fa-times hidden"></i>
                                    <div class="inputPopup inputPopup--grouped">
                                        <div class="inputPopup__center">
                                            <div id="CustNumberHint" class="inputorgNumberPopup__popup"><spring:message code="standalone.reg.step2.custNo.help" /></div>
                                        </div>
                                    </div>
                                    <div class="error error-message hidden">
                                        <i class="fas fa-exclamation-triangle"></i>
                                        <p>${sCustomerNewIdError}</p>
                                    </div>
                                    <a v-on:click="findAccount($event)" class="mat-button mat-button--action-green findAccountCustomer" data-aainteraction="continue from company" href="#" title="Find account">${sFindAccount}</a>
                                </div>
                            </div>
                        </div>

                        <div class="col-12 bisnode__results__additional hidden">
                            <div class="row">
                                <div class="col-12 col-lg-10">
                                    <div class="row">
                                        <div id="additional-changes-text">
                                            <h4>${sAdditionalChanges}</h4>
                                        </div>
                                        <c:set var="departmentMandatory" value="${currentCountry.isocode eq 'DK' || currentCountry.isocode eq 'EE' || currentCountry.isocode eq 'LV' || currentCountry.isocode eq 'NL' || currentCountry.isocode eq 'NO' || currentCountry.isocode eq 'SE' }"/>
                                        <c:if test="${departmentMandatory eq true}">
                                            <spring:message code="orgNumber.validation.pattern" var="sOrgNumberValidationPattern" />
                                            <div id="hiddenGroup" class="grouped department <c:if test="${departmentMandatory eq true}">isMandatory</c:if>">
                                                <label for="orgNumber">${sOrg}*</label>

                                                <div class="inputGroup">
                                                    <input class="<c:if test="${departmentMandatory eq true}">validate</c:if> orgNumber" id="orgNumber" name="organizationalNumber" data-validation-pattern="${sOrgNumberValidationPattern}"/>
                                                    <i class="fa fa-check tickItem ticks2 hidden"></i>
                                                    <i class="fa fa-times tickItem ticks2 hidden"></i>
                                                </div>
                                                <div class="inputPopup inputPopup--grouped">
                                                    <div class="inputPopup__center">
                                                        <div id="orgNumberHint" class="inputorgNumberPopup__popup">${sOrgHint}</div>
                                                    </div>
                                                </div>
                                                <div class="error error-message hidden">
                                                    <span id="orgNumberError">${sOrgError}</span>
                                                </div>
                                            </div>
                                        </c:if>

                                        <c:choose>
                                            <c:when test=" ${isExportShop and siteUid eq 'distrelec_FR'} ">
                                                <div class="grouped currency">
                                                    <formUtil:formLabel idKey="register.currencyCodeB2B" labelKey="checkoutregister.currency" path="currencyCode"  />
                                                    <div class="p-relative js-p-relative">
                                                        <formUtil:formSelectBox idKey="register.currencyCodeB2B" path="currencyCode" mandatory="true" skipBlank="false" selectCSSClass="selectpicker ux-selectpicker js-validate-empty" skipBlankMessageKey="form.select.empty" items="${currencies}" itemValue="isocode" />
                                                        <i class="ux-selectpicker__angle-down fa fa-angle-down"></i>
                                                        <i class="fa fa-check tickItem ticks2 hidden"></i>
                                                    </div>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="grouped currency">
                                                    <input id="currencyCode" type="hidden" value="${currentCurrency.isocode}" name="currencyCode" />
                                                </div>
                                            </c:otherwise>
                                        </c:choose>

                                        <c:if test="${currentCountry.isocode eq 'IT'}">
                                            <div class="vat-container">
                                                <div class="grouped validCounts2">
                                                    <h4 id="b2b-it-codice-label">${sCodiceQ}</h4>
                                                    <div class="wrapper row">
                                                        <div class="col-6 col-lg-3">
                                                            <div class="form-check form-check-inline" data-value="Yes">
                                                                <input type="radio" class="radio form-check-input" name="italianVat" id="italianVatYes" value="false" checked>
                                                                <label for="italianVatYes">
                                                                        ${sRadioYes}
                                                                </label>
                                                            </div>
                                                        </div>
                                                        <div class="col-6 col-lg-3">
                                                            <div class="form-existingCustomerb2bcheck form-check form-check-inline" data-value="No">
                                                                <input type="radio" class="radio form-check-input" name="italianVat" id="italianVatNo" value="false">
                                                                <label for="italianVatNo">
                                                                        ${sRadioNo}
                                                                </label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="grouped codiceDest active">
                                                    <label>${sCodice}</label>

                                                    <div class="inputGroup">
                                                        <input class="codiceEmail js-codiceEmail js-validate js-validate-minimum-characters" name="vat4" id="vat4" placeholder="${sCodiceDest}" />
                                                        <i class="tickItem fa fa-check hidden"></i>
                                                        <i class="tickItemError fa fa-times hidden"></i>
                                                        <div class="field-msgs" id="registerForm-error-codiceEmail-b2b">
                                                            <div class="error hidden">
                                                                <span id="registerForm-error-codiceEmail-b2b-txt"><spring:message code="vat.validation.error"/></span>
                                                            </div>
                                                        </div>
                                                        <div class="inputPopup inputPopup--grouped">
                                                            <div class="inputPopup__center">
                                                                <div id="b2b-it-codice-info" class="inputorgNumberPopup__popup">${sCodicePopup}</div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="grouped postCert hidden">
                                                    <label>${sPostCert}</label>
                                                    <div class="inputGroup">
                                                        <input type="email" placeholder="esempio@legalmail.it" class="legalEmail js-legalEmail js-validate js-validate-email" id="legalEmail" name="legalEmail"/>
                                                        <i class="tickItem fa fa-check hidden"></i>
                                                        <i class="tickItemError fa fa-times hidden"></i>
                                                        <div class="field-msgs" id="registerForm-error-postCert-b2b">
                                                            <div class="error hidden">
                                                                <span id="registerForm-error-postCert-b2b-txt"><spring:message code="validate.error.email"/></span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>

                                        <c:set var="isMandatory" value="${currentCountry.isocode eq 'BE' || currentCountry.isocode eq 'CZ' || currentCountry.isocode eq 'FI' || currentCountry.isocode eq 'HU' || currentCountry.isocode eq 'IT'
	                                        || currentCountry.isocode eq 'LV' || currentCountry.isocode eq 'LT' || currentCountry.isocode eq 'NL' || currentCountry.isocode eq 'PL' || currentCountry.isocode eq 'RO' || currentCountry.isocode eq 'SK'
	                                        || isExportShop || currentCountry.isocode eq 'FR'}"/>
                                        <c:set var="isNotHide" value="${currentCountry.isocode eq 'SE' || currentCountry.isocode eq 'NO'}"/>

                                        <c:if test="${isNotHide eq false}">
                                            <c:set var="isItaly" value="${siteUid eq 'distrelec_IT'}" />
                                            <c:set var="isSwitzerland" value="${siteUid eq 'distrelec_CH'}" />
                                            <c:set var="isExport" value="${siteUid eq 'distrelec_EX'}" />

                                            <c:choose>
                                                <c:when test="${not (isItaly or isExport or isSwitzerland)}">
                                                    <spring:message code="vatId.prefix.${siteUid}" var="vatIdPrefix" />
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${currentCountry.isocode != 'EX'}">
                                                            <spring:message code="vatId.prefix.${siteUid}.${currentCountry.isocode}" var="vatIdPrefix" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="vatIdPrefix" value="EX" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>

                                            <div class="grouped compVat <c:if test="${isMandatory eq true}">isMandatory</c:if>">
                                                <c:choose>
                                                    <c:when test="${isMandatory eq true}">
                                                        <label for="vatId">${sCompanyVat}</label>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <label for="vatId">${sCompanyVatOptional}</label>
                                                    </c:otherwise>
                                                </c:choose>
                                                <input id="vatId" type="hidden" name="vatId" value="" />
                                                <div class="grouped__prefilled p-relative js-p-relative">
                                                    <c:choose>
                                                        <c:when test="${empty vatIdPrefix}">
                                                            <div class="prefilled hidden ${vatIdPrefix}" data-ignore="true">
                                                                <p id="vatPreValue"></p>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="prefilled">
                                                                <p id="vatPreValue">${vatIdPrefix}</p>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:choose>
                                                        <c:when test="${isSwitzerland}">
                                                            <spring:message code="vat.placeholder.text.${currentCountry.isocode}" var="sVatPlaceholder" />
                                                            <spring:message code="vat.placeholder.text.CH" var="sVatPlaceholderCH" />
                                                            <spring:message code="vat.placeholder.text.LI" var="sVatPlaceholderLI" />
                                                            <c:choose>
                                                                <c:when test="${isMandatory}">
                                                                    <input id="groupVatId js-validate" class="vatID" name="groupVatId" placeholder="${sVatPlaceholder}"
                                                                           data-CH-placeholder="${sVatPlaceholderCH}"
                                                                           data-LI-placeholder="${sVatPlaceholderLI}"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <spring:message code="vat.placeholder.optional" var="sVatPlaceholderOptional" />
                                                                    <input id="groupVatId" class="vatID js-optional js-validate-minimum-vat-characters" name="groupVatId" placeholder="${sVatPlaceholder}"
                                                                           data-CH-placeholder="${sVatPlaceholderOptional}<c:if test="${not empty sVatPlaceholderCH}"> - </c:if>${sVatPlaceholderCH}"
                                                                           data-LI-placeholder="${sVatPlaceholderOptional}<c:if test="${not empty sVatPlaceholderLI}"> - </c:if>${sVatPlaceholderLI}"/>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:when test="${isItaly}">
                                                            <spring:message code="vat.placeholder.text.${currentCountry.isocode}" var="sVatPlaceholder" />
                                                            <spring:message code="vat.placeholder.text.IT" var="sVatPlaceholderIT" />
                                                            <spring:message code="vat.placeholder.text.SM" var="sVatPlaceholderSM" />
                                                            <spring:message code="vat.placeholder.text.VA" var="sVatPlaceholderVA" />
                                                            <c:choose>
                                                                <c:when test="${isMandatory}">
                                                                    <input id="groupVatId" class="vatID js-validate" name="groupVatId" placeholder="${sVatPlaceholder}"
                                                                           data-IT-placeholder="${sVatPlaceholderIT}"
                                                                           data-SM-placeholder="${sVatPlaceholderSM}"
                                                                           data-VA-placeholder="${sVatPlaceholderVA}"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <spring:message code="vat.placeholder.optional" var="sVatPlaceholderOptional" />
                                                                    <input id="groupVatId" class="vatID js-optional" name="groupVatId" placeholder="${sVatPlaceholder}"
                                                                           data-IT-placeholder="${sVatPlaceholderOptional}<c:if test="${not empty sVatPlaceholderIT}"> - </c:if>${sVatPlaceholderIT}"
                                                                           data-SM-placeholder="${sVatPlaceholderOptional}<c:if test="${not empty sVatPlaceholderSM}"> - </c:if>${sVatPlaceholderSM}"
                                                                           data-VA-placeholder="${sVatPlaceholderOptional}<c:if test="${not empty sVatPlaceholderVA}"> - </c:if>${sVatPlaceholderVA}"/>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:when test="${isExport}">
                                                            <spring:message code="vat.placeholder.text.BG" var="sVatPlaceholderBG" />
                                                            <spring:message code="vat.placeholder.text.HR" var="sVatPlaceholderHR" />
                                                            <spring:message code="vat.placeholder.text.CY" var="sVatPlaceholderCY" />
                                                            <spring:message code="vat.placeholder.text.GR" var="sVatPlaceholderGR" />
                                                            <spring:message code="vat.placeholder.text.IE" var="sVatPlaceholderIE" />
                                                            <spring:message code="vat.placeholder.text.LU" var="sVatPlaceholderLU" />
                                                            <spring:message code="vat.placeholder.text.MT" var="sVatPlaceholderMT" />
                                                            <spring:message code="vat.placeholder.text.PT" var="sVatPlaceholderPT" />
                                                            <spring:message code="vat.placeholder.text.SI" var="sVatPlaceholderSI" />
                                                            <spring:message code="vat.placeholder.text.ES" var="sVatPlaceholderES" />
                                                            <spring:message code="vat.placeholder.text.GB" var="sVatPlaceholderGB" />
                                                            <spring:message code="vat.placeholder.text.XI" var="sVatPlaceholderXI" />
                                                            <input id="groupVatId" class="vatID" name="groupVatId"
                                                                   data-BG-placeholder="${sVatPlaceholderBG}"
                                                                   data-HR-placeholder="${sVatPlaceholderHR}"
                                                                   data-CY-placeholder="${sVatPlaceholderCY}"
                                                                   data-GR-placeholder="${sVatPlaceholderGR}"
                                                                   data-IE-placeholder="${sVatPlaceholderIE}"
                                                                   data-LU-placeholder="${sVatPlaceholderLU}"
                                                                   data-MT-placeholder="${sVatPlaceholderMT}"
                                                                   data-PT-placeholder="${sVatPlaceholderPT}"
                                                                   data-SI-placeholder="${sVatPlaceholderSI}"
                                                                   data-ES-placeholder="${sVatPlaceholderES}"
                                                                   data-GB-placeholder="${sVatPlaceholderGB}"
                                                                   data-XI-placeholder="${sVatPlaceholderGB}"
                                                            />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <spring:message code="vat.placeholder.text" var="sVatPlaceholder" />
                                                            <c:choose>
                                                                <c:when test="${isMandatory}">
                                                                    <input id="groupVatId" class="vatID js-validate" name="groupVatId" placeholder="${sVatPlaceholder}"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <spring:message code="vat.placeholder.optional" var="sVatPlaceholderOptional" />
                                                                    <input id="groupVatId" class="vatID js-optional" name="groupVatId" placeholder="${sVatPlaceholder}"/>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <i class="fa fa-check ticks2 tickItem hidden"></i>
                                                    <i class="fa fa-times ticks2 tickItemError hidden"></i>
                                                </div>
                                                <c:choose>
                                                    <c:when test="${not isExport}">
                                                        <div class="inputPopup inputPopup--grouped">
                                                            <div class="inputPopup__center">
                                                                <div id="vatIdHint" class="inputPopup__popup">${sCompanyHintText}</div>
                                                            </div>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <spring:message code="checkoutregister.vat.prefill.hint-BG" var="vatIdHintBG" />
                                                        <spring:message code="checkoutregister.vat.prefill.hint-CY" var="vatIdHintCY" />
                                                        <spring:message code="checkoutregister.vat.prefill.hint-ES" var="vatIdHintES" />
                                                        <spring:message code="checkoutregister.vat.prefill.hint-GB" var="vatIdHintGB" />
                                                        <spring:message code="checkoutregister.vat.prefill.hint-XI" var="vatIdHintXI" />
                                                        <spring:message code="checkoutregister.vat.prefill.hint-GR" var="vatIdHintGR" />
                                                        <spring:message code="checkoutregister.vat.prefill.hint-HR" var="vatIdHintHR" />
                                                        <spring:message code="checkoutregister.vat.prefill.hint-IE" var="vatIdHintIE" />
                                                        <spring:message code="checkoutregister.vat.prefill.hint-LU" var="vatIdHintLU" />
                                                        <spring:message code="checkoutregister.vat.prefill.hint-MT" var="vatIdHintMT" />
                                                        <spring:message code="checkoutregister.vat.prefill.hint-PT" var="vatIdHintPT" />
                                                        <spring:message code="checkoutregister.vat.prefill.hint-SI" var="vatIdHintSI" />

                                                        <div class="inputPopup inputPopup--grouped">
                                                            <div class="inputPopup__center">
                                                                <div id="vatIdHint" class="inputPopup__popup"
                                                                     data-BG-hint = "${vatIdHintBG}"
                                                                     data-CY-hint = "${vatIdHintCY}"
                                                                     data-ES-hint = "${vatIdHintES}"
                                                                     data-GB-hint = "${vatIdHintGB}"
                                                                     data-XI-hint = "${vatIdHintXI}"
                                                                     data-GR-hint = "${vatIdHintGR}"
                                                                     data-HR-hint = "${vatIdHintHR}"
                                                                     data-IE-hint = "${vatIdHintIE}"
                                                                     data-LU-hint = "${vatIdHintLU}"
                                                                     data-MT-hint = "${vatIdHintMT}"
                                                                     data-PT-hint = "${vatIdHintPT}"
                                                                     data-SI-hint = "${vatIdHintSI}"
                                                                ></div>
                                                            </div>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:choose>
                                                    <c:when test="${not isExport}">
                                                        <spring:message code="vat.validation.error.${currentCountry.isocode}" var="sVatError" />
                                                        <div class="error error-message hidden">
                                                            <span id="vatIdError">${sVatError}</span>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <spring:message code="vat.validation.error.BG" var="vatIdErrorBG" />
                                                        <spring:message code="vat.validation.error.CY" var="vatIdErrorCY" />
                                                        <spring:message code="vat.validation.error.ES" var="vatIdErrorES" />
                                                        <spring:message code="vat.validation.error.GB" var="vatIdErrorGB" />
                                                        <spring:message code="vat.validation.error.GR" var="vatIdErrorGR" />
                                                        <spring:message code="vat.validation.error.HR" var="vatIdErrorHR" />
                                                        <spring:message code="vat.validation.error.IE" var="vatIdErrorIE" />
                                                        <spring:message code="vat.validation.error.LU" var="vatIdErrorLU" />
                                                        <spring:message code="vat.validation.error.MT" var="vatIdErrorMT" />
                                                        <spring:message code="vat.validation.error.PT" var="vatIdErrorPT" />
                                                        <spring:message code="vat.validation.error.SI" var="vatIdErrorSI" />
                                                        <div class="error error-message hidden">
                                                            <span id="vatIdError"
                                                                  data-BG-error = "${vatIdErrorBG}"
                                                                  data-CY-error = "${vatIdErrorCY}"
                                                                  data-ES-error = "${vatIdErrorES}"
                                                                  data-GB-error = "${vatIdErrorGB}"
                                                                  data-GR-error = "${vatIdErrorGR}"
                                                                  data-HR-error = "${vatIdErrorHR}"
                                                                  data-IE-error = "${vatIdErrorIE}"
                                                                  data-LU-error = "${vatIdErrorLU}"
                                                                  data-MT-error = "${vatIdErrorMT}"
                                                                  data-PT-error = "${vatIdErrorPT}"
                                                                  data-SI-error = "${vatIdErrorSI}"></span>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </c:if>

                                        <a id="continueStep" href="#" title="${sContinue}" data-aainteraction="continue from company">${sContinue}</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                   </div>
               </div>
           </div>
       </div>
    </section>
    <section class="form-b2b__personaldetails js-personaldetails card-wrapper">
        <div class="form-b2b__companydetails__title">
            <h3>3. ${sStep3}</h3>
        </div>

        <div class="step-toggle col hidden">
            <div class="col-12">
                <div class="row">
                    <div class="form-b2b__form__field col-12 col-lg-4">
                        <h4 id="regAboutYouTitleB2B"><spring:message code="standalone.reg.step3.aboutYou" /></h4>
                        <label id="regTitleHelpB2B">
                            <spring:message code="checkoutregister.titleHelp" />
                        </label>
                        <div class="form-b2b__form__field__title field-title">
                            <div class="row">
                                <div class="col-12">
                                    <div class="form-check-inline">
                                        <form:radiobutton path="titleCode" id="inlineMrB2B" value="mr" cssClass="js-validate title"/>
                                        <form:label for="inlineMrB2B" path="titleCode">${regMr}</form:label>
                                    </div>
                                    <div class="form-check-inline">
                                        <form:radiobutton path="titleCode" id="inlineMsB2B" value="ms" cssClass="js-validate title"/>
                                        <form:label for="inlineMsB2B" path="titleCode">${regMs}</form:label>
                                    </div>
                                    <i class="tickItem fa fa-check hidden"></i>
                                    <i class="tickItem fa fa-times hidden"></i>
                                    <div class="field-msgs" id="registerForm-error-title-b2b">
                                        <div class="error hidden">
                                            <span id="titleCode.errorsB2B">${sTitleInvalid}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="form-b2b__form__field col-12 col-lg-10">
                        <div class="row">
                            <div class="col-lg-6">
                                <label id="regFirstNameB2C"><spring:message code="addressform.firstName" /></label>
                                <div class="form-group">
                                    <formUtil:formInputBox idKey="register.firstNameB2B" path="firstName" placeHolderKey="" maxLength="35" inputCSS="js-validate js-validate-empty" mandatory="true" />
                                    <i class="tickItem fa fa-check hidden"></i>
                                    <i class="tickItemError fa fa-times hidden"></i>
                                    <div class="field-msgs" id="registerForm-error-firstName-b2b">
                                        <div class="error hidden">
                                            <span id="registerForm-error-firstName-b2b-txt"><spring:message code="register.firstName.invalid" /></span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-lg-6">
                                <label id="regLastNameB2C"><spring:message code="register.lastName" /></label>
                                <div class="form-group">
                                    <formUtil:formInputBox idKey="register.lastNameB2B" path="lastName" placeHolderKey="" maxLength="35" inputCSS="js-validate js-validate-empty" mandatory="true" />
                                    <i class="tickItem fa fa-check hidden"></i>
                                    <i class="tickItemError fa fa-times hidden"></i>
                                    <div class="field-msgs" id="registerForm-error-lastName-b2b">
                                        <div class="error hidden">
                                            <span id="registerForm-error-lastName-b2b-txt"><spring:message code="register.lastName.invalid" /></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="form-b2b__form__field col-12 col-lg-10">
                        <label id="regPhoneNumberB2B">
                            <spring:message code="addressform.phoneNumber" />
                        </label>
                        <div class="form-group">
                            <formUtil:formInputBox idKey="register.phoneNumberB2B" path="telephoneNumber" placeHolderKey="standalone.reg.number.placeholder" inputCSS="js-validate js-validate-empty js-libphonenumber" maxLength="241" mandatory="true" />
                            <i class="tickItem fa fa-check hidden"></i>
                            <i class="tickItemError fa fa-times hidden"></i>
                            <div class="field-msgs" id="registerForm-error-phoneNumber-b2b">
                                <div class="error hidden">
                                    <span id="registerForm-error-phoneNumber-b2b-txt"><spring:message code="address.contactPhone.invalid" /></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    
                    <c:if test="${isJobRoleShown}">
                        <div class="form-b2b__form__field form-b2b__form__field--select col-12 col-lg-10">
                            <div class="form-group">
                                <formUtil:formLabel idKey="register.functionB2B" labelKey="register.function" path="functionCode"  />
                                <div class="p-relative js-p-relative">
                                    <formUtil:formSelectBox idKey="register.functionB2B" path="functionCode" mandatory="true" skipBlank="false" selectCSSClass="selectpicker js-validate js-validate-select ux-selectpicker" skipBlankMessageKey="register.function.help" items="${functions}" />
                                    <i class="ux-selectpicker__angle-down fa fa-angle-down"></i>
                                    <i class="tickItem fa fa-check hidden"></i>
                                    <i class="tickItemError fa fa-times hidden"></i>
                                </div>
                                <div class="field-msgs" id="registerForm-error-functionCode-b2b">
                                    <div class="error hidden">
                                        <span id="registerForm-error-functionCode-b2b-txt"><spring:message code="register.function.invalid" /></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${!isExportShop}">
                        <div class="form-b2b__form__field form-b2b__form__field--country col-12 col-lg-10">
                            <div class="form-group">
                                <c:choose>
                                    <c:when test="${fn:length(countriesB2B) gt 1}">
                                        <formUtil:formLabel idKey="register.countryCodeB2B" labelKey="checkoutregister.country" path="countryCode"  />
                                        <div class="p-relative js-p-relative">
                                            <formUtil:formSelectBox idKey="register.countryCodeB2B" path="countryCode" mandatory="true" skipBlank="false" selectCSSClass="selectpicker ux-selectpicker js-validate js-validate-select js-country-select countrySelectExportShop select-countryCode" skipBlankMessageKey="form.select.empty" items="${countriesB2B}" itemValue="isocode" />
                                            <i class="ux-selectpicker__angle-down fa fa-angle-down"></i>
                                            <i class="tickItem fa fa-check hidden"></i>
                                            <i class="tickItemError fa fa-times hidden"></i>
                                        </div>
                                        <div class="field-msgs" id="registerForm-error-countryCode-b2b">
                                            <div class="error hidden">
                                                <span id="registerForm-error-countryCode-b2b-txt"><spring:message code="validate.error.required" /></span>
                                            </div>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <input id="countryCode" type="hidden" value="${currentCountry.isocode}" name="countryCode" />
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:if>

                    <div class="form-b2b__form__field form-b2b__form__field col-12 col-lg-10">
                        <h4 id="regStep3AccountDetailsB2B"><spring:message code="standalone.reg.step3.accDetails" /></h4>
                        <label id="standalone.email.label">
                            <spring:message code="standalone.reg.step3.email.label" />
                        </label>
                        <div class="form-group has-loader grouped validCounts2">
                            <formUtil:formInputBox idKey="register.emailB2B" path="email" value="${emailToRegister}" placeHolderKey="standalone.reg.step3.email.label" maxLength="241" inputCSS="js-validate js-validate-email js-already-exists-validation" mandatory="true" />
                            <i class="tickItem fa fa-check hidden"></i>
                            <i class="tickItemError fa fa-times hidden"></i>
                            <div class="field-msgs" id="registerForm-error-email-b2b">
                                <div class="error hidden">
                                    <span id="email.errorsB2B"><spring:message code="address.email.invalid" /></span>
                                </div>
                                <div class="error-already-existing js-error-already-existing hidden">
                                    <span id="email.errors.already.existingB2B">
                                        <spring:message code="address.email.alreadyExists" arguments="${loginUrl}"/>
                                    </span>
                                </div>
                            </div>
                            <span class="loading js-loading hidden">
                                <img class="loading-icon img-fluid" src="/_ui/all/media/img/page-preloader.gif">
                            </span>
                        </div>

                        <div class="inputPopup inputPopup--grouped">
                            <div class="inputPopup__center">
                                <div id="registration.useThisToLogin.b2b" class="inputorgNumberPopup__popup"><spring:message code="standalone.reg.step3.primaryemail" /></div>
                            </div>
                        </div>
                    </div>

                    <div class="form-b2b__form__field form-b2b__form__field--invoice col-12 col-lg-10">
                        <c:set var="isIT" value="${currentCountry.isocode eq 'IT' || currentCountry.isocode eq 'SM' || currentCountry.isocode eq 'VA'}"/>
                        <c:if test="${isIT eq 'false'}">
                            <a href="#"><spring:message code="standalone.reg.step3.addInvoiceEmail" />&nbsp;(<spring:message code="vat.placeholder.optional" />)</a>
                        </c:if>
                        <div class="form-group hidden">
                            <label id="regStep3InvoiceEmailB2B"><spring:message code="standalone.reg.step3.invoiceEmail" />&nbsp;(<spring:message code="vat.placeholder.optional" />)</label>
                            <formUtil:formInputBox idKey="register.emailB2BInvoice" path="invoiceEmail" inputCSS="js-validate-email js-optional" maxLength="241" mandatory="false" />
                            <i class="tickItem fa fa-check hidden"></i>
                            <i class="tickItemError fa fa-times hidden"></i>
                            <div class="field-msgs" id="registerForm-error-invoiceEmail-b2b">
                                <div class="error hidden">
                                    <span id="invoice.email.errors"><spring:message code="validate.error.email" /></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="pwd-container" class="form-b2b__form__field col-12 col-lg-10">
                        <label id="regPasswordLabelB2B">
                            <spring:message code="newcheckout.password.label" />
                        </label>
                        <div class="form-group form-group--password mb-2">
                            <formUtil:formPasswordBox idKey="passwordB2B" placeHolderKey="" inputCSS="js-validate password-field password-check" path="pwd" mandatory="true" />
                            <span class="form-group__pwd-reveal">
                                <i class="fas fa-eye form-group__pwd-reveal-icon hidden">&nbsp;</i>
                                <i class="fas fa-eye-slash form-group__pwd-reveal-icon">&nbsp;</i>
                            </span>
                            <div id="b2b_registration_pwd_help_text" class="help-text">
                                <p id="b2b_registration_pwd_help_text-txt"><spring:message code="register.pwd.help.text" /></p>
                            </div>
                            <i class="tickItem fa fa-check hidden"></i>
                            <i class="tickItemError fa fa-times hidden"></i>
                        </div>
                    </div>

                    <div class="form-b2b__form__field col-12 col-lg-10">
                        <label id="regPasswordConfirmationLabelB2B">
                            <spring:message code="register.checkPwd" />
                        </label>
                        <div class="form-group form-group--password mb-4">
                            <formUtil:formPasswordBox idKey="registercheckPwdB2B" path="checkPwd" placeHolderKey="" inputCSS="js-validate passwordSecond password-check" mandatory="true" errorPath="${modelAttribute}" />
                            <span class="form-group__pwd-reveal">
                                <i class="fas fa-eye form-group__pwd-reveal-icon hidden">&nbsp;</i>
                                <i class="fas fa-eye-slash form-group__pwd-reveal-icon">&nbsp;</i>
                            </span>
                            <i class="tickItem fa fa-check hidden"></i>
                            <i class="tickItemError fa fa-times hidden"></i>
                            <div class="field-msgs" id="registerForm-error-pwdFirst-b2b">
                                <div class="error hidden">
                                    <span id="password.not.match.error-b2b">${sPasswordNoMatch}</span>
                                </div>
                            </div>
                            <mod:password-strength />
                        </div>
                    </div>

                    <div class="form-b2b__form__field form-b2b__form__field--terms col-12 col-lg-8">
                        <div class="recaptcha-holder recaptcha">
                            <mod:captcha template="reg" htmlClasses="form-b2b" callback="onSubmitRegB2B" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>


    <section class="card-wrapper section-form col-12 js-preferencesSection js-consentSection hidden">
        <div class="form-b2b__form half-container">
            <div class="form-b2b__form__field is-full">
                <h4 class="no-padding"><spring:message code="text.communication.preferences"/></h4>
            </div>
        </div>

        <div class="half-container">
            <div><span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="text.preferences.email.b2b" labelKey="text.preferences.receive.communication" path="marketingConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></div>
            <div id="text.preferences.newsletter.disclaimer.b2b" class="description-text"><spring:message code="text.preferences.newsletter.disclaimer"/></div>
        </div>

        <div class="half-container">
            <div>
                <ul>
                    <li><span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="text.preferences.sms.b2b" labelKey="text.preferences.sms" path="smsConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></li>
                    <li><span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="text.preferences.phone.b2b" labelKey="text.preferences.phone" path="phoneConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></li>
                    <li><span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="text.preferences.post.b2b" labelKey="text.preferences.post" path="postConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></li>
                </ul>
            </div>
            <div id="text.preferences.sms.disclaimer.b2b" class="description-text"><spring:message code="text.preferences.sms.disclaimer"/></div>
        </div>

        <div class="half-container">
            <div><span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="personalise-communication-b2b" labelKey="text.preferences.receive.personalised" path="personalisationConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></div>
            <div id="text.preferences.personalise.content.b2b" class="description-text"><spring:message code="text.preferences.personalise.content.legal"/></div>
        </div>
        <div class="half-container">
            <div><span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="profling-communication-b2b" labelKey="text.preferences.combine.data" path="profilingConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></div>
            <c:url value="/information-notice/cms/datenschutz#thirdPartyCookies" var="thirdPartyCookiesLink"/>
            <div id="text.preferences.profiling.data.b2b" class="description-text"><spring:message code="text.preferences.profiling.data.legal" arguments="${thirdPartyCookiesLink}"/></div>
        </div>

        <p id="text.preferences.newsletter.disclaimer.after.register.b2b"><spring:message code="text.preferences.newsletter.disclaimer.after.register"/></p>

        <c:url value="/data-protection/cms/datenschutz" var="privacyLink"/>
        <c:url value="/general-terms-and-conditions/cms/agb?marketingPopup=false" var="termsLink"/>
        <div class="half-container half-container--t_and_c">
            <div data-error-text="<spring:message code="text.preferences.terms.error"/>" class="privacy-policy"><span class="js-checkboxIcon js-terms-check"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="privacyPolicyPopupB2b" labelKey="text.registration.accept.privacy" labelArguments="${termsLink},${privacyLink}" path="termsOfUseOption" inputCSS="js-validate js-checkbox js-termsCheckbox" mandatory="false"/></div>
            <div id="text.registration.accept.registering.b2b" class="description-text"><spring:message code="text.registration.accept.registering"/></div>
        </div>

        <div class="form-b2c__form__field form-b2c__form__field--create col-12 col-lg-8">
            <div class="row">
                <div class="col-12 col-lg-4">
                    <button class="mat-button mat-button--action-green btn-success mb-4 btn-register-customer-form js-submitB2bRegister disabled" type="submit" data-aainteraction="submit form">
                        <spring:message code="standalone.reg.step3.createAcc" />
                    </button>
                </div>
            </div>
        </div>
    </section>

    <section class="card-wrapper padded-content col-12 js-preferencesDescriptionSection hidden">
        <c:url value="/cms/contact" var="contactLink"/>
        <h2 id="text.preferences.data.b2b"><spring:message code="text.preferences.data"/></h2>
        <p id="text.preferences.privacy.policy.b2b"><spring:message code="text.preferences.privacy.policy"/></p>
        <p id="text.preferences.privacy.policy.2.b2b"><spring:message code="text.preferences.privacy.policy.2" arguments="${contactLink}"/></p>
        <p id="text.preferences.question.b2b"><spring:message code="text.preferences.question"/></p>
        <h4 id="text.registration.control.b2b"><spring:message code="text.registration.control"/></h4>
        <p id="text.registration.change.communication.b2b"><spring:message code="text.registration.change.communication"/></p>
    </section>

    <script id="tmpl-bisnodereg-validation-error-empty" type="text/template">
            <spring:message code="validate.error.required" />
    </script>
    <script id="tmpl-bisnodereg-validation-error-email" type="text/template">
            <spring:message code="validate.error.email" />
    </script>

    <input type="hidden" class="duns" id="duns" name="duns" />
    <input type="hidden" class="customerId" id="customerId" name="customerId" />
    <input type="hidden" id="regType" name="registrationType" value="${registerB2BForm.registrationType}" />
    <input type="hidden" class="existingCustomerb2b" id="existingCustomerb2b" name="existingCustomer" value="false" />
    <input type="hidden" id="vatIdMessage" value="${registerB2BForm.vatIdMessage}" />
    <input type="hidden" id="countryCode" value="${registerB2BForm.countryCode}" />
</form:form>
