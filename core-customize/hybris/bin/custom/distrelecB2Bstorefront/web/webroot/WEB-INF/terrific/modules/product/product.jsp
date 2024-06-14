<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<c:set var="productFamilyUrl" value="${product.productFamilyUrl}" />

<spring:message code="product.image.missing" text="Image not found"	var="sImageMissing" />

<c:set var="phaseOut" value="${(product.buyable == false) and (empty product.endOfLifeDate)}" />
<c:set var="eol" value="${not empty product.endOfLifeDate}" />
<c:set var="eolWithReplacement"	value="${not empty product.endOfLifeDate and product.buyableReplacementProduct}" />

<%-- Used by product and productlist module --%>
<input type="hidden" class="hidden-product-code" value="${product.code}" />
<%-- End product and productlist module --%>

<c:set var="codeErpRelevant" value="${product.codeErpRelevant == undefined ? 'x' : product.codeErpRelevant}" />

<%-- Variables are needed to determine if the product link should be displayed or not --%>
<c:if test="${product.buyable or eolWithReplacement}">
	<c:choose>
		<c:when	test="${product.buyable and fn:length(product.activePromotionLabels) gt 0}">
			<c:set var="promotionLabelsPresences" value="true" />
			<c:forEach items="${product.activePromotionLabels}"	var="activePromoLabel" end="0">
				<c:set var="promoLabel" value="${activePromoLabel}" />
				<c:set var="label" value="${activePromoLabel.code}" />
				<c:choose>
					<c:when test="${label eq 'noMover' }">
						<c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}-cre.-" />
					</c:when>
					<c:when test="${label eq 'new' }">
						<c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}-new.-" />
					</c:when>
					<c:otherwise>
						<c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}.-" />
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<c:set var="teasertrackingid" value="${wtTeaserTrackingId}.${codeErpRelevant}.-" />
		</c:otherwise>
	</c:choose>
</c:if>

<article class="main" data-product-url="${product.url}">
	<c:if test="${promotionLabelsPresences eq 'true'}">
		<div class="productlabel-wrap">
			<c:forEach items="${product.activePromotionLabels}" var="promoLabel" end="0">
				<mod:product-label promoLabel="${promoLabel}" />
			</c:forEach>
		</div>
	</c:if>
	<c:if test="${not empty product.energyEfficiency}">
       <mod:energy-efficiency-label skin="product" product="${product}" />
	</c:if>
	<c:if test="${product.buyable and not product.catPlusItem}">
		<nav class="ctrls">
			<mod:product-tools productId="${product.code}" />
		</nav>
	</c:if>
	<c:if test="${product.buyable}">
		<a href="${product.url}" class="teaser-link" data-teasertrackingid="${teasertrackingid}" data-position="${position}">
			<div class="image-container">
				<div class="image-wrapper">
					<c:set var="productImage" value="${product.productImages[0]}"/>
					<c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ? productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
					<c:set var="portraitSmallWebP" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : portraitSmallJpg}"/>
					<picture>
						<source srcset="${portraitSmallWebP}">
						<img width="83" height="110" alt="${product.name}<c:out value=' '/><spring:message code='product.page.title.buy' text='Buy' />" src="${portraitSmallJpg}"/>
					</picture>
				</div>
			</div>
		</a>
	</c:if>
	<a href="${product.url}" class="title-link" data-teasertrackingid="${teasertrackingid}" data-position="${position}">
		<h3 class="title">
			<span class="ellipsis" title="<c:out value="${product.name}" />">${product.name}</span>
		</h3>
	</a>

	<div class="list-attribs loading">
		<c:set var="key" value="1" />
		<c:forEach items="${product.volumePricesMap}" var="entry" end="0">
			<c:set var="key" value="${entry.key}" />
		</c:forEach>

		<div class="table" data-teasertrackingid="${teasertrackingid}" data-position="${position}">
			<div class="table-row">
				<div class="table-cell first">
					<span class="label"><spring:message	code="product.articleNumber" /></span> <span class="value">   <formatArticle:articleNumber articleNumber="${product.codeErpRelevant}"  />   </span>
					<c:if test="${not empty productFamilyUrl}">
						<spring:message code="product.family.linkTextExtended" var="sProductFamilyLinkText" />
						<span class="value"><a class="product-family" title="${sProductFamilyLinkText}" href="<c:out value="${productFamilyUrl}" />">${sProductFamilyLinkText}<i></i></a></span>
					</c:if>
				</div>
				<div class="table-cell second">
				
					<c:choose>
						<c:when test="${!product.catPlusItem}">
						<%-- DISTRELEC-8425 --%>
						<%--
							<span class="load-in-process label"><spring:message	code="product.shipping.loading.availability" /></span> <span class="load-complete available-text label"><spring:message	code="product.available" /></span>
							<ul class="available-bar small"></ul>
						 --%>
						</c:when>
						<c:otherwise>
							<span class="availabilityCatPlus">
								<spring:message code="product.shipping.availability" />:&nbsp;
								<span class="availabilityCatPlusText">
									<spring:message code="product.shipping.catplus.availability" />	
								</span>
							</span>					
						</c:otherwise>						
					</c:choose>
					
				</div>
			</div>
			<div class="table-row">
				<div class="table-cell first"><span class="label"><spring:message code="product.typeName" /></span><span class="value type-name">${product.typeName}</span></div>
				<div class="table-cell second"><span class="label"><spring:message code="product.stock" /></span><span	class="value"><span class="product-stock"></span></span></div>
			</div>
			<div class="table-row last">
				<div class="table-cell first"><span class="label"><spring:message	code="product.manufacturer" /></span><span class="value">${product.distManufacturer.name}</span></div>
				<div class="table-cell second"><span class="label"><spring:message	code="product.packing.unit" /></span><span class="value">${product.salesUnit}</span></div>
			</div>
		</div>
		<mod:scaled-prices product="${product}" template="product-list"	skin="product-list" />
	</div>
	<c:if test="${eol}">
		<div class="eol${eolWithReplacement ? ' hasReplacement' : ''}">
			<c:set var="formattedDate">
				<fmt:formatDate value="${product.endOfLifeDate}" />
			</c:set>
			<p class="eol-message">
				<spring:message code="product.notBuyable.endOfLife.message" arguments="${formattedDate}" />
				<c:if test="${not empty product.replacementReason}">
					<span class="value">
						<spring:message code="product.notBuyable.endOfLife.reason" arguments="${product.replacementReason}" />
					</span>
				</c:if>
			</p>
			<a class="btn-secondary" href="${product.url}"><spring:message code="product.notBuyable.endOfLife.alternative" /></a>
		</div>
	</c:if>
	<c:if test="${phaseOut}">
		<div class="phase-out">
			<p class="phase-out-message"><spring:message code="product.notBuyable.temporarly.message" /></p>
		</div>
	</c:if>
</article>
