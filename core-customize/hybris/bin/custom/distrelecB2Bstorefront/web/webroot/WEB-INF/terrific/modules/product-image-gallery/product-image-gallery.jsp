<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code="product.image.missing" text="Image not found" var="sImageMissing"/>
<spring:message code="product.image.noImage" text="This product has no images" var="sImageNoImages"/>
<spring:message code="product.gallery.magic360.loading" text="Loading..." var="sMagic360LoadingMessage"/>
<spring:message code="product.gallery.magic360.hintText" text="Drag to Spin" var="sMagic360DragToSpinMessage"/>
<spring:message code="product.gallery.magic360.mobileHintText" text="Swipe to Spin" var="sMagic360SwipeToSpinMessage"/>
<spring:message code="product.gallery.image.illustrative" text="Illustrative image" var="sImageIllustrativeContent" />
<spring:message code="product.gallery.image.illustrativeTitle" text="Illustrative image title" var="sImageIllustrativeTitle" />

<c:set var="imageLength" value="${fn:length(product.productImages) + fn:length(product.videos) + fn:length(product.images360)}"/>

<spring:theme code="toolsitem.share" var="sToolsitemShare" />

<c:set var="statusCode" value="${product.salesStatus}" />

<%-- twitter / gplus is not available for oci / ariba as of per ticket DISTRELEC-5540 --%>
<sec:authorize access="!hasRole('ROLE_OCICUSTOMERGROUP') and !hasRole('ROLE_ARIBACUSTOMERGROUP') and !hasRole('ROLE_CXMLCUSTOMERGROUP')">
	<c:set var="enableSharingFunctions" value="true" />
</sec:authorize>

<c:set var="isEProcurement" value="false"/>
<sec:authorize access="hasAnyRole('ROLE_EPROCUREMENTGROUP','ROLE_OCICUSTOMERGROUP','ROLE_ARIBACUSTOMERGROUP','ROLE_CXMLCUSTOMERGROUP')">
	<c:set var="isEProcurement" value="true"/>
</sec:authorize>

<div class="promo-label-illustrative-image-wrap">
	<c:set var="statusCheck" value="${statusCode != 40 && statusCode != 41 && statusCode != 60 && statusCode != 61 && statusCode != 62 }"/>
	<c:if test="${statusCheck}">
	<c:forEach items="${product.activePromotionLabels}" var="promoLabel" >
		<mod:product-label skin="gallery" promoLabel="${promoLabel}" />
	</c:forEach>
	</c:if>
	<c:if test="${not empty product.energyEfficiency}">
		<mod:energy-efficiency-label skin="single" product="${product}" />
	</c:if>
</div>

<div class="message-container-gallery" data-i18n-m360-loading="${sMagic360LoadingMessage}"
	 data-i18n-m360-hint-text="${sMagic360DragToSpinMessage}"
	 data-i18n-m360-mobile-hint-text="${sMagic360SwipeToSpinMessage}">
</div>

<c:if test="${imageLength > 1}">
	<c:set var="imageFull" value="imagery-holder--full"/>
</c:if>

<div class="zoom-gallery">
	<div class="imagery-holder ${imageFull}">
		<%-- note: if there exists a 'primary' image (defined in CMS), BE renders it as first in collection, make sure javascript uses this also as main gallery image! --%>

		<!-- 9.2 Rotating Teaser Tracking (Javascript) !-->
		<c:set var="codeErpRelevant" value="${product.codeErpRelevant == undefined ? 'x' : product.codeErpRelevant}" />
		<c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}.-" />

		<!-- Primary Image, Reorder of the images: 	DISTRELEC-9500 Don't show 360� product presentation as default -->
		<c:forEach items="${product.productImages}" var="productImage" varStatus="status" begin="0" end="0">
			<c:set var="imageIndex" value="${status.index}" />
			<div data-slide-id="zoom-item" class="zoom-gallery-slide active">
				<%@ include file="/WEB-INF/terrific/modules/product-image-gallery/gallery-item.jsp" %>
			</div>
		</c:forEach>

		<!-- 360� Images -->
		<c:set var="primaryImageOffset" value="1" />
		<c:forEach items="${product.images360}" var="productImage360" varStatus="status">
			<c:set var="imageIndex" value="${status.index}" />
			<%@ include file="/WEB-INF/terrific/modules/product-image-gallery/gallery-item-360.jsp" %>
		</c:forEach>

		<!-- videos -->
		<c:set var="imagesOffset" value="${fn:length(product.productImages)}" />
		<c:set var="imageOffset360" value="${fn:length(product.images360)}" />
		<c:forEach items="${product.videos}" var="productVideo" varStatus="status">
			<c:set var="imageIndex" value="${imagesOffset + imageOffset360 + status.index}" />
			<div data-slide-id="video-${status.index + 1}" class="gallery-item zoom-gallery-slide video-slide" data-position="${imageIndex + 1}" data-teasertrackingid="${teasertrackingid}">
				<mod:video videoId="${productVideo.youtubeUrl}" />
			</div>
		</c:forEach>

		<c:if test="${fn:length(product.productImages) == 0 and fn:length(product.videos) == 0 and fn:length(product.images360) == 0}">
			<div class="gallery-item">

				<span>
					<c:choose>
						<c:when test="${currentCountry.isocode eq 'DK' || currentCountry.isocode eq 'FI' || currentCountry.isocode eq 'NO' || currentCountry.isocode eq 'SE'
									 || currentCountry.isocode eq 'LT' || currentCountry.isocode eq 'LV' || currentCountry.isocode eq 'EE' || currentCountry.isocode eq 'NL'
									 || currentCountry.isocode eq 'PL'}" >
							<img alt="${sImageNoImages}" title="${sImageNoImages}" src="/_ui/all/media/img/missing_landscape_medium-elfa.png" />
						</c:when>
						<c:when test="${currentCountry.isocode eq 'FR'}" >
							<img alt="${sImageNoImages}" title="${sImageNoImages}" src="/_ui/all/media/img/distrelec_logo_v2_medium.png" />
						</c:when>
						<c:otherwise>
							<img alt="${sImageNoImages}" title="${sImageNoImages}" src="/_ui/all/media/img/missing_landscape_medium.png" />
						</c:otherwise>
					</c:choose>
				</span>
			</div>
		</c:if>
	</div>

	<c:if test="${imageLength > 1}">
		<div class="gallery-side MagicScroll" id="gal1" data-options="orientation: vertical">

			<!-- First image-->
			<c:forEach items="${product.productImages}" var="productImage" varStatus="status" begin="0" end="0">
				<c:set var="imageIndex" value="${status.index}" />
				<c:set var="landscapeLargeJpg" value="${not empty productImage.landscape_large.url ? productImage.landscape_large.url : '/_ui/all/media/img/missing_landscape_medium.png'}" />
				<c:set var="landscapeLarge" value="${not empty productImage.landscape_large_webp.url ? productImage.landscape_large_webp.url : not empty productImage.landscape_large.url ? productImage.landscape_large.url : '/_ui/all/media/img/missing_landscape_medium.png'}" />
				<c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ?  productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png'}" />
				<c:set var="portraitSmall" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : not empty productImage.portrait_small.url ?  productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png'}" />
				<c:set var="portraitSmallAltText" value="${not empty productImage.portrait_small_webp.altText ? productImage.portrait_small_webp.altText : not empty productImage.portrait_small.altText ?  productImage.portrait_small.altText : sImageMissing}" />
				<a data-slide-id="zoom-item" data-zoom-id="zoom-item-v" href="${landscapeLargeJpg}" class="item active current${status.index eq 0 and product.illustrativeImage ? ' illustrative-image' : ''}" data-image="${landscapeLarge}" data-index="${imageIndex}">
					<img width="51" height="68" alt="${portraitSmallAltText}" src="${portraitSmallJpg}">
				</a>
			</c:forEach>

			<!-- image 360 -->
			<c:set var="primaryImageOffset" value="1" />
			<c:forEach items="${product.images360}" var="productImage360" varStatus="status">
				<c:set var="imageIndex" value="${primaryImageOffset + status.index}" />
				<a data-slide-id="magic360" href="#" class="item magic-360" data-view="spin" data-index="${imageIndex}">
					<i></i>
				</a>
			</c:forEach>

			<!-- Remaining images -->
			<c:set var="imageOffset360" value="${fn:length(product.images360)}" />
			<c:forEach items="${product.productImages}" var="productImage" varStatus="status" begin="1" end="${fn:length(product.productImages)}">
				<c:set var="imageIndex" value="${imageOffset360 + status.index}" />
				<c:set var="landscapeLargeJpg" value="${not empty productImage.landscape_large.url ? productImage.landscape_large.url : '/_ui/all/media/img/missing_landscape_medium.png'}" />
				<c:set var="landscapeLarge" value="${not empty productImage.landscape_large_webp.url ? productImage.landscape_large_webp.url : not empty productImage.landscape_large.url ? productImage.landscape_large.url : '/_ui/all/media/img/missing_landscape_medium.png'}" />
				<c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ?  productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png'}" />
				<c:set var="portraitSmall" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : not empty productImage.portrait_small.url ?  productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png'}" />
				<c:set var="portraitSmallAltText" value="${not empty productImage.portrait_small_webp.altText ? productImage.portrait_small_webp.altText : not empty productImage.portrait_small.altText ?  productImage.portrait_small.altText : sImageMissing}" />
				<a data-slide-id="zoom-item" data-zoom-id="zoom-item-v" href="${landscapeLarge}" class="item" data-index="${imageIndex}" data-image="${landscapeLargeJpg}">
					<img width="51" height="68" alt="${portraitSmallAltText}" src="${portraitSmallJpg}">
				</a>
			</c:forEach>

			<!-- videos -->
			<c:set var="imagesOffset" value="${fn:length(product.productImages)}" />
			<c:set var="imageOffset360" value="${fn:length(product.images360)}" />
			<c:forEach items="${product.videos}" var="productVideo" varStatus="status">
				<c:set var="imageIndex" value="${imagesOffset + imageOffset360 + status.index}" />
				<a data-slide-id="video-${status.index + 1}" href="#" class="item video" data-index="${imageIndex}">
					<i></i>
				</a>
			</c:forEach>

		</div>
	</c:if>
</div>
<div class="image-reference">
	<p>${sImageIllustrativeTitle}</p>
</div>
