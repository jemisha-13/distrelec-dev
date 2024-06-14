<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code="checkout.address.b2b.billing.address" text="Billing Address" var="sTitle" />
<spring:message code="checkoutordersummaryinfobox.buttonChange" text="Edit" var="sButtonChange"/>
<c:set var="editAddressUrl" value="/checkout/address"/>

<sec:authorize access="hasAnyRole('ROLE_B2BEESHOPGROUP')">
	<c:set var="editAddressUrl" value="/checkout/address?showBillingEdit=false"/>
</sec:authorize>

<div class="title">
	<h4>${sTitle}:</h4>
	<c:if test="${!isExportShop}">
		<span class="title__edit">
			<a href="${editAddressUrl}">${sButtonChange}</a>
		</span>
	</c:if>
</div>
<div class="box box-address" data-address-id="${address.id}" data-is-billing-and-shipping="${address.billingAddress and address.shippingAddress}">
	<p>
		<c:out value="${address.title}" />
		<c:out value=" ${address.lastName}" />
		<c:out value=" ${address.firstName}" />
	</p>
	<p><c:out value="${address.line1} " /><c:out value=", ${address.line2}" /></p>
	<p><c:out value="${address.town}" /></p>
	<p><c:out value="${address.postalCode}" /></p>
</div>
