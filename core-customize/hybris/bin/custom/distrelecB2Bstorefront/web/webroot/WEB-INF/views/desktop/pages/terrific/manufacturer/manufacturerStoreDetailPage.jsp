<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<c:if test="${isOCI eq true}">
	<spring:eval expression="@configurationService.configuration.getString('default.view.standard')" var="defaultViewStandard" scope="session" />

	<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-responsive cmscontentpage skin-layout-manufacturer skin-layout-product-list skin-layout-wide skin-layout-nonavigation skin-layout-full-width">

		<div id="breadcrumb" class="breadcrumb">
			<mod:breadcrumb template="product" skin="product" />
		</div>

		<div class="plp-content">
			<div class="container">
				<mod:global-messages/>
			</div>
			<div class="plp-content__top">
				<fmt:formatNumber type="number" value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />

				<div class="container">

					<h1 class="base page-title page-title--with-count">
						<a href="#" class="page-title__back-navigation"><i class="fa fa-arrow-left"></i></a>

						<c:if test="${fn:length(breadcrumbs) > 1 }">
							${breadcrumbs[fn:length(breadcrumbs)-1].name}
						</c:if>

						<div class="desktop-header__text for-desktop">
					<span class="matched-products-count">
						<b>(</b>${matchedProductsCount} <span class="matched-products-count__text"><spring:message code="text.products" /></span><b>)</b>
					</span>
						</div>

						<div class="mobile-header__text for-mobile">
					<span class="matched-products-count">
						<span class="xmod-filter__matched-products-count facet-result-count">${searchPageData.pagination.totalNumberOfResults}</span>
						<span class="matched-products-count__text"><spring:message code="text.products" /></span>
					</span>
						</div>

					</h1>

					<mod:manufacturer-store-detail-topbar/>
				</div>

				<c:if test="${not empty slots['ManufacturerTopPosition'] and not empty slots['ManufacturerTopPosition'].cmsComponents }">
					<div class="manufacturerTopPosition">
						<cms:slot var="feature" contentSlot="${slots['ManufacturerTopPosition']}">
							<cms:component component="${feature}"/>
						</cms:slot>
					</div>
				</c:if>
				<cms:slot var="feature" contentSlot="${slots['Content']}">
					<cms:component component="${feature}"/>
				</cms:slot>

			</div>

			<div class="plp-content__categorynav-facets">
				<div class="container">
					<mod:categorynav template="facets" skin="facets" />
				</div>
			</div>

			<mod:productlist template="structure" skin="structure" searchPageData="${searchPageData}"/>

			<c:if test="${not empty slots['ManufacturerBottomPosition'] and not empty slots['ManufacturerBottomPosition'].cmsComponents }">
				<div id="manufacturerBottomPosition" class="row gu-14 manufacturerproduct-search skin-product-technicalBottomContainer">
					<cms:slot var="feature" contentSlot="${slots['ManufacturerBottomPosition']}">
						<cms:component component="${feature}"/>
					</cms:slot>
				</div>
			</c:if>

		</div>
	</views:page-default-md-full>
</c:if>

<c:if test="${isOCI eq false}">
	<spring:eval expression="@configurationService.configuration.getString('default.view.standard')" var="defaultViewStandard" scope="session" />

	<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-responsive cmscontentpage skin-layout-manufacturer skin-layout-product-list skin-layout-wide skin-layout-nonavigation skin-layout-full-width">

		<div id="breadcrumb" class="breadcrumb">
			<mod:breadcrumb template="product" skin="product" />
		</div>

		<div class="plp-content">
			<div class="container">
				<mod:global-messages/>
			</div>
			<div class="plp-content__top">
				<fmt:formatNumber type="number" value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />

				<div class="container">

					<h1 class="base page-title page-title--with-count">
						<a href="#" class="page-title__back-navigation"><i class="fa fa-arrow-left"></i></a>

						<div class="desktop-header__text for-desktop">
							<c:if test="${fn:length(breadcrumbs) > 1 }">
								${breadcrumbs[fn:length(breadcrumbs)-1].name}
							</c:if>
						</div>

						<div class="mobile-header__text for-mobile">
							<c:if test="${fn:length(breadcrumbs) > 1 }">
								${breadcrumbs[fn:length(breadcrumbs)-1].name}
							</c:if>
							<span class="matched-products-count">
							<spring:theme code="text.plp.showing.of" arguments="${firstProductNumber},${lastProductNumber}"/>&nbsp;${matchedProductsCount}&nbsp;<spring:message code="text.products" />
						</span>
						</div>

					</h1>

					<mod:manufacturer-store-detail-topbar/>
				</div>

				<c:if test="${not empty slots['ManufacturerTopPosition'] and not empty slots['ManufacturerTopPosition'].cmsComponents }">
					<div class="manufacturerTopPosition">
						<cms:slot var="feature" contentSlot="${slots['ManufacturerTopPosition']}">
							<cms:component component="${feature}"/>
						</cms:slot>
					</div>
				</c:if>
				<cms:slot var="feature" contentSlot="${slots['Content']}">
					<cms:component component="${feature}"/>
				</cms:slot>

			</div>

			<mod:productlist-pagination template="left-side-filters" skin="plp" htmlClasses="sidebar-filters-pagination" />

			<div class="container">
				<c:if test="${searchPageData.pagination.totalNumberOfResults > 0}">
					<div class="plp-content__nav-filters hidden">
						<c:if test="${fn:length(categoryDisplayDataList) > 0}">
							<div class="plp-content__categorynav-facets">
								<mod:categorynav template="plp" skin="plp" />
							</div>
						</c:if>

						<div id="plp-filter-action-bar-sidebar" class="productlistpage__filter-action-bar sidebar-applied-filters">
							<div class="container">
								<mod:facets template="plp-filter-controllbar" skin="plp-filter-controllbar" searchPageData="${searchPageData}" />
							</div>

						</div>

						<div class="plp-content__filters">
							<mod:productlist template="filters" skin="filters" searchPageData="${searchPageData}"/>
						</div>
					</div>
				</c:if>
			</div>

			<mod:productlist template="structure" skin="structure" searchPageData="${searchPageData}"/>

			<c:if test="${not empty slots['ManufacturerBottomPosition'] and not empty slots['ManufacturerBottomPosition'].cmsComponents }">
				<div id="manufacturerBottomPosition" class="row gu-14 manufacturerproduct-search skin-product-technicalBottomContainer">
					<cms:slot var="feature" contentSlot="${slots['ManufacturerBottomPosition']}">
						<cms:component component="${feature}"/>
					</cms:slot>
				</div>
			</c:if>

		</div>
	</views:page-default-md-full>
</c:if>
