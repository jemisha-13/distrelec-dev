<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="address.shipping.checkout.title" text="Shipping Address" var="sTitle" />
<spring:message code="checkoutordersummaryinfobox.buttonChange" text="Edit" var="sButtonChange"/>

<div class="title">
	<h4>${sTitle}:</h4>
	<span class="title__edit">
		<a href="/checkout/detail">${sButtonChange}</a>
	</span>
</div>
<div class="box box-address" data-warehouse-code="${warehouse.code}" data-is-billing-and-shipping="${address.billingAddress and address.shippingAddress}">
	<p><c:out value="${warehouse.name}" /></p>
	<p><spring:message code="checkout.address.street" text="Street" />:&nbsp;<c:out value="${warehouse.streetName}" />&nbsp;<c:out value="${warehouse.streetNumber}" /></p>
	<p><spring:message code="checkout.address.city" text="City" />:&nbsp;<c:out value="${warehouse.town}" /></p>
	<p><spring:message code="address.postcode" text="Postcode" />:&nbsp;<c:out value="${warehouse.postalCode}" /></p>
	<p><spring:message code="checkout.address.phone" text="Phone" />:&nbsp;<c:out value="${warehouse.phone}" /></p>
	<p><spring:message code="address.shipping.opening.hours" text="Pick-up desk opening hours" />:&nbsp;<spring:message code="checkout.address.mo2fr" text="Mo.-Fr." />:&nbsp;<c:out value="${warehouse.openingsHourMoFr}" /></p>
	<c:if test="${not empty warehouse.openingsHourSa}">
		<p><spring:message code="checkout.address.saturday" text="Sa." />:&nbsp;<c:out value="${warehouse.openingsHourSa}" /></p>
	</c:if>
</div>
