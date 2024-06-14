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
	<json:array name="invoices" items="${searchPageData.results}" var="invoice">
		<json:object>
			<json:property name="iconStatus">
				<c:choose>
					<c:when test="${invoice.invoiceStatus eq 'PAID' || invoice.invoiceStatus eq 'STORNO'}">
						ok
					</c:when>
					<c:otherwise>
						nok
					</c:otherwise>
				</c:choose>
			</json:property>

			<json:property name="invoiceNumber" value="${invoice.invoiceNumber}" />

			<json:property name="invoiceDate">
				<spring:message code="text.store.dateformat" var="datePattern" />
				<fmt:formatDate value='${invoice.invoiceDate}' dateStyle='short' timeStyle='short' type='date' pattern='${datePattern}' />
			</json:property>

			<json:property name="invoiceTotal" value="${invoice.currency} ${invoice.invoiceTotal}" />

			<json:property name="invoiceStatus">
				${invoice.invoiceStatus}
			</json:property>

			<json:property name="contactName">
				<c:forEach items="${invoice.relatedOrders}" var="order">
					${order.contactName}
				</c:forEach>
			</json:property>

			<json:property name="orderNumber">
				<c:forEach items="${invoice.relatedOrders}" var="order">
					${order.orderNumber}
				</c:forEach>
			</json:property>

			<c:url value="${not empty invoice.invoiceDocumentURL ? invoice.invoiceDocumentURL : '#'}" var="myAccountInvoiceDetailsUrl" />
			<json:property name="detailUrl" value="${myAccountInvoiceDetailsUrl}" />
		</json:object>
	</json:array>
</json:object>