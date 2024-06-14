<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="cart.list.articleNumber" var="sArticleNumber" />
<spring:message code="cart.list.typeName" var="sTypeNumber" />
<spring:message code="cart.list.manufacturer" var="sManufacturer" />
<spring:message code="cart.list.quantity" var="sQuantity" />
<spring:message var="dateFormat" code="text.store.dateformat" text="yyyy-MM-dd" />


<c:forEach items="${orderLines}" var="orderLine" varStatus="status">

    <c:set var="deliveryNumber" value="${orderLine.deliveryID}" />
    <fmt:formatDate var="deliveryDate" value="${orderLine.deliveryDate}" dateStyle="long" timeStyle="short" pattern="${dateFormat}" />

    <div class="modal" id="modalShipmentTracking-${status.index + 1}" tabindex="-1">
        <c:set var="handingUnitSize" value="${orderLine.handlingUnit.size()}" />

        <div class="hd">
            <div>
                <h3 class="title"><spring:message code="lightboxshipment.shipment" text="Shipment" />: <a href="#" class="tracking-id">${deliveryNumber}</a><span class="n">(${handingUnitSize})</span></h3>
                <a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="lightboxshoppinglist.close" /></a>
            </div>
            <p><spring:message code="lightboxshipment.toptext" arguments="${deliveryDate}" text="The following article(s) are included in this shipment from {0}" />:</p>
        </div>

        <div class="bd">
            <c:forEach items="${orderLine.handlingUnit}" var="listItems" varStatus="loop">

                <c:set var="TrackingUrl" value="${listItems.trackingURL}" />
                <c:set var="HandingUnitID" value="${listItems.unitID}" />

                <div class="cell-header-collapse">
                    <h2 class="cell-header-collapse__id" data-tab="cell-header-collapse__id--${loop.index + 1}--${status.index + 1}">
                        ${HandingUnitID}
                        <span class="toggle">
                            <i class="fa fa-angle-down" aria-hidden="true"></i>
                            <i class="fa fa-angle-up" aria-hidden="true"></i>
                        </span>
                    </h2>
                </div>

                <div class="cell-body-content" id="cell-header-collapse__id--${loop.index + 1}--${status.index + 1}">

                    <c:forEach items="${listItems.handlingUnitItem}" var="singleItems" varStatus="count">
                        <c:set var="ArticleName" value="${singleItems.articleName}" />
                        <c:set var="MaterialID" value="${singleItems.materialID}" />
                        <c:set var="ManufacturerName" value="${singleItems.manufacturer}" />
                        <c:set var="ProductQuantity" value="${singleItems.quantity}" />
                        <c:set var="ProductType" value="${singleItems.type}" />

                        <div class="cell-info">
                            <h3 class="ellipsis productName" title="${ArticleName}">${ArticleName}</h3>
                            <div class="cell-info-table">
                                <div class="cell-info-cell">
                                    <div class="hd">${sArticleNumber}</div>
                                    <div class="bd ellipsis"> <formatArticle:articleNumber articleNumber="${MaterialID}"  /></div>
                                </div>
                                <div class="cell-info-cell">
                                    <div class="hd">${sTypeNumber}</div>
                                    <div class="bd ellipsis" title="${ProductType}">${ProductType}</div>
                                </div>
                                <div class="cell-info-cell">
                                    <div class="hd">${sManufacturer}</div>
                                    <div class="bd ellipsis" title="${ManufacturerName}">${ManufacturerName}</div>
                                </div>
                                <div class="cell-info-cell">
                                    <div class="hd">${sQuantity}</div>
                                    <div class="bd ellipsis">${ProductQuantity}</div>
                                </div>
                            </div>
                        </div>

                    </c:forEach>

                    <div class="ft">
                        <a href="${TrackingUrl}" class="online-tracking" target="_blank"><spring:message code="lightboxshipment.trackingtext" text="Click here to track this shipment online" /></a>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>


</c:forEach>
