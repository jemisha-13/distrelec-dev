<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<template:page pageTitle="${pageTitle}">
	<jsp:attribute name="pageScripts">
		<product:productDetailsJavascript/>
	</jsp:attribute>

	<jsp:body>
		<c:if test="${not empty message}">
			<spring:theme code="${message}"/>
		</c:if>
		<div id="breadcrumb" class="breadcrumb">
			<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
		</div>
		<div id="globalMessages">
			<common:globalMessages/>
		</div>
		<div class="span-20">
			<div class="span-20" id="productDetailUpdateable">
				<product:productDetailsPanel product="${product}" galleryImages="${galleryImages}"/>
				<!-- promotional banner -->
				<div class="thumbnail_detail">

				</div>
			</div>
			<div class="span-20">
				<div class="span-4">
					<cms:slot var="comp" contentSlot="${slots.CrossSelling}">
						<cms:component component="${comp}"/>
					</cms:slot>
				</div>
				<div class="span-16 right last">
					<product:productPageTabs/>
				</div>
			</div>
		</div>
		<div class="span-4 last">
			<cms:slot var="comp" contentSlot="${slots.Accessories}">
				<cms:component component="${comp}"/>
			</cms:slot>
		</div>
	</jsp:body>
</template:page>
