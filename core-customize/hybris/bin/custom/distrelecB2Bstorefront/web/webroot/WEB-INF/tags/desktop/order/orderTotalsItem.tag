<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<%@ attribute name="containerCSS" required="false" type="java.lang.String" %>

<div class="item_container_holder ${containerCSS}">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2><spring:theme code="text.account.order.orderTotals" text="Order Totals"/></h2>
	</div>
	<div class="item_container">
		<dl class="order_totals">
			<dt><spring:theme code="text.account.order.subtotal" text="Subtotal:"/></dt>
			<dd><format:price priceData="${order.subTotal}"/></dd>
			<c:if test="${order.orderDiscounts.value > 0}">
				<dt class="savings"><spring:theme code="text.account.order.savings" text="Savings:"/></dt>
				<dd class="savings"><format:price priceData="${order.orderDiscounts}"/></dd>
			</c:if>
			<dt><spring:theme code="text.account.order.delivery" text="Delivery:"/></dt>
			<dd>
				<format:price priceData="${order.deliveryCost}" displayFreeForZero="true"/>
			</dd>
			<dt class="total"><spring:theme code="text.account.order.total" text="Total:"/></dt>
			<dd class="total"><format:price priceData="${order.totalPrice}"/></dd>
		</dl>
		<p><spring:theme code="text.account.order.includesTax" text="Your order includes {0} tax" arguments="${order.totalTax.formattedValue}" argumentSeparator="###"/></p>
	</div>
</div>