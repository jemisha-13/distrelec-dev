<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="cart.pricecalcbox.title" text="Your Cart" var="sTitle"/>
<spring:message code="cart.pricecalcbox.subtotal" text="Articles" var="sSubtotal"/>
<spring:message code="cart.pricecalcbox.voucher" text="Voucher" var="sVoucher" arguments="${cartData.erpVoucherInfoData.code}"/>
<spring:message code="cart.pricecalcbox.tax" text="Tax" var="sTax"/>
<spring:message code="cart.pricecalcbox.delivery.cost" text="Delivery Cost" var="sDeliveryCost" />
<spring:message code="cart.pricecalcbox.discount" text="Discount" var="sDiscount" />
<spring:message code="cart.pricecalcbox.total.payable" text="Total Payable" var="sTotalPayable"/>
<spring:message code="cart.pricecalcbox.termsAndCond" text="Terms and Conditions" var="sTermsAndCond"/>
<spring:message code="cart.pricecalcbox.newsletter" text="Newsletter" var="sNewsletter"/>
<spring:message code="cart.pricecalcbox.acceptTerms" text="Yes, I have read and hereby accept the <a href='#'>terms and conditions of Distrelec</a>" var="sAcceptTerms"/>
<spring:message code="cart.pricecalcbox.acceptNewsletter" text="Yes, I want to receive the Distrelec newsletter" var="sAcceptNewsletter"/>
<spring:message code="checkout.button.checkout" text="Continue Checkout" var="sCheckout"/>
<spring:message code="checkout.button.checkout.openOrder" text="Save this open order" var="sCheckoutOpenOrder"/>
<spring:message code="checkout.button.checkout.openOrderClose" text="Add products and close this open order" var="sCheckoutOpenOrderClose"/>

<c:set var="cartData" value="${cartData != null ? cartData : orderData}" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<spring:message code="checkout.button.eprocurement" text="" var="sCheckout"/>
</sec:authorize>

<spring:message code="text.store.dateformat" var="datePattern" />

<%-- ${quotationData} --%>
<div class="calc-box calc-box-subtotal">
	<div class="calc-row calc-row-subtotal">
		<div class="calc-cell calc-cell-subtotal nth-1">${sSubtotal}</div>
		<div class="calc-cell calc-cell-subtotal nth-2"><span class="currency"><format:price format="currency" priceData="${quotationData.subtotal2}"  /></span></div>
		<div class="calc-cell calc-cell-subtotal nth-3 calc-subtotal"><format:price format="price" explicitMaxFractionDigits="2" priceData="${quotationData.subtotal2}" /></div>
	</div>
</div>
<div class="calc-box calc-box-details">
	<c:set var="totalDiscounts" value="${cartData.totalDiscounts}" />
	<c:if test="${not empty totalDiscounts and totalDiscounts.value gt 0}">
		<div class="calc-row calc-row-details">
			<div class="calc-cell calc-cell-details nth-1"> ${sDiscount} 	</div>
			<div class="calc-cell calc-cell-details nth-2"><span class="currency"><format:price format="currency" priceData="${totalDiscounts}" fallBackCurrency="${cartData.totalPrice.currencyIso}" /></span></div>
			<div class="calc-cell calc-cell-details nth-3 calc-delivery">-<format:price format="price" explicitMaxFractionDigits="2" priceData="${totalDiscounts}" /></div>
		</div>
	</c:if>
	<c:if test="${not empty cartData.deliveryMode}">
		<div class="calc-row calc-row-details">
			<div class="calc-cell calc-cell-details nth-1">
				${sDeliveryCost}&nbsp;<span class="updated-delivery-mode-name"> <spring:message code="${cartData.deliveryMode.translationKey}" text="${cartData.deliveryMode.name}" /> </span>
			</div>
			<div class="calc-cell calc-cell-details nth-2"><span class="currency"><format:price format="currency" priceData="${cartData.deliveryCost}" fallBackCurrency="${cartData.totalPrice.currencyIso}" /></span></div>
			<div class="calc-cell calc-cell-details nth-3 calc-delivery"><format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.deliveryCost}" /></div>
		</div>
	</c:if>
	<c:if test="${not empty cartData.paymentMode and cartData.paymentCost.value gt 0}">
		<div class="calc-row calc-row-details">
			<div class="calc-cell calc-cell-details nth-1">
				${cartData.paymentMode.name}
				<c:if test="${empty cartData.paymentMode.paymentCost.currencyIso and cartData.paymentMode.paymentCost.value gt 0}">
					(<format:price format="default" priceData="${cartData.paymentMode.paymentCost}" />)
				</c:if>
			</div>
			<div class="calc-cell calc-cell-details nth-2"><span class="currency"><format:price format="currency" priceData="${cartData.paymentCost}" fallBackCurrency="${cartData.totalPrice.currencyIso}" /></span></div>
			<div class="calc-cell calc-cell-details nth-3 calc-payment"><format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.paymentCost}" /></div>
		</div>
	</c:if>
	<c:if test="${not empty cartData.erpVoucherInfoData and empty cartData.erpVoucherInfoData.erpErrorMessageKey and cartData.erpVoucherInfoData.valid}">
		<div class="calc-row calc-row-details">
			<div class="calc-cell calc-cell-details nth-1"> <spring:message code="cart.pricecalcbox.voucher" text="Voucher" arguments="${cartData.erpVoucherInfoData.code}"/> </div>
			<div class="calc-cell calc-cell-details nth-2"><span class="currency"><format:price format="currency" priceData="${cartData.subTotal}" fallBackCurrency="${cartData.totalPrice.currencyIso}" /></span></div>
			<div class="calc-cell calc-cell-details nth-3 calc-voucher">- <format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.erpVoucherInfoData.fixedValue}" /></div>
		</div>
	</c:if>
	<div class="calc-row">
		<div class="calc-cell calc-cell-details nth-1">${sTax}</div>
		<div class="calc-cell calc-cell-details nth-2"><span class="currency"><format:price format="currency" priceData="${quotationData.tax}" fallBackCurrency="${quotationData.tax.currencyIso}" /></span></div>
		<div class="calc-cell calc-cell-details nth-3 calc-tax"><format:price format="price" explicitMaxFractionDigits="2" priceData="${quotationData.tax}" /></div>
	</div>
</div>

<div class="calc-box calc-box-total">
	<div class="calc-row calc-row-total">
		<div class="calc-cell calc-cell-total nth-1">${sTotalPayable}</div>
		<div class="calc-cell calc-cell-total nth-2"><span class="currency"><format:price format="currency" priceData="${cartData.totalPrice}" fallBackCurrency="${cartData.totalPrice.currencyIso}" /></span></div>
		<div class="calc-cell calc-cell-total nth-3 calc-total"><format:price format="price" explicitMaxFractionDigits="2" priceData="${quotationData.total}" /></div>
	</div>
</div>

<c:if test="${showTerms == true && (!yearlyBudgetExceeds && !orderBudgetExceeds)}">
	<form:form action="${actionUrl}" modelAttribute="reviewForm" method="post" class="reviewForm">
		<div class="bd-terms-box" id="terms-and-conditions">
			<div class="terms-row">
				<h3>${sTermsAndCond}</h3>
				<div class="check-option">
					<formUtil:formCheckbox idKey="agree" labelKey="register.acceptTermsOfUseText" path="agree" inputCSS="checkbox checkbox-big" value="true"/>
				</div>
				<c:if test="${cancellationPolicyConfirmationRequired}">
					<div class="check-option">
						<formUtil:formCheckbox idKey="agree1" labelKey="cart.pricecalcbox.acceptTerms2" path="agree1" inputCSS="checkbox checkbox-big" value="true"/>
					</div>
				</c:if>
				<c:if test="${not empty cartData.b2bCustomerData and not cartData.b2bCustomerData.subscribedForNewsletter}">
					<h3>${sNewsletter}</h3>
					<div class="check-option">
						<formUtil:formCheckbox idKey="newsLetterOption" labelKey="cart.pricecalcbox.acceptNewsletter" path="marketingConsent" mandatory="false" inputCSS="checkbox checkbox-big" />
					</div>
				</c:if>
				<div class="hidden">
					<input type="hidden" name="completeDelivery" id="completeDelivery" value="false" />
				</div>
			</div>
		</div>
		<c:choose>
			<c:when test="${cartData.openOrder}">
				<input type="hidden" class="open-order-close-date" name="openOrderCloseDate" value="" />
				<button class="btn btn-primary btn-checkout" type="submit">
					<i class="icon-cart"></i> ${sCheckoutOpenOrder} <i class="icon-arrow"></i>
				</button>
				<c:if test="${cartData.openOrderErpCode ne null}">
					<a href="#" class="btn btn-secondary btn-checkout btn-open-order-close" 
					data-close-date-today="${dateToday}"
					data-lightbox-title="<spring:message code="cart.pricecalcbox.close.oo.title" />"  
				    data-lightbox-message="<spring:message code="cart.pricecalcbox.close.oo.text" />"   
				    data-lightbox-btn-deny="<spring:message code="cart.pricecalcbox.close.oo.no" />"  
				    data-lightbox-show-confirm-button="true"
				    data-lightbox-btn-conf="<spring:message code="cart.pricecalcbox.close.oo.yes" />"  
				    data-order-id="${cartData.openOrderErpCode}"	
				    data-action-url="${actionUrl}"
				    data-source = "checkout">
						<i class="icon-cart"></i> ${sCheckoutOpenOrderClose} 	<i class="icon-arrow"></i>
					</a>
				</c:if>
			</c:when>
			<c:otherwise>
				<button class="btn btn-primary btn-checkout btn-checkout-final" type="submit">
					<i class="icon-cart"></i>
						<c:choose>
							<c:when test="${reviewPage eq true and cartData.paymentMode.hop}">
							  	<%-- Case of CC, PP, etc --%>
								<spring:message code="checkout.button.checkout" />
							</c:when>
							<c:otherwise>
								<%-- Case of invoice --%>
								<spring:message code="checkout.summary.placeOrder" />
							</c:otherwise>
						</c:choose>
					<i class="icon-arrow"></i>
				</button>
			</c:otherwise>
		</c:choose>
	</form:form>
</c:if>
<%-- Case on Checkout Address Page --%>
<c:if test="${showContinueCheckoutButton == true}">
	<c:set var="openOrderType" value="" />
	<c:choose>
		<c:when test="${(cartData.openOrder and fn:length(existingOpenOrders) == 0) or (cartData.openOrder and empty cartData.openOrderErpCode)}">
			<c:set var="openOrderType" value="new" />
		</c:when>
		<c:when test="${cartData.openOrder and not empty cartData.openOrderErpCode}">
			<c:set var="openOrderType" value="existing" />
		</c:when>
	</c:choose>

	<mod:checkout-btn-continue
		deliveryType = "${deliveryType}"
		billingAddressId="${cartData.billingAddress.id}"
		billingIsShippingAddress="${cartData.billingAddress.billingAddress and cartData.billingAddress.shippingAddress}"
		shippingAddressId="${cartData.deliveryAddress.id}"
		pickupLocationCode="${cartData.pickupLocation.code}"
		isOpenOrder = "${cartData.openOrder}"
		openOrderType = "${openOrderType}" />
	<div class="plz-add-address hidden">
		<i class="alert"></i> <div><spring:message code="checkout.address.shipping.add.new" /></div>
	</div>
</c:if>