<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="product" value="${orderEntry.product}" />

<%-- Used by cart-list module --%>
<input type="hidden" class="hidden-product-code" value="${product.code}" />
<input type="hidden" class="hidden-entry-number" value="${orderEntry.entryNumber}" />
<input type="hidden" class="hidden-product-reference" value="" />
<%-- End cart-list module --%>


<div class="cell cell-list">
	<c:if test="${fn:length(product.activePromotionLabels) > 0}">
		<div class="productlabel-wrap">
			<c:forEach var="promotion" items="${product.activePromotionLabels}" end="0">
				<mod:product-label promoLabel="${promotion}"/>
			</c:forEach>
		</div>
	</c:if>
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
	<mod:energy-efficiency-label skin="cart" product="${product}" />
</div>

<div class="cell cell-info base js-base" data-delivery-id="${orderEntry.deliveryId}" data-delivery-quantity="${orderEntry.deliveryQuantity}" data-tracking-url="${orderEntry.deliveryTrackingUrl}" data-item-quantity="${orderEntry.quantity}">
	<c:choose>
		<c:when test="${isDummyItem}">
			<h3 class="ellipsis productName" title="${orderEntry.articleDescription}">${orderEntry.articleDescription}</h3>
		</c:when>
		<c:otherwise>
			<h3 class="ellipsis productName" title="${product.name}"><a href="${product.url}">${product.name}</a></h3>
		</c:otherwise>
	</c:choose>
	<div class="cell-info-table">
		<div class="cell-info-cell">
			<div class="hd"><spring:message code="cart.list.articleNumber" /></div>
			<div class="bd ellipsis">  <formatArticle:articleNumber articleNumber="${product.codeErpRelevant}"  />   </div>
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
	<div class="cell-info-cell reference">
		<div class="hd"><spring:message code="cart.list.reference" /></div>
		<div class="bd ellipsis" title="<c:out value='${orderEntry.customerReference}' />"><c:out value="${orderEntry.customerReference}" /></div>
	</div>
</div>

<c:choose>
	<c:when test="${isDummyItem}">
		<div class="cell cell-numeric"></div>
	</c:when>
	<c:otherwise>
		<div class="cell cell-numeric">
			<span class="cell-date">
				<c:if test="${currentSalesOrg.erpSystem eq 'SAP' and orderData.status.code ne 'ERP_STATUS_CANCELLED'}">
					<spring:message code="cart.list.deliverydate" />
				</c:if>
			</span>
			<c:set var="pendingQuantity" value="0" />
			<spring:message var="dateFormat" code="text.store.dateformat" text="yyyy-MM-dd" />
			<c:if test="${not empty orderEntry.deliveryDate}">
				<p class="date delivered">
					<fmt:formatDate value="${orderEntry.deliveryDate}" dateStyle="long" timeStyle="short" pattern="${dateFormat}" />
				</p>
			</c:if>
			<c:if test="${not empty orderEntry.availabilities}">
				<spring:message code="cart.list.deliverydate.estimated" var="sEstimated" text="Estimated Delivery Date" />
				<c:forEach items="${orderEntry.availabilities}" var="avail">
					<c:set var="pendingQuantity" value="${pendingQuantity + avail.quantity}"/>
					<p class="date estimated" title="${sEstimated}">
						<fmt:formatDate value="${avail.estimatedDate}" dateStyle="long" timeStyle="short" pattern="${dateFormat}" />
					</p>
				</c:forEach>
			</c:if>
		</div>
		<div class="cell cell-numeric">
			<span class="cell-qty">
				<spring:message code="cart.list.quantity" />
			</span>
			<c:choose>
				<c:when test="${isOpenOrder}">
					<p class="qty">${orderEntry.quantity}</p>
				</c:when>
				<c:otherwise>
					<c:if test="${not empty orderEntry.deliveryDate}">
						<p class="qty delivered">${orderEntry.quantity - pendingQuantity}</p>
					</c:if>
					<c:if test="${pendingQuantity gt 0}">
						<c:forEach items="${orderEntry.availabilities}" var="avail">
							<p class="qty">(${avail.quantity})</p>
						</c:forEach>
					</c:if>
				</c:otherwise>
			</c:choose>
		</div>
	</c:otherwise>
</c:choose>

<div id="${product.code}_price" class="cell cell-price cell-price-first">
	<div class="price-box left">
		<div class="price-wrapper">
			<c:set var="price" value="${orderEntry.totalPrice}" />
			<span class="price-currency">${price.currencyIso}</span>
			<span class="price" data-json="price"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
		</div>
	</div>
</div>
<div id="${product.code}_price" class="cell cell-price">
	<div class="price-box right">
		<div class="price-wrapper">
			<c:set var="price" value="${orderEntry.totalAfterDiscountPrice}" />
			<span class="price-currency">${price.currencyIso}</span>
			<span class="price" data-json="totalPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
		</div>
	</div>
</div>
