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
		bindToSelect();
		bindToDeselect();
	});

	/* Extend jquery with a postJSON method */
	jQuery.extend({
		postJSON: function(url, data, callback)
		{
			return jQuery.post(url, data, callback, "json");
		}
	});

	function bindToSelect()
	{

		$('.selectionLink').live('click', function()
		{
			$.postJSON(this.getAttribute('url'), {}, selectionCallback);
			return false;
		});
	}

	function bindToDeselect()
	{

		$('.deselectionLink').live('click', function()
		{
			$.postJSON(this.getAttribute('url'), {}, deselectionCallback);
			return false;
		});
	}

	var selectionCallback = function(obj)
	{
		$('#row-' + obj.normalizedCode).addClass("selected");
		$('#' + obj.normalizedCode).html($('#enableDisableLinksTemplate').tmpl(obj));
	};

	var deselectionCallback = function(obj)
	{
		$('#row-' + obj.normalizedCode).removeClass("selected");
		$('#' + obj.normalizedCode).html($('#enableDisableLinksTemplate').tmpl(obj));
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
<nav:myCompanyNav selected="usergroups"/>
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
						<spring:theme code="text.company.${action}.name.title" text="Permision Name"/>
					</th>
					<th id="header2">
						<spring:theme code="text.company.${action}.currency.title" text="Currency"/>
					</th>
					<th id="header3">
						<spring:theme code="text.company.${action}.value.title" text="Value"/>
					</th>
					<th id="header4">
						<spring:theme code="text.company.${action}.timespan.title" text="TimeSpan"/>
					</th>
					<th id="header5">
						<spring:theme code="text.company.${action}.unit.title" text="Parent business unit"/>
					</th>
					<th id="header6">
						<spring:theme code="text.company.${action}.actions.title" text="Actions"/>
					</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach items="${searchPageData.results}" var="result">

					<spring:url value="/my-company/organization-management/manage-permissions/"
								var="viewUrl">
						<spring:param name="permission" value="${result.code}"/>
					</spring:url>
					<tr class="${result.selected ? 'selected' : ''}" id="row-${result.normalizedId}">
						<td headers="header1">
							<ycommerce:testId code="${action}_name_link">
								<p><a href="${viewUrl}">${result.code}</a></p>
							</ycommerce:testId>
						</td>
						<td headers="header2">
							<ycommerce:testId code="${action}_currency_link">
								<p>${result.currency.name}</p>
							</ycommerce:testId>
						</td>
						<td headers="header1">
							<ycommerce:testId code="${action}_value_link">
								<p>${result.value}</p>
							</ycommerce:testId>
						</td>
						<td headers="header1">
							<ycommerce:testId code="${action}_timespan_link">
								<p>${result.timeSpan}</p>
							</ycommerce:testId>
						</td>
						<td headers="header2">
							<ycommerce:testId code="${action}_b2bunit_label">
								<p>${result.unit.name}</p>
							</ycommerce:testId>
						</td>

						<td headers="header4">
							<ycommerce:testId code="${action}_actions_label">
								<p>
									<span id="${result.normalizedId}">
											<spring:url value="${baseUrl}/${action}/select/"
														var="selectUrl">
												<spring:param name="usergroup" value="${param.usergroup}"/>
												<spring:param name="permission" value="${result.code}"/>
											</spring:url>
											<spring:url value="${baseUrl}/${action}/deselect/"
														var="deselectUrl">
												<spring:param name="usergroup" value="${param.usergroup}"/>
												<spring:param name="permission" value="${result.code}"/>
											</spring:url>

										<c:choose>
											<c:when test="${result.selected}">
												${selectAction} |
												<a href="#"
												   url="${deselectUrl}"
												   class="deselectionLink"
													>${deselectAction}</a>
											</c:when>
											<c:otherwise>
												<a href="#"
												   url="${selectUrl}"
												   class="selectionLink"
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
				url="${actionLink}/deselect/?permission={{= id}}&usergroup=${param.usergroup}"
				class="deselectionLink">${deselectAction}</a>
	{{else}}
	<a href="#"
	   url="${actionLink}/select/?permission={{= id}}&usergroup=${param.usergroup}"
	   class="selectionLink">${selectAction}</a> | ${deselectAction}
	{{/if}}
</script>

</template:page>
