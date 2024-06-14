<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@configurationService.configuration.getInt('distrelec.taxonomy.category.level.max')" var="maxCatLevel" scope="application" />
<c:if test="${not empty level2node.children}">
	<ul class="level-1-wrapper">
		<c:forEach items="${level2node.children}" var="level3node">
			<c:set var="level3entry" value="${level3node.entries[0]}" />
			<li class="li-mouseover main_li level3 nav-hover">
			<div class="level_1">
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
				<a title="${level3node.title}" class="link_l3${level3url == '#' ? ' no-click' : ''}" href="${level3url}" data-aainteraction='navigation' data-location='category nav' data-parent-link='${fn:toLowerCase(level2node.getTitle(enLocale))}' data-link-value='${fn:toLowerCase(level3node.getTitle(enLocale))}'>
					<span title="${level3node.title}" class="level1titleClass text ellipsis">${level3node.title}</span>
					<c:if test="${fn:length(level3node.children) > 0 && maxCatLevel > 3}">
						<i class="fa fa-angle-right" aria-hidden="true"></i>
					</c:if>
				</a>
				<c:if test="${fn:length(level3node.children) > 0 && maxCatLevel > 3}">
					<span class="level2-toggle">
						<i class="fa fa-angle-right" aria-hidden="true"></i>
					</span>
				</c:if>


			</div>
				<c:if test="${fn:length(level3node.children) > 0 && maxCatLevel > 3}">
				
					<div class="content-l3">
						<%@ include file="maincategorynav_l4.jsp" %>
					</div>
				</c:if>
			</li>
		</c:forEach>
	</ul>
</c:if>