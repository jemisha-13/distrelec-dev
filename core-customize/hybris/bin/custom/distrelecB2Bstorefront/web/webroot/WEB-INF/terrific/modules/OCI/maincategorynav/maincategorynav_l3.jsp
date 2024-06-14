<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty level2node.children}">
	<ul class="sub-nav ">
		<c:forEach items="${level2node.children}" var="level3node">
			<c:set var="level3entry" value="${level3node.entries[0]}" />
			<c:if test="${ycommerce:hasNotOnlyPunchedOutCategories(level3node, request)}">
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
				<li class="sub-nav3">
					<a class="a-mouseover-l3 resizable-a" href="${level3url}"> <span class="ellipsis-nav" title="${level3node.title}" data-aainteraction='navigation' data-location='category nav' data-parent-link='${fn:toLowerCase(level2node.getTitle(enLocale))}' data-link-value='${fn:toLowerCase(level3node.getTitle(enLocale))}'> ${level3node.title} </span> </a> 
					<label class="toggle-sub"></label>
				</li>						
			</c:if>
		</c:forEach>
	</ul>
</c:if>