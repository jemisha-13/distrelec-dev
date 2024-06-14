<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code="base.login" text="Login" var="sLogin" />
<spring:message code="base.email" text="E-Mail" var="sEmail2" />
<spring:message code="base.login.btn" text="Login" var="sLoginBtn" />
<spring:message code="base.logout" text="Logout" var="sLogout" />
<spring:message code="base.shop" text="Distrelec" var="sShop" />
<spring:message code="login.email-adress" text="Login" var="sEmail" />
<spring:message code="base.password" text="Password" var="sPassword" />
<spring:message code="base.close" text="Close" var="sClose" />
<spring:theme code="login.register" text="Register" var="sRegister" />
<spring:message code="metahd.account" text="Account" var="sAccount" />
<spring:message code="metahd.account.my" text="My Account" var="sMyAccount" />
<spring:message code="metahd.account.myAccount" text="Your Account" var="sYourAccount" />
<spring:message code="metahd.account.forgot-password" text="Forgot Password" var="sForgotPassword" />
<spring:message code="metahd.account.new-customer" text="I am a new Customer" var="sNewCustomer" />
<spring:message code="metahd.account.account-details" text="Account Details" var="sAccountDetails" />
<spring:message code="metahd.account.order-manager" text="Order Manager" var="sOrderManager" />
<spring:message code="metahd.account.invoice-manager" text="Invoice Manager" var="sInvoiceManager" />
<spring:message code="metahd.account.quotation-manager" text="Quote Manager" var="sQuoteManager" />
<spring:message code="metahd.account.settings" text="Settings" var="sSettings" />
<spring:message code="metahd.account.email" text="Settings" var="sEmailPlaceholder" />
<spring:message code="metahd.account.noAccount" text="Dont have an account yet" var="sNoAccountYet" />
<spring:message code="metahd.account.loginOrRegisterText" text="Login or Register" var="sLoginOrRegister" />
<spring:message code="metahd.account.rememberLogin" text="Remember login" var="sRememberLogin" />
<spring:message code="metahd.account.logOut" text="Log Out" var="sLogOut" />
<spring:theme code="metahd.lists" text="Lists" var="sLists" />
<spring:theme code="metahd.lists.favorites-shopping" text="Favorites & Shopping" var="sFavoritesShopping" />
<spring:theme code="metahd.lists.favorite-products" text="My Favorite Products" var="sFavoriteProducts" />
<spring:theme code="login.your-login" text="Login" var="mLogin" />
<spring:theme code="metahd.account.welcome" text="Hello text" var="sHello" />

<div class="menuitem-wrapper menuitem-wrapper--${currentCountry.isocode}">
	<div class="menuitem">
		<span class="vh">${sAccount}</span>
		<span class="dropdown-icon"><i class="fa fa-angle-down" aria-hidden="true"></i></span>
		<span class="label eproc-hide">
			<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
				<span class="label eproc-label">
					<span class="label-welcome">${sHello}</span>
					<span class="label-name ellipsis" title="${user.companyName}">
						<c:out value="${user.companyName}" />
					</span>
				</span>
			</sec:authorize>
		</span>
	</div>
	<c:url value="/my-account/quote-history" var="quoteUrl" />
	<c:if test="${(currentBaseStore.orderApprovalEnabled and appReqCount > 0) or (currentBaseStore.quotationsEnabled and quoteCount > 0)}">
		<c:set var="badgeNo" value="${quoteCount}" />
		<c:set var="badgeUrl" value="${quoteUrl}" />
		<c:if test="${appReqCount > 0}">
			<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
				<c:url value="/my-account/order-approval-requests" var="appReqUrl" />
			</sec:authorize>
			<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
				<c:url value="/my-account/order-approval" var="appReqUrl" />
			</sec:authorize>
			<spring:message code="text.account.approvalRequests" text="Approval Requests" var="sAppReq" />
			<c:set var="badgeNo" value="${quoteCount + appReqCount}" />
			<c:set var="badgeUrl" value="${appReqUrl}" />
			<c:set var="badgeDelim" value="${quoteCount > 0 ? ' / ' : ''}" />
		</c:if>
		<c:if test="${quoteCount > 0}">
			<spring:message code="text.account.openQuotations" text="Open Quotations" var="sOpenQote" />
		</c:if>
		<a href="${badgeUrl}" title="${sAppReq} ${badgeDelim} ${sOpenQote}"><span class="badge"><i>${badgeNo}</i></span></a>
	</c:if>
</div>