<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<template:page pageTitle="${pageTitle}">

	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<div class="span-4 side-content-slot cms_banner_slot">
		<cms:slot var="feature" contentSlot="${slots.SideContent}">
			<cms:component component="${feature}"/>
		</cms:slot>
	</div>

	<div class="span-20 right last">
		<div class="span-10 last">
			<c:url value="/j_spring_security_check" var="loginAndCheckoutAction" />
			<user:login actionNameKey="myCompany.login.myCompanyHome" action="${loginAndCheckoutAction}"/>
		</div>
		<div class="span-20 last">
			<cms:slot var="feature" contentSlot="${slots.MerchantContactContent}">
				<cms:component component="${feature}"/>
			</cms:slot>
		</div>
	</div>

</template:page>