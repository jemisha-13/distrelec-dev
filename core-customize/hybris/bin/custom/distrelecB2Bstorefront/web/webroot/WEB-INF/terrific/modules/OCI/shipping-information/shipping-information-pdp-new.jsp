<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<input type="hidden" id="product_code" value="${product.code}" />

<c:set var="statusCode" value="${product.salesStatus}" />
<c:set var="isNot20" value="${statusCode ne '20'}" />
<c:set var = "productMessageKey" value = "product.message.status.${statusCode}" />

<spring:message code="availability.availableToOrder" var="sAvailableToOrder" text="Available to order" />
<spring:message code="${productMessageKey}" var="statusText" arguments="${product.name}�${product.code}" argumentSeparator="�" text="" />
<spring:message code="product.shipping.available.leadTime.long" var="leadTimeText" text="Further stock available for delivery in {0} week(s)" />
<spring:message code="shipping-information.further.additional" var="sFurtherAdditionalStock" text="{0} Additional stock will be available in {1} days" />
<spring:message code="shipping-information.waldom.additional.available" var="sFurtherStockWaldom" text="{0} Additional stock will be available in {1} days" />
<spring:message code="shipping-information.instock" var="sInStock" text="{0} in stock" />
<spring:message code="shipping-information.pickup.instore" var="sPickupInStore" text="{0} Pickup in store" />
<spring:message code="shipping-information.further.stock" var="sFurtherStock" text="{0} Further stock available in {1} days" />
<spring:message code="shipping-information.instock" var="sInStock" text="{0} in stock" />
<spring:message code="shipping-information.pickup.instore" var="sPickupInStore" text="{0} Pickup in store" />
<spring:message code="shipping-information.instock.long" var="sInStockLong" text="{0} in stock available for immediate dispatch when order placed before 18:00, Mon-Fri excl. holidays. Estimated delivery date 1 working day" />
<spring:message code="shipping-information.estimated.deliverydate" var="sEstimatedDeliveryDate" text="Estimated delivery date 1 working day" />
<spring:message code="shipping-information.more.about.lead" var="sMoreAboutLeadTime" text="More about lead time" />
<spring:message code="shipping-information.accurate.delivery" var="sAccurateDelivery" text="Accurate delivery dates provided during check out" />
<spring:message code="shipping-information.more.stock.available" var="sMoreStockAvailable" text="More stock available in {0} weeks" />
<spring:message code="shipping-information.waldom.availablefuture" var="sInStockBTO" text="{0} Stock will be available in {1} days*" />
<spring:message code="shipping-information.waldom.inconfirmation" var="sDeliveryTimeInConfirmation" text="The exact delivery date will be stated in the order confirmation" />
<spring:message code="shipping-information.waldom.supplier" var="sDeliveryTimeBTO" text="*In accordance with supplier guidelines, subject to change. The exact delivery date will be stated in the order confirmation" />
<spring:message code="shipping-information.warehouse.cdc" var="sWarehouseCdc" text="7371" />
<spring:message code="show.local.stock" var="sShowLocalStock" />
<spring:message code="shipping-information.instock.delivery" var="sDeliveryTime" text="*usually received in 3 days. The exact delivery date will be stated in the order confirmation" />
<spring:message code="shipping-information.instock.nextdaydelivery" var="sNextDayDeliveryTime" text="*Available for next business day delivery. The exact delivery date will be stated in the order confirmation" />
<spring:message code="product.status.currently.not.available" var="sCurrentlyNotAvailable" text="Currently not available" />

<spring:message code="stock.notification.email.instruction.new" var="sStockNotificationEmailInstruction" />
<spring:message code="stock.notification.email.placeholder" var="sStockNotificationEmailPlaceholder" />
<spring:message code="stock.notification.email.notifyMe" var="sStockNotificationEmailNotifyMe" />
<spring:message code="stock.notification.email.assuranceText.new" var="sStockNotificationEmailAssuranceText" />
<spring:message code="stock.notification.email.privacyPolicy" var="sStockNotificationEmailPrivacyPolicy" />
<spring:message code="stock.notification.email.successText" var="sStockNotificationEmailSuccessText" />
<spring:message code="stock.notification.email.existingUserText" var="sStockNotificationEmailExistingUserText" />
<spring:message code="stock.notification.email.blankText" var="sStockNotificationEmailBlankText" />
<spring:message code="stock.notification.email.invalidText" var="sStockNotificationEmailInvalidText" />


<spring:message code="bans.shipping.info.message" var="sBansMessage" />
<spring:message code="banc.shipping.info.message" var="sBancMessage" />

<c:set var="isOutOfStockNotificationStatus" value="" />

<c:if test="${isExportShop and not currentCountry.european}">
    <spring:message code="shipping-information.estimated.deliverydate.not.eu" var="sEstimatedDeliveryDate" text="Estimated delivery date 3-9 working days" />
</c:if>

<spring:eval expression="@configurationService.configuration.getString('distrelec.noproduct.forsale.salestatus')" var="notforsales" scope="request" />

<c:choose>
    <c:when test="${fn:contains(notforsales, statusCode)}">
        <c:set var="codeStatus" value="notStock" />
    </c:when>
    <c:otherwise>
        <c:set var="codeStatus" value="inStock" />
    </c:otherwise>
</c:choose>

<div class="loading ${itemCategoryGroupClass}" data-status-code="${product.salesStatus}" data-item-catgroup="${product.itemCategoryGroup}">
    <c:choose>
        <c:when test="${product.catPlusItem}">
            <div class="border-right ellipsis" title="<spring:message code="product.shipping.availability" />">
                <span class="availability-service-plus" title="<spring:message code="product.shipping.availability" />:&nbsp;<spring:message code="product.shipping.catplus.availability" />">
                    <spring:message code="product.shipping.availability" />:&nbsp;
                </span>
                <spring:message code="product.shipping.catplus.availability" />
            </div>
        </c:when>
        <c:otherwise>
            <div class="loading__normal loading__normal--instock hidden">
                <div class="table-icon">
                    <span class="icon icon--inStock">
                        <i class="fa fa-check" aria-hidden="true"></i>
                    </span>
                </div>
                <div class="instock">
                    <div class="inStockText" id="inStockText" data-instock-text="${sInStock}" data-instock-bto="${sInStockBTO}" data-available-order="${sAvailableToOrder}"></div>
                </div>
                <div class="deliveryTime">
                    <div class="deliveryTimeText" id="deliveryTimeText" data-delivery-text="${sDeliveryTime}" data-next-day-delivery-text="${sNextDayDeliveryTime}" data-delivery-text-bto="${sDeliveryTimeBTO}" data-delivery-text-inconfirmation="${sDeliveryTimeInConfirmation}"></div>
                </div>
                <div class="tableinfo info-stock">
                    <c:if test="${sShowLocalStock eq 'true'}">
                        <div class="pickup hidden">  <div class="pickupInStoreText" data-pickup-text="${sPickupInStore}"></div> </div>
                    </c:if>
                    <div class="moreStockAvailable hidden"> <div class="moreStockAvailableText" data-morestockavailable-text="${sMoreStockAvailable}"></div> </div>
                    <div class="instock hidden"> <div class="inStockText" data-instock-text="${sInStock}"></div></div>
                    <div class="further hidden furtherAriba" data-further-text-additional="${sFurtherAdditionalStock}" data-further-text="${sFurtherStock}" data-further-text-waldom="${sFurtherStockWaldom}"></div>
                </div>
                <div class="leadtime-holder">
                    <div class="leadTimeFlyout hidden" id="leadTimeFlyout" data-warehouse-cdc="${sWarehouseCdc}" data-status-code="${product.salesStatus}">
                        <div class="leadTimeLine">
                            <div class="inStockLong" data-instock-text="${sInStockLong}"></div>
                            <div class="estimatedDeliveryDate hidden"> ${sEstimatedDeliveryDate}</div>
                        </div>
                        <div class="leadTimeLine"> <b> ${sAccurateDelivery}</b> </div>
                        <div class="lead-arrow-down hidden"></div>
                    </div>
                </div>
            </div>

            <div class="loading__normal loading__normal--outofstock hidden">
                <div class="table-icon">
                    <span class="icon icon--notStock hidden">
                        <i class="fa fa-times" aria-hidden="true"></i>
                    </span>
                </div>
                <div class="outofstock">
                    <div class="sales-status sales-status--no-longer-available" data-waldom-unavailable="${sCurrentlyNotAvailable}">
                        <c:choose>
                            <c:when test="${statusCode == 40 or statusCode == 41}">
                                <spring:message code="product.status.60" />
                            </c:when>
                            <c:when test="${statusCode == 90}">
                                <spring:message code="product.status.90" />
                            </c:when>
                            <c:when test="${statusCode == 91}">
                                <spring:message code="product.status.91" />
                            </c:when>
                            <c:when test="${statusCode == 50}">
                                <spring:message code="product.status.50" />
                            </c:when>
                            <c:when test="${statusCode == 51}">
                                <spring:message code="product.status.51" />
                            </c:when>
                            <c:when test="${statusCode == 52}">
                                <spring:message code="product.status.52" />
                            </c:when>
                            <c:when test="${statusCode == 53}">
                                <spring:message code="product.status.53" />
                            </c:when>
                            <c:when test="${statusCode == 60}">
                                <spring:message code="product.message.status.60" />
                            </c:when>
                            <c:when test="${statusCode == 61}">
                                <spring:message code="product.message.status.61" />
                            </c:when>
                            <c:when test="${statusCode == 62}">
                                <spring:message code="product.message.status.62" />
                            </c:when>
                            <c:otherwise>
                                <spring:message code="product.status.nolongeravailable" />
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="tableinfo info-stock">
                    <c:if test="${sShowLocalStock eq 'true'}">
                        <div class="pickup hidden">  <div class="pickupInStoreText" data-pickup-text="${sPickupInStore}"></div> </div>
                    </c:if>
                    <div class="instock hidden"> <div class="inStockText" data-instock-text="${sInStock}"></div></div>
                    <div class="further hidden" data-further-text="${sFurtherStock}"></div>
                </div>
                <div class="leadtime-holder">
                    <div class="leadTimeFlyout hidden" id="leadTimeFlyout" data-warehouse-cdc="${sWarehouseCdc}" data-status-code="${product.salesStatus}">
                        <div class="leadTimeLine">
                            <div class="inStockLong" data-instock-text="${sInStockLong}"></div>
                            <div class="estimatedDeliveryDate hidden"> ${sEstimatedDeliveryDate}</div>
                        </div>
                        <div class="leadTimeLine"> <b> ${sAccurateDelivery}</b> </div>
                        <div class="lead-arrow-down hidden"></div>
                    </div>
                </div>
            </div>

            <div class="loading__normal loading__normal--comingsoon hidden">

                <c:if test="${not empty statusCode && isNot20}">
                    <div class="table-icon">
                    <span class="icon icon--inStock">
                        <c:choose>
                            <c:when test="${ product.itemCategoryGroup == 'BANS' || product.itemCategoryGroup == 'BANC'}">
                                <c:if test="${ statusCode == 30 || statusCode == 31 }">
                                    <i class="fas fa-info" aria-hidden="true"></i>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <i class="fa fa-check" aria-hidden="true"></i>
                            </c:otherwise>
                        </c:choose>

                    </span>
                    </div>
                </c:if>

                <c:if test="${statusCode == 30 || statusCode == 31}">
                    <div class="outofstock hidden">
                        <div class="sales-status sales-status--no-longer-available" data-available-order="${sAvailableToOrder}">
                            <spring:message code="product.status.currently.not.available"/>
                        </div>

                        <div class="deliveryTime deliveryTimeText hidden">
                            <spring:message code="shipping-information.waldom.supplier" />
                        </div>

                        <span class="item-category-message hidden">

                            <c:if test="${product.itemCategoryGroup == 'BANS'}">
                                <spring:message code="bans.shipping.info.message"/>
                                <c:set var="isOutOfStockNotificationStatus" value="hidden" />
                            </c:if>
                            <c:if test="${product.itemCategoryGroup == 'BANC'}">
                                <spring:message code="banc.shipping.info.message"/>
                                <c:set var="isOutOfStockNotificationStatus" value="hidden" />
                            </c:if>

                        </span>


                    </div>
                </c:if>

                <div class="comingsoon">
                    <c:if test="${not empty statusText}">
                        <div class="sales-status sales-status--status-text">
                            <div class="sales-status sales-status--available-stock-last">
                                <c:choose>
                                    <c:when test="${statusCode eq 20}">
                                        <spring:message code="product.message.status.20.statusbox" />
                                    </c:when>
                                    <c:when test="${statusCode eq 21}">
                                        <spring:message code="product.message.status.21.statusbox" />
                                    </c:when>
                                    <c:otherwise>
                                        ${statusText}
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:if>
                </div>
                <div class="tableinfo info-stock">
                    <c:if test="${sShowLocalStock eq 'true'}">
                        <div class="pickup hidden">  <div class="pickupInStoreText" data-pickup-text="${sPickupInStore}"></div> </div>
                    </c:if>
                    <div class="moreStockAvailable hidden"> <div class="moreStockAvailableText" data-morestockavailable-text="${sMoreStockAvailable}"></div> </div>
                    <div class="instock hidden"> <div class="inStockText" data-instock-text="${sInStock}"></div></div>
                    <div class="further hidden" data-further-text="${sFurtherStock}"></div>
                </div>
                <div class="leadtime-holder">
                    <div class="leadTimeFlyout hidden" id="leadTimeFlyout" data-warehouse-cdc="${sWarehouseCdc}" data-status-code="${product.salesStatus}">
                        <div class="leadTimeLine">
                            <div class="inStockLong" data-instock-text="${sInStockLong}"></div>
                            <div class="estimatedDeliveryDate hidden"> ${sEstimatedDeliveryDate}</div>
                        </div>
                        <div class="leadTimeLine"> <b> ${sAccurateDelivery}</b> </div>
                        <div class="lead-arrow-down hidden"></div>
                    </div>
                </div>
                <div class="stock-notification ${isOutOfStockNotificationStatus}">
                    <div class="stock-notification__form-content">

                        <form:form id="stock_notification${product.codeErpRelevant}" class="row stock-notification__form js-stock-notification-form" method="GET">
                            <div class="stock-notification__form--instruction"> ${sStockNotificationEmailInstruction} </div>
                            <input type="text" name="email" id="email${product.codeErpRelevant}" maxlength="255" placeholder="${sStockNotificationEmailPlaceholder}" class="col col-md-6 stock-notification__form--emailinput" value="${cartData.b2bCustomerData.email}">
                            <button type="submit" class="mat-button mat-button__solid--action-green stock-notification__form--cta" data-aainteraction="out of stock submission" data-location="pdp">
                                    ${sStockNotificationEmailNotifyMe}
                            </button>

                            <span class="stock-notification--error  error-empty hidden">${sStockNotificationEmailBlankText}</span>
                            <span class="stock-notification--error error-emailvalid hidden">${sStockNotificationEmailInvalidText}</span>

                            <p class="stock-notification__form--instruction-policy stock-notification__form--assurance-text">
                                    ${sStockNotificationEmailAssuranceText}
                                <a class="stock-notification__form--assurance-text--privacy-policy" href="/data-protection/cms/datenschutz">${sStockNotificationEmailPrivacyPolicy}</a>
                            </p>
                        </form:form>

                    </div>

                    <div class="stock-notification__success hidden">
                        <span class="icon-wrapper">
                            <i class="fa fa-check" aria-hidden="true"></i>
                        </span>

                        <span class="stock-notification--message">${sStockNotificationEmailSuccessText}</span>
                    </div>

                    <div class="stock-notification__failure hidden">
                        <span class="icon-wrapper">
                            <i class="fa fa-check" aria-hidden="true"></i>
                        </span>
                        <span class="stock-notification--message">${sStockNotificationEmailExistingUserText}</span>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>
