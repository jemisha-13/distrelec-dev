<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:accountNav/>
	<div class="span-20 last">
		<div class="span-20 wide-content-slot cms_banner_slot">
			<cms:slot var="feature" contentSlot="${slots['TopContent']}">
				<cms:component component="${feature}"/>
			</cms:slot>
		</div>
		<div class="span-20 last">
			<div class="cust_acc">
				<div class="cust_acc_tile">
					<c:url value="/my-account/profile" var="encodedUrl" />
					<span>
						<spring:message code="text.account.profile" var="textAccountProfile"/>
						<a href="${encodedUrl}"><theme:image code="img.account.profile" alt="${textAccountProfile}" title="${textAccountProfile}"/></a>
					</span>
					<h1><a href="${encodedUrl}"><spring:theme code="text.account.profile" text="Profile"/></a></h1>
					<ul>
						<ycommerce:testId code="myAccount_options_profile_groupbox">
							<li><a href="${encodedUrl}"><spring:theme code="text.account.profile.updatePersonalDetails" text="Update personal details"/></a></li>
							<c:url value="/my-account/update-password" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.account.profile.changePassword" text="Change your password"/></a></li>
						</ycommerce:testId>
					</ul>
				</div>
				<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP')">
					<div class="cust_acc_tile">
						<c:url value="/my-account/address-book" var="encodedUrl" />
						<span>
							<spring:message code="text.account.addressBook" var="textAccountAddressBook"/>
							<a href="${encodedUrl}"><theme:image code="img.account.addressBook" alt="${textAccountAddressBook}" title="${textAccountAddressBook}"/></a>
						</span>
						<h1><a href="${encodedUrl}"><spring:theme code="text.account.addressBook" text="Address Book"/></a></h1>
						<ul>
							<ycommerce:testId code="myAccount_options_addressBook_groupbox">
								<li><a href="${encodedUrl}"><spring:theme code="text.account.addressBook.manageDeliveryAddresses" text="Manage your delivery addresses"/></a></li>
								<li><a href="${encodedUrl}"><spring:theme code="text.account.addressBook.setDefaultDeliveryAddress" text="Set default delivery address"/></a></li>
							</ycommerce:testId>
						</ul>
					</div>
					<div class="cust_acc_tile">
						<c:url value="/my-account/payment-details" var="encodedUrl" />
						<span>
							<spring:message code="text.account.paymentDetails" var="textAccountPaymentDetails"/>
							<a href="${encodedUrl}"><theme:image code="img.account.paymentDetails" alt="${textAccountPaymentDetails}" title="${textAccountPaymentDetails}"/></a>
						</span>
						<h1><a href="${encodedUrl}"><spring:theme code="text.account.paymentDetails" text="Payment Details"/></a></h1>
						<ul>
							<ycommerce:testId code="myAccount_options_paymentDetails_groupbox">
								<li><a href="${encodedUrl}"><spring:theme code="text.account.paymentDetails.managePaymentDetails" text="Manage your payment details"/></a></li>
								<li><a href="${encodedUrl}"><spring:theme code="text.account.paymentDetails.setDefaultPaymentDetails" text="Set default payment details"/></a></li>
							</ycommerce:testId>
						</ul>
					</div>
					<div class="cust_acc_tile">
						<c:url value="/my-account/my-quotes" var="encodedUrl" />
						<span>
							<a href="${encodedUrl}"><theme:image code="img.account.quotes" alt="Quotes" title="Quotes"/></a>
						</span>
						<h1><a href="${encodedUrl}"><spring:theme code="text.account.quotes" text="Quotes"/></a></h1>
						<ul>
							<ycommerce:testId code="myAccount_options_quotes_groupbox">
								<li><a href="${encodedUrl}"><spring:theme code="text.account.viewQuotes" text="View my quotes"/></a></li>
							</ycommerce:testId>
						</ul>
					</div>
					<div class="cust_acc_tile">
						<c:url value="/my-account/orders" var="encodedUrl" />
						<span>
							<spring:message code="text.account.orderHistory" var="textAccountOrderHistory"/>
							<a href="${encodedUrl}"><theme:image code="img.account.orderHistory" alt="${textAccountOrderHistory}" title="${textAccountOrderHistory}"/></a>
						</span>
						<h1><a href="${encodedUrl}"><spring:theme code="text.account.orderHistory" text="Order History"/></a></h1>
						<ul>
							<ycommerce:testId code="myAccount_options_orderHistory_groupbox">
								<li><a href="${encodedUrl}"><spring:theme code="text.account.viewOrderHistory" text="View order history"/></a></li>
								<c:url value="/my-account/my-replenishment" var="encodedUrl" />
								<li><a href="${encodedUrl}"><spring:theme code="text.account.myReplenishment" text="Change your password"/></a></li>
							</ycommerce:testId>
						</ul>
					</div>
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_B2BAPPROVERGROUP')">
					<div class="cust_acc_tile">
						<c:url value="/my-account/approval-dashboard" var="encodedUrl" />
						<span>
							<spring:message code="text.account.orderApproval" var="textAccountOrderApproval"/>
							<a href="${encodedUrl}"><theme:image code="img.account.orderApproval" alt="${textAccountOrderApproval}" title="${textAccountOrderApproval}"/></a>
						</span>
						<h1><a href="${encodedUrl}"><spring:theme code="text.account.orderApproval" text="Order Approval"/></a></h1>
						<ul>
							<ycommerce:testId code="myAccount_options_orderApproval_groupbox">
								<li><a href="${encodedUrl}"><spring:theme code="text.account.viewOrderApproval" text="View orders that require approval"/></a></li>
							</ycommerce:testId>
						</ul>
					</div>
				</sec:authorize>
			</div>
		</div>
	</div>
</template:page>
