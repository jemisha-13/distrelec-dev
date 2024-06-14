<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<c:set var="action" value="/checkout/j_spring_security_check" />

<spring:theme code="login.reset-password.title" text="Reset password" var="sResetPasswordTitle" />
<spring:theme code="login.back-to-login" text="Back to login" var="sBackToLogin" />
<spring:theme code="login.reset-password-success.message" text="If we can find your email address, you will receive an email within a few minutes. Please click the link in the email to continue." var="sResetPasswordSuccessMessage" />
<spring:theme code="login.reset-password-success.list.title" text="Didn't receive a reset email?" var="sResetPasswordSuccessListTitle" />
<spring:theme code="login.reset-password-success.list.1" text="1. Check your spam folder." var="sResetPasswordSuccessList1" />
<spring:theme code="login.reset-password-success.list.2" text="2. Check your spelling." var="sResetPasswordSuccessList2" />
<spring:theme code="login.reset-password-success.list.3" text="3. Wait 5 minutes and try again." var="sResetPasswordSuccessList3" />

<section class="forgottenPasswordSuccess">
	<div class="login__card__title">
		<h2 id="resetPassword_forgottenPasswordSuccess">${sResetPasswordTitle}</h2>
	</div>
	<div class="forgottenPasswordSuccess__subtext">
		<div class="col-12 forgottenPasswordSuccess__successMessage">${sResetPasswordSuccessMessage}</div>
		<div class="forgottenPasswordSuccess__instructions">
			${sResetPasswordSuccessListTitle}
		</div>
		<div class="forgottenPasswordSuccess__instructions__list">
			<ol>
				<li>${sResetPasswordSuccessList1}</li>
				<li>${sResetPasswordSuccessList2}</li>
				<li>${sResetPasswordSuccessList3}</li>
			</ol>
		</div>
		<div class="col-12 forgottenPasswordSuccess__backToLogin text-center js-back-to-login-forgotten-form-success">
			<span><strong>${sBackToLogin}</strong></span>
		</div>
	</div>
</section>

