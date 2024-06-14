<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div class="mod-order-detail-section__item mod-order-detail-section__item--track">
	<mod:track-and-trace orderLines="${orderData.deliveryData}"/>
</div>

<c:set var="cType" value="b2c" />
<c:if test="${not empty orderData.customerType}">
	<c:set var="cType" value="${fn:toLowerCase(fn:substring(orderData.customerType,0,3))}" />
</c:if>


<c:if test="${cType == 'b2b'}">
	<div class="mod-order-detail-section__item mod-order-detail-section__item--cost-center">
		<mod:checkout-order-summary-cost-center-box template="readonly" skin="my-account" costCenter="${orderData.distCostCenter}" projectNumber="${orderData.projectNumber}" />
	</div>
</c:if>

<c:if test="${not empty orderData.note}">
	<div class="mod-order-detail-section__item mod-order-detail-section__item--summary-note">
		<mod:checkout-order-summary-note-box template="readonly" note="${orderData.note}" />
	</div>
</c:if>

<div class="mod-order-detail-section__item">
	<h2 class="head"><spring:message code="orderdetailsection.billingAddress" /></h2>
    <mod:address template="billing-${cType}-myaccount"  address="${orderData.billingAddress}" customerType="${cType}" />
</div>

<div class="mod-order-detail-section__item">
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
</div>

<div class="mod-order-detail-section__item">
	<mod:checkout-order-summary-info-box template="shipping-method" skin="readonly" orderData="${orderData}" />
</div>

<div class="mod-order-detail-section__item">
	<mod:checkout-order-summary-info-box template="payment-method" skin="readonly" orderData="${orderData}" />
</div>
