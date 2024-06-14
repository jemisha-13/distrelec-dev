<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:if test="${not empty title}">
	<h2 class="list-title">${title}</h2>
</c:if>
<ul>
	<c:forEach items="${listData}" var="itemData">
		<c:url value="${ycommerce:cmsLinkComponentUrl(itemData, request)}" var="linkUrl" />
		<li><a href="${linkUrl}">${itemData.linkName}<i></i></a></li>
	</c:forEach>
</ul>
