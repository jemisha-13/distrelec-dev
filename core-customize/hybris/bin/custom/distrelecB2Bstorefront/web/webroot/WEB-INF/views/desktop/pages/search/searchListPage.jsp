<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<template:page pageTitle="${pageTitle}">

	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<div class="span-4">
		<nav:facetNavAppliedFilters pageData="${searchPageData}"/>
		<nav:facetNavRefinements pageData="${searchPageData}"/>
	</div>

	<div class="span-20 last">
		<div class="span-20">
			<cms:slot var="feature" contentSlot="${slots['Section2']}">
				<div class="section2 cms_banner_slot">
					<cms:component component="${feature}"/>
				</div>
			</cms:slot>
		</div>

		<div class="span-20">
			<div class="span-16">
				<div class="results">
					<h1><spring:theme code="search.page.searchText" arguments="${searchPageData.freeTextSearch}"/></h1>
				</div>

				<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/>

				<c:forEach items="${searchPageData.results}" var="product">
					<product:productListerItem product="${product}"/>
				</c:forEach>

				<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/>
			</div>

			<cms:slot var="feature" contentSlot="${slots['Section4']}">
				<div class="span-4 section4 cms_banner_slot last">
					<cms:component component="${feature}"/>
				</div>
			</cms:slot>
		</div>
	</div>
</template:page>