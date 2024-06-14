<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-account skin-layout-wide" >
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>
	<mod:global-messages/>
	<div class="ct">
		<div class="row">
			<div class="gu-4 nav-align-right">
				<mod:nav-content template="myaccount" skin="myaccount" expandedNav="AccountDetails" activeLink="Addresses" />
			</div>
			<div class="gu-8">
				<h1 class="base page-title">${cmsPage.title}</h1>
					<%-- Decide which form action should be used depending on Customer Type --%>
					<c:set var="formActionUrl" value=""/>
					<c:set var="showDeleteButton" value="false"/>
					<c:choose>
						<c:when test="${not empty b2CAddressForm}">
							<c:set var="formActionUrl" value="/my-account/edit-address/b2c"/>
							<c:set var="addressForm" value="${b2CAddressForm}" />
							<spring:message code="checkout.address.editForm.${empty b2CAddressForm.addressId ? 'new' : 'edit'}Address.${b2CAddressForm.billingAddress ? 'billing' : 'shipping'}" var="formTitle" />
							<c:if test="${not empty b2CAddressForm.addressId && !b2CAddressForm.billingAddress}">
								<c:set var="showDeleteButton" value="true"/>
							</c:if>
						</c:when>
						<c:when test="${not empty b2BShippingAddressForm}">
							<c:set var="formActionUrl" value="/my-account/edit-address/b2bshipping"/>
							<c:set var="addressForm" value="${b2BShippingAddressForm}" />
							<spring:message code="checkout.address.editForm.${empty addressForm.addressId ? 'new' : 'edit'}Address.${addressForm.shippingAddress ? 'shipping' : 'billing'}" var="formTitle" />
							<c:if test="${not empty addressForm.addressId}">
								<c:set var="showDeleteButton" value="true"/>
							</c:if>
						</c:when>
						<c:when test="${not empty b2BBillingAddressForm}">
							<c:set var="formActionUrl" value="/my-account/edit-address/b2bbilling"/>
							<c:set var="addressForm" value="${b2BBillingAddressForm}" />
							<spring:message code="checkout.address.editForm.${empty addressForm.addressId ? 'new' : 'edit'}Address.${addressForm.shippingAddress ? 'billing' : 'billing'}" var="formTitle" />
							<c:if test="${not empty addressForm.addressId}">
								<c:set var="showDeleteButton" value="false"/>
							</c:if>
						</c:when>
					</c:choose>
					<mod:address-form 
						template="${currentChannel.uid eq 'B2B' ? 'b2b' : 'b2c'}"
						actionUrl="${formActionUrl}"
						cancelUrl="/my-account/addresses"
						formTitle="${formTitle}"
						addressForm="${addressForm}"
						addressType="${addressType}"
						customerChannel="${currentChannel.uid eq 'B2B' ? 'b2b' : 'b2c'}"
						customerType="${customerType}"
						isShippingAddress="${addressForm.shippingAddress}"
						isBillingAddress="${addressForm.billingAddress}"
						showDeleteButton="${showDeleteButton}"
					/>
			</div>
		</div>
	</div>
</views:page-default>


	