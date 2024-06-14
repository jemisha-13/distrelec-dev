<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:if test="${cartData.b2bCustomerData.budget.exceededYearlyBudget gt 0}">
    <c:set var="yearlyBudgetExceeds" value="${true}" />
</c:if>
<c:if test="${cartData.b2bCustomerData.budget.exceededOrderBudget gt 0}">
    <c:set var="orderBudgetExceeds" value="${true}" />
</c:if>

<spring:theme code="cart.proceedBtn" text="Proceed to checkout" var="sProceedCheckout"/>
<spring:message code="checkout.button.checkout" text="Continue Checkout" var="sCheckout"/>

<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
    <spring:message code="checkout.button.eprocurement" text="Go to ERP" var="sProceedCheckout"/>
</sec:authorize>


<c:choose>
    <c:when test="${not empty href && showNextButton == true}">
        <a href="${href}" title="Proceed to checkout" class="mat-button mat-button--action-green btn-checkout fb-order-purchase" data-aainteraction="proceed to checkout">
            ${sProceedCheckout}
        </a>
    </c:when>
    <c:otherwise>
        <a href="" title="Proceed to checkout" class="mat-button mat-button--action-blue disabled" data-aainteraction="proceed to checkout">
            ${sProceedCheckout}
        </a>
    </c:otherwise>
</c:choose>

<c:if test="${showDownButton == true && (!yearlyBudgetExceeds && !orderBudgetExceeds)}">
    <a href="${href}" title="Proceed to checkout" class="mat-button mat-button--action-green btn-checkout-final fb-order-purchase" data-aainteraction="proceed to checkout">
        <c:choose>
            <c:when test="${reviewPage eq true and cartData.paymentMode.hop}">
                <%-- Case of CC, PP, etc --%>
                <spring:message code="checkout.button.checkout" />
            </c:when>
            <c:otherwise>
                <%-- Case of invoice --%>
                <spring:message code="checkout.summary.placeOrder" />
            </c:otherwise>
        </c:choose>
    </a>
</c:if>