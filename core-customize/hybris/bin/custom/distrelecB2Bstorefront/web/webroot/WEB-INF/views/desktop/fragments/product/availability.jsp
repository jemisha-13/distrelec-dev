<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<json:object>
	<json:object name="availabilityData">
		<json:array name="products" items="${availabilityData}" var="availabilityEntry">
			<json:object>
				<json:object name="${availabilityEntry.productCode}">
					<json:property name="stockLevelTotal" value="${availabilityEntry.stockLevelTotal}"/>
					<c:if test="${availabilityEntry.detailInfo}">
						<json:property name="stockLevelBackorderLabel" value="${availabilityEntry.deliveryTimeBackorder}"/>
						<json:property name="backorderQuantity" value="${availabilityEntry.backorderQuantity}"/>
						<json:property name="leadTimeErp" value="${availabilityEntry.leadTimeErp}"/>
						<json:property name="backorderDeliveryDate">
							<fmt:formatDate value="${availabilityEntry.backorderDeliveryDate}"/>
						</json:property>
						<c:if test="${not empty availabilityEntry.stockLevelPickup}">
							<json:array name="stockLevelPickup" items="${availabilityEntry.stockLevelPickup}" var="stockLevelPickupEntry">
								<json:object>
									<json:property name="warehouseCode" value="${stockLevelPickupEntry.warehouseCode}"/>
									<json:property name="warehouseName" value="${stockLevelPickupEntry.warehouseName}"/>
									<json:property name="stockLevel" value="${stockLevelPickupEntry.stockLevel}"/>
								</json:object>
							</json:array>
						</c:if>
						<c:if test="${not empty availabilityEntry.stockLevels}">
							<json:array name="stockLevels" items="${availabilityEntry.stockLevels}" var="stockLevel">
								<json:object>
									<json:property name="warehouseCode" value="${stockLevel.warehouseId}"/>
									<json:property name="available" value="${stockLevel.available}"/>
									<json:property name="deliveryTime" value="${stockLevel.deliveryTime}"/>
									<json:property name="leadTime" value="${stockLevel.leadTime}"/>
									<json:property name="fast" value="${stockLevel.fast}"/>
									<json:property name="external" value="${stockLevel.external}"/>
									<json:property name="isWaldom" value="${stockLevel.waldom}"/>
									<json:property name="replenishmentDeliveryTime" value="${stockLevel.replenishmentDeliveryTime}"/>
									<json:property name="replenishmentDeliveryTime2" value="${stockLevel.replenishmentDeliveryTime2}"/>
									<json:property name="mview" value="${stockLevel.mview}"/>
								</json:object>
							</json:array>
						</c:if>
					</c:if>
				</json:object>
			</json:object>
		</json:array>
	</json:object>
</json:object>
