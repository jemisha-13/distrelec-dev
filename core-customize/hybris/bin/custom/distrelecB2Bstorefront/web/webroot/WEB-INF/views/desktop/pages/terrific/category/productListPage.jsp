<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<spring:eval expression="@configurationService.configuration.getString('default.view.standard')" var="defaultViewStandard" scope="session" />
<spring:theme code="product.list.no.result" text="Unfortunately, your query returned no relevant results:" var="sListNoResult" arguments="${request.getParameter('digitalDataLayerTerm')}" />

<c:set var="redirectedFromFilteredSearch" value="${request.getParameter('RedirectedFromSearch')}" />

<views:page-default-seo pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-responsive skin-layout-category skin-layout-wide skin-layout-product-list skin-layout-nonavigation skin-layout-full-width ${useTechnicalView ? '' : 'skin-layout-category--standard'}" >

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb template="product" skin="product" />
	</div>

	<div class="plp-content md-system">

		<div class="container">

			<c:if test="${empty eolMessage}">
				<mod:global-messages />
			</c:if>

			<c:if test="${not empty eolMessage}">
				<mod:global-messages headline="" body="${eolMessage}" type="information" widthPercent="100%" displayIcon="true" />
			</c:if>
		</div>

		<fmt:formatNumber type="number" value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />

		<c:if test="${not empty metaData}">
	 		<mod:metadata-content metaData="${metaData}" />
	 	</c:if>

		<c:choose>
			<c:when test="${isOCI eq true}">
				<div class="plp-content__categorynav-facets">
			</c:when>
			<c:otherwise>
				<div class="plp-content">
			</c:otherwise>
		</c:choose>
			<div class="container plp-content__top__container">

				<div class="plp-content__top">

					<h1 class="base page-title page-title--with-count">
						<c:if test="${isOCI eq true}">
							<a href="#" class="page-title__back-navigation"><i class="fa fa-arrow-left"></i></a>
						</c:if>


						<c:choose>
							<c:when test="${isOCI eq true}">
								<div class="desktop-header__text for-desktop">
										${categoryPageData.sourceCategory.name}
									<span class="matched-products-count">
								<b>(</b>${matchedProductsCount} <span class="matched-products-count__text"><spring:message code="text.products" /></span><b>)</b>
							</span>
								</div>
							</c:when>
							<c:otherwise>
								<div class="desktop-header__text for-desktop">
										${categoryPageData.sourceCategory.name}
								</div>
								<div class="desktop-header__text for-mobile">
										${categoryPageData.sourceCategory.name}
								</div>
							</c:otherwise>
						</c:choose>

						<c:if test="${isOCI eq true}">
							<div class="mobile-header__text for-mobile">
									${categoryPageData.sourceCategory.name}
								<span class="matched-products-count">
								<span class="xmod-filter__matched-products-count facet-result-count">${searchPageData.pagination.totalNumberOfResults}</span>
								<span class="matched-products-count__text"><spring:message code="text.products" /></span>
							</span>
							</div>
						</c:if>

					</h1>

					<cms:slot var="categoryDescription" contentSlot="${slots['CatDescriptionContent']}">
						<cms:component component="${categoryDescription}"/>
					</cms:slot>

					<c:forEach var="advisorCampaign" items="${searchPageData.advisorCampaigns}">
						<mod:campaign template="advisor" advisorQuestions="${advisorCampaign.questions}" />
					</c:forEach>

					<c:if test="${redirectedFromFilteredSearch}">
						<div id="zero-category-search" class="col-sm-12 banner-wrapper__noresult">
								${sListNoResult}
						</div>
					</c:if>
				
					<c:choose>
						<c:when test="${isOCI eq true}">
							<%-- FEEDBACK CAMPAIGN --%>
							<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">
								<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_top'] || not empty feedbackCampaign.pushedProducts}">
									<mod:campaign feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_top']}" pushedProductsList="${feedbackCampaign.pushedProducts}" />
								</c:if>
							</c:forEach>

							</div>

							<mod:categorynav template="facets" skin="facets" />
							</div>
							</div>

							<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">
								<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_mid']}">
									<mod:campaign skin="feedback" feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_mid']}" />
								</c:if>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<%-- FEEDBACK CAMPAIGN --%>
							<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">
								<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_top'] || not empty feedbackCampaign.pushedProducts}">
									<div class="container">
										<mod:campaign feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_top']}" pushedProductsList="${feedbackCampaign.pushedProducts}" />
									</div>
								</c:if>
							</c:forEach>

							</div>
							</div>
							</div>


							<div class="container">

								<!-- Category SEO data  -->
								<mod:seo template="category" skin="category"/>
								
								<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">
									<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_mid']}">
										<mod:campaign skin="feedback" feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_mid']}" />
									</c:if>
								</c:forEach>

								<c:if test="${searchPageData.pagination.totalNumberOfResults > 0}">
									<mod:productlist-pagination template="left-side-filters" skin="plp" htmlClasses="sidebar-filters-pagination" />
									<div class="plp-content__nav-filters hidden">
										<c:if test="${ fn:length(categoryDisplayDataList) > 0}">
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

						</c:otherwise>
					</c:choose>


		<mod:productlist template="structure" skin="structure" searchPageData="${searchPageData}"/>

		<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">
			<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_bot']}">
				<mod:campaign skin="feedback" feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_bot']}" />
			</c:if>
		</c:forEach>

	</div>

</views:page-default-seo>
