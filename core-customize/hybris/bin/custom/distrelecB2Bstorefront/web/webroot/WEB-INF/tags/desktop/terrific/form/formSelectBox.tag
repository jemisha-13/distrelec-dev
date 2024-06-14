<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="idKey" required="true" type="java.lang.String" %>
<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="items" required="true" type="java.util.Collection" %>
<%@ attribute name="itemValue" required="false" type="java.lang.String" %>
<%@ attribute name="itemLabel" required="false" type="java.lang.String" %>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean" %>
<%@ attribute name="selectCSSClass" required="false" type="java.lang.String" %>
<%@ attribute name="skipBlank" required="false" type="java.lang.Boolean" %>
<%@ attribute name="skipBlankSelectable" required="false" type="java.lang.Boolean" description="True if the top skipBlank option should be selectabele" %>
<%@ attribute name="skipBlankMessageKey" required="false" type="java.lang.String" %>
<%@ attribute name="selectedValue" required="false" type="java.lang.String" %>
<%@ attribute name="tabindex" required="false" rtexprvalue="true" %>
<%@ attribute name="disabled" required="false" rtexprvalue="true" %>
<%@ attribute name="errorPath" required="false" type="java.lang.String" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/terrific/template" %>


<template:errorSpanField path="${path}">

	<c:if test="${not empty status.errorMessages}">
		<c:set var="cssErrorClass" value="error" />
	</c:if>
	

	<form:select id="${idKey}" path="${path}" cssClass="field ${selectCSSClass} ${cssErrorClass}" tabindex="${tabindex}" disabled="${disabled}">
		<c:if test="${skipBlank == null || skipBlank == false}">
			<option value="" ${skipBlankSelectable == null || skipBlankSelectable == false ? 'disabled="disabled"' : ''} selected="${selectedValue == null ? 'selected' : ''}"><spring:theme code='${skipBlankMessageKey}'/></option>
		</c:if>
		
		<%-- manual forEach is used instead of form:options tag, because dynamic title is not supported by the form:options tag --%>
		<%-- some selects do not have a itemValue property named 'code', thats why we have the possibility to provide a dynamic itemValue property name--%>
		<%-- some selects do not have a itemLabel property named 'name', thats why we have the possibility to provide a dynamic itemLabel property name--%>
		
		<%-- if selectedValue is set, we generate the options manually, since form:option tag only works with a path variable and is not taking a selected value --%>		
		<c:forEach items="${items}" var="item" varStatus="status">
				
			<c:set var="optionValue" value="${not empty itemValue ? item[itemValue] : item['code']}" />
			<c:set var="optionLabel" value="${not empty itemLabel ? item[itemLabel] :item['name']}" />
			
			<c:choose>
				<c:when test="${not empty selectedValue}">
					<option value="${optionValue}" title="${optionLabel}" ${selectedValue == optionValue ? 'selected="selected"' : ''}>
						${optionLabel}
					</option>
				</c:when>
				<c:otherwise>
					<form:option value="${optionValue}" label="${optionLabel}" title="${optionLabel}"  />
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</form:select>

</template:errorSpanField>
