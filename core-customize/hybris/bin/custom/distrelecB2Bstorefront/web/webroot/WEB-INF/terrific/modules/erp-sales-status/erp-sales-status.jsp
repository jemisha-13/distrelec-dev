<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<spring:message code="shipping-information.instock.short" var="sInStock" text="{0} in stock" />
<spring:message code="shipping-information.further.stock" var="sFurtherStock" text="{0} Further stock available in {1} days" />
<spring:message code="availability.availableToOrder" var="sAvailability" />
<spring:message code="shipping-information.warehouse.cdc" var="sWarehouseCdc" text="7371" />
<spring:message code="shipping-information.instock.long" var="sInStockLong" text="{0} in stock available for immediate dispatch when order placed before 18:00, Mon-Fri excl. holidays. Estimated delivery date 1 working day" />
<spring:message code="shipping-information.pickup.instore" var="sPickupInStore" text="{0} Pickup in store" />
<spring:message code="shipping-information.more.stock.available" var="sMoreStockAvailable" text="More stock available in {0} weeks" />
<spring:message code="shipping-information.accurate.delivery" var="sAccurateDelivery" text="Accurate delivery dates provided during check out" />
<spring:message code="shipping-information.leadtime&stockinformation" var="sMoreAboutLeadTime" text="More about lead time" />
<spring:message code="product.status.currently.not.available" var="sCurrentlyNotAvailable" text="Currently not available" />


<div class="erp-sales-status erp-sales-status--${productArtNo}" data-product-code="${productArtNo}" data-status-code="${productStatusCode}">
    <p class="erp-sales-status__availabletoorder erp-sales-status--element hidden"> <i class="fas fas fa-check-circle"></i> <span class="left">${sAvailability}</span> </p>
    <p class="erp-sales-status__instock erp-sales-status--element hidden"> <i class="fas fa-check-circle"></i> <span class="inStockText" data-instock-text="${sInStock}"></span> </p>
    <p class="erp-sales-status__currentlynotavailable erp-sales-status--element hidden"> <i class="fas fa-times-circle"></i> <span class="notAvailableText">${sCurrentlyNotAvailable}</span> </p>

    <p class="erp-sales-status__pickup erp-sales-status--element hidden">
        <div class="left pickupInStoreText" data-pickup-text="${sPickupInStore}"></div>
    </p>

    <p class="erp-sales-status__40-45 erp-sales-status--element hidden">
        <span class="erp-sales-status__40-45-instock hidden"> <i class="fas fa-check-circle"></i> <spring:message code="product.status.40" /> </span>
        <span class="erp-sales-status__40-45-outofstock hidden"> <i class="fas fa-times-circle"></i> <spring:message code="product.status.60" /> </span>
    </p>

    <p class="erp-sales-status__20 erp-sales-status--element hidden">
       <span> <i class="fa fa-clock"></i> <spring:message code="product.message.status.20.statusbox" /> </span>
    </p>

    <p class="erp-sales-status__21 erp-sales-status--element hidden">
        <span> <i class="fa fa-clock"></i>  <spring:message code="product.message.status.21.statusbox" /> </span>
    </p>

    <p class="erp-sales-status__currently-unavailable erp-sales-status--element hidden">
        <i class="fas fa-times-circle"></i> <spring:message code="product.message.status.block.supspended" />
    </p>

    <p class="erp-sales-status__furtherstock erp-sales-status--element"> <span class="further-stock" data-further-text="${sFurtherStock}"></span> </p>

    <p class="erp-sales-status__morestockavailable moreStockAvailable erp-sales-status--element hidden">
        <span class="morestockavailable" data-morestockavailable-text="${sMoreStockAvailable}"></span>
    </p>

    <p class="erp-sales-status__nolongeravailable erp-sales-status--element hidden">
        <i class="fas fa-times-circle"></i> <spring:message code="product.status.nolongeravailable" />
    </p>

    <!-- Lead time -->
    <div class="row leadtime">
        <div class="row leadTime hidden leadTimeField" data-available-leadtime-text="${leadTimeText}"></div>
    </div>

    <div class="leadtime-holder">

        <div class="leadTimeFlyout leadTimeFlyout-${productArtNo} hidden" id="leadTimeFlyout"  data-warehouse-cdc="${sWarehouseCdc}" data-status-code="${product.salesStatus}">
            <i class="fas fa-times lead-closer"></i>
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