<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:url value="/my-account/request-invoice-payment-mode" var="requestInvoicePaymentMode" />

<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.title" var="requestToPayWithTitleLabel"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.pending" var="requestToPayWithPendingLabel"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.invoice.title" var="requestToPayWithInvoice"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.invoice.title.intial.pending" var="requestToPayWithInvoicePendingInitial"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.pending.review" var="sRequestPendingReview"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.pending.review.initial.message" var="sRequestPendingReviewInitial"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.pending.review.initial.title" var="sRequestPendingReviewInitialTitle"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.pending.after" var="sPendingTitle"/>
<spring:theme code="payment.mode.invoice.message" var="sInvoiceMessage"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.invoice.descr" var="sInvoiceRecieve"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.btn" var="sRequestInvoiceBtn"/>

<c:if test="${canRequestInvoicePaymentMode}">
    <c:choose>
        <c:when test="${not invoicePaymentModeRequested}">
            <%-- request to pay with button --%>
            <div class="list__item request-pay-invoice">
                <input id="requestInvoicePaymentMode" type="button" class="paymentInfo" data-form-action="${requestInvoicePaymentMode}"/>
                <span class="tick">
					<i class="fa fa-check-circle">&nbsp;</i>
				</span>
                <label for="paymentOption${paymentOption.code}">
                    <span class="small">${requestToPayWithInvoice}</span>
                    <span class="big"><spring:message code="payment.mode.invoice" text="payment.mode.invoice" /> <i class="fa fa-file-alt">&nbsp;</i></span>
                </label>
            </div>
            <div class="invoice-extra hidden">
                <div class="col-12 request-pay-message">
                    <p>${sInvoiceMessage}</p>
                </div>
                <p class="request-pay-invoice__txt">${sInvoiceRecieve}</p>
                <button class="request-pay-invoice__btn">${sRequestInvoiceBtn}</button>
            </div>
        </c:when>
        <c:when test="${invoicePaymentModeJustRequested}">
            <%-- invoice payment is just requested --%>
            <div class="list__item list__item--active requestedActive disabled">
                <input id="requestInvoicePaymentMode" type="button" class="paymentInfo" data-form-action="${requestInvoicePaymentMode}"/>
                <label>
                    <span class="small">${requestToPayWithInvoice}</span>
                    <span class="big"><spring:message code="payment.mode.invoice" text="payment.mode.invoice" /> <i class="fa fa-file-alt">&nbsp;</i></span>
                </label>
            </div>
            <div class="col-12 request-pay-message">
                <p>${sInvoiceMessage}</p>
            </div>
            <div class="col-12 request-pending">
                <i class="fa fa-check-circle"></i>
                <div class="request-pending__title">
                    <strong class="title">${sRequestPendingReviewInitialTitle}</strong>
                    <p>${sRequestPendingReviewInitial}</p>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <%-- pending invoice payment request --%>
            <div class="list__item list__item--active requestedActive disabled">
                <input id="requestInvoicePaymentMode" type="button" class="paymentInfo" data-form-action="${requestInvoicePaymentMode}"/>
                <label>
                    <span class="small">${requestToPayWithInvoice}</span>
                    <span class="big">
						<spring:message code="checkoutpaymentoptionslist.requesttopaywith.pending"/>
					</span>
                </label>
            </div>
            <div class="col-12 request-pay-message">
                <p>${sInvoiceMessage}</p>
            </div>
            <div class="col-12 request-pending">
                <i class="fa fa-check-circle"></i>
                <div class="request-pending__title">
                    <strong class="title">${sPendingTitle}</strong>
                    <p>${sRequestPendingReview}</p>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</c:if>