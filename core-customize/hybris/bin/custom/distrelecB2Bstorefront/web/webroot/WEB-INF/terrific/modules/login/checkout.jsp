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

<spring:theme code="login.returning.customer.title" text="Returning Customer" var="sLoginReturningText" />
<spring:theme code="login.signin.create.title" text="Sign in or create an account..." var="sLoginSigninCreateText" />
<spring:theme code="login.login-subtitle" text="Welcome back! Login for faster checkout." var="sLoginSubtitle" />
<spring:theme code="text.account.confirmation.password.updated" text="Your password has been changed" var="sRestPwdSuccess" />

<c:if test="${not empty accErrorMsgs}">
	<c:set var="inputCSS" value="error" />
</c:if>

<section>
	<div class="login__title login__card__title">
		<h2 id="returningCustomerTitle">
			${sLoginReturningText}
		</h2>
		<p id="returningCustomerSubtitle">${sLoginSubtitle}</p>
		<c:if test="${not empty accErrorMsgs}">
			<div class="login-addl-error js-login-addl-error">
				<div class="login-addl-error__icon">
					<i class="fa fa-info-circle"></i>
				</div>

				<p id="addlErrorMessage" class="login-addl-error__text">
					<spring:message code="login.checkout.addl.error.forgot.password" />
				</p>
			</div>
		</c:if>
		<p class="login__title-success-message message hidden">${sRestPwdSuccess}.</p>
	</div>
	<form:form action="${action}" method="post" class="login__form" modelAttribute="loginForm" id="loginForm">
		<div class="row login__card__form-group js-form-group">
			<div class="col-12 login__form__label">
				<formUtil:formLabel labelId="returning.customer.email.label" idKey="j_username" labelKey="newcheckout.email.label" path="" mandatory="false"/>
			</div>
			<div class="col-12 login__form__field email js-login-form-field js-email">
				<formUtil:formInputBox idKey="j_username" path="j_username" inputCSS="${inputCSS} validate-empty-trim js-validate-username" mandatory="true"/>
			</div>
		</div>
		<div class="row login__card__form-group js-form-group">
			<div class="col-12 login__form__label">
				<formUtil:formLabel labelId="guest.customer.email.label" idKey="j_password" labelKey="newcheckout.password.label" path="" mandatory="false"/>
			</div>
			<div class="col-12 login__form__field password js-login-form-field js-password">
				<formUtil:formPasswordBox idKey="j_password" path="j_password" inputCSS="${inputCSS} validate-empty-trim js-validate-password" mandatory="true"/>
			</div>
		</div>

		<div class="row row--options">
			<div class="col-6 login__form__remember">
				<formUtil:ux-formCheckbox idKey="j_remember"
										  inputCSS="js-remember-me"
										  value="true"
										  name="_spring_security_remember_me"
										  labelKey="newcheckout.login.remember-login"/>
			</div>
		</div>
		
		<%-- ${showLoginCaptcha} is TRUE after error is shown 3 times --%>
		<c:if test="${showLoginCaptcha}">
			<div class="row recaptcha">
				<mod:captcha callback="onCaptchaSubmitLoginForm" htmlClasses="js-checkoutLoginForm"/>
			</div>
		</c:if>

		<div class="row row--submit js-row-submit">
			<div class="col-12">
				<button id="continueToCheckoutButtonReturningCustomer" data-aainteraction="login button" class="ux-btn ux-btn--brand-green js-login-continue" type="submit"><spring:theme code="login.checkout.continue.to.checkout.button"/><i></i></button>
			</div>
		</div>

		<div class="row">
			<div class="col-12 login__form__forgotten ux-link text-center js-to-forgotten-form">
				<span id="forgottenPasswordLink"><spring:theme code="login.checkout.forgot.password.link"/></span>
			</div>
		</div>
	</form:form>

	<script id="tmpl-login-validation-error-empty" type="text/template">
		<spring:message code="login.error.password.message" />
	</script>

	<script id="tmpl-login-validation-error-email" type="text/template">
		<spring:message code="login.error.email.message" />
	</script>

	<script id="tmpl-login-validation-error-password" type="text/template">
		<spring:message code="login.error.password.message" />
	</script>

	<script id="tmpl-login-validation-error-username" type="text/template">
		<spring:message code="login.error.email.message" />
	</script>
</section>
