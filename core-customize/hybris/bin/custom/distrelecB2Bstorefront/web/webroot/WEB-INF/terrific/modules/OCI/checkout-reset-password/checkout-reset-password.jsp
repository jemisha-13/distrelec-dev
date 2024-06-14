<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<c:set var="action" value="/checkout/j_spring_security_check" />
<spring:theme code="login.reset-password.title" text="Reset password" var="sResetPasswordTitle" />
<spring:theme code="login.reset-password.subtext" text="Enter the email-address for your Distrelec account" var="sResetPasswordSubtext" />
<spring:theme code="login.back-to-login" text="Back to login" var="sBackToLogin" />
<spring:theme code="support.send" text="Send" var="sSend" />
<spring:theme code="user.email" text="Email" var="sEmail" />
<spring:theme code="newcheckout.email.placeholder" text="Enter email" var="sEnterEmail" />
<c:if test="${not empty accErrorMsgs}">
	<c:set var="inputCSS" value="error" />
</c:if>

<section class="resetPassword">
	<div class="resetPassword__title">
		<h2 id="resetPassword">Reset Password</h2>
		<p id="resetPasswordSubtitle">Please create a new password.</p>
	</div>
	<form:form action="${action}" method="post" class="login__form" modelAttribute="loginForm" id="loginForm">
		<div class="row">
			<div class="col-12 resetPassword__form__label">Password</div>
			<div class="col-12 resetPassword__form__field js-password-check">
				<formUtil:formPasswordBox idKey="j_password" path="j_password" placeHolderKey="base.password" inputCSS="${inputCSS} validate-empty" mandatory="true"/>
				<span class="pwd-reveal">
					<i class="fas fa-eye form-group__pwd-reveal-icon">&nbsp;</i>
					<i class="fas fa-eye-slash form-group__pwd-reveal-icon hidden">&nbsp;</i>
				</span>
				<small>Please enter at least 6 characters.</small>
			</div>

		</div>
		<div class="row">
			<div class="col-12 resetPassword__form__label">Confirm Password</div>
			<div class="col-12 resetPassword__form__field js-second-password-check">
				<formUtil:formPasswordBox idKey="j_password" path="j_password" placeHolderKey="base.password" inputCSS="${inputCSS} validate-empty second-password-input" mandatory="true"/>
				<span class="pwd-reveal">
					<i class="fas fa-eye form-group__pwd-reveal-icon">&nbsp;</i>
					<i class="fas fa-eye-slash form-group__pwd-reveal-icon hidden">&nbsp;</i>
				</span>
			</div>
		</div>

		<div class="row form-box__row">
			<div class="col-sm-12 form-box__row__label">
				<button id="submitForgottenPassword" class="ux-btn ux-btn--brand-yellow js-forgotten-password-submit">Reset Password<i></i></button>
			</div>
		</div>

		<div class="row">
			<div class="col-12 resetPassword__form__backToLogin text-center js-back-to-login-forgotten-form">
				<span><strong>${sBackToLogin}</strong></span>
			</div>
		</div>
	</form:form>

	<script id="tmpl-login-validation-error-empty" type="text/template">
		<spring:message code="login.error.password.message" />
	</script>

	<script id="tmpl-login-validation-error-email" type="text/template">
		<spring:message code="login.error.email.message" />
	</script>
</section>
