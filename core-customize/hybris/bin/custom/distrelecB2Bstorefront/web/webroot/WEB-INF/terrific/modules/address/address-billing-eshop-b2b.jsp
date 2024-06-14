<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:url value="/checkout/address/b2eaddress/new" var="changeAddressUrl"/>

<div class="box box-address box-address-${address.id} address-billing-b2b">
    <div class="row">
        <div class="col-12 box-address__preview">
            <c:if test="${not empty address.companyName}">
                <p>
                    <c:out value="${address.companyName}"/>
                </p>
            </c:if>
            <c:if test="${not empty address.companyName2}">
                <p>
                    <c:out value="${address.companyName2}"/>
                </p>
            </c:if>
           
            <c:if test="${not empty address.line1}">
                <p>
                    <c:out value="${address.line1}"/>
                    <c:out value=" ${address.line2}"/>
                </p>
            </c:if>
            <c:if test="${not empty address.postalCode}">
                <p>
                    <c:out value="${address.postalCode}"/>
                    <c:out value=" ${address.town}"/>
                </p>
            </c:if>
            <c:if test="${not empty address.country.name}">
                <p>
                    <c:out value="${address.country.name}"/>
                </p>
            </c:if>
            </div>
            <c:if test="${not isExportShop}">
                <c:if test="${showBillingEdit eq true}">
                    <div class="col-12 box-address__edit">
                        <a href="#" class="address__edit__link">
                            <spring:message code="checkout.address.buttonChange" text="Change"/>
                        </a>
                    </div>
                </c:if>
            </c:if>
        
        <form:form class="col-12 box-address__form${showBillingEdit ? ' hidden' : '' }" method="post" modelAttribute="guestForm" id="guestForm" action="${changeAddressUrl}">
        	<input type="hidden" id="addressId" name="addressId" value="${address.id}" />
            <div class="box-address__form__select">
                <formUtil:formSelectBox idKey="titleCode" path="titleCode" mandatory="true" skipBlank="false" selectCSSClass="custom-select" skipBlankMessageKey="form.select.empty" items="${titles}" selectedValue="${address.titleCode}"/>
            </div>
            <div class="box-address__form__field">
                <formUtil:formInputBox idKey="firstName" path="firstName" placeHolderKey="checkoutregister.firstName" inputCSS="validate-empty" mandatory="true" value="${address.firstName}"/>
                <label for="firstName">&nbsp;</label>
            </div>
            <div class="box-address__form__field">
                <formUtil:formInputBox idKey="lastName" path="lastName" placeHolderKey="checkoutregister.lastName" inputCSS="validate-empty" mandatory="true" value="${address.lastName}"/>
                <label for="lastName">&nbsp;</label>
            </div>

            <c:if test="${currentCountry.isocode eq 'IT'}">
            <div class="box-address__form__field">
                <formUtil:formInputBox idKey="register.codiceFiscale" path="codiceFiscale" placeHolderKey="register.codiceFiscale.placeholder" maxLength="30" inputCSS="validate-empty" mandatory="true"/>
                <label for="codiceFiscale">&nbsp;</label>
            </div>
            </c:if>

            <div class="box-address__form__field">
                <formUtil:formInputBox idKey="email" path="email" placeHolderKey="register.email" inputCSS="validate-empty" mandatory="true" value="${address.email}"/>
                <label for="email">&nbsp;</label>
            </div>

            <div class="box-address__form__field">
                <formUtil:formInputBox idKey="line1" path="streetName" placeHolderKey="register.strName" inputCSS="validate-empty" mandatory="true" value="${address.line1}" maxLength="60"/>
                <label for="streetName">&nbsp;</label>
            </div>

            <div class="box-address__form__field">
                <formUtil:formInputBox idKey="line2" path="streetNumber" placeHolderKey="register.strNumber" inputCSS="validate-empty" mandatory="true" value="${address.line2}" maxLength="10"/>
                <label for="streetNumber">&nbsp;</label>
            </div>

            <c:choose>
                <c:when test="${currentCountry.isocode eq 'CH'}">
                    <div class="box-address__form__field">
                        <formUtil:formInputBox idKey="postalCode" path="postalCode" placeHolderKey="register.postalCode" inputCSS="validate-empty" mandatory="true" maxLength="10" value="${address.postalCode}"/>
                        <label for="postalCode">&nbsp;</label>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="box-address__form__field">
                        <formUtil:formInputBox idKey="postalCode" path="postalCode" placeHolderKey="register.postalCode" inputCSS="validate-empty" mandatory="true" maxLength="10" value="${address.postalCode}"/>
                        <label for="postalCode">&nbsp;</label>
                    </div>
                </c:otherwise>
            </c:choose>

            <div class="box-address__form__field">
                <formUtil:formInputBox idKey="town" path="town" placeHolderKey="register.town" inputCSS="validate-empty" mandatory="true" value="${address.town}" maxLength="60"/>
                <label for="town">&nbsp;</label>
            </div>

            <div class="box-address__form__select">
                <formUtil:formSelectBox idKey="countryCode" path="countryCode" mandatory="true" skipBlank="false" selectedValue="${address.country.isocode}" selectCSSClass="selectpicker validate-dropdown countrySelectExportShop" skipBlankMessageKey="form.select.empty" items="${countries}" itemValue="isocode"/>
            </div>

            <c:if test="${not empty countries[0].regions}">
                <c:choose>
                    <c:when test="${empty address.region}">
                        <c:set var="selectedRegion" scope="request" value="${regionCode}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="selectedRegion" scope="request" value="${address.region.isocode}"/>
                    </c:otherwise>
                </c:choose>
                <div class="box-address__form__select">
                    <formUtil:formSelectBox idKey="register.regionCode" path="regionCode" mandatory="true" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${countries[0].regions}" itemValue="isocode" />
                </div>
            </c:if>

            <div class="box-address__form__field">
                <formUtil:formInputBox idKey="phoneNumber" path="phoneNumber" placeHolderKey="register.phoneNumber.placeholder" inputCSS="validate-empty" mandatory="true" value="${address.phone}"/>
                <label for="phoneNumber">&nbsp;</label>
            </div>

            <div class="box-address__form__submit clear">
                <c:choose>
                    <c:when test="${fn:length(billingAddresses) gt 0}">
                        <button type="submit" class="mat-button mat-button--action-green">
                            <spring:message code="lightbox.orderReference.button.save" text="Save changes"/>
                        </button>
                        <a href="#" class="box-address__form__submit__cancel">
                            <spring:message code="addressform.buttonCancel"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <button type="submit" class="mat-button mat-button--action-green">
                            <spring:message code="lightboxshopsettings.save" text="Save changes"/>
                        </button>
                    </c:otherwise>
                </c:choose>
            </div>
        </form:form>
    </div>
</div>
