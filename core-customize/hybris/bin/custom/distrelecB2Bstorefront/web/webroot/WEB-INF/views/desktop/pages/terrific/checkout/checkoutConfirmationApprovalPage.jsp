<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:url value="/my-account/invoice-history" var="sViewPrintInvoices" />
<c:url value="#" var="sTrackDelivery" />
<c:url value="/my-account" var="sMyAccount" />
<c:url value="/" var="homeUrl" />
<spring:theme code="checkout.orderComplete.continueLink" text="Continue shopping" var="sContinueShopping" /><spring:theme code="checkout.orderConfirmation.fact.text" text="You can manage your orders..." var="sOrderConfirmFactText"/>

<spring:theme code="checkout.consent.manageAccount" text="Did you know" var="sOrderConfirmTitleSecond" />
<spring:theme code="checkout.consent.goToMyAccount" text="Go to my account" var="sGoToMyAccount" arguments="${sMyAccount}"/>
<spring:theme code="checkout.consent.manageOrders" text="You can manage your orders..." var="sOrderConfirmFactText"/>

<views:page-checkout pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout mod-layout--checkout skin-layout-purchase skin-layout-wide">
	<div class="checkout-confirmation-holder">
		<div class="container">
			<div class="row">
				<mod:global-messages htmlClasses="col-12 order-2"/>
				<div class="col-12 col-md-9">
					<mod:checkout-success template="approval" skin="approval" orderCode="${order.code}" exeededBudget="${exceededBudget}"/>
					<mod:checkout-consent />
				</div>
				<div class="col-12 col-md-3">
					<div class="did-you">
						<h3 id="orderConfirmationTitleSidebar">${sOrderConfirmTitleSecond}</h3>
						<p id="orderConfirmationParagraphSidebar">${sOrderConfirmFactText}</p>
						<span id="orderConfirmationLinkSidebar">${sGoToMyAccount}</span>
					</div>
				</div>
				<div class="col">
					<a class="return-btn" href="${homeUrl}"><i class="fa fa-angle-left" aria-hidden="true"></i>${sContinueShopping}</a>
				</div>
			</div>
		</div>
	</div>
</views:page-checkout>
