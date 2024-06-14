<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<spring:message code="product.shipping.available.leadTime.short" var="leadTimeText" text="More in {0} weeks" />

<c:set var="product" value="${quoteEntry.product}" />

<%-- Used by cart-list module --%>
<input type="hidden" class="hidden-product-code" value="${product.code}" />
<input type="hidden" class="hidden-product-reference" value="${quoteEntry.customerReference}" />
<input type="hidden" class="hidden-item-number" value="${quoteEntry.itemNumber}" />
<%-- End cart-list module --%>


<c:if test="${fn:length(product.activePromotionLabels) > 0}">
	<div class="productlabel-wrap">
		<c:forEach var="promotion" items="${product.activePromotionLabels}" end="0">
			<mod:product-label promoLabel="${promotion}"/>
		</c:forEach>
	</div>
</c:if>
<div class="gu-1 cell cell-list">
	<div class="image-wrap">
		<a href="${product.url}">
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
</div>

<div class="gu-4 cell cell-info base">
	<c:choose>
		<c:when test="${quoteEntry.dummyItem}">
			<h3 class="ellipsis productName" title="${quoteEntry.articleDescription}">${quoteEntry.articleDescription}</h3>
		</c:when>
		<c:otherwise>
			<h3 class="ellipsis productName" title="${product.name}"><a href="${product.url}">${product.name}</a></h3>
		</c:otherwise>
	</c:choose>
	<div class="cell-info-table">
		<div class="cell-info-cell">
			<div class="hd"><spring:message code="cart.list.articleNumber" /></div>
			<div class="bd ellipsis">  <formatArticle:articleNumber articleNumber="${product.code}"  />   </div>
		</div>
		<c:choose>
			<c:when test="${not empty product.typeName || not empty product.distManufacturer.name}">
				<div class="cell-info-cell">
					<div class="hd"><spring:message code="cart.list.typeName" /></div>
					<div class="bd ellipsis" title="<c:out value="${product.typeName}" />">${product.typeName}</div>
				</div>
				<div class="cell-info-cell">
					<div class="hd"><spring:message code="cart.list.manufacturer" /></div>
					<div class="bd ellipsis" title="<c:out value="${product.distManufacturer.name}" />">${product.distManufacturer.name}</div>
				</div>
			</c:when>
			<c:otherwise>
				<c:if test="${not empty quoteEntry.manufacturerPartNumber || not empty quoteEntry.manufacturerType}">
					<div class="cell-info-cell">
						<div class="hd"><spring:message code="cart.list.typeName" /></div>
						<div class="bd ellipsis" title="<c:out value="${quoteEntry.manufacturerPartNumber}" />">${quoteEntry.manufacturerPartNumber}</div>
					</div>
					<div class="cell-info-cell">
						<div class="hd"><spring:message code="cart.list.manufacturer" /></div>
						<div class="bd ellipsis" title="<c:out value="${quoteEntry.manufacturerType}" />">${quoteEntry.manufacturerType}</div>
					</div>
				</c:if>
			</c:otherwise>
		
		</c:choose>
	</div>
	<div class="cell-info-cell">
		<div class="hd"><spring:message code="cart.list.reference" /></div>
		<div class="bd ellipsis" title="${quoteEntry.customerReference}">${quoteEntry.customerReference}</div>
	</div>
</div>


<div class="gu-2 cell cell-availability">

	<c:if test="${not quoteEntry.dummyItem}">

		<!-- Availability -->
		<mod:shipping-information template="compare-list" product="${product}" skin="comparelist quote-detail" />


	</c:if>
</div>

<c:set var="editAllowed" value="${quotationData.purchasable && quoteEntry.quantityModificationType.code < '03'}" />
<c:set var="mandatoryItem" value="${quoteEntry.mandatory}" />
<c:set var="minimumQuantity" value="${quoteEntry.quantityModificationType.code == '01' ? quoteEntry.quantity : '1'}" />
<c:set var="maximumQuantity" value="${quoteEntry.quantityModificationType.code == '02' ? quoteEntry.quantity : '0'}" />
<c:set var="quantityStep" value="${not empty product.orderQuantityStep ? product.orderQuantityStep : '1'}" />
<div class="gu-1-5 cell cell-numeric">
	<div class="numeric numeric-small is-quot"
		data-mod-code="${quoteEntry.quantityModificationType.code}"
		data-min="${minimumQuantity}"
		data-max="${maximumQuantity}" 
		data-step="${quantityStep}" 
		data-min-error="<spring:message code='validation.error.min.order.quantity' arguments='${product.codeErpRelevant},${minimumQuantity}' htmlEscape='true' />"
		data-max-error="<spring:message code='validation.error.max.order.quantity' arguments='${product.codeErpRelevant},${maximumQuantity}' htmlEscape='true' />"
		data-step-error="<spring:message code='validation.error.steps.order.quantity' arguments='${product.codeErpRelevant},${quantityStep}' htmlEscape='true'/>"
	>
		<c:choose>
			<c:when test="${editAllowed}">
				<input type="text" name="countItems" class="ipt" data-quoted-quantity="${quoteEntry.quantity}" value="${quoteEntry.quantity}">
				<div class="btn-wrapper">
			        <button class="btn numeric-btn numeric-btn-up">+</button>
			        <button class="btn numeric-btn numeric-btn-down disabled">&ndash;</button>
		        </div>
				<div class="numeric-popover popover top">
					<div class="arrow"></div>
					<div class="popover-content"></div>
				</div>
			</c:when>
			<c:otherwise>
				<input type="hidden" name="countItems" value="${quoteEntry.quantity}">
				<p class="qty"> ${quoteEntry.quantity} </p>
			</c:otherwise>
		</c:choose>
	</div>
	<c:if test="${quotationData.purchasable && !quoteEntry.mandatory}">
		<button class="btn-numeric-remove"><spring:message code="text.remove" /></button>
	</c:if>
</div>

<div id="${product.code}_price" class="gu-2 cell cell-price">
	<div class="price-box left">
		<div class="price-wrapper quotation-single-price ${quoteEntry.price}">
			<c:set var="price" value="${quoteEntry.price}" />
			<span class="price-currency">${quoteEntry.price.currencyIso}</span>
			<span class="price" data-json="price"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
		</div>
	</div>
</div>
<div id="${product.code}_price" class="gu-2 cell cell-price">
	<div class="price-box right">
		<div class="price-wrapper quotation-subtotal-price">
			<c:set var="price" value="${quoteEntry.subtotal}" />
			<span class="price-currency">${quoteEntry.price.currencyIso}</span>
			<span class="price" data-json="totalPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
		</div>
	</div>
</div>
<c:if test="${not empty quoteEntry.itemNote}">
<div class="gu-12 cell-note">
	<div class="gu-1">&nbsp;</div>
	<div class="gu-8 cell-info">
		<div class="hd"><spring:message code="cart.list.note" text="Note" /></div>
		<div class="bd scroll" title="${quoteEntry.itemNote}">${quoteEntry.itemNote}</div>
	</div>
</div>
</c:if>
