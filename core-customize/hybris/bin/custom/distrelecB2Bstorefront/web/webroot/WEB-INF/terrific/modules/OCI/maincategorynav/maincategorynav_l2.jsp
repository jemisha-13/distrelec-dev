<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<c:if test="${not empty level1node.children}">
	<div class="title-l2">
			<span class="title-l2__text">${level1node.title}<i class="fa fa-angle-right" aria-hidden="true"></i></span>
	</div>
	<div class="wrapper-l2">
		<c:set var="noOfChildren" value="${fn:length(level1node.children)}" />
		<fmt:parseNumber var="halfOfChildren" value="${noOfChildren/2+.6}" integerOnly="true" />
		<c:set var="columnStarter" value="</div><div class=\"level2col\">" />
		<div class="level2col">
		<c:forEach items="${level1node.children}" var="level2node" varStatus="l2Count" >
			<c:set var="level2entry" value="${level2node.entries[0]}" />
			<div class="content-l2">
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
				<div class="ellipsis-nav">
					<a href="${level2url}" data-aainteraction='navigation' data-location='category nav' data-parent-link='${fn:toLowerCase(level1node.getTitle(enLocale))}' data-link-value='${fn:toLowerCase(level2node.getTitle(enLocale))}'>	${level2node.title} </a>
				</div>
			</div>
			<c:if test="${noOfChildren > 5 && l2Count.count eq halfOfChildren}">
				${columnStarter}
			</c:if>
		</c:forEach>
		</div>
	</div>
</c:if>
