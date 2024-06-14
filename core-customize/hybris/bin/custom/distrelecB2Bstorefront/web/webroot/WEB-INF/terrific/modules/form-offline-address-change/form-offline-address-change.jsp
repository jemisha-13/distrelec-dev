<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="cssErrorClass" value="" />

<spring:theme code="formOfflineAddressChange.oldAddress" var="sOldAddress" />
<spring:theme code="formOfflineAddressChange.newAddress" var="sNewAddress" />
<spring:theme code="formOfflineAddressChange.sendButton.text" var="sSendAddressChangeButton" />

<c:set var="showForm" value="true" />

<c:if test="${showForm}">
	<form:form action="${action}" method="post" modelAttribute="addressChangeForm">
		<div class="offline-address-change-box border-top border-bottom base padding">
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="offline-address-change-customer-number" labelKey="formOfflineAddressChange.customerNumber" path="customerNumber" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="offline-address-change-customer-number" path="customerNumber" placeHolderKey="formOfflineAddressChange.customerNumber.placeholder" maxLength="10" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<h3 class="base">${sOldAddress}</h3>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="oldAddress-company-name" labelKey="formOfflineAddressChange.oldAddress.companyName" path="oldAddress.companyName" mandatory="false" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="oldAddress-company-name" path="oldAddress.companyName" placeHolderKey="formOfflineAddressChange.oldAddress.companyName.placeholder" maxLength="35" mandatory="false" inputCSS="${inputCSS}" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="oldAddress-first-name" labelKey="formOfflineAddressChange.oldAddress.firstName" path="oldAddress.firstName" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="oldAddress-first-name" path="oldAddress.firstName" placeHolderKey="formOfflineAddressChange.oldAddress.firstName.placeholder" maxLength="35" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="oldAddress-last-name" labelKey="formOfflineAddressChange.oldAddress.lastName" path="oldAddress.lastName" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="oldAddress-last-name" path="oldAddress.lastName" placeHolderKey="formOfflineAddressChange.oldAddress.lastName.placeholder" maxLength="35" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="oldAddress-department" labelKey="formOfflineAddressChange.oldAddress.department" path="oldAddress.department" mandatory="false" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="oldAddress-department" path="oldAddress.department" placeHolderKey="formOfflineAddressChange.oldAddress.department.placeholder" maxLength="35" mandatory="false" inputCSS="${inputCSS}" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="oldAddress-street" labelKey="formOfflineAddressChange.oldAddress.street" path="oldAddress.street" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="oldAddress-street" path="oldAddress.street" placeHolderKey="formOfflineAddressChange.oldAddress.street.placeholder" maxLength="60" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="oldAddress-number" labelKey="formOfflineAddressChange.oldAddress.number" path="oldAddress.number" mandatory="false" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="oldAddress-number" path="oldAddress.number" placeHolderKey="formOfflineAddressChange.oldAddress.number.placeholder" maxLength="10" mandatory="true" inputCSS="${inputCSS}" />
				</div>
			</div>
			
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="oldAddress-postal-code" labelKey="formOfflineAddressChange.oldAddress.postalCode" path="oldAddress.zip" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="oldAddress-postal-code" path="oldAddress.zip" placeHolderKey="formOfflineAddressChange.oldAddress.postalCode.placeholder" maxLength="10" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="oldAddress-town" labelKey="formOfflineAddressChange.oldAddress.town" path="oldAddress.place" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="oldAddress-town" path="oldAddress.place" placeHolderKey="formOfflineAddressChange.oldAddress.town.placeholder" maxLength="40" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="oldAddress-country" labelKey="formOfflineAddressChange.oldAddress.country" path="oldAddress.country" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="oldAddress-country" path="oldAddress.country" placeHolderKey="formOfflineAddressChange.oldAddress.country.placeholder" maxLength="40" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>

			<h3 class="base">${sNewAddress}</h3>

			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="newAddress-company-name" labelKey="formOfflineAddressChange.newAddress.company" path="newAddress.companyName" mandatory="false" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="newAddress-company-name" path="newAddress.companyName" placeHolderKey="formOfflineAddressChange.newAddress.company.placeholder" maxLength="35" mandatory="false" inputCSS="${inputCSS}" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="newAddress-first-name" labelKey="formOfflineAddressChange.newAddress.firstName" path="newAddress.firstName" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="newAddress-first-name" path="newAddress.firstName" placeHolderKey="formOfflineAddressChange.newAddress.firstName.placeholder" maxLength="35" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="newAddress-last-name" labelKey="formOfflineAddressChange.newAddress.lastName" path="newAddress.lastName" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="newAddress-last-name" path="newAddress.lastName" placeHolderKey="formOfflineAddressChange.newAddress.lastName.placeholder" maxLength="35" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="newAddress-department" labelKey="formOfflineAddressChange.newAddress.department" path="newAddress.department" mandatory="false" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="newAddress-department" path="newAddress.department" placeHolderKey="formOfflineAddressChange.newAddress.department.placeholder" maxLength="35" mandatory="false" inputCSS="${inputCSS}" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="newAddress-street" labelKey="formOfflineAddressChange.newAddress.street" path="newAddress.street" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="newAddress-street" path="newAddress.street" placeHolderKey="formOfflineAddressChange.newAddress.street.placeholder" maxLength="60" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="newAddress-number" labelKey="formOfflineAddressChange.newAddress.number" path="newAddress.number" mandatory="false" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="newAddress-number" path="newAddress.number" placeHolderKey="formOfflineAddressChange.newAddress.number.placeholder" maxLength="10" mandatory="false" inputCSS="${inputCSS}" />
				</div>
			</div>
			
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="newAddress-postal-code" labelKey="formOfflineAddressChange.newAddress.postalCode" path="newAddress.zip" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="newAddress-postal-code" path="newAddress.zip" placeHolderKey="formOfflineAddressChange.newAddress.postalCode.placeholder" maxLength="10" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="newAddress-town" labelKey="formOfflineAddressChange.newAddress.town" path="newAddress.place" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="newAddress-town" path="newAddress.place" placeHolderKey="formOfflineAddressChange.newAddress.town.placeholder" maxLength="40" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="newAddress-country" labelKey="formOfflineAddressChange.newAddress.country" path="newAddress.country" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="newAddress-country" path="newAddress.country" placeHolderKey="formOfflineAddressChange.newAddress.country.placeholder" maxLength="40" mandatory="false" inputCSS="${inputCSS} validate-empty" />
				</div>
			</div>
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="newAddress-comment" labelKey="formOfflineAddressChange.newAddress.comment" path="comment" mandatory="false" />
				</div>
				<div class="gu-4">
					<formUtil:formTextarea idKey="newAddress-comment" path="comment" placeHolderKey="formOfflineAddressChange.newAddress.comment.placeholder" mandatory="false" inputCSS="${inputCSS}" />
				</div>
			</div>

			<%-- Captcha --%>
			<div class="row row-captcha base">
				<div class="gu-4">
				</div>
				<div class="gu-4 recaptcha<c:if test="${captchaError}"> error</c:if>">
					<mod:captcha/>
				</div>
			</div>

			<div class="row row-button">
				<div class="gu-4 label-box">
					&nbsp;
				</div>
				<div class="gu-4 field">
					<button type="submit" name="submit" class="btn btn-primary">${sSendAddressChangeButton}<i></i></button>
				</div>
			</div>
		</div>
	</form:form>
</c:if>

<script id="tmpl-form-offline-address-change-validation-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>

<script id="tmpl-form-offline-address-change-validation-error-captcha" type="text/template">
	<spring:message code="validate.error.captcha" />
</script>
