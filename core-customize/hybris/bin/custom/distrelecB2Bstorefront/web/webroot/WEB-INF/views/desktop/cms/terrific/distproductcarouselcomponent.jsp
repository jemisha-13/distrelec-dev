<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="false"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<c:if test="${not empty productCarouselData}">
	<mod:carousel-teaser
		layout="product"
		skin="product-box-horizontal"
		carouselData="${productCarouselData}"
		componentWidth="${component.componentWidth.code}" 
		title="${component.title}"
		autoplay="${autoplay}" 
		autoplayTimeout="${component.autoplayTimeout}"
		autoplayDirection="${component.autoplayDirection.code}"
		displayPromotionText="${component.displayPromotionText}"
		maxNumberToDisplay="${component.maxNumberToDisplay}"
		wtTeaserTrackingId="${wtTeaserTrackingId}"
	/>
</c:if>
