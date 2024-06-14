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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form"%>

<spring:url value="/my-company/organization-management/manage-permissions" var="cancelUrl" />

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
				<h2><spring:theme code="text.company.managePermissions.create.permission.title" text="Create Permission" /></h2>
			</div>
			<div class="item_container">
				<p class="required">
					<spring:theme code="form.required" text="Fields marked * are required"/>
				</p>
				
				<spring:theme code="text.company.managePermissions.create.step1" text="Step 1 of 2"/>
				
				<form:form id="b2BPermissionTypeSelectionForm" modelAttribute="b2BPermissionTypeSelectionForm" method="POST">
				<formUtil:formSelectBox idKey="text.company.managePermissions.type.label" labelKey="text.company.managePermissions.type.label" skipBlankMessageKey="text.company.managePermissions.selectBox.permissionType" mandatory="true" path="b2BPermissionType" items="${ b2bPermissionTypes}" />
						
				<button type="submit" class="positive place-order">
					<spring:theme code="text.company.managePermissions.create.continueButton" text="Continue" />
				</button> 
				
				<a class="cancel_button" href="${cancelUrl}">
					<button  type="button" class="positive place-order left">
						<spring:theme code="text.company.managePermissions.edit.cancelButton" text="Cancel" />
					</button> 
				</a>
				</form:form>
						
			</div>
		</div>
	</div>
</template:page>
