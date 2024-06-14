<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="visibleItems" value="0"/>
<c:set var="maxVisibleItemsFullWidth" value="7"/>
<c:set var="maxVisibleItemsTwoThird" value="5"/>

<c:choose>
	<c:when test="${componentWidth == 'fullWidth'}">
		<c:set var="visibleItems" value="${maxVisibleItemsFullWidth}"/>
		<c:set var="visibleItemPageLoad" value="4"/> <!-- 1-based Index !-->
		<c:if test="${fn:length(teaserItemData) < maxVisibleItemsFullWidth}">
			<c:set var="visibleItems" value="${fn:length(teaserItemData)}"/>
		</c:if>
	</c:when>
	<c:when test="${componentWidth == 'twoThird'}">
		<c:set var="visibleItems" value="${maxVisibleItemsTwoThird}"/>
		<c:set var="visibleItemPageLoad" value="3"/> <!-- 1-based Index !-->
		<c:if test="${fn:length(teaserItemData) < maxVisibleItemsTwoThird}">
			<c:set var="visibleItems" value="${fn:length(teaserItemData)}"/>
		</c:if>
	</c:when>
</c:choose>

<h2 class="base padding-left">${title}</h2>
<section class="teaser-content ${componentWidth == 'fullWidth' ? 'fullWidth' : 'twoThird'}">
	<div class="teaser-content-carousel">
		<c:forEach items="${teaserItemData}" var="itemData" varStatus="status">
			<c:set var="codeErpRelevant" value="${itemData.codeErpRelevant == undefined ? 'x' : itemData.codeErpRelevant}" />
			<!-- Position is used for the Module JS Logic !-->
			<mod:hero-teaser-item itemData="${itemData}" htmlClasses="${componentWidth == 'fullWidth' ? 'fullWidth' : 'twoThird'}${itemData.fullsize ? ' fullsize' : '' }" attributes='data-product-id="${itemData.productCode}" data-position="${status.index + 1}"'/>
		</c:forEach>
	</div>
</section>
<section class="teaser-thumbnails ${componentWidth == 'fullWidth' ? 'fullWidth' : 'twoThird'}">
	<div class="teaser-thumbnails-carousel" data-autoplay="${autoplay}" data-timeout="${autoplayTimeout}" data-items-visible="${visibleItems}">
		<!-- Position is used for the Module JS Logic !-->
		<c:forEach items="${teaserItemData}" var="itemData" varStatus="status">
			<a href="#" class="thumbnail${status.index == 0 ? ' first-item' : ''}" data-product-id="${itemData.productCode}" data-position="${status.index + 1}"><img alt="${itemData.thumbnail.altText == null ? sImageMissing : itemData.thumbnail.altText}" src="${itemData.thumbnail.url eq null ? '/_ui/all/media/img/missing_landscape_small.png' : itemData.thumbnail.url}" /></a>
		</c:forEach>
	</div>
	<a class="btn-prev" href="#"><i></i></a>
	<a class="btn-next" href="#"><i></i></a>
</section>