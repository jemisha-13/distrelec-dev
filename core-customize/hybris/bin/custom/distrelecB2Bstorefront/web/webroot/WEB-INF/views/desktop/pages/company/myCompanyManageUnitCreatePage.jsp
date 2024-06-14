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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/desktop/company"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:if test="${empty saveUrl}">
	<spring:url value="/my-company/organization-management/manage-units/create" var="saveUrl"/>
</c:if>
<c:if test="${empty cancelUrl}">
	<spring:url value="/my-company/organization-management/manage-units/" var="cancelUrl"/>
</c:if>

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
		<common:back/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:myCompanyNav selected="units"/>
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
						<spring:theme code="text.company.manage.units.unit.create" text="Create Child Unit for Unit: {0}" arguments="${unitName}"/>
					</h2>
				</div>
				<div class="item_container">
					<company:b2bUnitForm formUrl="${saveUrl}" b2BUnitForm="${b2BUnitForm}" cancelUrl="${cancelUrl}"/>
				</div>
			</div>
		</div>
	</div>
</template:page>
