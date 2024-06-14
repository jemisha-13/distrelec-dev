<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<%-- Used in toolsitem module --%>
<input type="hidden" class="hidden-product-code" value="${itemData.productCode}" />
<%-- End toolsitem module --%>

<c:if test="${not empty itemData.productCode}">
	<nav class="ctrls${not empty itemData.activePromotion ? ' compensate-promo' : ''}">
		<mod:product-tools skin="vertical" productId="${itemData.productCode}" />
	</nav>
</c:if>

<c:set var="linkTarget" value="_self"/>
<c:if test="${itemData.linkTarget == 'NEWWINDOW'}">
	<c:set var="linkTarget" value="_blank"/>
</c:if>

<a href="${itemData.url}" class="teaser-item-url" target="${linkTarget}">

	<c:if test="${not empty itemData.manufacturerImage or not empty itemData.activePromotion}">
		<div class="hd">
			<c:if test="${not empty itemData.manufacturerImage}">
				<mod:product-manufacturer manufacturerLogoUrl="${itemData.manufacturerImage.url}" manufacturerLogoAltText="${itemData.manufacturer}" showLink="false" />
			</c:if>
			<c:if test="${not empty itemData.activePromotion}">
				<div class="promo-label-wrap">
					<mod:product-label promoLabel="${itemData.activePromotion}" />
				</div>
			</c:if>
		</div>
	</c:if>
	<%-- itemData.title is only set within free configurable tiles --%>
	<c:if test="${not empty itemData.title}">
		<div class="hd base ellipsis" title="${itemData.title}">
			${itemData.title}
		</div>
	</c:if>
	<div class="bd">
		<img class="item-image" alt="${itemData.picture.altText == null ? sImageMissing : itemData.picture.altText}" src="${itemData.picture.url eq null ? '/_ui/all/media/img/missing_landscape_large.png' : itemData.picture.url}" />
	</div>
	<div class="ft">
		<h3 class="base item-name ellipsis" title="${itemData.name}">${itemData.name}</h3>
		<c:if test="${not empty itemData.promotionText}">
			<span class="promotion-text ellipsis" title="${itemData.promotionText}">${itemData.promotionText}</span>
		</c:if>
		<c:if test="${not empty itemData.price}">
			<span class="product-price"><format:price format="defaultSplit" priceData="${itemData.price}" /></span>
		</c:if>
	</div>
</a>

