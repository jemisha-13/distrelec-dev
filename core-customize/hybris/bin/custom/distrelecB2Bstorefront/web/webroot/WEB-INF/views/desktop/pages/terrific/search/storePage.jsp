<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-responsive skin-layout-store skin-layout-wide skin-layout-nonavigation skin-layout-full-width">

	<div class="md-system">
		<c:set var="clearanceFlag" value="${searchPageData.pagination.totalNumberOfResults eq 0}"/>

		<div id="breadcrumb" class="breadcrumb">
			<mod:breadcrumb template="product" skin="product" />
		</div>

		<div class="plp-content">
			<div class="container">
				<mod:global-messages/>
			</div>
		</div>

		<fmt:formatNumber type="number" value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />

		<div class="container">

			<h1 class="base page-title page-title--with-count <c:if test="${clearanceFlag eq true}">hidden</c:if>">
				<a href="#" class="page-title__back-navigation"><i class="fa fa-arrow-left"></i></a>


				<div class="desktop-header__text for-desktop">
						${cmsPage.title}
					<span class="matched-products-count">
				<b>(</b>${matchedProductsCount} <span class="matched-products-count__text"><spring:message code="text.products" /></span><b>)</b>
			</span>
				</div>

				<div class="mobile-header__text for-mobile">
						${cmsPage.title}
					<span class="matched-products-count">
						<spring:theme code="text.plp.showing.of" arguments="${firstProductNumber},${lastProductNumber}"/>&nbsp;${matchedProductsCount}&nbsp;<spring:message code="text.products" />
					</span>
				</div>

			</h1>

		</div>

	</div>

	<%-- CMS content slot --%>
	<cms:slot var="feature" contentSlot="${slots['StoreContent']}">
		<cms:component component="${feature}"/>
	</cms:slot>

	<div class="plp-content md-system clear">

		<mod:productlist-pagination template="left-side-filters" skin="plp" htmlClasses="sidebar-filters-pagination" />

		<div class="container">

			<c:if test="${searchPageData.pagination.totalNumberOfResults > 0}">
				<div class="plp-content__nav-filters hidden">

					<div class="plp-content__categorynav-facets">
						<c:if test="${ fn:length(categoryDisplayDataList) > 0}">
							<mod:categorynav template="plp" skin="plp" />
						</c:if>
					</div>

					<div id="plp-filter-action-bar-sidebar" class="productlistpage__filter-action-bar sidebar-applied-filters">
						<div class="container">
							<mod:facets template="plp-filter-controllbar" skin="plp-filter-controllbar" searchPageData="${searchPageData}" />
						</div>

					</div>

				</div>
			</c:if>

		</div>

		<c:if test="${clearanceFlag eq false}">
			<mod:productlist template="structure" skin="structure" searchPageData="${searchPageData}"/>
		</c:if>

	</div>
</views:page-default>
