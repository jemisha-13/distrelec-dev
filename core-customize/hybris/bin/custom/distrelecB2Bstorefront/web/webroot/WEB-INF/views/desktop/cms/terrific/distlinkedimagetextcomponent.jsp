<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:url value="${not empty page ? page.label : component.localizedUrlLink}" var="encodedUrl" />

<c:choose>
	<c:when test="${imageWidth.code eq 'width140'}">
		<c:set var="imgWidth" value="140"/>
		<c:set var="imgHeight" value="80"/>
	</c:when>
	<c:otherwise>
		<c:set var="imgWidth" value="220"/>
		<c:set var="imgHeight" value="125"/>
	</c:otherwise>
</c:choose>

<mod:linked-image template="text"
	media="${media}"
	contentText="${contentText}"
	encodedUrl="${encodedUrl}"
	imgWidth="${imgWidth}"
	imgHeight="${imgHeight}"
	caption="${caption}" />
