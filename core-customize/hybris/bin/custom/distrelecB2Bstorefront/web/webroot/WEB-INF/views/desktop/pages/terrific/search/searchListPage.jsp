<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<c:if test="${isOCI eq true}">
	<spring:eval expression="@configurationService.configuration.getString('default.view.standard')" var="defaultViewStandard" scope="session" />

	<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-responsive skin-layout-search skin-layout-wide skin-layout-product-list skin-layout-nonavigation skin-layout-full-width">

		<div id="breadcrumb" class="breadcrumb">
			<mod:breadcrumb template="product" skin="product" />
		</div>


		<fmt:formatNumber type="number" value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />

		<div class="plp-content">
			<div class="container">
				<mod:global-messages/>
			</div>
			<div class="plp-content__top">

				<div class="container plp-content__top__container">

					<h1 class="base page-title page-title--with-count">
						<a href="#" class="page-title__back-navigation"><i class="fa fa-arrow-left"></i></a>

						<div class="desktop-header__text for-desktop">

							<c:choose>
								<c:when test="${currentCountry.isocode eq 'LV'}">
									<spring:message code="text.showing.result.for"  arguments='"${searchPageData.freeTextSearch}"' />
								</c:when>
								<c:otherwise>
									<spring:message code="text.showing.result.for" /> <span class="search-term"> "${searchPageData.freeTextSearch}"</span>
								</c:otherwise>
							</c:choose>

							<span class="matched-products-count">
						<b>(</b>${matchedProductsCount} <span class="matched-products-count__text"><spring:message code="text.products" /></span><b>)</b>
					</span>
						</div>

						<div class="mobile-header__text for-mobile">
							<spring:message code="text.showing.result.for" /> <span class="search-term"> "${searchPageData.freeTextSearch}"</span>
							<span class="matched-products-count">
						<span class="xmod-filter__matched-products-count facet-result-count">${searchPageData.pagination.totalNumberOfResults}</span>
						<span class="matched-products-count__text"><spring:message code="text.products" /></span>
					</span>
						</div>

					</h1>

						<%-- FEEDBACK CAMPAIGN on pageLoad (replaces C81 SearchResultsDynamicBanner [mod:dynamic-banner], see DISTRELEC-2482) --%>
					<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">
						<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_top'] || not empty feedbackCampaign.feedbackTexts['Fusion_SearchResult_top'] || not empty feedbackCampaign.pushedProducts}">
							<mod:campaign skin="feedback" feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_top']}" pushedProductsList="${feedbackCampaign.pushedProducts}" />
						</c:if>
					</c:forEach>

						<%-- ADVISOR CAMPAIGN on pageLoad --%>
					<c:forEach var="advisorCampaign" items="${searchPageData.advisorCampaigns}">
						<mod:campaign template="advisor" advisorQuestions="${advisorCampaign.questions}" htmlClasses="js-initial-campaign" />
					</c:forEach>

				</div>



			</div>

			<div class="plp-content__categorynav-facets">
				<div class="container">
					<mod:categorynav template="facets" skin="facets" />
				</div>
			</div>

			<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">
				<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_mid']}">
					<mod:campaign skin="feedback" feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_mid']}" />
				</c:if>
			</c:forEach>

			<mod:productlist template="structure" skin="structure" searchPageData="${searchPageData}"/>

			<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">
				<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_bot']}">
					<mod:campaign skin="feedback" feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_bot']}" />
				</c:if>
			</c:forEach>

		</div>
	</views:page-default-md-full>
</c:if>

<c:if test="${isOCI eq false}">
	<spring:eval expression="@configurationService.configuration.getString('default.view.standard')" var="defaultViewStandard" scope="session" />

	<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-responsive skin-layout-search skin-layout-wide skin-layout-product-list skin-layout-nonavigation skin-layout-full-width">

		<div id="breadcrumb" class="breadcrumb">
			<mod:breadcrumb template="product" skin="product" />
		</div>


		<fmt:formatNumber type="number" value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />

		<div class="plp-content">
			<div class="container">
				<mod:global-messages/>
			</div>
			<div class="plp-content__top">

				<div class="container plp-content__top__container">

					<%--Title section for Fusion search--%>
					<h1 class="base page-title page-title--with-count search">
						<a href="#" class="page-title__back-navigation"><i class="fa fa-arrow-left"></i></a>

						<div class="desktop-header__text for-desktop">

							<c:choose>
								<c:when test="${currentCountry.isocode eq 'LV'}">
									<spring:message code="text.showing.result.for"  arguments='"${searchPageData.freeTextSearch}"' />
								</c:when>
								<c:otherwise>
									<spring:message code="text.showing.result.for" /> <span class="search-term"> "${searchPageData.freeTextSearch}"</span>
								</c:otherwise>
							</c:choose>

						</div>

						<div class="mobile-header__text for-mobile search">
							<spring:message code="text.showing.result.for" /> <span class="search-term"> "${searchPageData.freeTextSearch}"</span>

							<c:choose>
								<c:when test="${currentLanguage.getIsocode() eq 'fr'}">
									<span class="matched-products-count">
										<spring:theme code="text.plp.showing.of" arguments="${firstProductNumber},${lastProductNumber},${matchedProductsCount}"/>&nbsp;<spring:message code="text.products" />
									</span>
								</c:when>
								<c:otherwise>
									<span class="matched-products-count">
										<spring:theme code="text.plp.showing.of" arguments="${firstProductNumber},${lastProductNumber}"/>&nbsp;${matchedProductsCount}&nbsp;<spring:message code="text.products" />
									</span>
								</c:otherwise>
							</c:choose>

						</div>

					</h1>



						<%-- ADVISOR CAMPAIGN on pageLoad --%>
					<c:forEach var="advisorCampaign" items="${searchPageData.advisorCampaigns}">
						<mod:campaign template="advisor" advisorQuestions="${advisorCampaign.questions}" htmlClasses="js-initial-campaign" />
					</c:forEach>

				</div>

			</div>

			<mod:productlist-pagination template="left-side-filters" skin="plp" htmlClasses="sidebar-filters-pagination" />

			<div class="container">

				<%-- FEEDBACK CAMPAIGN on pageLoad (replaces C81 SearchResultsDynamicBanner [mod:dynamic-banner], see DISTRELEC-2482) --%>
				<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">
					<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_top'] || not empty feedbackCampaign.pushedProducts}">
						<mod:campaign skin="feedback" feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_top']}" pushedProductsList="${feedbackCampaign.pushedProducts}" />
					</c:if>
				</c:forEach>

				<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">
					<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_mid']}">
						<mod:campaign skin="feedback" feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_mid']}" />
					</c:if>
				</c:forEach>

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

						<c:if test="${ fn:length(searchPageData.otherFacets) > 0}">
							<div class="plp-content__filters">
								<mod:productlist template="filters" skin="filters" searchPageData="${searchPageData}"/>
							</div>
						</c:if>
					</div>
				</c:if>

			</div>

			<mod:productlist template="structure" skin="structure" searchPageData="${searchPageData}"/>

			<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">
				<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_bot']}">
					<mod:campaign skin="feedback" feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_bot']}" />
				</c:if>
			</c:forEach>

		</div>
	</views:page-default-md-full>
</c:if>
