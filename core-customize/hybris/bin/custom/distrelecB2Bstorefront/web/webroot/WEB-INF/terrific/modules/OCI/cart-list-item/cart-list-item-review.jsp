<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<spring:message code="cart.image.missing" text="Image not found" var="sImageMissing"/>
<spring:message code="text.store.dateformat.datepicker.selection" var="datePattern" />
<c:set var="product" value="${isSubItem ? subEntry.product : orderEntry.product}" />
<c:set var="itemUrl" value="${product.url}" />
<c:set var="itemNumber">
	<formatArticle:articleNumber articleNumber="${product.codeErpRelevant}"  />
</c:set>
<c:set var="itemName" value="${product.name}" />
<c:choose>
	<c:when test="${isSubItem}">
		<c:set var="itemQuantity" value="${subEntry.orderQuantity}" />
	</c:when>
	<c:otherwise>
		<c:set var="itemQuantity" value="${orderEntry.quantity}" />
	</c:otherwise>
</c:choose>
<c:if test="${not isSubItem}">
	<input type="hidden" class="hidden-product-code" value="${product.code}" />
	<input type="hidden" class="hidden-entry-number" value="${orderEntry.entryNumber}" />
</c:if>

<div class="col-6 col-lg-1">
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
</div>

<div class="col-6 col-lg-2 product">
	<c:choose>
		<c:when test="${isDummyItem}">
			<h3 title="${orderEntry.articleDescription}">${orderEntry.articleDescription}</h3>
		</c:when>
		<c:otherwise>
			<h3 title="${itemName}">${itemName}</h3>
		</c:otherwise>
	</c:choose>
	<c:if test="${not empty orderEntry.customerReference}">
		${orderEntry.customerReference}
	</c:if>
</div>

<div class="col-12 col-lg-2 manufacturer">
	<div class="label">Manufacturer:</div>
	<c:if test="${not empty product.distManufacturer.name}">
		${product.distManufacturer.name}
	</c:if>
</div>

<div class="col-12 col-lg-2 mpn">
	<div class="label">Art.Nr.:</div>
	<span>${itemNumber}</span>
	<div class="label">MPN:</div>
	<c:if test="${not empty product.typeName || not empty product.distManufacturer.name}">
		<span>${product.typeName}</span>
	</c:if>
</div>

<div id="${product.code}_availability" class="col-12 col-lg-2 date">
	<c:if test="${not isSubItem && not isDummyItem}">
		<spring:message code="availability.timeInMillisMaxGreen" var="timeInMillisMaxGreen"/>
		<spring:message code="availability.timeInMillisMaxOrange" var="timeInMillisMaxOrange"/>
		<c:set var="numberAvailabilityDisplayed" value="0"/>
		<c:forEach var="availability" items="${orderEntry.availabilities}">
			<c:if test="${numberAvailabilityDisplayed < 4}">
				<div class="label">Delivery Date:</div>
				<c:set var="timeInMillis" value="${availability.estimatedDate.time - todayMidnight.time}" />
				<c:choose>
					<c:when test="${timeInMillis < timeInMillisMaxGreen}">
						<c:set var="classOrder" value="date__green" />
					</c:when>
					<c:when test="${timeInMillis < timeInMillisMaxOrange}">
						<c:set var="classOrder" value="date__orange" />
					</c:when>
					<c:otherwise>
						<c:set var="classOrder" value="date__red" />
					</c:otherwise>
				</c:choose>
				<div class="${classOrder}">
					<fmt:formatDate value="${availability.estimatedDate}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" var="sDate" />
					<span class="qty-on-date">
						<c:if test="${availability.quantity > 0}">
							<spring:message code="availability.quantityOnDate" arguments="${availability.quantity}@${sDate}" argumentSeparator="@" />
						</c:if>
					</span>
					<c:set var="numberAvailabilityDisplayed" value="${numberAvailabilityDisplayed + 1}" />
				</div>
			</c:if>
		</c:forEach>
	</c:if>
</div>

<div class="col-12 col-lg-1 qty">
	<div class="label">Qty:</div>
	${itemQuantity}
</div>

<div id="${product.code}_price" class="col-12 col-lg-1">
	<div class="label">Price:</div>
	<div class="price-box left">
		<div class="price-wrapper">
			<span class="price-currency">${price.currencyIso}</span>
			<span class="price" data-json="price"><format:price format="price" priceData="${orderEntry.basePrice}" displayValue="${orderEntry.basePrice.value}" /></span>
		</div>
	</div>
</div>
<div id="${product.code}_price" class="col-12 col-lg-1">
	<div class="price-box right">
		<div class="price-wrapper">
			<c:set var="price" value="${empty orderEntry.totalPrice ? orderEntry.totalListPrice : orderEntry.totalPrice}" />
			<span class="price" data-json="totalPrice"><format:price format="price" priceData="${price}" displayValue="${price.value}" /></span>
		</div>
	</div>
</div>

<c:if test="${isBOM}">
	<div class="col-12 bundle-product">
		<h4><spring:message code="product.bundle.includes" text="Bundle includes" /></h4>
	</div>
	<c:forEach var="subEntry" items="${orderEntry.subOrderEntryData}" varStatus="subStatus">
		<div class="col-12">
			<mod:cart-list-item tag="div" subEntry="${subEntry}" template="review" skin="review" htmlClasses="row sub-item" position="${subStatus.index + 1}" isSubItem="true"/>
		</div>
	</c:forEach>
</c:if>
