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
<c:set var="action" value="/info-center/seminar/register" />

<spring:theme code="formSeminarSignup.title" var="sSeminarSignupTitle" />
<spring:theme code="formSeminarSignup.intro" var="sSeminarSignipIntro" />
<spring:theme code="formSeminarSignup.success" var="sRequestSuccess" />
<spring:theme code="formSeminarSignup.failed" var="sRequestFailed" />
<spring:theme code="formSeminarSignup.buttonText" var="sSeminarSignupButton" />
<spring:message code="text.store.dateformat.datepicker.selection" var="sDateFormat" />

<h1 class="base page-title">${sSeminarSignupTitle}</h1>

<c:if test="${not empty status}">
	<c:if test="${status}"><p class="status status-success padding">${sRequestSuccess}</p></c:if>
	<c:if test="${!status}"><p class="status status-failed padding">${sRequestFailed}</p></c:if>
</c:if>


<form:form action="${action}" method="post" modelAttribute="seminarRegistrationForm">
	<div class="seminar-signup-box border-top border-bottom base padding">
		<h3 class="base">${sSeminarSignipIntro}</h3>
		<form:hidden path="seminar"  />
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-topic" labelKey="formSeminarSignup.topic" path="topic" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-topic" path="topic" placeHolderKey="formSeminarSignup.topic.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>		
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-date" labelKey="formSeminarSignup.date"  path="date" mandatory="true"/>
				(${datePattern})
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-date" path="date" placeHolderKey="formSeminarSignup.date.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-company-name" labelKey="formSeminarSignup.companyName" path="companyName" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-company-name" path="companyName" placeHolderKey="formSeminarSignup.companyName.placeholder" mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-customer-number" labelKey="formSeminarSignup.customerNumber" path="customerNumber" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-customer-number" path="customerNumber" placeHolderKey="formSeminarSignup.customerNumber.placeholder" mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>		
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-salutation" labelKey="formSeminarSignup.salutation" path="salutation" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formSelectBox idKey="seminar-signup-salutation" path="salutation" mandatory="false" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${titles}" />
			</div>
		</div>		
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-first-name" labelKey="formSeminarSignup.firstName" path="firstName" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-first-name" path="firstName" placeHolderKey="formSeminarSignup.firstName.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-last-name" labelKey="formSeminarSignup.lastName" path="lastName" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-last-name" path="lastName" placeHolderKey="formSeminarSignup.lastName.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-department" labelKey="formSeminarSignup.department" path="department" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-department" path="department" placeHolderKey="formSeminarSignup.department.placeholder" mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-street" labelKey="formSeminarSignup.street" path="street" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-street" path="street" placeHolderKey="formSeminarSignup.street.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-number" labelKey="formSeminarSignup.number" path="number" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-number" path="number" placeHolderKey="formSeminarSignup.number.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<c:if test="${currentCountry.isocode eq 'CH'}">
			<div class="row">
				<div class="gu-4">
					<formUtil:formLabel idKey="seminar-signup-pobox" labelKey="formSeminarSignup.pobox" path="pobox" mandatory="false"/>
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="seminar-signup-pobox" path="pobox" placeHolderKey="formSeminarSignup.pobox.placeholder" mandatory="false" inputCSS="${inputCSS}" />
				</div>
			</div>
		</c:if>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-zip" labelKey="formSeminarSignup.zip" path="zip" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-zip" path="zip" placeHolderKey="formSeminarSignup.zip.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-place" labelKey="formSeminarSignup.place" path="place" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-place" path="place" placeHolderKey="formSeminarSignup.place.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-country" labelKey="formSeminarSignup.country" path="country" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formSelectBox idKey="seminar-signup-country" path="country" mandatory="true" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${countries}" itemValue="isocode"/>
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-direct-phone" labelKey="formSeminarSignup.directPhone" path="directPhone" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-direc-phone" path="directPhone" placeHolderKey="formSeminarSignup.directPhone.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-email" labelKey="formSeminarSignup.email" path="eMail" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-email" path="eMail" placeHolderKey="formSeminarSignup.email.placeholder" mandatory="true" inputCSS="${inputCSS} validate-email" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-fax" labelKey="formSeminarSignup.fax" path="fax" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="seminar-signup-fax" path="fax" placeHolderKey="formSeminarSignup.fax.placeholder" mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="seminar-signup-comment" labelKey="formSeminarSignup.comment" path="comment" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formTextarea idKey="seminar-signup-comment" path="comment" placeHolderKey="formSeminarSignup.comment.placeholder" mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>

		<div class="row row-button">
			<div class="gu-4 label-box">
				&nbsp;
			</div>
			<div class="gu-4 field">
				<button type="submit" name="submit" class="btn btn-primary">${sSeminarSignupButton}<i></i></button>
			</div>
		</div>
	</div>
</form:form>

<script id="tmpl-form-seminar-signup-validation-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>
<script id="tmpl-form-seminar-signup-validation-error-dropdown" type="text/template">
	<spring:message code="validate.error.dropdown" />
</script>
<script id="tmpl-form-seminar-signup-validation-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>