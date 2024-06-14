<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<cms:slot var="feature" contentSlot="${slots['Section1']}">
		<div class="span-24 section1 cms_banner_slot">
			<cms:component component="${feature}"/>
		</div>
	</cms:slot>
	<div class="span-24">
		<div class="span-4">
			<nav:facetNavAppliedFilters pageData="${searchPageData}"/>
			<nav:facetNavRefinements pageData="${searchPageData}"/>

			<cms:slot var="feature" contentSlot="${slots['Section4']}">
				<div class="span-4 section4 cms_banner_slot">
					<cms:component component="${feature}"/>
				</div>
			</cms:slot>
		</div>
	
		<div class="span-20 last productgridcontainer">
			<cms:slot var="feature" contentSlot="${slots['Section2']}">
				<div class="section2 cms_banner_slot">
					<cms:component component="${feature}"/>
				</div>
			</cms:slot>
			<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/>
			<div class="span-20 last">
				<cms:slot var="feature" contentSlot="${slots['Section3']}">
					<div class="span-5 section3 small_detail">
						<cms:component component="${feature}"/>
					</div>
				</cms:slot>
			</div>
			<div class="span-20 last">
				<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
					<div class="span-4${(status.index+1)%5 == 0 ? ' last' : ''}">
						<product:productListerGridItem product="${product}"/>
					</div>
				</c:forEach>
			</div>
			<div class="span-20 last">
				<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchPageData.currentQuery.url}"/>
			</div>
		</div>
	</div>
</template:page>