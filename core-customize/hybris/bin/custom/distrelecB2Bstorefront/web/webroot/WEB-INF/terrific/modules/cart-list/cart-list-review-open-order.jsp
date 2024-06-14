<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>


<div class="row cart-list-header">
	<div class="gu-1 cell cell-list"></div> 
	<div class="gu-3 cell cell-info base"></div>
	<div class="gu-2 cell cell-availability">
		<p><spring:message code="cart.list.deliverydate" /></p>
	</div>	
	<div class="gu-2 cell cell-numeric">
	    <p><spring:message code="cart.list.total.quantity" /></p>
	</div>
	<div class="gu-2 cell cell-price"> 
		<div class="price-cell">
			<p class="price ellipsis" title="<spring:message code="cart.list.my.single.price" />"><spring:message code="cart.list.my.single.price" /></p>
		</div>
	</div>
	<div class="gu-2 cell cell-price">	
		<div class="price-cell">
			<p class="price ellipsis" title="<spring:message code="cart.list.my.subtotal" />" ><spring:message code="cart.list.my.subtotal" /></p>
		</div>
	</div>
</div>

<ul class="cart-list loading">
	<c:forEach var="orderEntry" items="${cartItemList}">
		<c:set var="isDummyItem" value="${orderEntry.dummyItem}" />
		<mod:cart-list-item template="review" skin="review" tag="li" htmlClasses="row${isDummyItem ? ' dummy-item' : '' }" orderEntry="${orderEntry}" isDummyItem="${isDummyItem}" />
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