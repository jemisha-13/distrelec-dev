<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="visibleItems" value="0"/>
<c:set var="maxVisibleItemsFullWidth" value="3"/>
<c:set var="maxVisibleItemsTwoThird" value="2"/>

<c:set var="maxVisibleItems" value="4"/>

<c:set var="ajaxURL" value="/accessories"/>

<section class="carousel-teaser-container cf specialVerticalWidth accessories" 
	data-ajax-url="${ajaxURL}" 
	data-max-items-to-display="0"
	data-max-items-visible="3"
	data-max-items-in-carousel="0"
>

	<div class="carousel-header">
		<h3 class="base ellipsis padding-left" title="${title}">${title}</h3>
	</div>
	
	<nav class="carousel-nav vertical">
		<a class="btn-next" href="#"><i></i></a>
		<a class="btn-prev" href="#"><i></i></a>
	</nav>	

	<div class="carousel-teaser" 
		data-autoplay="false" 
		data-timeout="" 
		data-direction="up" 	  
	>
		<%-- Items are loaded via AJAX --%>
	</div>
	
	
</section>

<script id="tmpl-carousel-teaser-item" type="text/x-template-dotjs">
<mod:carousel-teaser-item 
	template="product-dot-tpl-vertical" 
	skin="product-vertical-box"   
	displayPromotionText="${displayPromotionText}"  
	attributes="data-product-id='{{= it.code }}' data-teasertrackingid='${wtTeaserTrackingId}.{{= it.code || 'x'}}.-' data-position='{{= it.itemPositionOneBased }}'"
/>
</script>