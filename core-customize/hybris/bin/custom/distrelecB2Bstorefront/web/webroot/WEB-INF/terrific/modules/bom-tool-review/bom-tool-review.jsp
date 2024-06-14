<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>


<c:set var="unavailableProductsNoAlternative" value="0" />
<c:set var="unavailableProductsWithAlternative" value="0" />

<c:forEach items="${unavailableProducts}" var="unavailableProduct">

    <c:set var="productReferences" value="${unavailableProduct.product.productReferences}"/>

    <c:choose>
        <c:when test="${fn:length(productReferences) gt 0}">
            <c:set var="unavailableProductsWithAlternative" value="${unavailableProductsWithAlternative + 1}" />
        </c:when>
        <c:otherwise>
            <c:set var="unavailableProductsNoAlternative" value="${unavailableProductsNoAlternative + 1}" />
        </c:otherwise>
    </c:choose>

</c:forEach>

<c:set var="noMatchedProductCount" value="${unavailableProductsNoAlternative + fn:length(notMatchingProducts)}" />

<c:set var="mpnDuplicateProductCount" value="${fn:length(duplicateMpnProductList)}" />

<c:set var="totalProducts" value="${fn:length(matchingProducts) + unavailableProductsWithAlternative + fn:length(notMatchingProducts) + mpnDuplicateProductCount }" />
<c:set var="foundProducts" value="${fn:length(matchingProducts) + mpnDuplicateProductCount }" />

<c:choose>
    <c:when test="${loadFile}">

        <div class="loadfile">

            <div class="loadfile__edit-item">
                <h1 class="base">
                    <span class="loadfile__old-filename">${fileName} </span><span class="loadfile__edit-icon"><i class="fas fa-pencil-alt"></i></span>
                </h1>
            </div>

            <div class="loadfile__save-item hidden">
                <input type="text" class="form-control loadfile__new-filename" value="${fileName}" placeholder="${fileName}"/>
                <button class="mat-button mat-button__solid--action-green loadfile__save-item-save"> <spring:message code="lightboxshopsettings.save" /> </button>
                <button class="mat-button mat-button--action-red loadfile__save-item-close"> <spring:message code="base.close" /> </button>
            </div>

        </div>

    </c:when>
    <c:otherwise>
        <h1 class="base">${cmsPage.title}</h1>
    </c:otherwise>
</c:choose>

<h2 class="import-total"><spring:message code="importTool.summaryTitle" arguments="${foundProducts},${totalProducts}" text="We have <b>found {0}</b> out of {1} products you requested." /></h2>

<c:if test="${not empty matchingProducts}">
    <p class="import-success"> <i class="fas fa-check-circle"></i><spring:message code="importTool.successText" arguments="${fn:length(matchingProducts)}" text="{0} products(s) successfully matched!" /></p>
</c:if>
<c:if test="${unavailableProductsWithAlternative > 0}">
    <p class="import-info"> <i class="fas fa-info-circle"></i><spring:message code="bom.matchedalternative.info" arguments="${unavailableProductsWithAlternative}" text="{0} products are not stocked, alternative products have been provided." /></p>

    <c:forEach items="${unavailableProducts}" var="unavailableProduct">

        <c:set var="productReferences" value="${unavailableProduct.product.productReferences}"/>

        <c:if test="${fn:length(productReferences) gt 0}">
            <input class="hidden hiddenUnavailableProducts" data-quantity="${unavailableProduct.quantity}" data-code="${unavailableProduct.productCode}" data-customerreference="${unavailableProduct.reference}"/>
        </c:if>

    </c:forEach>

</c:if>
<c:if test="${mpnDuplicateProductCount > 0}">
    <p class="import-info"> <i class="fas fa-info-circle"></i><spring:message code="bom.mpnduplicatesummary.info" arguments="${mpnDuplicateProductCount}" text="{0} products are not stocked, alternative products have been provided." /></p>

    <c:if test="${not empty duplicateMpnProductList}">

        <c:forEach items="${duplicateMpnProductList}" var="unavailableProduct">
            <input class="hidden hiddenDuplicateMpnProducts" data-quantity="${unavailableProduct.quantity}" data-code="${unavailableProduct.mpn}" data-customerreference="${unavailableProduct.reference}"/>
        </c:forEach>

    </c:if>

</c:if>
<c:if test="${quantityAdjustedCount > 0}">
    <div class="showhidetoggle import-red">

        <span class="showhidetoggle__header">
            <i class="fas fa-times-circle"></i><spring:message code="importTool.stockLevel" arguments="${quantityAdjustedCount}" text="{0} product(s) we've reduced the quantity to match available stock levels." />
            <span class="showhidetoggle__header-link">
                <span class="show"><spring:message code="text.show" /></span>
                <span class="hide"><spring:message code="text.hide" /></span>
            </span>
        </span>

        <div class="showhidetoggle__content">

            <span class="showhidetoggle__label">( <spring:message code="import-tool.matching.options.quantity" />, <spring:message code="import-tool.matching.options.distArticleNumber" />, <spring:message code="bomnomatches.itemReference" /> ) </span>

            <ul class="showhidetoggle__content-list">
                <c:forEach items="${adjustedQuantityProducts}" var="matchingProduct" varStatus="status">
                    <li>

                        <span class="showhidetoggle__label">
                            ${matchingProduct.quantity}, <b>${matchingProduct.product.code} </b> / ${matchingProduct.product.typeName}, ${matchingProduct.reference}
                        </span>

                    </li>
                </c:forEach>
            </ul>

        </div>

    </div>
</c:if>

<c:if test="${fn:length(notMatchingProducts) > 0}">

    <div id="showhidetoggle" class="showhidetoggle import-red">

        <span class="showhidetoggle__header">
            <i class="fas fa-times-circle"></i><spring:message code="bom.notmatched.info" arguments="${fn:length(notMatchingProducts)}" text="{0} products could not be matched and no alternatives could be provided." />
            <span class="showhidetoggle__header-link">
                <span class="show"><spring:message code="text.show" /></span>
                <span class="hide"><spring:message code="text.hide" /></span>
            </span>
        </span>

        <div class="showhidetoggle__content">
            <spring:message code="bom.notmatched.message.info" />
            </br>
            <span class="showhidetoggle__label">( <spring:message code="import-tool.matching.options.quantity" />, <spring:message code="import-tool.matching.options.distArticleNumber" />, <spring:message code="bomnomatches.itemReference" /> ) </span>

            <ul class="showhidetoggle__content-list">
                <c:forEach items="${notMatchingProducts}" var="notMatchingProduct">
                    <li>

                        <span class="showhidetoggle__label">

                            <c:if test="${not empty notMatchingProduct.quantity}">${notMatchingProduct.quantityRaw} </c:if>
                            <c:if test="${not empty notMatchingProduct.productCode}">, ${notMatchingProduct.productCode} </c:if>
                            <c:if test="${not empty notMatchingProduct.reference}">, ${notMatchingProduct.reference} </c:if>

                            <input class="hidden hiddenNotMathchedProducts" data-quantity="${notMatchingProduct.quantity}" data-code="${notMatchingProduct.productCode}" data-customerreference="${notMatchingProduct.reference}"/>

                        </span>

                    </li>
                </c:forEach>
            </ul>

            <a href="#" class="showhidetoggle__content-link hidden"> <i class="fas fa-angle-right"></i></a>

        </div>

    </div>

</c:if>

<input type="hidden" id="bomResults" data-requested="${totalProducts}" data-matched="${foundProducts}" data-alternatives="${unavailableProductsWithAlternative}" data-notFound="${fn:length(notMatchingProducts)}">

<input type="hidden" name="fileName" id="bomfileName" value="${fileName}">
<input type="hidden" name="customerId" id="bomcustomerId" value="${customerId}">

<cms:slot var="feature" contentSlot="${slots.Content}">
    <cms:component component="${feature}" />
</cms:slot>