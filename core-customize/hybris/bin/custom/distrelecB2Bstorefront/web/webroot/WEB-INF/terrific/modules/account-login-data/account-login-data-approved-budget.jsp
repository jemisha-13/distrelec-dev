<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/login-data/send" />

<!-- Approved Budget
https://wiki.namics.com/display/distrelint/C203-LoginDataChange
this form is just for users, NOT visible for admin account
!-->
<div class="form-box">
	<form>
		<h2 class="form-title"><spring:theme code="logindata.approvedBudget" /></h2>
		<div class="row base">
			<div class="gu-4">
				<label class="required"><spring:theme code="logindata.approvedBudget.perOrder" /></label>
			</div>
			<div class="gu-4">
				<p class="value"><format:price format="defaultSplit" displayValue="${budget.orderBudget}" fallBackCurrency="${budget.currency.isocode}" /></p>
			</div>
		</div>
		<div class="row base">
			<div class="gu-4">
				<label class="required"><spring:theme code="logindata.approvedBudget.yearly" /></label>
			</div>
			<div class="gu-4">
				<p class="value"><format:price format="defaultSplit" displayValue="${budget.originalYearlyBudget}" fallBackCurrency="${budget.currency.isocode}" /></p>
			</div>
		</div>
		<div class="row base">
			<div class="gu-4">
				<label class="required"><spring:theme code="logindata.approvedBudget.residualBudget" /></label>
			</div>
			<div class="gu-4">
				<p class="value"><format:price format="defaultSplit" displayValue="${budget.yearlyBudget}" fallBackCurrency="${budget.currency.isocode}" /></p>
			</div>
		</div>
	</form>
</div>
