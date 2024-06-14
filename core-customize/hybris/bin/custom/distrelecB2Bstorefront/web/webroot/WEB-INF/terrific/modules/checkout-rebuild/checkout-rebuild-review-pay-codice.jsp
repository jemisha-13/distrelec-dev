<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="o-codice-posta js-codice-posta js-inline-validation" data-iv-trigger-success-only-on-page-load="true">
    <form:form modelAttribute="codiceVatForm" class="js-iv-form js-codice-posta-form">
        <c:set var="legalEmail" value="${codiceVatForm.legalEmail}"/>
        <c:set var="vat4" value="${codiceVatForm.vat4}"/>
        <c:set var="isVat4Only" value="${not empty vat4 and empty legalEmail}"/>
        <c:set var="isLegalOnly" value="${empty vat4 and not empty legalEmail}"/>
        <c:set var="bothVat4Legal" value="${not empty vat4 and not empty legalEmail}"/>
        <c:set var="noneVat4Legal" value="${empty vat4 and empty legalEmail}"/>
        <c:set var="codiceCUP" value="${codiceVatForm.codiceCUP}"/>
        <c:set var="codiceCIG" value="${codiceVatForm.codiceCIG}"/>

        <c:if test="${isVat4Only or noneVat4Legal or bothVat4Legal}">
            <div class="js-codice-block" style="">
                <h3 id="checkoutReviewCodiceTitle" class="mod-checkout-rebuild-block__title is-full"><spring:message code="vat.reg.codice.dest.label"/></h3>

                <c:if test="${noneVat4Legal}">
                    <ul class="o-cr-editable-form__info-list">
                        <li id="checkoutReviewCodiceInfo">
                            <spring:message code="vat.reg.label"/>
                        </li>
                        <li>&nbsp;</li>
                    </ul>
                </c:if>

                <div class="row">
                    <div class="col-md-6">
                        <c:set var="codiceRegex" value="^.{6,7}$"/>
                        <formUtil:ux-formInput inputId="checkoutReviewCodiceInput"
                                               path="vat4"
                                               groupClass="js-iv-item"
                                               errorGroupClass="js-iv-errors"
                                               labelKey="vat.reg.codice.dest.label"
                                               inputClass="js-iv-field js-codice-posta-field"
                                               maxLength="7"
                                               readonly="${not empty vat4}"
                                               inputAttributes="data-iv-triggers='change' data-iv-errors='empty regex' data-iv-regex='${codiceRegex}'">
                        </formUtil:ux-formInput>
                    </div>
                </div>

                <c:if test="${noneVat4Legal}">
                    <button id="codicePostaButton1" class="ux-link is-codice-pec ux-link--clean js-codice-posta-button" type="button"><spring:message code="vat.reg.codice.cert"/></button>
                </c:if>
            </div>
        </c:if>

        <c:if test="${isLegalOnly or noneVat4Legal}">
            <div class="js-posta-block" style="${noneVat4Legal ? 'display: none;' : ''}">
                <h3 id="checkoutReviewPostaTitle" class="mod-checkout-rebuild-block__title is-full"><spring:message code="vat.reg.codice.cert.title"/></h3>

                <c:if test="${noneVat4Legal}">
                    <ul class="o-cr-editable-form__info-list">
                        <li id="checkoutReviewPostaInfo">
                            <spring:message code="vat.reg.codice.cert.label"/>
                        </li>
                        <li>&nbsp;</li>
                    </ul>
                </c:if>

                <div class="row">
                    <div class="col-md-6">
                        <formUtil:ux-formInput inputId="checkoutReviewLegalEmailInput"
                                               path="legalEmail"
                                               groupClass="js-iv-item"
                                               errorGroupClass="js-iv-errors"
                                               labelKey="user.email"
                                               inputClass="${not empty legalEmail ? 'success' : 'js-iv-field'} js-codice-posta-field"
                                               readonly="${not empty legalEmail}"
                                               inputAttributes="data-iv-triggers='change' data-iv-errors='legalEmail'">
                            <div id="checkoutLegalEmailErrorMessage" class="js-iv-errors-legalEmail" hidden>
                                <spring:message code="register.email.invalid"/>
                            </div>
                        </formUtil:ux-formInput>
                    </div>
                </div>

                <c:if test="${noneVat4Legal}">
                    <button id="codicePostaButton2" class="ux-link is-codice-pec ux-link--clean js-codice-posta-button" type="button"><spring:message code="vat.reg.codice.dest"/></button>
                </c:if>
            </div>
        </c:if>

        <div class="row">
            <div class="col-12">
                <div>
                    <formUtil:ux-formCheckboxPath idKey="checkboxCupOrCig"
                                                  isChecked="${not empty codiceCUP or not empty codiceCIG}"
                                                  formGroupClass="ux-form-group"
                                                  inputCSS="js-codice-cup-cig-checkbox"
                                                  labelKey="vat.reg.codice"/>

                    <div class="js-codice-cup-cig-content" style="${empty codiceCUP and empty codiceCIG ? 'display: none;' : ''}">
                        <div class="row">
                            <div class="col-md-6">
                                <c:set var="CUPMaxlength" value="15"/>
                                <c:set var="CUPRegex" value="^.{${CUPMaxlength}}$"/>
                                <formUtil:ux-formInput inputId="codiceCup"
                                                       path="codiceCUP"
                                                       groupClass="js-iv-item"
                                                       inputClass="js-iv-field js-iv-remove-error-on-blank js-codice-posta-field"
                                                       labelKey="vat.reg.placeholder.cup"
                                                       maxLength="15"
                                                       inputAttributes="data-iv-triggers='change' data-iv-errors='regex' data-iv-regex='${CUPRegex}'"
                                                       optionalLabel="true">

                                    <div id="checkoutCUPerrorMessage" class="js-iv-errors-regex" hidden>
                                        <spring:message code="vat.reg.codice.error"/>
                                    </div>
                                </formUtil:ux-formInput>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <c:set var="CIGMaxlength" value="10"/>
                                <c:set var="CIGRegex" value="^.{${CIGMaxlength}}$"/>
                                <formUtil:ux-formInput inputId="codiceCig"
                                                       path="codiceCIG"
                                                       groupClass="js-iv-item"
                                                       inputClass="js-iv-field js-iv-remove-error-on-blank js-codice-posta-field"
                                                       labelKey="vat.reg.placeholder.cig"
                                                       maxLength="10"
                                                       inputAttributes="data-iv-triggers='change' data-iv-errors='regex' data-iv-regex='${CIGRegex}'"
                                                       optionalLabel="true">
                                    <div id="checkoutCIGerrorMessage" class="js-iv-errors-regex" hidden>
                                        <spring:message code="vat.reg.codice.cig.error"/>
                                    </div>
                                </formUtil:ux-formInput>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
</div>
