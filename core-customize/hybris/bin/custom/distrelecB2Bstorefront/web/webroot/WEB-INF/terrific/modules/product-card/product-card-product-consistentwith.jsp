<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="plp.helpprompts.facetlistactionbar.detailview.label1" var="viewMoreProductsText"/>


<div class="card-product-holder">
    <h3 class="base ellipsis">${title} &nbsp</h3>

    <%-- Subtract one because loop counter var is zero based --%>
    <c:set var="toDisplay" value="${fn:length(carouselData)}" />

    <section class="consistentwith">
        <div class="row toggle-container">

            <c:forEach items="${carouselData}" var="itemData" end="${toDisplay}" varStatus="status">

                <c:if test = "${status.count < 9}">
                    <mod:product-card-item
                    displayPromotionText=""
                    showLogo="${showLogo}"
                    itemData="${itemData}"
                    htmlClasses="col-12 col-sm-6 col-lg-3"
                    position="${status.index + 1}"
                    cardTitle="Consistent with"
                    />
                </c:if>

            </c:forEach>

            <c:if test = "${toDisplay > 8}">
                <div class="col-12">
                    <button  class="mat-button mat-button--action-green toggle-container__buttton" >${viewMoreProductsText}</button>
                </div>
            </c:if>

            <c:forEach items="${carouselData}" var="itemData" end="${toDisplay}" varStatus="status">

                <c:if test = "${status.count > 8}">

                    <mod:product-card-item
                    displayPromotionText=""
                    showLogo="${showLogo}"
                    itemData="${itemData}"
                    htmlClasses="col-12 col-sm-6 col-lg-3 toggle-container__content hidden"
                    position="${status.index + 1}"
                    cardTitle="Consistent with"
                    />

                </c:if>

            </c:forEach>

        </div>
    </section>
</div>