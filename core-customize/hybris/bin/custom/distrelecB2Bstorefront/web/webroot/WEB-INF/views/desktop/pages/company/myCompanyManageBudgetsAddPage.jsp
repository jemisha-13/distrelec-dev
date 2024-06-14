<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/desktop/company"%>

<c:if test="${empty saveUrl}">
	<spring:url value="/my-company/organization-management/manage-budgets" var="cancelUrl"/>
	<spring:url value="/my-company/organization-management/manage-budgets/add" var="saveUrl"/>
</c:if>

<template:page pageTitle="${pageTitle}">
	<spring:theme code="text.store.dateformat.datepicker.selection" text="mm/dd/yy" var="dateForForDatePicker"/>
	<script type="text/javascript">
		/*<![CDATA[*/
		$(document).ready(function()
		{
			$("#budgetStartDate").datepicker({dateFormat: '${dateForForDatePicker}'});
			$("#budgetEndDate").datepicker({dateFormat: '${dateForForDatePicker}'});
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
					<spring:theme code="text.company.budget.create.title.label" text="Create Budget"/>
				</h2>
			</div>
			<div class="item_container">
				<company:b2bBudgetForm cancelUrl="${cancelUrl}" saveUrl="${saveUrl}" b2BBudgetForm="${b2BBudgetForm}"/>
			</div>
		</div>
	</div>
</template:page>
