<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<spring:message code="b2bunit.save" var="sSaveChanges" />
<spring:message code="backorder.page.returnCart" var="sReturnCart" text="Cancel and go back to Cart" />
<spring:message code="stock.notification.email.notifyMe" var="sStockNotificationEmailNotifyMe" />

<spring:url value="/checkout/backorderDetails/updateBackOrder" var="backOrderUrl" />
<spring:url value="/cart" var="cartUrl" />

<div id="appSave">
    <div class="back-order-holder__side">
         <div class="mod-back-order-item-save__notify">
             <span class="notify-me-toggle">
                 ${sStockNotificationEmailNotifyMe}
             </span>
         </div>
        <form:form class="continue-checkout" action="${backOrderUrl}" method="POST">
            <button type="submit" class="mat-button mat-button--action-green btn-checkout" data-aainteraction="save changes to order">
                    ${sSaveChanges}
            </button>
            <a href="${cartUrl}" class="return-cart-btn" title="${sReturnCart}">
                    ${sReturnCart}
            </a>
        </form:form>

        <mod:stock-notification/>

        <div class="hidden" id="hiddenResponse"></div>
    </div>
</div>