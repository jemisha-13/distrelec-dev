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
<spring:theme code="login.reset-password.subtext" text="Enter the email-address for your Distrelec account." var="sResetPasswordSubtext" />
<spring:theme code="login.back-to-login" text="Back to login" var="sBackToLogin" />
<spring:theme code="support.send" text="Send" var="sSend" />
<spring:theme code="user.email" text="Email" var="sEmail" />
<spring:theme code="newcheckout.email.placeholder" text="Enter email" var="sEnterEmail" />
<spring:theme code="account.confirmation.forgotten.password.link.expired" text="Your password reset link has expired. Please request another one below." var="sTokenExpired" />
<c:if test="${not empty accErrorMsgs}">
	<c:set var="inputCSS" value="error" />
</c:if>

<section class="forgottenPassword" id="forgottenPassword">
	<form:form method="post" action="/login/checkout/pw/request/async" id="forgottenPasswordForm" modelAttribute="forgottenPwdForm">
		<div class="login__card__title">
			<h2 id="resetPassword_forgottenPassword">${sResetPasswordTitle}</h2>
			<p id="resetPasswordSubtitle_forgottenPassword">${sResetPasswordSubtext}</p>
			<c:if test="${isTokenInvalid}">
				<p class="forgottenPassword__title-error-message js-forgotten-pass-error-msg">${sTokenExpired}</p>
			</c:if>
		</div>
		<div class="row js-form-group">
			<div class="col-12 forgottenPassword__form__label">
				<label>${sEmail}</label>
			</div>
			<div class="col-12 forgottenPassword__form__field js-forgotten-form-field">
				<formUtil:formInputBox maxLength="255" idKey="forgottenPwd.email" path="email" inputCSS="js-forgotten-password-email validate-email" placeHolderKey="forgottenPwd.email.placeholder" mandatory="true"/>
			</div>
		</div>
		<div class="row form-box__row">
			<div class="col-sm-12 form-box__row__label">
				<button id="submitForgottenPassword" class="ux-btn ux-btn--brand-yellow js-forgotten-password-submit">${sSend}<i></i></button>
			</div>
		</div>
		<div class="row">
			<div class="col-12 forgottenPassword__form__backToLogin text-center js-back-to-login-forgotten-form">
				<span><strong id="backToLoginLink">${sBackToLogin}</strong></span>
			</div>
		</div>
		<%-- TODO: add flag from BE to show captcha only after 3 submits --%>
		<div class="recaptcha">
			<mod:captcha />
		</div>
	</form:form>
</section>