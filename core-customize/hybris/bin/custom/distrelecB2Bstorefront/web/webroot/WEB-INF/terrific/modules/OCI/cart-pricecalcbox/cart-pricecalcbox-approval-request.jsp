<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code="cart.pricecalcbox.subtotal" text="Articles" var="sSubtotal"/>
<spring:message code="cart.pricecalcbox.tax" text="Tax" var="sTax"/>
<spring:message code="cart.pricecalcbox.delivery.cost" text="Delivery Cost" var="sDeliveryCost" />
<spring:message code="cart.pricecalcbox.total.payable" text="Total Payable" var="sTotalPayable"/>
<spring:message code="cart.pricecalcbox.termsAndCond" text="Terms and Conditions" var="sTermsAndCond"/>
<spring:message code="cart.pricecalcbox.acceptTerms" text="Yes, I have read and hereby accept the <a href='#'>terms and conditions of Distrelec</a>" var="sAcceptTerms"/>
<spring:message code="cart.pricecalcbox.acceptNewsletter" text="Yes, I want to receive the Distrelec newsletter" var="sAcceptNewsletter"/>
<spring:message code="checkout.button.checkout" text="Continue Checkout" var="sCheckout"/>

<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
	<spring:message code="approvalbar.button.approveAndSubmit" text="Approve and submit" var="sCheckout"/>
</sec:authorize>
<c:set var="cartData" value="${cartData != null ? cartData : orderData}" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<spring:message code="checkout.button.eprocurement" text="" var="sCheckout"/>
</sec:authorize>

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
			<div class="calc-cell calc-cell-details nth-1"> ${sDeliveryCost}&nbsp;(<spring:message code="${cartData.deliveryMode.translationKey}" text="${cartData.deliveryMode.name}" />) </div>
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
<div class="calc-box calc-box-total" id="anchor-calc-box-total"><%-- Anchor Target for Approval Button on B2B --%>
	<div class="calc-row calc-row-total">
		<div class="calc-cell calc-cell-total nth-1">${sTotalPayable}</div>
		<div class="calc-cell calc-cell-total nth-2"><span class="currency"><format:price format="currency" priceData="${cartData.totalPrice}" fallBackCurrency="${cartData.totalPrice.currencyIso}" /></span></div>
		<div class="calc-cell calc-cell-total nth-3 calc-total"><format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.totalPrice}" /></div>
	</div>
</div>
<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
	<form:form action="/my-account/order-approval/approval-decision" method="post" modelAttribute="orderApprovalDecisionForm">
		<div class="bd-terms-box" id="terms-and-conditions">
			<div class="terms-row"> 	<h3>${sTermsAndCond}</h3>
				<div class="check-option"> <formUtil:formCheckbox idKey="termsAndConditions" labelKey="cart.pricecalcbox.acceptTerms" path="termsAndConditions" value="true" inputCSS="checkbox checkbox-big" /> </div>
			</div>
		</div>
		<input type="hidden" name="workFlowActionCode" value="${orderApprovalData.workflowActionModelCode}"/>
		<input type="hidden" name="approverSelectedDecision" value="approve"/>
		<input type="hidden" name="comments" value=""/>
		<button class="btn btn-primary btn-checkout" type="submit">
			<i class="icon-cart"></i> ${sCheckout} <i class="icon-arrow"></i>
		</button>
	</form:form>
</sec:authorize>
