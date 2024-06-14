<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="idKey" required="true" type="java.lang.String" %>
<%--<%@ attribute name="labelKey" required="true" type="java.lang.String" %>--%>
<%@ attribute name="placeHolderKey" required="false" type="java.lang.String" %>
<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean" %>
<%@ attribute name="labelCSS" required="false" type="java.lang.String" %>
<%@ attribute name="inputCSS" required="false" type="java.lang.String" %>
<%@ attribute name="tabindex" required="false" rtexprvalue="true" %>
<%@ attribute name="readonly" required="false" rtexprvalue="false" %>
<%@ attribute name="errorPath" required="false" type="java.lang.String" %>
<%@ attribute name="dataAttributes" required="false" type="java.lang.String" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/terrific/template" %>

<%-- we use text="" to prevent the message key from showing up as placeholder in case the message property was not defined --%>
<spring:message code="${placeHolderKey}" text="" var="placeHolderText" />

<template:errorSpanField path="${path}" errorPath="${errorPath}">
	<c:if test="${not empty status.errorMessages}">
		<c:set var="cssErrorClass" value="error" />
	</c:if>
	<form:textarea cssClass="field ${inputCSS} ${cssErrorClass}" id="${idKey}" path="${path}" placeholder="${placeHolderText}" tabindex="${tabindex}" readonly="${readonly}" rows="${rows != null ? rows : '5' }" htmlEscape="true" />
</template:errorSpanField>
