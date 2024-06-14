<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:forEach items="${orderLines}" var="orderLine" varStatus="status">
	<c:set var="deliveryList" value="${orderLine.deliveryID}" />
</c:forEach>

<c:if test="${not empty deliveryList}"> <%-- Don't display this section if the list is empty --%>
	<div class="mod-track-and-trace__holder">
		<h2 class="head"><spring:message code="orderdetailsection.trackAndTrace" text="Shipment tracking" /></h2>
		<div class="box">
			<span class="small"><spring:message code="orderdetailsection.trackAndTrace.text" text="Click on the tracking number(s) to see which products are in each shipment and access a link to track the status of your delivery." /></span>
			<ul class="data-list">
				<c:forEach items="${orderLines}" var="orderLine" varStatus="status">
					<c:set var="deliveryIdList" value="${orderLine.deliveryID}" />
					<c:set var="handingUnitSize" value="${orderLine.handlingUnit.size()}" />
					<li class="row">
						<a href="#" class="tracking-item tracking-link-${status.index + 1}" data-tab="modalShipmentTracking-${status.index + 1}" data-shipped-lines="${handingUnitSize}">${deliveryIdList}</a><span class="n">(${handingUnitSize})</span>
					</li>
				</c:forEach>
			</ul>
		</div>
		<mod:lightbox-shipment-tracking orderLines="${orderLines}"/>
	</div>
</c:if>