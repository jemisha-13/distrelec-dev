<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<c:set var="isInter" value="${siteUid eq 'distrelec_FR'}" />

<div class="action-box">
	<a href="/my-account/company/create/newemployee" class="btn btn-primary btn-add  <c:if test="${isInter eq true}">hidden</c:if>"><i></i> <spring:message code="accountlist.userManagement.button.addUser" /></a>
	<mod:account-order template="user-management" skin="print" />
</div>
<ul class="data-list">
	<c:forEach items="${b2bCustomers}" var="customer">
		<li class="row">
			<ul class="data cf">
				<li class="entry">
					<dl>
						<dt class="label"><spring:message code="accountlist.userManagement.userName" /></dt>
						<dd class="value"><c:out value="${customer.name}" /> </dd>
						<c:if test="${siteUid ne 'distrelec_FR'}">
							<dt class="label"><spring:message code="accountlist.userManagement.userStatus" /></dt>
							<c:if test="${customer.doubleOptinActivated and !customer.loginDisabled}">
								<dd class="value ok"><i></i> <spring:message code="accountlist.userManagement.userStatus.active" /></dd>
							</c:if>
							<c:if test="${!customer.doubleOptinActivated and !customer.loginDisabled}">
								<dd class="value nok"><i></i> <spring:message code="accountlist.userManagement.userStatus.pending" /></dd>
							</c:if>
							<c:if test="${!customer.doubleOptinActivated and customer.loginDisabled}">
								<dd class="value nok"><i></i> <spring:message code="accountlist.userManagement.userStatus.deactivated" /></dd>
							</c:if>
						</c:if>
					</dl>
				</li>
				<c:if test="${siteUid ne 'distrelec_FR'}">
					<li class="entry">
						<dl>
							<dt class="label"><spring:message code="accountlist.userManagement.budgetApprover" /></dt>
							<dd class="value"><c:out value="${customer.approvers[0].name}" /></dd>
							<dt class="label">&nbsp;</dt>
							<dd class="value">&nbsp;</dd>
						</dl>
					</li>
					<li class="entry">
						<dl>
							<c:choose>
								<c:when test="${not empty customer.budget.originalYearlyBudget && not empty customer.budget.orderBudget}">
									<dt class="label"><spring:message code="accountlist.userManagement.yearlyBudget" /></dt>
									<dd class="value"><format:price format="defaultSplit" displayValue="${customer.budget.originalYearlyBudget}" fallBackCurrency="${customer.budget.currency.isocode}" /></dd>
									<dt class="label"><spring:message code="accountlist.userManagement.orderBudget" /></dt>
									<dd class="value"><format:price format="defaultSplit" displayValue="${customer.budget.orderBudget}" fallBackCurrency="${customer.budget.currency.isocode}" /></dd>
								</c:when>
								<c:when test="${not empty customer.budget.originalYearlyBudget && empty customer.budget.orderBudget}">
									<dt class="label"><spring:message code="accountlist.userManagement.yearlyBudget" /></dt>
									<dd class="value"><format:price format="defaultSplit" displayValue="${customer.budget.originalYearlyBudget}" fallBackCurrency="${customer.budget.currency.isocode}" /></dd>
									<dt class="label">&nbsp;</dt>
									<dd class="value">&nbsp;</dd>
								</c:when>
								<c:when test="${empty customer.budget.yearlyBudget && not empty customer.budget.orderBudget}">
									<dt class="label"><spring:message code="accountlist.userManagement.orderBudget" /></dt>
									<dd class="value"><format:price format="defaultSplit" displayValue="${customer.budget.orderBudget}" fallBackCurrency="${customer.budget.currency.isocode}" /></dd>
									<dt class="label">&nbsp;</dt>
									<dd class="value">&nbsp;</dd>
								</c:when>
								<c:otherwise>
									<dt class="label">&nbsp;</dt>
									<dd class="value">&nbsp;</dd>
									<dt class="label">&nbsp;</dt>
									<dd class="value">&nbsp;</dd>
								</c:otherwise>
							</c:choose>
						</dl>
					</li>
					<li class="entry">
						<dl>
							<c:choose>
								<c:when test="${not empty customer.budget.yearlyBudget}">
									<dt class="label"><spring:message code="accountlist.userManagement.yearlyBudgetStatus" /></dt>
									<dd class="value${customer.budget.yearlyBudget < 0 ? ' neg' : ''}"><format:price format="defaultSplit" displayValue="${customer.budget.yearlyBudget}" fallBackCurrency="${customer.budget.currency.isocode}" /></dd>
									<dt class="label">&nbsp;</dt>
									<dd class="value">&nbsp;</dd>
								</c:when>
								<c:otherwise>
									<dt class="label">&nbsp;</dt>
									<dd class="value">&nbsp;</dd>
									<dt class="label">&nbsp;</dt>
									<dd class="value">&nbsp;</dd>
								</c:otherwise>
							</c:choose>
						</dl>
					</li>
					<li class="action">
						<a href="/my-account/company/edit/employee/${customer.customerId}" class="more"><i></i></a>
					</li>
				</c:if>
			</ul>
		</li>
	</c:forEach>
</ul>
