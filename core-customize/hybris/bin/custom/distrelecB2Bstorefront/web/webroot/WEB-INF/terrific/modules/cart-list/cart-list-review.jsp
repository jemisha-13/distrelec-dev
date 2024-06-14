<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="showMyPrice" value="false" />
<c:set var="showListPrice" value="false" />
<c:set var="showCatalogQuantityError" value="false" />

<spring:message code="cartlist.review.header.product" text="Product" var="sHeaderProduct" />
<spring:message code="cartlist.review.header.manufacturer" text="Manufacturer" var="sHeaderManufacturer" />
<spring:message code="cartlist.review.header.artNr" text="Art.Nr. / MPN" var="sHeaderArtNr" />
<spring:message code="cart.list.deliverydate" text="Delivery Date" var="sHeaderDelivery" />
<spring:message code="cart.directorder.qty" text="Qty" var="sHeaderQty" />
<spring:message code="cartlist.review.header.price" text="Price" var="sHeaderPrice" />
<spring:message code="cartlist.review.header.subTotal" text="Sub-Total" var="sHeaderSubTotal" />

<c:forEach var="orderEntry" items="${cartData.entries}">
	<c:choose>
		<c:when test="${empty orderEntry.baseListPrice }">
			<%-- Only show My Price --%>
			<c:if test="${not showMyPrice}">
				<c:set var="showMyPrice" value="true" />
			</c:if>
			<c:if test="${not showListPrice}">
				<c:set var="showListPrice" value="false" />
			</c:if>
		</c:when>
		<c:when test="${orderEntry.baseListPrice.value eq orderEntry.basePrice.value}">
			<%-- Only show List Price --%>
			<c:if test="${not showMyPrice}">
				<c:set var="showMyPrice" value="false" />
			</c:if>
			<c:if test="${not showListPrice}">
				<c:set var="showListPrice" value="true" />
			</c:if>
		</c:when>
		<c:when test="${orderEntry.baseListPrice.value lt orderEntry.basePrice.value}">
			<%-- only show My Price Price --%>
			<c:if test="${not showMyPrice}">
				<c:set var="showMyPrice" value="true" />
			</c:if>
			<c:if test="${not showListPrice}">
				<c:set var="showListPrice" value="false" />
			</c:if>
		</c:when>
		<c:when test="${orderEntry.baseListPrice.value gt orderEntry.basePrice.value}">
			<%-- show Both Prices --%>
			<c:if test="${not showMyPrice}">
				<c:set var="showMyPrice" value="true" />
			</c:if>
			<c:if test="${not showListPrice}">
				<c:set var="showListPrice" value="true" />
			</c:if>
		</c:when>
		<c:otherwise>
			<%-- Only show List Price --%>
			<c:if test="${not showMyPrice}">
				<c:set var="showMyPrice" value="false" />
			</c:if>
			<c:if test="${not showListPrice}">
				<c:set var="showListPrice" value="true" />
			</c:if>
		</c:otherwise>
	</c:choose>

	<c:if test="${fn:contains(catalogIds,orderEntry.product.codeErpRelevant) && orderEntry.quantity > 1}" >
		<c:set var="showCatalogQuantityError" value="true" />
	</c:if>
</c:forEach>

<div class="cart-header row">
	<div class="cart-header__item col-12 col-lg-1">&nbsp;</div>
	<div class="cart-header__item col-12 col-lg-2">${sHeaderProduct}</div>
	<div class="cart-header__item col-12 col-lg-2">${sHeaderManufacturer}</div>
	<div class="cart-header__item col-12 col-lg-2">${sHeaderArtNr}</div>
	<div class="cart-header__item col-12 col-lg-2">${sHeaderDelivery}</div>
	<div class="cart-header__item col-12 col-lg-1">${sHeaderQty}</div>
	<div class="cart-header__item col-12 col-lg-1">${sHeaderPrice}</div>
	<div class="cart-header__item col-12 col-lg-1">${sHeaderSubTotal}</div>
</div>

<ul class="cart-list loading">
	<c:forEach var="orderEntry" items="${cartData.entries}">
		<c:set var="isBOM" value="${orderEntry.bom}" />
		<c:set var="BOMClass" value="${isBOM ? 'bom' : ''}" />
		<c:set var="isQuoteItem" value="${not empty orderEntry.quotationId}" /> <%-- For some reason orderEntry.isQuotation doesn't work --%>
		<c:set var="quoteClassString" value=" quote-item quote-${orderEntry.quotationId}" />
		<c:set var="quoteClass" value="${isQuoteItem ? quoteClassString : ''}" />
		<c:set var="isDummyItem" value="${orderEntry.dummyItem}" />
		<c:set var="dummyClass" value="${isDummyItem ? ' dummy-item' : ''}" />

		<c:if test="${isQuoteItem && currentQuote !=  orderEntry.quotationId}">
			<mod:cart-list-item template="quote-head" tag="li" orderEntry="${orderEntry}" htmlClasses="row quote-head quote-head-review quote-${orderEntry.quotationId}" isBOM="${isBOM}" />
			<c:set var="currentQuote" value="${orderEntry.quotationId}" />
		</c:if>

		<mod:cart-list-item template="review" skin="review" tag="li" htmlClasses="${BOMClass}${quoteClass}${dummyClass} quote-item-review-row-${isQuoteItem} row" orderEntry="${orderEntry}" showMyPrice="${showMyPrice}"
			showListPrice="${showListPrice}" isBOM="${isBOM}" isQuoteItem="${isQuoteItem}" isDummyItem="${isDummyItem}" position="${status.index + 1}"/>

	</c:forEach>
</ul>
