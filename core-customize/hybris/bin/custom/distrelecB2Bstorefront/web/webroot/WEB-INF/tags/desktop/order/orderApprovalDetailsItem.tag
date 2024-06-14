<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true"
	type="de.hybris.platform.commercefacades.order.data.AbstractOrderData"%>
<%@ attribute name="isOrderDetailsPage" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<div class="item_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h1>
			<spring:theme code="text.account.order.approval.title" />
		</h1>
	</div>
	<div class="item_container">
		<table>
			<thead>
				<tr>
					<th><span><spring:theme code="text.account.order.permission.title" /></span></th>
					<th><span><spring:theme code="text.account.order.approver.title" /></span></th>
					<th><span><spring:theme code="text.account.order.status.title" /></span></th>
					<th><span><spring:theme code="text.account.order.permission.notes"/></span></th>
				</tr>
			</thead>
			<tbody>
			<tbody>
				<c:forEach items="${order.b2bPermissionResult}"
					var="b2bPermissionResult">
					<tr>
						<td>${b2bPermissionResult.permissionTypeData.name}</td>
						<td>${b2bPermissionResult.approver}</td>
						<td>${b2bPermissionResult.statusDisplay}</td>
						<td>
							<c:choose>
							   <c:when test="${not empty b2bPermissionResult.approverNotes}">
							   		${b2bPermissionResult.approverNotes}
								</c:when>
								<c:otherwise>
									<span><spring:theme code="text.notAvailable"/></span>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			</tbody>
		</table>
	</div>
</div>

