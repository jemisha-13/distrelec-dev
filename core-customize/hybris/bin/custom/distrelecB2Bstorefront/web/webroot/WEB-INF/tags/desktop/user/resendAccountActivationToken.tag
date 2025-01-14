<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<div class="span-4 side-content-slot cms_banner_slot">
	<cms:slot var="feature" contentSlot="${slots['SideContent']}">
		<cms:component component="${feature}"/>
	</cms:slot>
</div>

<div class="span-20 last">
	<div class="item_container_holder">
		<div class="title_holder">
			<div class="title">
				<div class="title-top">
					<span></span>
				</div>
			</div>
			<h2><spring:theme code="resendAccountActivationToken.title"/></h2>
		</div>

		<div class="item_container">
			<p><spring:theme code="resendAccountActivationToken.description"/></p>
			<p class="required"><spring:theme code="form.required"/></p>
			<form:form method="post" modelAttribute="resendAccountActivationTokenForm">
				<dl>
					<formUtil:formInputBox idKey="resendAccountActivationToken.email" labelKey="resendAccountActivationToken.email" path="email" placeHolderKey="resendAccountActivationToken.email.placeholder" inputCSS="text" mandatory="true"/>
				</dl>
				<span style="display: block; clear: both;">
					<button class="form"><spring:theme code="resendAccountActivationToken.submit"/></button>
				</span>
			</form:form>
		</div>
	</div>
</div>