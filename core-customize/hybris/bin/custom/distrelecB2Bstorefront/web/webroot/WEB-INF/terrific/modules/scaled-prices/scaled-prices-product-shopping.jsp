<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty product.volumePricesMap and !product.catPlusItem}">
    <c:set var="yourPriceDiffers" value="${false}"/>
    <c:set var="hasCustomPrices" value="${false}"/>
    <c:forEach items="${product.volumePricesMap}" var="volumePrice" begin="0" end="1">
        <c:if test="${not empty volumePrice.value.custom}">
            <c:set var="hasCustomPrices" value="${true}"/>
            <c:set var="yourPriceDiffers" value="${volumePrice.value.list.value gt volumePrice.value.custom.value}"/>
        </c:if>
    </c:forEach>
    <c:set var="priceScale" value=""/>
    <c:forEach items="${product.volumePricesMap}" var="entry">
        <c:set var="priceScale" value="${priceScale};${entry.key}"/>
    </c:forEach>
    <div class="shoppinglist" data-price-scale="${priceScale}">
        <mod:scaled-prices product="${product}" skin="single" template="single"/>
    </div>
</c:if>

