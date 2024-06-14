<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<%-- feedback text delivered via Ajax --%>
<div class="feedback-top">${feedbackTextTop}</div>

<%-- CarouselData is delivered later via AJAX, if there is a page-specific FF campaign --%>
<mod:carousel-teaser
	template="campaign"
	skin="campaign"
	htmlClasses="loading"
	componentWidth="fullWidth"
	title=""
	autoplay="false"
	autoplayTimeout="0"
	autoplayDirection="left"
	displayPromotionText="false"
	maxNumberToDisplay="0"
	wtTeaserTrackingId="${fn:toLowerCase(currentCountry.isocode)}.faf-c-f"
/>
