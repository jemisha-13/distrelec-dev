<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<c:set var="productFamilyUrl" value="${product.productFamilyUrl}" />

<spring:message code="product.product-detail.image.missing" text="Image not found" var="sImageMissing"/>

<c:set var="phaseOut" value="${(product.buyable == false) and (empty product.endOfLifeDate)}" />
<c:set var="eol" value="${not empty product.endOfLifeDate}" />
<c:set var="eolWithReplacement" value="${not empty product.endOfLifeDate and product.buyableReplacementProduct}" />

<c:set var="codeErpRelevant" value="${product.codeErpRelevant == undefined ? 'x' : product.codeErpRelevant}" />

<!-- Variable is needed to determine if the product link should be displayed or not -->
<c:if test="${product.buyable or eolWithReplacement}">
	<c:set var="linkOpeningTag">
		<a href="${product.url}">
	</c:set>
	<c:set var="linkClosingTag">
		</a>
	</c:set>
</c:if>

<%-- Used by product and productlist module --%>
<input type="hidden" class="hidden-product-code" value="${product.code}" />
<%-- End product and productlist module --%>

<article class="main" data-product-url="${product.url}">
	<c:if test="${product.buyable and fn:length(product.activePromotionLabels) > 0}">
		<div class="productlabel-wrap">
			<c:forEach items="${product.activePromotionLabels}" var="promoLabel" end="0">
				<mod:product-label promoLabel="${promoLabel}" />
			</c:forEach>
		</div>
	</c:if>
	<c:if test="${product.buyable and not product.catPlusItem}">
		<nav class="ctrls">
			<mod:product-tools productId="${product.code}"/>
		</nav>
	</c:if>
	<c:if test="${product.buyable}">
		<a class="teaser-link" href="${product.url}">
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
		</a>
	</c:if>
	<a class="teaser-link" href="${product.url}">
		<h3 class="title">
			<span class="ellipsis" title="<c:out value="${product.name}" />">${product.name}</span>
		</h3>
	</a>
	<div class="list-attribs loading">
		<c:set var="key" value="1" />
		<c:forEach items="${product.volumePricesMap}" var="entry" end="0">
			<c:set var="key" value="${entry.key}" />
		</c:forEach>

		<div class="table" data-teasertrackingid="${teasertrackingid}"  data-position="${position}">
			<div class="table-row">
				<div class="table-cell first">
					<span class="label"><spring:message code="product.articleNumber" /></span>
					<span class="value"><formatArticle:articleNumber articleNumber="${product.codeErpRelevant}" /></span>
					<c:if test="${not empty productFamilyUrl}">
						<spring:message code="product.family.linkText" var="sProductFamilyLinkText" />
						<span class="value" ><a class="product-family" title="${sProductFamilyLinkText}" href="<c:out value="${productFamilyUrl}" />">${sProductFamilyLinkText}<i></i></a></span>
					</c:if>
				</div>
				<div class="table-cell second">
					<c:if test="${not empty product.technicalAttributes}"><span class="label" title="${product.technicalAttributes[0].key}">${product.technicalAttributes[0].key}</span><span class="value" title="${product.technicalAttributes[0].value}">${product.technicalAttributes[0].value}</span></c:if>
				</div>
				<div class="table-cell third">
					<c:if test="${not empty product.technicalAttributes}"><span class="label" title="${product.technicalAttributes[5].key}">${product.technicalAttributes[5].key}</span><span class="value" title="${product.technicalAttributes[5].value}">${product.technicalAttributes[5].value}</span></c:if>
				</div>
			</div>
			<div class="table-row">
				<div class="table-cell first"><span class="label"><spring:message code="product.typeName" /></span><span class="value">${product.typeName}</span></div>
				<div class="table-cell second">
					<c:if test="${not empty product.technicalAttributes}"><span class="label" title="${product.technicalAttributes[1].key}">${product.technicalAttributes[1].key}</span><span class="value" title="${product.technicalAttributes[1].value}">${product.technicalAttributes[1].value}</span></c:if>
				</div>
				<div class="table-cell third">
					<c:if test="${not empty product.technicalAttributes}"><span class="label" title="${product.technicalAttributes[6].key}">${product.technicalAttributes[6].key}</span><span class="value" title="${product.technicalAttributes[6].value}">${product.technicalAttributes[6].value}</span></c:if>
				</div>
			</div>
			<div class="table-row">
				<div class="table-cell first"><span class="label"><spring:message code="product.manufacturer" /></span><span class="value">${product.distManufacturer.name}</span></div>
				<div class="table-cell second">
					<c:if test="${not empty product.technicalAttributes}"><span class="label" title="${product.technicalAttributes[2].key}">${product.technicalAttributes[2].key}</span><span class="value" title="${product.technicalAttributes[2].value}">${product.technicalAttributes[2].value}</span></c:if>
				</div>
				<div class="table-cell third">
					<c:if test="${not empty product.technicalAttributes}"><span class="label" title="${product.technicalAttributes[7].key}">${product.technicalAttributes[7].key}</span><span class="value" title="${product.technicalAttributes[7].value}">${product.technicalAttributes[7].value}</span></c:if>
				</div>
			</div>
			<div class="table-row">
				<div class="table-cell first"><%-- price comes from absolute positioned module scaled-prices --%></div>
				<div class="table-cell second">
					<c:if test="${not empty product.technicalAttributes}"><span class="label" title="${product.technicalAttributes[3].key}">${product.technicalAttributes[3].key}</span><span class="value" title="${product.technicalAttributes[3].value}">${product.technicalAttributes[3].value}</span></c:if>
				</div>
				<div class="table-cell third">
					<c:if test="${not empty product.technicalAttributes}"><span class="label" title="${product.technicalAttributes[8].key}">${product.technicalAttributes[8].key}</span><span class="value" title="${product.technicalAttributes[8].value}">${product.technicalAttributes[8].value}</span></c:if>
				</div>
			</div>
			<div class="table-row last">
				<div class="table-cell first">
					<span class="load-in-process label"><spring:message code="product.shipping.loading.availability" /></span>
					<span class="load-complete available-text label"><spring:message code="product.available" /></span> <ul class="available-bar small"></ul>
				</div>
				<div class="table-cell second">
					<c:if test="${not empty product.technicalAttributes}"><span class="label" title="${product.technicalAttributes[4].key}">${product.technicalAttributes[4].key}</span><span class="value" title="${product.technicalAttributes[4].value}">${product.technicalAttributes[4].value}</span></c:if>
				</div>
				<div class="table-cell third">
					<c:if test="${not empty product.technicalAttributes}"><span class="label" title="${product.technicalAttributes[9].key}">${product.technicalAttributes[9].key}</span><span class="value" title="${product.technicalAttributes[9].value}">${product.technicalAttributes[9].value}</span></c:if>
				</div>
			</div>
		</div>
		<mod:scaled-prices product="${product}" template="product-list-technical" skin="product-list-technical" />

	</div>
	<c:if test="${eol}">
		<div class="eol${eolWithReplacement ? ' hasReplacement' : ''}">
			<c:set var="formattedDate"> 
				<fmt:formatDate value="${product.endOfLifeDate}"/>
			</c:set>
			<p class="eol-message">
				<spring:message code="product.notBuyable.endOfLife.message" arguments="${formattedDate}"/>
				<c:if test="${not empty product.replacementReason }">
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
