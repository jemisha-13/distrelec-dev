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

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:accountNav selected="address-book" />
	<div class="span-20 last">
		<div class="item_container_holder">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h2><spring:theme code="text.account.addressBook.addressDetails" text="Address Details"/></h2>
			</div>
			<div class="item_container">
				<p><spring:theme code="text.account.addressBook.addEditform" text="Please use this form to add/edit an address."/></p>
				<p class="required"><spring:theme code="form.required" text="Fields marked * are required"/></p>
				<form:form action="add-address" method="post" modelAttribute="addressForm">
					<dl>
						<form:hidden path="addressId"/>
						<formUtil:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titleData}"  selectedValue="${addressForm.titleCode}"/>
						<formUtil:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" placeHolderKey="address.firstName.placeholder" inputCSS="text" mandatory="true"/>
						<formUtil:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" placeHolderKey="address.surname.placeholder" inputCSS="text" mandatory="true"/>
						<formUtil:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" placeHolderKey="address.line1.placeholder" inputCSS="text" mandatory="true"/>
						<formUtil:formInputBox idKey="address.line2" labelKey="address.line2" path="line2" placeHolderKey="address.line2.placeholder" inputCSS="text" mandatory="false"/>
						<formUtil:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" placeHolderKey="address.townCity.placeholder" inputCSS="text" mandatory="true"/>
						<formUtil:formInputBox idKey="address.postcode" labelKey="address.postcode" path="postcode" placeHolderKey="address.postcode.placeholder" inputCSS="text" mandatory="true"/>
						<formUtil:formSelectBox idKey="address.country" labelKey="address.country" path="countryIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectCountry" items="${countryData}" itemValue="isocode" selectedValue="${addressForm.countryIso}"/>
						<c:if test="${ not addressForm.defaultAddress}">
							<formUtil:formCheckbox idKey="address.default" labelKey="address.default" path="defaultAddress" inputCSS="add-address-left-input" labelCSS="add-address-left-label" mandatory="false"/>
						</c:if>
					</dl>
					
						<button class="form"><spring:theme code="text.account.addressBook.saveAddress" text="Save address"/></button>

				</form:form>
			</div>
		</div>
	</div>
</template:page>