<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<spring:message code="cart.image.missing" text="Image not found" var="sImageMissing"/>
<spring:message code="product.shipping.available.leadTime.short" var="leadTimeText" text="More in {0} weeks" />

<c:set var="product" value="${orderEntry.product}" />
<c:set var="EditCart" value="${(empty EditCart or EditCart) and not isQuoteItem}" />
<c:set var="hasMoqUpdatedSinceLastCartLoad" value="${hasMoqUpdatedSinceLastCartLoad}" />

<c:set var="itemUrl" value="${product.url}" />

<c:set var="itemNumber">
	<formatArticle:articleNumber articleNumber="${product.codeErpRelevant}"  />
</c:set>

<c:set var="itemName" value="${product.name}" />
<c:set var="itemQuantity" value="${orderEntry.quantity}" />

<c:if test="${not isQuote}">
	
	<%-- Used by cart-list module --%>
	<input type="hidden" class="hidden-product-code" value="${product.code}" />
	<input type="hidden" class="hidden-entry-number" value="${orderEntry.entryNumber}" />
	<input type="hidden" class="hidden-position-number" value="${position-1}" />
	<%-- End cart-list module --%>

	<c:if test="${fn:length(product.activePromotionLabels) > 0 || (!currentSalesOrg.calibrationInfoDeactivated && 
		product.calibrationService && not empty product.calibrationItemArtNo)}">
		<div class="productlabel-wrap">

			<c:choose>
				<c:when test="${fn:length(product.activePromotionLabels) > 0}">
					<c:forEach var="promotion" items="${product.activePromotionLabels}" end="0">
						<mod:product-label promoLabel="${promotion}"/>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${product.calibrationService}">
							<mod:product-label promoLabel="${promotion}" code="cal" htmlClasses="cal"/>
						</c:when>
						<c:otherwise>
							<mod:product-label promoLabel="${promotion}" code="cal" htmlClasses="cal hidden"/>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>

</c:if>
<div class="gu-1 cell cell-list">
	<div class="image-wrap">
		<a href="${itemUrl}">
			<c:set var="productImage" value="${product.productImages[0]}"/>
			<c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ? productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
			<c:set var="portraitSmallWebP" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : portraitSmallJpg}"/>
			<c:set var="portraitSmallAlt" value="${not empty productImage.portrait_small_webp.altText ? productImage.portrait_small_webp.altText : not empty productImage.portrait_small.altText == null ? productImage.portrait_small.altText : sImageMissing }"/>
			<picture>
				<source srcset="${portraitSmallWebP}">
				<img alt="${portraitSmallAlt}" src="${portraitSmallJpg}" />
			</picture>
		</a>
	</div>

	<mod:energy-efficiency-label skin="cart" product="${product}" />

</div>
<div class="gu-4 cell cell-info base">
	<c:choose>
		<c:when test="${isDummyItem}">
			<h3 class="ellipsis productName" title="${orderEntry.articleDescription}">${orderEntry.articleDescription}</h3>
		</c:when>
		<c:otherwise>
			<h3 class="ellipsis productName" title="${itemName}"><a href="${product.url}">${itemName}</a></h3>
		</c:otherwise>
	</c:choose>
	<div class="cell-info-table">
		<div class="cell-info-cell">
			<div class="hd"><spring:message code="cart.list.articleNumber" /></div>
			<div class="bd ellipsis" title="${itemNumber}">${itemNumber}</div>
		</div>
		<c:if test="${not empty product.typeName || not empty product.distManufacturer.name}">
			<div class="cell-info-cell">
				<div class="hd"><spring:message code="cart.list.typeName" /></div>
				<div class="bd ellipsis" title="<c:out value="${product.typeName}" />">${product.typeName}</div>
			</div>
			<div class="cell-info-cell">
				<div class="hd"><spring:message code="cart.list.manufacturer" /></div>
				<div class="bd ellipsis" title="<c:out value="${product.distManufacturer.name}" />">${product.distManufacturer.name}</div>
			</div>
		</c:if>
	</div>

	<c:if test="${EditCart || isQuoteItem}">
		<input name="reference" class="field ipt-reference" type="text" maxlength="35" value="${orderEntry.customerReference}" placeholder="<spring:message code='cart.list.reference' />" />
	</c:if>
</div>

<div class="gu-1 cell cell-availability">
	<c:if test="${EditCart || isQuoteItem && not isDummyItem}">
	
		<mod:shipping-information template="compare-list-new" product="${product}" skin="comparelist-new" />
	   			 
	</c:if>
</div>



<div class="gu-2 cell cell-numeric">
	<div class="numeric numeric-small"
		data-min="${product.orderQuantityMinimum}" 
		data-step="${product.orderQuantityStep}"
		 <c:choose>
			 <c:when test="${hasMoqUpdatedSinceLastCartLoad}">
				 data-min-error="<spring:message code='validation.error.min.cart.moq.quantity' arguments='${product.codeErpRelevant},${product.orderQuantityMinimum}' htmlEscape='true' />"
			 </c:when>
			 <c:otherwise>
				 data-min-error="<spring:message code='validation.error.min.order.quantity' arguments='${product.codeErpRelevant},${product.orderQuantityMinimum}' htmlEscape='true' />"
			 </c:otherwise>
		 </c:choose>
		data-step-error="<spring:message code='validation.error.steps.order.quantity' arguments='${product.orderQuantityStep}' htmlEscape='true'/>">

		<c:choose>
			<c:when test="${EditCart}">
				<input type="text" name="countItems" class="ipt" placeholder="${itemQuantity}" value="${itemQuantity}">
				<div class="btn-wrapper">
			        <button class="btn numeric-btn numeric-btn-up">+</button>
			        <button class="btn numeric-btn numeric-btn-down disabled">&ndash;</button>
		        </div>
				<div class="numeric-popover popover top">
					<div class="arrow"></div>
					<div class="popover-content"></div>
				</div>
				<button class="btn-numeric-remove"><spring:message code="cart.list.remove" /></button>
			</c:when>
			<c:otherwise>
				<span class="noEditCart">${itemQuantity}</span>
			</c:otherwise>
		</c:choose>		
	</div>
</div>

<c:choose>
	<c:when test="${showMyPrice and showListPrice}">
		<div id="${product.code}-price-single" class="gu-2 cell cell-price">
			<div class="price-box left">
				<div class="price-wrapper">
					<c:set var="price" value="${orderEntry.basePrice}" />
					<span class="price-currency">${price.currencyIso}</span>
					<span class="price" data-json="basePrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
				</div>
				<div class="price-wrapper price-light">
					<c:set var="price" value="${orderEntry.baseListPrice}" />
					<span class="price-currency">${price.currencyIso}</span>
					<span class="price" data-json="baseListPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
				</div>
			</div>
		</div>
		<div id="${product.code}-price-total" class="gu-2 cell cell-price">	
			<div class="price-box right">
				<div class="price-wrapper">
					<c:set var="price" value="${orderEntry.totalPrice}" />
					<span class="price-currency">${price.currencyIso}</span>
					<span class="price" data-json="totalPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
				</div>
				<div class="price-wrapper price-light">
					<c:set var="price" value="${orderEntry.totalListPrice}" />
					<span class="price-currency">${price.currencyIso}</span>
					<span class="price" data-json="totalListPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
				</div>
			</div>
		</div>
	</c:when>
	<c:when test="${not showMyPrice and showListPrice}">
		<div id="${product.code}-price-single" class="gu-2 cell cell-price">
			<div class="price-box left">
				<div class="price-wrapper">
					<c:set var="price" value="${empty orderEntry.baseListPrice ? orderEntry.basePrice : orderEntry.baseListPrice}" />
					<span class="price-currency">${price.currencyIso}</span>
					<span class="price" data-json="baseListPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
				</div>
			</div>
		</div>
		<div id="${product.code}-price-total" class="gu-2 cell cell-price">
			<div class="price-box right">	
				<div class="price-wrapper">
					<c:set var="price" value="${empty orderEntry.totalListPrice ? orderEntry.totalPrice : orderEntry.totalListPrice}" />
					<span class="price-currency">${price.currencyIso}</span>
					<span class="price" data-json="totalListPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
				</div>
			</div>
		</div>
	</c:when>
	<c:when test="${showMyPrice and not showListPrice}">
		<div id="${product.code}-price-single" class="gu-2 cell cell-price">
			<div class="price-box left">
				<div class="price-wrapper">
					<c:set var="price" value="${orderEntry.basePrice}" />
					<span class="price-currency">${price.currencyIso}</span>
					<span class="price" data-json="basePrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
				</div>
			</div>
		</div>
		<div id="${product.code}-price-total" class="gu-2 cell cell-price">
			<div class="price-box right">
				<div class="price-wrapper">
					<c:set var="price" value="${orderEntry.totalPrice}" />
					<span class="price-currency">${price.currencyIso}</span>
					<span class="price" data-json="totalPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div id="${product.code}-price" class="gu-4 cell cell-price-empty">
		</div>
	</c:otherwise>
</c:choose>
<%-- #CAL
 <c:if test="${isBOM}">
	<div class="row">
		<div class="gu-8">
			<h4 class="bundle"><spring:message code="product.bundle.includes" text="Bundle includes" /></h4>
		</div>
		<div class="gu-4 cell-price-empty">
		</div>
	</div>
	<c:forEach var="subEntry" items="${orderEntry.subOrderEntryData}" varStatus="subStatus">
		<mod:cart-list-item tag="div" subEntry="${subEntry}" showMyPrice="false" htmlClasses="row sub-item" showListPrice="false" position="${subStatus.index + 1}" isSubItem="true"/>
	</c:forEach>
</c:if>
--%>
	<c:if test="${!currentSalesOrg.calibrationInfoDeactivated}">
		<c:if test="${product.calibrationService}">
			<div class="row calibration" data-calibrated-item-id="${product.codeErpRelevant}" data-non-calibrated-item-id="${product.calibrationItemArtNo}" data-is-calibrated="true">
				<div class="gu-8">
					<div class="calibration-service">
						<h4><spring:message code="cart.list.calibration.title" /></h4>
						<div>
							<spring:message code="cart.list.calibration.text" />&nbsp;<a href="<spring:message code="cart.list.calibration.cta.url" />" target="_blank"><spring:message code="cart.list.calibration.cta.text" /></a>
						</div>
						<div class="calibration-selection">
							<div>
								<input type="radio" name="calibration-${product.codeErpRelevant}-${position}" id="calibration-${product.calibrationItemArtNo}-${position}" value="0" /><label for="calibration-${product.calibrationItemArtNo}-${position}"><spring:message code="cart.list.calibration.no" />&nbsp;<span class="calibration-cost-${product.calibrationItemArtNo}"></span></label><br />
							</div>
							<div>
								<input type="radio" name="calibration-${product.codeErpRelevant}-${position}" id="calibration-${product.codeErpRelevant}-${position}" value="1" checked="checked" /><label for="calibration-${product.codeErpRelevant}-${position}"><spring:message code="cart.list.calibration.yes" />&nbsp;<span class="calibration-cost-${product.codeErpRelevant}"></span></label>
							</div>
						</div>
					</div>
				</div>
				<div class="gu-4 cell-price-empty">
				</div>
			</div>
		</c:if>
		<c:if test="${!product.calibrated && not empty product.calibrationItemArtNo}">
			<div class="row calibration" data-calibrated-item-id="${product.calibrationItemArtNo}" data-non-calibrated-item-id="${product.codeErpRelevant}" data-is-calibrated="false">
				<div class="gu-8">
					<div class="calibration-service">
						<h4><spring:message code="cart.list.calibration.title" /></h4>
						<div>
							<spring:message code="cart.list.calibration.text" />&nbsp;<a href="<spring:message code="cart.list.calibration.cta.url" />" target="_blank"><spring:message code="cart.list.calibration.cta.text" /></a>
						</div>
						<div class="calibration-selection">
							<div>
								<input type="radio" name="calibration-${product.codeErpRelevant}-${position}" id="calibration-${product.codeErpRelevant}-${position}" value="0" checked="checked" /><label for="calibration-${product.codeErpRelevant}-${position}"><spring:message code="cart.list.calibration.no" />&nbsp;<span class="calibration-cost-${product.codeErpRelevant}"></span></label><br />
							</div>
							<div>
								<input type="radio" name="calibration-${product.codeErpRelevant}-${position}" id="calibration-${product.calibrationItemArtNo}-${position}" value="1" /><label for="calibration-${product.calibrationItemArtNo}-${position}"><spring:message code="cart.list.calibration.yes" />&nbsp;<span class="calibration-cost-${product.calibrationItemArtNo}"></span></label>
							</div>
						</div>
					</div>
				</div>
				<div class="gu-4 cell-price-empty">
				</div>
			</div>
		</c:if>
	</c:if>
