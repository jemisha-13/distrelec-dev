<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<spring:message code="approvalbar.status.approvalNeeded" var="sStatusApprovalNeeded" />
<spring:message code="text.store.dateformat" var="datePattern" />

<c:if test="${cartData.b2bCustomerData.budget.exceededYearlyBudget gt 0}">
	<c:set var="yearlyBudgetExceeds" value="${true}" />
</c:if>

<c:if test="${cartData.b2bCustomerData.budget.exceededOrderBudget gt 0}">
	<c:set var="orderBudgetExceeds" value="${true}" />
</c:if>
						
<c:if test="${yearlyBudgetExceeds or orderBudgetExceeds}">
	<c:set var="customer" value="${cartData.b2bCustomerData}" />
	<c:set var="budget" value="${customer.budget}" />

	<div class="wrapper">
		<div class="row">
			<div class="title">
				<h4><spring:theme code="approvalbar.orderNeedsApproval" /></h4>
			</div>
			<div class="col-12 col-md-6">
				<div class="method">
					<p>
						<strong><spring:theme code="approvalbar.requestedBy" /></strong>
					</p>
					<p><c:out value="${customer.name}" /></p>
				</div>
			</div>
			<div class="col-12 col-md-6">
				<div class="method">
					<p>
						<strong><spring:theme code="approvalbar.orderValue" /></strong>
					</p>
					<p><format:price format="defaultSplit" priceData="${cartData.totalPrice}"/></p>
				</div>
			</div>
			<div class="col-12 col-md-6">
				<div class="method">
					<p>
						<strong><spring:theme code="approvalbar.requestedOn" /></strong>
					</p>
					<p><fmt:formatDate pattern="${datePattern}" value="${today}"/></p>
				</div>
			</div>
			<c:if test="${yearlyBudgetExceeds}">
				<div class="col-12 col-md-6">
					<div class="method">
						<p>
							<strong><spring:theme code="approvalbar.yearlyBudget" /></strong>
						</p>
						<p><format:price format="defaultSplit" displayValue="${budget.originalYearlyBudget}" fallBackCurrency="${budget.currency.isocode}" /></p>
					</div>
				</div>
			</c:if>
			<c:if test="${yearlyBudgetExceeds}">
				<div class="col-12 col-md-6">
					<div class="method">
						<p>
							<strong><spring:theme code="approvalbar.orderBudget" /></strong>
						</p>
						<p><format:price format="defaultSplit" displayValue="${budget.orderBudget}" fallBackCurrency="${budget.currency.isocode}" /></p>
					</div>
				</div>
			</c:if>
			<div class="col-12 col-md-6">
				<div class="method">
					<p>
						<strong><spring:theme code="approvalbar.approver" /></strong>
					</p>
					<c:forEach end="0" items="${customer.approvers}" var="approver">
						<p><c:out value="${approver.name}" /></p>
					</c:forEach>
				</div>
			</div>
			<c:if test="${yearlyBudgetExceeds}">
				<div class="col-12 col-md-6">
					<div class="method">
						<p>
							<strong><spring:theme code="approvalbar.yearlyBudgetUsedToDate" /></strong>
						</p>
						<p><format:price format="defaultSplit" displayValue="${budget.yearlyBudgetUsedToDate}" fallBackCurrency="${budget.currency.isocode}" /></p>
					</div>
				</div>
			</c:if>
			<div class="col-12 col-md-6">
				<div class="method method--status">
					<p>
						<strong><spring:theme code="approvalbar.status" /></strong>
					</p>
					<p>
						<i class="fa fa-times">&nbsp;</i>
							${sStatusApprovalNeeded}
					</p>
				</div>
			</div>
			<c:if test="${yearlyBudgetExceeds}">
				<div class="col-12 col-md-6">
					<div class="method">
						<p>
							<strong><spring:theme code="approvalbar.orderExceedsYearlyBudgetBy" /></strong>
						</p>
						<p><fmt:formatNumber type="number" maxFractionDigits="2" value="${cartData.b2bCustomerData.budget.exceededYearlyBudget}" /></p>
					</div>
				</div>
			</c:if>
			<c:if test="${orderBudgetExceeds}">
				<div class="col-12 col-md-6">
					<div class="method">
						<p>
							<strong><spring:theme code="approvalbar.orderExceedsOrderBudgetBy" /></strong>
						</p>
						<p><fmt:formatNumber type="number" maxFractionDigits="2" value="${cartData.b2bCustomerData.budget.exceededOrderBudget}" /></p>
					</div>
				</div>
			</c:if>
			<div class="col-12">
				<div class="method method--submit">
					<div class="row">
						<div class="col-12">
							<p>
								<strong><spring:theme code="approvalbar.pleaseNote" /></strong>
							</p>
							<p><spring:theme code="approvalbar.pleaseNote.notice" /></p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>