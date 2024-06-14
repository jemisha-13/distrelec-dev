<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<spring:message code="product.carousel-teaser.image.missing" text="Image not found" var="sImageMissing"/>

<c:if test="${itemData.code != null}">
	<nav class="ctrls<c:if test="${not empty itemData.activePromotionLabels}"> compensate-promo</c:if>">
		<mod:product-tools skin="vertical" productId="${itemData.code}" />
	</nav>
</c:if>

<!-- Used in carousel-teaser-item-product and toolsitem module -->
<input type="hidden" class="hidden-product-code" value="${itemData.code}" />
<!-- End carousel-teaser-item-product and toolsitem module -->

<a href="${itemData.url}" class="item-link product1">
	<c:if test="${not empty itemData.distManufacturer or fn:length(itemData.activePromotionLabels) > 0}">	
		<div class="hd">
			<c:if test="${not empty itemData.distManufacturer}">
				<mod:product-manufacturer distManufacturer="${itemData.distManufacturer}" showLink="false" />
			</c:if>
			<c:if test="${fn:length(itemData.activePromotionLabels) > 0}">
				<div class="promo-label-wrap">
					<c:forEach items="${itemData.activePromotionLabels}" var="promoLabel" end="0">
						<mod:product-label promoLabel="${promoLabel}" />
					</c:forEach>
				</div>
			</c:if>
		</div>
	</c:if>
	<div class="bd">
		<img width="300" height="168" class="item-image" alt="${itemData.productImages[0].landscape_medium.altText == null ? sImageMissing : itemData.productImages[0].landscape_medium.altText}" src="${itemData.productImages[0].landscape_medium.url eq null ?  "/_ui/all/media/img/missing_landscape_medium.png" : itemData.productImages[0].landscape_medium.url}" />
	</div>
	<div class="ft">
		<h3 class="base item-name ellipsis" title="${itemData.name}">${itemData.name}</h3>
		<c:if test="${displayPromotionText}">
			<span class="promotion-text ellipsis" title="${itemData.promotionText}">${itemData.promotionText}</span>
		</c:if>

		<c:choose>
			<c:when test="${not empty itemData.price}">
				<span class="product-price"><format:price format="defaultSplit" priceData="${itemData.price}" /></span>
			</c:when>
			<c:otherwise>
				<c:forEach items="${itemData.volumePrices}" var="volumePriceEntry" end="0">
					<span class="product-price"><format:price format="defaultSplit" priceData="${volumePriceEntry.value.list}" /></span>
				</c:forEach>
			</c:otherwise>
		</c:choose>
	</div>
</a>
