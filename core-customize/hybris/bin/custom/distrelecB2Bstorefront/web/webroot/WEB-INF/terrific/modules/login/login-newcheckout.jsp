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
<spring:theme code="login.login" text="Login" var="sLogin" />
<spring:theme code="login.login-existing-text" text="Login to your existing account" var="sLoginExistingText" />
<spring:theme code="newcheckout.forgotPass" text="Forgot password?" var="sResendPassword" />

<c:if test="${not empty accErrorMsgs}">
	<c:set var="inputCSS" value="error" />
</c:if>
<h2 class="section-title">
  <spring:message code="newcheckout.loginTitle" />
</h2>
<div class="form-box form-box--newcheckout border-bottom padding-gu">
	<form:form action="${action}" method="post" class="base" modelAttribute="loginForm" id="loginForm">
		<div class="row">
			<div class="gu-4 label-box">
				<formUtil:formLabel idKey="j_username" labelKey="login.loginfield" path="" mandatory="false"/>
			</div>
			<div class="gu-4">
				<%-- Validation is just standard not-empty because of cr DISTRELEC-2406 migrated users do not have an email address --%>
				<formUtil:formInputBox idKey="j_username" path="j_username" placeHolderKey="login.login.placeholder" inputCSS="${inputCSS} validate-empty" mandatory="true"/>
			</div>
		</div>
		<div class="row">
			<div class="gu-4 label-box">
				<formUtil:formLabel idKey="j_password" labelKey="login.password" path="" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formPasswordBox idKey="j_password" path="j_password" placeHolderKey="login.password.placeholder" inputCSS="${inputCSS} validate-empty" mandatory="true"/>
			</div>
		</div>
		<div class="row row-login-options">
			<div class="gu-2 remember">
				<form:checkbox id="j_remember" class="checkbox-big" path="_spring_security_remember_me" />
				<formUtil:formLabel idKey="j_remember" labelKey="newcheckout.rememberLogin" path="j_remember" mandatory="false"/>
			</div>
		</div>
		<c:if test="${showLoginCaptcha}">
			<div class="row recaptcha">
				<div class="gu-4 field">
					<mod:captcha/>
				</div>
			</div>
		</c:if>
		<div class="row">
			<div class="gu-4">
				<button class="btn btn-primary b-login" type="submit">
          <spring:message code="newcheckout.loginButton" /> <i></i>
        </button>
			</div>
		</div>
    <div class="row">
      <div class="gu-4 links">
				<a class="resend-password" href="/login/pw/request">${sResendPassword}</a>
			</div>
    </div>
	</form:form>

	<script id="tmpl-login-validation-error-empty" type="text/template">
		<spring:message code="validate.error.required" />
	</script>
	<script id="tmpl-login-validation-error-email" type="text/template">
		<spring:message code="validate.error.email" />
	</script>
</div>
