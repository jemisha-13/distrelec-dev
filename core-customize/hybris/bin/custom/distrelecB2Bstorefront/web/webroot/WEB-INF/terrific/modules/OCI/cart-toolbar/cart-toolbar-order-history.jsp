<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>


<div class="item btns">
	<div class="btns__left">
		<c:if test="${not empty orderHistoryData}">
			<mod:toolsitem template="toolsitem-print" skin="print skin-toolsitem-narrow" tag="div" htmlClasses="tooltip-hover" attributes="data-aaorder-id='${orderHistoryData.code}',data-aainteraction='print order details',data-aalocation='order history'" />
			<mod:toolsitem template="toolsitem-download" skin="download skin-toolsitem-narrow" tag="div" downloadUrl="/my-account/order-history/order-details/${orderHistoryData.code}/download" attributes="data-aaorder-id='${orderHistoryData.code}',data-aainteraction='download order details',data-aalocation='order history'" />
			<c:if test="${(orderHistoryData.status.code eq 'ERP_STATUS_SHIPPED')}">
				<c:choose>
					<c:when test="${isExportShop}">
						<mod:toolsitem template="toolsitem-lbl-return-items" skin="with-label skin-toolsitem-return-items skin-toolsitem-narrow" tag="div" downloadUrl="/cms/returnorrepair" />
					</c:when>
					<c:otherwise>
						<mod:toolsitem template="toolsitem-lbl-return-items" skin="with-label skin-toolsitem-return-items skin-toolsitem-narrow" tag="div" downloadUrl="/my-account/order-history/order-details/${orderHistoryData.code}/return-items" attributes="data-aaorder-id='${orderHistoryData.code}',data-aainteraction='return items',data-aalocation='order history'"/>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:if>
	</div>
</div>

