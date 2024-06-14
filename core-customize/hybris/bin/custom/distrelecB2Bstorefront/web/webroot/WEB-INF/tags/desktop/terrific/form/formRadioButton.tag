<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/terrific/template" %>

<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="labelKey" required="false" type="java.lang.String" %>
<%@ attribute name="inputCSS" required="false" type="java.lang.String" %>
<%@ attribute name="idKey" required="true" type="java.lang.String" %>
<%@ attribute name="value" required="false" type="java.lang.String" %>
<%@ attribute name="name" required="false" type="java.lang.String" %>

<template:errorSpanField path="${path}">
	<spring:theme code="${labelKey}" var="sRadioButtonLabel" />
	<form:radiobutton htmlEscape="false" cssClass="${inputCSS}" id="${idKey}" path="${path}" value="${value}" label="${sRadioButtonLabel}" name="${name}" />
</template:errorSpanField>