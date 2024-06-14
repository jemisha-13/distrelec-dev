<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<c:set var="trackingcode" value="${trackingcode}" />

<!-- Used in the toolsitem module !-->
<input type="hidden" class="hidden-product-code" value="${productItemData.code}" />
<!-- End !-->

<div class="hd">
	<c:if test="${not empty productItemData.distManufacturer}">
		<mod:product-manufacturer distManufacturer="${productItemData.distManufacturer}" showLink="false" skin="carpet" />
	</c:if>

    <!-- 	DISTRELEC-10879: prioritise discount higher than all other labels & only show this one -->
	<c:choose>
		<c:when test="${showOriginalPrice and discount ge 3}">
			<div class="promo-label-wrap discount-wrapper">
				<span class="discount-label">-${discount}%</span>
			</div>
		</c:when>
		<c:otherwise>
			<c:if test="${fn:length(productItemData.activePromotionLabels) > 0 && empty productItemData.energyEfficiency}">
				<div class="promo-label-wrap">
					<c:forEach items="${productItemData.activePromotionLabels}" var="promoLabel" end="0">
						<mod:product-label promoLabel="${promoLabel}" skin="carpetnew" template="carpetnew" />
					</c:forEach>
				</div>
			</c:if>		
		</c:otherwise>
	</c:choose>
	
	<c:if test="${not empty product.energyEfficiency}">
       <mod:energy-efficiency-label skin="carpet" product="${product}" />
	</c:if>

</div>

<div class="bd">

<%-- Variables are needed to determine if the product link should be displayed or not --%>
<c:if test="${product.buyable or eolWithReplacement}">

</c:if>

	<a class="item-link product" href="${productItemData.url}?int_cid=0000homepage.${trackingcode}-Carpet" data-youtubeid="${youTubeID}">
		<img class="item-image" alt="${productItemData.productImages[0].landscape_medium.altText == null ? sImageMissing : productItemData.productImages[0].landscape_medium.altText}" src="${productItemData.productImages[0].landscape_medium.url eq null ?  "/_ui/all/media/img/missing_landscape_medium.png" : productItemData.productImages[0].landscape_medium.url}" />

		<h3 class="base item-name ellipsis" title="${productItemData.name}">${productItemData.name}</h3>
		<c:if test="${not empty productItemData.typeName or not empty productItemData.distManufacturer.name}">
			<span class="promotion-text ellipsis">
				<c:if test="${not empty productItemData.typeName}">${productItemData.typeName}</c:if>
				<c:if test="${not empty productItemData.typeName and not empty productItemData.distManufacturer.name}">,&nbsp;</c:if>
				<c:if test="${not empty productItemData.distManufacturer.name}">${productItemData.distManufacturer.name}</c:if>
			</span>
		</c:if>
	</a>
	<div class="price-wrap">
		<a class="btn btn-buy ellipsis" href="#"><spring:message code='carpet.add.cart' text='Buy' /></a>
		<div class="product-price">
			<span class="currency"><format:price format="currency" priceData="${productItemData.price}" /></span> <format:price format="defaultOnlyNumbers" priceData="${productItemData.price}" />
		</div>
	</div>
</div>
<div class="ft">
	<c:if test="${showOriginalPrice and discount ge 3}">
		<div class="price-wrap wrap-original">
			<div class="product-price">
				<span class="currency"><format:price format="currency" priceData="${originalPrice}" /></span> <format:price format="defaultOnlyNumbers" priceData="${originalPrice}" />
			</div>
		</div>
	</c:if>

</div>
