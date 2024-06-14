<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="text.store.dateformat" var="datePattern" />
<spring:message code="accountlist.invoiceHistoryList.invoiceNr" var="sInvoiceNumber" />
<spring:message code="accountlist.invoiceHistoryList.invoiceTotal" var="sInvoiceTotal" />
<spring:message code="accountlist.invoiceHistoryList.invoiceSubTotal" var="sInvoiceSubTotal" />
<spring:message code="accountlist.invoiceHistoryList.invoiceTax" var="sInvoiceTax" />
<spring:message code="accountlist.invoiceHistoryList.invoiceAmount" var="sInvoiceAmountTitle" />
<spring:message code="accountlist.invoiceHistoryList.contactName" var="sInvoiceContactName" />
<spring:message code="accountlist.invoiceHistoryList.DueDate" var="sInvoiceDueDate" />
<spring:message code="accountlist.invoiceHistoryList.invoiceDate" var="sInvoiceDate" />
<spring:message code="accountlist.invoiceHistoryList.invoiceStatus" var="sInvoiceStatus" />
<spring:message code="accountlist.invoiceHistoryList.emailInvoice" var="sEmailInvoice" />
<spring:message code="accountlist.invoiceHistoryList.queryInvoice" var="sQueryInvoice" />
<spring:message code="accountlist.invoiceHistoryList.viewPrintInvoice" var="sViewPrintInvoice" />

<h2 class="title"><spring:message code="accountlist.invoiceHistoryList.title" /></h2>

<!-- selectbox: order by -->
<div class="action-box">
	<mod:account-order template="invoice-history" skin="print" />
	<mod:productlist-tools template="order-history" />
</div>

<%-- determine pageSize since BE is not able to store it --%>
<c:set var="resultCount" value="${fn:length(searchPageData.results)}" />
<c:choose>
	<c:when test="${resultCount > 25 && resultCount <= 50}">
		<c:set var="pageSize" value="50" />
	</c:when>
	<c:when test="${resultCount > 10 && resultCount <= 25}">
		<c:set var="pageSize" value="25" />
	</c:when>
	<c:otherwise>
		<c:set var="pageSize" value="10" />
	</c:otherwise>
</c:choose>

<!-- list: user management list -->
<ul class="data-list"
	data-current-query-url="/my-account/invoice-history"
	data-page="${searchPageData.pagination.currentPage}"
	data-page-size="${pageSize}"

	data-sort="${sortCode}"
	data-status="${invoiceHistoryForm.status}"
	data-invoice-number="${invoiceHistoryForm.orderNumber}"
	data-from-date="${invoiceHistoryForm.fromDate}"
	data-to-date="${invoiceHistoryForm.toDate}"
	>
	<c:choose>
		<c:when test="${empty searchPageData.results}">
			<li class="row">
				<span class="no-invoices"><spring:theme code="text.account.invoiceHistory.noInvoices" text="You have no invoices" /></span>
			</li>
		</c:when>
		<c:otherwise>
			<c:forEach items="${searchPageData.results}" var="invoice">
				<c:choose>
					<c:when test="${invoice.invoiceStatus eq 'PAID' || invoice.invoiceStatus eq 'STORNO'}">
						<c:set var="iconStatus" value="ok" />
					</c:when>
					<c:otherwise>
						<c:set var="iconStatus" value="nok" />
					</c:otherwise>
				</c:choose>
				<%-- c:url value="/my-account/invoice-history/invoice-details/${invoice.invoiceNumber}" var="myAccountInvoiceDetailsUrl" / --%>
				<c:url value="${not empty invoice.invoiceDocumentURL ? invoice.invoiceDocumentURL : '#'}" var="myAccountInvoiceDetailsUrl" />
				<li class="row">
					<div class="invoice-manager data cf">
						<div class="invoice-manager__content">
							<div class="invoice-manager__item">
								<dl>
									<dt class="label">${sInvoiceNumber}</dt>
									<dd class="value">${invoice.invoiceNumber}</dd>
									<dt class="label">${sInvoiceContactName}</dt>
									<c:forEach items="${invoice.relatedOrders}" var="order">
										<dd class="value">${order.contactName}</dd>
									</c:forEach>
									<dt class="label"><spring:message code="accountlist.invoiceHistoryList.orderNr" /></dt>
									<c:forEach items="${invoice.relatedOrders}" var="order">
										<dd class="value">${order.orderNumber}</dd>
									</c:forEach>
								</dl>
							</div>
							<div class="invoice-manager__item">
								<dl>
									<dt class="label">${sInvoiceDate}:</dt>
									<dd class="value"><fmt:formatDate value="${invoice.invoiceDate}" dateStyle="short" timeStyle="short" type="date"  pattern="${datePattern}" /></dd>

                                    <c:choose>
                                        <c:when test="${invoice.invoiceStatus eq 'PAID'}">
                                            <dt class="label ellipsis">${sInvoiceStatus}:</dt>
                                            <dd class="value ${iconStatus}"><spring:message code="invoiceOverviewBox.state.${invoice.invoiceStatus}"/></dd>
                                        </c:when>
                                        <c:otherwise>
                                            <dt class="label ellipsis">${sInvoiceDueDate}:</dt>
                                            <dd class="value"><fmt:formatDate value="${invoice.invoiceDueDate}" dateStyle="short" timeStyle="short" type="date"  pattern="${datePattern}" /></dd>
                                        </c:otherwise>

                                    </c:choose>

                                </dl>
							</div>
							<div class="invoice-manager__item">
								<h2>${sInvoiceAmountTitle}:</h2>
								<div class="holder">
									<dl>
										<dt class="label ellipsis">${sInvoiceSubTotal}</dt>
										<dd class="value"><format:price format="defaultSplit" displayValue="${invoice.invoiceTotalWithoutTaxes}" fallBackCurrency="${invoice.currency}" /></dd>
									</dl>
									<dl>
										<dt class="label ellipsis">${sInvoiceTax}</dt>
										<dd class="value"><format:price format="defaultSplit" displayValue="${invoice.invoiceTaxes}" fallBackCurrency="${invoice.currency}" /></dd>
									</dl>
									<dl>
										<dt class="label ellipsis">${sInvoiceTotal}</dt>
										<dd class="value"><format:price format="defaultSplit" displayValue="${invoice.invoiceTotal}" fallBackCurrency="${invoice.currency}" /></dd>
									</dl>
								</div>
							</div>
						</div>
						<div class="invoice-manager__actions">
							<c:if test="${not empty invoice.invoiceDocumentURL}">
								<a href="${myAccountInvoiceDetailsUrl}" class="morePdf" target="_blank"><i></i><span class="ellipsis">${sViewPrintInvoice}</span></a>
							</c:if>
						</div>
					</div>
				</li>
			</c:forEach>
		</c:otherwise>
	</c:choose>
	<li class="rowtemplate">
		<ul class="data cf">
			<li class="entry">
				<dl>
					<dt class="label"><spring:message code="accountlist.orderHistoryList.orderNr" /></dt>
					<dd class="value code"></dd>
					<dt class="label"><spring:message code="accountlist.orderHistoryList.orderDate" /></dt>
					<dd class="value orderdate"></dd>
				</dl>
			</li>
			<li class="entry">
				<dl>
					<dt class="label"><spring:message code="accountlist.orderHistoryList.orderTotal" /></dt>
					<dd class="value ordertotal"></dd>
					<dt class="label"><spring:message code="accountlist.orderHistoryList.orderStatus" /></dt>
					<dd class="value status"><i></i> </dd>
				</dl>
			</li>
			<li class="entry">&nbsp;
			</li>
			<li class="entry">&nbsp;
			</li>
			<li class="action">
				<a href="${myAccountInvoiceDetailsUrl}" class="more"><i></i></a>
			</li>
		</ul>
	</li>
</ul>
<div class="ajax-invoices-loader">
	<div class="background-overlay"></div>
	<div class="message-wrapper">
		<p class="loading-message">
			<i></i>
			<spring:message code="accountlist.orderHistoryList.loadInvoices" />
		</p>
	</div>
</div>

<c:if test="${not empty searchPageData.results}">
	<%-- ProductListPagination --%>
	<mod:productlist-pagination myAccountSearchPageData="${searchPageData}" template="myaccount" skin="my-account" />
</c:if>

<%-- doT-Template invoice-history --%>
<script id="tmpl-invoice-history" type="text/x-template-dotjs">
	<li class="row">
		<ul class="data cf">
			<li class="entry">
				<dl>
					<dt class="label"><spring:message code="accountlist.invoiceHistoryList.invoiceNr" /></dt>
					<dd class="value">{{= it.invoiceNumber}}</dd>
					<dt class="label"><spring:message code="accountlist.invoiceHistoryList.invoiceDate" /></dt>
					<dd class="value">{{= it.invoiceDate}}</dd>
				</dl>
			</li>
			<li class="entry">
				<dl>
					<dt class="label"><spring:message code="accountlist.invoiceHistoryList.invoiceTotal" /></dt>
					<dd class="value">{{= it.invoiceTotal}}</dd>
					<dt class="label"><spring:message code="accountlist.invoiceHistoryList.invoiceStatus" /></dt>
					<dd class="value {{= it.iconStatus}}"><i></i>&nbsp;
						{{? it.invoiceStatus == "OPEN" }}
							<spring:message code="invoiceOverviewBox.state.OPEN"/>
						{{?}}
						{{? it.invoiceStatus == "PAID" }}
							<spring:message code="invoiceOverviewBox.state.PAID"/>
						{{?}}
						{{? it.invoiceStatus == "STORNO" }}
							<spring:message code="invoiceOverviewBox.state.STORNO"/>
						{{?}}
					</dd>
				</dl>
			</li>
			<li class="entry">
				<dl>
					<dt class="label"><spring:message code="accountlist.invoiceHistoryList.contactName" /></dt>
					<dd class="value">{{= it.contactName}}</dd>
				</dl>
			</li>
			<li class="entry">
				<dl>
					<dt class="label"><spring:message code="accountlist.invoiceHistoryList.orderNr" /></dt>
					<dd class="value">{{= it.orderNumber}}</dd>
				</dl>
			</li>
			<li class="action">
				{{? it.detailUrl != ""}}
					<a href="{{= it.detailUrl}}" class="morePdf" target="_blank"><i></i></a>
				{{?}}
			</li>
		</ul>
	</li>
</script>
