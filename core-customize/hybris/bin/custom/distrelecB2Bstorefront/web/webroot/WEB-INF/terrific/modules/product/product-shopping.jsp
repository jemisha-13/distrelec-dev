<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:message code="cart.image.missing" text="Image not found" var="sImageMissing"/>

<c:set var="product" value="${wishlistEntry.product}" />
<c:set var="phaseOut" value="${(product.buyable == false) and (empty product.endOfLifeDate)}" />
<c:set var="eol" value="${not empty product.endOfLifeDate}" />
<c:set var="eolWithReplacement" value="${not empty product.endOfLifeDate and product.buyableReplacementProduct}" />

<%-- Variable is needed to determine if the product link should be displayed or not --%>
<c:if test="${product.buyable or eolWithReplacement}">
	<c:set var="linkOpeningTag">
		<a href="${product.url}">
	</c:set>
	<c:set var="linkClosingTag">
		</a>
	</c:set>
</c:if>

<%-- Used by product and productlist module --%>
<input type="hidden" class="hidden-product-code" value="${product.code}" />
<input type="hidden" class="hidden-list-id" value="${shoppinglistId}" />

<c:set var="productReference" value="${wishlistEntry.comment == 'undefined' ? ' ' : wishlistEntry.comment}" />
<input type="hidden" class="hidden-product-reference" value="${productReference}" />
<%-- End product and productlist module --%>

<div class="row shopping-list" data-product-url="${product.url}">
	<div class="col-1 shopping-list__form-checkbox">
		<c:if test="${product.buyable and not product.catPlusItem}">
			<form:form action="#" class="select-product-form" method="GET">
				<input id="select-product-${productCounter}" class="select-product checkbox" type="checkbox" value="select-product-${productCounter}" name="checkboxes">
				<label for="select-product-${productCounter}"></label>
			</form:form>
		</c:if>
	</div>
	<div class="col-3 col-md-2 shopping-list__image-container">
		<c:if test="${product.buyable and fn:length(product.activePromotionLabels) > 0}">
			<div class="productlabel-wrap">
				<c:forEach items="${product.activePromotionLabels}" var="promoLabel" end="0">
					<mod:product-label promoLabel="${promoLabel}" />
				</c:forEach>
			</div>
		</c:if>
		<c:set var="productImage" value="${product.productImages[0]}"/>
		<c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ? productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
		<c:set var="portraitSmallWebP" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : portraitSmallJpg}"/>
		<c:set var="portraitSmallAlt" value="${not empty productImage.portrait_small_webp.altText ? productImage.portrait_small_webp.altText : not empty productImage.portrait_small.altText == null ? productImage.portrait_small.altText : sImageMissing }"/>
		<picture>
			<source srcset="${portraitSmallWebP}">
			<img width="83" height="110" alt="${portraitSmallAlt}" src="${portraitSmallJpg}"/>
		</picture>
		<c:if test="${product.buyable}">
			${linkOpeningTag}
			<mod:energy-efficiency-label skin="shopping" product="${product}" />
			${linkClosingTag}
		</c:if>

	</div>

	<div class="col-7 col-md-9 pull-right">

		${linkOpeningTag}
		<div class="shopping-list__top-header">
		${linkClosingTag}



		<div class="list-attribs loading">
			<h3 class="${eol or phaseOut ? ' no-checkbox' : ''} shopping-list__title" title="<c:out value="${product.name}" />">${product.name}</h3>
			<c:set var="key" value="1" />
			<c:forEach items="${product.volumePricesMap}" var="entry" end="0">
				<c:set var="key" value="${entry.key}" />
			</c:forEach>

			<div class="productlist-container">
					<div class="productlist-container__data">
						<span class="label"><spring:message code="product.articleNumber" /></span><span class="value"> <formatArticle:articleNumber articleNumber="${product.codeErpRelevant}" /> </span>
					</div>
					<div class="productlist-container__data">
						<span class="label"><spring:message code="product.typeName" /></span><span class="value">${product.typeName}</span>
					</div>
					<div class="productlist-container__data">
						<span class="label"><spring:message code="product.packing.unit" /></span><span class="value">${product.salesUnit}</span>
					</div>
			</div>

			<div class="num-stepper js-num-stepper" data-product-code="${product.code}">
				<mod:numeric-stepper product="${product}" template="product-list-shopping" skin="product-list-shopping" shoppingListQty="${wishlistEntry.desired}"/>
				<mod:toolsitem template="toolsitem-shoppinglist-remove" skin="shopping-remove" tag="div" productId="${product.code}" listId="${shoppinglistId}" />
			</div>
		</div>

		<c:choose>
				<c:when test="${product.buyable and not product.catPlusItem}">
					<nav class="ctrls">
						<mod:scaled-prices product="${product}" template="product-shopping" skin="product-shopping" />
					</nav>
				</c:when>
				<c:otherwise>
					<nav class="ctrls">
						<span class="read-only-pieces">${wishlistEntry.desired} <spring:message code="product.list.pieces" /></span>
						<mod:toolsitem template="toolsitem-shopping-remove" skin="shopping-remove" tag="div" productId="${product.code}" listId="${shoppinglistId}" />
					</nav>
				</c:otherwise>
		</c:choose>

		<c:if test="${eol}">
			<div class="eol${eolWithReplacement ? ' hasReplacement' : ''}">
				<c:set var="formattedDate">
					<fmt:formatDate value="${product.endOfLifeDate}"/>
				</c:set>
				<p class="eol-message">
					<spring:message code="product.notBuyable.endOfLife.message" arguments="${formattedDate}"/>
					<c:if test="${not empty product.replacementReason}">
					<span class="value">
						<spring:message code="product.notBuyable.endOfLife.reason" arguments="${product.replacementReason}"/>
					</span>
					</c:if>
				</p>
				<a class="btn-secondary" href="${product.url}"><spring:message code="product.notBuyable.endOfLife.alternative"/></a>
			</div>
		</c:if>
		<c:if test="${phaseOut}">
			<div class="phase-out">
				<p class="phase-out-message"><spring:message code="product.notBuyable.temporarly.message"/></p>
			</div>
		</c:if>
		</div>

	</div>
	<span class="product-stock hidden"></span>
</div>
