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
<spring:message code="product.scaledPrices.saveText" var="sSaveText" />
<spring:message code="text.plp.show.details" var="sShowDetails" />
<spring:message code="text.plp.hide.details" var="sHideDetails" />

<c:set var="isEProcurement" value="false"/>
<sec:authorize access="hasAnyRole('ROLE_EPROCUREMENTGROUP','ROLE_OCICUSTOMERGROUP','ROLE_ARIBACUSTOMERGROUP','ROLE_CXMLCUSTOMERGROUP')">
	<c:set var="isEProcurement" value="true"/>
</sec:authorize>

<c:set var="productFamilyUrl" value="${product.productFamilyUrl}" />
<c:set var="reevooProductId" value="${request.getParameter('reevoo-product-id')}"/>

<c:set var="statusCode" value="${product.salesStatus}" />

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

<ul class="plp-filter-products-heading">
	<li class="plp-filter-products-heading__product"> <spring:message code="text.product" /> </li>
	<li class="plp-filter-products-heading__stock"> <spring:message code="product.stock" /> </li>
	<li class="plp-filter-products-heading__price"> <spring:message code="cart.list.list.single.price" /> </li>
	<li class="plp-filter-products-heading__quantity"> <spring:message code="text.quantity" /> </li>
</ul>

<ul class="plp-filter-products">

	<c:forEach items="${searchPageData.results}" var="product" varStatus="status">
		<c:set var="productFamilyUrl" value="${product.productFamilyUrl}" />
		<li class="plp-filter-products__product js-plp-product-list-item js-numeric-stepper-without-buying-section productCode-${product.codeErpRelevant}" data-status-code="${product.salesStatus}">
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
				<c:if test="${not empty filterapplied}">
                    <c:param name="filterapplied" value="${filterapplied}" />
                </c:if>
				<c:if test="${not empty param.digitalDataLayerTerm}">
					<c:param name="digitalDataLayerTerm" value="${param.digitalDataLayerTerm}" />
                </c:if>
			</c:url>


			<div class="plp-filter-products__product__info js-check-if-out-of-stock-scope">

				<div class="plp-filter-products__product__info__compact row">

					<div class="plp-filter-products__product__info__compact__product-info col-lg-5">

						<div class="plp-filter-products__product__info__compact__product-info__image-wrapper">
							<c:set var="statusCheck" value="${statusCode != 40 && statusCode != 41 && statusCode != 60 && statusCode != 61 && statusCode != 62 }"/>
							<c:if test="${product.buyable and fn:length(product.activePromotionLabels) > 0 && statusCheck}">
								<div class="productlabel-wrap">
									<c:forEach items="${product.activePromotionLabels}" var="promoLabel" end="0">
										<mod:product-label promoLabel="${promoLabel}" />
									</c:forEach>
								</div>
							</c:if>
							<c:if test="${not empty product.energyEfficiency}">
								<mod:energy-efficiency-label skin="product" product="${product}" />
							</c:if>
							<c:set var="productImage" value="${product.productImages[0]}" />
							<c:set var="landscapeSmallJpg" value="${not empty productImage.landscape_small.url ? productImage.landscape_small.url : '/_ui/all/media/img/missing_landscape_small.png' }" />
							<c:set var="landscapeSmallWebP" value="${not empty productImage.landscape_small_webp.url ? productImage.landscape_small_webp.url : landscapeSmallJpg}" />
							<a href="${productUrl}" class="teaser-link plp-filter-products__product-link" data-aainteraction="product click" data-product-code="${product.code}" data-position="${statusIndex}" >
							<picture>
							    <source srcset="${landscapeSmallWebP}">
								<img class="plp-filter-products__img"
								     alt="<spring:message code='product.page.title.buy' text='Buy' arguments='${product.name}' argumentSeparator='!!!!' />"
									 width="70" height="58"
									 src="${empty product.productImages[0].landscape_small.url ? '/_ui/all/media/img/distrelec_logo_v2_small.png' : product.productImages[0].landscape_small.url}" />
							</a>
						</div>

                        <div class="plp-filter-products__product__info__name-image">
							<div class="plp-filter-products__product__info__compact__product-info__title">
								<a href="${productUrl}" class="teaser-link title" title="${product.name}" data-aainteraction="product click" data-product-code="${product.code}" data-position="${statusIndex}">
									<span class=""> ${product.name}	</span>
								</a>

							</div>

							<div class="plp-filter-products__product__info__compact__product-info__data">
								<div class="plp-filter-products__product__info__compact__product-info__data__item">
									<span class="label"><spring:message code="product.articleNumber" />.</span>
									<span class="value"><format:articleNumber articleNumber="${product.codeErpRelevant}"  /></span>
								</div>
								<div class="plp-filter-products__product__info__compact__product-info__data__item">
									<span class="label">MPN.</span>
									<span title="${product.typeName}" class="value type-name-short">${product.typeName}</span>
								</div>
								<div class="plp-filter-products__product__info__compact__product-info__data__item">
									<span class="label"><spring:message code="product.manufacturer" />:</span>
									<span title="${product.distManufacturer.name}" class="value type-name-short">${product.distManufacturer.name}</span>
								</div>
							</div>

							<c:if test="${isReevooActivatedForWebshop && product.eligibleForReevoo}">
								<reevoo-product-badge
								variant='PDP'
								SKU="${product.code}"
								id="reevoo_badge_pdp">
								</reevoo-product-badge>
							</c:if>

							<div class="plp-filter-products__product__right sidebar-plp">
								<span class="plp-filter-products__product__right__show-details">${sShowDetails}</span>
								<span class="plp-filter-products__product__right__hide-details hide">${sHideDetails}</span>
								<span class="plp-filter-products__product__right__toggle">  <i class="fas fa-chevron-down"></i> </span>
							</div>
						</div>



					</div>
					<div class="plp-filter-products__product__info__compact__product-stock col-lg-2">
						<mod:shipping-information template="plp-new" product="${product}" skin="comparelist-new plp" />
					</div>
					<div class="plp-filter-products__product__info__compact__product-price col-lg-2">
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
					<div class="plp-filter-products__product__info__compact__product-quantity col-lg-3">
						<div class="num-stepper">
							<mod:numeric-stepper template="productlist-search-tabular" skin="productlist-search-tabular plp" product="${product}" />
						</div>
						<div class="button-holder button-holder--${product.code} button-holder--false plp-filter-products__cart-cta">
							<button class="btn btn-cart fb-add-to-cart mat-button mat-button__solid--action-green <c:if test="${not product.buyable}"> disabled</c:if> js-btn-cart"
									data-aainteraction="add to cart"
									data-position="${statusIndex}"
									data-product-code="${product.codeErpRelevant}" title="<spring:message code="toolsitem.add.cart" text="Add to Cart" />">
								<i class="fas fa-cart-plus"></i>
								<span class="text">
										<spring:message code="toolsitem.add.cart" />
									</span>
							</button>
						</div>
						<div class="plp-filter-products__product__left">
							<mod:product-tools template="tabular" productOrderQuantityMinimum="${product.orderQuantityMinimum}" productId="${product.code}" positionIndex="${statusIndex}" />
						</div>
					</div>
				</div>

				<div class="plp-filter-products__product__info__detailed row">

					<div class="plp-filter-products__product__info__detailed__product-attributes col-lg-7">
						<c:forEach items="${product.technicalAttributes}" var="attribute" varStatus="status" >
							<div class="attributes">
								<div class="attributes__row">
									<span class="label">${product.technicalAttributes[status.count - 1].key} </span>
									<span class="value">${product.technicalAttributes[status.count - 1].value}</span>
								</div>
							</div>
						</c:forEach>
					</div>
					<div class="plp-filter-products__product__info__detailed__product-price col-lg-2">
						<mod:scaled-prices product="${product}" template="prices-tabular" skin="single-tabular" />
					</div>
					<div class="plp-filter-products__product__info__detailed__product-attachement col-lg-3">

						<c:if test="${not empty productFamilyUrl}">
							<div class="product-family-holder">
								<a class="product-family ellipsis"
								   title="${sProductFamilyLinkText}"
								   href="<c:out value="${productFamilyUrl}"/>"
								   data-position="${statusIndex}"
								   data-aainteraction="product family click"
								   data-link-text="${englishMessageSourceAccessor.getMessage('product.family.linkTextExtended')}"
								   data-location="pdp"
								>${sProductFamilyLinkText}</a>
							</div>
						</c:if>

					</div>

				</div>

			</div>

			<div class="plp-filter-products__product__right">
				<span class="plp-filter-products__product__right__toggle"> <i class="fas fa-chevron-down"></i> </span>
			</div>

		</li>

	</c:forEach>

</ul>

<mod:toolsitem template="toolsitem-compare-popup-plp" skin="compare-popup-plp" tag="div" productId="${product.code}"/>

<spring:message code="product.list.facetAjax.loadProducts" var="loadingStateMessage" />
<mod:loading-state skin="loading-state" loadingStateMessage="${loadingStateMessage}" />

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
