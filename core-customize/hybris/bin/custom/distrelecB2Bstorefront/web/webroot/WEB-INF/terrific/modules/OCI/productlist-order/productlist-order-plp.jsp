<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="orderlist-select-holder orderlist-select-holder--${currentCountry.isocode}">
    <label for="select-productlist-order"><spring:message code="text.sortby" /></label>
    <select id="select-productlist-order" name="productlist-order" class="selectpicker selectboxit-meta" data-pretext='<spring:message code="product.order.by" />: '>
        <c:forEach items="${searchPageData.sorting}" var="sortItem">
            <c:choose>
                <c:when test="${sortItem.relevanceSort}">
                    <option value="" <c:if test="${sortItem.selected}">selected</c:if>><spring:message code="product.order.relevance" /></option>
                </c:when>
                <c:otherwise>
                    <c:if test="${not empty sortItem.name and not empty sortItem.sortType.value}">

                        <option value="${sortItem.name}:${sortItem.sortType.value}" <c:if test="${sortItem.selected}">selected</c:if>>

                            <spring:message code="product.order.${fn:toLowerCase(sortItem.name)}.${sortItem.sortType.value}" text="${sortItem.name}: ${sortItem.sortType.value}" />

                        </option>

                    </c:if>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </select>
</div>