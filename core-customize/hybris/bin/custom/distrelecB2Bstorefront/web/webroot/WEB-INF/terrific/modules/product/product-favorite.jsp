<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<spring:message code="shoppinglist.favorite.image.missing" text="Image not found" var="sImageMissing"/>

<c:set var="phaseOut" value="${(product.buyable == false) and (empty product.endOfLifeDate)}" />
<c:set var="eol" value="${not empty product.endOfLifeDate}" />
<c:set var="eolWithReplacement" value="${not empty product.endOfLifeDate and product.buyableReplacementProduct}" />

<%-- Used by product and productlist module --%>
<input type="hidden" class="hidden-product-code" value="${product.code}" />
<%-- End product and productlist module --%>

<%-- Variable is needed to determine if the product link should be displayed or not --%>
<c:if test="${product.buyable or eolWithReplacement}">
	<c:set var="linkOpeningTag">
		<a href="${product.url}">
	</c:set>
	<c:set var="linkClosingTag">
		</a>
	</c:set>
</c:if>

<article class="main" data-product-url="${product.url}">
	<c:if test="${product.buyable and fn:length(product.activePromotionLabels) > 0}">
		<div class="productlabel-wrap">
			<c:forEach items="${product.activePromotionLabels}" var="promoLabel" end="0">
				<mod:product-label promoLabel="${promoLabel}" />
			</c:forEach>
		</div>
	</c:if>
	<c:choose>
		<c:when test="${product.buyable and not product.catPlusItem}">
			<nav class="ctrls">
				<mod:product-tools template="favorite" productId="${product.code}"/>
			</nav>
		</c:when>
		<c:otherwise>
			<c:if test="${not product.catPlusItem}">
				<nav class="ctrls">
					<mod:product-tools template="favorite-eol" productId="${product.code}"/>
				</nav>
			</c:if>
		</c:otherwise>
	</c:choose>
	${linkOpeningTag}
		<h3 class="title">
			<div class="ellipsis" title="<c:out value="${product.name}" />">${product.name}</div>
		</h3>
	${linkClosingTag}
	<c:if test="${product.buyable}">
		${linkOpeningTag}
			<mod:energy-efficiency-label skin="favorite" product="${product}" />
			<div class="image-container">
				<div class="image-wrapper">
					<c:set var="productImage" value="${product.productImages[0]}"/>
					<c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ? productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
					<c:set var="portraitSmallWebP" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : portraitSmallJpg}"/>
					<c:set var="portraitSmallAlt" value="${not empty productImage.portrait_small_webp.altText ? productImage.portrait_small_webp.altText : not empty productImage.portrait_small.altText == null ? productImage.portrait_small.altText : sImageMissing }"/>
					<picture>
						<source srcset="${portraitSmallWebP}">
						<img width="83" height="110" alt="${portraitSmallAlt}" src="${portraitSmallJpg}"/>
					</picture>
				</div>
			</div>
		${linkClosingTag}
	</c:if>
	<div class="list-attribs loading">
		<c:set var="key" value="1" />
		<c:forEach items="${product.volumePricesMap}" var="entry" end="0">
			<c:set var="key" value="${entry.key}" />
		</c:forEach>

		<div class="table">
			<div class="table-row">
				<div class="table-cell first"><span class="label"><spring:message code="product.articleNumber" /></span><span class="value"><formatArticle:articleNumber articleNumber="${product.codeErpRelevant}" /></span></div>
				<div class="table-cell second">
					&nbsp;
				</div>
			</div>
			<div class="table-row">
				<div class="table-cell first"><span class="label"><spring:message code="product.typeName" /></span><span class="value">${product.typeName}</span></div>
				<div class="table-cell second"><span class="label"><spring:message code="product.stock" /></span><span class="value"><span class="product-stock"></span></span></div>
			</div>
			<div class="table-row last">
				<div class="table-cell first"><span class="label"><spring:message code="product.manufacturer" /></span><span class="value">${product.distManufacturer.name}</span></div>
				<div class="table-cell second"><span class="label"><spring:message code="product.packing.unit" /></span><span class="value">${product.salesUnit}</span></div>
			</div>
		</div>
		<mod:scaled-prices product="${product}" template="product-list" skin="product-list" />
	</div>
		
	<c:if test="${eol}">
		<div class="eol${eolWithReplacement ? ' hasReplacement' : ''}">
			<c:set var="formattedDate"> 
				<fmt:formatDate value="${product.endOfLifeDate}"/>
			</c:set>
			<p class="eol-message">
				<spring:message code="product.notBuyable.endOfLife.message" arguments="${formattedDate}"/>
				<c:if test="${not empty product.replacementReason}">
					<span class="value">
						<spring:message code="product.notBuyable.endOfLife.reason" arguments="${product.replacementReason}"/>
					</span>
				</c:if>
			</p>
			<a class="btn-secondary" href="${product.url}"><spring:message code="product.notBuyable.endOfLife.alternative"/></a>
		</div>
	</c:if>
	<c:if test="${phaseOut}">
		<div class="phase-out">
			<p class="phase-out-message"><spring:message code="product.notBuyable.temporarly.message"/></p>
		</div>
	</c:if>
</article>
