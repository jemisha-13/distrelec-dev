<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="cssErrorClass" value="" />
<c:set var="readonlyClass" value="" />


<spring:theme code="lightbox.confirm.set.address.default" var="sSetAddressDefault" />

<c:set var="modelAttribute" value="b2BBillingAddressForm" />
<c:if test="${isShippingAddress && !isBillingAddress}">
	<c:set var="modelAttribute" value="b2BShippingAddressForm" />
</c:if>

<%-- readonly class hides save, cancel and back button on form --%>
<c:if test="${isBillingAddress && customerType == 'B2B_KEY_ACCOUNT'}">
	<c:set var="readonlyClass" value="readonly" />
</c:if>

<spring:theme code="addressform.buttonCancel" var="sButtonCancel" />
<spring:theme code="addressform.buttonBack" var="sButtonBack" />
<spring:theme code="addressform.buttonDelete" var="sButtonDelete" />
<spring:theme code="addressform.buttonSave" var="sButtonSave" />
<spring:theme code="addressform.deleteLightbox.title" var="lightboxTitle" />
<spring:theme code="addressform.deleteLightbox.message" var="lightboxMessage" />
<spring:theme code="addressform.deleteLightbox.confirm" var="lightboxConfirmButtonText" />
<spring:theme code="addressform.deleteLightbox.deny" var="lightboxDenyButtonText" />
<spring:theme code="register.latin-info-message" text="" var="sLatinInfoMessage" />

<spring:message code="text.setDefault" var="sSetDefault" />
<spring:theme code="addressform.deleteLightbox.title" var="lightboxTitle" />
<spring:theme code="addressform.deleteLightbox.message" var="lightboxMessage" />
<spring:theme code="addressform.deleteLightbox.confirm" var="lightboxConfirmButtonText" />
<spring:theme code="addressform.deleteLightbox.deny" var="lightboxDenyButtonText" />

<c:set var="companyNameReadOnly" value="${not empty canEditCompanyName && not canEditCompanyName}" />


<h2 class="head">${formTitle}</h2>
<div class="form-box base ${readonlyClass}" data-customer-type="${customerType}" data-customer-channel="${customerChannel}" data-address-type="${addressType}">
	<form:form action="${actionUrl}" modelAttribute="${modelAttribute}" method="post" cssClass="address-form">
		<form:hidden path="addressId" />
		<form:hidden path="shippingAddress" />
		<form:hidden path="billingAddress" />

		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="companyName" labelKey="addressform.companyName" path="companyName" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox readonly="${companyNameReadOnly}" idKey="companyName" path="companyName" placeHolderKey="addressform.companyName.placeholder" maxLength="35" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="companyName2" labelKey="addressform.companyName2" path="companyName2" mandatory="false" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="companyName2" path="companyName2" placeHolderKey="addressform.companyName2.placeholder" maxLength="35" mandatory="false" />
			</div>
		</div>

		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="line1" labelKey="addressform.line1" path="line1" mandatory="true" />
				<c:if test="${currentSalesOrg.code eq '7801'}">
					<span class="latin-info-message-inline">(${sLatinInfoMessage})</span>
				</c:if>
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
		
		
		
		<c:if test="${isBillingAddress || isShippingAddress}">
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="addressform.phoneNumber" labelKey="addressform.phoneNumber" path="phoneNumber" mandatory="true" stars="**" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="addressform.phoneNumber"  path="phoneNumber" placeHolderKey="addressform.phoneNumber.placeholder" maxLength="30" mandatory="true" />
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
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="addressform.faxNumber" labelKey="addressform.faxNumber" path="faxNumber" mandatory="false" stars="" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="addressform.faxNumber" path="faxNumber" placeHolderKey="addressform.faxNumber.placeholder" maxLength="30" mandatory="false" />
				</div>
			</div>		
		</c:if>
		
		
 


		<div class="row buttons">
			<div class="gu-5 label-box">
				<a href="${cancelUrl}" class="btn btn-secondary btn-cancel"><i></i>${sButtonCancel}</a> 
				<a href="${cancelUrl}" class="btn btn-secondary btn-back"><i></i>${sButtonBack}</a>
				<c:if test="${showDeleteButton}">
					<a href="#"
					   data-action="/my-account/remove-address/"
					   data-address-id="${addressForm.addressId}"
					   data-lightbox-title="${lightboxTitle}"
					   data-lightbox-message="${lightboxMessage}"
					   data-lightbox-btn-deny="${lightboxDenyButtonText}"
					   data-lightbox-show-confirm-button="true"
					   data-lightbox-btn-conf="${lightboxConfirmButtonText}"
					   class="btn btn-secondary btn-delete"><i></i>${sButtonDelete}</a>
				</c:if>
				
				
<%-- 				<c:if test="${paymentAddress.defaultBilling eq false}"> 
					<a href="#"
					   data-action="/my-account/set-default-address?addressCode="
					   data-is-billing="${addressForm.billingAddress}"
					   data-is-shipping="${addressForm.shippingAddress}"
					   data-address-id="${addressForm.addressId}"
					   data-lightbox-title="${lightboxTitle}"
					   data-lightbox-message="${sSetAddressDefault}"
					   data-lightbox-btn-deny="${lightboxDenyButtonText}"
					   data-lightbox-show-confirm-button="true"
					   data-lightbox-btn-conf="${lightboxConfirmButtonText}"
					   class="btn btn-secondary btn-set-default set-as-default-button">${sSetDefault}</a>
				
 				</c:if> --%>
				
				
			</div>



			<div class="gu-4 field button-save">
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