<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:choose>
	<c:when test="${page eq 'checkoutAddress'}">
		<c:set var="url" value="/checkout/address/continueCheckout"/>
	</c:when>
	<c:otherwise>
		<c:set var="url" value="/checkout/detail/continueCheckout"/>
	</c:otherwise>
</c:choose>

<form:form class="continue-checkout" action="${url}" method="post">
	<input type="hidden" class="deliveryType" name="deliveryType" value="${deliveryType}" />
	<input type="hidden" class="billingAddressId" data-billing-is-shipping="${billingIsShippingAddress}" name="billingAddressId" value="${billingAddressId}" />
	<input type="hidden" class="shippingAddressId" name="shippingAddressId" value="${shippingAddressId}" />
	<input type="hidden" class="pickupLocationCode" name="pickupLocationCode" value="${pickupLocationCode}" />
	<input type="hidden" class="paymentModeCode" name="paymentModeCode" value="${selectedPaymentOptionCode}" />
	<button type="submit" class="mat-button mat-button--action-green btn-checkout">
		<spring:message code="checkout.button.checkout" />
	</button>
</form:form>


