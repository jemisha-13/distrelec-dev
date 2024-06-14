<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="cart.pricecalcbox.title" text="Your Cart" var="sTitle" />
<spring:message code="cart.pricecalcbox.subtotal" text="Articles" var="sSubtotal" />
<spring:message code="cart.pricecalcbox.voucher" text="Voucher" var="sVoucher" arguments="${cartData.erpVoucherInfoData.code}" />
<spring:message code="cart.pricecalcbox.tax" text="Tax" var="sTax" />
<spring:message code="cart.pricecalcbox.delivery.cost" text="Delivery Cost" var="sDeliveryCost" />
<spring:message code="cart.pricecalcbox.discount" text="Discount" var="sDiscount" />
<spring:message code="cart.pricecalcbox.total.payable" text="Total Payable" var="sTotalPayable" />
<spring:message code="cart.pricecalcbox.termsAndCond" text="Terms and Conditions" var="sTermsAndCond" />
<spring:message code="cart.pricecalcbox.paymentMethod" text="Payment method:" var="sPaymentMethod" />
<spring:message code="cart.pricecalcbox.billingAddress" text="Billing address:" var="sBillingAddress" />
<spring:message code="cart.pricecalcbox.newsletter" text="Newsletter" var="sNewsletter" />
<spring:message code="cart.pricecalcbox.acceptTerms" text="Yes, I have read and hereby accept the <a href='#'>terms and conditions of Distrelec</a>" var="sAcceptTerms" />
<spring:message code="cart.pricecalcbox.acceptNewsletter" text="Yes, I want to receive the Distrelec newsletter" var="sAcceptNewsletter" />
<spring:message code="checkout.button.checkout" text="Continue Checkout" var="sCheckout" />
<spring:message code="checkout.button.checkout.openOrder" text="Save this open order" var="sCheckoutOpenOrder" />
<spring:message code="checkout.button.checkout.openOrderClose" text="Add products and close this open order" var="sCheckoutOpenOrderClose" />
<spring:message code="cart.pricecalcbox.discount" text="Discount" var="sDiscount" />


<c:set var="cartData" value="${cartData != null ? cartData : orderData}" />

<c:if test="${not empty cartData.discounts}">
	<c:set var="discountPercentage" value="${cartData.discounts[1].value}" />
</c:if>

<div class="table">
	<dl class="table__line">
		<dt class="table__line__heading">${sSubtotal}:</dt>
		<dd class="table__line__value">
			<format:price format="currency" priceData="${cartData.subTotal}" fallBackCurrency="${cartData.totalPrice.currencyIso}" />
			&nbsp;
			<format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.subTotal}" />
		</dd>
	</dl>
	<c:if test="${not empty cartData.deliveryMode}">
		<dl class="table__line table__line--delivery">
			<dt class="table__line__heading">${sDeliveryCost}:</dt>
			<dd class="table__line__value">
				<c:choose>
					<c:when test="${cartData.deliveryCost.value gt 0}">
						<format:price format="currency" priceData="${cartData.deliveryCost}" fallBackCurrency="${cartData.totalPrice.currencyIso}" />
						&nbsp;
						<format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.deliveryCost}" />
					</c:when>
					<c:otherwise>
						<spring:message code="checkout.summary.deliveryCost.free" />
					</c:otherwise>
				</c:choose>
			</dd>
		</dl>
	</c:if>
	
	<!-- Voucher Data -->
	<c:if test="${cartData.erpVoucherInfoData.valid}">
		<dl class="table__line table__line--tax">
			<dt class="table__line__heading">${sDiscount}:</dt>
			<dt class="table__line__heading">${cartData.erpVoucherInfoData.code}</dt>
			<dd class="table__line__value">
				<format:price format="currency" priceData="${cartData.erpVoucherInfoData.fixedValue}" fallBackCurrency="${cartData.totalPrice.currencyIso}" />
				&nbsp;
				- <format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.erpVoucherInfoData.fixedValue}" />
			</dd>
		</dl>
	</c:if>
	
	<dl class="table__line table__line--tax">
		<dt class="table__line__heading">${sTax}:</dt>
		<dd class="table__line__value">
			<format:price format="currency" priceData="${cartData.totalTax}" fallBackCurrency="${cartData.totalPrice.currencyIso}" />
			&nbsp;
			<format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.totalTax}" />
		</dd>
	</dl>
	<c:if test="${not empty discountPercentage}">
		<dl class="table__line ">
			<dt class="table__line__heading">${sDiscount}:</dt>
			<dd class="table__line__value">
				${discountPercentage}%
			</dd>
		</dl>
	</c:if>
	<dl class="table__line table__line--total table__line--total--${currentCountry.isocode}">
		<dt class="table__line__heading">${sTotalPayable}:</dt>
		<dd class="table__line__value">
			<format:price format="currency" priceData="${cartData.totalPrice}" fallBackCurrency="${cartData.totalPrice.currencyIso}" />
			&nbsp;
			<format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.totalPrice}" />
		</dd>
	</dl>
</div>
