<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="formatprice" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<input type="hidden" id="product_code" class="hidden_product_code_compare" value="${product.code}" data-item-catgroup="${product.itemCategoryGroup}"/>
<spring:message code="availability.availableToOrder" var="sAvailableToOrder" text="Available to order" />
<spring:message code="product.shipping.available.leadTime.short" var="leadTimeText" text="More in {0} weeks" />

<spring:message code="shipping-information.instock.short" var="sInStock" text="{0} in stock" />
<spring:message code="shipping-information.pickup.instore" var="sPickupInStore" text="{0} Pickup in store" />
<spring:message code="shipping-information.further.long" var="sFurtherStock" text="{0} Further additional stock in {1} days" />

<spring:message code="shipping-information.instock.long" var="sInStockLong" text="{0} in stock available for immediate dispatch when order placed before 18:00, Mon-Fri excl. holidays. Estimated delivery date 1 working day" />
<spring:message code="shipping-information.more.stock.available" var="sMoreStockAvailable" text="More stock available in {0} weeks" />
<spring:message code="shipping-information.accurate.delivery" var="sAccurateDelivery" text="Accurate delivery dates provided during check out" />
<spring:message code="shipping-information.leadtime&stockinformation" var="sMoreAboutLeadTime" text="More about lead time" />
<spring:message code="product.status.currently.not.available" var="sCurrentlyNotAvailable" text="Currently not available" />

<spring:message code="shipping-information.warehouse.cdc" var="sWarehouseCdc" text="7371" />
<spring:message code="show.local.stock" var="sShowLocalStock" />

<spring:message code="availability.availableToOrder" var="sAvailability" />

<div class="cell cell-availability compare-list" >
    <div class="product-code-${product.code} availability-compare" data-status-code="${product.salesStatus}" data-item-catgroup="${product.itemCategoryGroup}">
        <!-- Availability -->
        <div class="availability compareee info-stock info-stock-${product.codeErpRelevant}">
            <div class="left instock hidden">
                <i class="fas fa-check-circle"></i>
                <span class="left inStockText inStock-${product.code}" data-instock-text="${sInStock}"></span>
            </div>
            <div class="sales-status sales-status--status-text-40-45 hidden">
                <div class="sales-status instock-41-45 hidden"> <i class="fas fa-check-circle"></i> <spring:message code="product.status.40" /> </div>
                <div class="sales-status outofstock-41-45 js-check-if-out-of-stock-msg hidden">
                    <c:choose>
                        <c:when test="${product.salesStatus == 90 || product.salesStatus == 91 || product.salesStatus == 50 ||
                    product.salesStatus == 51 || product.salesStatus == 52 || product.salesStatus == 53}">
                            <i class="fas fa-times-circle"></i> <spring:message code="product.status.currently.not.available" />
                        </c:when>
                        <c:when test="${product.salesStatus == 41}">
                            <i class="fas fa-times-circle"></i> <spring:message code="product.status.41" />
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-times-circle"></i> <spring:message code="product.status.no.longer.available" />
                        </c:otherwise>
                    </c:choose>

                </div>
                <div class="sales-status stock-bom-status hidden"> <i class="fas fa-times-circle"></i> <spring:message code="product.status.nolongeravailable" /> </div>
            </div>

            <div class="sales-status sales-status--block hidden">
                <c:choose>
                    <c:when test="${product.salesStatus == 20}">
                        <i class="fa fa-clock" aria-hidden="true"></i><spring:message code="product.message.status.20.statusbox" />
                    </c:when>
                    <c:when test="${product.salesStatus == 21}">
                        <i class="fa fa-clock"  aria-hidden="true"></i> <spring:message code="product.message.status.21.statusbox" />
                    </c:when>
                    <c:when test="${product.salesStatus == 60 || product.salesStatus == 61}"></c:when>
                    <c:when test="${product.salesStatus == 62}">
                        <i class="fas fa-times-circle"></i> <spring:message code="product.message.status.62" />
                    </c:when>
                    <c:when test="${product.salesStatus == 90 || product.salesStatus == 91 || product.salesStatus == 50 ||
                    product.salesStatus == 51 || product.salesStatus == 52 || product.salesStatus == 53}">
                        <i class="fas fa-times-circle"></i> <spring:message code="product.message.status.block.supspended" />
                    </c:when>
                    <c:otherwise>
                        <i class="fas fa-times-circle"></i> <spring:message code="product.message.status.block.supspended" />
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="left out-of-stock availableToOrder" data-waldom-unavailable="${sCurrentlyNotAvailable}">
                <i class="fas fa-check-circle" aria-hidden="true"></i><span class="left">${sAvailableToOrder}</span>
            </div>

            <div class="left out-of-stock currentlyNotAvailable hidden">
                <i class="fas fa-times-circle" aria-hidden="true"></i><span class="left">${sCurrentlyNotAvailable}</span>
            </div>

            <div class="price">
                <span>1+</span>
                <span class="price__sale-price"><formatprice:price format="default" priceData="${product.price}" /></span>
            </div>

            <div class="row moreStockAvailable hidden">
                <div class="moreStockAvailableText" data-morestockavailable-text="${sMoreStockAvailable}"></div>
            </div>

            <!-- Lead time -->
            <div class="row leadtime">
                <div class="leadTime hidden leadTimeField" data-available-leadtime-text="${leadTimeText}"></div>
            </div>

            <div class="leadtimeholder">
                <div class="leadtimeholder__content">
                    <div class="leadTimeFlyout leadTimeFlyout-${product.codeErpRelevant} hidden" id="leadTimeFlyout"  data-warehouse-cdc="${sWarehouseCdc}" data-status-code="${product.salesStatus}">
                        <div class="leadTimeLine">
                            <h3 class="base ellipsis"><i class="lead-closer"></i></h3>
                        </div>
                        <div class="leadTimeLine">
                            <div class="inStockLong" data-instock-text="${sInStockLong}"></div>
                        </div>
                        <div class="leadTimeLine">
                            <div class="pickupLong" data-pickup-long-text="${sPickupInStore}"></div>
                        </div>

                        <div class="leadTimeLine">
                            <div class="furtherLong" data-further-text="${sFurtherStock}"></div>
                        </div>

                        <div class="leadTimeLine">
                            <div class="moreStockAvailable" data-morestock-text="${sMoreStockAvailable}"></div>
                        </div>

                        <div class="leadTimeLine">
                            <b> ${sAccurateDelivery}</b>
                        </div>

                        <div class="lead-arrow-down lead-arrow-down-${product.codeErpRelevant} "></div>
                    </div>
                </div>
            </div>

        </div>

    </div>
</div>

<!-- tmpl -->


<div class="hidden" id="tmpl-stock_level_pickup_header">
    <div class="row line"></div>
    <div class="row head-pickup">
        <h2><spring:message code="product.shipping.in.store.pickup" /></h2>
    </div>
</div>

<script id="tmpl-stock_level_pickup_row" type="text/x-dot-template">

</script>

<script id="tmpl-stock_level" type="text/x-dot-template">

</script>
<!-- end tmpl -->