<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/login-data/send" />

<spring:theme code="base.passwordIndicator.Title" text="Info" var="sChangePasswordMoreInfo" />
<spring:theme code="base.passwordIndicator.Content" text="Indicates the strenght of your password." var="sPasswordIndicatorContent" />

<div class="form-box">
	<form:form action="update-password" modelAttribute="updatePasswordForm" method="post" autocomplete="off">
		<h2 class="form-title"><spring:theme code="logindata.changePassword" /></h2>
		<div class="row base">
			<div class="gu-4">
				<formUtil:formLabel idKey="logindata.changePassword.currentPassword" labelKey="logindata.changePassword.currentPassword" path="currentPassword" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formPasswordBox idKey="logindata.changePassword.currentPassword" path="currentPassword" placeHolderKey="logindata.changePassword.currentPassword.placeholder" />
			</div>
		</div>
		<div class="row base">
			<div class="gu-4">
				<formUtil:formLabel idKey="logindata.changePassword.newPassword" labelKey="logindata.changePassword.newPassword" path="newPassword" mandatory="true" />
			</div>
			<div class="gu-4 pwi-trigger" data-pwi-content="${sPasswordIndicatorContent}" data-pwi-title="${sChangePasswordMoreInfo}" data-pwi-target="pwi-target" data-pwi-input="pwi-field">
				<formUtil:formPasswordBox idKey="logindata.changePassword.newPassword" path="newPassword" placeHolderKey="logindata.changePassword.newPassword.placeholder" inputCSS="pwi-field" />
				<div class="field-msgs">
					<spring:theme code="logindata.changePassword.passwordStrength" />: <span class="pwi-target"></span>
				</div>
			</div>
		</div>
		<div class="row base">
			<div class="gu-4">
				<formUtil:formLabel idKey="logindata.changePassword.confirmNewPassword" labelKey="logindata.changePassword.confirmNewPassword" path="checkNewPassword" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formPasswordBox idKey="logindata.changePassword.confirmNewPassword" path="checkNewPassword" placeHolderKey="logindata.changePassword.confirmNewPassword.placeholder" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4 label-box">&nbsp;</div>
			<div class="gu-4 field">
				<button class="btn btn-primary btn-change" type="submit"><i></i><spring:theme code="logindata.buttonChange"/></button>
			</div>
		</div>
	</form:form>
</div>