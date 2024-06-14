<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<%-- this json is also used for the product list view switch (standard / technical) --%>

<c:set var="phaseOut" value="false" />
<spring:message code="product.image.missing" text="Image not found" var="sImageMissing"/>
<json:object>
	<json:object name="paging">
		<json:property name="isLastPage" value="${isLastPage}" />
	</json:object>
	<json:object name="pagination">
		<json:property name="firstUrl" value="${searchPageData.pagination.firstUrl}" />
		<json:property name="prevUrl" value="${searchPageData.pagination.prevUrl}" />
		<json:property name="showPrevButton">
			${searchPageData.pagination.currentPage > 1 && searchPageData.pagination.numberOfPages >= 5}
		</json:property>

		<json:property name="numberOfPages" value="${searchPageData.pagination.numberOfPages}" />
		<json:property name="currentPage" value="${searchPageData.pagination.currentPage}" />

		<json:property name="nextUrl" value="${searchPageData.pagination.nextUrl}" />
		<json:property name="showNextButton">
			${(searchPageData.pagination.currentPage + 1) <= searchPageData.pagination.numberOfPages && searchPageData.pagination.numberOfPages >= 5}
		</json:property>
		<json:property name="lastUrl" value="${searchPageData.pagination.lastUrl}" />
		<json:array name="pages">
			<c:forEach var="i" begin="1" end="${searchPageData.pagination.numberOfPages}">
				<json:object>
					<json:property name="pageNr" value="${i}" />
				</json:object>
			</c:forEach>
		</json:array>
	</json:object>
	<json:object name="control">
		<json:property name="pageReload" value="${showCategoriesOnly}" />
	</json:object>
	<%-- available categories --%>
	<json:array name="categories" items="${searchPageData.categories.values}" var="category">
		<json:object>
			<json:property name="categoryUrl">
				<c:url value="${category.query.url}" />
			</json:property>
			<json:property name="categoryName">
				${category.name}
			</json:property>
			<json:property name="categoryProducts">
				${category.count}
			</json:property>
		</json:object>
	</json:array>
	<json:property name="categoriesCount" value="${fn:length(searchPageData.categories.values)}" />
	
	<%-- CategoryDisPlayDataList starts here --%>
	<json:array name="CategoryDisPlayDataList" items="${CategoryDisPlayDataList}" var="CategoryDisPlayData">
		<json:object>
			<json:property name="url">
				<c:url value="${CategoryDisPlayData.url}" />
			</json:property>
			<json:property name="name">
				${CategoryDisPlayData.name}
			</json:property>	
			
			<c:choose>
			  <c:when test="${not empty CategoryDisPlayData.image}">
			  	<json:array name="images">
				  <c:forEach var="image" items="${CategoryDisPlayData.image}">
						<json:object>
							<json:property name="${image.key}">
								 ${image.value.url}
							</json:property>	
						</json:object>
					</c:forEach>
				</json:array>	
			  </c:when>				  
			</c:choose>
			
			<json:array name="categorylevel3dataList" items="${CategoryDisPlayData.subCategoryDisPlayDataList}" var="categorylevel3data">
				<json:object>
					<json:property name="name">
						${categorylevel3data.name}
					</json:property>
					<json:property name="code">
						${categorylevel3data.code}
					</json:property>
					<json:property name="count">
						${categorylevel3data.count}
					</json:property>
					<json:property name="url">
						${categorylevel3data.query.url}
					</json:property>
					<json:property name="query">
						${categorylevel3data.query.query}
					</json:property>
					<json:property name="selected">
						${categorylevel3data.selected}
					</json:property>
				</json:object>
			</json:array>
		</json:object>
	</json:array>
	<%-- CategoryDisPlayDataList ends here --%>

	<%-- active filters / facets --%>
	<json:array name="filters" items="${searchPageData.filters}" var="filter">
		<c:if test="${not empty searchPageData.filters || not empty searchPageData.freeTextSearch}">
			<json:object>
				<json:property name="removeQueryUrl">
					<c:url value="${filter.removeQuery.url}" />
				</json:property>
				<json:property name="name">
					<c:choose>
						<c:when test="${not empty filter.facetValuePropertyName}">
							<spring:theme code="${filter.facetValuePropertyName}" arguments="${filter.facetValuePropertyNameArguments}" 
								argumentSeparator="${filter.facetValuePropertyNameArgumentSeparator}" var="filterFacetValueName" />
						</c:when>
						<c:otherwise>
							<c:set value="${filter.facetValueName}" var="filterFacetValueName" />
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${filter.type.value eq 'checkbox'}">
							<spring:theme code="search.nav.appliedFacet" arguments="${filter.facetName}^${filterFacetValueName}" argumentSeparator="^" />
						</c:when>
						<c:otherwise>
							<spring:theme code="search.nav.appliedMultiSelectFacet" arguments="${filter.facetName}" />
						</c:otherwise>
					</c:choose>
				</json:property>
				<json:property name="isCategory" value="${filter.categoryFilter}" />
				<json:property name="facetValueName" value="${filter.facetValueName}" />
				<json:property name="filterString" value="${filter.filterString}" />
			</json:object>
		</c:if>
	</json:array>
	<%-- all available facet groups for current query --%>
	<json:array name="facetGroups" items="${searchPageData.otherFacets}" var="facet">
		<c:if test="${not empty facet.values}">
			<json:object>
				<json:property name="code">
					${facet.code}
				</json:property>
				<json:property name="name">
					${facet.name}
				</json:property>
				<json:property name="typeLowerCase">
					${facet.type.value}
				</json:property>
				<json:property name="type">
					${fn:toUpperCase(facet.type.value)}
				</json:property>
				<c:choose>
					<c:when test="${facet.hasSelectedElements}">
						<json:property name="expansionStatus" value="is-expanded" />
					</c:when>
					<c:otherwise>
						<json:property name="expansionStatus" value="" />
					</c:otherwise>
				</c:choose>
				<json:array name="values" items="${facet.values}" var="facetValue">
					<json:object>
						<json:object name="query">
							<json:property name="url">
								<c:url value="${facetValue.query.url}" />
							</json:property>
						</json:object>
						<json:property name="name" value="${facetValue.name}" />
						<json:property name="facetValueName">
							<c:choose>
								<c:when test="${not empty facetValue.propertyName}">
									<spring:theme code="${facetValue.propertyName}" arguments="${facetValue.propertyNameArguments}" 
										argumentSeparator="${facetValue.propertyNameArgumentSeparator}" />
								</c:when>
								<c:otherwise>
									${facetValue.name}
								</c:otherwise>
							</c:choose>
						</json:property>
						<json:property name="count" value="${facetValue.count}" />
						<json:property name="isSelected" >
							${facetValue.selected}
						</json:property>
						<json:property name="filterString" value="${facetValue.filterString}" />
						<c:if test="${facet.type.value eq 'slider'}">
							<json:property name="absoluteMinValue" value="${facetValue.absoluteMinValue}" />
							<json:property name="absoluteMaxValue" value="${facetValue.absoluteMaxValue}" />
							<json:property name="selectedMinValue" value="${facetValue.selectedMinValue}" />
							<json:property name="selectedMaxValue" value="${facetValue.selectedMaxValue}" />
						</c:if>
					</json:object>
				</json:array>
			</json:object>
		</c:if>
	</json:array>
	<%-- additional lazy facets for current query --%>
	<json:array name="lazyFacets" items="${searchPageData.lazyFacets}" var="lazyFacet">
		<json:object>
			<json:property name="lazyFacetUrl">
				<c:url value="${lazyFacet.query.url}" />
			</json:property>
			<json:property name="lazyFacetName">
				${lazyFacet.name}
			</json:property>
		</json:object>
	</json:array>
	<%-- products for current query --%>
	<json:property name="productsCount" value="${searchPageData.pagination.totalNumberOfResults}" />
	<json:property name="productsCountCatalogPlus" value="${searchPageData.pagination.totalNumberOfCatalogPlusResults}" />
	<json:array name="products" items="${searchPageData.results}" var="product">
		<json:object>
			<json:property name="code" value="${product.code}" />
			
			<c:set var="formattedCodeErpRelevant">
				<formatArticle:articleNumber articleNumber="${product.codeErpRelevant}"  />
			</c:set>
			
			<json:property name="codeErpRelevant" value="${formattedCodeErpRelevant}" />    
			<json:property name="name" value="${product.name}" />
			<json:property name="url" value="${product.url}" />
			<json:property name="productFamilyUrl" value="${product.productFamilyUrl}" />
			<json:property name="typeName" value="${product.typeName}" />
			<json:property name="salesUnit" value="${product.salesUnit}" />
			<json:property name="productEnergyEfficiency" value="${product.energyEfficiency}" />
			
			<!-- State Booleans needed for easier DotTemplating on ajax load products -->
			<json:property name="showProductLink" value="${product.buyable  or ((not empty product.endOfLifeDate) and product.buyableReplacementProduct)}" />
			<json:property name="phaseOut" value="${(product.buyable == false) and (empty product.endOfLifeDate)}" />
			<json:property name="eol" value="${not empty product.endOfLifeDate}" />
			<json:property name="eolWithReplacement" value="${not empty product.endOfLifeDate and product.buyableReplacementProduct}" />
			<json:property name="buyableReplacementProduct" value="${product.buyableReplacementProduct}" />
			<json:property name="buyable" value="${product.buyable}" />
			<json:property name="catPlusItem" value="${product.catPlusItem}" />
			<!-- State Booleans needed for easier DotTemplating on ajax load products -->
			
			<c:set var="formattedDate">
				<fmt:formatDate value="${product.endOfLifeDate}" />
			</c:set>
			<json:property name="endOfLifeDate" value="${formattedDate}" />
			<json:property name="replacementReason" value="${product.replacementReason}" />

			<c:set var="portraitSmall" value="${product.productImages[0].portrait_small}"/>
			<c:set var="portraitSmallWebp" value="${product.productImages[0].portrait_small_webp}"/>

			<json:property name="productImageAltText" value="${not empty portraitSmallWebp.altText ? portraitSmallWebp.altText : not empty portraitSmall.altText ? portraitSmall.altText : sImageMissing}" />
			<json:property name="productImageUrl" value="${not empty portraitSmallWebp.url ? portraitSmallWebp.url : not empty portraitSmall.url ? portraitSmall.url : '/_ui/all/media/img/missing_portrait_small.png'}" />
			
			<json:property name="manufacturer" value="${product.manufacturer}" />
			<json:object name="distManufacturer">
				<json:property name="urlId" value="${product.distManufacturer.urlId}" />
				<json:property name="name" value="${product.distManufacturer.name}" />
				<json:object name="image">
					<json:object name="brand_logo">
						<json:property name="url"
							value="${product.distManufacturer.image.brand_logo.url}" />
					</json:object>
				</json:object>
			</json:object>
			<json:array name="activePromotionLabels" items="${product.activePromotionLabels}" var="promotion">
				<json:object>
					<json:property name="code" value="${promotion.code}" />
					<json:property name="label" value="${promotion.label}" />
				</json:object>
			</json:array>
			<json:object name="price">
				<json:property name="currency">
					<c:forEach items="${product.volumePricesMap}" var="volumePriceEntry" end="0">
						<format:price format="currency" priceData="${volumePriceEntry.value.list}" />
					</c:forEach>
				</json:property>
				<json:property name="formattedValue">
					<c:forEach items="${product.volumePricesMap}" var="volumePriceEntry" end="0">
						<format:price format="price" priceData="${volumePriceEntry.value.list}" />
					</c:forEach>
				</json:property>
			</json:object>
			<json:array name="volumePrices">
				<c:forEach items="${product.volumePricesMap}" var="volumePrice" begin="0" end="1">
					<c:if test="${not empty volumePrice.value.custom}">
						<c:set var="hasCustomPrices" value="${true}" />
						<c:set var="yourPriceDiffers" value="${volumePrice.value.list.value gt volumePrice.value.custom.value}" />
					</c:if>
				</c:forEach>
				<c:forEach items="${product.volumePricesMap}" var="entry" end="2">
					<json:object>
						<c:set var="currency">
							<format:price format="currency" priceData="${entry.value.list}" />
						</c:set>
						<c:set var="customPrice">
							<format:price format="price" priceData="${entry.value.custom}" />
						</c:set>
						<c:set var="listPrice">
							<format:price format="price" priceData="${entry.value.list}" />
						</c:set>
						<json:property name="minQuantity" value="${entry.key}" />
						<json:property name="currency" value="${currency}" />
						<c:choose>
							<c:when test="${yourPriceDiffers}">
								<json:property name="value" value="${customPrice}" />
							</c:when>
							<c:when test="${hasCustomPrices}">
								<json:property name="value" value="${customPrice}" />
							</c:when>
							<c:otherwise>
								<json:property name="value" value="${listPrice}" />
							</c:otherwise>
						</c:choose>
					</json:object>
				</c:forEach>
			</json:array>
			<json:array name="technicalAttributes">
				<c:forEach items="${product.technicalAttributes}" var="attribute" begin="0" end="9">
						<json:object>
							<json:property name="key">
								${attribute.key}
							</json:property>
							<json:property name="value">
								${attribute.value}
							</json:property>
						</json:object>
				</c:forEach>
			</json:array>
			<json:property name="teaserTrackingId">
				<c:set var="codeErpRelevant" value="${product.codeErpRelevant == undefined ? 'x' : product.codeErpRelevant}" />
				<c:if test="${product.buyable or eolWithReplacement}">
					${wtTeaserTrackingId}.${codeErpRelevant}.-
				</c:if>
			</json:property>
		</json:object>
	</json:array>
	<json:object name="factFinderCampaign">
		<json:array name="advisorCampaigns" items="${searchPageData.advisorCampaigns}" var="advisorCampaign">
			<json:object>
				<json:array name="advisorQuestions" items="${advisorCampaign.questions}" var="question">
					<json:object>
						<json:property name="questionText" value="${question.question}" />

						<c:if test="${not empty question.answers}">
							<json:array name="advisorAnswers" items="${question.answers}" var="answer">
								<json:object>
									<json:property name="queryUrl" value="${answer.query.url}" />
									<json:property name="text" value="${answer.text}" />
									<json:property name="image" value="${answer.image}" />
								</json:object>
							</json:array>
						</c:if>
					</json:object>
				</json:array>
			</json:object>
		</json:array>
		<json:array name="feedbackCampaigns" items="${searchPageData.feedbackCampaigns}" var="feedbackCampaign">
			<json:object>
				<json:property name="id" value="${feedbackCampaign.id}" />
				<json:property name="category" value="${feedbackCampaign.category}" />
				<json:property name="name" value="${feedbackCampaign.name}" />
				<json:property name="feedbackTextTop" value="${feedbackCampaign.feedbackTexts['SearchResult_top']}" />
				<json:property name="componentWidth" value="fullWidth" />
				<json:property name="maxNumberToDisplay" value="0" />
				<json:property name="autoplay" value="false" />
				<json:property name="autoplayTimeout" value="0" />
				<json:property name="autoplayDirection" value="left" />
				<json:property name="displayPromotionText" value="false" />

				<c:set var="elementCounter" value="1"/>
				<c:if test="${not empty feedbackCampaign.pushedProducts}">
					<json:array name="pushedProductsList" items="${feedbackCampaign.pushedProducts}" var="itemData">
						<json:object>
							<json:property name="code" value="${itemData.code}" />
							<json:property name="erpCode" value="${itemData.codeErpRelevant}" />
							<json:property name="name" value="${itemData.name}" />
							<json:property name="promotionText" value="${itemData.promotionText}" />

							<json:property name="promoLabelCompensateClass">
								${not empty itemData.activePromotionLabels ? 'compensate-promo' : ''}
							</json:property>
							<json:array name="activePromotionLabels" items="${itemData.activePromotionLabels}" var="promotion">
								<json:object>
									<json:property name="code" value="${promotion.code}" />
									<json:property name="label" value="${promotion.label}" />
								</json:object>
							</json:array>

							<json:object name="price">
								<json:property name="currency">
									<c:forEach items="${itemData.volumePrices}" var="volumePriceEntry" end="0">
										<format:price format="currency" priceData="${volumePriceEntry.value.list}" />
									</c:forEach>
								</json:property>
								<json:property name="formattedValue">
									<c:forEach items="${itemData.volumePrices}" var="volumePriceEntry" end="0">
										<format:price format="price" priceData="${volumePriceEntry.value.list}" />
									</c:forEach>
								</json:property>
							</json:object>

							<json:property name="typeName" value="${itemData.typeName}" />
							<json:property name="url" value="${itemData.url}" />

							<json:property name="itemPositionOneBased" value="${elementCounter}" />
							<json:property name="showCarouselItemHead" value="${not empty itemData.distManufacturer or fn:length(itemData.activePromotionLabels) > 0}" />

							<json:property name="manufacturer" value="${itemData.manufacturer}" />
							<json:object name="distManufacturer">
								<json:property name="name" value="${itemData.distManufacturer.name}" />
								<json:object name="brand_logo">
									<json:property name="url" value="${itemData.distManufacturer.image.brand_logo.url}" />
								</json:object>
							</json:object>

							<c:set var="landscapeMedium" value="${product.productImages[0].landscape_medium}"/>
							<c:set var="landscapeMediumWebp" value="${product.productImages[0].landscape_medium_webp}"/>

							<json:property name="productImageAltText" value="${not empty landscapeMediumWebp.altText ? landscapeMediumWebp.altText : not empty landscapeMedium.altText ? landscapeMedium.altText : sImageMissing}" />
							<json:property name="productImageUrl" value="${not empty landscapeMediumWebp.url ? landscapeMediumWebp.url : not empty landscapeMedium.url ? landscapeMedium.url : '/_ui/all/media/img/missing_landscape_medium.png'}" />

							<json:array name="volumePrices" items="${itemData.volumePrices}" var="volumePriceEntry">
								<json:object>
									<json:property name="value" value="${volumePriceEntry.value.list}" />
								</json:object>
							</json:array>
						</json:object>
						<c:set var="elementCounter" value="${elementCounter + 1 }"/>
					</json:array>
				</c:if>
			</json:object>
		</json:array>
	</json:object>
</json:object>