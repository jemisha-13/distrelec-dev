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
<spring:url value="/my-company/organization-management/manage-units/disable"
			var="disableUrl">
	<spring:param name="unit" value="${unit.uid}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/details"
			var="cancelUrl">
	<spring:param name="unit" value="${unit.uid}"/>
</spring:url>

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
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
						<spring:theme code="text.company.manage.units.unit.disable" text="Confirm Disable"
									  arguments="${unit.uid}"/>
					</h2>
				</div>
				<div class="item_container">
					<p>
						<spring:theme code="text.company.manage.units.disableUnit.confirmation"
									  text="Doing this will disable disable unit {0} and all it descendent
										  Units as well as all related Users, Budgets and Cost Centers. Do you want
										   to proceed?"
									  arguments="${unit.uid}"/>
					</p>

					<div style="display: block; clear: both;">
						<form:form action="${disableUrl}">
							<a class="cancel_button" href="${cancelUrl}">
								<button type="button" class="form">
									<spring:theme code="b2bunit.no.button" text="No"/>
								</button>
							</a>
							<button type="submit" class="form">
								<spring:theme code="b2bunit.yes.button" text="Yes"/>
							</button>
						</form:form>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>
