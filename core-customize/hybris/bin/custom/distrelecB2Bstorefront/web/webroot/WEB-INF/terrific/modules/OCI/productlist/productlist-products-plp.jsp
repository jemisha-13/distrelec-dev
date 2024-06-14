<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="formatprice" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<c:set var="EditCart" value="${(empty EditCart or EditCart) and not isSubItem}" />
<spring:message code="text.view.product.family" var="sProductFamilyLinkText"/>
<spring:message code="product.scaledPrices.excVat" var="sExcVat" />
<spring:message code="product.scaledPrices.incVat" var="sIncVat" />

<c:set var="productFamilyUrl" value="${product.productFamilyUrl}" />
<c:set var="reevooProductId" value="${request.getParameter('reevoo-product-id')}"/>

<c:forEach items="${searchPageData.pagination.productsPerPageOptions}" var="productsPerPageOption">
	<c:if test="${productsPerPageOption.selected}">
		<c:set var="pageSize" value="${productsPerPageOption.value}" />
	</c:if>
</c:forEach>

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



<ul class="plp-filter-products">

	<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
		<c:set var="productFamilyUrl" value="${product.productFamilyUrl}" />
		<li class="plp-filter-products__product productCode-${product.codeErpRelevant}" data-status-code="${product.salesStatus}">
			<mod:schema template="product-list" product="${product}" />
			<c:set var="productsPerPage" value="10" />
			<c:forEach items="${searchPageData.pagination.productsPerPageOptions}" var="productsPerPageOption">
				<c:if test="${productsPerPageOption.selected}">
					<c:set var="productsPerPage" value="${productsPerPageOption.value}" />
				</c:if>
			</c:forEach>
			<c:set var="position" value="${((searchPageData.pagination.currentPage-1)* productsPerPage)+status.index+1}" />
			<c:set var="statusIndex" value="${((searchPageData.pagination.currentPage-1)* productsPerPage)+status.index+1}" />
			<input type="hidden" class="hidden-product-code" value="${product.code}" />
			<input type="hidden" class="hidden-price" value="${product.price.value}" />
			<input type="hidden" class="hidden-origPosition" value="${product.origPosition}" />
			<input type="hidden" class="hidden-categoryCodePath" value="${product.categoryCodePath}" />
			<input type="hidden" class="hidden-searchQuery" value="${param['q']}" />
			<input type="hidden" class="hidden-origPageSize" value="${productsPerPage}" />
			<input type="hidden" class="hidden-pos" value="${position}" />
			<input type="hidden" class="hidden-productFamily" value="${productFamily}" />
			<input type="hidden" class="hidden-productCampaign" value="${product.campaign}" />
			<c:set var="statusIndex" value="${((searchPageData.pagination.currentPage-1)* productsPerPage)+status.index+1}" />

			<c:url value="${product.url}" var="productUrl">
				<c:choose>
					<c:when test="${not empty trackQuery}">
						<c:param name="trackQuery" value="${trackQuery}" />
					</c:when>
					<c:otherwise>
						<c:param name="trackQuery" value="${param.q}" />
					</c:otherwise>
				</c:choose>
				<c:param name="pos" value="${position}" />
				<c:param name="origPos" value="${product.origPosition}" />
				<c:param name="origPageSize" value="${productsPerPage}" />
				<c:if test="${not empty param.digitalDataLayerTerm}">
                	<c:param name="digitalDataLayerTerm" value="${param.digitalDataLayerTerm}" />
                </c:if>
			</c:url>

			<div class="row">

				<div class="col-3 col-md-1">
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
					<a href="${productUrl}" class="teaser-link plp-filter-products__product-link" data-aainteraction="product click" data-product-code="${product.code}" data-position="${statusIndex}" >
						<img class="plp-filter-products__img" alt="<spring:message code='product.page.title.buy' text='Buy' arguments='${product.name}' argumentSeparator='!!!!' />"
							 width="70" height="58"
							 src="${empty product.productImages[0].landscape_small.url ? '/_ui/all/media/img/distrelec_logo_v2_small.png' : product.productImages[0].landscape_small.url}" />
					</a>
				</div>

				<div class="col-9 col-md-11 pull-right">

					<div class="plp-filter-products__information">
						<div class="plp-filter-products__information__title">
							<a href="${productUrl}" class="teaser-link title" title="${product.name}" data-aainteraction="product click" data-product-code="${product.code}" data-position="${statusIndex}">
								<span class=""> ${product.name}	</span>
							</a>

						</div>
						<c:if test="${isReevooActivatedForWebshop && product.eligibleForReevoo}">
							<reevoo-product-badge
									variant='PDP'
									SKU="${product.code}"
									id="reevoo_badge_pdp">
							</reevoo-product-badge>
						</c:if>

						<div class="plp-filter-products__information__data">
							<div class="plp-filter-products__information__data__item">
								<span class="label"><spring:message code="product.articleNumber" />:</span>
								<span class="value"><format:articleNumber articleNumber="${product.codeErpRelevant}"  /></span>
							</div>
							<div class="plp-filter-products__information__data__item">
								<span class="label"><spring:message code="product.typeName" />:</span>
								<span title="${product.typeName}" class="value type-name-short">${product.typeName}</span>
							</div>
							<div class="plp-filter-products__information__data__item">
								<span class="label"><spring:message code="product.manufacturer" />:</span>
								<span title="${product.distManufacturer.name}" class="value type-name-short">${product.distManufacturer.name}</span>
							</div>
							<div class="plp-filter-products__information__data__main">

								<c:forEach items="${product.technicalAttributes}" var="attribute" varStatus="status" >
									<div class="attributes">
										<div class="attributes__row">
											<span class="label">${product.technicalAttributes[status.count - 1].key} </span>
											<span class="value">${product.technicalAttributes[status.count - 1].value}</span>
										</div>
									</div>
								</c:forEach>

							</div>
						</div>
					</div>
					<div class="plp-filter-products__price">
						<div class="plp-filter-products__price__main">
							<span class="price">
								<formatprice:price format="default" priceData="${product.price}" />

								<c:forEach var="volumePrice" items="${product.volumePricesMap}" end="0">
									<c:if test="${not empty volumePrice.value.custom}">
										<c:set var="price" value="${volumePrice}"/>
										<c:set var="yourPriceDiffers" value="${volumePrice.value.list.value gt volumePrice.value.custom.value}" />
									</c:if>
								</c:forEach>

								<c:if test="${yourPriceDiffers}">
									<span class="old-price">
										<formatprice:price format="default" priceData="${price.value.list}" />
									</span>
								</c:if>

								<c:choose>
									<c:when test="${currentChannel.net}">
										<span class="vat-text"> (${sExcVat}) </span>
									</c:when>
									<c:otherwise>
										<span class="vat-text"> (${sIncVat}) </span>
									</c:otherwise>
								</c:choose>

								<c:if test="${yourPriceDiffers}">
									<span class="price-save">
										${sSaveText}&nbsp;${price.value.custom.saving} %
									</span>
								</c:if>
							</span>
						</div>
					</div>


					<div class="plp-filter-products__product-actions">

						<div class="plp-filter-products__product-actions__item plp-filter-products__product-actions__item--left">
							<mod:product-tools template="tabular" productId="${product.code}"  />
							<c:if test="${not empty productFamilyUrl}">
								<div class="product-family-holder">
									<a class="product-family ellipsis"
									   title="${sProductFamilyLinkText}"
									   href="<c:out value="${productFamilyUrl}"/>"
									   data-aainteraction="product family click"
									   data-link-text="${englishMessageSourceAccessor.getMessage('product.family.linkTextExtended')}"
									   data-location="pdp"
									>${sProductFamilyLinkText}</a>
								</div>
							</c:if>
						</div>

						<div class="plp-filter-products__product-actions__item plp-filter-products__product-actions__item--right">

							<div class="shipping-info">
								<mod:shipping-information template="plp-new" product="${product}" skin="comparelist-new plp" />
							</div>

							<div class="num-stepper">
								<mod:numeric-stepper template="productlist-search-tabular" skin="productlist-search-tabular" product="${product}" />
							</div>
							<div class="button-holder button-holder--true plp-filter-products__cart-cta">
								<button class="btn btn-cart fb-add-to-cart mat-button mat-button__solid--action-green ellipsis <c:if test="${not product.buyable}"> disabled</c:if>"
										data-aainteraction="add to cart"
										data-position="${statusIndex}"
										data-product-code="${product.codeErpRelevant}" title="<spring:message code="toolsitem.add.cart" text="Add to Cart" />">
									<i class="fas fa-cart-plus"></i>
									<span class="text">
										<spring:message code="toolsitem.add.cart" />
									</span>
								</button>
							</div>
						</div>
					</div>

				</div>
			</div>

		</li>

	</c:forEach>

</ul>


<spring:url value="${commonResourcePath}/images/loader.gif" var="spinnerUrl" />

<div class="ajax-product-loader">
	<div class="background-overlay"></div>
	<div class="message-wrapper">
		<div class="loading-message">
			<img id="spinner" src="${spinnerUrl}" alt="spinner" class="loading-message__icon"/>
			<p class="loading-message__text"><spring:message code="product.list.facetAjax.loadProducts" /></p>
		</div>
	</div>
</div>

<div class="ajax-product-loader js-apply-facets">
	<div class="background-overlay apply-facets"></div>
	<div class="message-wrapper js-apply-facets">
		<div class="loading-message apply-facets reload-message" >
			<a href="" class="js-update-result" data-current-query-url="${searchPageData.currentQuery.url}" >
				<span>
					<spring:message code="product.list.reloadLayer" />
				</span>
			</a>
		</div>
	</div>
</div>
