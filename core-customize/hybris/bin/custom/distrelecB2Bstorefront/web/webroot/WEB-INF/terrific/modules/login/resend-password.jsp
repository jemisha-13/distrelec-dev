<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<spring:theme code="login.resend-password.send" text="Send" var="sSend" />
<spring:theme code="login.resend-password.intro" text="Please fill out the form below." var="sIntro" />
<spring:theme code="login.resend-password.migrated.into" text="If you do not have an E-Mail as Login, use this form." var="sMigratedIntro" />

<spring:theme code="login.your-email" text="Your E-Mail" var="sYourEmail" />
<spring:theme code="login.your-login" text="Your E-Login" var="sYourLogin" />
<spring:theme code="login.resend-password.infomessage" text="Send" var="sInfoMessage" />

<c:set var="cssErrorClass" value="" />

<%-- STANDARD RESET: email only --%>
<div class="form-box border-top border-bottom base">
	<p class="padding small form-box__text">${sIntro}</p>

	<form:form method="post" action="/login/pw/request" modelAttribute="forgottenPwdForm" cssClass="form-box__form">

		<c:if test="${not empty accErrorMsgs}">
			<c:set var="cssErrorClass" value="error" />
		</c:if>

		<div class="row row-email form-box__form__row">
			<div class="col-sm-12 label-box form-box__form__row__label">
				<label for="forgottenPwd.email">${sYourEmail}</label>
			</div>
			<div class="col-sm-12 form-box__form__row__label">
				<formUtil:formInputBox idKey="forgottenPwd.email" path="email" inputCSS="validate-email js-validate-email" placeHolderKey="forgottenPwd.email.placeholder" mandatory="true"/>
			</div>
		</div>

		<c:if test="${showResetCaptcha}">
			<div class="recaptcha">
				<mod:captcha />
			</div>
		</c:if>

		<div class="row form-box__row">
			<div class="col-sm-12 form-box__row__label">
				<button class="mat-button mat-button--matterhorn js-submit-form" type="button">${sSend}<i></i></button>
			</div>
		</div>
	</form:form>
</div>

<c:if test="${currentCountry.isocode eq 'SE' or currentCountry.isocode eq 'NO' or currentCountry.isocode eq 'FI' }">
	<p class="padding small">${sInfoMessage}</p>
</c:if>

<script id="tmpl-login-validation-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>
