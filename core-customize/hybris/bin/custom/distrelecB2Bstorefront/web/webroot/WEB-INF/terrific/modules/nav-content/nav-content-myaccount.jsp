<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="customerType" value="${cartData.b2bCustomerData.customerType}" />

<ul class="content-nav-ul">
    <li class="${expandedNav eq 'AccountDetails' ? 'accordion-item is-expanded' : 'accordion-item'}">
		<a class="accordion-item-title" href="#AccountDetails"><i class="icon-indicator"></i><spring:theme code="text.account.details" text="Account Details"/></a>
		<div class="spring" ${expandedNav eq 'AccountDetails' ? 'style="display: block;"' : '' }>
    		<ul>
    			<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP')">
					<li>
						<c:url value="/my-account/company/information" var="encodedUrl" />
						<a class="${activeLink eq 'CompanyInfo' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}"><spring:theme code="text.account.company.information" text="Company Information"/></a>
					</li>
				</sec:authorize>
				<li>
					<c:url value="/my-account/my-account-information" var="encodedUrl" />
					<a class="${activeLink eq 'LoginData' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}"><spring:theme code="text.preferences.account.info" text="Login Data"/></a>
				</li>
				<li>
					<c:url value="/my-account/preference-center" var="encodedUrl" />
					<a class="${activeLink eq 'PreferenceCenter' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}"><spring:theme code="text.preferences.communication" text="Preference center"/></a>
				</li>
				<li>
					<c:url value="/my-account/addresses" var="encodedUrl" />
					<a class="${activeLink eq 'Addresses' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}"><spring:theme code="text.account.addresses" text="Addresses"/></a>
				</li>
    		</ul>
    	</div>
    </li>
    <c:if test="${not empty user.unit.erpCustomerId && (shippingOptionsEditable || paymentOptionsEditable)}">
	    <li class="${expandedNav eq 'Settings' ? 'accordion-item is-expanded' : 'accordion-item'}">
			<a class="accordion-item-title" href="#Settings"><i class="icon-indicator"></i><spring:theme code="text.account.settings" /></a>
			<div class="spring" ${expandedNav eq 'Settings' ? 'style="display: block;"' : '' }>
	    		<ul>
					<li>
						<c:url value="/my-account/payment-and-delivery-options" var="encodedUrl" />
						<a class="${activeLink eq 'PaymentAndDeliveryOptions' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}">
							<spring:theme code="text.account.paymentOptions" />
						</a>
					</li>
	    		</ul>
	    	</div>
	    </li>
	</c:if>
    <li class="${expandedNav eq 'OrderManager' ? 'accordion-item is-expanded' : 'accordion-item'}">
		<a class="accordion-item-title" href="#OrderManager"><i class="icon-indicator"></i><spring:theme code="text.account.orderManager" /></a>
		<c:if test="${(currentBaseStore.orderApprovalEnabled and appReqCount > 0) or (currentBaseStore.quotationsEnabled and quoteCount > 0)}">
			<i class="badge">${quoteCount + appReqCount}</i>
		</c:if>
    	<div class="spring" ${expandedNav eq 'OrderManager' ? 'style="display: block;"' : '' }>
    		<ul>
    			<c:if test="${(currentSalesOrg.erpSystem eq 'ELFA' or currentSalesOrg.erpSystem eq 'SAP') and allowedToPlaceOpenOrders}">
    				<li>
						<c:url value="/my-account/open-orders" var="encodedUrl" />
						<a class="${activeLink eq 'OpenOrderHistory' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}"><spring:theme code="text.account.openOrderHistory" /></a>
					</li>
    			</c:if>
				<li>
					<c:url value="/my-account/order-history" var="encodedUrl" />
					<a class="${activeLink eq 'OrderHistory' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}"><spring:theme code="text.account.orderHistory" /></a>
				</li>
				<li>
					<c:url value="/my-account/invoice-history" var="encodedUrl" />
					<a class="${activeLink eq 'InvoiceHistory' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}"><spring:theme code="text.account.invoiceHistory" /></a>
				</li>
				<c:if test="${currentBaseStore.quotationsEnabled and currentChannel.type ne 'B2C'}">
					<li>
						<c:url value="/my-account/quote-history" var="encodedUrl" />
						<a class="${activeLink eq 'QuotationHistory' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}"><spring:theme code="text.account.quoteManager" /></a>
						<c:if test="${quoteCount > 0}">
							<i class="badge">${quoteCount}</i>
						</c:if>
					</li>
				</c:if>
				<c:if test="${currentSalesOrg.adminManagingSubUsers}">
					<c:if test="${currentBaseStore.orderApprovalEnabled}">
						<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
							<li>
								<c:url value="/my-account/order-approval-requests" var="encodedUrl" />
								<a class="${activeLink eq 'OrderApprovals' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}"><spring:theme code="text.account.myOrderApprovalRequests" /></a>
								<c:if test="${appReqCount > 0}">
									<i class="badge">${appReqCount}</i>
								</c:if>
							</li>
						</sec:authorize>
						<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
							<li>
								<c:url value="/my-account/order-approval" var="encodedUrl" />
								<a class="${activeLink eq 'OrderApprovals' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}"><spring:theme code="text.account.approvalRequests" /></a>
								<c:if test="${appReqCount > 0}">
									<i class="badge">${appReqCount}</i>
								</c:if>
							</li>
						</sec:authorize>
					</c:if>
					<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
						<li>
							<c:url value="/my-account/company/user-management" var="encodedUrl" />
							<a class="${activeLink eq 'UserManagement' ? 'menuOption active' : 'menuOption'}" href="${encodedUrl}"><spring:theme code="text.account.company.userManagement" text="User Management"/></a>
						</li>
					</sec:authorize>
				</c:if>
    		</ul>
    	</div>
    </li>  
</ul>
