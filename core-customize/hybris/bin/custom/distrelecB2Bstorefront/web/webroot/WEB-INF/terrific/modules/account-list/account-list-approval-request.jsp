<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code="checkoutorderbudgetapprovalbar.status.REJECTED" var="sRejected" />
<spring:message code="checkoutorderbudgetapprovalbar.status.APPROVED" var="sApproved" />
<spring:message code="checkoutorderbudgetapprovalbar.status.PENDING_APPROVAL" var="sPending" />

<!--<h2 class="title"><spring:message code="accountlist.approvalRequest.title" /></h2>-->

<!-- selectbox: order by -->
<div class="action-box">
	<mod:account-order template="approval-request" skin="print"/>
	<%-- C16 ProductListTools --%>
	<mod:productlist-tools template="approval-request" />
</div>

<!-- list: order approval list -->
<ul class="data-list">
	<c:choose>
		<c:when test="${empty searchPageData.results}">
			<li class="row">
				<span class="no-approval-requests"><spring:theme code="text.account.orderApprovalDashBoard.noOrders" text="You have no order approval request" /></span>
			</li>
		</c:when>
		<c:otherwise>
			<c:forEach items="${searchPageData.results}" var="approvalRequest">
				<c:choose>
					<c:when test="${approvalRequest.b2bOrderData.status == 'APPROVED'}">
						<c:set var="iconStatus" value="ok" />
					</c:when>
					<c:otherwise>
						<c:set var="iconStatus" value="nok" />
					</c:otherwise>
				</c:choose>

				<c:choose>
					<c:when test="${approvalRequest.b2bOrderData.status == 'REJECTED'}">
						<c:set var="orderStatus" value="${sRejected}" />
						<c:set var="highlightClass" value="" />
					</c:when>
					<c:when test="${approvalRequest.b2bOrderData.status == 'APPROVED'}">
						<c:set var="orderStatus" value="${sApproved}" />
						<c:set var="highlightClass" value="" />
					</c:when>
					<c:otherwise>
						<c:set var="orderStatus" value="${sPending}" />
						<c:set var="highlightClass" value="highlight" />
					</c:otherwise>
				</c:choose>

				<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
					<c:url value="/my-account/order-approval-requests/order-details/${approvalRequest.b2bOrderData.code}/workflow/${approvalRequest.workflowActionModelCode}" var="myAccountApprovalRequestDetailsUrl" />					
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
					<c:url value="/my-account/order-approval/order-details/${approvalRequest.b2bOrderData.code}/workflow/${approvalRequest.workflowActionModelCode}" var="myAccountApprovalRequestDetailsUrl" />					
				</sec:authorize>
				<li class="row ${highlightClass}">
					<ul class="data cf">
						<li class="entry">
							<dl>
								<dt class="label"><spring:message code="accountlist.approvalRequest.requestedBy" /></dt>
								<dd class="value">${approvalRequest.b2bOrderData.b2bCustomerData.firstName }&nbsp;${approvalRequest.b2bOrderData.b2bCustomerData.lastName}</dd>
								<dt class="label"><spring:message code="accountlist.approvalRequest.requestDate" /></dt>
								<dd class="value"><fmt:formatDate value="${approvalRequest.b2bOrderData.created}" dateStyle="short" timeStyle="short" type="both" /></dd>
							</dl>
						</li>
						<li class="entry">
							<dl>
								<dt class="label"><spring:message code="accountlist.approvalRequest.orderTotal" /></dt>
								<dd class="value"><format:price format="defaultSplit" priceData="${approvalRequest.b2bOrderData.totalPrice}" fallBackCurrency="${approvalRequest.b2bOrderData.totalPrice.currencyIso}" /></dd>
								<dt class="label"><spring:message code="accountlist.approvalRequest.approvalStatus" /></dt>
								<dd class="value ${iconStatus}"><i></i> ${orderStatus}</dd>
							</dl>
						</li>
						<li class="entry">
							<dl>
								<c:if test="${approvalRequest.b2bOrderData.b2bCustomerData.budget.exceededYearlyBudget gt 0}">
									<dt class="label"><spring:message code="accountlist.approvalRequest.yearlyBudgetExceededBy" /></dt>
									<dd class="value neg"><format:price format="defaultSplit" displayValue="${approvalRequest.b2bOrderData.b2bCustomerData.budget.exceededYearlyBudget}" fallBackCurrency="${approvalRequest.b2bOrderData.totalPrice.currencyIso}" /></dd>
								</c:if>
								<c:if test="${approvalRequest.b2bOrderData.b2bCustomerData.budget.exceededOrderBudget gt 0}">
									<dt class="label"><spring:message code="accountlist.approvalRequest.orderBudgetExceededBy" /></dt>
									<dd class="value neg"><format:price format="defaultSplit" displayValue="${approvalRequest.b2bOrderData.b2bCustomerData.budget.exceededOrderBudget}" fallBackCurrency="${approvalRequest.b2bOrderData.totalPrice.currencyIso}" /></dd>
								</c:if>
							</dl>
						</li>
						<li class="action">
							<a href="${myAccountApprovalRequestDetailsUrl}" class="more"><i></i></a>
						</li>
					</ul>
				</li>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</ul>