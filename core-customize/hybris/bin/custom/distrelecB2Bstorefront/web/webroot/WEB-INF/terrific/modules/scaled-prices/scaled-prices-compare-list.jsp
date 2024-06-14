<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<c:if test="${not empty product.volumePricesMap}">
	<c:set var="hasCustomPrices" value="${false}" />
	<c:forEach items="${product.volumePricesMap}" var="volumePrice" begin="0" end="1">
		<c:if test="${not empty volumePrice.value.custom}">
			<c:set var="hasCustomPrices" value="${true}" />
		</c:if>		
	</c:forEach>
	<c:choose>
        <c:when test="${hasCustomPrices}">
            <div class="tableGrid__price__wrap">
                <div class="tableGrid__price__wrap__title"><spring:message code="product.scaledPrices.your.price" /></div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row head col-sm-6">
                <div class="td nth2 text-header"><spring:message code="product.scaledPrices.list.price" /></div>
            </div>
        </c:otherwise>
    </c:choose>

	<div class="tableGrid__price__wrap">
	<c:forEach items="${product.volumePricesMap}" var="entry" end="3">
		<div class="tableGrid__price__wrap__items">
			<c:choose>
				<c:when test="${hasCustomPrices}">
					<div class="tableGrid__price__wrap__items__item test">
						<format:price format="default" priceData="${entry.value.custom}" />
					</div>
					<div class="td nth3"></div>
				</c:when>
				<c:otherwise>
					<div class="tableGrid__price__wrap__items__item">
						<format:price format="default" priceData="${entry.value.list}" />
					</div>
					<div class="td nth3"></div>
				</c:otherwise>
			</c:choose>
			<div class="tableGrid__price__wrap__items__item">${entry.key}&nbsp;+</div>
		</div>
	</c:forEach>
	</div>
</c:if>