<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<c:if test="${not empty level2node.children}">
	<c:set var="nrOfLevel3nodes" value="${fn:length(level2node.children)}" />
	<ul class="l3">
	<c:set var="e3SizeLimit" value="1"/>
	<c:set var="level3node_max" value="${e3SizeLimit -1}"/>
	<c:forEach items="${level2node.children}" var="level3node"  begin="0" end="${level3node_max}">
		<li class="e3">
			<c:set var="level3entry" value="${level3node.entries[0]}" />
			<c:choose>
				<c:when test="${level3entry.item.itemtype eq 'ContentPage'}">
					<c:url value="${ycommerce:contentPageUrl(level3entry.item, request)}" var="level3url" />
				</c:when>
				<c:when test="${level3entry.item.itemtype eq 'CMSLinkComponent'}">
					<c:url value="${ycommerce:cmsLinkComponentUrl(level3entry.item, request)}" var="level3url" />
				</c:when>
				<c:when test="${level3entry.item.itemtype eq 'Category'}">
					<c:url value="${ycommerce:categoryUrl(level3entry.item, request)}" var="level3url" />
				</c:when>
				<c:otherwise>
					<c:url value="#" var="level3url" />
				</c:otherwise>
			</c:choose>
			<a class="a3" href="${level3url}" name="${namicscommerce:encodeURI(level3node.title)}">${level3node.title}</a>
		</li>
	</c:forEach>
	<c:if test="${nrOfLevel3nodes gt level3node_max + 1}">
		<li class="e3 more">
			<spring:theme htmlEscape="yes" var="moreLabel" code="mainnav.link.more" text="more"/>
			<a class="a3" href="${level2url}"><i></i>${moreLabel}</a>
		</li>
	</c:if>
	</ul>
</c:if>
