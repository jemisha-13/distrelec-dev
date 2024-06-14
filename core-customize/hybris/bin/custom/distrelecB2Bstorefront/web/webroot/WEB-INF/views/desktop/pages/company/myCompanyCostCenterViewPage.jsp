<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>

<spring:message code="text.store.dateformat" var="datePattern" />
<c:if test="${empty editUrl}">
	<spring:url
		value="/my-company/organization-management/manage-costcenters/edit"
		var="editUrl">
		<spring:param name="costCenterCode" value="${b2bCostCenter.code}" />
	</spring:url>
</c:if>
<spring:url
	value="/my-company/organization-management/manage-costcenters/selectBudget"
	var="selectBudgetsForCostcenterUrl">
	<spring:param name="costCenterCode" value="${b2bCostCenter.code}" />
</spring:url>
<spring:url
	value="/my-company/organization-management/manage-costcenters/disable"
	var="disableCostCenterUrl">
	<spring:param name="costCenterCode" value="${b2bCostCenter.code}" />
</spring:url>
<spring:url
	value="/my-company/organization-management/manage-costcenters/enable"
	var="enableCostCenterUrl">
	<spring:param name="costCenterCode" value="${b2bCostCenter.code}" />
</spring:url>
<spring:url
	value="/my-company/organization-management/manage-costcenters/unitDetails"
	var="viewCostCenterUnitUrl">
	<spring:param name="costCenterCode" value="${b2bCostCenter.code}" />
	<spring:param name="unit" value="${b2bCostCenter.unit.uid}" />
</spring:url>

<template:page pageTitle="${pageTitle}">

	<script type="text/javascript">
		/*         */
		$(document).ready(function() {
			if ('${b2bCostCenter.unit.active}' != 'true') {
				$('.disable-button').attr("disabled", true);
				$('.enable-button').attr("disabled", true);
				$('.edit').attr("disabled", true);
			}
		});
		/*   */
	</script>



	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}" />
	</div>
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	<nav:myCompanyNav selected="costCenters" />
	<div class="span-20 last">
		<div class="span-20 wide-content-slot cms_banner_slot">
			<cms:slot var="feature" contentSlot="${slots['TopContent']}">
				<cms:component component="${feature}" />
			</cms:slot>
		</div>
		<div class="item_container_holder">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h2>
					<spring:theme code="text.company.costCenter.viewDetails.label"
						text="View Cost Center: {0}" arguments="${b2bCostCenter.name}" />
				</h2>
			</div>
			<div class="last">
				<a href="${editUrl}">
					<button class="positive right pad_right place-order edit">
						<spring:theme code="text.company.costCenter.button.displayName" />
					</button>
				</a>
				<c:choose>
					<c:when test="${b2bCostCenter.active}">
						<a href="${disableCostCenterUrl}"> <ycommerce:testId
								code="costCenter_DisableCC_button">
								<button
									class="positive right pad_right place-order disable-button">
									<spring:theme code="text.company.costCenter.disable.button" />
								</button>
							</ycommerce:testId>
						</a>
					</c:when>
					<c:otherwise>
						<a href="${enableCostCenterUrl}"> <ycommerce:testId
								code="costCenter_EnableCC_button">
								<button
									class="positive right pad_right place-order enable-button">
									<spring:theme code="text.company.costCenter.enable.button" />
								</button>
							</ycommerce:testId>
						</a>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="item_container">
				<spring:theme code="text.company.manage.costcenters.subtitle"
					arguments="${b2bStore}" />
			</div>
			<div class="item_container">
				<table class="table_budget">
					<tr>
						<td><spring:theme code="text.company.costCenter.id.title"
								text="Cost Center ID" />:</td>
						<td>${b2bCostCenter.code}</td>
					</tr>
					<tr>
						<td><spring:theme code="text.company.costCenter.name.title"
								text="Cost Center Name" />:</td>
						<td>${b2bCostCenter.name}</td>
					</tr>
					<tr>
						<td><spring:theme code="text.company.costCenter.unit.title"
								text="Parent business unit" />:</td>
						<td><a href="${viewCostCenterUnitUrl}">${b2bCostCenter.unit.uid}</a></td>
					</tr>
					<tr>
						<td><spring:theme
								code="text.company.costCenter.currency.title" text="Currency" />:</td>
						<td>${b2bCostCenter.currency.isocode}</td>
					</tr>
				</table>



				<div class="span-15">
					<div class="item_container_holder">
						<div class="title_holder">
							<div class="title">
								<div class="title-top">
									<span></span>
								</div>
							</div>
							<h2>
								<spring:theme code="text.company.manage.costcenter.budget.title"
									text="Budgets" />
							</h2>
						</div>
						<div class="last">
							<a href="${selectBudgetsForCostcenterUrl}">
								<button class="positive right pad_right place-order edit">
									<spring:theme code="text.company.costCenter.button.displayName" />
								</button>
							</a>
						</div>
						<div class="item_container">
							<spring:theme
								code="text.company.manage.costcenters.view.budgets.subtitle" />
						</div>
						<div class="item_container">
							<c:choose>
								<c:when test="${not empty b2bCostCenter.b2bBudgetData}">
									<table>
										<thead>
											<tr>
												<th><span><spring:theme
															code="text.company.budget.id" text="ID" /> </span></th>
												<th><span><spring:theme
															code="text.company.budget.name" text="Budget Name" /> </span></th>
												<th><span><spring:theme
															code="text.company.budget.start" text="Start" /> </span></th>
												<th><span><spring:theme
															code="text.company.budget.currency" text="Currency" /> </span></th>
												<th><span><spring:theme
															code="text.company.budget.amount" text="Budget Amount" />
												</span></th>
											</tr>
										</thead>
										<tbody>


											<c:forEach items="${b2bCostCenter.b2bBudgetData}"
												var="b2bBudget">
												<tr>
													<spring:url
														value="/my-company/organization-management/manage-budgets/view"
														var="viewBudgetUrl">
														<spring:param name="budgetCode" value="${b2bBudget.code}" />
													</spring:url>
													<td><ycommerce:testId code="test_budget_id_link">
															<a href="${viewBudgetUrl}">${b2bBudget.code}</a>
														</ycommerce:testId></td>
													<td>${b2bBudget.name}</td>
													<td><fmt:formatDate value="${b2bBudget.startDate}" pattern="${datePattern}" />
													</td>
													<td>${b2bBudget.currency.isocode}</td>
													<td><fmt:formatNumber value="${b2bBudget.budget}" /></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</c:when>
								<c:otherwise>
									<spring:theme code="text.company.budget.noBudgetMessage" />
									<%--<a href="${selectBudgetsForCostcenterUrl}">
										<spring:theme
											code="text.company.budget.SelectBudgets"/>
									</a> --%>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
			</div>
			<div class="item_container">
				<spring:theme code="text.company.costCenter.enabled.title"
					text="Cost Center Enables/disabled" />
				:
				<c:choose>
					<c:when test="${b2bCostCenter.active}">
						<spring:theme code="text.company.on" text="ON" />
					</c:when>
					<c:otherwise>
						<spring:theme code="text.company.off" text="OFF" />
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</template:page>
