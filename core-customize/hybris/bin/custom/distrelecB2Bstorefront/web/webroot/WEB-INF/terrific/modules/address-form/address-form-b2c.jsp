<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="cssErrorClass" value="" />
<c:set var="modelAttribute" value="b2CAddressForm" />

<spring:theme code="addressform.buttonCancel" var="sButtonCancel" />
<spring:theme code="addressform.buttonDelete" var="sButtonDelete" />
<spring:theme code="addressform.buttonSave" var="sButtonSave" />
<spring:theme code="addressform.deleteLightbox.title" var="lightboxTitle" />
<spring:theme code="addressform.deleteLightbox.message" var="lightboxMessage" />
<spring:theme code="addressform.deleteLightbox.confirm" var="lightboxConfirmButtonText" />
<spring:theme code="addressform.deleteLightbox.deny" var="lightboxDenyButtonText" />

<h2 class="head">${formTitle}</h2>
<div class="form-box base" data-customer-type="${customerType}" data-customer-channel="${customerChannel}" data-address-type="${addressType}">
	<form:form action="${actionUrl}" modelAttribute="${modelAttribute}" method="post" cssClass="address-form">
		<form:hidden path="addressId" />
		<form:hidden path="shippingAddress" />
		<form:hidden path="billingAddress" />

		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="title" labelKey="addressform.title" path="titleCode" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formSelectBox idKey="title" path="titleCode" mandatory="true" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${titles}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="firstName" labelKey="addressform.firstName" path="firstName" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="firstName" path="firstName" placeHolderKey="addressform.firstName.placeholder" maxLength="35" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="lastName" labelKey="addressform.lastName" path="lastName" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="lastName" path="lastName" placeHolderKey="addressform.lastName.placeholder" maxLength="35" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="line1" labelKey="addressform.line1" path="line1" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="line1" path="line1" placeHolderKey="addressform.line1.placeholder" maxLength="60" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="line2" labelKey="addressform.line2" path="line2" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="line2" path="line2" placeHolderKey="addressform.line2.placeholder" maxLength="10" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="postcode" labelKey="addressform.postcode" path="postalCode" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="postcode" path="postalCode" placeHolderKey="addressform.postcode.placeholder" maxLength="10" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="townCity" labelKey="addressform.townCity" path="town" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="townCity" path="town" placeHolderKey="addressform.townCity.placeholder" maxLength="40" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row address-form__row">
			<div class="gu-4">
				<formUtil:formLabel idKey="countryCode" labelKey="addressform.country" path="countryIso" mandatory="true" />
			</div>
			<div class="gu-4 address-form__row__col">
				<c:choose>
					<c:when test="${siteUid eq 'distrelec_FR'}">
						<formUtil:formSelectBox idKey="countryCode" path="countryIso" mandatory="true" skipBlank="true" selectCSSClass="selectpicker validate-dropdown disabled-select" items="${countries}" itemValue="isocode" />
					</c:when>
					<c:otherwise>
						<formUtil:formSelectBox idKey="countryCode" path="countryIso" mandatory="true" skipBlank="true" selectCSSClass="selectpicker validate-dropdown" items="${countries}" itemValue="isocode" />
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<c:if test="${not empty countries[0].regions}">
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="regionCode" labelKey="addressform.region" path="regionIso" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formSelectBox idKey="regionCode" path="regionIso" mandatory="true" skipBlank="true" selectCSSClass="selectpicker validate-dropdown" items="${countries[0].regions}" itemValue="isocode" />
				</div>
			</div>
		</c:if>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="addressform.contactPhone" labelKey="addressform.contactPhone" path="contactPhone" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="addressform.contactPhone" path="contactPhone" placeHolderKey="addressform.contactPhone.placeholder" maxLength="30" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		 <div class="row">
   			<div class="gu-4">
    			<formUtil:formLabel idKey="addressform.mobileNumber" labelKey="addressform.mobileNumber" path="mobileNumber" mandatory="true" stars="**" />
   			</div>
   			<div class="gu-4">
    		<formUtil:formInputBox idKey="addressform.mobileNumber" path="mobileNumber" placeHolderKey="addressform.mobileNumber.placeholder" maxLength="30" mandatory="false" />
   			</div>
  		</div>
		<div class="row buttons">
			<div class="gu-4 label-box">
				<a href="${cancelUrl}" class="btn btn-secondary btn-cancel"><i></i>${sButtonCancel}</a> 
				<c:if test="${showDeleteButton}">
					<a href="#"
					   data-action="/my-account/remove-address/"
					   data-address-id="${b2CAddressForm.addressId}"
					   data-lightbox-title="${lightboxTitle}"
					   data-lightbox-message="${lightboxMessage}"
					   data-lightbox-btn-deny="${lightboxDenyButtonText}"
					   data-lightbox-show-confirm-button="true"
					   data-lightbox-btn-conf="${lightboxConfirmButtonText}"
					   class="btn btn-secondary btn-delete">${sButtonDelete}</a>
				</c:if>
			</div>
			<div class="gu-4 field">
				<button class="btn btn-primary btn-save" type="submit"><i></i>${sButtonSave}</button>
			</div>
		</div>
	</form:form>

	<script id="tmpl-address-form-validation-error-empty" type="text/template">
		<spring:message code="address.form.input.empty" />
	</script>

	<script id="tmpl-address-form-validation-error-dropdown" type="text/template">
		<spring:message code="validate.error.dropdown" />
	</script>

</div>