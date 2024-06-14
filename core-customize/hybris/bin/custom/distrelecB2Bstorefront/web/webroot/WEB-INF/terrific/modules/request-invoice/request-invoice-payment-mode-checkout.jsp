<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.pending.review.initial.message" var="sRequestPendingReviewInitial"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.pending.review.initial.title" var="sRequestPendingReviewInitialTitle"/>
<spring:theme code="payment.mode.invoice.message" var="sInvoiceMessage"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.invoice.descr" var="sInvoiceRecieve"/>
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith.btn" var="sRequestInvoiceBtn"/>
<spring:theme code="checkoutpaymentoptionslist.order.complete.title" var="sInvoiceTitle"/>
<spring:theme code="checkoutpaymentoptionslist.order.complete.subtitle" var="sInvoiceSubTitle"/>
<spring:theme code="checkoutpaymentoptionslist.order.complete.button" var="sInvoiceBtn"/>

<c:if test="${canRequestInvoicePaymentMode}">
    <div class="pay-invoice" id="requestInvoiceRoot">
    <c:choose>
        <c:when test="${not invoicePaymentModeRequested}">
            <%-- request to pay with button --%>
            <div class="pay-invoice__item">
                <strong>${sInvoiceTitle}</strong>
                <p>${sInvoiceSubTitle}</p>
                <label v-on:click="clickInvoiceBtn" for="requestInvoicePaymentMode" class="button">
                    <span>${sInvoiceBtn}</span>
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
            <div class="pay-invoice__item requestedActive">
                <div class="pay-invoice__grouped">
                    <i class="fa fa-check-circle"></i>
                    <div class="title">
                        <strong>${sRequestPendingReviewInitialTitle}</strong>
                        <p>${sRequestPendingReviewInitial}</p>
                    </div>
                </div>
            </div>
            <div class="pay-invoice__item__message">
                <p>${sInvoiceMessage}</p>
            </div>
        </c:when>
        <c:otherwise>
            <%-- pending invoice payment request --%>
            <div class="pay-invoice__item requestedActive">
                <div class="pay-invoice__grouped">
                    <i class="fa fa-check-circle"></i>
                    <div class="title">
                        <strong>${sRequestPendingReviewInitialTitle}</strong>
                        <p>${sRequestPendingReviewInitial}</p>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
    </div>
</c:if>