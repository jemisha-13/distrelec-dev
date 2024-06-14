<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<spring:message code="product.image.missing" text="Image not found" var="sImageMissing"/>

<json:object>

	<json:property name="numberOfItems">
		${fn:length(productFFCarouselData)}
	</json:property>

	<c:set var="elementCounter" value="1"/>
	<json:array name="carouselData" items="${productFFCarouselData}" var="product">
		<json:object>
			<json:property name="code" value="${product.code}" />
			<json:property name="erpCode" value="${product.codeErpRelevant}" />
			<json:property name="typeName" value="${product.typeName}" /> 
			<json:property name="name" value="${product.name}" />
			<json:property name="url" value="${product.url}" />
			<json:property name="promotiontext" value="${product.promotionText}" />
			<json:property name="energyEfficiencyData" value="${product.energyEfficiency}" />

			<c:set var="landscapeMedium" value="${product.productImages[0].landscape_medium}"/>
			<c:set var="landscapeMediumWebp" value="${product.productImages[0].landscape_medium_webp}"/>
			<c:set var="imageUrl" value="${not empty landscapeMediumWebp.url ? landscapeMediumWebp.url : landscapeMedium.url}" />

			<json:property name="productImageAltText" value="${not empty landscapeMediumWebp.altText ? landscapeMediumWebp.altText : not empty landscapeMedium.altText ? landscapeMedium.altText : sImageMissing}" />

			<c:choose>
				<c:when test="${not empty imageUrl}">
					<json:property name="productImageUrl" value="${imageUrl}" />
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${currentCountry.isocode eq 'DK' || currentCountry.isocode eq 'FI' || currentCountry.isocode eq 'NO' || currentCountry.isocode eq 'SE'
									 || currentCountry.isocode eq 'LT' || currentCountry.isocode eq 'LV' || currentCountry.isocode eq 'EE' || currentCountry.isocode eq 'NL'
									 || currentCountry.isocode eq 'PL'}" >
							<json:property name="productImageUrl" value="/_ui/all/media/img/missing_landscape_medium-elfa.png" />
						</c:when>
						<c:when test="${currentCountry.isocode eq 'FR'}" >
							<json:property name="productImageUrl" value="/_ui/all/media/img/missing_landscape_medium-fr.png" />
						</c:when>
						<c:otherwise>
							<json:property name="productImageUrl" value="/_ui/all/media/img/missing_landscape_medium.png" />
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			
			<json:property name="itemPositionOneBased" value="${elementCounter}" />
			
			<json:property name="originalPackSize" value="${product.originalPackSize}" />
			<json:property name="salesUnit" value="${product.salesUnit}" />
			
			<json:property name="showCarouselItemHead" value="${not empty product.distManufacturer or fn:length(product.activePromotionLabels) > 0}" />
			
			<json:object name="distManufacturer">
				<json:property name="name" value="${product.distManufacturer.name}" />
				<json:object name="brand_logo">
					<json:property name="url" value="${product.distManufacturer.image.brand_logo.url}" />
				</json:object>
			</json:object>
			
			<json:property name="promoLabelCompensateClass">
				${not empty product.activePromotionLabels ? 'compensate-promo' : ''}
			</json:property>
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
			
			<c:set var="categories" value=""/>
			<c:forEach var="entry" items="${product.categories}" varStatus="status">
				<c:choose>
					<c:when test="${status.last}">
						<c:set var="categories" value="${categories}${entry.name}"/>
					</c:when>
					<c:otherwise>
						<c:set var="categories" value="${categories}${entry.name},"/>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			<json:property name="categories">
				${categories}
			</json:property>
			<json:property name="orderQuantityMinimum" value="${product.orderQuantityMinimum}" />
			<json:property name="orderQuantityStep" value="${product.orderQuantityStep}" />
		</json:object>
		<c:set var="elementCounter" value="${elementCounter + 1 }"/>
	</json:array>
</json:object>
