<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="idKey" required="true" type="java.lang.String" %>
<%@ attribute name="labelId" required="false" type="java.lang.String" %>
<%@ attribute name="labelKey" required="false" type="java.lang.String" %>
<%@ attribute name="path" required="false" type="java.lang.String" %>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean" %>
<%@ attribute name="stars" required="false" type="java.lang.String" %>
<%@ attribute name="labelCSS" required="false" type="java.lang.String" %>
<%@ attribute name="tabindex" required="false" rtexprvalue="true" %>
<%@ attribute name="disabled" required="false" rtexprvalue="false" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>

<c:choose>
	<c:when test="${empty stars}" >
		<c:set var="stars" value="*"/>
	</c:when>
</c:choose>
<label id="${labelId}" class="${not empty labelCSS ? labelCSS : ''}" for="${idKey}">
	<spring:theme code="${labelKey}"/>
	<c:if test="${mandatory != null && mandatory == true}">
		<span class="mandatory"> ${stars}</span>
	</c:if>
</label>