<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<c:if test="${productItemData.code != null}">
	<nav class="ctrls<c:if test="${not empty productItemData.activePromotionLabels}"> compensate-promo</c:if>">
		<mod:product-tools skin="vertical" productId="${productItemData.code}" />
	</nav>
</c:if>

<!-- Used in the toolsitem module !-->
<input type="hidden" class="hidden-product-code" value="${productItemData.code}" />
<!-- End !-->

<a class="js-item-link" href="${productItemData.url}">
	<c:if test="${not empty productItemData.distManufacturer or fn:length(productItemData.activePromotionLabels) > 0}">
		<div class="hd">
			<c:if test="${not empty productItemData.distManufacturer}">
				<mod:product-manufacturer distManufacturer="${productItemData.distManufacturer}" showLink="false" />
			</c:if>
			<c:if test="${fn:length(productItemData.activePromotionLabels) > 0}">
				<div class="promo-label-wrap">
					<c:forEach items="${productItemData.activePromotionLabels}" var="promoLabel" end="0">
						<mod:product-label promoLabel="${promoLabel}" />
					</c:forEach>
				</div>
			</c:if>
		</div>
	</c:if>
	<div class="bd">
		<c:set var="productImage" value="${productItemData.productImages[0]}"/>
		<c:set var="portraitMediumJpg" value="${not empty productImage.portrait_medium.url ? productImage.portrait_medium.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
		<c:set var="portraitMediumWebP" value="${not empty productImage.portrait_medium_webp.url ? productImage.portrait_medium_webp.url : portraitMediumJpg}"/>
		<c:set var="portraitMediumAlt" value="${not empty productImage.portrait_medium_webp.altText ? productImage.portrait_medium_webp.altText : not empty productImage.portrait_medium.altText == null ? productImage.portrait_medium.altText : sImageMissing }"/>
		<picture>
			<source srcset="${portraitMediumWebP}">
			<img class="item-image" alt="${portraitMediumAlt}" src="${portraitMediumJpg}"/>
		</picture>
	</div>
	<div class="ft">
		<h3 class="base item-name">${productItemData.name}</h3>
		<c:if test="${not empty productItemData.promotionText}">
			<span class="promotion-text">${productItemData.promotionText}</span>
		</c:if>
		<span class="product-price"><format:price format="defaultSplit" priceData="${productItemData.price}" /></span>
	</div>
</a>