<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="manufacturer.carousel-teaser.image.missing" text="Image not found" var="sImageMissing"/>


<a class="thumb-link" href="${itemData.url}">
    <div class="thumb-row">
        <div class="thumb">
            <c:set var="productImage" value="${itemData.image}"/>
            <c:set var="portraitMediumJpg" value="${not empty productImage.portrait_medium.url ? productImage.portrait_medium.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
            <c:set var="portraitMediumWebP" value="${not empty productImage.portrait_medium_webp.url ? productImage.portrait_medium_webp.url : portraitMediumJpg}"/>
            <picture>
                <source srcset="${portraitMediumWebP}">
                <img alt="${itemData.image.altText == null ? sImageMissing : itemData.image.altText}" src="${portraitMediumJpg}"/>
            </picture>
            <i></i>
        </div>
    </div>
    <div class="thumb-row">
        <div class="thumb-title">
            <span>${itemData.name}</span>
        </div>
    </div>
</a>