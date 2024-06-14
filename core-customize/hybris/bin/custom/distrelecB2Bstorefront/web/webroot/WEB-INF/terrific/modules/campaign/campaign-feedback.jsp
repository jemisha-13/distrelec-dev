<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:theme text="Unfortunately, your query" var="sMessage"/>
<c:set var="isMPNMatch" value="${isMPNMatch}"/>
<c:choose>
    <c:when test="${isMPNMatch}">
        <c:if test="${not empty feedbackTextTop}">
            <c:if test=" ${!fn:contains(feedbackTextTop,sMessage)}">
                <div class="feedback-top">${feedbackTextTop}</div>
            </c:if>
        </c:if>
    </c:when>
    <c:otherwise>
        <c:if test="${not empty feedbackTextTop}">
            <div class="feedback-top">${feedbackTextTop}</div>
        </c:if>
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
