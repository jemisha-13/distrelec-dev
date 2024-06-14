<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>



<section class="carousel-teaser-container cf fullWidth">
	<div class="carousel-header${fn:length(carouselData) le visibleItems ? ' no-nav-buttons' : ''}">
		<h3 class="base ellipsis padding-left" title="${title}">${title}</h3>
		
		<c:if test="${fn:length(carouselData) > 2}">
			<nav class="carousel-nav">
				<a class="btn-prev" href="#"><i></i></a>
				<a class="btn-next" href="#"><i></i></a>
			</nav>
		</c:if>
	</div>
	<%-- Subtract one because loop counter var is zero based --%>
	<c:set var="toDisplay" value="${fn:length(carouselData)}" />

	<div class="carousel-teaser" data-autoplay="${autoplay}" data-timeout="${autoplayTimeout}" data-direction="left" data-items-visible="3">
		<c:forEach items="${carouselData}" var="itemData" end="${toDisplay}" varStatus="status">
		
			<mod:carousel-teaser-item 
				template="product-box" 
				skin="product-horizontal-box" 
				showLogo="${showLogo}"
				displayPromotionText=""  
				itemData="${itemData.product}"
 			/> 	
			
		</c:forEach>
	</div>

	<c:if test="${isCheckout}"> 
		<a href="#" class="close-link">  <spring:message code='toolsitem.share.close' />  </a> - 
		<a href="#" class="never-again"> Never show this again </a>
	</c:if>
</section>
