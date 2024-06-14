<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- isSecure comes from Abstract Page Controller and is a boolean, if the protocoll is https or not --%>

<%-- video object needs to be wrapped in a div (module div), see link below --%>
<div class="wrapper-container">
	
	<c:choose>
		<c:when test="${fn:contains(videoId, 'youtu')}">
			<c:set var="youtubeId" value="${fn:split(videoId, '/')}" />
			<iframe width="480" height="310" src="https://www.youtube.com/embed/${youtubeId[2]}?rel=0&modestbranding=1" frameborder="0" allowfullscreen></iframe>
		</c:when>
		<c:otherwise>
			<iframe width="480" height="310" src="https://www.youtube.com/embed/${videoId}?rel=0&modestbranding=1" frameborder="0" allowfullscreen></iframe>
		</c:otherwise>
	</c:choose>
</div>