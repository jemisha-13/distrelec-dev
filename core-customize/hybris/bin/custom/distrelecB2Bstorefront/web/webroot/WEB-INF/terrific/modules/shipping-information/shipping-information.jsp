<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<input type="hidden" id="product_code" value="${product.code}" />
<c:set var="statusCode" value="${product.salesStatus}" />
<spring:message code="product.status.${statusCode}" var="statusText" text="" />
<spring:message code="product.shipping.available.leadTime.long" var="leadTimeText" text="Further stock available for delivery in {0} week(s)" />
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
<spring:message code="shipping-information.warehouse.cdc" var="sWarehouseCdc" text="7371" />
<spring:message code="show.local.stock" var="sShowLocalStock" />

<c:if test="${isExportShop and not currentCountry.european}">
	<spring:message code="shipping-information.estimated.deliverydate.not.eu" var="sEstimatedDeliveryDate" text="Estimated delivery date 3-9 working days" />
</c:if>
<div class="leadTimeFlyout hidden" id="leadTimeFlyout" data-warehouse-cdc="${sWarehouseCdc}" data-status-code="${product.salesStatus}">
	<div class="leadTimeLine"> <h3 class="base ellipsis"><i class="lead-closer"></i></h3> </div>
	<div class="leadTimeLine">
		<div class="inStockLong" data-instock-text="${sInStockLong}"></div>
		<div class="estimatedDeliveryDate hidden"> ${sEstimatedDeliveryDate}</div>
	</div>
	<div class="leadTimeLine"> <div class="pickupLong" data-pickup-long-text="${sPickupInStore}"></div> </div>

	<div class="leadTimeLine"> <div class="furtherLong" data-further-text="${sFurtherStock}"></div> </div>

	<div class="leadTimeLine"> <div class="moreStockAvailable" data-morestock-text="${sMoreStockAvailable}"></div> </div>

	<div class="leadTimeLine"> <b> ${sAccurateDelivery}</b> </div>
	<div class="lead-arrow-down hidden"></div>

</div>
<div class="row loading">
	<c:choose>
		<c:when test="${product.catPlusItem}">
			<div class="gu-3 border-right ellipsis" title="<spring:message code="product.shipping.availability" />">
				<span class="availability-service-plus" title="<spring:message code="product.shipping.availability" />:&nbsp;<spring:message code="product.shipping.catplus.availability" />">
					<spring:message code="product.shipping.availability" />:&nbsp;
				</span>
				<spring:message code="product.shipping.catplus.availability" />
			</div>
		</c:when>
		<c:otherwise>
		    <div class="gu-3 tableinfo info-stock">
	            <div class="row head">
	                <div class="td nth1 availabilityHeader"><spring:message code="product.shipping.availability" /></div>
	            </div>	
		   		<div class="row instock hidden"> <div class="inStockText" data-instock-text="${sInStock}"></div> </div>
				<c:if test="${sShowLocalStock eq 'true'}">
					<div class="row pickup hidden">  <div class="pickupInStoreText" data-pickup-text="${sPickupInStore}"></div> </div>
				</c:if>
			   	
				<div class="row further hidden"> <div class="furtherText" data-further-text="${sFurtherStock}"></div> 	</div>
				<div class="row moreStockAvailable hidden"> <div class="moreStockAvailableText" data-morestockavailable-text="${sMoreStockAvailable}"></div> </div>

		   		<div class="row leadTime hidden" data-available-leadtime-text="${leadTimeText}"></div>
		   		<c:if test="${not empty statusText}">
			   		<div class="row">  <div class="sales-status"> ${statusText} </div> </div>
		   		</c:if>
		   	</div>	
    		</c:otherwise>
	</c:choose>
</div>
