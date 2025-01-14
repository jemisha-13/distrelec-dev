<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<div class="item_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2><spring:theme code="login.title"/></h2>
	</div>

	<div class="item_container">
		<p><spring:theme code="login.description"/></p>
		<p class="required"><spring:theme code="login.required.message"/></p>

		<form:form action="${action}" method="post" modelAttribute="loginForm">
			<c:if test="${not empty message}">
				<span class="errors">
					<spring:theme code="${message}"/>
				</span>
			</c:if>
			<dl>
				<c:if test="${not empty accErrorMsgs}">
					<span class="form_field_error">
				</c:if>

				<formUtil:formInputBox idKey="j_username" labelKey="login.username" path="j_username" placeHolderKey="login.username.placeholder" inputCSS="text" mandatory="true"/>
				<formUtil:formPasswordBox idKey="j_password" labelKey="login.password" path="j_password" placeHolderKey="login.password.placeholder" inputCSS="text password" mandatory="true"/>

				<dd>
					<a href="<c:url value='/login/pw/request'/>" class="password-forgotten"><spring:theme code="login.link.forgottenPwd"/></a>
				</dd>

				<c:if test="${not empty accErrorMsgs}">
					</span>
				</c:if>
			</dl>
			<div id="login"><span style="display: block; clear: both;">
			<ycommerce:testId code="login_Login_button">
				<button type="submit" class="form"><spring:theme code="${actionNameKey}"/></button>
			</ycommerce:testId>
			</span></div>
		</form:form>
	</div>
</div>