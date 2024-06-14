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

<spring:theme code="text.company.select.action.label" text="Select" var="selectAction"/>
<spring:theme code="text.company.deselect.action.label" text="Deselect" var="deselectAction"/>

<template:page pageTitle="${pageTitle}">

<script type="text/javascript">
	/*<![CDATA[*/

	$(document).ready(function()
	{
		bindToSelectUser();
		bindToDeselectUser();
	});

	/* Extend jquery with a postJSON method */
	jQuery.extend({
		postJSON: function(url, data, callback)
		{
			return jQuery.post(url, data, callback, "json");
		}
	});

	function bindToSelectUser()
	{

		$('.selectUser').live('click', function()
		{
			$.postJSON(this.getAttribute('url'), {}, selectionCallback);
			return false;
		});
	}

	function bindToDeselectUser()
	{

		$('.deselectUser').live('click', function()
		{
			$.postJSON(this.getAttribute('url'), {}, deselectionCallback);
			return false;
		});
	}

	var selectionCallback = function(user)
	{
		$('#row-' + user.normalizedCode).addClass("selected");
		$('#selection-' + user.normalizedCode).html($('#enableDisableLinksTemplate').tmpl(user));
		$('#roles-' + user.normalizedCode).html($('#userRolesTemplate').tmpl(user));
	};

	var deselectionCallback = function(user)
	{
		$('#row-' + user.normalizedCode).removeClass("selected");
		$('#selection-' + user.normalizedCode).html($('#enableDisableLinksTemplate').tmpl(user));
		$('#roles-' + user.normalizedCode).html($('#userRolesTemplate').tmpl(user));
	};


	/*]]>*/
</script>


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
	<div class="item_container_holder">
		<div class="title_holder">
			<div class="title">
				<div class="title-top">
					<span></span>
				</div>
			</div>
			<h2>
				<spring:theme code="text.company.manage.units.${action}.manage.title" text="${action}" arguments="${unit.name}"/>
			</h2>
		</div>
		<div class="item_container">
			<spring:theme code="text.company.manage.units.${action}.subtitle"/>
			<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}"
							searchUrl="${baseUrl}/${action}?unit=${param.unit}&sort=${searchPageData.pagination
								.sort}"
							sortQueryParams="unit=${param.unit}"
							msgKey="text.company.${action}.page"/>
			<table id="order_history">
				<thead>
				<tr>
					<th id="header1">
						<spring:theme code="text.company.name.name.title" text="Name"/>
					</th>
					<th id="header2">
						<spring:theme code="text.company.parentUnit.name" text="Parent Unit"/>
					</th>
					<th id="header3">
						<spring:theme code="text.company.roles.name" text="Roles"/>
					</th>
					<th id="header4">
						<spring:theme code="text.company.actions.name" text="Actions"/>
					</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach items="${searchPageData.results}" var="user">
					<spring:url value="/my-company/organization-management/manage-users/details"
								var="viewUrl">
						<spring:param name="user" value="${user.uid}"/>
					</spring:url>
					<spring:url value="${baseUrl}/approvers/select/"
								var="selectUrl">
						<spring:param name="user" value="${user.uid}"/>
						<spring:param name="unit" value="${param.unit}"/>
					</spring:url>
					<spring:url value="${baseUrl}/approvers/deselect/"
								var="deselectUrl">
						<spring:param name="user" value="${user.uid}"/>
						<spring:param name="unit" value="${param.unit}"/>
					</spring:url>
					<tr class="${user.selected ? 'selected' : ''}" id="row-${user.normalizedUid}">
						<td headers="header1">
							<ycommerce:testId code="${action}_name_link">
								<a href="${viewUrl}">${user.name}</a>
							</ycommerce:testId>
						</td>
						<td headers="header2">
							<ycommerce:testId code="${action}_b2bunit_label">
								<p>${user.unit.name}</p>
							</ycommerce:testId>
						</td>
						<td headers="heade3">
							<ycommerce:testId code="${action}_roles_label">
								<div id="roles-${user.normalizedUid}">
									<c:forEach items="${user.roles}" var="role">
										<p>
											<spring:theme code="b2busergroup.${role}.name"/>
										</p>
									</c:forEach>
								</div>
							</ycommerce:testId>
						</td>
						<td headers="header4">
							<ycommerce:testId code="${action}_actions_label">
								<p>
									<c:choose>
										<c:when test="${user.active}">
									<span id="selection-${user.normalizedUid}">
										<c:choose>
											<c:when test="${user.selected}">
												${selectAction} |
												<a href="#"
													url="${deselectUrl}"
													class="deselectUser"
													>${deselectAction}</a>
											</c:when>
											<c:otherwise>
												<a href="#"
													url="${selectUrl}"
													class="selectUser">${selectAction}</a> | ${deselectAction}
											</c:otherwise>
										</c:choose>
									</span>
										</c:when>
										<c:otherwise>
											<spring:theme code="text.user.disabled" text="Disabled User"/>
										</c:otherwise>
									</c:choose>
								</p>
							</ycommerce:testId>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>

			<nav:pagination top="false"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}"
							searchUrl="${baseUrl}/${action}?unit=${param.unit}&sort=${searchPageData.pagination
								.sort}"
							sortQueryParams="unit=${param.unit}"
							msgKey="text.company.${action}.page"/>
			<c:if test="${empty searchPageData.results}">
				<p>No entries.</p>
			</c:if>
		</div>
	</div>
</div>

<c:url value="${baseUrl}/approvers" var="approversActionLink" />
<script id="enableDisableLinksTemplate" type="text/x-jquery-tmpl">
	
	{{if selected}}
	${selectAction} | <a href="#"
				url="${approversActionLink}/deselect/?user={{= id}}&unit=${param.unit}"
				class="deselectUser">${deselectAction}</a>
	{{else}}
	<a href="#"
		url="${approversActionLink}/select/?user={{= id}}&unit=${param.unit}"
		class="selectUser">${selectAction}</a> | ${deselectAction}
	{{/if}}
</script>


<script id="userRolesTemplate" type="text/x-jquery-tmpl">
	{{each displayRoles}}
		<p>
			{{= $value}}
		</p>
	{{/each}}
</script>

</template:page>
