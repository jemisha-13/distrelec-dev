<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>

<c:set var="phaseOut" value="false" />
<spring:message code="product.image.missing" text="Image not found" var="sImageMissing"/>
<spring:message code="text.store.dateformat" var="datePattern" />

<json:object>
	<json:object name="paging">
		<json:property name="isLastPage" value="${isLastPage}" />
	</json:object>
	<json:object name="pagination">
		<json:property name="firstUrl" value="${searchPageData.pagination.firstUrl}" />
		<json:property name="prevUrl" value="${searchPageData.pagination.prevUrl}" />
		<json:property name="showPrevButton">
			${(searchPageData.pagination.currentPage + 1) > 1}
		</json:property>
		
		<json:property name="numberOfPages" value="${searchPageData.pagination.numberOfPages}" />

		<json:property name="currentPage" value="${searchPageData.pagination.currentPage}" />
		
		<json:property name="nextUrl" value="${searchPageData.pagination.nextUrl}" />
		<json:property name="showNextButton">
			${((searchPageData.pagination.currentPage + 1) + 1) <= searchPageData.pagination.numberOfPages}
		</json:property>
		<json:property name="lastUrl" value="${searchPageData.pagination.lastUrl}" />
		<json:array name="pages">
			<c:forEach var="i" begin="1" end="${searchPageData.pagination.numberOfPages}">
				<json:object>
					<json:property name="pageNr" value="${i}" />
				</json:object>
			</c:forEach>
		</json:array>
	</json:object>
	<json:array name="orders" items="${searchPageData.results}" var="order">
		<json:object>
			<json:property name="iconStatus" value="${order.status.code == 'ERP_STATUS_SHIPPED' || order.status.code == 'ERP_STATUS_RECIEVED' ? 'ok' : 'nok'}" />

			<json:property name="orderCode" value="${order.code}" />

			<json:property name="orderDate">
				<fmt:formatDate value='${order.placed}' dateStyle='short' timeStyle='short' type='date' pattern='${datePattern}' />
			</json:property>


			<c:set var="val" value="${order.total}" /> 
			<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${val}" var="pat" />
			
			
	
			<json:property name="orderTotal" value="${order.currency} ${pat}" />

			<json:property name="orderStatus">
				<spring:message code='orderOverviewBox.state.${order.status.code}' />
			</json:property>

			<json:property name="orderedByLabel">
				<spring:message code='accountlist.orderHistoryList.orderedBy' />
			</json:property>

			<json:property name="orderedBy" value="${order.userFullName}" />

			<json:property name="invoiceIdsLabel">
				<spring:message code='accountlist.orderHistoryList.invoiceNr' />
			</json:property>

			<json:property name="invoiceIds" value="${order.invoiceIds}" />

			<json:property name="deliveryDateLabel">
				<spring:message code='accountlist.orderHistoryList.estDeliveryDate' />
			</json:property>

			<json:property name="deliveryDate">
				<fmt:formatDate value='${order.deliveryDate}' dateStyle='short' type='date' pattern='${datePattern}' />
			</json:property>

			<c:url value="/my-account/order-history/order-details/${order.code}" var="myAccountOrderDetailsUrl" />
			<json:property name="detailUrl" value="/my-account/order-history/order-details/${order.code}" />
		</json:object>
	</json:array>
</json:object>
