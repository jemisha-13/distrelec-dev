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
<spring:message code="shipping-information.pickup.instore" var="sPickupInStore" text="{0} Available for pickup 2h after receipt of order" />
<spring:message code="shipping-information.further.stock" var="sFurtherStock" text="{0} Further stock available in {1} days" />
<spring:message code="shipping-information.further.additional" var="sFurtherAdditionalStock" text="Additional stock will be available in {1} days" />
<spring:message code="shipping-information.waldom.additional.available" var="sFurtherStockWaldom" text="{0} Additional stock will be available in {1} days" />
<spring:message code="shipping-information.waldom.availablefuture" var="sInStockBTO" text="{0} Stock will be available in {1} days*" />
<spring:message code="shipping-information.instock" var="sInStock" text="{0} in stock" />
<spring:message code="shipping-information.instock.long" var="sInStockLong" text="{0} in stock available for immediate dispatch when order placed before 18:00, Mon-Fri excl. holidays. Estimated delivery date 1 working day" />
<spring:message code="shipping-information.estimated.deliverydate" var="sEstimatedDeliveryDate" text="Estimated delivery date 1 working day" />
<spring:message code="shipping-information.waldom.supplier" var="sDeliveryTimeBTO" text="*In accordance with supplier guidelines, subject to change. The exact delivery date will be stated in the order confirmation" />
<spring:message code="shipping-information.waldom.inconfirmation" var="sDeliveryTimeInConfirmation" text="The exact delivery date will be stated in the order confirmation" />
<spring:message code="shipping-information.more.about.lead" var="sMoreAboutLeadTime" text="More about lead time" />
<spring:message code="shipping-information.accurate.delivery" var="sAccurateDelivery" text="Accurate delivery dates provided during check out" />
<spring:message code="shipping-information.more.stock.available" var="sMoreStockAvailable" text="More stock available in {0} weeks" />
<spring:message code="shipping-information.warehouse.cdc" var="sWarehouseCdc" text="7371" />
<spring:message code="show.local.stock" var="sShowLocalStock" />
<spring:message code="shipping-information.instock.delivery" var="sDeliveryTime" text="*usually received in 3 days. The exact delivery date will be stated in the order confirmation" />
<spring:message code="shipping-information.instock.nextdaydelivery" var="sNextDayDeliveryTime" text="*Available for next business day delivery. The exact delivery date will be stated in the order confirmation" />
<spring:message code="product.status.currently.not.available" var="sCurrentlyNotAvailable" text="Currently not available" />

<spring:message code="stock.notification.email.instruction.new" var="sStockNotificationEmailInstruction" />
<spring:message code="stock.notification.email.notifyMe" var="sStockNotificationEmailNotifyMe" />
<spring:message code="stock.notification.email.assuranceText.new" var="sStockNotificationEmailAssuranceText" />
<spring:message code="stock.notification.email.privacyPolicy" var="sStockNotificationEmailPrivacyPolicy" />

<spring:message code="bans.shipping.info.message" var="sBansMessage" />
<spring:message code="banc.shipping.info.message" var="sBancMessage" />

<c:set var="isOutOfStockNotificationStatus" value="" />

<c:if test="${isExportShop and not currentCountry.european}">
    <spring:message code="shipping-information.estimated.deliverydate.not.eu" var="sEstimatedDeliveryDate" text="Estimated delivery date 3-9 working days" />
</c:if>

<c:if test="${statusCode eq 21}">
    <spring:message code="product.message.status.21" var="sStockNotificationEmailInstruction" />
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
                    <div class="further hidden" data-further-text="${sFurtherStock}" data-further-text-additional="${sFurtherAdditionalStock}" data-further-text-waldom="${sFurtherStockWaldom}"></div>
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

            <div class="loading__normal loading__normal--outofstock js-check-if-out-of-stock-msg hidden">
                <div class="table-icon">
                    <span class="icon icon--notStock hidden">
                        <i class="fas fa-times-circle" aria-hidden="true"></i>
                    </span>
                </div>
                <div class="outofstock">
                    <div class="sales-status sales-status--no-longer-available" data-waldom-unavailable="${sCurrentlyNotAvailable}">
                        <c:choose>
                            <c:when test="${statusCode == 40}">
                                <spring:message code="product.status.40" />
                            </c:when>
                            <c:when test="${statusCode == 41}">
                                <spring:message code="product.status.41" />
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
                            <c:when test="${statusCode == 60 or statusCode == 61 or statusCode == 62}">
                                <spring:message code="product.status.60" />
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
                <div class="table-icon">
                    <span class="icon icon--comingSoon">
                        <i class="${statusCode == 30 || statusCode == 31 ? 'fas fa-times-circle' : 'fa fa-clock'}" aria-hidden="true"></i>
                    </span>
                </div>

                <c:if test="${statusCode == 30 || statusCode == 31}">
                    <div class="outofstock hidden">
                        <div class="sales-status sales-status--no-longer-available" data-available-order="${sAvailableToOrder}">
                            <spring:message code="product.status.currently.not.available" />
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
                <a id="pdp.availability.notify.me" href="#" class="notify-link js-availabilityNotify"><i class="far fa-envelope" aria-hidden="true"></i> <spring:message code="availability.notify.me"/></a>
                <mod:lightbox-availability-popup />
            </div>
        </c:otherwise>
    </c:choose>
</div>
