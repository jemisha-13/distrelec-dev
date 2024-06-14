<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code="cart.pricecalcbox.title" text="Your Cart" var="sTitle"/>
<spring:message code="cart.pricecalcbox.subtotal" text="Articles" var="sSubtotal"/>
<spring:message code="cart.pricecalcbox.tax" text="Tax" var="sTax"/>
<spring:message code="cart.pricecalcbox.delivery.cost" text="Delivery Cost" var="sDeliveryCost" />
<spring:message code="cart.pricecalcbox.total.payable" text="Total Payable" var="sTotalPayable"/>
<spring:message code="checkout.button.checkout.openOrderCloseMyAccount" text="Close Order now" var="sCheckoutOpenOrderClose"/>
<spring:message code="text.store.dateformat" var="datePattern" />
<jsp:useBean id="dateTodayOCI" class="java.util.Date"/>
<c:set var="dateTodayOCI">
	<fmt:formatDate value="${dateTodayOCI}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" />
</c:set>

<c:set var="openOrderCloseLink" value="/checkout/openorder/changeOpenOrderInERP?code=${orderCode}&closeDate=${dateTodayOCI}" />
<c:set var="isEditableByAllOption" value="${cartData != null ? cartData.openOrderEditableForAllContacts : orderData.openOrderEditableForAllContacts}" />
<c:set var="cartData" value="${cartData != null ? cartData : orderData}" />
<c:set var="orderCode" value="${cartData != null ? cartData.code : orderData.code}" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<spring:message code="checkout.button.eprocurement" text="" var="sCheckout"/>
</sec:authorize>
<c:if test="${showTitle}">
	<div class="calc-box calc-box-header"> <h3 class="title"> <i class="icon-cart"></i> ${sTitle} </h3> </div>
</c:if>
<div class="calc-box calc-box-subtotal">
	<div class="calc-row calc-row-subtotal">
		<div class="calc-cell calc-cell-subtotal nth-1">${sSubtotal}</div>
		<div class="calc-cell calc-cell-subtotal nth-2"><span class="currency"><format:price format="currency" priceData="${cartData.subTotal}" fallBackCurrency="${cartData.totalPrice.currencyIso}" /></span></div>
		<div class="calc-cell calc-cell-subtotal nth-3 calc-subtotal"><format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.subTotal}" /></div>
	</div>
</div>
<div class="calc-box calc-box-details">
	<c:if test="${not empty cartData.deliveryMode}">
		<div class="calc-row calc-row-details">
			<div class="calc-cell calc-cell-details nth-1">
				${sDeliveryCost} (<spring:message code="${cartData.deliveryMode.translationKey}" text="${cartData.deliveryMode.name}" />)
			</div>
			<div class="calc-cell calc-cell-details nth-2"><span class="currency"><format:price format="currency" priceData="${cartData.deliveryCost}" fallBackCurrency="${cartData.totalPrice.currencyIso}" /></span></div>
			<div class="calc-cell calc-cell-details nth-3 calc-delivery"><format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.deliveryCost}" /></div>
		</div>
	</c:if>
	<c:if test="${not empty cartData.paymentMode and cartData.paymentCost.value gt 0}">
		<div class="calc-row calc-row-details">
			<div class="calc-cell calc-cell-details nth-1">
				${cartData.paymentMode.name}
				<c:if test="${empty cartData.paymentMode.paymentCost.currencyIso and cartData.paymentMode.paymentCost.value gt 0}">
					(<format:price format="default" priceData="${cartData.paymentMode.paymentCost}" />)
				</c:if>
			</div>
			<div class="calc-cell calc-cell-details nth-2"><span class="currency"><format:price format="currency" priceData="${cartData.paymentCost}" fallBackCurrency="${cartData.totalPrice.currencyIso}" /></span></div>
			<div class="calc-cell calc-cell-details nth-3 calc-payment"><format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.paymentCost}" /></div>
		</div>
	</c:if>
	<div class="calc-row">
		<div class="calc-cell calc-cell-details nth-1">${sTax}</div>
		<div class="calc-cell calc-cell-details nth-2"><span class="currency"><format:price format="currency" priceData="${cartData.totalTax}" fallBackCurrency="${cartData.totalPrice.currencyIso}" /></span></div>
		<div class="calc-cell calc-cell-details nth-3 calc-tax"><format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.totalTax}" /></div>
	</div>
</div>
<div class="calc-box calc-box-total">
	<div class="calc-row calc-row-total">
		<div class="calc-cell calc-cell-total nth-1">${sTotalPayable}</div>
		<div class="calc-cell calc-cell-total nth-2"><span class="currency"><format:price format="currency" priceData="${cartData.totalPrice}" fallBackCurrency="${cartData.totalPrice.currencyIso}" /></span></div>
		<div class="calc-cell calc-cell-total nth-3 calc-total"><format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.totalPrice}" /></div>
	</div>
</div>


<c:if test="${isOrderEditable}">
		<a href="#" class="btn btn-secondary btn-checkout btn-open-order-close" 
			data-close-date-today="${dateTodayOCI}"
			data-lightbox-title="<spring:message code="cart.pricecalcbox.close.oo.title" />"  
		    data-lightbox-message="<spring:message code="cart.pricecalcbox.close.oo.text" />"   
		    data-lightbox-btn-deny="<spring:message code="cart.pricecalcbox.close.oo.no" />"  
		    data-lightbox-show-confirm-button="true"
		    data-lightbox-btn-conf="<spring:message code="cart.pricecalcbox.close.oo.yes" />"  
		    data-order-id="${orderCode}"	
		    data-source = "myaccount"
		    data-action-url="/checkout/review/continueCheckout">
			<i class="icon-cart"></i> ${sCheckoutOpenOrderClose} 	<i class="icon-arrow"></i>
	</a>
</c:if>
