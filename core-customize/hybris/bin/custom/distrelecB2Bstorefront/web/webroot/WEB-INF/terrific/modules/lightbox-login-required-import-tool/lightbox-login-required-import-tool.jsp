<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<spring:message code="metahd.account.email" text="Settings" var="sEmailPlaceholder" />
<spring:message code="base.password" text="Password" var="sPassword" />
<spring:message code="lightboxloginrequiredImportTool.buttonLabel" text="Login & continue" var="sLoginContinueBtn" />
<spring:message code="lightboxloginrequiredImportTool.buttonContinue" text="Continue without logging in" var="sContinueBtn" />
<spring:message code="lightboxloginrequiredImportTool.or" text="Or" var="sOr" />
<spring:message code="login.login" text="Modal title" var="sModalTitle" />

<div class="modal-backdrop hidden">&nbsp;</div>
<div class="import-tool-modal modal base" tabindex="-1">
	<div class="hd">
		<div class="hd__left">
			<h3 title="${sModalTitle}" class="title">${sModalTitle}</h3>
		</div>
		<div class="hd__right">
			<a title="<spring:message code="lightboxloginrequired.close" />" class="btn btn-close octagon" href="#" data-dismiss="modal" aria-hidden="true">
				<div class="octagon__inner">
					<i class="fa fa-times" aria-hidden="true"></i>
				</div>
			</a>
		</div>
	</div>
	<c:url value="/j_spring_security_check" var="loginAction" />
	<form:form id="metaLoginForm_import_tool" action="${loginAction}" method="post" modelAttribute="metaLoginForm">
		<div class="bd">
			<div class="error-box">
				<h4><spring:message code="lightboxloginrequired.error" />!</h4>
				<p><spring:message code="lightboxloginrequired.message.error" /></p>
			</div>
			<div class="form-row">
				<formUtil:formLabel idKey="j_username" labelKey="lightboxloginrequired.login" path="" mandatory="false"/>
				<%-- Validation is just standard not-empty because of cr DISTRELEC-2406 migrated users do not have an email address --%>
				<div class="field"><input name="j_username" id="j_username_import_tool" placeholder="${sEmailPlaceholder}" class="fields validate-empty" tabindex="1" type="text" value=""></div>
			</div>
			<div class="form-row password">
				<formUtil:formLabel idKey="j_password" labelKey="lightboxloginrequired.password" path="" mandatory="false"/>
				<div class="field"><input name="j_password" id="j_password_import_tool" placeholder="${sPassword}" class="fields validate-empty" tabindex="2" type="password" value=""></div>
			</div>
		</div>
		<div class="ft">
			<div class="left">
				<a class="link" href="/login/pw/request"><spring:message code="lightboxloginrequired.forgot.password" />?</a>
				<div class="remember-btn-holder">
					<form:checkbox id="j_remember" class="checkbox-big" path="_spring_security_remember_me" tabindex="3" />
					<formUtil:formLabel idKey="j_remember" labelKey="login.remember-login" path="j_remember" mandatory="false"/>
				</div>
			</div>
			<input type="submit" class="mat-button mat-button--action-red" data-aainteraction="login button" value="${sLoginContinueBtn}" tabindex="4" />
		</div>
	</form:form>
</div>
<script id="tmpl-lightbox-login-validation-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>
<script id="tmpl-lightbox-login-validation-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>
