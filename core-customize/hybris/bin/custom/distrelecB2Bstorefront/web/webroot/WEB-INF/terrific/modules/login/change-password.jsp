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

<spring:theme code="login.resend-password.intro" text="Please fill out the form below." var="sIntro" />
<spring:theme code="updatePwd.pwd" text="Password" var="sPassword" />
<spring:theme code="updatePwd.checkPwd" text="Confirm Password" var="sConfirmPassword" />

<spring:theme code="base.passwordStrengthLabel" text="Password Strength" var="sPasswordStrengthLabel" />
<spring:theme code="base.passwordIndicator.Title" text="Info" var="sPasswordStrengthTitle" />
<spring:theme code="base.passwordIndicator.Content" text="Indicates the strenght of your password." var="sPasswordStrengthContent" />


<c:set var="cssErrorClass" value="" />
<c:url value="/login/pw/change" var="encodedUrl" />
<div class="form-box border-top border-bottom base padding-gu">

	<p class="padding small">${sIntro}</p>

	<form:form method="post" action="${encodedUrl}" modelAttribute="updatePwdForm">
		<div class="row">
			<div class="gu-4 label-box">
				<label for="forgottenPwd.email">${sPassword}</label>
			</div>

			<div class="gu-8 pwi-trigger" data-pwi-content="${sPasswordStrengthContent}" data-pwi-title="${sPasswordStrengthTitle}" data-pwi-target="pwi-target" data-pwi-input="pwi-field">
				<formUtil:formPasswordBox idKey="updatePwd-pwd" path="pwd" placeHolderKey="forgottenPwd.pwd.placeholder" inputCSS="field password strength pwi-field" mandatory="true" tabindex="1"/>
			</div>

		</div>
		<div class="row">
			<div class="gu-4 label-box">
				<label for="forgottenPwd.email">${sConfirmPassword}</label>
			</div>
			<div class="gu-8">
				<formUtil:formPasswordBox idKey="updatePwd.checkPwd"  path="checkPwd" placeHolderKey="forgottenPwd.checkPwd.placeholder" inputCSS="text password" mandatory="true" tabindex="2" errorPath="updatePwdForm"/>
			</div>
		</div>

        <form:hidden path="token" id="token"/>
		<div class="row">
			<div class="gu-4 label-box">&nbsp;</div>
			<div class="gu-4">
				<button class="btn btn-primary" type="submit" tabindex="3"><spring:theme code="updatePwd.submit"/><i></i></button>
			</div>
		</div>
	</form:form>
</div>



