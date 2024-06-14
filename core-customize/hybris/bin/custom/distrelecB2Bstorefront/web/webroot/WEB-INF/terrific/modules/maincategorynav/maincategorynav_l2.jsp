<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<c:if test="${not empty level1node.children}">
	<ul class="level-1-wrapper">
		<c:forEach items="${level1node.children}" var="level2node">
			<c:set var="level2entry" value="${level2node.entries[0]}" />
			<li class="li-mouseover main_li level2 nav-hover">
			<div class="level_1">
			<c:choose>
				<c:when test="${level2entry.item.itemtype eq 'ContentPage'}">
					<c:url value="${ycommerce:contentPageUrl(level2entry.item, request)}" var="level2url" />
				</c:when>
				<c:when test="${level2entry.item.itemtype eq 'CMSLinkComponent'}">
					<c:url value="${ycommerce:cmsLinkComponentUrl(level2entry.item, request)}" var="level2url" />
				</c:when>
				<c:when test="${level2entry.item.itemtype eq 'Category'}">
					<c:url value="${ycommerce:categoryUrl(level2entry.item, request)}" var="level2url" />
				</c:when>
				<c:otherwise>
					<c:url value="#" var="level2url" />
				</c:otherwise>
			</c:choose>
			<c:set var="titleValueWt" value="${fn:replace(level2node.title, ' ', '-')}" />
			<c:set var="titleValueWtLower" value="${fn:toLowerCase(titleValueWt)}" />
			<a title="${level2node.title}" class="link_l2${level2url == '#' ? ' no-click' : ''}" href="${level2url}" data-aainteraction='navigation' data-location='category nav' data-parent-link='${fn:toLowerCase(level1node.getTitle(enLocale))}' data-link-value='${fn:toLowerCase(level2node.getTitle(enLocale))}'>
				<span title="${level2node.title}" class="level1titleClass text ellipsis">${level2node.title}</span>
				<c:if test="${fn:length(level2node.children) > 0 && maxCatLevel > 2}">
					<i class="fa fa-angle-right" aria-hidden="true"></i>
				</c:if>
			</a>
				<c:if test="${fn:length(level2node.children) > 0 && maxCatLevel > 2}">
					<span class="level2-toggle menu">
						<i class="fa fa-angle-right" aria-hidden="true"></i>
					</span>
				</c:if>
			</div>
				<c:if test="${fn:length(level2node.children) > 0 && maxCatLevel > 2}">
					<div class="content-l2">
						<%@ include file="maincategorynav_l3.jsp" %>
					</div>
				</c:if>
			</li>
		</c:forEach>
	</ul>
</c:if>
