<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="product.order.by" var="sOrderByTitle" />
<spring:message code="base.close" var="sCloseBtn" text="Close" />
<spring:message code="productlist.order.sortTitle" var="sSortTitle" text="Sort" />

<div class="orderlist-select-holder">
    <div class="orderlist-select-holder__header">
        <div class="orderlist-select-holder__header__text">
		<span class="matched-products-count">
			${sOrderByTitle}
		</span>
        </div>
        <div class="orderlist-select-holder__header__close">
		<span>
            ${sCloseBtn}
        </span>
        </div>
    </div>
    <div id="select-productlist-order" class="orderlist-select-holder__content" data-pretext='<spring:message code="product.order.by" />: '>
        <c:forEach items="${searchPageData.sorting}" var="sortItem">
            <c:choose>
                <c:when test="${sortItem.relevanceSort}">
                    <span data-order-value="" class="<c:if test="${sortItem.selected}">selected</c:if>">
                        <spring:message code="product.order.relevance" />
                    </span>
                </c:when>
                <c:otherwise>
                    <c:if test="${not empty sortItem.name and not empty sortItem.sortType.value}">

                        <span class="<c:if test="${sortItem.selected}">selected</c:if>" data-order-value="${sortItem.name}:${sortItem.sortType.value}">
                            <spring:message code="product.order.${fn:toLowerCase(sortItem.name)}.${sortItem.sortType.value}" text="${sortItem.name}: ${sortItem.sortType.value}" />
                        </span>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </div>
    <div class="orderlist-select-holder__footer">
        <span class="mat-button mat-button--action-green submit-btn">
            ${sSortTitle}
        </span>
    </div>
</div>