<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="namicscommerce"
	uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<c:set var="productFamilyUrl" value="${product.productFamilyUrl}" />
<c:set var="phaseOut" value="${(product.buyable == false) and (empty product.endOfLifeDate)}" />
<c:set var="eol" value="${not empty product.endOfLifeDate}" />
<c:set var="eolWithReplacement" value="${not empty product.endOfLifeDate and product.buyableReplacementProduct}" />
<c:set var="productFamily" value="false" />
<c:choose>
    <c:when test="${not empty param['filter_productFamilyCode']}">
        <c:set var="productFamily" value="true" />
    </c:when>
    <c:when test="${not empty param['filter_CuratedProducts']}">
        <c:set var="productFamily" value="true" />
    </c:when>
    <c:otherwise>
        <c:set var="productFamily" value="false" />
    </c:otherwise>
</c:choose>
<c:set var="requestPath" value="${requestScope['javax.servlet.forward.request_uri']}"/>
<c:if test = "${productFamily =='false' && fn:contains(requestPath, '/new')}">
	<c:set var="productFamily" value="true" />
</c:if>
<c:if test = "${productFamily =='false' && fn:contains(requestPath, '/clearance')}">
	<c:set var="productFamily" value="true" />
</c:if>
<c:if test = "${productFamily =='false' && fn:contains(requestPath, '/manufacturer/')}">
	<c:set var="productFamily" value="true" />
</c:if>

<spring:message code="product.image.missing" text="Image not found" var="sImageMissing" />


<%-- Used by product and productlist module --%>
<input type="hidden" class="hidden-product-code" value="${product.code}" />
<input type="hidden" class="hidden-price" value="${product.price.value}" />
<%-- End product and productlist module --%>

<%-- Variables are needed to determine if the product link should be displayed or not --%>
<c:if test="${product.buyable or eolWithReplacement}">
	<c:forEach items="${product.activePromotionLabels}" var="activePromoLabel" end="0">
		<c:set var="promoLabel" value="${activePromoLabel}" />
		<c:set var="label" value="${activePromoLabel.code}" />
	</c:forEach>
</c:if>

<article class="main" data-product-url="${product.url}">
	<div class="main__image">
		<c:if test="${product.buyable and fn:length(product.activePromotionLabels) > 0}">
			<div class="productlabel-wrap">
				<c:forEach items="${product.activePromotionLabels}" var="promoLabel" end="0">
					<mod:product-label promoLabel="${promoLabel}" />
				</c:forEach>
			</div>
		</c:if>
		<c:if test="${not empty product.energyEfficiency}">
			<mod:energy-efficiency-label skin="product" product="${product}" />
		</c:if>
		<c:if test="${product.buyable}">
			<c:set var="productsPerPage" value="10" />
        	<c:forEach items="${searchPageData.pagination.productsPerPageOptions}" var="productsPerPageOption">
        		<c:if test="${productsPerPageOption.selected}">
					<c:set var="productsPerPage" value="${productsPerPageOption.value}" />
				</c:if>
			</c:forEach>
			<c:set var="currentPage" value="${searchPageData.pagination.currentPage-1}" />
			<c:set var="productPostion" value="${((searchPageData.pagination.currentPage-1)* productsPerPage)+position}" />
			<c:url value="${product.url}" var="productTrackUrl">
        	 	 <c:param name="pos" value="${productPostion}"/>
        	 	 <c:param name="origPos" value="${product.origPosition}"/>
	        	 <c:param name="origPageSize" value="${productsPerPage}"/>
				 <c:param name="prodprice" value="${product.price.value}"/>
				 <c:param name="q" value="${param['q']}"/>
				  <c:param name="p" value="${product.categoryCodePath}"/>
				 <c:param name="isProductFamily" value="${productFamily}"/>
				  <c:param name="campaign" value="${product.campaign}"/>
				 
			</c:url>
					<a href="${productTrackUrl}" class="teaser-link" data-position="${position}">
				<div class="image-container">
					<div class="image-wrapper">
						<c:set var="productImage" value="${product.productImages[0]}"/>
						<c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ? productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
						<c:set var="portraitSmallWebP" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : portraitSmallJpg}"/>
						<picture>
							<source srcset="${portraitSmallWebP}">
							<img width="83" height="110"
							alt="${product.name}<c:out value=' '/><spring:message code='product.page.title.buy' text='Buy' />"
							src="${portraitSmallJpg}"/>
						</picture>
					</div>
				</div>
			</a>
		</c:if>
		<c:if test="${product.buyable and not product.catPlusItem}">
			<nav class="ctrls">
				<mod:product-tools template="technical" skin="technical" productId="${product.code}" />
			</nav>
		</c:if>
	</div>
	<div class="main__content">
		<div class="title-holder">
			<a class="title-link" href="${productTrackUrl}" data-position="${position}">
				<h3 class="title">
					<span class="ellipsis" title="<c:out value="${product.name}" />">${product.name}</span>
				</h3>
			</a>
		</div>
		<div class="list-attribs loading pricessssss">
			<c:set var="key" value="1" />
			<c:forEach items="${product.volumePricesMap}" var="entry" end="0">
				<c:set var="key" value="${entry.key}" />
			</c:forEach>

			<div class="table" data-position="${position}">
				<div class="table-row">
					<div class="table-cell first">
					<span class="label"><spring:message
							code="product.articleNumber" /></span> <span class="value">  <formatArticle:articleNumber articleNumber="${product.codeErpRelevant}"  />  </span>
					</div>
					<div class="table-cell second">
						<c:if test="${not empty product.technicalAttributes}">
							<span class="label" title="${product.technicalAttributes[0].key}">${product.technicalAttributes[0].key}</span>
							<span class="value"
								  title="${product.technicalAttributes[0].value}">${product.technicalAttributes[0].value}</span>
						</c:if>
					</div>
					<div class="table-cell third">
						<c:if test="${not empty product.technicalAttributes}">
							<span class="label" title="${product.technicalAttributes[5].key}">${product.technicalAttributes[5].key}</span>
							<span class="value"
								  title="${product.technicalAttributes[5].value}">${product.technicalAttributes[5].value}</span>
						</c:if>
					</div>
				</div>
				<div class="table-row">
					<div class="table-cell first">
						<span class="label"><spring:message code="product.typeName" /></span><span
							class="value type-name">${product.typeName}</span>
					</div>
					<div class="table-cell second">
						<c:if test="${not empty product.technicalAttributes}">
							<span class="label" title="${product.technicalAttributes[1].key}">${product.technicalAttributes[1].key}</span>
							<span class="value"
								  title="${product.technicalAttributes[1].value}">${product.technicalAttributes[1].value}</span>
						</c:if>
					</div>
					<div class="table-cell third">
						<c:if test="${not empty product.technicalAttributes}">
							<span class="label" title="${product.technicalAttributes[6].key}">${product.technicalAttributes[6].key}</span>
							<span class="value"
								  title="${product.technicalAttributes[6].value}">${product.technicalAttributes[6].value}</span>
						</c:if>
					</div>
				</div>
				<div class="table-row">
					<div class="table-cell first">
					<span class="label"><spring:message
							code="product.manufacturer" /></span><span class="value manufacturer">${product.distManufacturer.name}</span>
					</div>
					<div class="table-cell second">
						<c:if test="${not empty product.technicalAttributes}">
							<span class="label" title="${product.technicalAttributes[2].key}">${product.technicalAttributes[2].key}</span>
							<span class="value"
								  title="${product.technicalAttributes[2].value}">${product.technicalAttributes[2].value}</span>
						</c:if>
					</div>
					<div class="table-cell third">
						<c:if test="${not empty product.technicalAttributes}">
							<span class="label" title="${product.technicalAttributes[7].key}">${product.technicalAttributes[7].key}</span>
							<span class="value"
								  title="${product.technicalAttributes[7].value}">${product.technicalAttributes[7].value}</span>
						</c:if>
					</div>
				</div>
				<div class="table-row">
					<div class="table-cell first">
						<c:if test="${not empty productFamilyUrl}">
							<spring:message code="product.family.linkText" var="sProductFamilyLinkTechnicalText" />
							<span class="value-family">
								<a class="product-family" title="${sProductFamilyLinkTechnicalText}" href="<c:out value="${productFamilyUrl}" />">${sProductFamilyLinkTechnicalText}</a>
							</span>
						</c:if>
					</div>
					<div class="table-cell second">
						<c:if test="${not empty product.technicalAttributes}">
							<span class="label" title="${product.technicalAttributes[3].key}">${product.technicalAttributes[3].key}</span>
							<span class="value"
								  title="${product.technicalAttributes[3].value}">${product.technicalAttributes[3].value}</span>
						</c:if>
					</div>
					<div class="table-cell third">
						<c:if test="${not empty product.technicalAttributes}">
							<span class="label" title="${product.technicalAttributes[8].key}">${product.technicalAttributes[8].key}</span>
							<span class="value"
								  title="${product.technicalAttributes[8].value}">${product.technicalAttributes[8].value}</span>
						</c:if>
					</div>
				</div>
				<div class="table-row last">
					<div class="table-cell first">

					</div>
					<div class="table-cell second">
						<c:if test="${not empty product.technicalAttributes}">
							<span class="label" title="${product.technicalAttributes[4].key}">${product.technicalAttributes[4].key}</span>
							<span class="value"
								  title="${product.technicalAttributes[4].value}">${product.technicalAttributes[4].value}</span>
						</c:if>
					</div>
					<div class="table-cell third">
						<c:if test="${not empty product.technicalAttributes}">
							<span class="label" title="${product.technicalAttributes[9].key}">${product.technicalAttributes[9].key}</span>
							<span class="value"
								  title="${product.technicalAttributes[9].value}">${product.technicalAttributes[9].value}</span>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="main__actions">
		<mod:scaled-prices product="${product}" template="product-list-technical" skin="product-list-technical" />
		<div class="action-detail">

			<mod:shipping-information template="category" product="${product}" skin="category" />

		</div>
		<mod:numeric-stepper product="${product}" template="product-list-technical" skin="product-list-technical" />
		<div class="button-holder">
			<%-- Used by product and productlist module --%>
				<c:set var="productsPerPage" value="10" />
				<c:set var="productPostion" value="${((searchPageData.pagination.currentPage-1)* productsPerPage)+position}" />
	        	<c:forEach items="${searchPageData.pagination.productsPerPageOptions}" var="productsPerPageOption">
	        		<c:if test="${productsPerPageOption.selected}">
						<c:set var="productsPerPage" value="${productsPerPageOption.value}" />
					</c:if>
				</c:forEach>
				<input type="hidden" class="hidden-product-code" value="${product.code}" />
				<input type="hidden" class="hidden-price" value="${product.price.value}" />
				<input type="hidden" class="hidden-origPosition" value="${product.origPosition}" />
				<input type="hidden" class="hidden-categoryCodePath" value="${product.categoryCodePath}" />
				<input type="hidden" class="hidden-searchQuery" value="${param['q']}" />
				<input type="hidden" class="hidden-origPageSize" value="${productsPerPage}" />
				<input type="hidden" class="hidden-pos" value="${productPostion}" />
				<input type="hidden" class="hidden-productFamily" value="${productFamily}" />
				<input type="hidden" class="hidden-productCampaign" value="${product.campaign}" />
				<input type="hidden" id="pdpSearchDatalayer" name="pdpSearchDatalayer" value="${pdpSearchDatalayer}">
			<%-- End product and productlist module --%>

			<button class="btn btn-primary btn-cart fb-add-to-cart" data-product-code="${product.codeErpRelevant}" title="Add to Cart">
				<i></i>
			</button>
		</div>
	</div>

	<c:if test="${eol}">
		<div class="eol${eolWithReplacement ? ' hasReplacement' : ''}">
			<c:set var="formattedDate">
				<fmt:formatDate value="${product.endOfLifeDate}" />
			</c:set>
			<p class="eol-message">
				<spring:message code="product.notBuyable.endOfLife.message"
					arguments="${formattedDate}" />
				<c:if test="${not empty product.replacementReason}">
					<span class="value"> <spring:message
							code="product.notBuyable.endOfLife.reason"
							arguments="${product.replacementReason}" />
					</span>
				</c:if>
			</p>
			<a class="btn-secondary" href="${product.url}"><spring:message
					code="product.notBuyable.endOfLife.alternative" /></a>
		</div>
	</c:if>
	<c:if test="${phaseOut}">
		<div class="phase-out">
			<p class="phase-out-message">
				<spring:message code="product.notBuyable.temporarly.message" />
			</p>
		</div>
	</c:if>

	<mod:schema template="product-list" product="${product}" />
</article>
