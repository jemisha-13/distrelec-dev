<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="formatprice" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<spring:message code="product.product-bom.image.missing" text="Image not found" var="sImageMissing"/>

<c:choose>
	<c:when test="${producttype eq 'alternative'}">
		<c:set var="product" value="${product}" />
	</c:when>
	<c:otherwise>
		<c:set var="product" value="${matchingProduct.product}" />
	</c:otherwise>
</c:choose>

<c:set var="phaseOut" value="${(product.buyable == false) and (empty product.endOfLifeDate)}" />
<c:set var="eol" value="${not empty product.endOfLifeDate}" />
<c:set var="eolWithReplacement" value="${not empty product.endOfLifeDate and product.buyableReplacementProduct}" />

<spring:message code="product.scaledPrices.excVat" var="sExcVat" />
<spring:message code="product.scaledPrices.incVat" var="sIncVat" />

<%-- Used by product and productlist module --%>
<input type="hidden" class="hidden-product-code" value="${product.code}" />

<c:set var="productReference" value="${matchingProduct.reference == 'undefined' ? ' ' : matchingProduct.reference}" />

<c:if test="${not empty carriedReference}">
	<c:set var="productReference" value="${carriedReference}" />
</c:if>

<c:if test="${not empty carriedQuantity}">
	<c:set var="productCarriedQuantity" value="${carriedQuantity}" />
</c:if>

<input type="hidden" class="hidden-product-reference" value="${productReference}" />
<input type="hidden" class="hidden-product-quantity" value="${matchingProduct.quantity}" />
<%-- End productlist --%>

<%-- Variable is needed to determine if the product link should be displayed or not --%>
<c:if test="${product.buyable or eolWithReplacement}">
	<c:set var="linkOpeningTag">
		<a href="${product.url}" class="teaser-link" data-position="${position}">
	</c:set>
	<c:set var="linkClosingTag">
		</a>
	</c:set>
</c:if>

<c:url value="${product.url}" var="productUrl">
	<c:param name="q" value="${param.q}" />
	<c:param name="pos" value="${position}" />
	<c:param name="origPos" value="${product.origPosition}" />
	<c:param name="origPageSize" value="${productsPerPage}" />
</c:url>

<div class="row bom-product bom-product--${product.code} js-numeric-stepper-without-buying-section" data-status-code="${product.salesStatus}">

	<div class="col-3 col-md-2">

		<label class="bom-product__checkbox">
			<input type="checkbox" />
			<span class="mat-checkbox"></span>
		</label>

		<a href="${productUrl}" class="teaser-link bom-product__image" data-aainteraction="product click" data-product-code="${product.code}" data-position="${statusIndex}" >
			<c:set var="productImage" value="${product.productImages[0]}"/>
			<c:set var="landscapeSmallJpg" value="${not empty productImage.landscape_small.url ? productImage.landscape_small.url : '/_ui/all/media/img/missing_landscape_small.png' }"/>
			<c:set var="landscapeSmallWebP" value="${not empty productImage.landscape_small_webp.url ? productImage.landscape_small_webp.url : landscapeSmallJpg}"/>
			<picture>
				<source srcset="${landscapeSmallWebP}">
				<img class="bom-product__img"
				width="112" height="93"
				alt="<spring:message code='product.page.title.buy' text='Buy' arguments='${product.name}' argumentSeparator='!!!!' />"
				src="${landscapeSmallJpg}"/>
			</picture>
		</a>
	</div>
	<div class="col-9 col-md-10 pull-right">
		<div class="bom-product__price">
			<formatprice:price format="default" priceData="${product.price}" />

			<c:choose>
				<c:when test="${currentChannel.net}">
					<span class="vat-text"> (${sExcVat}) </span>
				</c:when>
				<c:otherwise>
					<span class="vat-text"> (${sIncVat}) </span>
				</c:otherwise>
			</c:choose>

		</div>
		<div class="bom-product__information">
			${linkOpeningTag}
			<h3 class="bom-product__title" title="${product.name}">${product.name}</h3>
			${linkClosingTag}

			<c:if test="${isReevooActivatedForWebshop && product.eligibleForReevoo}">
				<reevoo-product-badge
						variant='PDP'
						SKU="${product.code}"
						id="reevoo_badge_pdp">
				</reevoo-product-badge>
			</c:if>

			<div class="bom-product__information articleNumber">

				<div class="bom-product__information-item">
					<span class="label"><spring:message code="product.articleNumber" />:</span>
					<span class="value"><format:articleNumber articleNumber="${product.codeErpRelevant}"  /></span>
				</div>
				<div class="bom-product__information-item">
					<span class="label"><spring:message code="product.typeName" />:</span>
					<span title="${product.typeName}" class="value type-name-short">${product.typeName}</span>
				</div>
				<div class="bom-product__information-item">
					<span class="label"><spring:message code="product.manufacturer" />:</span>
					<span title="${product.distManufacturer.name}" class="value type-name-short">${product.distManufacturer.name}</span>
				</div>

			</div>
		</div>

		<div class="row bom-product__detail-view">

			<div class="col-md-8">
				<c:forEach items="${product.technicalAttributes}" var="attribute" varStatus="status" >
					<div class="attributes">
						<div class="attributes__row">
							<span class="label">${product.technicalAttributes[status.count - 1].key} </span>
							<span class="value">${product.technicalAttributes[status.count - 1].value}</span>
						</div>
					</div>
				</c:forEach>
			</div>

			<div class="col-md-4 bom-product__price__second">
				<mod:scaled-prices product="${product}" skin="single" template="single" />
			</div>

		</div>

		<div class="bom-product__actions row">

			<div class="col-lg-7 bom-product__actions-left">
				<mod:toolsitem template="toolsitem-bom-remove" skin="bom-remove" productId="${product.code}"/>
				<mod:toolsitem template="toolsitem-shopping-bom" skin="shopping" tag="div"
							   productMinQuantity="${product.orderQuantityMinimum}"
							   productId="${product.code}" htmlClasses="tabular skin-toolsitem-shopping-bom"/>
				<input type="text" name="reference" placeholder="<spring:message code="text.addyourreference" />" class="product-reference bom-product__reference" value="${productReference}" />
			</div>

			<div class="col bom-product__actions-right">

				<div class="bom-product__shipping-info">
					<mod:erp-sales-status productArtNo="${product.code}" productStatusCode="${product.salesStatus}"/>
				</div>

				<div class="bom-product__numeric-stepper">
					<c:choose>
						<c:when test="${productCarriedQuantity gt 0}">
							<c:set var="bomQty" value="${productCarriedQuantity != null ? productCarriedQuantity : '1' }" />
						</c:when>
						<c:otherwise>
							<c:set var="bomQty" value="${matchingProduct.quantity != null ? matchingProduct.quantity : '1' }" />
						</c:otherwise>
					</c:choose>
					<mod:numeric-stepper template="productlist-search-tabular" skin="productlist-search-tabular" product="${product}" bomQty="${bomQty}"/>
				</div>

			</div>

		</div>

	</div>

	<span class="addtocart-message"> <spring:message code="text.added.to.cart" /> <i class="fas fa-check-circle"></i> </span>

</div>