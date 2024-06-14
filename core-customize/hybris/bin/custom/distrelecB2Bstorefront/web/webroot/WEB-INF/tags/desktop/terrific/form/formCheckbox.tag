<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/terrific/template" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="idKey" required="true" type="java.lang.String" %>
<%@ attribute name="labelKey" required="false" type="java.lang.String" %>
<%@ attribute name="labelArguments" required="false" %>
<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean" %>
<%@ attribute name="labelCSS" required="false" type="java.lang.String" %>
<%@ attribute name="inputCSS" required="false" type="java.lang.String" %>
<%@ attribute name="tabindex" required="false" rtexprvalue="true" %>
<%@ attribute name="value" required="false" type="java.lang.String" %>
<%@ attribute name="showAsterisk" required="false" type="java.lang.Boolean" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>


<template:errorSpanField path="${path}">
	
	<c:set var="asterisk" value="${showAsterisk == null or showAsterisk}" />
	<c:set var="disabled" value="${disabled ? true : false}" />

	<c:choose>
		<c:when test="${not empty value}">
			<form:checkbox cssClass="${inputCSS}" id="${idKey}" path="${path}" value="${value}" disabled="${disabled}"/>
		</c:when>
		<c:otherwise>
			<form:checkbox cssClass="${inputCSS}" id="${idKey}" path="${path}" disabled="${disabled}"/>
		</c:otherwise>
	</c:choose>

	<label class="${labelCSS}" for="${idKey}" id="${idKey.concat('-label')}" ${disabled ? 'disabled="disabled"' : ''}">
		<spring:theme code="${labelKey}" arguments="${labelArguments}" />
		<c:if test="${mandatory != null && mandatory == true && asterisk}">
			<span class="mandatory"> *</span>
		</c:if>
	</label>
</template:errorSpanField>
