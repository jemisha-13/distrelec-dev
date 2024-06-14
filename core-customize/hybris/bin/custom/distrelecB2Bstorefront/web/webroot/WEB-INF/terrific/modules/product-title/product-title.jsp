<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:choose>
	<c:when test="${not empty metaData.h1}">
		<h1 class="base title">${metaData.h1}</h1>
	</c:when>
	<c:otherwise>
		<h1 class="base title">${title}</h1>
	</c:otherwise>
</c:choose> 		
