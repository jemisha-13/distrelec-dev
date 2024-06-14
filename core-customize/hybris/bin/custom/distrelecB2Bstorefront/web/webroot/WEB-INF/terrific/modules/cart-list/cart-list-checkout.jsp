<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Logic to decide which prices should be displayed --%>

<c:set var="showMyPrice" value="false" />
<c:set var="showListPrice" value="false" />
<c:set var="showCatalogQuantityError" value="false" />
<spring:eval expression="@configurationService.configuration.getString('sap.catalog.order.articles')" var="catalogIds" scope="application" />

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
<%-- Logic to decide which prices should be displayed --%>

<c:if test="${dangerousProducts != null}">
	<div class="dangerous-goods-warning">
		<spring:message code="cart.dangerousgoods.warning.message" arguments="${dangerousProducts}" argumentSeparator="���" />
	</div>
</c:if>

<c:if test="${phaseOutProducts != null}">
	<div class="dangerous-goods-warning">
		<spring:message code="basket.phaseout.quantity.reducedNumberOfItemsAdded.lowStock" arguments="${phaseOutProducts}" />
	</div>
</c:if>

<c:if test="${showCatalogQuantityError}">
	<div class="catalog-quantity-error">
		<spring:message code="sap.catalog.order.articles.error" />
	</div>
</c:if>

<spring:message code="product.shipping.available.leadTime.short" var="leadTimeText" text="More in {0} weeks" />

<div class="row cart-list-header">
	<div class="gu-1 cell cell-list"></div>
	<div class="gu-4 cell cell-info base"></div>
	<div class="gu-1 cell cell-availability">
		<c:if test="${empty EditCart or EditCart}">
			<p class="ellipsis" title="<spring:message code="cart.list.availability" />">
				<spring:message code="cart.list.availability" />
			</p>
		</c:if>
	</div>
	<div class="gu-2 cell cell-numeric">
		<p>
			<spring:message code="cart.list.quantity" />
		</p>
	</div>

	<c:if test="${showMyPrice and showListPrice}">
		<div class="gu-2 cell cell-price two-prices">
			<div class="price-row">
				<div class="price-cell">
					<p class="price ellipsis" title="<spring:message code="cart.list.my.single.price" />">
						<spring:message code="cart.list.my.single.price" />
					</p>
				</div>
				<div class="price-cell price-light">
					<p class="price ellipsis" title="<spring:message code="cart.list.list.single.price" />">
						<spring:message code="cart.list.list.single.price" />
					</p>
				</div>
			</div>
		</div>
		<div class="gu-2 cell cell-price two-prices">
			<div class="price-row">
				<div class="price-cell">
					<p class="price ellipsis" title="<spring:message code="cart.list.my.subtotal" />">
						<spring:message code="cart.list.my.subtotal" />
					</p>
				</div>
				<div class="price-cell price-light">
					<p class="price ellipsis" title="<spring:message code="cart.list.list.subtotal" />">
						<spring:message code="cart.list.list.subtotal" />
					</p>
				</div>
			</div>
		</div>
	</c:if>
	<c:if test="${not showMyPrice and showListPrice}">
		<div class="gu-2 cell cell-price">
			<div class="price-cell">
				<p class="price ellipsis" title="<spring:message code="cart.list.list.single.price" />">
					<spring:message code="cart.list.list.single.price" />
				</p>
			</div>
		</div>
		<div class="gu-2 cell cell-price">
			<div class="price-cell">
				<p class="price ellipsis" title="<spring:message code="cart.list.list.subtotal" />">
					<spring:message code="cart.list.list.subtotal" />
				</p>
			</div>
		</div>
	</c:if>
	<c:if test="${showMyPrice and not showListPrice}">
		<div class="gu-2 cell cell-price">
			<div class="price-cell">
				<p class="price ellipsis" title="<spring:message code="cart.list.my.single.price" />">
					<spring:message code="cart.list.my.single.price" />
				</p>
			</div>
		</div>
		<div class="gu-2 cell cell-price">
			<div class="price-cell">
				<p class="price ellipsis" title="<spring:message code="cart.list.my.subtotal" />">
					<spring:message code="cart.list.my.subtotal" />
				</p>
			</div>
		</div>
	</c:if>
</div>

<ul class="cart-list loading">
	<c:set var="currentQuote" value="X" />
	<c:forEach var="orderEntry" items="${cartData.entries}" varStatus="status">
		<c:set var="isBOM" value="${orderEntry.bom }" />
		<c:set var="BOMClass" value="${isBOM ? ' bom' : ''}" />
		<c:set var="isQuoteItem" value="${not empty orderEntry.quotationId}" /> <%-- For some reason orderEntry.isQuotation doesn't work --%>
		<c:set var="quoteClassString" value=" quote-item quote-${orderEntry.quotationId}" />
		<c:set var="quoteClass" value="${isQuoteItem ? quoteClassString : ''}" />
		<c:set var="isDummyItem" value="${orderEntry.dummyItem}" />
		<c:set var="dummyClass" value="${isDummyItem ? ' dummy-item' : ''}" />
		<c:if test="${isQuoteItem && currentQuote !=  orderEntry.quotationId}">
			<mod:cart-list-item template="quote-head" tag="li" orderEntry="${orderEntry}" htmlClasses="row quote-head quote-${orderEntry.quotationId}" isBOM="${isBOM}" />
			<c:set var="currentQuote" value="${orderEntry.quotationId}" />
		</c:if>
		<mod:cart-list-item tag="li" orderEntry="${orderEntry}" showMyPrice="${showMyPrice}" showListPrice="${showListPrice}" htmlClasses="row${BOMClass}${quoteClass}${dummyClass} quote-item-checkout-row-${isQuoteItem}" 
			isBOM="${isBOM}" isQuoteItem="${isQuoteItem}" isDummyItem="${isDummyItem}" position="${status.index + 1}" />
	</c:forEach>
</ul>

<!-- tmpl -->
<div class="hidden">
	<mod:cart-list-item template="dot-tmpl" />
</div>



<div class="hidden" id="tmpl-stock_level_pickup_header">
    <div class="row line"></div>
    <div class="row head-pickup">
        <h2><spring:message code="product.shipping.in.store.pickup" /></h2>
    </div>
</div>





<script id="tmpl-stock_level_pickup_row" type="text/x-dot-template">
    {{~it :item:id }}

    {{~}}
</script>

<script id="tmpl-stock_level" type="text/x-dot-template">

	{{? it.leadTimeErp !== undefined && it.leadTimeErp !== 0 }}
	  <div class="row leadtime" data-available-leadtime-text="${leadTimeText}" />
	{{?}}
</script>





<!-- end tmpl -->
