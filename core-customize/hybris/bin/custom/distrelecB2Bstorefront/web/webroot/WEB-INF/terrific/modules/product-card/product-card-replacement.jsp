<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<h3 class="base ellipsis">${title} &nbsp</h3>

<%-- Subtract one because loop counter var is zero based --%>
<c:set var="toDisplay" value="${fn:length(carouselData)}" />

<section class="replacement-holder">
    <c:forEach items="${carouselData}" var="itemData" end="${toDisplay}" varStatus="status">
        <c:choose>
            <c:when test="${!fn:contains(itemData, 'B2BProductData')}">
                <mod:product-card-item
                        displayPromotionText=""
                        showLogo="${showLogo}"
                        itemData="${itemData.product}"
                        position="${status.index + 1}"
                        cardTitle="Alternative"
                />
            </c:when>
            <c:otherwise>
                <mod:product-card-item
                        displayPromotionText=""
                        showLogo="${showLogo}"
                        itemData="${itemData}"
                        cardTitle="Alternative"
                        position="${status.index + 1}"
                />
            </c:otherwise>
        </c:choose>
    </c:forEach>
</section>