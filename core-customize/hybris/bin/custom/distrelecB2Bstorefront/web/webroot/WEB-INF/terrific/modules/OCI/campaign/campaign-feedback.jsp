<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- feedback text SearchResult_top (feedbackCampaign.feedbackTexts['SearchResult_top']) --%>
<spring:theme text="Unfortunately, your query" var="sMessage"/>
<c:set var="isMPNMatch" value="${isMPNMatch}" />
<c:choose>
	<c:when test="${isMPNMatch}">
		<c:if test="${not empty feedbackTextTop}">
			<c:if test=" ${!fn:contains(feedbackTextTop,sMessage)}">
				<div class="feedback-top">${feedbackTextTop}</div>
			</c:if>
		</c:if>
	</c:when>
	<c:when test="${not empty feedbackTextTop}">
	    <div class="feedback-top">${feedbackTextTop}</div>
	</c:when>
	<c:otherwise>
        <%-- fusion simulates fact-finder feedback campaign --%>
        <spring:theme code="product.list.no.result" text="Unfortunately, your query returned no relevant results:" var="sListNoResult" arguments="${request.getParameter('q')}" />
        <div class="feedback-top">
            <style>
                .relevancy-style {
                    background-color: #FFF3CD;
                    color: #856404;
                    padding: 10px;
                    width: 100%;
                    text-align: center;
                    margin-bottom: 20px;
            </style>
            <div class="relevancy-style" id="zero-category-search">${sListNoResult}</div>
        </div>
	</c:otherwise>
</c:choose>
<%-- carousel with pushedProducts (feedbackCampaign.pushedProducts) --%>
<c:if test="${not empty pushedProductsList}">

	<%-- on pageload loaded carousel --%>
	<mod:carousel-teaser
		layout="product"
		skin="product skin-carousel-teaser-feedback"
		carouselData="${pushedProductsList}"
		componentWidth="fullWidth"
		title=""
		autoplay="false"
		autoplayTimeout="0"
		autoplayDirection="left"
		displayPromotionText="false"
		maxNumberToDisplay="0"
		wtTeaserTrackingId="${fn:toLowerCase(currentCountry.isocode)}.faf-c-f"
	/>

</c:if>
