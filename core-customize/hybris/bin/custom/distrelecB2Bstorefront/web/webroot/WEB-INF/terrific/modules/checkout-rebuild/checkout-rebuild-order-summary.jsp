<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="terrificFormat" tagdir="/WEB-INF/tags/shared/terrific/format" %>

<c:set var="isInter" value="${siteUid eq 'distrelec_FR'}" />
<c:set var="isReviewPay" value="${template eq 'review-pay-order-summary'}"/>
<spring:message code="checkout.rebuild.order.summary.product.delivery.date.message" var="deliveryMessage"/>
<spring:message code="checkout.rebuild.order.summary.product.delivery.date.message.and" var="deliveryMessageAnd"/>
<spring:message code="availability.cart.noStockDate" var="deliveryMessageNoStockDate"/>

<div class="o-cr-order-summary">
    <div class="o-cr-order-summary__head">
        <span id="checkoutOrderSummaryItems" class="fw-m">${totalProducts}&nbsp;<spring:message
                code="metahd.cart.item${(totalProducts > 1) ? 's' : ''}"/></span>

        <a id="checkoutOrderSummaryEditLink" class="ux-link fw-m" href="/cart"><spring:message code="checkout.payment.edit.cartBtn"/></a>
    </div>

    <div class="o-cr-order-summary__products js-cr-product-list-wrapper has-overflow">
        <ul class="o-cr-product-list js-cr-product-list" data-delivery-msg="${deliveryMessage}"
            data-delivery-and-msg="${deliveryMessageAnd}" data-delivery-msg-no-stock="${deliveryMessageNoStockDate}">
            <c:forEach items="${cartData.entries}" var="entry">
                <c:set var="isQuoteItem" value="${not empty entry.quotationId}"/>
                <c:set var="isBOM" value="${entry.bom}"/>
                <c:url value="${entry.product.url}" var="productUrl"/>

                <c:if test="${isQuoteItem}">
                    <li class="o-cr-product-list__item is-quote js-cr-product-list-item">
                        <article class="o-cr-product-item is-quote">
                            <div class="o-cr-product-item__body">
                                <div class="o-cr-product-item__content">
                                    <h2 id="checkoutOrderSummaryProductQuoteLabel_${entry.product.codeErpRelevant}" class="o-cr-product-item__title">
                                        <spring:message code="text.quote"/>
                                    </h2>

                                    <ul class="o-cr-product-item__attr-list">
                                        <li class="o-cr-product-item__attr-list__item">
                                            <strong id="checkoutOrderSummaryProductQuoteNumber_${entry.product.codeErpRelevant}"><spring:message code="cart.list.quoteNumber"/></strong>
                                            <span id="checkoutOrderSummaryProductQuoteId_${entry.product.codeErpRelevant}">${entry.quotationId}</span>
                                        </li>

                                        <c:if test="${not empty orderEntry.quotationReference}">
                                            <li class="o-cr-product-item__attr-list__item">
                                                <strong id="checkoutOrderSummaryProductQuoteReferenceLabel_${entry.product.codeErpRelevant}"><spring:message code="cart.list.quoteReference"/></strong>
                                                <span id="checkoutOrderSummaryProductQuoteReferenceId_${entry.product.codeErpRelevant}"><c:out value="${orderEntry.quotationReference}"/></span>
                                            </li>
                                        </c:if>

                                        <li id="checkoutOrderSummaryProductQuoteIncludes_${entry.product.codeErpRelevant}" class="o-cr-product-item__attr-list__item is-text">
                                            <spring:message code="text.quote.includes"/>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </article>
                    </li>
                </c:if>

                <li class="o-cr-product-list__item ${isBOM ? 'is-bom-main' : ''} js-cr-product-list-item">
                    <article class="o-cr-product-item">
                        <div class="o-cr-product-item__body">
                            <div class="o-cr-product-item__image">
                                <a class="o-cr-product-item__image-link" href="${productUrl}">
                                    <terrificFormat:productImage
                                            imageUrl="${entry.product.productImages[0].portrait_small.url}"
                                            imageAlt="${entry.product.productImages[0].landscape_medium.altText}"
                                            imageWidth="70"
                                            productName="${entry.product.name}"/>
                                </a>
                            </div>

                            <div class="o-cr-product-item__content">
                                <h2 class="o-cr-product-item__title">
                                    <a id="checkoutOrderSummaryProductLink_${entry.product.codeErpRelevant}" href="${productUrl}">${entry.product.name}</a>
                                </h2>

                                <ul class="o-cr-product-item__attr-list">
                                    <li class="o-cr-product-item__attr-list__item">
                                        <strong id="checkoutOrderSummaryProductArticleNumberLabel_${entry.product.codeErpRelevant}"><spring:message code="cart.directorder.articleNumber"/></strong>
                                        <span id="checkoutOrderSummaryProductArticleNumber_${entry.product.codeErpRelevant}"><formatArticle:articleNumber
                                                articleNumber="${entry.product.codeErpRelevant}"/></span>
                                    </li>

                                    <li class="o-cr-product-item__attr-list__item is-half">
                                        <strong id="checkoutOrderSummaryProductArticleQtyLabel_${entry.product.codeErpRelevant}"><spring:message code="cart.directorder.qty"/></strong>
                                        <span id="checkoutOrderSummaryProductArticleQty_${entry.product.codeErpRelevant}">${entry.quantity}</span>
                                    </li>

                                    <li class="o-cr-product-item__attr-list__item is-half">
                                        <strong id="checkoutOrderSummaryProductCurrency_${entry.product.codeErpRelevant}"><terrificFormat:price format="currency" priceData="${entry.basePrice}"
                                                                      fallBackCurrency="${entry.basePrice.currencyIso}"/></strong>
                                        <span id="checkoutOrderSummaryProductPrice_${entry.product.codeErpRelevant}"><terrificFormat:price format="price" explicitMaxFractionDigits="2"
                                                                    priceData="${entry.basePrice}"/></span>
                                    </li>
                                </ul>
                            </div>
                        </div>

                        <div class="o-cr-product-item__footer">
                            <c:set var="entryProduct" value="${entry.product}"/>
                            <c:if test="${(entryProduct.itemCategoryGroup != 'BANS') and (entryProduct.itemCategoryGroup != 'BANC')}">
                                <c:if test="${not empty entry}">
                                    <ul class="o-cr-product-item__attr-list">
                                        <c:choose>
                                            <c:when test="${not empty entry.availabilities}">
                                                <c:set var="deliveryDatesText" value=""/>

                                                <c:forEach var="deliveryDate"
                                                           items="${entry.availabilities}"
                                                           varStatus="index">
                                                    <c:if test="${not empty deliveryDate.formattedEstimatedDate && deliveryDate.quantity > 0}">
                                                        <c:choose>
                                                            <c:when test="${index.last}">
                                                                <c:set var="deliveryDatesText">
                                                                    ${deliveryDatesText}&nbsp;<strong>${deliveryDate.formattedEstimatedDate}</strong>.
                                                                </c:set>
                                                            </c:when>

                                                            <c:otherwise>
                                                                <c:set var="deliveryDatesText">
                                                                    ${deliveryDatesText}&nbsp;<strong>${deliveryDate.formattedEstimatedDate}</strong>&nbsp;${deliveryMessageAnd}
                                                                </c:set>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                </c:forEach>

                                                <li class="o-cr-product-item__attr-list__item is-availability-message">
                                                    <div id="checkoutOrderSummaryProductDeliveryMsg_${entry.product.codeErpRelevant}"
                                                         class="js-product-item-delivery-message-${entry.product.codeErpRelevant}">${deliveryMessage}${deliveryDatesText}</div>
                                                </li>
                                            </c:when>

                                            <c:otherwise>
                                                <li id="checkoutOrderSummaryProductDeliveryMsgNoStock_${entry.product.codeErpRelevant}" class="o-cr-product-item__attr-list__item">${deliveryMessageNoStockDate}</li>
                                            </c:otherwise>
                                        </c:choose>
                                    </ul>
                                </c:if>
                            </c:if>
                        </div>
                    </article>
                </li>

                <c:if test="${isBOM}">
                    <li class="o-cr-product-list__item is-bom js-cr-product-list-item">
                        <article class="o-cr-product-item is-bom">
                            <div class="o-cr-product-item__body">
                                <div class="o-cr-product-item__content">
                                    <h2 id="checkoutOrderSummaryProductBomTitle_${entry.product.codeErpRelevant}" class="o-cr-product-item__title">
                                        <spring:message code="checkout.rebuild.review.pay.product.bom.title"/>
                                    </h2>

                                    <ul class="o-cr-product-item__attr-list">
                                        <li id="checkoutOrderSummaryProductBomBundle_${entry.product.codeErpRelevant}" class="o-cr-product-item__attr-list__item is-text">
                                            <spring:message code="product.bundle.includes"/>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </article>
                    </li>

                    <c:forEach var="subEntry" items="${entry.subOrderEntryData}" varStatus="loop">
                        <c:url value="${subEntry.product.url}" var="subEntryProductUrl"/>

                        <li class="o-cr-product-list__item ${not loop.last ? 'is-bom-sub' : ''} js-cr-product-list-item">
                            <article class="o-cr-product-item ${not loop.last ? 'is-bom-sub' : ''}">
                                <div class="o-cr-product-item__body">
                                    <div class="o-cr-product-item__image">
                                        <a class="o-cr-product-item__image-link" href="${subEntryProductUrl}">
                                            <terrificFormat:productImage
                                                    imageUrl="${subEntry.product.productImages[0].portrait_small.url}"
                                                    imageAlt="${subEntry.product.productImages[0].landscape_medium.altText}"
                                                    imageWidth="70"
                                                    productName="${subEntry.product.name}"/>
                                        </a>
                                    </div>

                                    <div class="o-cr-product-item__content">
                                        <h2 class="o-cr-product-item__title">
                                            <a id="checkoutOrderSummarySubProductLink_${entry.product.codeErpRelevant}" href="${subEntryProductUrl}">${subEntry.product.name}</a>
                                        </h2>

                                        <ul class="o-cr-product-item__attr-list">
                                            <li class="o-cr-product-item__attr-list__item">
                                                <strong id="checkoutOrderSummarySubProductArticleNumberLabel_${entry.product.codeErpRelevant}"><spring:message code="cart.directorder.articleNumber"/></strong>
                                                <span id="checkoutOrderSummarySubProductArticleNumber_${entry.product.codeErpRelevant}"><formatArticle:articleNumber
                                                        articleNumber="${subEntry.product.codeErpRelevant}"/></span>
                                            </li>

                                            <li class="o-cr-product-item__attr-list__item is-half">
                                                <strong id="checkoutOrderSummarySubProductQtyLabel_${entry.product.codeErpRelevant}"><spring:message code="cart.directorder.qty"/></strong>
                                                <span id="checkoutOrderSummarySubProductQty_${entry.product.codeErpRelevant}">${entry.quantity}</span>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </article>
                        </li>
                    </c:forEach>
                </c:if>
            </c:forEach>
        </ul>
    </div>

    <div class="o-cr-order-summary__totals js-cr-totals">
        <div class="js-cr-totals-template">
            <ul class="o-cr-order-summary-totals">
                <li class="o-cr-order-summary-totals__item">
                    <div id="checkoutOrderSummarySubtotalLabel"><spring:message code="cart.pricecalcbox.subtotal"/></div>
                    <div id="checkoutOrderSummarySubtotal">
                        <div class="o-cr-order-summary-totals__item-price">
                            <div id="checkoutOrderSummarySubtotal_currency"><terrificFormat:price format="currency" priceData="${cartData.subTotal}"/></div>
                            <div id="checkoutOrderSummarySubtotal_price"><terrificFormat:price format="price" priceData="${cartData.subTotal}"/></div>
                        </div>
                    </div>
                </li>

                <c:set var="totalDiscounts" value="${cartData.totalDiscounts}"/>
                <c:if test="${not empty totalDiscounts and totalDiscounts.value gt 0}">
                    <li class="o-cr-order-summary-totals__item">
                        <div id="checkoutOrderSummaryDiscountLabel"><spring:message code="cart.pricecalcbox.discount"/></div>
                        <div id="checkoutOrderSummaryDiscount">
                            <div class="o-cr-order-summary-totals__item-price">
                                <div id="checkoutOrderSummaryDiscount_currency"><terrificFormat:price format="currency" priceData="${totalDiscounts}"/></div>
                                <div id="checkoutOrderSummaryDiscount_price"><terrificFormat:price format="price" priceData="${totalDiscounts}"/></div>
                            </div>
                        </div>
                    </li>
                </c:if>

                <c:if test="${not empty cartData.deliveryMode and not cartData.deliveryCostExcluded}">
                    <li class="o-cr-order-summary-totals__item">
                        <div id="checkoutOrderSummaryDeliveryLabel"><spring:message code="checkout.deliveryPage.title"/> </div>
                        <div id="checkoutOrderSummaryDelivery">
                            <c:choose>
                                <c:when test="${cartData.deliveryCost.value gt 0}">
                                    <div class="o-cr-order-summary-totals__item-price">
                                        <div id="checkoutOrderSummaryDelivery_currency"><terrificFormat:price format="currency" priceData="${cartData.deliveryCost}"/></div>
                                        <div id="checkoutOrderSummaryDelivery_price"><terrificFormat:price format="price" priceData="${cartData.deliveryCost}"/></div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <spring:message code="checkout.summary.deliveryCost.free"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </li>
                </c:if>

                <c:if test="${not empty cartData.erpVoucherInfoData and empty cartData.erpVoucherInfoData.erpErrorMessageKey and cartData.erpVoucherInfoData.valid}">
                    <li class="o-cr-order-summary-totals__item">
                        <div id="checkoutOrderSummaryVoucherLabel"><spring:message code="cart.pricecalcbox.voucher"/></div>
                        <div id="checkoutOrderSummaryVoucher">
                            <div class="o-cr-order-summary-totals__item-price">
                                <div id="checkoutOrderSummaryVoucher_currency"><terrificFormat:price format="currency" priceData="${cartData.erpVoucherInfoData.fixedValue}"/></div>
                                <div id="checkoutOrderSummaryVoucher_price">&ndash;&nbsp;<terrificFormat:price format="price" priceData="${cartData.erpVoucherInfoData.fixedValue}"/></div>
                            </div>
                    </li>
                </c:if>

                <li class="o-cr-order-summary-totals__item">
                    <div id="checkoutOrderSummaryTaxLabel"><spring:message code="cart.pricecalcbox.tax"/></div>
                    <div id="checkoutOrderSummaryTax">
                        <div class="o-cr-order-summary-totals__item-price">
                            <div id="checkoutOrderSummaryTax_currency"><terrificFormat:price format="currency" priceData="${cartData.totalTax}"/></div>
                            <div id="checkoutOrderSummaryTax_price"><terrificFormat:price format="price" priceData="${cartData.totalTax}"/></div>
                        </div>
                    </div>
                </li>

                <li class="o-cr-order-summary-totals__item is-total">
                    <div id="checkoutOrderSummaryTotalLabel"><spring:message code="order.total"/></div>
                    <div id="checkoutOrderSummaryTotal">
                        <div class="o-cr-order-summary-totals__item-price">
                            <div id="checkoutOrderSummaryTotal_currency"><terrificFormat:price format="currency" priceData="${cartData.totalPrice}"/></div>
                            <div id="checkoutOrderSummaryTotal_price"><terrificFormat:price format="price" priceData="${cartData.totalPrice}"/></div>
                        </div>
                    </div>
                </li>
            </ul>
        </div>

        <div class="o-cr-order-summary__totals-loader js-cr-totals-loader">
            <div class="o-cr-order-summary__totals-loader__content">
                <mod:loading-spinner spinnerID="spinner_order-summary"/>

                <div id="checkoutOrderSummaryRefreshingPrices" class="o-cr-order-summary__totals-loader__text fw-b"><spring:message
                        code="checkout.rebuild.order.summary.refreshing.prices"/></div>
            </div>
        </div>
    </div>

    <c:if test="${isReviewPay}">
        <div>
            <c:if test="${isInter}">
                <mod:summary-terms template="inter" skin="inter" />
            </c:if>
        </div>
    </c:if>

    <div class="o-cr-order-summary__button js-cr-order-summary-cta">
        <c:choose>
            <c:when test="${isReviewPay}">
                <c:choose>
                    <c:when test="${isOrderApprovalLimitExceeded}">
                        <button id="checkoutOrderSummarySubmitApprovalButton" class="ux-btn ux-btn--red is-grey-when-disabled" onclick="location.href='/checkout/review-and-pay/approve/invoice'">
                            <spring:theme code="approvalbar.button.submitForApproval" />
                        </button>
                    </c:when>
                    <c:otherwise>
                        <button id="checkoutOrderSummaryPurchaseButton" class="ux-btn ux-btn--brand-green is-grey-when-disabled js-purchase-button"
                                data-translation-CreditCard="<spring:theme code="checkout.rebuild.review.pay.button.CreditCard"/>"
                                data-translation-PayPal="<spring:theme code="checkout.rebuild.review.pay.button.PayPal"/>"
                                data-translation-Invoice="<spring:theme code="checkout.rebuild.review.pay.button.Invoice"/>"
                                ${isInter or (showCodiceDestinatario and empty codiceVatForm.vat4 and empty codiceVatForm.legalEmail) or empty selectedPaymentOption or (selectedPaymentOption.code eq 'CreditCard' and not selectedCcPaymentInfo.isValid) ? 'disabled="true"' : ''}>
                            <c:choose>
                                <c:when test="${not empty selectedPaymentOption}">
                                    <spring:theme code="checkout.rebuild.review.pay.button.${fn:contains(selectedPaymentOption.code, 'Invoice') ? 'Invoice' : selectedPaymentOption.code}"/>
                                </c:when>
                                <c:otherwise>
                                    <spring:theme code="checkout.payment.title"/>
                                </c:otherwise>
                            </c:choose>
                        </button>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <button id="checkoutOrderSummaryContinueToReviewPayButton" onclick="location.href='/checkout/delivery/continueCheckout'"
                        class="ux-btn ux-btn--brand-green is-grey-when-disabled js-continue-to-review-pay" ${enableContinueButton ? '' : 'disabled="disabled"'}>
                    <spring:message code="checkout.rebuild.order.summary.continue.to.review.and.pay"/>
                </button>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<c:choose>
    <c:when test="${isReviewPay}">
        <div class="o-cr-order-summary__iframe js-cc-form-payment-wrapper">
            <c:url value="/checkout/payment/hiddenPaymentForm" var="hiddenPaymentFormUrl" />
            <iframe id="iframe" class="js-cc-form-payment" src="" data-src1="${hiddenPaymentFormUrl}"></iframe>
            <div class="js-pp-iframe"></div>
        </div>

        <form:form class="js-hidden-invoice-form" action="/checkout/review-and-pay/order/invoice/" method="POST"/>

        <c:if test="${not isInter}">
            <mod:summary-terms />
        </c:if>
    </c:when>
    <c:otherwise>
        <script id="tmpl-order-summary-totals" type="text/x-template-dotjs">
            <ul class="o-cr-order-summary-totals">
                <li class="o-cr-order-summary-totals__item">
                    <div id="checkoutOrderSummarySubtotalLabel"><spring:message code="cart.pricecalcbox.subtotal"/></div>
                    <div id="checkoutOrderSummarySubtotal">
                        <div class="o-cr-order-summary-totals__item-price">
                            <div id="checkoutOrderSummarySubtotal_currency">{{= it.subTotal.currencyIso }}</div>
                            <div id="checkoutOrderSummarySubtotal_price">{{= it.subTotal.formattedValue }}</div>
                        </div>
                    </div>
                </li>

                {{? it.totalDiscounts && it.totalDiscounts.value > 0}}
                <li class="o-cr-order-summary-totals__item">
                    <div id="checkoutOrderSummaryDiscountLabel"><spring:message code="cart.pricecalcbox.discount"/></div>
                    <div id="checkoutOrderSummaryDiscount">
                        <div class="o-cr-order-summary-totals__item-price">
                            <div id="checkoutOrderSummaryDiscount_currency">{{= it.totalDiscounts.currencyIso }}</div>
                            <div id="checkoutOrderSummaryDiscount_price">{{= it.totalDiscounts.formattedValue }}</div>
                        </div>
                    </div>
                </li>
                {{?}}

                <li class="o-cr-order-summary-totals__item">
                    <div id="checkoutOrderSummaryDeliveryLabel"><spring:message code="checkout.deliveryPage.title"/></div>
                    <div id="checkoutOrderSummaryDelivery">
                        {{? it.deliveryCost.value > 0}}
                        <div class="o-cr-order-summary-totals__item-price">
                            <div id="checkoutOrderSummaryDelivery_currency">{{= it.deliveryCost.currencyIso }}</div>
                            <div id="checkoutOrderSummaryDelivery_price">{{= it.deliveryCost.formattedValue }}</div>
                        </div>
                        {{??}}
                        <spring:message code="checkout.summary.deliveryCost.free"/>
                        {{?}}
                    </div>
                </li>

                {{? it.erpVoucherInfoData}}
                {{? it.erpVoucherInfoData.erpErrorMessageKey == null && it.erpVoucherInfoData.valid}}
                <li class="o-cr-order-summary-totals__item">
                    <div id="checkoutOrderSummaryVoucherLabel"><spring:message code="cart.pricecalcbox.voucher"/></div>
                    <div id="checkoutOrderSummaryVoucher">
                        <div class="o-cr-order-summary-totals__item-price">
                            <div id="checkoutOrderSummaryVoucher_currency">{{= it.erpVoucherInfoData.fixedFormattedValue.currencyIso }}</div>
                            <div id="checkoutOrderSummaryVoucher_price">&ndash;&nbsp;{{= it.erpVoucherInfoData.fixedFormattedValue.formattedValue }}</div>
                        </div>
                    </div>
                </li>
                {{?}}
                {{?}}

                <li class="o-cr-order-summary-totals__item">
                    <div id="checkoutOrderSummaryTaxLabel"><spring:message code="cart.pricecalcbox.tax"/></div>
                    <div id="checkoutOrderSummaryTax">
                        <div class="o-cr-order-summary-totals__item-price">
                            <div id="checkoutOrderSummaryTax_currency">{{= it.totalTax.currencyIso }}</div>
                            <div id="checkoutOrderSummaryTax_price">{{= it.totalTax.formattedValue }}</div>
                        </div>
                    </div>
                </li>

                <li class="o-cr-order-summary-totals__item is-total">
                    <div id="checkoutOrderSummaryTotalLabel"><spring:message code="order.total"/></div>
                    <div id="checkoutOrderSummaryTotal">
                        <div class="o-cr-order-summary-totals__item-price">
                            <div id="checkoutOrderSummaryTotal_currency">{{= it.totalPrice.currencyIso }}</div>
                            <div id="checkoutOrderSummaryTotal_price">{{= it.totalPrice.formattedValue }}</div>
                        </div>
                    </div>
                </li>
            </ul>
        </script>
    </c:otherwise>
</c:choose>
