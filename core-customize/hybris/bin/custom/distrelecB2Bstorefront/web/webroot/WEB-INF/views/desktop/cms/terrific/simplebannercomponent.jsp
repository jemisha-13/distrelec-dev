<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:url value="${component.localizedUrlLink}" var="encodedUrl" />
<div class="simple-banner">
	<c:choose>
		<c:when test="${empty encodedUrl || encodedUrl eq '#'}">
		<span class="img-ct">
			<c:if test="${not empty media.url}">
				<img class="img" alt="${media.altText}" src="${media.url}" width="190" height="33">
			</c:if>
		</span>
		</c:when>
		<c:otherwise>
			<a class="img-ct sbc-link" data-youtubeid-sbp="${component.youTubeID}" href="${encodedUrl}"<c:if test="${not empty component.wtOnSiteLinkId}"> name="${component.wtOnSiteLinkId}"</c:if>>
				<c:if test="${not empty media.url}">
					<img class="img" alt="${media.altText}" src="${media.url}" width="190" height="33">
				</c:if>
			</a>
		</c:otherwise>
	</c:choose>
</div>

	