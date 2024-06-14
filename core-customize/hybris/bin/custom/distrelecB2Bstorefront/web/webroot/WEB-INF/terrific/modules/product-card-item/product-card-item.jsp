<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<spring:message code="product.carousel-teaser.image.missing" text="Image not found" var="sImageMissing"/>

<a class="card-item-anchor" title="${itemData.name}" href="${itemData.url}" data-aainteraction="product click" data-stock="0" data-section-title="${cardTitle}" data-link-type="component" data-location="pdp" data-position="${position}" data-product-id="${itemData.code}">
    <div class="card-item">
        <div class="card-item__image">
            <div class="promo-holder">
            </div>
            <c:set var="productImage" value="${itemData.productImages[0]}" />
            <c:set var="landscapeSmallJpg" value="${not empty productImage.landscape_small.url ? productImage.landscape_small.url : '/_ui/all/media/img/missing_landscape_small.png' }"/>
            <c:set var="landscapeSmallWebP" value="${not empty productImage.landscape_small_webp.url ? productImage.landscape_small_webp.url : landscapeSmallJpg}"/>
            <c:set var="landscapeSmallAlt" value="${not empty productImage.landscape_small_webp.altText ? productImage.landscape_small_webp.altText : not empty productImage.landscape_small.altText == null ? productImage.landscape_small.altText : sImageMissing }"/>
            <picture>
                <source srcset="${landscapeSmallWebP}">
                <img width="61" height="46" class="item-image" alt="${landscapeSmallAlt}" src="${landscapeSmallJpg}"/>
            </picture>
        </div>
        <div class="card-item__content">
            <h3>${itemData.name}</h3>
            <div id="wrapper3" class="stock-${itemData.code}">
                <span class="hidden stock-id">${itemData.code}</span>
                <p><spring:message code='product.stock' text='Stock' /> <span id="rightColumn2" class="holder-s"></span></p>
            </div>
            <p class="price">
                <format:price format="defaultSplit" priceData="${itemData.price}" />
            </p>
        </div>
    </div>
</a>
