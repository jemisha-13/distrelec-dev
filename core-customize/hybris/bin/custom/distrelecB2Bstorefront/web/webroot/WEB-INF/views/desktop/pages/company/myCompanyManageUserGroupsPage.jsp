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

<spring:url value="/my-company/organization-management/manage-usergroups/create" var="createUserGroupUrl"/>
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
		<div class="item_container_holder">
			<div class="last">
				<a href="${createUserGroupUrl}">
					<ycommerce:testId code="Usergroup_createNew_button">
						<button class="positive right pad_right place-order">
							<spring:theme code="text.company.manageUsergroups.newUserGroupButton"
										  text="Create New Usergroup"/>
						</button>
					</ycommerce:testId>
				</a>
			</div>

			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h2>
					<spring:theme code="text.company.${action}.title" text="All UserGroups"/>
				</h2>
			</div>
			<div class="item_container">
				<spring:theme code="text.company.${action}.subtitle" />
			</div>
			<div class="item_container">
				<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}"
								searchUrl="/my-company/organization-management/manage-usergroups?sort=$
								{searchPageData.pagination.sort}"
								msgKey="text.company.${action}.page"/>
				<table id="order_history">
					<thead>
					<tr>
						<th id="header1">
							<spring:theme code="text.company.column.id.name" text="ID"/>
						</th>
						<th id="header2">
							<spring:theme code="text.company.column.name.name" text="Name"/>
						</th>
						<th id="header3">
							<spring:theme code="text.company.column.parentUnit.name" text="Parent unit"/>
						</th>
						<th id="header4"><spring:theme code="text.company.status.title" text="Status"/></th>

					</tr>
					</thead>
					<tbody>
					<c:forEach items="${searchPageData.results}" var="group">
						<tr id="row-${result.normalizedId}">
							<td headers="header1">
								<spring:url
									value="/my-company/organization-management/manage-usergroups/details/"
									var="viewUrl">
									<spring:param name="usergroup" value="${group.uid}"/>
								</spring:url>
								<spring:url
									value="/my-company/organization-management/manage-units/details/"
									var="viewUnitUrl">
									<spring:param name="unit" value="${group.unit.uid}"/>
								</spring:url>
								<ycommerce:testId code="${action}_uid_link">
									<p><a href="${viewUrl}">${group.uid}</a></p>
								</ycommerce:testId>
							</td>
							<td headers="header2">
								<ycommerce:testId code="${action}_name_link">
									<p>${group.name}</p>
								</ycommerce:testId>
							</td>
							<td headers="header3">
								<ycommerce:testId code="${action}_b2bunit_label">
									<p><a href="${viewUnitUrl}">${group.unit.name}</a></p>
								</ycommerce:testId>
							</td>
							<td headers="header4">
								<ycommerce:testId code="costCenter_status_label">
									<p>
										<spring:theme code="text.company.status.active.${fn:length(group.members)>0}"/>
									</p>
								</ycommerce:testId>
							</td>

						</tr>
					</c:forEach>
					</tbody>
				</table>

				<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}"
								searchUrl="/my-company/organization-management/manage-usergroups?sort=$
								{searchPageData.pagination.sort}"
								msgKey="text.company.${action}.page"/>
				<c:if test="${empty searchPageData.results}">
					<p>No entries.</p>
				</c:if>
			</div>
		</div>
	</div>
</template:page>
