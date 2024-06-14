<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:url value="${url}" var="encodedUrl" />
<c:choose>
	<c:when test="${not empty component.iconClass}">
		<a class="${fn:toLowerCase(component.iconClass)}" href="${encodedUrl}" title="${component.linkName}" ${component.target == null || component.target == 'SAMEWINDOW' ? '' : 'target="_blank"'}></a>
	</c:when>
	<c:otherwise>
		<a href="${encodedUrl}" title="${component.linkName}" ${component.target == null || component.target == 'SAMEWINDOW' ? '' : 'target="_new"'}>${component.linkName}</a>
	</c:otherwise>
</c:choose>
