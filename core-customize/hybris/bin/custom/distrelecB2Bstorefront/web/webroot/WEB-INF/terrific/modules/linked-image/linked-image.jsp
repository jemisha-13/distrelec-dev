<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:if test="${not empty media}">
	<div class="img-ct">
		<c:choose>
			<c:when test="${empty encodedUrl || encodedUrl eq '#'}">
				<%-- w/o link --%>
				<img src="${media.url}" width="${imgWidth}" height="${imgHeight}" alt="${media.altText}" />
				<c:if test="${not empty caption}">
					<figcaption>${caption}</figcaption>
				</c:if>
			</c:when>
			<c:otherwise>
				<%-- w/ link --%>
				<a href="${encodedUrl}" title="${media.altText}">
					<img src="${media.url}" width="${imgWidth}" height="${imgHeight}" alt="${media.altText}" />
				</a>
				<c:if test="${not empty caption}">
					<figcaption>${caption}</figcaption>
				</c:if>
			</c:otherwise>
		</c:choose>
	</div>
</c:if>
