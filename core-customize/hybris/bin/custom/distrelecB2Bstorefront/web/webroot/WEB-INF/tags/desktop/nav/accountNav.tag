<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="selected" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="span-4">
	<div class="span-4 nav_column">
		<div class="title_holder">
			<div class="title">
				<div class="title-top">
					<span></span>
				</div>
			</div>
			<h2><spring:theme code="text.account.yourAccount" text="Your Account"/></h2>
		</div>
		<div class="item">
			<ul class="facet_block indent">
				<li class='${selected eq 'profile' ? 'nav_selected' : ''}'>
					<c:url value="/my-account/profile" var="encodedUrl" />
					<ycommerce:testId code="myAccount_profile_navLink">
						<a href="${encodedUrl}"><spring:theme code="text.account.profile" text="Profile"/></a>
					</ycommerce:testId>
				</li>
				<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP')">
					<li class='${selected eq 'address-book' ? 'nav_selected' : ''}'>
						<c:url value="/my-account/address-book" var="encodedUrl" />
						<ycommerce:testId code="myAccount_addressBook_navLink">
							<a href="${encodedUrl}"><spring:theme code="text.account.addressBook" text="Address Book"/></a>
						</ycommerce:testId>
					</li>
					<li class='${selected eq 'payment-details' ? 'nav_selected' : ''}'>
						<c:url value="/my-account/payment-details" var="encodedUrl" />
						<ycommerce:testId code="myAccount_paymentDetails_navLink">
							<a href="${encodedUrl}"><spring:theme code="text.account.paymentDetails" text="Payment Details"/></a>
						</ycommerce:testId>
					</li>
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP')">
					<li class='${selected eq 'my-quotes' ? 'nav_selected' : ''}'>
						<c:url value="/my-account/my-quotes" var="encodedUrl" />
						<ycommerce:testId code="myAccount_orderquotes_navLink">
							<a href="${encodedUrl}"><spring:theme code="text.account.myQuotes" text="My Quotes"/></a>
						</ycommerce:testId>
					</li>
					<li class='${selected eq 'orders' || selected eq 'order' ? 'nav_selected' : ''}'>
						<c:url value="/my-account/orders" var="encodedUrl" />
						<ycommerce:testId code="myAccount_orders_navLink">
							<a href="${encodedUrl}"><spring:theme code="text.account.orderHistory" text="Order History"/></a>
						</ycommerce:testId>
					</li>
					<li class='${selected eq 'my-replenishment-orders' ? 'nav_selected' : ''}'>
						<c:url value="/my-account/my-replenishment" var="encodedUrl" />
						<ycommerce:testId code="myAccount_replenishment_navLink">
							<a href="${encodedUrl}"><spring:theme code="text.account.myReplenishment" text="My Replenishment Orders"/></a>
						</ycommerce:testId>
					</li>
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_B2BAPPROVERGROUP')">
					<li class='${selected eq 'approval-dashboard' ? 'nav_selected' : ''}'>
						<c:url value="/my-account/approval-dashboard" var="encodedUrl" />
						<ycommerce:testId code="myAccount_orderdashboard_navLink">
							<a href="${encodedUrl}"><spring:theme code="text.account.orderApprovalDashboard" text="Order Approval Dashboard"/></a>
						</ycommerce:testId>
					</li>
				</sec:authorize>
			</ul>
		</div>
	</div>
	<div class="span-4 side-content-slot cms_banner_slot">
		<cms:slot var="feature" contentSlot="${slots['SideContent']}">
			<cms:component component="${feature}"/>
		</cms:slot>
	</div>
</div>
