<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="text.store.dateformat" var="datePattern" />
<h2 class="title"><spring:message code="accountlist.quotationHistoryList.title" /></h2>

<!-- selectbox: order by -->
<div class="action-box">
	<mod:account-order template="quotation-history" skin="print" />
	<mod:productlist-tools template="quotation-history" />
</div>

<c:set var="isLoggedIn" value="false" />
<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
	<c:set var="isLoggedIn" value="true" />
</sec:authorize>

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

<c:if test="${searchPageData.pagination.pageSize != null && searchPageData.pagination.pageSize > pageSize}">
	<c:set var="pageSize" value="${searchPageData.pagination.pageSize}" />
</c:if>

<spring:message code="form.global.error.erpcommunication" var="sError" text="An error occured" />
<!-- list: quotation history list -->
<ul class="data-list"
	data-current-query-url="/my-account/quote-history"
	data-page="${searchPageData.pagination.currentPage}"
	data-page-size="${pageSize}"

	data-sort="${sortCode}"
	data-status="${quotationHistoryForm.status}"
	data-quotation-number="${quotationHistoryForm.quotationId}"
	data-from-date="${quotationHistoryForm.fromDate}"
	data-to-date="${quotationHistoryForm.toDate}"
	
	data-error-text="${sError}"
	>
	<c:choose>
		<c:when test="${empty searchPageData.results}">
			<li class="row">
				<span class="no-quotations"><spring:theme code="text.account.quotationHistory.noQuotations" text="You have no quotations" /></span>
			</li>
		</c:when>
		<c:otherwise>
			<c:forEach items="${searchPageData.results}" var="quotation">
				<c:url value="/my-account/quote-history/quote-details/${quotation.quotationId}" var="myAccountQuotationDetailsUrl" />
				<c:set var="statusClass" value="${quotation.status.code == '03' && quotation.purchasable ? '20' : quotation.status.code}" />
				<li class="row">
					<ul class="data cf">
						<li class="entry e1">
							<dl>
								<dt class="label"><spring:message code="accountlist.quotationHistoryList.quotationNr" text="Quote no." /></dt>
								<dd class="value">${quotation.quotationId}</dd>
								<dt class="label"><spring:message code="accountlist.quotationHistoryList.quotationStatus" text="Quote status"/></dt>
								<dd class="value status-text status-${statusClass}">${quotation.status.name}</dd>
							</dl>
						</li>
						<li class="entry e2">
							<dl>
								<dt class="label"><spring:message code="accountlist.quotationHistoryList.quotationTotal" text="Quote total"/></dt>
								<dd class="value"><format:price format="defaultSplit" displayValue="${quotation.total.value}" fallBackCurrency="${quotation.currency.isocode}" /></dd>
								<dt class="label"><spring:message code="accountlist.quotationHistoryList.quotationExpiryDate" text="Expiry date" /></dt>
								<dd class="value">
									<c:choose>
										<c:when test="${not empty quotation.quotationExpiryDate}">
											<fmt:formatDate value="${quotation.quotationExpiryDate}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" />
										</c:when>
										<c:otherwise> - </c:otherwise>
									</c:choose>
								</dd>
							</dl>
						</li>
						<li class="entry e3">
							<dl>
								<dt class="label"><spring:message code="accountlist.quotationHistoryList.POnumber" text="Quote reference"/></dt>
								<dd class="value">${quotation.poNumber}&nbsp;</dd>
								<dt class="label"><spring:message code="accountlist.quotationHistoryList.customerName" text="Ordered by"/></dt>
								<dd class="value">${quotation.customerName}</dd>
							</dl>
						</li>
						<c:set var="test" value="true" />
						<c:set var="childQuoteTest" value="32323232" />
						<li class="action">
							<a href="${myAccountQuotationDetailsUrl}" class="more"><i></i></a>
							<br />
							<c:if test="${not empty quotation.quotationDocURL}">
								<a href="${quotation.quotationDocURL}" target="_blank" data-aainteraction="file download" data-quoteID="${quotation.quotationId}" class="pdf-quotes">
									<i class="far fa-file-pdf" aria-hidden="true"></i>
								</a>
							</c:if>
							<c:if test="${quotation.purchasable}">
								<a href="#" class="btn-add-to-cart fb-add-to-cart" data-quotation-id="${quotation.quotationId}"><i></i></a>
							</c:if>
							<c:choose>
								<c:when test="${quotation.resubmitted eq true and not empty quotation.childQuote}">
									<a class="resubmit active" data-quote-id="${quotation.quotationId}"
									   data-aainteraction="resubmit quotation"
									   data-resubmitted-text="<spring:message code="quote.resubmitted.link"/>"
									   href="#">
										<spring:message code="quote.resubmitted.link"/>
									</a>
									<label class="resubmit--label"><spring:message code="quote.resubmitted.new.quote" /></label>
									<a class="resubmit--childQuote" href="${quotation.resubmitOrChildQuotationUrl}" title="<spring:message code="quote.resubmitted.new.quote" />">
										${quotation.childQuote}
									</a>
								</c:when>
								<c:when test="${quotation.status.code == '04' and quotation.resubmitted eq false}">
									<a class="resubmit <c:if test="${isCustomerOverQuotationLimit eq true}">limitPopUp</c:if>" data-quote-id="${quotation.quotationId}"
									   data-aainteraction="resubmit quotation"
									   data-resubmitted-text="<spring:message code="quote.resubmitted.link"/>"
									   href="#" title="<spring:message code="quote.resubmit.link.tooltip"/>">
										<spring:message code="quote.resubmit.link"/>
									</a>
								</c:when>
								<c:when test="${quotation.resubmitted eq true and empty quotation.childQuote}">
									<a class="resubmit active" data-quote-id="${quotation.quotationId}"
									   data-aainteraction="resubmit quotation"
									   data-resubmitted-text="<spring:message code="quote.resubmitted.link"/>"
									   href="#" title="<spring:message code="quote.resubmitted.link.tooltip"/>">
										<spring:message code="quote.resubmitted.link"/>
									</a>
								</c:when>
							</c:choose>
						</li>
					</ul>
				</li>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</ul>
 <c:if test="${not empty searchPageData.results}">
	<%-- ProductListPagination --%>
	<mod:productlist-pagination myAccountSearchPageData="${searchPageData}" template="myaccount" skin="my-account" htmlClasses="quote" />
</c:if>

<mod:lightbox-quotation-confirmation template="form" />
<mod:loading-state skin="loading-state hidden" />
