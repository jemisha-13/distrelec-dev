<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<c:if test="${not empty level2node.children}">
	<c:set var="nrOfLevel3nodes" value="${fn:length(level2node.children)}" />
	<ul class="l3">
		<c:set var="e3SizeLimit" value="7" />
		<c:set var="level3node_max" value="${e3SizeLimit - 1}"/>
		<c:if test="${level2entry.item.itemtype eq 'Category'}">
			<c:set var="numberOfDirectSubCategories" value="${fn:length(level2entry.item.categories)}" />
		</c:if>
		<c:forEach items="${level2node.children}" var="level3node" begin="0" end="${level3node_max}">
			<c:set var="level3entry" value="${level3node.entries[0]}" />
			<c:if test="${ycommerce:hasNotOnlyPunchedOutCategories(level3node, request)}">
				<li class="e3">
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
					<c:set var="titleValueWt" value="${fn:replace(level3node.title, ' ', '-')}" />
					<c:set var="titleValueWtLower" value="${fn:toLowerCase(titleValueWt)}" />							
					<a class="a3" href="${level3url}" name="${wtTeaserTrackingIdMnv}.${titleValueWtLower}.-">${level3node.title}</a>
				</li>
			</c:if>
		</c:forEach>
		<c:if test="${nrOfLevel3nodes gt e3SizeLimit - 1 && numberOfDirectSubCategories gt e3SizeLimit}">
			<li class="e3 more">
				<a class="a3" href="${level2url}"><i></i><spring:theme htmlEscape="yes" code="mainnav.link.more" text="more"/></a>
			</li>
		</c:if>
	</ul>
</c:if>
