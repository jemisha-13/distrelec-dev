<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<input type="hidden" id="product_code" class="hidden_product_code_compare" value="${product.code}" />
<spring:message code="product.shipping.available.leadTime.short" var="leadTimeText" text="More in {0} weeks" />

<spring:message code="shipping-information.instock.short" var="sInStock" text="{0} in stock" />
<spring:message code="shipping-information.pickup.instore" var="sPickupInStore" text="{0} Pickup in store" />
<spring:message code="shipping-information.pickup.instore" var="sPickupInStorePopup" text="{0} Pickup in store" />
<spring:message code="shipping-information.further.long" var="sFurtherStock" text="{0} Further additional stock in {1} days" />

<spring:message code="shipping-information.instock.long" var="sInStockLong" text="{0} in stock available for immediate dispatch when order placed before 18:00, Mon-Fri excl. holidays. Estimated delivery date 1 working day" />
<spring:message code="shipping-information.more.stock.available" var="sMoreStockAvailable" text="More stock available in {0} weeks" />
<spring:message code="shipping-information.accurate.delivery" var="sAccurateDelivery" text="Accurate delivery dates provided during check out" />
<spring:message code="shipping-information.more.about.lead" var="sMoreAboutLeadTime" text="More about lead time" />

<spring:message code="shipping-information.warehouse.cdc" var="sWarehouseCdc" text="7371" />
<spring:message code="show.local.stock" var="sShowLocalStock" />

<div class="compare-list compare-list__title">Availability</div>

<div class="cell cell-availability compare-list__main">
    <div class="product-code-${product.code} availability-compare">

        <!-- Availability -->
        <div class="availability compareee info-stock info-stock-${product.codeErpRelevant}">
            <div class="row instock hidden">
                <div class="left inStockText" data-instock-text="${sInStock}"></div>
            </div>
            <c:if test="${sShowLocalStock eq 'true'}">
                <div class="row pickup hidden">
                    <div class="left pickupInStoreText" data-pickup-text="${sPickupInStore}"></div>
                </div>
            </c:if>

            <div class="row left further hidden" data-further-text="${sFurtherStock}"></div>

            <div class="row moreStockAvailable hidden">
                <div class="moreStockAvailableText" data-morestockavailable-text="${sMoreStockAvailable}"></div>
            </div>

            <!-- Lead time -->
            <div class="row leadtime">
                <div class="leadTime hidden leadTimeField" data-available-leadtime-text="${leadTimeText}"></div>
            </div>

            <c:if test="${siteUid ne 'distrelec_FR'}">
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
                                <div class="pickupLongPopup" data-pickup-long-text-popup="${sPickupInStorePopup}"></div>
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
            </c:if>

        </div>

    </div>
</div>

<!-- tmpl -->
<div class="hidden">
    <mod:cart-list-item template="dot-tmpl" />
</div>

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