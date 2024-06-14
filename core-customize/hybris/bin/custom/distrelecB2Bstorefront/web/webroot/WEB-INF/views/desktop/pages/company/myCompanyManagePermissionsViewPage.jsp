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

<spring:url
	value="/my-company/organization-management/manage-permissions/edit"
	var="editPermissionDetailsUrl">
	<spring:param name="permissionCode" value="${permissionData.code}" />
</spring:url>
<spring:url
	value="/my-company/organization-management/manage-permissions/enable"
	var="enableUrl">
	<spring:param name="permissionCode" value="${permissionData.code}" />
</spring:url>
<spring:url
	value="/my-company/organization-management/manage-permissions/disable"
	var="disableUrl">
	<spring:param name="permissionCode" value="${permissionData.code}" />
</spring:url>
<spring:url
	value="/my-company/organization-management/manage-units/details"
	var="unitDetailsUrl">
	<spring:param name="unit" value="${permissionData.unit.uid}" />
</spring:url>

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}" />
	</div>
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	<nav:myCompanyNav selected="managePermissions" />
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
					<spring:theme
						code="text.company.managePermissions.viewDetails.page.title"
						text="View Permission: {0}"  arguments="${permissionData.code }"/>
				</h2>
			</div>
			<div class="last">
				<a href="${editPermissionDetailsUrl}">
					<button class="positive right pad_right place-order">
						<spring:theme code="text.company.costCenter.button.displayName" />
					</button>
				</a>
				<c:choose>
					<c:when test="${permissionData.active}">
						<a href="${disableUrl}">
							<button class="positive right pad_right place-order">
								<spring:theme
									code="text.company.budget.disableButton.displayName" />
							</button>
						</a>
					</c:when>
					<c:otherwise>
						<a href="${enableUrl}">
							<button class="positive right pad_right place-order">
								<spring:theme
									code="text.company.budget.enableButton.displayName" />
							</button>
						</a>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="item_container">
				<table class="table_budget">
					<tr>
						<td><spring:theme
								code="text.company.managePermissions.name.label"
								text="Permission Name" />:</td>
						<td>${permissionData.code}</td>
					</tr>
					<tr>
						<td><spring:theme
								code="text.company.managePermissions.unit.label"
								text="Parent Business Unit" />:</td>
						<td><a href="${unitDetailsUrl}">${permissionData.unit.name}</a></td>
					</tr>
					<tr>
						<td><spring:theme
								code="text.company.managePermissions.type.label"
								text="permission type" />:</td>
						<td>${permissionData.b2BPermissionTypeData.name}</td>
					</tr>
					<c:if
						test="${permissionData.b2BPermissionTypeData.code ne 'B2BBudgetExceededPermission' }">
						<c:if
							test="${permissionData.b2BPermissionTypeData.code eq 'B2BOrderThresholdTimespanPermission'}">
							<tr>
								<td><spring:theme
										code="text.company.managePermissions.timespan.label"
										text="Permission timespan" />:</td>
								<td>${permissionData.timeSpan}</td>
							</tr>
						</c:if>
						<tr>
							<td><spring:theme
									code="text.company.managePermissions.currency.label"
									text="Permission currency" />:</td>
							<td>${permissionData.currency.isocode}</td>
						</tr>
						<tr>
							<td><spring:theme
									code="text.company.managePermissions.value.label"
									text="Permission value" />:</td>
							<td>
								<fmt:formatNumber value="${permissionData.value}"/></td>

						</tr>
					</c:if>
					<tr>
						<td><spring:theme
								code="text.company.managePermissions.active.label"
								text="Permission enabled/disabled" />:</td>
						<td><c:choose>
								<c:when test="${permissionData.active}">
									<spring:theme code="text.company.on" text="ON" />
								</c:when>
								<c:otherwise>
									<spring:theme code="text.company.off" text="OFF" />
								</c:otherwise>
							</c:choose></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</template:page>
