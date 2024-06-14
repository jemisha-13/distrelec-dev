<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="selected" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="span-4">
	<div class="span-4 nav_column">
		<div class="title_holder">
			<div class="title">
				<div class="title-top">
					<span></span>
				</div>
			</div>
			<h2><spring:theme code="text.company.myCompany" text="My Company"/></h2>
		</div>
		<div class="item">
			<ul class="facet_block indent">
				<li class='${selected eq 'organizationManagement' ? 'nav_selected' : ''}'>
					<c:url value="/my-company/organization-management/" var="encodedUrl" />
					<ycommerce:testId code="myCompany_organizationManagement_navLink">
						<a href="${encodedUrl}"><spring:theme code="text.company.organizationManagement" text="Organization Management"/></a>
					</ycommerce:testId>
				</li>
				<li class='${selected eq 'budgets' ? 'nav_selected' : ''}'>
					<c:url value="/my-company/organization-management/manage-budgets/" var="encodedUrl" />
					<ycommerce:testId code="myCompany_budgets_navLink">
						<a href="${encodedUrl}"><spring:theme code="text.company.manageBudgets" text="Manage Budgets"/></a>
					</ycommerce:testId>
				</li>
				<li class='${selected eq 'costCenters' ? 'nav_selected' : ''}'>
					<c:url value="/my-company/organization-management/manage-costcenters/" var="encodedUrl" />
					<ycommerce:testId code="myCompany_costCenters_navLink">
						<a href="${encodedUrl}"><spring:theme code="text.company.manageCostCenters" text="Manage Cost Centers"/></a>
					</ycommerce:testId>
				</li>
				<li class='${selected eq 'units' ? 'nav_selected' : ''}'>
					<c:url value="/my-company/organization-management/manage-units/" var="encodedUrl" />
					<ycommerce:testId code="myCompany_units_navLink">
						<a href="${encodedUrl}"><spring:theme code="text.company.manage.units" text="Manage Business Units"/></a>
					</ycommerce:testId>
				</li>
				<li class='${selected eq 'users' ? 'nav_selected' : ''}'>
					<c:url value="/my-company/organization-management/manage-users/" var="encodedUrl" />
					<ycommerce:testId code="myCompany_users_navLink">
						<a href="${encodedUrl}"><spring:theme code="text.company.manageUsers" text="Manage Users"/></a>
					</ycommerce:testId>
				</li>
				<li class='${selected eq 'manageUsergroups' ? 'nav_selected' : ''}'>
					<c:url value="/my-company/organization-management/manage-usergroups/" var="encodedUrl" />
					<ycommerce:testId code="myCompany_usergroups_navLink">
						<a href="${encodedUrl}"><spring:theme code="text.company.manageUsergroup" text="Manage Usergroups"/></a>
					</ycommerce:testId>
				</li>
				<li class='${selected eq 'managePermissions' ? 'nav_selected' : ''}'>
					<c:url value="/my-company/organization-management/manage-permissions/" var="encodedUrl" />
					<ycommerce:testId code="myCompany_orderThresholds_navLink">
						<a href="${encodedUrl}"><spring:theme code="text.company.managePermissions" text="Manage Permissions"/></a>
					</ycommerce:testId>
				</li>
			</ul>
		</div>
	</div>
	<div class="span-4 side-content-slot cms_banner_slot">
		<cms:slot var="feature" contentSlot="${slots['SideContent']}">
			<cms:component component="${feature}"/>
		</cms:slot>
	</div>
</div>
