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
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/desktop/company" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/desktop/company" %>

<spring:url value="/my-company/organization-management/manage-units/edit" var="editUnitUrl">
	<spring:param name="unit" value="${unit.uid}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/create" var="createUnitUrl">
	<spring:param name="unit" value="${unit.uid}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/details" var="unitDetailsUrl">
	<spring:param name="unit" value="${unit.uid}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/disable" var="disableUnitUrl">
	<spring:param name="unit" value="${unit.uid}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/enable" var="enableUnitUrl">
	<spring:param name="unit" value="${unit.uid}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/details" var="parentUnitUrl">
	<spring:param name="unit" value="${unit.unit.uid}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/add-address" var="addUnitAddressUrl">
	<spring:param name="unit" value="${unit.uid}"/>
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/addcostcenter" var="addCostcenterUrl">
	<spring:param name="unit" value="${unit.uid}"/>
</spring:url>

<template:page pageTitle="${pageTitle}">

<script type="text/javascript">
	/*<![CDATA[*/
	$(document).ready(function ()
	{
		if ('${unit.active}' != 'true')
		{
			$('.create-new').attr("disabled", true);
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
<nav:myCompanyNav selected="units"/>
<div class="span-20 last">
<div class="span-20 wide-content-slot cms_banner_slot">
	<cms:slot var="feature" contentSlot="${slots['TopContent']}">
		<cms:component component="${feature}"/>
	</cms:slot>
</div>

<div class="span-20 last myCompany">
<div class="item_container_holder top">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2>
			<spring:theme code="text.company.manage.units.unitDetails" text="View Unit: {0}" arguments="${unit.name}"/>
		</h2>
	</div>
	<div class="last">
		<a href="${editUnitUrl}">
			<button class="positive right pad_right place-order edit">
				<spring:theme code="text.company.manage.units.button.editUnit" text="Edit Unit"/>
			</button>
		</a>
		<c:choose>
			<c:when test="${unit.active}">
				<c:if test="${unit.uid != user.unit.uid}">
					<a href="${disableUnitUrl}">
						<ycommerce:testId code="Unit_DisableUnit_button">
							<button type="button" class="positive right pad_right place-order">
								<spring:theme code="text.company.manage.units.button.disableUnit" text="Disable Unit"/>
							</button>
						</ycommerce:testId>
					</a>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${unit.unit.active}">
						<a href="${enableUnitUrl}">
							<ycommerce:testId code="Unit_EnableUnit_button">
								<button type="button" class="positive right pad_right place-order">
									<spring:theme code="text.company.manage.units.button.enableUnit"
												  text="Enable Unit"/>
								</button>
							</ycommerce:testId>
						</a>
					</c:when>
					<c:otherwise>
						<div class="right pad_right">
							<spring:theme code="text.company.manage.units.parentUnit.disabled"
										  text="It is not possible to enable unit whose parent unit is disabled"/>
						</div>
					</c:otherwise>
				</c:choose>


			</c:otherwise>
		</c:choose>
	</div>
	<div class="item_container">
		<spring:theme code="text.company.manage.units.subtitle" text="" />
	</div>
	<div class="item_container">
		<table class="table_budget">
			<tr>
				<td>
					<ycommerce:testId code="Unit_UnitDetailsPage_UnitID">
						<spring:theme code="text.company.unit.id" text="Business Unit ID:"/>
					</ycommerce:testId>
				</td>
				<td>
					<ycommerce:testId code="Unit_UnitDetailsPage_UnitID">${unit.uid}</ycommerce:testId>
				</td>
			</tr>
			<tr>
				<td>
					<spring:theme code="text.company.unit.name" text="Business Unit Name:"/>
				</td>
				<td>${unit.name}</td>
			</tr>
			<tr>
				<td>
					<spring:theme code="text.company.unit.parent" text="Parent Business Unit:"/>
				</td>
				<td><a href="${parentUnitUrl}"> ${unit.unit.uid}</a></td>
			</tr>
			<tr>
				<td>
					<spring:theme code="text.company.unit.approvalProcess" text="Approval Process:"/>
				</td>
				<td>${unit.approvalProcessCode}</td>
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
			<spring:theme code="text.company.manage.units.accountManagers" text="Account Managers"/>
		</h2>
	</div>
	<div class="item_container">
		<spring:theme code="text.company.manage.units.view.accountManagers.subtitle" arguments="${b2bStore}" />
		<div class="span-19">
			<div class="item_container_holder">
				<div class="item_container">
					<table class="border">
						<thead>
						<tr>
							<th id="accountManagerName">
								<spring:theme code="text.company.manage.units.accountManager.name" text="Name"/>
							</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="${unit.accountManagers}" var="accountManager">
							<tr>
								<td headers="1">
									${accountManager.name}
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
			<spring:theme code="text.company.manage.units.addresses" text="Addresses"/>
		</h2>
	</div>
	<div class="last">
		<a href="${addUnitAddressUrl}">
			<ycommerce:testId code="Unit_AddAddress_button">
				<button class="positive right pad_right place-order create-new">
					<spring:theme code="text.company.manage.units.button.create" text="Create"/>
				</button>
			</ycommerce:testId>
		</a>
	</div>
	<div class="item_container">
		<spring:theme code="text.company.manage.units.view.addresses.subtitle" />
		<div class="span-19">
			<div class="item_container_holder">
				<div class="item_container">
					<table class="border">
						<thead>
						<tr>
							<th id="line1">
								<spring:theme code="text.company.manage.units.address.line1" text="Address Line 1"/>
							</th>
							<th id="line2">
								<spring:theme code="text.company.manage.units.address.line2" text="Address Line 2"/>
							</th>
							<th id="city">
								<spring:theme code="text.company.manage.units.address.towncity" text="Town/City"/>
							</th>
							<th id="zip">
								<spring:theme code="text.company.manage.units.address.zipcode" text="Postal Code"/>
							</th>
							<th id="country">
								<spring:theme code="text.company.manage.units.address.country" text="Country"/>
							</th>
							<th id="actions">
								<spring:theme code="text.company.manage.units.address.actions" text="Actions"/>
							</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="${unit.addresses}" var="address">
							<tr>
								<spring:url
									value="/my-company/organization-management/manage-units/edit-address/"
									var="editUnitAddressUrl">
									<spring:param name="unit" value="${unit.uid}"/>
									<spring:param name="addressId" value="${address.id}"/>
								</spring:url>
								<td headers="1">${address.line1}</td>
								<td headers="2">${address.line2}</td>
								<td headers="3">${address.town}</td>
								<td headers="4">${address.postalCode}</td>
								<td headers="5">${address.country.name}</td>
								<td headers="6">
									<a href="${editUnitAddressUrl}">
										<spring:theme code="text.company.manage.units.edit"
													  text="Edit"/>
									</a>
									|
									<spring:url
										value="/my-company/organization-management/manage-units/remove-address/"
										var="removeUnitAddressUrl">
										<spring:param name="unit" value="${unit.uid}"/>
										<spring:param name="addressId" value="${address.id}"/>
									</spring:url>

									<a href="${removeUnitAddressUrl}">
										<spring:theme code="text.company.manage.units.remove"
													  text="Remove"/>
									</a>
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
			<spring:theme code="text.company.manage.units.costCenters" text="Cost Centers"/>
		</h2>
	</div>
	<div class="last">
		<a href="${addCostcenterUrl}">
			<button class="positive right pad_right place-order create-new">
				<spring:theme code="text.company.manage.units.button.create" text="Create"/>
			</button>
		</a>
	</div>
	<div class="item_container">
		<spring:theme code="text.company.manage.units.view.costCenters.subtitle" />
		<div class="span-19">
			<div class="item_container_holder">
				<div class="item_container">
					<table class="border">
						<thead>
						<tr>
							<th id="csid">
								<spring:theme code="text.company.manage.unit.costCenter.id" text="ID"/>
							</th>
							<th id="csname">
								<spring:theme code="text.company.manage.unit.costCenter.name" text="Name"/>
							</th>
							<th id="cscurrency">
								<spring:theme code="text.company.manage.unit.costCenter.currency" text="Currency"/>
							</th>
							<th id="csactions">
								<spring:theme code="text.company.manage.unit.costCenter.actions" text="Actions"/>
							</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="${unit.costCenters}" var="b2bCostCenter">
							<spring:url
								value="/my-company/organization-management/manage-units/costcenter/"
								var="viewCostcenterUrl">
								<spring:param name="unit" value="${unit.uid}"/>
								<spring:param name="costCenterCode" value="${b2bCostCenter.code}"/>
							</spring:url>

							<spring:url
								value="/my-company/organization-management/manage-units/editcostcenter/"
								var="editCostcenterUrl">
								<spring:param name="unit" value="${unit.uid}"/>
								<spring:param name="costCenterCode" value="${b2bCostCenter.code}"/>
							</spring:url>

							<tr>
								<td headers="1">
									<a href="${viewCostcenterUrl}">${b2bCostCenter.code}</a>
								</td>
								<td headers="2">${b2bCostCenter.name}</td>
								<td headers="3">${b2bCostCenter.currency.name}</td>
								<td headers="4">
									<a href="${editCostcenterUrl}">
										<spring:theme code="text.company.manage.unit.costCenter.edit" text="Edit"/>
									</a>
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
			<spring:theme code="text.company.manage.units.childUnits" text="Child Units"/>
		</h2>
	</div>
	<div class="last">
		<a href="${createUnitUrl}">
			<button class="positive right pad_right place-order create-new">
				<spring:theme code="text.company.manage.units.button.create" text="Create"/>
			</button>
		</a>
	</div>
	<div class="item_container">
		<spring:theme code="text.company.manage.units.view.units.subtitle" />
		<div class="span-19">
			<div class="item_container_holder">
				<div class="item_container">
					<table class="border">
						<thead>
						<tr>
							<th id="childrenUnitsID">
								<spring:theme code="text.company.manage.units.childrenUnits.id" text="ID"/>
							</th>
							<th id="childrenUnitsName">
								<spring:theme code="text.company.manage.units.childrenUnits.name" text="Name"/>
							</th>
							<th id="childrenUnitsActions">
								<spring:theme code="text.company.manage.units.childrenUnits.actions" text="Actions"/>
							</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="${unit.children}" var="unit">
							<spring:url value="/my-company/organization-management/manage-units/details"
										var="unitUrl">
								<spring:param name="unit" value="${unit.uid}"/>
							</spring:url>
							<spring:url value="/my-company/organization-management/manage-units/edit"
										var="editUnit">
								<spring:param name="unit" value="${unit.uid}"/>
							</spring:url>
							<tr>
								<td headers="1">
									<a href="${unitUrl}">${unit.uid}</a>
								</td>
								<td headers="2">${unit.name}</td>
								<td headers="3">
									<a href="${editUnit}">
										<spring:theme code="text.company.manage.unit.childrenUnits.edit"
													  text="Edit"/>
									</a>
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

<spring:url
	value="/my-company/organization-management/manage-units/administrators"
	var="editAdministratorUrl">
	<spring:param name="unit" value="${unit.uid}"/>
	<spring:param name="role" value="b2badmingroup"/>
</spring:url>
<spring:url
	value="/my-company/organization-management/manage-units/createuser"
	var="createUserUrl">
	<spring:param name="unit" value="${unit.uid}"/>
</spring:url>
<spring:url
	value="/my-company/organization-management/manage-units/managers"
	var="editManagersUrl">
	<spring:param name="unit" value="${unit.uid}"/>
	<spring:param name="role" value="b2bmanagergroup"/>
</spring:url>
<spring:url
	value="/my-company/organization-management/manage-units/approvers"
	var="editApproversUrl">
	<spring:param name="unit" value="${unit.uid}"/>
</spring:url>
<spring:url
	value="/my-company/organization-management/manage-units/customers"
	var="editCustomersUrl">
	<spring:param name="unit" value="${unit.uid}"/>
	<spring:param name="role" value="b2bcustomergroup"/>

</spring:url>

<company:approverList users="${unit.approvers}" role="b2bapprovergroup"
					  editUrl="${editApproversUrl}"
					  createUrl="${createUserUrl}"/>

<company:userList users="${unit.administrators}" action="administrators" role="b2badmingroup"
				  editUrl="${editAdministratorUrl}"
				  createUrl="${createUserUrl}" subtitleKey="text.company.manage.units.view.administrators.subtitle"/>
				  
<company:userList users="${unit.managers}" action="managers" role="b2bmanagergroup"
				  editUrl="${editManagersUrl}"
				  createUrl="${createUserUrl}" subtitleKey="text.company.manage.units.view.managers.subtitle" />

<company:userList users="${unit.customers}" action="customers" role="b2bcustomergroup"
				  editUrl="${editCustomersUrl}"
				  createUrl="${createUserUrl}" subtitleKey="text.company.manage.units.view.customers.subtitle" />

</div>
</div>
</div>
</div>
</template:page>
