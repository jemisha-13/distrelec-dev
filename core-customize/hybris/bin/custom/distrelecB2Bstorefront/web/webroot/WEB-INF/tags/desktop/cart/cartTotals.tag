<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<div class="item_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
			<span></span>
			</div>
		</div>
		<h2><spring:theme code="order.order.totals" /></h2>
	</div>
	<div class="item_container">
		<dl class="order_totals">
			<dt><spring:theme code="basket.page.totals.subtotal"/></dt>
			<dd><format:price priceData="${cartData.subTotal}"/></dd>
			<c:if test="${cartData.orderDiscounts.value > 0}">
				<dt class="savings"><spring:theme code="basket.page.totals.savings"/></dt>
				<dd class="savings"><format:price priceData="${cartData.orderDiscounts}"/></dd>
			</c:if>
			<c:if test="${not empty cartData.deliveryCost}">
				<dt><spring:theme code="basket.page.totals.delivery"/></dt>
				<dd>
					<format:price priceData="${cartData.deliveryCost}" displayFreeForZero="TRUE"/>
				</dd>
			</c:if>

			<c:if test="${cartData.net}">
				<dt class="total"><spring:theme code="basket.page.totals.netTax"/></dt>
				<dd class="total"><format:price priceData="${cartData.totalTax}"/></dd>
			</c:if>

			<dt class="total"><spring:theme code="basket.page.totals.total"/></dt>
			<dd class="total">
				<ycommerce:testId code="cart_totalPrice_label">
					<format:price priceData="${cartData.totalPrice}"/>
				</ycommerce:testId>
			</dd>
		</dl>

		<c:if test="${not cartData.net}">
			<ycommerce:testId code="cart_taxes_label">
				<p><spring:theme code="basket.page.totals.grossTax" arguments="${cartData.totalTax.formattedValue}" argumentSeparator="!!!!" /></p>
			</ycommerce:testId>
		</c:if>
	</div>
</div>
