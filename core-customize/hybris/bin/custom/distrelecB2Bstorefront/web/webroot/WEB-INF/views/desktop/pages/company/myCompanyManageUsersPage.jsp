<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<spring:url value="/my-company/organization-management/manage-users/create"
			var="manageUsersUrl"/>
<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:myCompanyNav selected="users"/>
	<div class="span-20 last">
		<div class="item_container_holder">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h2>
					<spring:theme code="text.company.manageusers.label" text="All Users"/>
				</h2>
			</div>
			<div class="last">
				<a href="${manageUsersUrl}">
					<ycommerce:testId code="User_AddUser_button">
						<button class="positive right pad_right place-order">
							<spring:theme code="text.company.manageUser.button.create" text="Create New User"/>
						</button>
					</ycommerce:testId>
				</a>
			</div>
			<div class="item_container">
					<spring:theme code="text.company.manageusers.subtitle" arguments="${b2bStore}"/>
			</div>
			<div class="item_container">
				<c:if test="${not empty searchPageData.results}">
					<p>
						<spring:theme code="text.company.manageUser.viewUsers" text="View Users"/>
					</p>
					<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}"

										searchUrl="/my-company/organization-management/manage-users?sort=${searchPageData.pagination.sort}"
									msgKey="text.company.manageUser.pageAll"/>
					<form>
						<table id="manage_user">
							<thead>
							<tr>
								<th id="header1">
									<spring:theme code="text.company.column.name.name" text="Name"/>
								</th>
								<th id="header2">
									<spring:theme code="text.company.column.roles.name" text="Roles"/>
								</th>
								<th id="header3">
									<spring:theme code="text.company.column.parentUnit.name" text="Parent Unit"/>
								</th>
								<th id="header4">
									<spring:theme code="text.company.manageUser.user.costCenter" text="Cost center"/>
								</th>
								<th id="header5">
									<spring:theme code="text.company.status.title" text="Status"/>
								</th>
							</tr>
							</thead>
							<tbody>
							<c:forEach items="${searchPageData.results}" var="user">
								<tr>
									<spring:url value="/my-company/organization-management/manage-users/details/"
												var="viewUserUrl">
										<spring:param name="user" value="${user.uid}"/>
									</spring:url>
									<spring:url value="/my-company/organization-management/manage-units/details/"
												var="viewUnitUrl">
										<spring:param name="unit" value="${user.unit.uid}"/>
									</spring:url>

									<td headers="header1">
										<ycommerce:testId code="my-company_username_label">
											<p><a href="${viewUserUrl}">${user.firstName}&nbsp;${user.lastName}</a></p
												>
										</ycommerce:testId>
									</td>
									<td headers="header2">
										<ycommerce:testId code="my-company_user_roles_label">
											<c:forEach items="${user.roles}" var="role">
												<p>
													<spring:theme code="b2busergroup.${role}.name"/>
												</p>
											</c:forEach>
										</ycommerce:testId>
									</td>
									<td headers="header3">
										<ycommerce:testId code="my-company_user_unit_label">
											<p><a href="${viewUnitUrl}">${user.unit.name}</a></p>
										</ycommerce:testId>
									</td>
									<td headers="header4">
										<ycommerce:testId code="my-company_user_costcenter_label">
											<c:forEach items="${user.unit.costCenters}" var="costCenter">
												<spring:url value="/my-company/organization-management/manage-costcenters/view/"
													var="viewCostCenterUrl">
													<spring:param name="costCenterCode" value="${costCenter.code}"/>
												</spring:url>
												<p><a href="${viewCostCenterUrl}">${costCenter.code}</a></p>
											</c:forEach>
										</ycommerce:testId>
									</td>
									<td headers="header5">
										<ycommerce:testId code="costCenter_status_label">
											<p>
												<spring:theme code="text.company.status.active.${user.active}" />
											</p>
										</ycommerce:testId>
									</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</form>
					<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"
									searchPageData="${searchPageData}"

										searchUrl="/my-company/organization-management/manage-users?sort=${searchPageData.pagination.sort}"
									msgKey="text.company.manageUser.pageAll"/>
				</c:if>
				<c:if test="${empty searchPageData.results}">
					<p>
						<spring:theme code="text.company.manageUser.noUser" text="You have no users"/>
					</p>
				</c:if>
			</div>
		</div>
	</div>
</template:page>
