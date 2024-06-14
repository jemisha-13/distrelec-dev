<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<section class="carousel-teaser-container2 cf width300px">
	<div class="carousel-header${fn:length(carouselData) le visibleItems ? ' no-nav-buttons' : ''}">
		<h3 class="base ellipsis">${title} &nbsp</h3>
		
		<c:if test="${fn:length(carouselData) > 3}">
			<nav class="carousel-nav hidden carousel-nav-product-box-vertical">
				<a class="btn-prev" href="#"><i></i></a>
				<a class="btn-next" href="#"><i></i></a>
			</nav>
		</c:if>



	<div id="specialSeparator" class="hidden">
	</div>

	</div>
	
	<%-- Subtract one because loop counter var is zero based --%>
	<c:set var="toDisplay" value="${fn:length(carouselData)}" />

	<div class="carousel-teaser" data-autoplay="${autoplay}" data-timeout="${autoplayTimeout}" data-direction="${autoplayDirection}" data-items-visible="4">

		<c:forEach items="${carouselData}" var="itemData" end="${toDisplay}" varStatus="status">

			<c:choose>
				<c:when test="${!fn:contains(itemData, 'B2BProductData')}">
					<mod:carousel-teaser-item 
 						template="product-box"  
 						skin="product-vertical-box"  
 						displayPromotionText="" 
 						showLogo="${showLogo}" 
 						itemData="${itemData.product}" 
 		 			/> 				 
 				</c:when> 
 				<c:otherwise> 
 					<mod:carousel-teaser-item  
 						template="product-box"  
 						skin="product-vertical-box"  
 						displayPromotionText="" 
 						showLogo="${showLogo}" 
 						itemData="${itemData}" 
 		 			/> 	
 				</c:otherwise>  
 			</c:choose> 
			
 		</c:forEach> 


	</div>
</section>
