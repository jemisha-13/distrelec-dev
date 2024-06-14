<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<c:choose>
	<c:when test="${shippingOptionsEditable && paymentOptionsEditable}">
		<spring:theme code="text.account.paymentAndDeliveryOptions" var="pageTitle" />
		<c:set var="formAction" value="set-payment-and-delivery-options" />
	</c:when>
	<c:when test="${shippingOptionsEditable && !paymentOptionsEditable}">
		<spring:theme code="text.account.deliveryOptions" var="pageTitle" />
		<c:set var="formAction" value="set-delivery-option" />
	</c:when>
	<c:when test="${!shippingOptionsEditable && paymentOptionsEditable}">
		<spring:theme code="text.account.paymentOptions" var="pageTitle" />
		<c:set var="formAction" value="set-payment-option" />
	</c:when>
	<c:when test="${paymentOptionsEditable && empty ccPaymentInfos}">
		<spring:theme code="text.account.paymentOptions" var="pageTitle" />
		<c:set var="formAction" value="set-payment-options" />
	</c:when>
	<c:when test="${paymentOptionsEditable && not empty ccPaymentInfos}">
		<spring:theme code="text.account.paymentOptions" var="pageTitle" />
		<c:set var="formAction" value="set-payment-info" />
	</c:when>
</c:choose>

<views:page-default pageTitle="${not empty pageTitle ? pageTitle : cmsPage.title}" bodyCSSClass="mod mod-layout skin-layout-account skin-layout-wide" >
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>

	<mod:global-messages/>

	<div class="md-system">
		<div class="row">
			<div class="col-12 col-md-4 order-2">
				<mod:nav-content template="myaccount" skin="myaccount" expandedNav="Settings" activeLink="PaymentAndDeliveryOptions" />
			</div>
			<div class="col-12 col-md-7 order-1">
				<h1>${not empty pageTitle ? pageTitle : cmsPage.title}</h1>
				<c:if test="${shippingOptionsEditable}">
					<mod:checkout-delivery-options-list skin="myaccount" template="myaccount" shippingOptions="${shippingOptions}" selectedShippingOption="${selectedShippingOption}" />
				</c:if>
				<c:if test="${paymentOptionsEditable}">
					<mod:checkout-payment-options-list skin="myaccount" template="myaccount" paymentOptions="${paymentOptions}" selectedPaymentOption="${selectedPaymentOption}" ccPaymentInfos="${ccPaymentInfos}" />
				</c:if>
			</div>
		</div>
	</div>
</views:page-default>
