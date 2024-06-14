<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<c:set var="fmtExceededBudget">
	<format:price format="defaultSplit" priceData="${exceededBudget}" explicitMaxFractionDigits="2" />
</c:set>

<spring:theme code="accountlist.approvalRequest.title" text="Approval Request List" var="sApprovalRequestList" />
<spring:theme code="checkout.orderConfirmation.titleApproval" text="Your order has been forwarded to your administrator for approval!" var="sOrderConfirmTitle" />

<%-- argument separator is set because if it isn't spring takes the comma of the price as separator and doesn't show the decimal numbers --%>
<spring:theme code="checkout.orderConfirmation.approval" text="Success." var="sConfirmationText1" arguments="${fmtExceededBudget}" argumentSeparator="#@;&?_" />
<spring:theme code="checkout.orderConfirmation.backToStore.buttonText" text="Return to store" var="sButtonText" />
<c:url var="continueUrl" value="/" />

<div class="card-wrapper">
	<div class="card-wrapper__item">
		<h2>
			${sOrderConfirmTitle}
		</h2>
		<div class="item-text">
			<p>${sConfirmationText1}</p>
			<spring:message code="checkout.orderConfirmation.processing.error" text="Cannot generate your order confirmation number" var="loadOrderCodeError"/>
		</div>
		<div class="order-code checkout-success-approval">
			<a href="/my-account/order-approval-requests"><span class="meta">${sApprovalRequestList}<i></i></span></a>
			<br />
			<br />
		</div>
	</div>
</div>
