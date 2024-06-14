<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<c:set var="action" value="/j_spring_security_check" />

<spring:theme code="login.login" text="Login" var="sLogin" />
<spring:theme code="login.your-login" text="Login" var="mLogin" />

<spring:theme code="login.login-existing-text" text="Login to your existing account" var="sLoginExistingText" />
<spring:theme code="login.email-adress" text="E-Mail Address" var="sEmailAddress" />
<spring:theme code="login.password" text="Password" var="sPassword" />
<spring:theme code="login.resend-password" text="Resend Password" var="sResendPassword" />
<spring:theme code="login.register" text="Resend Password" var="sRegister" />
<spring:theme code="login.remember-login" text="Remember Login" var="sRememberLogin" />

<c:if test="${not empty accErrorMsgs}">
	<c:set var="inputCSS" value="error" />
</c:if>

<div class="form-box border-top border-bottom base">
    <form:form action="${action}" method="post" id="loginForm" modelAttribute="loginForm" cssClass="form-box__form">
        <div class="row form-box__form__row">
            <div class="col-sm-12 label-box form-box__form__row__label">
                <formUtil:formLabel idKey="j_username" labelKey="login.your-login" path="j_username" mandatory="false"/>
            </div>
            <div class="col-sm-12 form-box__form__row__label">
                    <%-- Validation is just standard not-empty because of cr DISTRELEC-2406 migrated users do not have an email address --%>
                <formUtil:formInputBox idKey="j_username" path="j_username" placeHolderKey="login.login.placeholder" inputCSS="${inputCSS} validate-empty js-username" mandatory="true" tabindex="1"/>
            </div>
        </div>
        <div class="row form-box__form__row">
            <div class="gu-2--${currentCountry.isocode} label-box form-box__form__row__label">
                <formUtil:formLabel idKey="j_password" labelKey="login.password" path="j_password" mandatory="false"/>
            </div>
            <div class="col-sm-12 form-box__form__row__label">
                <formUtil:formPasswordBox idKey="j_password" path="j_password" placeHolderKey="login.password.placeholder" inputCSS="${inputCSS} validate-empty js-password" mandatory="true" tabindex="2"/>
            </div>
        </div>

        <div class="row row-login-options form-box__form__row">
            <div class="remember form-box__form__row__label">
                <form:checkbox id="j_remember" class="checkbox-big js-remember" path="_spring_security_remember_me" tabindex="3" />
                <formUtil:formLabel idKey="j_remember" labelKey="newcheckout.rememberLogin" path="j_remember" mandatory="false"/>
            </div>
        </div>

        <c:if test="${showLoginCaptcha}">
            <div class="recaptcha">
                <mod:captcha/>
            </div>
        </c:if>

        <div class="row form-box__form__row">
            <div class="col-sm-12 form-box__form__row__label">
                <button class="mat-button mat-button--matterhorn b-login js-login-button" data-aainteraction="login button" type="submit" tabindex="5">
                    <spring:message code="newcheckout.loginButton" /> <i class="fa fa-chevron-right" aria-hidden="true"></i>
                </button>
            </div>
        </div>
        <div class="row form-box__form__row">
            <div class="col-sm-12 form-box__form__row__label">
                <div class="links">
                    <a class="resend-password" href="/login/pw/request"><spring:message code="newcheckout.forgotPass" /></a>
                    <a class="register" href="/registration"><spring:message code="newcheckout.registerTitle" /></a>
                </div>
            </div>
        </div>
    </form:form>
</div>

	<script id="tmpl-login-validation-error-empty" type="text/template">
		<spring:message code="validate.error.required" />
	</script>

	<script id="tmpl-login-validation-error-email" type="text/template">
		<spring:message code="validate.error.email" />
	</script>

	<script id="tmpl-login-validation-error-captcha" type="text/template">
		<spring:message code="validate.error.captcha" />
	</script>
