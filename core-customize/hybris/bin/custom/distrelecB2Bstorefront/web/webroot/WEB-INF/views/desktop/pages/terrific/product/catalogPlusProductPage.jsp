<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-product skin-layout-wide">

	<c:if test="${not empty message}">
		<spring:theme code="${message}" />
	</c:if>

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb />
	</div>

	<c:if test="${empty eolMessage}">
		<mod:global-messages />
	</c:if>
	<c:if test="${not empty eolMessage}">
		<mod:global-messages headline="" body="${eolMessage}" type="information" widthPercent="100%" displayIcon="true" />
	</c:if>

		<div class="ct" itemscope itemtype="http://schema.org/Product">
		<mod:nextprevproductdetail product="${product}" />
		<div class="row row-relative border-bottom">
			<div class="gu-12 print-product-name">
				<mod:product-title title="${productTitle}" />
			</div>
			<div class="gu-12">
				<mod:article-numbers product="${product}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-6">
				<mod:product-image-gallery product="${product}" galleryImages="${galleryImages}" />
			</div>
			<div class="gu-6">
				<mod:buying-section product="${product}" />
				<mod:scaled-prices product="${product}" />
				<mod:shipping-information product="${product}"/>
			</div>
		</div>
		<div class="row border-bottom social-media-row">
		
<!-- 		Set it back to gu-6 after the commit !!! -->
<!-- 			<div class="gu-6"> -->
			<div class="gu-12">
				<mod:detailpage-toolbar product="${product}" />
			</div>
		</div>
		<mod:lightbox-image productName="${product.name}" />
		<mod:lightbox-quotation />
		<mod:lightbox-quotation-confirmation />
		<mod:detail-tabs product="${product}" downloadSection="${downloadSection}" />
		<%--<mod:detail-accordion product="${product}" downloadSection="${downloadSection}" /> --%>
	</div>

	<cms:slot var="comp" contentSlot="${slots.Recommendation}">
		<cms:component component="${comp}" />
	</cms:slot>

	<cms:slot var="comp" contentSlot="${slots.ConsistentWith}">
		<cms:component component="${comp}" />
	</cms:slot>

	<cms:slot var="comp" contentSlot="${slots.AlsoBought}">
		<cms:component component="${comp}" />
	</cms:slot>

	<mod:print-footer/>

</views:page-default>