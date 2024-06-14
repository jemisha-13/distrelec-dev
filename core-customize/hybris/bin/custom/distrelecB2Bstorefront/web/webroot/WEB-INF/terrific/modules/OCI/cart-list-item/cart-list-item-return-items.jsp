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
    <input type="hidden" class="hidden-product-code" name="orderItems[${loopNumber}].articleNumber" value="${product.code}"/>
    <input type="hidden" class="hidden-entry-number" value="${orderEntry.entryNumber}"/>
    <input type="hidden" class="hidden-entry-number" name="orderItems[${loopNumber}].itemNumber" value="${orderEntry.itemPosition}"/>
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

<div id="reasonBox-${product.codeErpRelevant}" class="reasonBox-${product.codeErpRelevant} return-reasons-holder">
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
            <div class="return-reasons-holder__item">
                <label for="return-reason-${product.codeErpRelevant}"><spring:message code="cart.return.items.return.reason"/></label>

                <form:select path="orderItems[${loopNumber}].returnReasonID" id="return-reason-${product.codeErpRelevant}" cssClass="return-reason return-reason-${product.codeErpRelevant}">
                    <form:option value=""><spring:message code="cart.return.items.return.reason.ps"/></form:option>
                    <c:forEach items="${orderData.returnReason}" var="returnReason">
                        <spring:eval expression="@messageSource.getMessage('cart.return.items.return.reason.desc.'+returnReason.returnReasonId,null, T(java.util.Locale).ENGLISH)" var="returnReasonEn" />
                        <form:option value="${returnReason.returnReasonId}"
                                     data-aaorder-id="${orderData.code}"
                                     data-aaproduct-id="${product.code}"
                                     data-aareason="${returnReasonEn}"
                                     data-aalocation="return items">
                            <spring:message code="cart.return.items.return.reason.desc.${returnReason.returnReasonId}"/>
                        </form:option>
                    </c:forEach>
                    <%--<form:options items="${orderData.returnReason}" itemValue="returnReasonId" itemLabel="returnReasonId"></form:options>--%>
                </form:select>
            </div>
            <div class="return-reasons-holder__item default-holder">
                <label for="quantity"><spring:message code="cart.return.items.quantity"/></label>
                <form:input id="quantity"
                            type="text"
                            path="orderItems[${loopNumber}].quantity"
                            class="quantity-${product.codeErpRelevant}"
                            data-max-quantity="${returnData.remainingReturnQty}"
                            cssStyle="width: 50px;height: 25px;font-size:14px"
                            cssErrorClass="error"
                            data-aafield="quantity returned"
                            data-aaorder-id="${orderData.code}"
                            data-aaproduct-id="${product.code}"
                            data-aalocation="return items"/>
                <p class="select-qty">/ <span class="select-qty-number">${returnData.remainingReturnQty}</span> <span class="select-qty-text select-qty-text-${loopNumber + 1}">${sReturnSelectMax}</span></p>
            </div>
            <div class="return-reasons-holder__item default-holder">
                <label for="comment">${sReturnComments}</label>
                <form:textarea path="orderItems[${loopNumber}].customerText" id="comment" rows="3" cols="28" cssClass="comment-${product.codeErpRelevant}"/>
            </div>
            <div class="return-reasons-holder__item credit-holder">
                <label class="ellipsis" for="replacement"><spring:message code="cart.return.items.replacement"/> *</label>
                <span class="replacement-radio-buttons replacement-radio-buttons--${fn:toUpperCase(currentLanguage.isocode)}">
                    <form:radiobutton path="orderItems[${loopNumber}].refundType"
                                      value="1"
                                      id="replacement-yes-${product.codeErpRelevant}"
                                      cssClass="replacement-${product.codeErpRelevant}"
                                      data-aafield="replacement option"
                                      data-aaorder-id="${orderData.code}"
                                      data-aaproduct-id="${product.code}"
                                      data-aalocation="return items" />
					<label for="replacement-yes-${product.codeErpRelevant}"><spring:message code="cart.return.items.replacement.newItem"/></label>
                    <form:radiobutton path="orderItems[${loopNumber}].refundType"
                                      value=""
                                      id="replacement-no-${product.codeErpRelevant}"
                                      cssClass="no-replace replacement-${product.codeErpRelevant}"
                                      data-aafield="replacement option"
                                      data-aaorder-id="${orderData.code}"
                                      data-aaproduct-id="${product.code}"
                                      data-aalocation="return items" />
					<label for="replacement-no-${product.codeErpRelevant}"><spring:message code="cart.return.items.replacement.credit"/></label>
				</span>
            </div>
        </c:otherwise>
    </c:choose>
</div>