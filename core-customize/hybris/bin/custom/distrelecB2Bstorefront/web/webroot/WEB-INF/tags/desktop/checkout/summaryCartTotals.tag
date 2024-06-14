<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<script id="cartTotalsTemplate" type="text/x-jquery-tmpl">

	<dl class="order_totals">
		<dt><spring:theme code="basket.page.totals.subtotal"/></dt>
		<dd>{{= subTotal.formattedValue}}</dd>
		<dt class="savings"><spring:theme code="basket.page.totals.savings"/></dt>
		<dd class="savings">{{= totalDiscounts.formattedValue}}</dd>
		{{if deliveryCost}}
			<dt><spring:theme code="basket.page.totals.delivery"/></dt>
			<dd>
				{{if deliveryCost.value > 0}}
					{{= deliveryCost.formattedValue}}
				{{else}}
					<spring:theme code="basket.page.free"/>
				{{/if}}
			</dd>
		{{/if}}
		<dt class="total"><spring:theme code="basket.page.totals.total"/></dt>
		<dd class="total">{{= totalPrice.formattedValue}}</dd>
	</dl>
	{{if !net}}
		<p><spring:theme code="basket.page.totals.grossTax" arguments="{{= totalTax.formattedValue}}" argumentSeparator="!!!!" /></p>
	{{/if}}

</script>

<div class="item_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2><spring:theme code="order.order.totals" /></h2>
	</div>
	<div class="item_container" id="cart_totals_div">
		<dl class="order_totals">
			<dt><spring:theme code="basket.page.totals.subtotal"/></dt>
			<dd><format:price priceData="${cartData.subTotal}"/></dd>
			<dt class="savings"><spring:theme code="basket.page.totals.savings"/></dt>
			<dd class="savings"><format:price priceData="${cartData.totalDiscounts}"/></dd>
			<c:if test="${not empty cartData.deliveryCost}">
				<dt><spring:theme code="basket.page.totals.delivery"/></dt>
				<dd>
					<c:choose>
						<c:when test="${cartData.deliveryCost.value > 0}">
							<format:price priceData="${cartData.deliveryCost}"/>
						</c:when>
						<c:otherwise>
							<spring:theme code="basket.page.free"/>
						</c:otherwise>
					</c:choose>
				</dd>
			</c:if>
			<dt class="total"><spring:theme code="basket.page.totals.total"/></dt>
			<dd class="total"><ycommerce:testId code="checkout_totalPrice_label"><format:price priceData="${cartData.totalPrice}"/></ycommerce:testId></dd>
		</dl>
		<c:if test="${not cartData.net}">
			<p>
				<ycommerce:testId code="checkout_tax_label">
					<spring:theme code="basket.page.totals.grossTax" arguments="${cartData.totalTax.formattedValue}" argumentSeparator="!!!!" />
				</ycommerce:testId>
			</p>
		</c:if>
	</div>
</div>
