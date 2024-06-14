<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/desktop/company" %>
<spring:url value="/my-company/organization-management/manage-usergroups/edit"
			var="formUrl">
	<spring:param name="usergroup" value="${b2BUserGroupForm.originalUid}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-usergroups/details"
			var="cancelUrl">
	<spring:param name="usergroup" value="${b2BUserGroupForm.originalUid}"/>
</spring:url>
<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
		<common:back/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:myCompanyNav selected="manageUsergroups"/>
	<div class="span-20 last">
		<div class="span-20 wide-content-slot cms_banner_slot">
			<cms:slot var="feature" contentSlot="${slots['TopContent']}">
				<cms:component component="${feature}"/>
			</cms:slot>
		</div>
		<div class="span-20 last">
			<div class="item_container_holder">
				<div class="title_holder">
					<div class="title">
						<div class="title-top">
							<span></span>
						</div>
					</div>
					<h2>
						<spring:theme code="text.company.manageUsergroups.unit.edit" text="Edit Usergroup"/>
					</h2>
				</div>
				<div class="item_container">
					<company:b2bUserGroupForm formUrl="${formUrl}" b2BUserGroupForm="${b2BUserGroupForm}" cancelUrl="${cancelUrl}"/>
				</div>
			</div>
		</div>
	</div>
</template:page>
