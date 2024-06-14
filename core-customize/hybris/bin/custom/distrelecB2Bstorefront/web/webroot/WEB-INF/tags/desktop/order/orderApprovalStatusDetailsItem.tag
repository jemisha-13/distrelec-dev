<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="orderApprovalData" required="true"
	type="de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData"%>
<%@ attribute name="isOrderDetailsPage" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order"%>


<div class="item_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h1>
			<spring:theme
				code="text.account.orderApprovalDetails.orderStatusDetails.label"
				text="Order Status Details" />
		</h1>
	</div>

	<div class="item_container">
		<div class="span-80 left">
			<table class="table_budget">
				<tr>
					<td><spring:theme
							code="text.account.orderApprovalDetails.OrderNumber"
							text="Order number" />:</td>
					<td>${orderApprovalData.b2bOrderData.code}</td>
				</tr>

				<tr>
					<td><spring:theme
							code="text.account.orderApprovalDetails.orderPlacedBy"
							text="Order placed by" />:</td>
					<td>${orderApprovalData.b2bOrderData.b2bCustomerData.name}</td>
				</tr>

				<tr>
					<td><spring:theme
							code="text.account.orderApprovalDetails.purchaseOrderNumber"
							text="P.O.No" />:</td>
					<td>${orderApprovalData.b2bOrderData.purchaseOrderNumber}</td>
				</tr>

			</table>
		</div>
		<div class="span-40 left center">
			<table class="table_budget">
				<tr>
					<td><spring:theme
							code="text.account.orderApprovalDetails.parentBusinessUnit"
							text="Parent Business Unit" />:</td>
					<td>${orderApprovalData.b2bOrderData.costCenter.unit.name}</td>
				</tr>
				<c:if
					test="${orderApprovalData.b2bOrderData.paymentType.code eq 'ACCOUNT'}">
					<tr>
						<td><spring:theme
								code="text.account.orderApprovalDetails.costCenter"
								text="Cost Center" />:</td>
						<td>${orderApprovalData.b2bOrderData.costCenter.code}</td>
					</tr>

					<tr>
						<td><spring:theme
								code="text.account.orderApproval.orderStatus"
								text="Order Status" />:</td>
						<td>${orderApprovalData.b2bOrderData.statusDisplay}</td>
					</tr>
				</c:if>
			</table>
		</div>
	</div>
</div>

