<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<c:if test="${not empty navigationNodeList}">
	<ul class="content-nav-ul">
		<c:forEach items="${navigationNodeList}" var="navRootNode">
			<c:set var="navRootNodeEntry" value="${navRootNode.entries[0]}" />
			<c:choose>
				<c:when test="${navRootNodeEntry.item.itemtype eq 'ContentPage'}">
					<c:url value="${ycommerce:contentPageUrl(navRootNodeEntry.item, request)}" var="navRootNodeUrl" />
				</c:when>
				<c:when test="${navRootNodeEntry.item.itemtype eq 'CMSLinkComponent'}">
					<c:url value="${ycommerce:cmsLinkComponentUrl(navRootNodeEntry.item, request)}" var="navRootNodeUrl" />
				</c:when>
				<c:when test="${navRootNodeEntry.item.itemtype eq 'Category'}">
					<c:url value="${ycommerce:categoryUrl(navRootNodeEntry.item, request)}" var="navRootNodeUrl" />
				</c:when>
			</c:choose>
			<li class="${navRootNode.pk eq pageRootNavNode.pk ? 'accordion-item is-expanded' : 'accordion-item'}">
						<a href="${navRootNodeUrl}" class="sitemap-page__category"><b>${navRootNode.title}</b></a>
				<div class="spring" ${navRootNode.pk eq pageRootNavNode.pk ? 'style="display: block;"' : '' }>
		    		<ul>
		    			<c:forEach items="${navRootNode.children}" var="navSubNode">
		    				<li>
			    				<c:set var="navNodeEntry" value="${navSubNode.entries[0]}" />
			    				<c:choose>
									<c:when test="${navNodeEntry.item.itemtype eq 'ContentPage'}">
										<c:url value="${ycommerce:contentPageUrl(navNodeEntry.item, request)}" var="encodedUrl" />
									</c:when>
									<c:when test="${navNodeEntry.item.itemtype eq 'CMSLinkComponent'}">
										<c:url value="${ycommerce:cmsLinkComponentUrl(navNodeEntry.item, request)}" var="encodedUrl" />
									</c:when>
									<c:when test="${navNodeEntry.item.itemtype eq 'Category'}">
										<c:url value="${ycommerce:categoryUrl(navNodeEntry.item, request)}" var="encodedUrl" />
									</c:when>
									<c:otherwise>
										<c:url value="#" var="encodedUrl" />
									</c:otherwise>
								</c:choose>
			    				<a class="sitemap-page__sub-category" href="${encodedUrl}">${navSubNode.title}</a>
							</li>
		    			</c:forEach>
		    		</ul>
		    	</div>
		    </li>
		</c:forEach>
	</ul>
</c:if>			