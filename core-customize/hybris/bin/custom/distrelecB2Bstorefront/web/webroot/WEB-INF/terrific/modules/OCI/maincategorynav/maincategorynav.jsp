<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:eval expression="new java.util.Locale('en')" var="enLocale" />
<c:set var="nodesChildren" value="${nodes.get()}"/>
<div class="wrapper">
<div id="menu"> 
	<ul class="level-1-wrapper">
		<c:if test="${not empty nodesChildren}">
		
		<c:url value="${ycommerce:categoryUrl(component.rootNavigationNode.entries[0].item, request)}" var="mainCatUrl" />
			<c:forEach items="${nodesChildren}" var="level1node" >
				<c:set var="level1entry" value="${level1node.entries[0]}" />
				<c:set var="level1entry" value="${level1node.entries[0]}" />
				<c:if test="${ycommerce:displayCatalogEProcurement(level1entry.item, request) && ycommerce:hasNotOnlyPunchedOutCategories(level1node, request)}">
					<li class="li-mouseover main_li nav-hover">
						<div class='activator'>
							<div class="level_1">
								<c:choose>
									<c:when test="${level1entry.item.itemtype eq 'ContentPage'}">
										<c:url value="${ycommerce:contentPageUrl(level1entry.item, request)}" var="level1url" />
									</c:when>
									<c:when test="${level1entry.item.itemtype eq 'CMSLinkComponent'}">
										<c:url value="${ycommerce:cmsLinkComponentUrl(level1entry.item, request)}" var="level1url" />
									</c:when>
									<c:when test="${level1entry.item.itemtype eq 'Category'}">
										<c:url value="${ycommerce:categoryUrl(level1entry.item, request)}" var="level1url" />
									</c:when>
									<c:otherwise>
										<c:url value="#" var="level1url" />
									</c:otherwise>
								</c:choose>					
								<c:set var="titleValueWt" value="${fn:replace(level1node.title, ' ', '-')}" />
								<c:set var="titleValueWtLower" value="${fn:toLowerCase(titleValueWt)}" />
								<a title="${level1node.title}" class="link_l1${level1url == '#' ? ' no-click' : ''}" href="${level1url}" data-aainteraction='navigation' data-location='category nav' data-parent-link='${fn:toLowerCase(component.rootNavigationNode.getTitle(enLocale))}' data-link-value='${fn:toLowerCase(level1node.getTitle(enLocale))}'>
									<span title="${level1node.title}" class="level1titleClass"><span class="text ellipsis">${level1node.title}</span><i class="fa fa-angle-right" aria-hidden="true"></i></span>
								</a>
								<span class="level2-toggle">
									<i class="fa fa-angle-right" aria-hidden="true"></i>
								</span>
							</div>
							<%-- DISTRELEC-11523: Don't open right content when a category has no children --%>
							<c:if test="${fn:length(level1node.children) > 0}">
								<div class="content-l1">
									<%@ include file="maincategorynav_l2.jsp" %>
								</div>
							</c:if>
						</div>
					</li>
				</c:if>
			</c:forEach>
		</c:if>
	</ul>
	<div class="overlay-second"></div>
</div>
</div>
