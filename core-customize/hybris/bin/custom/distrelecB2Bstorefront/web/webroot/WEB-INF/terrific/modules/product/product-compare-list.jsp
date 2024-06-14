<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<spring:message code="product.product-compare.image.missing" text="Image not found" var="sImageMissing"/>
<spring:message code="product.articleNumber" var="sProductArticleNumber"/>
<spring:message code="cart.list.typeName" var="sProductTypeName"/>

<mod:toolsitem template="toolsitem-compare-remove" skin="compare-remove" tag="div" productId="${product.code}" />
<mod:product-manufacturer skin="compare" distManufacturer="${product.distManufacturer}" showLink="false" />

<input type="hidden" class="hidden-product-code" value="${product.code}" />
<a href="${product.url}" class="teaser-link tableGrid__product">
	<%-- img --%>
	<div class="tableGrid__product__image" title="${product.name}">
		<c:set var="productImage" value="${product.productImages[0]}"/>
		<c:set var="landscapeSmallJpg" value="${not empty productImage.landscape_small.url ? productImage.landscape_small.url : '/_ui/all/media/img/missing_landscape_small.png' }"/>
		<c:set var="landscapeSmallWebP" value="${not empty productImage.landscape_small_webp.url ? productImage.landscape_small_webp.url : landscapeSmallJpg}"/>
		<c:set var="landscapeSmallAlt" value="${not empty productImage.landscape_small_webp.altText ? productImage.landscape_small_webp.altText : not empty productImage.landscape_small.altText == null ? productImage.landscape_small.altText : sImageMissing }"/>
		<picture>
			<source srcset="${landscapeSmallWebP}">
			<img width="118" height="98" alt="${landscapeSmallAlt}"
			src="${landscapeSmallJpg}"/>
		</picture>
	</div>

	<%-- name & price --%>
	<div class="tableGrid__product__title" title="${product.name}">
		<h3 class="title">${product.name}</h3>
	</div>

	<div class="tableGrid__product__info">
        <ul class="tableGrid__product__info__items" title="<formatArticle:articleNumber articleNumber="${product.codeErpRelevant}" />">
            <li class="tableGrid__product__info__items__item">${sProductArticleNumber}</li>
            <li class="tableGrid__product__info__items__item"><formatArticle:articleNumber articleNumber="${product.codeErpRelevant}" /></li>
        </ul>
        <ul class="tableGrid__product__info__items" title="${product.typeName}">
            <li class="tableGrid__product__info__items__item ellipsis">${sProductTypeName}</li>
            <li class="tableGrid__product__info__items__item tableGrid__product__info__items__item--typename ellipsis">${product.typeName}</li>
        </ul>
	</div>

	<div class="tableGrid__product__tools">
		<mod:product-tools template="compare-list" productId="${product.code}"/>
	</div>
</a>

<mod:energy-efficiency-label skin="compare" product="${product}" />
