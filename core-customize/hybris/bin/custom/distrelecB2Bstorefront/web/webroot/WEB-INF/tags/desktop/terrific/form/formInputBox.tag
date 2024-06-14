<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="idKey" required="true" type="java.lang.String" %>
<%--<%@ attribute name="labelKey" required="true" type="java.lang.String" %>--%>
<%@ attribute name="placeHolderKey" required="false" type="java.lang.String" %>
<%@ attribute name="dataLengthKey" required="false" type="java.lang.String" 
	description="The key of the data length. If both values dataLengthKey and dataLength are set, then the high priority is set to the dataLengthKey if it is not empty" %>
<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean" %>
<%@ attribute name="labelCSS" required="false" type="java.lang.String" %>
<%@ attribute name="inputCSS" required="false" type="java.lang.String" %>
<%@ attribute name="tabindex" required="false" rtexprvalue="true" %>
<%@ attribute name="readonly" required="false" rtexprvalue="true" %>
<%@ attribute name="dataMin" required="false" rtexprvalue="false" %>
<%@ attribute name="dataMax" required="false" rtexprvalue="false" %>
<%@ attribute name="dataLength" required="false" rtexprvalue="false" %>
<%@ attribute name="maxLength" required="false" rtexprvalue="false" %>
<%@ attribute name="errorPath" required="false" type="java.lang.String" %>
<%@ attribute name="dataAttributes" required="false" type="java.lang.String" %>
<%@ attribute name="value" required="false" type="java.lang.String" %>
<%@ attribute name="inputType" required="false" type="java.lang.String" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/terrific/template" %>

<%-- we use text="" to prevent the message key from showing up as placeholder in case the message property was not defined --%>
<spring:message code="${placeHolderKey}" text="" var="placeHolderText" />

<c:if test="${not empty dataLengthKey}">
	<spring:message code="${dataLengthKey}" text="" var="dataLength2" />
</c:if>
<c:if test="${not empty dataLength2}">
	<c:set var="dataLength" value="${dataLength2}"/>
</c:if>

<template:errorSpanField path="${path}" errorPath="${errorPath}">
	<c:if test="${not empty status.errorMessages}">
		<c:set var="cssErrorClass" value="error" />
	</c:if>
	<form:input cssClass="field ${inputCSS} ${cssErrorClass}" value="${value}" id="${idKey}" type="${not empty inputType ? inputType : 'text'}" placeholder="${placeHolderText}" path="${path}" tabindex="${tabindex}" readonly="${readonly}" htmlEscape="true" data-length="${dataLength}" data-min="${dataMin}" data-max="${dataMax}" maxlength="${maxLength}" />
</template:errorSpanField>
