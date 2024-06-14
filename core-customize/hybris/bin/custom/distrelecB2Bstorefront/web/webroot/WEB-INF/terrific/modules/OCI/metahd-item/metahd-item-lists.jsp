<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:theme code="metahd.lists" text="Lists" var="sLists" />
<spring:theme code="metahd.lists.favorites-shopping" text="Favorites & Shopping" var="sFavoritesShopping" />
<spring:theme code="metahd.lists.favorite-products" text="My Favorite Products" var="sFavoriteProducts" />
<spring:theme code="metahd.lists.show-all-lists" text="Show all lists" var="sShowAllLists" />
<c:set var="maxVisibleShoppingLists" value="5" />
<c:set var="isLoggedin" value="false" />
<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
	<c:set var="isLoggedin" value="true" />
</sec:authorize>
<sec:authorize access="!hasRole('ROLE_B2BEESHOPGROUP')">
	<c:url value="/shopping" var="shoppinglistHomeUrl"/>
</sec:authorize>
<div class="menuitem-wrapper">
	<a class="menuitem popover-origin" href="${shoppinglistHomeUrl}"> <span class="vh">${sLists}</span> <i></i> </a>
	<section class="flyout">
		<div class="hd">
			<div class="-left"> <h3>${sLists}</h3><small>${sFavoritesShopping}</small> </div>
			<div class="-right"> <a aria-hidden="true" data-dismiss="modal" href="#" class="btn btn-close">${sClose}</a> </div>
		</div>
		<c:choose>
			<c:when test="${isLoggedin}">
				<div class="bd">
					<ul class="ui-list">
						<sec:authorize access="!hasRole('ROLE_B2BEESHOPGROUP')">
							<li class="favorite-products"> <a href="/shopping/favorite"><i class="ico-favorite-products"></i>${sFavoriteProducts}</a> </li>
							<c:forEach items="${metaLists}" var="listItem" end="${maxVisibleShoppingLists-1}">
								<li class="list">
									<%-- Span is needed to be able to update list name when changing it on the shoppinglist page --%>
									<a href="/shopping/${listItem.uniqueId}" data-list-id="${listItem.uniqueId}" class="ellipsis listname"><i class="ico-list"></i>	<span>${fn:escapeXml(listItem.name)}</span> </a>
								</li>
							</c:forEach>
						</sec:authorize>
					</ul>
				</div>
				<c:if test="${fn:length(metaLists) > maxVisibleShoppingLists}">
					<div class="ft"> <a class="show-all" href="/shopping">${sShowAllLists}</a> </div>
				</c:if>
			</c:when>
			<c:otherwise>
				<div class="bd">
					<ul class="ui-list">
						<li class="favorite-products"> <a href="/shopping/favorite"><i class="ico-favorite-products"></i><i class="ico-closed"></i>${sFavoriteProducts}</a> </li>
						<li class="list"> <a href="/shopping"><i class="ico-list"></i><i class="ico-closed"></i><spring:message code="metahd.lists.shopping-list.loggedout" /></a> </li>
					</ul>
				</div>
			</c:otherwise>
		</c:choose>
	</section>
</div>
<script id="template-metahd-new-list" type="text/x-template-dotjs">
	 <li class="list">
		<a href="/shopping/{{=it.listId}}" data-list-id="{{=it.listId}}" class="ellipsis listname"><i class="ico-list"></i>
			{{=it.listName}}
		</a>
	</li>
</script>