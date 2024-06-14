<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<spring:message code="checkoutorderbudgetapprovalbar.status.REJECTED" var="sRejected" />
<spring:message code="checkoutorderbudgetapprovalbar.status.APPROVED" var="sApproved" />
<spring:message code="checkoutorderbudgetapprovalbar.status.PENDING_APPROVAL" var="sPending" />

<c:set var="customer" value="${orderData.b2bCustomerData}" />
<c:set var="budget" value="${customer.budget}" />

<c:if test="${budget.exceededYearlyBudget gt 0}">
	<c:set var="yearlyBudgetExceeds" value="${true}" />
</c:if>

<c:if test="${budget.exceededOrderBudget gt 0}">
	<c:set var="orderBudgetExceeds" value="${true}" />
</c:if>

<c:choose>
	<c:when test="${orderData.status == 'APPROVED'}">
		<c:set var="iconStatus" value="ok" />
	</c:when>
	<c:otherwise>
		<c:set var="iconStatus" value="nok" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${orderData.status == 'REJECTED'}">
		<c:set var="orderStatus" value="${sRejected}" />
	</c:when>
	<c:when test="${orderData.status == 'APPROVED'}">
		<c:set var="orderStatus" value="${sApproved}" />
	</c:when>
	<c:otherwise>
		<c:set var="orderStatus" value="${sPending}" />
	</c:otherwise>
</c:choose>

<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
	<a class="btn btn-secondary btn-back" href="<c:url value="/my-account/order-approval" />"><spring:message code="text.back" text="Back" /><i></i></a>
</sec:authorize>
<sec:authorize access="!hasRole('ROLE_B2BADMINGROUP') and hasRole('ROLE_B2BCUSTOMERGROUP')">
	<a class="btn btn-secondary btn-back" href="<c:url value="/my-account/order-approval-requests" />"><spring:message code="text.back" text="Back" /><i></i></a>
</sec:authorize>

<h3 class="base title"><spring:theme code="approvalbar.orderApprovalInformation" /></h3>
<ul class="data cf">
	<li class="entry">
		<dl>
			<dt class="label"><spring:theme code="approvalbar.requestedBy" /></dt>
			<dd class="value"><c:out value="${customer.name}" /></dd>
			<dt class="label"><spring:theme code="approvalbar.orderValue" /></dt>
			<dd class="value"><format:price format="defaultSplit" priceData="${orderData.totalPrice}" fallBackCurrency="${orderData.totalPrice.currencyIso}" /></dd>
		</dl>
	</li>
	<li class="entry">
		<dl>
			<dt class="label"><spring:theme code="approvalbar.requestedOn" /></dt>
			<dd class="value"><fmt:formatDate value="${orderData.created}" dateStyle="short" timeStyle="short" type="both" /></dd>
			<c:if test="${yearlyBudgetExceeds}">
				<dt class="label"><spring:theme code="approvalbar.yearlyBudget" /></dt>
				<dd class="value"><format:price format="defaultSplit" displayValue="${budget.originalYearlyBudget}" fallBackCurrency="${budget.currency.isocode}" /></dd>
			</c:if>
			<c:if test="${orderBudgetExceeds}">
				<dt class="label"><spring:theme code="approvalbar.orderBudget" /></dt>
				<dd class="value"><format:price format="defaultSplit" displayValue="${budget.orderBudget}" fallBackCurrency="${budget.currency.isocode}" /></dd>
			</c:if>
		</dl>
	</li>
	<li class="entry">
		<dl>
			<dt class="label"><spring:theme code="approvalbar.approver" /></dt>
			<c:forEach end="0" items="${customer.approvers}" var="approver">
				<dd class="value"><c:out value="${approver.name}" /></dd>
			</c:forEach>
			<dt class="label"><spring:theme code="approvalbar.yearlyBudgetUsedBeforeOrder" /></dt>
			<dd class="value${budget.yearlyBudgetUsedToDate gt budget.originalYearlyBudget ? ' neg' : ''}"><format:price format="defaultSplit" displayValue="${budget.yearlyBudgetUsedToDate}" fallBackCurrency="${budget.currency.isocode}" /></dd>
		</dl>
	</li>
	<li class="entry">
		<dl>
			<dt class="label"><spring:theme code="approvalbar.status" /></dt>
			<dd class="value ${iconStatus}"><i></i> ${orderStatus}</dd>
			<c:if test="${yearlyBudgetExceeds}">
				<dt class="label"><spring:theme code="approvalbar.orderExceedsYearlyBudgetBy" /></dt>
				<dd class="value neg"><format:price format="defaultSplit" displayValue="${budget.exceededYearlyBudget}" fallBackCurrency="${budget.currency.isocode}" /></dd>
			</c:if>
			<c:if test="${orderBudgetExceeds}">
				<dt class="label"><spring:theme code="approvalbar.orderExceedsOrderBudgetBy" /></dt>
				<dd class="value neg"><format:price format="defaultSplit" displayValue="${budget.exceededOrderBudget}" fallBackCurrency="${budget.currency.isocode}" /></dd>
			</c:if>
		</dl>
	</li>
	<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
		<c:if test="${not empty orderApprovalData.approvalComments}">
			<li class="entry">
				<dl>
					<dt class="label">${orderStatus}&nbsp;<spring:theme code="approvalbar.on" /></dt>
					<dd class="value"><fmt:formatDate value="${orderApprovalData.decisionDate}" dateStyle="long" timeStyle="short" type="both" /></dd>
				</dl>
			</li>
		</c:if>
	</sec:authorize>
	<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
		<li class="entry action">
			<dl>
				<dt class="label"><spring:theme code="approvalbar.pleaseNote" /></dt>
				<dd class="value"><spring:theme code="approvalbar.pleaseNote.notice" /></dd>
			</dl>
			<a href="#" class="btn btn-secondary btn-reject" data-action="/my-account/order-approval/approval-decision" data-model-attribute="orderApprovalDecisionForm" data-workflow-code="${orderApprovalData.workflowActionModelCode}" data-decision="REJECT"><spring:theme code="approvalbar.button.reject" /></a>
			<a href="#anchor-calc-box-total" class="btn btn-primary btn-approve"><span><spring:theme code="approvalbar.button.approveAndSubmit" /></span><i class="icon-arrow-down"></i></a>
		</li>
	</sec:authorize>
	<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
		<li class="entry action">
			<mod:toolsitem template="toolsitem-notice" skin="notice" message="${orderApprovalData.approvalComments}" htmlClasses="tooltip-hover" />
		</li>
	</sec:authorize>
</ul>
<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
	<mod:lightbox-reject-order />
</sec:authorize>