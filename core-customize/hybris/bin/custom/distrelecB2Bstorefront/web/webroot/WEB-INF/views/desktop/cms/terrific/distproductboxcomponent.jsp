<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="false"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- DistProductBoxComponent -->

<c:if test="${not empty productBoxData}">
	<c:choose>
 		<c:when test="${productBoxData.checkout && productBoxData.horizontal}"> 
			<mod:carousel-teaser
				template="product-box-horizontal"
				skin="product-box-horizontal-checkout"
				carouselData="${productBoxData.items}"
				htmlClasses=""
				componentWidth="960px" 
				title="${productBoxData.title}"  
				autoplay="${productBoxData.rotating}"  
				autoplayTimeout="4"
				autoplayDirection=""
				displayPromotionText="false"
				maxNumberToDisplay="4"
				showLogo="${productBoxData.showLogo}"
				wtTeaserTrackingId="${wtTeaserTrackingId}"
				isCheckout="${productBoxData.checkout}"  
			/>		
 		</c:when> 
		<c:when test="${productBoxData.horizontal}">
			<mod:carousel-teaser
				template="product-box-horizontal"
				skin="product-box-horizontal"
				carouselData="${productBoxData.items}"
				htmlClasses=""
				componentWidth="660px" 
				title="${productBoxData.title}"  
				autoplay="${productBoxData.rotating}"  
				autoplayTimeout="4"
				autoplayDirection=""
				displayPromotionText="false"
				maxNumberToDisplay="2"
				showLogo="${productBoxData.showLogo}"
				wtTeaserTrackingId="${wtTeaserTrackingId}"
			/>			
		</c:when>
		<c:otherwise>
			<mod:carousel-teaser
				template="product-box-vertical"
				skin="product-box-vertical" 
				carouselData="${productBoxData.items}"
				htmlClasses=""
				componentWidth="660px" 
				title="${productBoxData.title}"  
				autoplay="${productBoxData.rotating}"  
				autoplayTimeout="4"
				autoplayDirection="down"
				displayPromotionText="false"
				maxNumberToDisplay="2"
				showLogo="${productBoxData.showLogo}"
				wtTeaserTrackingId="${wtTeaserTrackingId}"
			/>	
		</c:otherwise>
	</c:choose>
</c:if>