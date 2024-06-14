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
<spring:theme code="text.company.disable.action.label" text="Disable" var="disableAction"/>


<template:page pageTitle="${pageTitle}">

	<script type="text/javascript">
		/*<![CDATA[*/

		$(document).ready(function ()
		{
			bindToSelectUser();
			bindToDeselectUser();
		});

		/* Extend jquery with a postJSON method */
		jQuery.extend({
			postJSON:function (url, data, callback)
			{
				return jQuery.post(url, data, callback, "json");
			}
		});

		function bindToSelectUser()
		{

			$('.selectUser').live('click', function ()
			{
				$.postJSON(this.getAttribute('url'), {}, selectionCallback);
				return false;
			});
		}

		function bindToDeselectUser()
		{

			$('.deselectUser').live('click', function ()
			{
				$.postJSON(this.getAttribute('url'), {}, deselectionCallback);
				return false;
			});
		}

		var selectionCallback = function (user)
		{
			$('#row-' + user.normalizedUid).addClass("selected");
			$('#selection-' + user.normalizedUid).html($('#enableDisableLinksTemplate').tmpl(user));

		};

		var deselectionCallback = function (user)
		{
			$('#row-' + user.normalizedUid).removeClass("selected");
			$('#selection-' + user.normalizedUid).html($('#enableDisableLinksTemplate').tmpl(user));

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
					<spring:theme code="text.company.usergroups.${action}.title" text="${action}" arguments="${param.usergroup}"/>
				</h2>
			</div>
			<div class="item_container">
				<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}"


									searchUrl="${baseUrl}/${action}?usergroup=${param.usergroup}&sort=${searchPageData.pagination.sort}"
								sortQueryParams="usergroup=${param.usergroup}"
								msgKey="text.company.${action}.page"/>
				<table id="order_history">
					<thead>
					<tr>
						<th id="header1">
							<spring:theme code="text.company.${action}.name.title" text="Name"/>
						</th>
						<th id="header2">
							<spring:theme code="text.company.${action}.unit.title" text="Parent unit"/>
						</th>
						<th id="header3">
							<spring:theme code="text.company.${action}.roles.title" text="Roles"/>
						</th>
						<th id="header4">
							<spring:theme code="text.company.${action}.actions.title" text="Actions"/>
						</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach items="${searchPageData.results}" var="user">
						<spring:url value="/my-company/organization-management/manage-users/details"
									var="viewUrl">
							<spring:param name="user" value="${user.uid}"/>
						</spring:url>
						<spring:url value="${baseUrl}/${action}/select/"
									var="selectUrl">
							<spring:param name="user" value="${user.uid}"/>
							<spring:param name="usergroup" value="${param.usergroup}"/>
						</spring:url>
						<spring:url value="${baseUrl}/${action}/deselect/"
									var="deselectUrl">
							<spring:param name="user" value="${user.uid}"/>
							<spring:param name="usergroup" value="${param.usergroup}"/>
						</spring:url>
						<spring:url
							value="/my-company/organization-management/manage-units/details/"
							var="unitDetailUrl">
							<spring:param name="unit" value="${user.unit.uid}"/>
						</spring:url>
						
						<tr class="${user.selected ? 'selected' : ''}" id="row-${user.normalizedUid}">
							<td headers="header1">
								<ycommerce:testId code="${action}_name_link">
									<a href="${viewUrl}">${user.name}</a>
								</ycommerce:testId>
							</td>
							<td headers="header2">
								<ycommerce:testId code="${action}_b2bunit_label">
									<p><a href="${unitDetailUrl}">${user.unit.name}</a></p>
								</ycommerce:testId>
							</td>
							<td headers="header3">
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
												   class="selectUser"
													>${selectAction}</a> | ${deselectAction}
											</c:otherwise>
										</c:choose>
									</span>
									</p>
								</ycommerce:testId>
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>

				<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}"
								searchUrl="${baseUrl}/${action}?usergroup=${param.usergroup}&sort=${searchPageData.pagination.sort}"
								sortQueryParams="usergroup=${param.usergroup}"
								msgKey="text.company.${action}.page"/>
				<c:if test="${empty searchPageData.results}">
					<p>No entries.</p>
				</c:if>
			</div>
		</div>
	</div>

	<c:url value="${baseUrl}/${action}" var="actionLink" />
	<script id="enableDisableLinksTemplate" type="text/x-jquery-tmpl">
		{{if selected}}
		${selectAction} | <a href="#"
							 url="${actionLink}/deselect/?user={{= uid}}&usergroup=${param.usergroup}"
							 class="deselectUser">${deselectAction}</a>
		{{else}}
		<a href="#"
		   url="${actionLink}/select/?user={{= uid}}&usergroup=${param.usergroup}"
		   class="selectUser">${selectAction}</a> | ${deselectAction}
		{{/if}}
	</script>

</template:page>
