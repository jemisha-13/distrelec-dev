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
<c:set var="action" value="/catalog-order" />

<spring:theme code="formCatalogOrder.success" var="sRequestSuccess" />
<spring:theme code="formCatalogOrder.failed" var="sRequestFailed" />
<spring:theme code="formCatalogOrder.buttonText" var="sCatalogOrderButton" />
<spring:theme code="formCatalogOrder.intro" var="sCatalogOrderIntro" />

<c:if test="${not empty status}">
	<c:if test="${status}"><p class="status status-success padding">${sRequestSuccess}</p></c:if>
	<c:if test="${!status}"><p class="status status-failed padding">${sRequestFailed}</p></c:if>
</c:if>


<form:form action="${action}" method="post" modelAttribute="catalogOrderForm">
	<div class="catalog-order-box border-bottom base padding">
		<!-- div class="row row-checkbox">
			<div class="gu-4">&nbsp;</div>
		</div -->
		<div class="row">
			&nbsp;			
		</div>
		
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-company-name" labelKey="formCatalogOrder.companyName" path="companyName" mandatory="false" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-company-name" path="companyName" placeHolderKey="formCatalogOrder.companyName.placeholder" maxLength="35" mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-company-name-2" labelKey="formCatalogOrder.companyName2" path="companyName2" mandatory="false" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-company-name-2" path="companyName2" placeHolderKey="formCatalogOrder.companyName2.placeholder" maxLength="35" mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-salutation" labelKey="formCatalogOrder.salutation" path="salutation" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formSelectBox idKey="catalog-order-salutation" path="salutation" mandatory="false" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${titles}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-first-name" labelKey="formCatalogOrder.firstName" path="firstName" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-first-name" path="firstName" placeHolderKey="formCatalogOrder.firstName.placeholder" maxLength="35" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-last-name" labelKey="formCatalogOrder.lastName" path="lastName" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-last-name" path="lastName" placeHolderKey="formCatalogOrder.lastName.placeholder" maxLength="35" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-department" labelKey="formCatalogOrder.department" path="department" mandatory="false" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-department" path="department" placeHolderKey="formCatalogOrder.department.placeholder" maxLength="35"  mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-street" labelKey="formCatalogOrder.street" path="street" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-street" path="street" placeHolderKey="formCatalogOrder.street.placeholder" maxLength="60" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-number" labelKey="formCatalogOrder.number" path="number" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-number" path="number" placeHolderKey="formCatalogOrder.number.placeholder" maxLength="10" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-zip" labelKey="formCatalogOrder.zip" path="zip" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-zip" path="zip" placeHolderKey="formCatalogOrder.zip.placeholder" maxLength="10" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-place" labelKey="formCatalogOrder.place" path="place" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-place" path="place" placeHolderKey="formCatalogOrder.place.placeholder" maxLength="40" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-country" labelKey="formCatalogOrder.country" path="country" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formSelectBox idKey="catalog-order-country" path="country" mandatory="true" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${countries}" itemValue="isocode" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-direct-phone" labelKey="formCatalogOrder.directPhone" path="directPhone" mandatory="false" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-direct-phone" path="directPhone" placeHolderKey="formCatalogOrder.directPhone.placeholder" maxLength="30" mandatory="true" inputCSS="${inputCSS}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-mobile" labelKey="formCatalogOrder.mobile" path="mobile" mandatory="false" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-mobile" path="mobile" placeHolderKey="formCatalogOrder.mobile.placeholder" maxLength="30" mandatory="true" inputCSS="${inputCSS}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-fax" labelKey="formCatalogOrder.fax" path="fax" mandatory="false" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-fax" path="fax" placeHolderKey="formCatalogOrder.fax.placeholder" maxLength="30" mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-email" labelKey="formCatalogOrder.email" path="eMail" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="catalog-order-email" path="eMail" placeHolderKey="formCatalogOrder.email.placeholder" maxLength="241" mandatory="true" inputCSS="${inputCSS} validate-email" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="catalog-order-comment" labelKey="formCatalogOrder.comment" path="comment" mandatory="false" />
			</div>
			<div class="gu-4">
				<formUtil:formTextarea idKey="catalog-order-comment" path="comment" placeHolderKey="formCatalogOrder.comment.placeholder"  mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>

		<div class="row row-captcha base">
			<div class="gu-4">
			</div>
			<div class="gu-4 recaptcha">
				<mod:captcha/>
			</div>
		</div>

		<div class="row row-button">
			<div class="gu-4 label-box">
				&nbsp;
			</div>
			<div class="gu-4 field">
				<button type="submit" name="submit" class="btn btn-primary">${sCatalogOrderButton}<i></i></button>
			</div>
		</div>
	</div>
</form:form>

<script id="tmpl-form-catalog-order-validation-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>
<script id="tmpl-form-catalog-order-validation-error-dropdown" type="text/template">
	<spring:message code="validate.error.dropdown" />
</script>
<script id="tmpl-form-catalog-order-validation-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>
<script id="tmpl-form-catalog-order-validation-error-captcha" type="text/template">
	<spring:message code="validate.error.captcha" />
</script>
<script id="tmpl-form-catalog-order-validation-error-checkboxgroup" type="text/template">
	<spring:message code="validate.error.checkboxgroup" />
</script>