<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${not empty product.volumePricesMap and !product.catPlusItem}">
	<c:set var="yourPriceDiffers" value="${false}" />
	<c:set var="hasCustomPrices" value="${false}" />
	<c:forEach items="${product.volumePricesMap}" var="volumePrice" begin="0" end="1">
		<c:if test="${not empty volumePrice.value.custom}">
			<c:set var="hasCustomPrices" value="${true}" />
			<c:set var="yourPriceDiffers" value="${volumePrice.value.list.value gt volumePrice.value.custom.value}" />
		</c:if>
	</c:forEach>
	<div class="table">
    <%-- Technical Design accept only 1 row --%>
    <c:forEach items="${product.volumePricesMap}" var="entry" end="0">
        <div class="table-row">
        	<div class="table-cell">
				<span class="label">${entry.key} +</span>
				<span class="value">
				<c:choose>
					<c:when test="${yourPriceDiffers}">
						<format:price format="default" priceData="${entry.value.custom}" />
					</c:when>
					<c:when test="${hasCustomPrices}">
						<format:price format="default" priceData="${entry.value.custom}" />
					</c:when>
					<c:otherwise>
						<format:price format="default" priceData="${entry.value.list}" />
					</c:otherwise>
				</c:choose>
				</span>
			</div>
        </div>
    </c:forEach>
	</div>
</c:if>