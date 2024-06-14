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
<spring:theme code="metahd.lists.import-tool" text="Import Tool" var="sImportTool" />
<spring:theme code="login.your-login" text="Login" var="mLogin" />
<spring:theme code="metahd.account.welcome" text="Hello text" var="sHello" />
<spring:theme code="text.guest.customer" text="Guest" var="sGuest" />

<div class="menuitem-wrapper menuitem-wrapper--${currentCountry.isocode} ${isGuestCheckout ? ' is-guest-checkout' : ''}">
	<a class="popover-origin" href="${goToCompareUrl}"> <span class="vh"><spring:message code="metahd.compare" /></span> <i class="vh"></i> </a>
	<c:set var="menuUrl" value="/login" />
	<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP') and !hasRole('ROLE_B2BEESHOPGROUP')">
		<c:set var="menuUrl" value="/my-account" />
	</sec:authorize>
	<div class="menuitem ${isGuestCheckout ? 'is-guest-checkout' : ''}">
		<span class="vh">${sAccount}</span>

		<c:choose>
			<c:when test="${isGuestCheckout}">
				<div>
					<span class="label-welcome">${sHello}&nbsp;${sGuest}</span>
					<a class="ux-link ux-link--grey ux-link--clean" href="<c:url value='/logout'/>">${sLogOut}</a>
				</div>
			</c:when>

			<c:otherwise>
				<span class="dropdown-icon"><i class="fa fa-angle-down" aria-hidden="true"></i></span>
				<span class="label eproc-hide">
					<sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
						<c:set var="linkStatus" value="link-inactive" />
						<c:set var="loginUrl" value="/login" />
						<c:set var="registerUrl" value="/registration?registerFrom=header" />

						<div class="mobile-holder">
							<span>
								<i class="fa fa-user" aria-hidden="true"></i>
							</span>
						</div>
						<div class="desktop-holder">
							<span class="label-welcome">${sHello}</span>
							<spring:theme code="metahd.account.loginOrRegister" arguments="${loginUrl},${registerUrl}" />
						</div>

					</sec:authorize>
					<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
						<div class="mobile-holder">
							<span>
								<i class="fa fa-user" aria-hidden="true"></i>
							</span>
						</div>
						<div class="desktop-holder">
							<span class="label-welcome">
								<span class="label-name ellipsis" title="${user.firstName}">
									<a href="${menuUrl}">${sHello} &nbsp;<c:out value="${user.firstName}" /></a>
								</span>
							</span>
							<span class="label-account">${sMyAccount}</span>
						</div>
						<c:set var="linkStatus" value="link-active" />
					</sec:authorize>
				</span>
			</c:otherwise>
		</c:choose>
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

	<c:if test="${not isGuestCheckout}">
		<section class="flyout">
			<div class="flyout__holder">
				<div class="hd">
					<sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
						<h3>${sLoginOrRegister}</h3>
					</sec:authorize>
					<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
						<h3 class="eproc-hide loggedin">${sYourAccount}</h3>
						<h3 class="hidden">${sLists}</h3>
					</sec:authorize>
					<span class="flyout-close">
					<i class="fa fa-times" aria-hidden="true"></i>
				</span>
				</div>
				<div class="bd">
					<sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
						<c:url value="/j_spring_security_check" var="loginAction" />
						<form:form action="${loginAction}" method="post" modelAttribute="metaLoginForm" class="eproc-hide" id="loginForm">
							<h3>${sLogin}</h3>
							<div class="row">
								<label class="input-label" for="metahd-account-login">${sLogin}</label>
								<form:input type="text" cssClass="input-field" id="metahd-account-login" path="j_username" tabindex="0" readonly="false" placeholder="${sEmailPlaceholder}" maxlength="255" autocomplete="off"/>
							</div>
							<div class="row">
								<label class="input-label" for="metahd-account-password">${sPassword}</label>
								<form:password cssClass="input-field" id="metahd-account-password" path="j_password" placeholder="${sPassword}" maxlength="255" />
								<div class="actions-holder">
									<a class="forgetpass" href="/login/pw/request">${sForgotPassword}</a>
									<div class="actions-holder__remember">
										<form:checkbox id="metahd-account-remember" class="checkbox-big" path="_spring_security_remember_me" tabindex="3" />
										<label for="metahd-account-remember">${sRememberLogin}</label>
									</div>
								</div>
							</div>

							<button type="submit" data-aainteraction="login button" class="btn btn-primary btn-login">${sLoginBtn}</button>
						</form:form>
					</sec:authorize>
					<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
						<ul class="ui-list eproc-hide <sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')"> -signed-in</sec:authorize>">
							<li class="account-details">
								<c:url value="/my-account" var="accountDetailUrl" />
								<a href="${accountDetailUrl}" class="${linkStatus}"><i class="fa fa-user" aria-hidden="true"></i>${sAccountDetails}</a>
							</li>
								<%-- In a Movex based shop the payment and delivery options are persisted at the MovexCustomer. MovexCustomer object exists if erpCustomerId is defined. --%>
							<c:if test="${not empty user.unit.erpCustomerId && (shippingOptionsEditable || paymentOptionsEditable)}">
								<li class="settings">
									<c:url value="/my-account/payment-and-delivery-options" var="settingsUrl" />
									<a href="${settingsUrl}" class="${linkStatus}"><i class="fa fa-credit-card" aria-hidden="true"></i>${sSettings}</a>
								</li>
							</c:if>
							<li class="order-manager">
								<c:url value="/my-account/order-history" var="orderManagerUrl" />
								<a href="${(currentBaseStore.orderApprovalEnabled and appReqCount > 0) ? appReqUrl : orderManagerUrl}" class="${linkStatus}">
									<i class="fa fa-archive" aria-hidden="true"></i>${sOrderManager}
									<c:if test="${currentBaseStore.orderApprovalEnabled and appReqCount > 0}">
										<span class="badge"><i>${appReqCount}</i></span>
									</c:if>
								</a>
							</li>
							<li class="invoice-manager">
								<c:url value="/my-account/invoice-history" var="invoiceManagerUrl" />
								<a href="${invoiceManagerUrl}" class="${linkStatus}">
									<i class="far fa-file-alt" aria-hidden="true"></i>${sInvoiceManager}
								</a>
							</li>

							<c:if test="${currentBaseStore.quotationsEnabled and currentChannel.type ne 'B2C'}">
								<li class="quote-manager">
									<a href="${quoteUrl}" class="${linkStatus}">
										<i class="far fa-file" aria-hidden="true"></i>${sQuoteManager}
										<c:if test="${quoteCount > 0}">
											<span class="badge"><i>${quoteCount}</i></span>
										</c:if>
									</a>
								</li>
							</c:if>
						</ul>
						<ul class="ui-list -is-last <sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')"> -signed-in</sec:authorize>">
							<li class="compare-products">
								<a href="/compare"><i class="fas fa-exchange-alt" aria-hidden="true"></i><spring:message code="metahd.compare.list" /> (<div class="number-of-products compare-list-size">${compareListSize}</div>) </a>
							</li>
							<sec:authorize access="!hasRole('ROLE_B2BEESHOPGROUP')">
								<sec:authorize access="!hasRole('ROLE_EPROCUREMENTGROUP')">
									<li class="shopping-list">
										<a href="/shopping" class="${linkStatus}"><i class="far fa-file-alt" aria-hidden="true"></i><spring:message code="metahd.lists.shopping-list.loggedout" />
											<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
												(<div class="number-of-products shopping-lists-size">${shoppingListCount}</div>)
											</sec:authorize>
										</a>
									</li>
								</sec:authorize>
							</sec:authorize>
							<c:set var="bomFileSizeHidden" value="hidden" />
							<c:if test="${bomFileCount gt 0}">
								<c:set var="bomFileSizeHidden" value="" />
							</c:if>
							<li class="bom-file-list ${bomFileSizeHidden}">
								<a href="/my-account/savedBomEntries"><i class="far fa-file-alt" aria-hidden="true"></i><spring:message code="text.savedbomfiles" />
									(<div class="number-of-products bom-file-list-size">${bomFileCount}</div>)
								</a>
							</li>
							<li class="import"> <a href="/bom-tool" class="${linkStatus}"><i class="fas fa-upload" aria-hidden="true"></i>${sImportTool}</a> </li>
						</ul>
					</sec:authorize>
				</div>
				<div class="fd">
					<sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
						<div class="fd__new">
							<h3>${sNoAccountYet}</h3>
							<a class="newcustomer" href="/registration?registerFrom=header-green-button">${sNewCustomer}<i></i></a>
						</div>
						<ul class="ui-list">
							<li class="compare-products">
								<a href="/compare"><i class="fas fa-exchange-alt" aria-hidden="true"></i><spring:message code="metahd.compare.list" /> (<div class="number-of-products compare-list-size">${compareListSize}</div>) </a>
							</li>
						</ul>
					</sec:authorize>
					<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
						<a class="btn btn-secondary btn-logout" href="<c:url value='/logout'/>">${sLogOut}</a>
					</sec:authorize>
				</div>
			</div>
		</section>
	</c:if>
</div>
