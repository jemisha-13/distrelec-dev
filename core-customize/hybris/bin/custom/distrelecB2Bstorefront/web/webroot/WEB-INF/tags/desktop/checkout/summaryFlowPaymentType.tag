<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<spring:url value="/_ui/desktop/common/images/spinner.gif" var="spinnerUrl" />
<spring:url value="/checkout-not/summary/setPaymentType.json" var="setPaymentTypeURL" />
<spring:url value="/checkout-not/summary/getCheckoutCart.json" var="getCheckoutCartUrl" />
<spring:url value="/checkout-not/summary/setPurchaseOrderNumber.json" var="setPurchaseOrderNumberURL" />

<script type="text/javascript">
/*<![CDATA[*/
	$(document).ready(function() {
		bindToPaymentTypeSelection();
		bindToPurchaseOrderNumberInput();
	});

	function bindToPaymentTypeSelection()
	{	
		$("input:radio[name='PaymentType']").change(function()
		{
			$('#checkout_summary_paymentType_div').removeClass('complete');
			var paymentTypeSelected = $("input:radio[name='PaymentType']:checked").val();
			$.postJSON('${setPaymentTypeURL}', {paymentType: paymentTypeSelected}, summaryFlowAfterPaymentTypeSetSuccess);				
		});
	}
	
	function bindToPurchaseOrderNumberInput()
	{
		$("#PurchaseOrderNumber").focusout(function()
		{
			var purchaseOrderNumber = $("input[name='PurchaseOrderNumber']").val();
			$.postJSON('${setPurchaseOrderNumberURL}', {purchaseOrderNumber: purchaseOrderNumber}, summaryFlowAfterPurchaseOrderNumberSuccess);				
		});				
	}
	
	var  summaryFlowAfterPaymentTypeSetSuccess = function(checkoutCartData) {
		markPaymentTypeSectionAsCompleted();
		hideAndShowRequiredDiv( checkoutCartData.paymentType.code);
		refreshCostCenterSection(checkoutCartData);
		refreshDeliveryAddressSection(checkoutCartData);
		refreshDeliveryMethodSection(checkoutCartData);
		refreshCartItems(checkoutCartData);
		refreshCartTotals(checkoutCartData);
		cancleQuoteNegotiationEvent();
		cancelReplenishmentEvent();
	};
	
	function refreshPaymentTypeSection(checkoutCartData)
	{
			var paymentTypeFromCart =  checkoutCartData.paymentType.code;
		   	$("input:radio[name='PaymentType'][value="+paymentTypeFromCart+"]").attr('checked',true);		
		   	$("input[name='PurchaseOrderNumber']").attr('value',checkoutCartData.purchaseOrderNumber);
		    markPaymentTypeSectionAsCompleted();
			hideAndShowRequiredDiv(checkoutCartData.paymentType.code);
	}
	
	var summaryFlowAfterPurchaseOrderNumberSuccess = function(checkoutCartData) {
		
	};
	

	function markPaymentTypeSectionAsCompleted()
	{
		if ($("input:radio[name='PaymentType']:checked").val() != ''){
			$('#checkout_summary_paymentType_div').addClass('complete');
		}else{
			$('#checkout_summary_paymentType_div').removeClass('complete');
		}
	}
	
	
	function hideAndShowRequiredDiv(paymentType)
	{
		if(paymentType == 'CARD'){
			$('#checkout_summary_costcenter_div').hide();
			$('#checkout_summary_payment_div').show();
		}else{
			$('#checkout_summary_payment_div').hide();
			$('#checkout_summary_costcenter_div').show();
		}
	}
/*]]>*/
</script>	

<div class="checkout_summary_flow_a" id="checkout_summary_paymentType_div">
	<div class="item_container_holder">
		<ycommerce:testId code="paymentType_text">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h2><spring:theme code="checkout.summary.paymentType.header" htmlEscape="false"/><span></span></h2>
			</div>

			<div class="item_container">
				<div class="radiobuttons_paymentselection">
					<form:radiobuttons path="paymentTypes" id="PaymentTypeSelection" cssClass="left" name="PaymentType" items="${paymentTypes}" itemLabel="displayName" itemValue="code" />
				</div>
				
				<div class="pon">
					<label><spring:theme code="checkout.summary.purchaseOrderNumber"/></label>
					<br>
					<input type="text" id="PurchaseOrderNumber" style="left" name="PurchaseOrderNumber" maxlength="255" />
				</div>
			</div>
			
		</ycommerce:testId>
	</div>
	
</div>

