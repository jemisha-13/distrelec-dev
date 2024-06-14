<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:theme code="shoppingListPage.favorite" text="My Favorite products" var="favoriteListTitle" />
<spring:theme code="shoppingList.default" text="Shopping List" var="shoppingListName" />

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout ${currentList.listType == 'FAVORITE' or (empty currentList.listType) ? 'skin-layout-favorite-list' : 'skin-layout-shopping-list'}  skin-layout-wide">
	<div class="container">
		<div class="row">
			<div id="breadcrumb" class="breadcrumb">
				<mod:breadcrumb/>
			</div>
			<div class="col-12 d-print-none">
				<mod:global-messages/>
			</div>
		</div>
	</div>

	<div class="container">
		<div class="row">
			<div class="col-sm-12 col-lg-9">
				<c:choose>
					<c:when test="${currentList.listType == 'FAVORITE' or (empty currentList.listType)}">
						<h1 class="base page-title">${favoriteListTitle}</h1>
						<mod:productlist-tools template="favorite" skin="favorite" exportId="${currentList.uniqueId}" downloadUrl="${downloadUrl}" productsInListCount="${fn:length(currentList.entries)}" />
						<mod:productlist-controllbar template="favorite" skin="favorite" productListOrders="${productListOrders}" productsInListCount="${fn:length(currentList.entries)}" />
						<mod:productlist template="favorite" skin="favorite" currentList="${currentList}" />
					</c:when>
					<c:otherwise>
						<div class="row">
							<c:set var="listName" value="${currentList.name}"/>
							<c:if test="${currentList.name=='Shopping List'}">
								<c:set var="listName" value="${shoppingListName}"/>
							</c:if>
							<mod:shoppinglist-title shoppinglistTitle="${listName}" htmlClasses="col-sm-6" />
							<mod:shoppinglist-add-to-cart listId="${currentList.uniqueId}"  htmlClasses="col-sm-6" />
						</div>
						<mod:productlist-tools template="shopping" skin="shopping" exportId="${currentList.uniqueId}" downloadUrl="${downloadUrl}" productsInListCount="${fn:length(currentList.entries)}" />
						<mod:productlist-controllbar template="shopping" skin="shopping" productListOrders="${productListOrders}" productsInListCount="${fn:length(currentList.entries)}" />
						<mod:productlist template="shopping" skin="shopping" attributes="data-list-id='${currentList.uniqueId}'" currentList="${currentList}" htmlClasses="container" />
						<mod:productlist template="calculate" skin="calculate" htmlClasses="container" />
					</c:otherwise>
				</c:choose>
			</div>
			<div class="col-sm-12 col-lg-3">
				<mod:shoppinglist-meta-actions shoppingListsList="${shoppingLists}" favoriteListItemCount="${favoriteListCount}" favoriteListId="${favoriteListId}" currentList="${currentList}"/>
			</div>			
			<mod:print-footer/>
		</div>
	</div>
</views:page-default-md-full>