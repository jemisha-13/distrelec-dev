<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="product" value="${orderEntry.product}" />
<c:set var="entryNumber">${orderEntry.entryNumber}</c:set>

<fmt:parseNumber var="requestedQty" integerOnly="true"
                 type="number" value="${orderEntry.quantity}" />
<fmt:parseNumber var="backOrderQty" integerOnly="true"
                 type="number" value="${orderEntry.backOrderedQuantity}" />

<c:set var="hasAlternate">${orderEntry.alternateAvailable}</c:set>
<c:set var="requestedQty">${orderEntry.quantity}</c:set>

<spring:message code="backorder.item.btn.alternative" var="sAlternativeBtn" text="Show alternative items" />
<spring:message code="backorder.item.modal.close" var="sAlternativeBtnHide" text="Hide alternative items" />
<spring:message code="backorder.item.manufacturer" var="sManufacturer" text="Manufacturer" />
<spring:message code="rma.guest.returnPage.formQuantity" var="sQty" text="Quantity" />
<spring:message code="cart.list.typeName" var="sMPN" text="MPN"/>
<spring:message code="backorder.item.message" var="sMessage" text="Product Not In Stock"/>
<spring:message code="backorder.item.article" var="sArticleNumber" text="Article Number" />
<spring:message code="backorder.item.message.noAlternate" var="sBackOrderMessage" text="Sorry, but this product is not in stock" />
<spring:message code="backorder.item.message.requested.noAlternate" arguments="${requestedQty},${backOrderQty}" var="sAdjustedMessageNo" />
<spring:message code="backorder.item.message.requested.alternate" arguments="${requestedQty},${backOrderQty}" var="sAdjustedMessage" />

<input value="${product.codeErpRelevant}" type="hidden" class="product-code">

<div class="mod-back-order-item__message">
    <div class="mod-back-order-item__message__icon">
        <i class="fas fa-exclamation-circle"></i>
    </div>
    <div class="mod-back-order-item__message__text">
	<c:set var = "alternateProductUrl" value = "/checkout/backorderDetails/getAlternateProductsForBackOrder/${product.codeErpRelevant}/${orderEntry.quantity}"/>
        <c:choose>
            <c:when test="${(backOrderQty == 0) and (hasAlternate eq true)}">
                ${sMessage}
                <c:set var="qtyValue" value="${orderEntry.quantity}"/>
            </c:when>
            <c:when test="${(backOrderQty == 0) and (hasAlternate eq false)}">
                ${sBackOrderMessage}
            </c:when>
            <c:when test="${requestedQty >  backOrderQty and hasAlternate eq false}">
                ${sAdjustedMessageNo}
				<c:set var = "alternateProductUrl" value = "/checkout/backorderDetails/getAlternateProductsForBackOrder/${product.codeErpRelevant}/${requestedQty - backOrderQty}"/>
            </c:when>
            <c:when test="${requestedQty >  backOrderQty and hasAlternate eq true}">
                ${sAdjustedMessage}
                <c:set var="qtyValue" value="${requestedQty - backOrderQty}"/>
				<c:set var = "alternateProductUrl" value = "/checkout/backorderDetails/getAlternateProductsForBackOrder/${product.codeErpRelevant}/${requestedQty - backOrderQty}"/>
            </c:when>
        </c:choose>
    </div>
</div>
<div data-entry-number="${entryNumber}" class="mod-back-order-item__content">
    <div class="mod-back-order-item__content__img">
        <a href="${product.url}">
            <c:set var="productImage" value="${product.productImages[0]}"/>
            <c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ? productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
            <c:set var="portraitSmallWebP" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : portraitSmallJpg}"/>
            <c:set var="portraitSmallAlt" value="${not empty productImage.portrait_small_webp.altText ? productImage.portrait_small_webp.altText : not empty productImage.portrait_small.altText == null ? productImage.portrait_small.altText : sImageMissing }"/>
            <picture>
                <source srcset="${portraitSmallWebP}">
                <img class="img-fluid" height="63" width="47" alt="${portraitSmallAlt}" src="${portraitSmallJpg}"/>
            </picture>
        </a>
    </div>
    <div class="mod-back-order-item__content__text">
        <h3>
            ${product.name}
        </h3>
        <div class="text-item">
            <span class="text-item__title">
                ${sManufacturer}:
            </span>
            <span class="text-item__attr">
                ${product.distManufacturer.name}
            </span>
        </div>
        <div class="text-item">
            <span class="text-item__title">
                ${sArticleNumber}:
            </span>
            <span class="text-item__attr parentArtNumber">
                <formatArticle:articleNumber articleNumber="${product.codeErpRelevant}"  />
            </span>
        </div>
        <div class="text-item">
            <span class="text-item__title">
                ${sMPN}:
            </span>
            <span class="text-item__attr">
                ${product.typeName}
            </span>
        </div>
        <div class="text-item">
            <span class="text-item__title">
                ${sQty}:
            </span>
            <span class="text-item__attr itemQty">
                ${backOrderQty}
            </span>
        </div>
    </div>
    <c:if test="${hasAlternate eq true}">
        <div class="mod-back-order-item__content__btn">
            <a data-url="${alternateProductUrl}" data-entry="${entryNumber}" href="#" class="mat-button mat-button--action-blue show-alt ellipsis">
                <span class="btn-show-text">${sAlternativeBtn}</span>
                <span class="btn-hide-text hidden">${sAlternativeBtnHide}</span>
            </a>
        </div>
    </c:if>
</div>
<div class="mod-back-order-item__alternative mod-back-order-item__alternative--${entryNumber}">
    <spring:url value="${commonResourcePath}/images/loader.gif" var="spinnerUrl" />
    <div class="spinnerWrapper" id="spinnerWrapper${entryNumber}">
        <img height="30px" width="30px" id="spinner" src="${spinnerUrl}" alt="spinner"/>
    </div>
    <mod:back-order-alternative-items qtyValue="${qtyValue}" htmlClasses="alternative-items-${entryNumber}" count="${entryNumber}" />
    <div class="hidden" id="hiddenResponse"></div>
    <div class="hidden" id="hiddenResponseParent"></div>
</div>
