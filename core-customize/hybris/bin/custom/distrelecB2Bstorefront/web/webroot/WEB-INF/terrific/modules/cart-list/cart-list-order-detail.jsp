<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:set var="isPendingLabel" value="false"/>
<c:forEach var="orderEntry" items="${orderData.entries}">
	<c:set var="pendingQuantityLabel" value="0" />

	<c:if test="${not empty orderEntry.availabilities}">
		<c:forEach items="${orderEntry.availabilities}" var="avail">
			<c:set var="pendingQuantityLabel" value="${pendingQuantityLabel + avail.quantity}"/>
		</c:forEach>
	</c:if>

	<c:choose>
		<c:when test="${orderData.openOrder}">
			<c:set var="isQuantityLabel" value="true"/>
		</c:when>
		<c:otherwise>
			<c:if test="${not empty orderEntry.deliveryDate}">
				<c:set var="isQuantityLabel" value="true"/>
			</c:if>
			<c:if test="${pendingQuantityLabel gt 0}">
				<c:forEach items="${orderEntry.availabilities}" var="avail">
					<c:set var="isPendingLabel" value="true"/>
				</c:forEach>
			</c:if>
		</c:otherwise>
	</c:choose>
</c:forEach>

<div class="row cart-list-header">
	<div class="gu-5 cell cell-info base"></div>
	<div class="gu-2 cell cell-date">
	    <c:if test="${currentSalesOrg.erpSystem eq 'SAP' and orderData.status.code ne 'ERP_STATUS_CANCELLED'}">
	    	<p><spring:message code="cart.list.deliverydate" /></p>
	    </c:if>
	</div>
	<div class="gu-1 cell cell-numeric">
	    <p>
			<c:choose>
				<c:when test="${(isQuantityLabel && isPendingLabel) || (not isQuantityLabel && not isPendingLabel)}">
					<spring:message code="cart.list.quantity" /><br>(<spring:message code="orderOverviewBox.state.PENDING" />)
				</c:when>

				<c:otherwise>
					<c:if test="${isQuantityLabel}">
						<spring:message code="cart.list.quantity" />
					</c:if>

					<c:if test="${isPendingLabel}">
						<spring:message code="orderOverviewBox.state.PENDING" />
					</c:if>
				</c:otherwise>
			</c:choose>
		</p>
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
	<c:forEach var="orderEntry" items="${orderData.entries}">
		<c:set var="isDummyItem" value="${orderEntry.dummyItem}" />
		<mod:cart-list-item template="order-detail" skin="order-detail" tag="li" htmlClasses="row${isDummyItem ? ' dummy-item' : '' }" orderEntry="${orderEntry}" isOpenOrder="${orderData.openOrder}" isDummyItem="${isDummyItem}" />
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