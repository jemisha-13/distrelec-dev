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
	value="/my-company/organization-management/manage-usergroups/permissions/"
	var="permissionsUrl">
	<spring:param name="usergroup" value="${usergroup.uid}"/>
</spring:url>
<spring:url
	value="/my-company/organization-management/manage-usergroups/members/"
	var="membersUrl">
	<spring:param name="usergroup" value="${usergroup.uid}"/>
</spring:url>

<spring:url
	value="/my-company/organization-management/manage-units/details/"
	var="unitDetailsUrl">
	<spring:param name="unit" value="${usergroup.unit.uid}"/>
</spring:url>

<c:if test="${empty editUrl}">
	<spring:url
		value="/my-company/organization-management/manage-usergroups/edit/"
		var="editUrl">
		<spring:param name="usergroup" value="${usergroup.uid}"/>
	</spring:url>
</c:if>
<c:if test="${empty disableUrl}">
	<spring:url
		value="/my-company/organization-management/manage-usergroups/disable/"
		var="disableUrl">
		<spring:param name="usergroup" value="${usergroup.uid}"/>
	</spring:url>
</c:if>


<template:page pageTitle="${pageTitle}">
<script type="text/javascript">
	/*<![CDATA[*/
	$(document).ready(function ()
	{
		if ('${usergroup}' == '')
		{
			$('.edit').attr("disabled", true);
		}

	});
	/*]]>*/
</script>

<div id="breadcrumb" class="breadcrumb">
	<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
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
		<div class="title_holder">
			<div class="title">
				<div class="title-top">
					<span></span>
				</div>
			</div>
			<h2>
				<spring:theme code="text.company.usergroups.viewDetails.label"
							  text="View Usergroup Details"/>
			</h2>
		</div>
		<div class="last">
			<a href="${editUrl}">
				<ycommerce:testId code="Usergroup_Edit_button">
					<button class="positive right pad_right place-order edit">
						<spring:theme code="text.company.usergroup.edit.button" text="Edit"/>
					</button>
				</ycommerce:testId>
			</a>
			<c:if test="${fn:length(usergroup.members) > 0}">
				<a href="${disableUrl}">
					<ycommerce:testId code="Usergroup_Disable_button">
						<button class="positive right pad_right place-order">
							<spring:theme code="text.company.usergroup.disable.button" text="Disable"/>
						</button>
					</ycommerce:testId>
				</a>
			</c:if>
		</div>
		<div class="item_container">
			<table class="table_budget">
				<tr>
					<td>
						<spring:theme code="text.company.usergroup.id.title" text="Usergroup ID"/>
						:
					</td>
					<td>${usergroup.uid}</td>
				</tr>
				<tr>
					<td>
						<spring:theme code="text.company.usergroup.name.title" text="Usergroup Name"/>
						:
					</td>
					<td>${usergroup.name}</td>
				</tr>
				<tr>
					<td>
						<spring:theme code="text.company.usergroup.unit.title" text="Parent unit"/>
						:
					</td>
					<td><a href="${unitDetailsUrl}">${usergroup.unit.name}</a></td>
				</tr>
			</table>


			<div class="item_container_holder">
				<div class="title_holder">
					<div class="title">
						<div class="title-top">
							<span></span>
						</div>
					</div>
					<h2>
						<spring:theme code="text.company.manageUser.permission.title" text="Permissions"/>
					</h2>
				</div>
				<div class="last">
					<a href="${permissionsUrl}">
						<button class="positive right pad_right place-order edit">
							<spring:theme code="text.edit" text="Edit"/>
						</button>
					</a>
				</div>
				<div class="item_container">
					<spring:theme code="text.company.permissions.subtitle" />
				</div>
				<div class="item_container">
					<div class="span-19">
						<div class="item_container_holder">
							<div class="item_container">
								<table class="border">
									<thead>
									<tr>
										<th id="header1">
											<spring:theme code="text.company.permissions.name.title"
														  text="Permision Name"/>
										</th>
										<th id="header2">
											<spring:theme code="text.company.permissions.currency.title"
														  text="Currency"/>
										</th>
										<th id="header3">
											<spring:theme code="text.company.permissions.value.title" text="Value"/>
										</th>
										<th id="header4">
											<spring:theme code="text.company.permissions.timespan.title"
														  text="TimeSpan"/>
										</th>
										<th id="header5">
											<spring:theme code="text.company.permissions.unit.title"
														  text="Parent unit"/>
										</th>
									</tr>
									</thead>
									<tbody>
									<c:forEach items="${usergroup.permissions}" var="permission">
										<spring:url
											value="/my-company/organization-management/manage-units/details/"
											var="unitDetailUrl">
											<spring:param name="unit" value="${permission.unit.uid}"/>
										</spring:url>
										<spring:url
											value="/my-company/organization-management/manage-permissions/view/"
											var="permissionDetailsUrl">
											<spring:param name="permissionCode" value="${permission.code}"/>
										</spring:url>
										<tr>
											<td headers="header1">
												<ycommerce:testId code="permission_name_link">
													<p><a href="${permissionDetailsUrl}">${permission.code}</a></p>
												</ycommerce:testId>
											</td>
											<td headers="header2">
												<ycommerce:testId code="permission_currency_link">
													<p>${permission.currency.name}</p>
												</ycommerce:testId>
											</td>
											<td headers="header3">
												<ycommerce:testId code="permission_value_link">
													<p>${permission.value}</p>
												</ycommerce:testId>
											</td>
											<td headers="header4">
												<ycommerce:testId code="permission_timespan_link">
													<p>${permission.timeSpan}</p>
												</ycommerce:testId>
											</td>
											<td headers="header5">
												<ycommerce:testId code="permission_b2bunit_label">
													<p><a href="${unitDetailUrl}">${permission.unit.name}</a></p>
												</ycommerce:testId>
											</td>
										</tr>
									</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="item_container_holder">
				<div class="title_holder">
					<div class="title">
						<div class="title-top">
							<span></span>
						</div>
					</div>
					<h2>
						<spring:theme code="text.company.usergroup.members" text="Users"/>
					</h2>
				</div>
				<div class="last">
					<a href="${membersUrl}">
						<button class="positive right pad_right place-order edit">
							<spring:theme code="text.company.usergroup.button.createnew" text="Edit"/>
						</button>
					</a>
				</div>
				<div class="item_container">
					<spring:theme code="text.company.usergroups.users.subtitle"/>
				</div>
				<div class="item_container">
					<div class="span-19">
						<div class="item_container_holder">
							<div class="item_container">
								<table class="border">
									<thead>
									<tr>
										<th id="membername">
											<spring:theme code="text.company.usergroup.name.title"
														  text="User Name"/>
										</th>
										<th id="header5">
											<spring:theme code="text.company.usergroup.unit.title"
														  text="Parent business unit"/>
										</th>
									</tr>
									</thead>
									<tbody>
									<c:forEach items="${usergroup.members}" var="user">
										<spring:url
											value="/my-company/organization-management/manage-units/details/"
											var="unitDetailUrl">
											<spring:param name="unit" value="${user.unit.uid}"/>
										</spring:url>
										<spring:url
											value="/my-company/organization-management//manage-users/details/"
											var="userDetailUrl">
											<spring:param name="user" value="${user.uid}"/>
										</spring:url>



										<tr>
											<td headers="header1">
												<ycommerce:testId code="member_name_link">
													<p><a href="${userDetailUrl}">${user.name}</a></p>
												</ycommerce:testId>
											</td>
											<td headers="header5">
												<ycommerce:testId code="member_b2bunit_label">
													<p><a href="${unitDetailUrl}">${user.unit.name}</a></p>
												</ycommerce:testId>
											</td>
										</tr>
									</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>

		</div>

	</div>
</div>
</template:page>
