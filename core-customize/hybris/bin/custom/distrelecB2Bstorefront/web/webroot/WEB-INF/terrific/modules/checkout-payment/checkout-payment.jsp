<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<spring:theme code="checkout.payment.title" var="pay" />

<c:url value="/checkout/payment/hiddenPaymentForm" var="hiddenPaymentFormUrl" />

<button id="checkoutPaymentPayButton" name="button" onclick="makePayment(this)" class="mat-button mat-button--action-green btn-checkout fb-order-purchase" data-aainteraction="proceed to checkout">
    ${pay}
</button>

<iframe id="iframe" class="cc-form-payment" no-fastclick src="" data-src1="${hiddenPaymentFormUrl}" onload="reziseFunction()"></iframe>

<script type="text/javascript">
    function makePayment(button) {
        $(button).attr("disabled", true);
        var srcVar = $('.cc-form-payment').data("src1");
        $('.cc-form-payment').attr('src',srcVar);
    }
</script>