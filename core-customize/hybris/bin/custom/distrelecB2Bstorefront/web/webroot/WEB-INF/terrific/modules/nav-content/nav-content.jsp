<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>

<c:choose>
	<c:when test="${empty cmsPage.navigationNodeList[0].parent.parent.parent}">
		<c:set var="navRootNodes" value="${cmsPage.navigationNodeList[0].children}" />
		<c:set var="pageRootNavNode" value="${cmsPage.navigationNodeList[0].children[0]}" />
	</c:when>
	<c:when test="${empty cmsPage.navigationNodeList[0].parent.parent.parent.parent}">
		<c:set var="navRootNodes" value="${cmsPage.navigationNodeList[0].parent.children}" />
		<c:set var="pageRootNavNode" value="${cmsPage.navigationNodeList[0]}" />
	</c:when>
	<c:when test="${empty cmsPage.navigationNodeList[0].parent.parent.parent.parent.parent}">
		<c:set var="navRootNodes" value="${cmsPage.navigationNodeList[0].parent.parent.children}" />
		<c:set var="pageRootNavNode" value="${cmsPage.navigationNodeList[0].parent}" />
	</c:when>
</c:choose>

<c:if test="${not empty navRootNodes}">
	<ul class="content-nav-ul">
		<c:forEach items="${navRootNodes}" var="navRootNode">
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
				<c:choose>
					<c:when test="${empty navRootNodeUrl}">
						<a class="accordion-item-title"><i class="icon-indicator"></i>${navRootNode.title}</a>
					</c:when>
					<c:otherwise>
						<a class="accordion-item-title" href="${navRootNodeUrl}"><i class="icon-indicator"></i>${navRootNode.title}</a>
					</c:otherwise>
				</c:choose>
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
			    				<a class="${navNodeEntry.item.pk eq cmsPage.pk ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}">${navSubNode.title}</a>
							</li>
		    			</c:forEach>
		    		</ul>
		    	</div>
		    </li>
		</c:forEach>
	</ul>
</c:if>