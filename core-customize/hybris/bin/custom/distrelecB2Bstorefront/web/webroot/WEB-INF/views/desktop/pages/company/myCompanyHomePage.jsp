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

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:myCompanyNav selected="organizationManagement"/>
	<div class="span-20 last">
		<div class="span-20 wide-content-slot cms_banner_slot">
			<cms:slot var="feature" contentSlot="${slots['TopContent']}">
				<cms:component component="${feature}"/>
			</cms:slot>
		</div>
		<div class="span-20 last">
			<div class="cust_acc">
				<div class="cust_acc_tile">
					<spring:url value="/my-company/organization-management/manage-budgets" var="encodedUrl" />
					<span>
						<a href="${encodedUrl}"><theme:image code="img.company.budgets" alt="Manage Budgets" title="Manage budgets"/></a>
					</span>
					<h1><a href="${encodedUrl}"><spring:theme code="text.company.manageBudgets" text="Manage Budgets"/></a></h1>
					<ul>
						<ycommerce:testId code="mycompany_options_managebudgets_groupbox">
							<spring:url value="/my-company/organization-management/manage-budgets/add" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.company.addNewBudgets" text="Add new budgets"/></a></li>
							<spring:url value="/my-company/organization-management/manage-budgets" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.company.editOrDisableBudgets" text="Edit or disable budgets"/></a></li>
						</ycommerce:testId>
					</ul>
				</div>
				<div class="cust_acc_tile">
					<spring:url value="/my-company/organization-management/manage-costcenters" var="encodedUrl" />
					<span>
						<a href="${encodedUrl}"><theme:image code="img.company.costCenters" alt="Manage Cost Centers" title="Manage Cost Centers"/></a>
					</span>
					<h1><a href="${encodedUrl}"><spring:theme code="text.company.manageCostCenters" text="Manage Cost Centers"/></a></h1>
					<ul>
						<ycommerce:testId code="mycompany_options_managecostcenters_groupbox">
							<spring:url value="/my-company/organization-management/manage-costcenters/add" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.company.addNewCostCenters" text="Add new cost center"/></a></li>
							<spring:url value="/my-company/organization-management/manage-costcenters" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.company.editOrDisableCostCenters" text="Edit or disable cost centers"/></a></li>
						</ycommerce:testId>
					</ul>
				</div>
				<div class="cust_acc_tile">
					<spring:url value="/my-company/organization-management/manage-units" var="encodedUrl" />
					<span>
						<a href="${encodedUrl}"><theme:image code="img.company.units" alt="Manage Units" title="Manage Units"/></a>
					</span>
					<h1><a href="${encodedUrl}"><spring:theme code="text.company.manage.units" text="Manage Units"/></a></h1>
					<ul>
						<ycommerce:testId code="mycompany_options_manageunits_groupbox">
							<spring:url value="/my-company/organization-management/manage-units/create" var="encodedUrl" >
								<spring:param name="unit" value="${unitUid}"/>
							</spring:url>
							<li><a href="${encodedUrl}"><spring:theme code="text.company.createNewUnits" text="Create new units"/></a></li>
							<spring:url value="/my-company/organization-management/manage-units" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.company.editOrDisableUnits" text="Edit or disable units"/></a></li>
						</ycommerce:testId>
					</ul>
				</div>
				<div class="cust_acc_tile">
					<spring:url value="/my-company/organization-management/manage-users" var="encodedUrl" />
					<span>
						<a href="${encodedUrl}"><theme:image code="img.company.users" alt="Manage users" title="Manage Users"/></a>
					</span>
					<h1><a href="${encodedUrl}"><spring:theme code="text.company.manageUsers" text="Manage Users"/></a></h1>
					<ul>
						<ycommerce:testId code="mycompany_options_manageusers_groupbox">
							<spring:url value="/my-company/organization-management/manage-users/create" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.company.addNewUsers" text="Add new users"/></a></li>
							<spring:url value="/my-company/organization-management/manage-users" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.company.editOrDisableUsers" text="Edit or disable users"/></a></li>
						</ycommerce:testId>
					</ul>
				</div>
				<div class="cust_acc_tile">
					<spring:url value="/my-company/organization-management/manage-usergroups" var="encodedUrl" />
					<span>
						<a href="${encodedUrl}"><theme:image code="img.company.usergroups" alt="Manage Usergroups" title="Manage Usergroups"/></a>
					</span>
					<h1><a href="${encodedUrl}"><spring:theme code="text.company.manageUsergroups" text="Manage Usergroups"/></a></h1>
					<ul>
						<ycommerce:testId code="mycompany_options_manageusergroups_groupbox">
							<spring:url value="/my-company/organization-management/manage-usergroups/create" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.company.addNewUsergroup" text="Add new usergroup"/></a></li>
							<spring:url value="/my-company/organization-management/manage-usergroups" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.company.editOrDisableUsergroups" text="Edit or disable order usergroups"/></a></li>
						</ycommerce:testId>
					</ul>
				</div>
				<div class="cust_acc_tile">
					<spring:url value="/my-company/organization-management/manage-permissions" var="encodedUrl" />
					<span>
						<a href="${encodedUrl}"><theme:image code="img.company.permissions" alt="Manage permissions" title="Manage Permissions"/></a>
					</span>
					<h1><a href="${encodedUrl}"><spring:theme code="text.company.managePermissions" text="Manage Permissions"/></a></h1>
					<ul>
						<ycommerce:testId code="mycompany_options_managepermissions_groupbox">
							<spring:url value="/my-company/organization-management/manage-permissions/add" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.company.addNewPermission" text="Add new permission"/></a></li>
							<spring:url value="/my-company/organization-management/manage-permissions" var="encodedUrl" />
							<li><a href="${encodedUrl}"><spring:theme code="text.company.editOrDisablePermissions" text="Edit or disable permissions"/></a></li>
						</ycommerce:testId>
					</ul>
				</div>
			</div>
		</div>
	</div>
</template:page>
