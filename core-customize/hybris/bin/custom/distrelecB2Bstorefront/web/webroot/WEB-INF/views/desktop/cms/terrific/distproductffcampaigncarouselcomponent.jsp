<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="false"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- CarouselData is delivered later via AJAX --%>
<mod:carousel-teaser
	template="product"
	skin="product"
	carouselData="${productCarouselData}"
	htmlClasses="loading"
	componentWidth="${component.componentWidth.code}" 
	title="${component.title}"
	autoplay="${autoplay}" 
	autoplayTimeout="${component.autoplayTimeout}"
	autoplayDirection="${component.autoplayDirection.code}"
	displayPromotionText="${component.displayPromotionText}"
	maxNumberToDisplay="${component.maxNumberToDisplay}"
	wtTeaserTrackingId="${wtTeaserTrackingId}"
/>
