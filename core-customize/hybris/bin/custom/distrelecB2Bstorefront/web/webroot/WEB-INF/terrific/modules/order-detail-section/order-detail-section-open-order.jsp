<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<c:set var="cType" value="b2c" />
<c:if test="${not empty orderData.customerType}">
	<c:set var="cType" value="${fn:toLowerCase(fn:substring(orderData.customerType,0,3))}" />
</c:if>

<mod:checkout-order-summary-info-box template="open-order" skin="open-order-myaccount" orderData="${orderData}" />
<mod:checkout-order-summary-cost-center-box template="readonly" skin="open-order" orderCode="${orderData.code}" costCenter="${orderData.distCostCenter}" projectNumber="${orderData.projectNumber}" />

<c:if test="${not empty orderData.note}">
	<mod:checkout-order-summary-note-box template="readonly" note="${orderData.note}" />
</c:if>

<h2 class="head"><spring:message code="orderdetailsection.billingAddress" /></h2>
<mod:address template="billing-${cType}"  address="${orderData.billingAddress}" customerType="${cType}"/>

<c:choose>
	<c:when test="${not empty orderData.pickupLocation}">
		<h2 class="head"><spring:message code="orderdetailsection.pickupLocation" /></h2>
		<mod:address template="pickup" warehouse="${orderData.pickupLocation}" />
	</c:when>
	<c:otherwise>
		<h2 class="head"><spring:message code="orderdetailsection.deliveryAddress" /></h2>
		<mod:address template="shipping-${cType}" address="${orderData.deliveryAddress}" customerType="${cType}"/>
	</c:otherwise>
</c:choose>

<mod:checkout-order-summary-info-box template="shipping-method" skin="readonly" orderData="${orderData}" />
<mod:checkout-order-summary-info-box template="payment-method" skin="readonly" orderData="${orderData}" />

<a class="btn btn-secondary btn-back" href="<c:url value="/my-account/order-history" />"><spring:message code="text.back" text="Back" /><i></i></a>