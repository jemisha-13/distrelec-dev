<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<json:object>
	<json:object name="paging">
		<json:property name="isLastPage" value="${fn:length(products) == 0}" />
	</json:object>
	<json:array name="products" items="${products}" var="product">
		<json:object>
			<json:property name="code" value="${product.code}" />
			<json:property name="codeErpRelevant" value="${product.codeErpRelevant}" />
			<json:property name="name" value="${product.name}" />
			<json:property name="url" value="${product.url}" />
			<json:property name="productFamilyUrl" value="${product.productFamilyUrl}" />
			<json:property name="typeName" value="${product.typeName}" />
			<json:property name="salesUnit" value="${product.salesUnit}" />
			
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

			<c:set var="portraitSmall" value="${product.productImages[0].portrait_small}"/>
			<c:set var="portraitSmallWebp" value="${product.productImages[0].portrait_small_webp}"/>

			<json:property name="productImageAltText" value="${not empty portraitSmallWebp.altText ? portraitSmallWebp.altText : not empty portraitSmall.altText ? portraitSmall.altText : sImageMissing}" />
			<json:property name="productImageUrl" value="${not empty portraitSmallWebp.url ? portraitSmallWebp.url : not empty portraitSmall.url ? portraitSmall.url : '/_ui/all/media/img/missing_portrait_small.png'}" />
			
			<json:property name="originalPackSize" value="${product.originalPackSize}" />
			<json:property name="salesUnit" value="${product.salesUnit}" />
			
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
</json:object>