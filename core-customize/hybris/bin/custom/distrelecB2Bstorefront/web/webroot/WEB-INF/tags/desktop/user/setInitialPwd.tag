<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
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
			<h2><spring:theme code="setInitialPwd.title"/></h2>
		</div>

		<div class="item_container">
			<p><spring:theme code="setInitialPwd.description"/></p>
			<p class="required"><spring:theme code="form.required"/></p>
			<form:form method="post" modelAttribute="setInitialPwdForm">
				<dl>
					<formUtil:formPasswordBox idKey="setInitialPwd-pwd" labelKey="setInitialPwd.pwd" path="pwd" placeHolderKey="setInitialPwd.pwd.placeholder" inputCSS="text password strength" mandatory="true"/>
					<formUtil:formPasswordBox idKey="setInitialPwd.checkPwd" labelKey="setInitialPwd.checkPwd" path="checkPwd" placeHolderKey="setInitialPwd.checkPwd.placeholder" inputCSS="text password" mandatory="true" errorPath="setInitialPwdForm"/>
				</dl>
				<span style="display: block; clear: both;">
					<button class="form"><spring:theme code="setInitialPwd.submit"/></button>
				</span>
			</form:form>
		</div>
	</div>
</div>
