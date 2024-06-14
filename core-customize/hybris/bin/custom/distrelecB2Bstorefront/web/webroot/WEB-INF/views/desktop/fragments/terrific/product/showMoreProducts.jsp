<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

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
			${searchPageData.pagination.currentPage > 1}
		</json:property>
		
		<json:property name="numberOfPages" value="${searchPageData.pagination.numberOfPages}" />
		<json:property name="currentPage" value="${searchPageData.pagination.currentPage}" />
		
		<json:property name="nextUrl" value="${searchPageData.pagination.nextUrl}" />
		<json:property name="showNextButton">
			${(searchPageData.pagination.currentPage + 1) <= searchPageData.pagination.numberOfPages}
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
	<json:array name="products" items="${products}" var="product">
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

			<!-- State Booleans needed for easier DotTemplating on ajax show more -->
			<json:property name="showProductLink" value="${product.buyable  or ((not empty product.endOfLifeDate) and product.buyableReplacementProduct)}" />
			<json:property name="phaseOut" value="${(product.buyable == false) and (empty product.endOfLifeDate)}" />
			<json:property name="eol" value="${not empty product.endOfLifeDate}" />
			<json:property name="eolWithReplacement" value="${not empty product.endOfLifeDate and product.buyableReplacementProduct}" />
			<json:property name="buyableReplacementProduct" value="${product.buyableReplacementProduct}" />
			<json:property name="buyable" value="${product.buyable}" />
			<json:property name="catPlusItem" value="${product.catPlusItem}" />
			<!-- State Booleans needed for easier DotTemplating on ajax show more -->

			<c:set var="formattedDate">
				<fmt:formatDate value="${product.endOfLifeDate}" />
			</c:set>
			<json:property name="endOfLifeDate" value="${formattedDate}" />
			<json:property name="replacementReason" value="${product.replacementReason}" />

			<json:property name="productImageAltText"  value="${not empty product.productImages[0].portrait_small_webp.altText ? product.productImages[0].portrait_small_webp.altText : not empty product.productImages[0].portrait_small.altText ? product.productImages[0].portrait_small.altText : sImageMissing }"/>
			<json:property name="productImageUrl" value="${not empty product.productImages[0].portrait_small_webp.url ? product.productImages[0].portrait_small_webp.url : not empty product.productImages[0].portrait_small.url ? product.productImages[0].portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>

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

							<json:property name="productImageAltText" value="${not empty product.productImages[0].landscape_medium_webp.altText ? product.productImages[0].landscape_medium_webp.altText : not empty product.productImages[0].landscape_medium.altText ? product.productImages[0].landscape_medium.altText : sImageMissing }"/>
							<json:property name="productImageUrl" value="${not empty product.productImages[0].landscape_medium_webp.url ? product.productImages[0].landscape_medium_webp.url : not empty product.productImages[0].landscape_medium.url ? product.productImages[0].landscape_medium.url : '/_ui/all/media/img/landscape_medium.png' }"/>

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
