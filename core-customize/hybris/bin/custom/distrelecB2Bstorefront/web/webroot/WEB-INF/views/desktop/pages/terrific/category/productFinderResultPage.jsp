<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<views:page-default-seo pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-category skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>
	<mod:global-messages/>
	<div class="ct">
		<div class="row">
			<div class="gu-12">
				<h1 class="base page-title">${categoryPageData.sourceCategory.name}</h1>
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<mod:amount-products-found />
			</div>
			<div class="gu-8">
				<mod:productlist-tools />
			</div>
		</div>
		<div class="row">
			<div class="gu-12">
				<mod:productlist-controllbar template="category" skin="category" pageData="${searchPageData}"  />
			</div>
		</div>
		<div class="row">
			<div class="gu-3 padding-right">
				<mod:facets template="product-finder" skin="product-finder" searchPageData="${searchPageData}" />
				<cms:slot var="feature" contentSlot="${slots['TeaserContent']}">
					<cms:component component="${feature}"/>
				</cms:slot>
			</div>
			<div class="gu-9 padding-left padding-right-gu">
				<mod:productlist searchPageData="${searchPageData}"/>
				<cms:slot var="feature" contentSlot="${slots['Content']}">
					<cms:component component="${feature}"/>
				</cms:slot>
			</div>
		</div>
	</div>
</views:page-default-seo>
