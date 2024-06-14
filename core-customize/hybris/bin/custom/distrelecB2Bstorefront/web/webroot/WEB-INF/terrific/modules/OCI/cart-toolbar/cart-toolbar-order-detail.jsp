<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="bd">
	<div class="bd__left">
		<ul class="tools-bar">
			<c:if test="${not empty orderData.entries}">
				<mod:toolsitem template="toolsitem-print" skin="print skin-toolsitem-narrow" tag="li" htmlClasses="tooltip-hover" attributes="data-aaorder-id='${orderData.code}',data-aainteraction='print order details',data-aalocation='past order detail'" />
				<mod:toolsitem template="toolsitem-download" skin="download skin-toolsitem-narrow" tag="li" downloadUrl="/my-account/order-history/order-details/${orderData.code}/download" attributes="data-aaorder-id='${orderData.code}',data-aainteraction='download order details',data-aalocation='past order detail'" />
				<mod:toolsitem template="toolsitem-lbl-order-detail-shopping-bulk" skin="with-label skin-toolsitem-shopping-bulk skin-toolsitem-narrow" tag="li" attributes="data-aaorder-id='${orderData.code}',data-aainteraction='add to shopping list',data-aalocation='past order detail'"/>
			</c:if>
		</ul>
	</div>

	<div class="bd__right">
		<ul class="tools-bar">
			<c:if test="${not empty orderData.entries}">
				<mod:toolsitem template="toolsitem-lbl-cart-bulk" skin="with-label skin-toolsitem-cart-bulk skin-toolsitem-narrow" tag="li" />
				<c:if test="${(orderData.status.code eq 'ERP_STATUS_SHIPPED')}">
					<c:choose>
						<c:when test="${isExportShop}">
							<mod:toolsitem template="toolsitem-lbl-return-items" skin="with-label skin-toolsitem-return-items skin-toolsitem-narrow" tag="li" downloadUrl="/cms/returnorrepair" />
						</c:when>
						<c:otherwise>
							<mod:toolsitem template="toolsitem-lbl-return-items" skin="with-label skin-toolsitem-return-items skin-toolsitem-narrow" tag="li" downloadUrl="/my-account/order-history/order-details/${orderData.code}/return-items" attributes="data-aaorder-id='${orderData.code}',data-aainteraction='return items',data-aalocation='past order detail'"/>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:if>
		</ul>
	</div>
</div>
