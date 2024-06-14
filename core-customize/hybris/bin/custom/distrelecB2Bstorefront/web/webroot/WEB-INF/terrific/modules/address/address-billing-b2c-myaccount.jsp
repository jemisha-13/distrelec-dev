<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="address.billing.checkout.title" text="Billing Address" var="sTitle" />
<spring:message code="checkoutordersummaryinfobox.buttonChange" text="Edit" var="sButtonChange"/>

<div class="box box-address" data-address-id="${address.id}" data-is-billing-and-shipping="${address.billingAddress and address.shippingAddress}">
	<div class="row">
		<div class="col-12 box-address__preview">
			<p><c:out value="${address.line1} " /></p>
			<p><c:out value="${address.line2}" /></p>
			<p><c:out value="${address.town}" /></p>
			<p><c:out value="${address.postalCode}" /></p>
		</div>
	</div>
</div>
