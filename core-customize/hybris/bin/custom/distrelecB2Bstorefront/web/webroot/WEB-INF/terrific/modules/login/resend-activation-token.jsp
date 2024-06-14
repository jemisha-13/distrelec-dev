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

<spring:theme code="login.your-email" text="Your E-Mail" var="sYourEmail" />

<c:set var="action" value="/j_spring_security_check" />

<div class="form-box border-top border-bottom base padding-gu">
	
	<p class="padding small"><spring:theme code="resendAccountActivationToken.description"/></p>

	<form:form method="post" modelAttribute="resendAccountActivationTokenForm">

		<div class="row row-email">
			<div class="gu-4 label-box">
				<label for="resendAccountActivationToken.email">${sYourEmail}</label>
			</div>
			<div class="gu-8">
				<formUtil:formInputBox idKey="resendAccountActivationToken.email" path="email" placeHolderKey="resendAccountActivationToken.email.placeholder" inputCSS="field" mandatory="true"/>
			</div>
		</div>
		
		<c:if test="${showResendTokenCaptcha}">
			<div class="row recaptcha">
				<div class="gu-4 label-box"></div>
				<div class="gu-8 field">
					<mod:captcha/>
				</div>
			</div>
		</c:if>

		<div class="row">
			<div class="gu-4 label-box">&nbsp;</div>
			<div class="gu-4">
				<button class="btn btn-primary"><spring:theme code="resendAccountActivationToken.submit"/><i></i></button>
			</div>
		</div>
	</form:form>
</div>