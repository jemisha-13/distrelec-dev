<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>


<c:url value="${localizedUrl}" var="encodedUrl" />
<c:choose>
	<c:when test="${not empty component.iconClass}">
		<a class="${fn:toLowerCase(component.iconClass)}" href="${encodedUrl}" title="${component.linkName}" ${component.target == null || component.target == 'SAMEWINDOW' ? '' : 'target="_blank"'} name="${namicscommerce:encodeURI(component.linkName)}" ></a>
	</c:when>
	<c:when test="${component.icon.url != null}">
		<div class="USPElement">
			<img class="iconImageCMS" src="${component.icon.url}" />
			<div class="imageTitleCMS">
				<a href="${encodedUrl}" title="${component.linkName}" ${component.target == null || component.target == 'SAMEWINDOW' ? '' : 'target="_new"'} name="${namicscommerce:encodeURI(component.linkName)}">${component.linkName}<i></i></a>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<a href="${encodedUrl}" title="${component.linkName}" ${component.target == null || component.target == 'SAMEWINDOW' ? '' : 'target="_new"'} name="${namicscommerce:encodeURI(component.linkName)}">${component.linkName}<i></i></a>
	</c:otherwise>
</c:choose>
