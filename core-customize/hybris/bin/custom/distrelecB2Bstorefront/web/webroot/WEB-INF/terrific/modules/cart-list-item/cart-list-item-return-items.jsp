<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="product" value="${orderEntry.product}"/>
<c:set var="returnData" value="${orderEntry.rmaData}"/>
<c:set var="sContactLink" value="/cms/contact" />
<spring:message code="cart.list.reference" var="sCartListReference"/>
<spring:message code="cart.list.quantityOrdered" var="sCartListQuantityOrdered"/>
<spring:message code="cart.return.items.return.reason.selectMax" var="sReturnSelectMax"/>
<spring:message code="cart.return.items.comment" var="sReturnComments"/>


<c:if test="${returnData.notAllowed=='false'}">
    <%-- Used by cart-list module --%>
    <input type="hidden" class="hidden-product-code js-rma-field-attr-name" data-name="orderItems[${loopNumber}].articleNumber" value="${product.code}"/>
    <input type="hidden" class="hidden-entry-number" value="${orderEntry.entryNumber}"/>
    <input type="hidden" class="hidden-entry-number js-rma-field-attr-name" data-name="orderItems[${loopNumber}].itemNumber" value="${orderEntry.itemPosition}"/>
    <%-- End cart-list module --%>
</c:if>


<div class="return-items-holder">
    <div class="return-items-holder__item image-item">
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
                    <img class="img-fluid" alt="${portraitSmallAlt}" src="${portraitSmallJpg}"/>
                </picture>
            </a>
        </div>
    </div>
    <div class="return-items-holder__item">
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
                <div class="hd"><spring:message code="cart.list.articleNumber"/></div>
                <div class="bd ellipsis" title="${product.codeErpRelevant}">${product.codeErpRelevant}</div>
            </div>
            <c:if test="${not empty product.typeName || not empty product.distManufacturer.name}">
                <div class="cell-info-cell">
                    <div class="hd"><spring:message code="cart.list.typeName"/></div>
                    <div class="bd ellipsis" title="<c:out value="${product.typeName}" />">${product.typeName}</div>
                </div>
                <div class="cell-info-cell">
                    <div class="hd"><spring:message code="cart.list.manufacturer"/></div>
                    <div class="bd ellipsis" title="<c:out value="${product.distManufacturer.name}" />">${product.distManufacturer.name}</div>
                </div>
            </c:if>
            <div class="cell-info-cell">
                <div class="hd">${sCartListReference}</div>
                <div class="bd ellipsis" title="<c:out value='${orderEntry.customerReference}' />"><c:out value="${orderEntry.customerReference}"/></div>
            </div>
        </div>
    </div>
    <div class="return-items-holder__item qty-item">
        <p class="qty"><span>${sCartListQuantityOrdered}:</span>${orderEntry.quantity}</p>
    </div>
</div>

<div id="reasonBox-${product.codeErpRelevant}" class="reasonBox-${product.codeErpRelevant} return-reasons-holder js-rma-reasons">
    <c:if test="${not empty orderEntry.rmaData.rmas}">
        <div class="return-reasons-holder__returned">
            <p><spring:message code='rma.returnAlreadySubmitted'/></p>
            <c:forEach items="${orderEntry.rmaData.rmas}" var="rmaData" varStatus="status">
                <div class="rma-holder">
                    <p class="returnID"><spring:message code='rma.returnID' arguments='${rmaData.rmaNumber}'/>
                        <c:if test="${not empty rmaData.rmaItemStatus}">
                            (<spring:message code="rma.status.${rmaData.rmaItemStatus}" arguments='${rmaData.rmaNumber}'/>)
                        </c:if>
                    </p>
                    <span class="hidden-address hidden-address-${status.index + 1}">${rmaData.officeAddress}</span>
                    <span class="hidden-rmaNumber hidden-rmaNumber-${status.index + 1}">${rmaData.rmaNumber}</span>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${returnData.notAllowed=='true' or not orderData.validForReturn}">
            <c:if test="${empty orderEntry.rmaData.rmas}">
                <div class="return-reasons-holder__returned">
                    <p class="error-date"><spring:message code='rma.returnDateIssue' arguments='${sContactLink}'/></p>
                </div>
            </c:if>
        </c:when>
        <c:otherwise>
            <div class="return-reasons-holder__item js-rma-reasons-item js-is-main-reason">
                <label for="return-reason-${product.codeErpRelevant}"><spring:message code="cart.return.items.return.reason"/></label>

                <div class="p-relative">
                    <select class="selectpicker ux-selectpicker js-selectpicker js-rma-main-reason" name="">
                        <option value=""><spring:message code="cart.return.items.return.reason.ps"/></option>

                        <c:forEach items="${orderData.returnReason}" var="returnReasonMain">
                            <c:set var="englishTranslation">
                                <spring:eval expression="@messageSource.getMessage('${returnReasonMain.mainReasonText}' ,null, T(java.util.Locale).ENGLISH)" />
                            </c:set>

                            <c:set var="returnReasonSubCodes">
                                <c:forEach items="${returnReasonMain.subReasons}" var="returnReasonSub">${returnReasonSub.subReasonId}|</c:forEach>
                            </c:set>

                            <c:if test="${not empty returnReasonMain.defaultSubReasonId}">
                                <c:set var="returnReasonSubCodes" value="${returnReasonMain.defaultSubReasonId}" />
                            </c:if>

                            <option value="${returnReasonSubCodes}"
                                    data-english-translation="${englishTranslation}"><spring:message code="${returnReasonMain.mainReasonText}"/></option>
                        </c:forEach>
                    </select>

                    <i class="ux-selectpicker__angle-down fa fa-angle-down"></i>
                </div>
            </div>

            <div class="return-reasons-holder__item js-rma-reasons-item hidden js-is-sub-reason">
                <div class="return-reasons-holder__item__fields p-relative">

                    <select id="return-reason-${product.codeErpRelevant}"
                            class="return-reason return-reason-${product.codeErpRelevant} selectpicker ux-selectpicker js-selectpicker js-rma-sub-reason">
                        <option value=""><spring:message code="cart.return.items.return.reason.ps"/></option>

                        <c:forEach items="${orderData.returnReason}" var="returnReasonMain">
                            <c:forEach items="${returnReasonMain.subReasons}" var="returnReasonSub">
                                <c:forEach items="${returnReasonSub.subReasonMessages}" var="returnReasonSubItems" varStatus="loop">
                                    <c:set var="englishTranslation">
                                        <spring:eval expression="@messageSource.getMessage('${returnReasonSubItems}' ,null, T(java.util.Locale).ENGLISH)" />
                                    </c:set>

                                    <option value="${returnReasonSub.subReasonId}_${loop.index + 1}"
                                                 data-code="${returnReasonSub.subReasonId}"
                                                 data-aaorder-id="${orderData.code}"
                                                 data-aaproduct-id="${product.code}"
                                                 data-aareason="${englishTranslation}"
                                                 data-english-translation="${englishTranslation}"
                                                 data-aalocation="return items">
                                        <spring:message code="${returnReasonSubItems}"/>
                                    </option>
                                </c:forEach>
                            </c:forEach>
                        </c:forEach>
                    </select>

                    <form:hidden class="js-reason-for-BE-id js-rma-field-attr-name" data-name="orderItems[${loopNumber}].returnReasonID" path="orderItems[${loopNumber}].returnReasonID" />
                    <form:hidden class="js-reason-for-BE-subreason js-rma-field-attr-name" data-name="orderItems[${loopNumber}].returnSubReason" path="orderItems[${loopNumber}].returnSubReason" />

                    <i class="ux-selectpicker__angle-down fa fa-angle-down"></i>
                </div>
            </div>

            <div class="return-reasons-holder__item is-qty js-rma-reasons-item js-is-qty hidden">
                <label for="quantity-${product.codeErpRelevant}"><spring:message code="cart.return.items.quantity"/></label>

                <div class="return-reasons-holder__item__fields">
                    <form:input id="quantity-${product.codeErpRelevant}"
                                type="number"
                                path="orderItems[${loopNumber}].quantity"
                                data-name="orderItems[${loopNumber}].quantity"
                                class="js-rma-qty js-rma-field-attr-name"
                                value="1"
                                min="1"
                                max="${returnData.remainingReturnQty}"
                                data-max-quantity="${returnData.remainingReturnQty}"
                                cssErrorClass="error"
                                data-aafield="quantity returned"
                                data-aaorder-id="${orderData.code}"
                                data-aaproduct-id="${product.code}"
                                data-aalocation="return items"/>
                    <p class="select-qty">/<span class="select-qty-number">${returnData.remainingReturnQty}</span>&nbsp;<span
                            class="select-qty-text js-rma-qty-select-max">${sReturnSelectMax}</span></p>
                </div>
            </div>

            <div class="return-reasons-holder__item is-comment js-rma-reasons-item js-is-comment js-char-counter hidden">
                <label for="comment-${product.codeErpRelevant}">${sReturnComments}</label>
                <div class="return-reasons-holder__item__fields">
                    <div class="return-reasons-holder__item__comment">
                        <c:set var="commentMaxLength" value="50" />
                        <form:textarea path="orderItems[${loopNumber}].customerText" id="comment-${product.codeErpRelevant}" rows="3" cols="28"
                                       data-name="orderItems[${loopNumber}].customerText"
                                       maxlength="${commentMaxLength}"
                                       cssClass="comment-${product.codeErpRelevant} js-char-counter-element js-rma-field-attr-name"/>
                        <div class="return-reasons-holder__item__comment-char-counter js-char-counter-output">0&nbsp;/&nbsp;${commentMaxLength}</div>
                    </div>
                </div>
            </div>

        </c:otherwise>
    </c:choose>
</div>
