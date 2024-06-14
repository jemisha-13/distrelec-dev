<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:accountNav selected="${cmsPage.label}" />
	<div class="span-20 last">
		<div class="item_container_holder">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h2><spring:theme code="text.account.invoiceHistory" text="Invoice History"/></h2>
			</div>
			<div class="item_container">
				<c:if test="${not empty searchPageData.results}">
					<p><spring:theme code="text.account.invoiceHistory.viewOrders" text="View your invoices"/></p>

					<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="/my-account/invoices?sort=${searchPageData.pagination.sort}" msgKey="text.account.invoiceHistory.page"/>

					<table id="invoice_history">
						<thead>
							<tr>
								<th id="header1"><spring:theme code="text.account.invoiceHistory.invoiceNumber" text="Invoice Number"/></th>
								<th id="header2"><spring:theme code="text.account.invoiceHistory.invoiceStatus" text="Invoice Status"/></th>
								<th id="header4"><spring:theme code="text.account.invoiceHistory.invoiceDate" text="Invoice Date"/></th>
								<th id="header4"><spring:theme code="text.account.invoiceHistory.invoiceDueDate" text="Invoice Due Date"/></th>
								<th id="header3"><spring:theme code="text.account.invoiceHistory.invoiceTotal" text="Invoice Total"/></th>
								<th id="header3"><spring:theme code="text.account.invoiceHistory.invoiceCurrency" text="Invoice Currency"/></th>
								<th id="header5"><spring:theme code="text.account.invoiceHistory.actions" text="Actions"/></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${searchPageData.results}" var="invoice">

								<%-- <c:url value="/my-account/order/${order.code}" var="myAccountOrderDetailsUrl"/> --%>

								<tr>
									<td headers="header1">
										<ycommerce:testId code="invoiceHistory_invoiceNumber_label">
											<p>${invoice.invoiceNumber}</p>
										</ycommerce:testId>
									</td>
									<td headers="header2">
										<ycommerce:testId code="invoiceHistory_invoiceStatus_label">
											<p>${invoice.invoiceStatus}</p>
										</ycommerce:testId>
									</td>
									<td headers="header3">
										<ycommerce:testId code="invoiceHistory_invoiceDate_label">
											<p>${invoice.invoiceDate}</p>
										</ycommerce:testId>
									</td>
									<td headers="header3">
										<ycommerce:testId code="invoiceHistory_invoiceDueDate_label">
											<p>${invoice.invoiceDueDate}</p>
										</ycommerce:testId>
									</td>
									<td headers="header3">
										<ycommerce:testId code="invoiceHistory_invoiceTotal_label">
											<p>${invoice.invoiceTotal}</p>
										</ycommerce:testId>
									</td>
									<td headers="header3">
										<ycommerce:testId code="invoiceHistory_invoiceCurrency_label">
											<p>${invoice.currency}</p>
										</ycommerce:testId>
									</td>
									
								</tr>
							</c:forEach>
						</tbody>
					</table>

					<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="/my-account/invoices?sort=${searchPageData.pagination.sort}" msgKey="text.account.invoiceHistory.page"/>

				</c:if>
				<c:if test="${empty searchPageData.results}">
					<p><spring:theme code="text.account.invoiceHistory.noOrders" text="You have no invoices"/></p>
				</c:if>
			</div>
		</div>
	</div>
</template:page>
