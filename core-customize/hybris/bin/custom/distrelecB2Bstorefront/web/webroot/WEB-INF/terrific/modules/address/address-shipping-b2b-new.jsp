<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="checkout.address.Shipping.address" text="Shipping Address" var="sTitle" />
<spring:message code="checkoutordersummaryinfobox.buttonChange" text="Edit" var="sButtonChange"/>

<div class="title">
	<h4>${sTitle}:</h4>
	<c:if test="${!isExportShop}">
		<c:choose>
		<c:when test="${isEShopGroup eq false}">
			<span class="title__edit">
				<a href="/checkout/detail">${sButtonChange}</a>
			</span>
			</c:when>
			<c:otherwise>
				<span class="title__edit">
				<a href="/checkout/address?showBillingEdit=false">${sButtonChange}</a>
			</span>
			</c:otherwise>
		</c:choose>
	</c:if>
</div>
<div class="box box-address" data-address-id="${address.id}" data-is-billing-and-shipping="${address.billingAddress and address.shippingAddress}">
	<p><c:out value="${address.companyName}" /></p>
	<p><c:out value="${address.companyName2}" /></p>
	<p><c:out value="${address.line1} " /><c:out value=", ${address.line2}" /></p>
	<p><c:out value="${address.town}" /></p>
	<p><c:out value="${address.postalCode}" /></p>
</div>
