<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>



<c:choose>
	<c:when test="${layout == 'product'}">
		<c:set var="visibleItems" value="0"/>
		<c:set var="maxVisibleItemsFullWidth" value="4"/>
		<c:set var="maxVisibleItemsTwoThird" value="2"/>
	</c:when>
	<c:when test="${layout == 'category'}">
		<c:set var="visibleItems" value="0"/>
		<c:set var="maxVisibleItemsFullWidth" value="6"/>
		<c:set var="maxVisibleItemsTwoThird" value="4"/>
	</c:when>
	<c:when test="${layout == 'manufacturer'}">
		<c:set var="visibleItems" value="0"/>
		<c:set var="maxVisibleItemsFullWidth" value="6"/>
		<c:set var="maxVisibleItemsTwoThird" value="4"/>
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${componentWidth == 'fullWidth'}">
		<c:set var="visibleItems" value="${maxVisibleItemsFullWidth}"/>
		<c:if test="${fn:length(carouselData) < maxVisibleItemsFullWidth}">
			<c:set var="visibleItems" value="${fn:length(carouselData)}"/>
		</c:if>
	</c:when>
	<c:when test="${componentWidth == 'twoThird'}">
		<c:set var="visibleItems" value="${maxVisibleItemsTwoThird}"/>
		<c:if test="${fn:length(carouselData) < maxVisibleItemsTwoThird}">
			<c:set var="visibleItems" value="${fn:length(carouselData)}"/>
		</c:if>
	</c:when>
</c:choose>

<section class="carousel-teaser-container cf ${componentWidth == 'fullWidth' ? 'fullWidth' : 'twoThird'}">
	<div class="carousel-header${fn:length(carouselData) le visibleItems ? ' no-nav-buttons' : ''}">
		<h3 class="base ellipsis padding-left" title="${title}">${title}</h3>
		<c:if test="${fn:length(carouselData) > visibleItems}">
			<nav class="carousel-nav">
				<a class="btn-prev" href="#"><i></i></a>
				<a class="btn-next" href="#"><i></i></a>
			</nav>
		</c:if>
	</div>
	<%-- Subtract one because loop counter var is zero based --%>
	<c:set var="toDisplay" value="${fn:length(carouselData) - 1}" />
	<%-- maxNumberToDisplay comes from cms --%>
	<c:if test="${maxNumberToDisplay gt 0}">
		<c:set var="toDisplay" value="${maxNumberToDisplay-1}" />
	</c:if>

	<div class="carousel-teaser" data-autoplay="${autoplay}" data-timeout="${autoplayTimeout}" data-direction="${autoplayDirection}" data-items-visible="${visibleItems}">
		<c:choose>
			<c:when test="${layout == 'product'}">
				<c:forEach items="${carouselData}" var="itemData" end="${toDisplay}" varStatus="status">
					<mod:carousel-teaser-item
					template="product-box"
					skin="product-horizontal-box"
					displayPromotionText=""
					showLogo="${showLogo}"
					itemData="${itemData}"
					/>
				</c:forEach>
			</c:when>
			<c:when test="${layout == 'category'}">
				<c:forEach items="${carouselData}" var="itemData" end="${toDisplay}" varStatus="status">
					<mod:carousel-teaser-item template="category" skin="category" itemData="${itemData}" position="${status.index + 1}" attributes="data-product-id='${itemData.code}' data-position='${status.index + 1}'"/>
				</c:forEach>
			</c:when>
			<c:when test="${layout == 'manufacturer'}">
				<c:forEach items="${carouselData}" var="itemData" end="${toDisplay}" varStatus="status">
					<mod:carousel-teaser-item template="manufacturer" skin="manufacturer" itemData="${itemData}" position="${status.index + 1}" attributes="data-product-id='${itemData.code}' data-position='${status.index + 1}'" />
				</c:forEach>
			</c:when>
		</c:choose>
	</div>
</section>
