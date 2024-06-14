<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="form.global.error.erpcommunication" var="sError" text="An error occured" />

<div class="row cart-list-header">
	<div class="gu-5 cell cell-info base"></div>
	<div class="gu-2 cell cell-date">
	    <c:if test="${orderData.status.code ne 'ERP_STATUS_CANCELLED'}">
	    	<p><spring:message code="cart.list.availability" /></p>
	    </c:if>
	</div>
	<div class="gu-1 cell cell-numeric">
	    <p><spring:message code="cart.list.quantity" />&nbsp;*</p>
	</div>
	<div class="gu-2 cell cell-price">
		<div class="price-cell">
			<p class="price ellipsis padding-left" title="<spring:message code="cart.list.my.single.price" />"><spring:message code="cart.list.my.single.price" /></p>
		</div>
	</div>
	<div class="gu-2 cell cell-price">	
		<div class="price-cell">
			<p class="price ellipsis" title="<spring:message code="cart.list.my.subtotal" />" ><spring:message code="cart.list.my.subtotal" /></p>
		</div>
	</div>
</div>


<ul class="cart-list loading" data-quote-id="${quotationData.quotationId}" data-error-text="${sError}">
	<c:forEach var="quotationEntry" items="${quotationData.quotationEntries}">
		<c:set var="isDummyItem" value="${quotationEntry.dummyItem}" />
		<mod:cart-list-item template="quote-detail" skin="quote-detail" tag="li" htmlClasses="row${isDummyItem ? ' dummy-item' : '' }" 
			quoteStatus="${quotationData.status.code}" quoteEntry="${quotationEntry}" isDummyItem="${isDummyItem}" 
			quotationData="${quotationData}"/>
	</c:forEach>
</ul>

<!-- tmpl -->
<div class="hidden">
	<mod:cart-list-item template="dot-tmpl" />
</div>

<div class="hidden" id="tmpl-stock_level_pickup_header">
	<div class="row line"></div>
	<div class="row">
		<h2><spring:message code="product.shipping.in.store.pickup" /></h2>
	</div>
</div>

<script id="tmpl-stock_level_pickup_row" type="text/x-dot-template">
	{{~it :item:id }}
	<div class="row">
		<div class="left">{{= item.warehouseName}}</div>
		<div class="right">{{= item.stockLevel}}</div>
	</div>
	{{~}}
</script>

<script id="tmpl-stock_level" type="text/x-dot-template">
	<div class="row">
		<h2><spring:message code="product.shipping" /></h2>
	</div>
	<div class="row">
		<div class="left">24h</div>
		<div class="right">{{= it.stockLevel24h}}</div>
	</div>
	<div class="row">
		<div class="left">< 10 d</div>
		<div class="right">{{= it.stockLevel48h}}</div>
	</div>
	<div class="row">
		<div class="left">> 10 d{{= it.backorderQuantityFormated}}</div>
		<div class="right">{{= it.backorderQuantity}}</div>
	</div>
</script>
<!-- end tmpl -->


