<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="visibleItems" value="0"/>
<c:set var="maxVisibleItemsFullWidth" value="4"/>
<c:set var="maxVisibleItemsTwoThird" value="2"/>

<c:choose>
	<c:when test="${componentWidth == 'fullWidth'}">
		<c:set var="maxVisibleItems" value="${maxVisibleItemsFullWidth}"/>
	</c:when>
	<c:when test="${componentWidth == 'twoThird'}">
		<c:set var="maxVisibleItems" value="${maxVisibleItemsTwoThird}"/>
	</c:when>
</c:choose>

<section class="carousel-teaser-container cf ${componentWidth == 'fullWidth' ? 'fullWidth' : 'twoThird'}" 
	data-ajax-url="${productFFCarouselDataPath}" 
	data-max-items-to-display="${maxNumberToDisplay}"
	data-max-items-visible="${maxVisibleItems}"
	data-max-items-in-carousel="${maxNumberToDisplay}"
>
	<div class="carousel-header">
		<h2 class="base ellipsis padding-left" title="${title}">${title}</h2>
		<nav class="carousel-nav">
			<a class="btn-prev" href="#"><i></i></a>
			<a class="btn-next" href="#"><i></i></a>
		</nav>
	</div>

	<div class="carousel-teaser" 
		data-autoplay="${autoplay}" 
		data-timeout="${autoplayTimeout}" 
		data-direction="${autoplayDirection}" 	  
	>
		<%-- Items are loaded via AJAX --%>
	</div>
</section>

<script id="tmpl-carousel-teaser-item" type="text/x-template-dotjs">
<mod:carousel-teaser-item 
	template="product-dot-tpl" 
	skin="product-horizontal-box" 
	displayPromotionText="${displayPromotionText}"  
	attributes="data-product-id='{{= it.code }}' data-teasertrackingid='${wtTeaserTrackingId}.{{= it.code || 'x'}}.-' data-position='{{= it.itemPositionOneBased }}'"
/>
</script>