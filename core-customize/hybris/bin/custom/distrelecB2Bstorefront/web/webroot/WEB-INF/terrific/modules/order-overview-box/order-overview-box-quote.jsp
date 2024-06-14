<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="text.store.dateformat" var="datePattern" />
<c:set var="statusClass" value="${quotationData.status.code == '03' && quotationData.purchasable ? '20' : quotationData.status.code}" />

<a class="btn btn-secondary btn-back" href="<c:url value="/my-account/quote-history" />"><spring:message code="text.back" text="Back" /><i></i></a>
  
<ul class="data-list">
	<li class="row">
		<h3 class="base title"><spring:message code="quoteOverviewBox.title"/></h3>
		<ul class="data cf quotes-overview">
			<li class="entry_admin">
				<dl>
					<dt class="label"><spring:message code="quotationhistory.expirydate"/></dt>
					<dd class="value"><fmt:formatDate value="${quotationData.quotationExpiryDate}" dateStyle="long" timeStyle="short" type="date" pattern="${datePattern}" /></dd>
				</dl>
			</li>
			<li class="entry_admin">
				<dl>
					<dt class="label"><spring:message code="quotations.quotationId"/> </dt>
					<dd class="value">${quotationData.quotationId}</dd>
				</dl>
			</li>
			<li class="entry_admin">
				<dl>
					<dt class="label"><spring:message code="accountlist.approvalRequest.requestedBy"/>  </dt>
					<dd class="value">${quotationData.customerName}</dd>
				</dl>
			</li> 
			<li class="entry_admin">
				<dl>
					<dt class="label"><spring:message code="accountlist.quotationHistoryList.POnumber" text="Quote reference"/></dt>
					<dd class="value">${not empty quotationData.poNumber ? quotationData.poNumber : '-'}</dd>
				</dl>
			</li>
			<li class="entry_admin">
				<dl>
					<dt class="label"><spring:message code="text.account.order.status.title"/>    </dt>
					<dd class="value status-text status-${statusClass}">${quotationData.status.name}</dd>
				</dl>
			</li>
			<li class="entry_admin button-cart">
				<dl>
					<dt class="label"></dt>
					<dd class="value">
						<c:if test="${not empty quotationData.quotationDocURL}">
							<a href="${quotationData.quotationDocURL}" target="_blank" data-aainteraction="file download" data-quoteID="${quotationData.quotationId}" class="pdf-quotes">
								<i class="far fa-file-pdf" aria-hidden="true"></i>
							</a>
						</c:if>
						<c:if test="${quotationData.purchasable}" >
							<a class="ico ico-cart btn-add-to-cart fb-add-to-cart" data-quotation-id="${quotationData.quotationId}" title="<spring:message code='toolsitem.add.cart' />"data-product-code=""><i></i></a>
						</c:if>
						<c:choose>
							<c:when test="${quotationData.resubmitted eq true and not empty quotationData.childQuote}">
								<a class="resubmit active" data-quote-id="${quotationData.quotationId}"
								   data-aainteraction="resubmit quotation"
								   data-resubmitted-text="<spring:message code="quote.resubmitted.link"/>"
								   href="#">
									<spring:message code="quote.resubmitted.link"/>
								</a>
								<label class="resubmit--label"><spring:message code="quote.resubmitted.new.quote" /></label>
								<a class="resubmit--childQuote" href="${quotationData.resubmitOrChildQuotationUrl}" title="<spring:message code="quote.resubmitted.new.quote" />">
										${quotationData.childQuote}
								</a>
							</c:when>
							<c:when test="${quotationData.status.code == '04' and quotationData.resubmitted eq false}">
								<a class="resubmit <c:if test="${isCustomerOverQuotationLimit eq true}">limitPopUp</c:if>" data-quote-id="${quotationData.quotationId}"
								   data-aainteraction="resubmit quotation"
								   data-resubmitted-text="<spring:message code="quote.resubmitted.link"/>"
								   href="#" title="<spring:message code="quote.resubmit.link.tooltip"/>">
									<spring:message code="quote.resubmit.link"/>
								</a>
							</c:when>
							<c:when test="${quotationData.resubmitted eq true and empty quotationData.childQuote}">
								<a class="resubmit active" data-quote-id="${quotationData.quotationId}"
								   data-aainteraction="resubmit quotation"
								   data-resubmitted-text="<spring:message code="quote.resubmitted.link"/>"
								   href="#" title="<spring:message code="quote.resubmitted.link.tooltip"/>">
									<spring:message code="quote.resubmitted.link"/>
								</a>
							</c:when>
						</c:choose>
					</dd>
				</dl>
			</li>
		</ul>
	</li>
</ul>
<mod:loading-state skin="loading-state hidden" />
