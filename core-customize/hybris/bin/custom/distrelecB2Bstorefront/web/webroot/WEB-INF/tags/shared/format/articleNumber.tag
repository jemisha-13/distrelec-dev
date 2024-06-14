<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="articleNumber" required="true" type="java.lang.String" %>

<%--
 Tag to render an article number using the format XXX-XX-XXX 
--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:choose>
	<c:when test="${fn:length(articleNumber) eq 8}">
		<c:set var="string1" value="${fn:substring(articleNumber, 0, 3)}" />
		<c:set var="string2" value="${fn:substring(articleNumber, 3, 5)}" />
		<c:set var="string3" value="${fn:substring(articleNumber, 5, 8)}" />
		
		${string1}-${string2}-${string3}	
	</c:when>
	<c:otherwise>
		${articleNumber}
	</c:otherwise>
</c:choose>




