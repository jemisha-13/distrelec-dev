<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

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

		<div class="span-20 top-content-slot cms_banner_slot last">
			<cms:slot var="feature" contentSlot="${slots.TopContent}">
				<cms:component component="${feature}"/>
			</cms:slot>
		</div>

		<div class="span-10 last">
			<c:url value="/j_spring_security_check" var="loginAction" />
			<user:login actionNameKey="login.login" action="${loginAction}"/>
		</div>
		
	</div>
	
	<div class="span-20 right last">
		
		<cms:slot var="feature" contentSlot="${slots.MerchantContactContent}">
			<div class="span-10 login-info">
				<cms:component component="${feature}"/>
			</div>
		</cms:slot>
	</div>
	

</template:page>