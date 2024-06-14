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
<spring:url value="/my-company/organization-management/manage-budgets/edit" var="editBudgetDetailsUrl">
	<spring:param name="budgetCode" value="${budget.code}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-budgets/enable" var="enableUrl">
	<spring:param name="budgetCode" value="${budget.code}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-budgets/disable" var="disableUrl">
	<spring:param name="budgetCode" value="${budget.code}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-budgets/unitDetails" var="viewBudgetUnitUrl">
	<spring:param name="budgetCode" value="${budget.code}"/>
	<spring:param name="unit" value="${budget.unit.uid}"/>
</spring:url>

<spring:message code="text.store.dateformat" var="datePattern" />

<template:page pageTitle="${pageTitle}">
<script type="text/javascript">
	/*<![CDATA[*/
	$(document).ready(function ()
	{
		if ('${budget.unit.active}' != 'true')
		{
			$('.disable-button').attr("disabled", true);
			$('.enable-button').attr("disabled", true);
			$('.edit').attr("disabled", true);
		}
	});
	/*]]>*/
</script>



	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
		<common:back/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:myCompanyNav selected="budgets"/>
	<div class="span-20 last">
		<div class="span-20 wide-content-slot cms_banner_slot">
			<cms:slot var="feature" contentSlot="${slots['TopContent']}">
				<cms:component component="${feature}"/>
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
					<spring:theme code="text.company.budget.viewPage.label"
								  text="View Budget: {0}" arguments="${budget.name}"/>
				</h2>
			</div>
			<div class="last">
				<a href="${editBudgetDetailsUrl}">
					<button class="positive right pad_right place-order edit" id=EditBudget_button>
						<spring:theme code="text.company.costCenter.button.displayName"/>
					</button>
				</a>
				<c:choose>
					<c:when test="${budget.active}">
						<a href="${disableUrl}">
							<button class="positive right pad_right place-order disable-button" id="DisableBudget_button">
								<spring:theme code="text.company.budget.disableButton.displayName"/>
							</button>
						</a>
					</c:when>
					<c:otherwise>
						<a href="${enableUrl}">
							<button class="positive right pad_right place-order enable-button" id="EnableBudget_button">
								<spring:theme code="text.company.budget.enableButton.displayName"/>
							</button>
						</a>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="item_container">
				<table class="table_budget">
					<spring:theme code="text.company.manage.budget.view.subtitle"  arguments="${b2bStore}"/>
				</table>
				<table class="table_budget">
					<tr>
						<td><spring:theme code="text.company.budget.budgetId" text="Budget ID"/>: </td>
						<td>${budget.code}</td>
					</tr>
					<tr>
						<td><spring:theme code="text.company.budget.name" text="Budget Name"/>: </td>
						<td>${budget.name}</td>
					</tr>
					<tr>
						<td><spring:theme code="text.company.budget.unit" text="Parent business unit"/>: </td>
						<td><a href="${viewBudgetUnitUrl}">${budget.unit.name}</a></td>
					</tr>
					<tr>
						<td><spring:theme code="text.company.budget.startDate" text="Start Date"/>: </td>
						<td><fmt:formatDate value="${budget.startDate}" pattern="${datePattern}" /></td>
					</tr>
					<tr>
						<td><spring:theme code="text.company.budget.endDate" text="End Date"/>: </td>
						<td><fmt:formatDate value="${budget.endDate}" pattern="${datePattern}" /></td>
					</tr>
					<tr>
						<td><spring:theme code="text.company.budget.currency" text="Currency"/>: </td>
						<td>${budget.currency.isocode}</td>
					</tr>
					<tr>
						<td><spring:theme code="text.company.budget.amount" text="Budget Amount"/>: </td>
						<td><fmt:formatNumber value="${budget.budget}"/></td>
					</tr>
					<tr>
						<td><spring:theme code="text.company.budget.enabledDisabled" text="Budget enabled/disabled"/>: </td>
						<td><c:choose>
								<c:when test="${budget.active}">
									<spring:theme code="text.company.on" text="ON"/>
								</c:when>
								<c:otherwise>
									<spring:theme code="text.company.off" text="OFF"/>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					
				</table>
				<div class="span-10">
					<div class="item_container_holder">
						<div class="title_holder">
							<div class="title">
								<div class="title-top">
									<span></span>
								</div>
							</div>
							<h2>
								<spring:theme code="text.company.budget.viewPage.costCenters" text="Cost Centers"/>
							</h2>
						</div>
						<div class="item_container">
							<spring:theme code="text.company.manage.budget.view.costcenter.subtitle" arguments="${b2bStore}"/>
							<c:choose>
								<c:when test="${not empty budget.costCenters}">
									<table>
										<thead>
										<tr>
											<th><span><spring:theme code="text.company.costCenter.id"
																	text="ID"/></span>
											</th>
											<th><span><spring:theme code="text.company.costCenter.name"
																	text="Name"/></span></th>
										</tr>
										</thead>
										<tbody>
										<c:forEach items="${budget.costCenters}" var="costCenter">
											<tr>
												<td>
													<spring:url
														value="/my-company/organization-management/manage-costcenters/view"
														var="viewCostCenterUrl">
														<spring:param name="costCenterCode" value="${costCenter.code}"/>
													</spring:url>
													<a href="${viewCostCenterUrl}">
														${costCenter.code}
													</a>
												</td>
												<td>${costCenter.name}</td>
											</tr>
										</c:forEach>
										</tbody>
									</table>
								</c:when>
								<c:otherwise>
									<spring:theme code="text.company.budget.noCostCentersFound.message"/>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>
