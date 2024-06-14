<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<spring:message code="cart.image.missing" text="Image not found" var="sImageMissing"/>
<spring:message code="cart.list.addReference" text="Add a reference code" var="sAddReferenceText"/>
<spring:message code="cart.list.done" text="Done" var="sDoneText"/>
<spring:message code="cart.list.editBtn" text="Edit" var="sEditText"/>
<spring:message code="product.shipping.available.leadTime.short" var="leadTimeText" text="More in {0} weeks" />

<c:set var="product" value="${orderEntry.product}" />
<c:set var="EditCart" value="${(empty EditCart or EditCart) and not isQuoteItem}" />


<c:set var="itemUrl" value="${product.url}" />

<c:set var="itemNumber">
	<formatArticle:articleNumber articleNumber="${product.codeErpRelevant}"  />
</c:set>

<c:set var="itemName" value="${product.name}" />
<c:set var="itemQuantity" value="${orderEntry.quantity}" />

<div class="skin-cart-list-item-cart__holder">
	<c:if test="${not isQuote}">

		<%-- Used by cart-list module --%>
		<input type="hidden" class="hidden-product-code" value="${product.code}" />
		<input type="hidden" class="hidden-entry-number" value="${orderEntry.entryNumber}" />
		<input type="hidden" class="hidden-position-number" value="${position-1}" />
		<%-- End cart-list module --%>

		<c:if test="${fn:length(product.activePromotionLabels) > 0 || (!currentSalesOrg.calibrationInfoDeactivated &&
		product.calibrationService)}">
			<div class="productlabel-wrap">

				<c:choose>
					<c:when test="${fn:length(product.activePromotionLabels) > 0}">
						<c:forEach var="promotion" items="${product.activePromotionLabels}" end="0">
							<mod:product-label promoLabel="${promotion}"/>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${product.calibrationService && product.calibrated}">
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
	<div class="skin-cart-list-item-cart__holder__image">
		<div class="image-wrap">
			<a href="${itemUrl}">
				<c:set var="productImage" value="${product.productImages[0]}"/>
				<c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ? productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
				<c:set var="portraitSmallWebP" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : portraitSmallJpg}"/>
				<c:set var="portraitSmallAlt" value="${not empty productImage.portrait_small_webp.altText ? productImage.portrait_small_webp.altText : not empty productImage.portrait_small.altText == null ? productImage.portrait_small.altText : sImageMissing }"/>
				<picture>
					<source srcset="${portraitSmallWebP}">
					<img alt="${portraitSmallAlt}" src="${portraitSmallJpg}"/>
				</picture>
			</a>
		</div>

		<mod:energy-efficiency-label skin="cart" product="${product}" />

	</div>
	<div class="skin-cart-list-item-cart__holder__title">
		<c:choose>
			<c:when test="${isDummyItem}">
				<h3 class="ellipsis productName" title="${orderEntry.articleDescription}">${orderEntry.articleDescription}</h3>
			</c:when>
			<c:otherwise>
				<h3 class="ellipsis productName" title="${itemName}"><a href="${product.url}">${itemName}</a></h3>
			</c:otherwise>
		</c:choose>
	</div>
	<div class="skin-cart-list-item-cart__holder__content">
		<div class="desktop-title">
			<c:choose>
				<c:when test="${isDummyItem}">
					<span class="ellipsis productName" title="${orderEntry.articleDescription}">${orderEntry.articleDescription}</span>
				</c:when>
				<c:otherwise>
					<span class="ellipsis productName" title="${itemName}"><a href="${product.url}">${itemName}</a></span>
				</c:otherwise>
			</c:choose>
		</div>
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
	</div>

	<div class="skin-cart-list-item-cart__holder__availability">
		<c:if test="${EditCart || isQuoteItem && not isDummyItem}">
			<label class="availability"><spring:message code="cart.list.availability" /></label>
			<mod:shipping-information template="compare-list-new" product="${product}" skin="comparelist-new" />

		</c:if>
	</div>

	<c:if test="${isQuoteItem eq false}">
		<div class="skin-cart-list-item-cart__holder__numeric">
			<div class="numeric numeric-small"
				 data-min="${product.orderQuantityMinimum}"
				 data-step="${product.orderQuantityStep}"
				 data-min-error="<spring:message code='validation.error.min.order.quantity' arguments='${product.codeErpRelevant},${product.orderQuantityMinimum}' htmlEscape='true' />"
				 data-step-error="<spring:message code='validation.error.steps.order.quantity' arguments='${product.codeErpRelevant},${product.orderQuantityStep}' htmlEscape='true'/>">

				<c:choose>
					<c:when test="${EditCart}">
						<c:set var="hasFlag" value="${false}" />
						<c:forEach items="${hasMoqUpdatedSinceLastCartLoad}" var="items">
							<c:if test="${items eq product.codeErpRelevant}">
								<c:set var="hasFlag" value="${true}" />
							</c:if>
						</c:forEach>
						<div class="btn-wrapper">
							<button class="btn-wrapper__btn btn numeric-btn numeric-btn-down disabled">&ndash;</button>
							<input  class="btn-wrapper__input ipt" type="text" name="countItems" placeholder="${itemQuantity}" value="${itemQuantity}" />
							<button class="btn-wrapper__btn btn numeric-btn numeric-btn-up">+</button>
						</div>
						<div class="numeric-popover popover top <c:if test="${hasFlag eq true}">active_moq</c:if>">
							<div class="arrow"></div>
							<div class="popover-content">
								<c:if test="${hasFlag eq true}">
									<spring:message code="validation.error.min.cart.moq.quantity" arguments="${product.code},${product.orderQuantityStep}" />
								</c:if>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<span class="noEditCart">${itemQuantity}</span>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="toolbar">
				<button class="btn-numeric-remove" data-aainteraction="remove from cart" data-product-id="${product.codeErpRelevant}" data-quantity="${itemQuantity}"><spring:message code="cart.list.remove" /></button>
				<div class="toolbar__edit">
					<span class="toolbar__edit__reference">

					</span>
					<span class="toolbar__edit__text">
						${sEditText}
					</span>
				</div>
				<span class="toolbar__add-reference">
					${sAddReferenceText}
				</span>
				<div class="toolbar__input">
					<c:if test="${EditCart || isQuoteItem}">
						<input name="reference" class="field ipt-reference" type="text" maxlength="35" value="${orderEntry.customerReference}" placeholder="<spring:message code='cart.list.reference' />" />
					</c:if>
					<span class="toolbar__input__btn">
						${sDoneText}
					</span>
				</div>
			</div>
		</div>
	</c:if>

	<c:choose>
		<c:when test="${showMyPrice and showListPrice}">
			<div id="${product.code}-price-single" class="skin-cart-list-item-cart__holder__price">
				<div class="price-box left">
					<div class="price-wrapper">
						<c:set var="price" value="${orderEntry.basePrice}" />
						<p class="price ellipsis" title="<spring:message code="cart.list.my.single.price" />">
							<spring:message code="cart.list.my.single.price" />
						</p>
						<span class="price-currency">${price.currencyIso}</span>
						<span class="price" data-json="basePrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
					</div>
					<div class="price-wrapper price-light">
						<c:set var="price" value="${orderEntry.baseListPrice}" />
						<p class="price ellipsis" title="<spring:message code="cart.list.list.single.price" />">
							<spring:message code="cart.list.list.single.price" />
						</p>
						<span class="price-currency">${price.currencyIso}</span>
						<span class="price" data-json="baseListPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
					</div>
				</div>
			</div>
			<div id="${product.code}-price-total" class="skin-cart-list-item-cart__holder__price skin-cart-list-item-cart__holder__price--second">
				<div class="price-box right">
					<div class="price-wrapper">
						<c:set var="price" value="${orderEntry.totalPrice}" />
						<p class="price ellipsis" title="<spring:message code="cart.list.my.subtotal" />">
							<spring:message code="cart.list.my.subtotal" />
						</p>
						<span class="price-currency">${price.currencyIso}</span>
						<span class="price" data-json="totalPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
					</div>
					<div class="price-wrapper price-light">
						<c:set var="price" value="${orderEntry.totalListPrice}" />
						<p class="price ellipsis" title="<spring:message code="cart.list.list.subtotal" />">
							<spring:message code="cart.list.list.subtotal" />
						</p>
						<span class="price-currency">${price.currencyIso}</span>
						<span class="price" data-json="totalListPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
					</div>
				</div>
			</div>
		</c:when>
		<c:when test="${not showMyPrice and showListPrice}">
			<div id="${product.code}-price-single" class="skin-cart-list-item-cart__holder__price">
				<div class="price-box left">
					<div class="price-wrapper">
						<c:set var="price" value="${empty orderEntry.baseListPrice ? orderEntry.basePrice : orderEntry.baseListPrice}" />
						<p class="price ellipsis" title="<spring:message code="cart.list.list.single.price" />">
							<spring:message code="cart.list.list.single.price" />
						</p>
						<span class="price-currency">${price.currencyIso}</span>
						<span class="price" data-json="baseListPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
					</div>
				</div>
			</div>
			<div id="${product.code}-price-total" class="skin-cart-list-item-cart__holder__price skin-cart-list-item-cart__holder__price--second">
				<div class="price-box right">
					<div class="price-wrapper">
						<c:set var="price" value="${empty orderEntry.totalListPrice ? orderEntry.totalPrice : orderEntry.totalListPrice}" />
						<p class="price ellipsis" title="<spring:message code="cart.list.list.subtotal" />">
							<spring:message code="cart.list.list.subtotal" />
						</p>
						<span class="price-currency">${price.currencyIso}</span>
						<span class="price" data-json="totalListPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
					</div>
				</div>

				<c:if test="${isQuoteItem eq true}">
					<div class="quantity-item">
						<spring:message code="basket.page.quantity"/> &nbsp;${itemQuantity}
					</div>
				</c:if>

			</div>
		</c:when>
		<c:when test="${showMyPrice and not showListPrice}">
			<div id="${product.code}-price-single" class="skin-cart-list-item-cart__holder__price">
				<div class="price-box left">
					<div class="price-wrapper">
						<c:set var="price" value="${orderEntry.basePrice}" />
						<p class="price ellipsis" title="<spring:message code="cart.list.my.single.price" />">
							<spring:message code="cart.list.my.single.price" />
						</p>
						<span class="price-currency">${price.currencyIso}</span>
						<span class="price" data-json="basePrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
					</div>
				</div>
			</div>
			<div id="${product.code}-price-total" class="skin-cart-list-item-cart__holder__price skin-cart-list-item-cart__holder__price--second">
				<div class="price-box right">
					<div class="price-wrapper">
						<c:set var="price" value="${orderEntry.totalPrice}" />
						<p class="price ellipsis" title="<spring:message code="cart.list.my.subtotal" />">
							<spring:message code="cart.list.my.subtotal" />
						</p>
						<span class="price-currency">${price.currencyIso}</span>
						<span class="price" data-json="totalPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
					</div>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<div id="${product.code}-price" class="skin-cart-list-item-cart__holder__price skin-cart-list-item-cart__holder__price--empty">
			</div>
		</c:otherwise>
	</c:choose>

	<c:if test="${!currentSalesOrg.calibrationInfoDeactivated && product.calibrationService && not empty product.calibrationItemArtNo}">
		<c:if test="${product.calibrated}">
			<div class="skin-cart-list-item-cart__holder__calibration calibration" data-calibrated-item-id="${product.codeErpRelevant}" data-non-calibrated-item-id="${product.calibrationItemArtNo}" data-is-calibrated="true">
				<div class="calibration__item">
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
				<div class="calibration__item calibration__item--empty">
				</div>
			</div>
		</c:if>
		<c:if test="${!product.calibrated}">
			<div class="skin-cart-list-item-cart__holder__calibration calibration" data-calibrated-item-id="${product.calibrationItemArtNo}" data-non-calibrated-item-id="${product.codeErpRelevant}" data-is-calibrated="false">
				<div class="calibration__item">
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
				<div class="calibration__item calibration__item--empty"></div>
			</div>
		</c:if>
	</c:if>
</div>
