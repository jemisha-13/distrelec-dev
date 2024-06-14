<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
{
	"page": ${orderHistoryForm.page},
	"nbPages": ${searchPageData.pagination.numberOfPages},
	"orders": [
		<c:if test="${not empty searchPageData.results}">
			<c:forEach items="${searchPageData.results}" var="order" varStatus="count">
				<c:choose>
					<c:when test="${order.status.code == 'ERP_STATUS_SHIPPED' || order.status.code == 'ERP_STATUS_RECIEVED'}">
						<c:set var="iconStatus" value="ok" />
					</c:when>
					<c:otherwise>
						<c:set var="iconStatus" value="nok" />
					</c:otherwise>
				</c:choose>
				<spring:message code="text.store.dateformat" var="datePattern" />
				<c:url value="/my-account/order-history/order-details/${order.code}" var="myAccountOrderDetailsUrl" />
				{
					"orderCode": "${order.code}",
					"orderDate": "<fmt:formatDate value='${order.placed}' dateStyle='short' timeStyle='short' type='date' pattern='${datePattern}' />",
					"orderTotal": "${order.currency}&nbsp;${order.total}",
					"iconStatus": "${iconStatus}",
					"orderStatus": "<spring:message code='orderOverviewBox.state.${order.status.code}' />",
					"orderedByLabel": "<spring:message code='accountlist.orderHistoryList.orderedBy' />",
					"orderedBy": "${order.userFullName}",
					"invoiceIdsLabel": "<spring:message code='accountlist.orderHistoryList.invoiceNr' />",
					"invoiceIds": "${order.invoiceIds}",
					"deliveryDateLabel": "<spring:message code='accountlist.orderHistoryList.estDeliveryDate' />",
					"deliveryDate": "<fmt:formatDate value='${order.deliveryDate}' dateStyle='short' type='date' pattern='${datePattern}' />",
					"detailUrl": "${myAccountOrderDetailsUrl}"
				}${(fn:length(searchPageData.results)-1) > count.index ? ',' : ''}
			</c:forEach>
		</c:if>
	]
}