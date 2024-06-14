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

<spring:theme code="login.back-to-login" text="Back to login" var="sBackToLogin" />
<spring:theme code="base.password" text="Password" var="sPassword" />
<spring:theme code="register.checkPwd" text="Confirm password" var="sConfirmPassword" />
<spring:theme code="login.reset-password.title" text="Reset password" var="sResetPassword" />
<spring:theme code="register.pwd.help.text" text="Please enter at least 6 characters." var="sPasswordResetHint" />
<spring:theme code="register.pwd.reset.text" text="Please create a new password" var="sPasswordResetSubtitle" />
<spring:theme code="validation.checkPwd.equals" text="Password and password confirmation do not match" var="sValidationMatchText" />

<section class="resetPassword" data-token="${token}">
	<div class="resetPassword__title">
		<h2 id="resetPassword_reset">${sResetPassword}</h2>
		<p id="resetPasswordSubtitle_reset">${sPasswordResetSubtitle}</p>
	</div>
		<div class="row">
			<label for="password" class="col-12 resetPassword__form__label">${sPassword}</label>
			<div class="col-12 resetPassword__form__field js-password-check">
				<input type="password" name="password" id="password_reset" placeholder="${sPassword}" class=" validate-empty first-password-input" >
				<span class="pwd-reveal">
					<i class="fas fa-eye form-group__pwd-reveal-icon">&nbsp;</i>
					<i class="fas fa-eye-slash form-group__pwd-reveal-icon hidden">&nbsp;</i>
				</span>
				<small>${sPasswordResetHint}</small>
			</div>

		</div>
		<div class="row">
			<label for="confirmPassword" class="col-12 resetPassword__form__label">${sConfirmPassword}</label>
			<div class="col-12 resetPassword__form__field js-second-password-check">
				<input type="password" name="confirmPassword" id="confirmPassword_resetModule" placeholder="${sPassword}" class=" validate-empty second-password-input" >
				<span class="pwd-reveal">
					<i class="fas fa-eye form-group__pwd-reveal-icon">&nbsp;</i>
					<i class="fas fa-eye-slash form-group__pwd-reveal-icon hidden">&nbsp;</i>
				</span>
				<div class="field-msgs hidden" data-reset-password-valid-text="${sValidationMatchText}."></div>
			</div>
		</div>

		<div class="row form-box__row">
			<div class="col-sm-12 form-box__row__label">
				<button id="submitResetPassword" class="ux-btn ux-btn--brand-yellow js-reset-password-submit">${sResetPassword}<i></i></button>
			</div>
		</div>

		<div class="row">
			<div class="col-12 resetPassword__form__backToLogin text-center js-back-to-login-reset-password-form">
				<span><strong>${sBackToLogin}</strong></span>
			</div>
		</div>

	<script id="tmpl-login-validation-error-empty" type="text/template">
		<spring:message code="login.error.password.message" />
	</script>

	<script id="tmpl-login-validation-error-email" type="text/template">
		<spring:message code="login.error.email.message" />
	</script>
</section>
