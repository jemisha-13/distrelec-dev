<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="idKey" required="true" type="java.lang.String" %>
<%@ attribute name="labelKey" required="true" type="java.lang.String" %>
<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean" %>
<%@ attribute name="labelCSS" required="false" type="java.lang.String" %>
<%@ attribute name="inputCSS" required="false" type="java.lang.String" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ attribute name="typeIdentifier" required="false" type="java.lang.String" %>
<%@ attribute name="items" required="true" type="java.util.Collection" %>
<%@ attribute name="itemValue" required="false" type="java.lang.String" %>
<%@ attribute name="itemLabel" required="false" type="java.lang.String" %>

<template:errorSpanField path="${path}">
	<spring:theme code="${idKey}" var="themeIdKey"/>
	<dt class="${labelCSS}">
		<label class="${labelCSS}" for="${themeIdKey}">
			<spring:theme code="${labelKey}"/>
			<c:if test="${mandatory != null && mandatory == true}">
				<span class="mandatory">
					<spring:theme code="login.required" var="loginrequiredText"/>
					<img width="5" height="6" alt="${loginrequiredText}" title="${loginrequiredText}"
						 src="<c:url value="/_ui/common/images/mandatory.gif"/>">
				</span>
			</c:if>
			<span class="skip"><form:errors path="${path}"/></span>
		</label>
	</dt>
	<dd>
		<form:checkboxes cssClass="${inputCSS}" id="${themeIdKey}" path="${path}" items="${items}"
						 itemValue="${not empty itemValue ? itemValue :'code'}"
						 itemLabel="${not empty itemLabel ? itemLabel :'name'}"/>

	</dd>
</template:errorSpanField>
