<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>

<script type="text/javascript">
	/*<![CDATA[*/

	$(document).ready(function()
	{	
		$("#requestQuoteButton").attr('quoteAllowed','${cartData.quoteAllowed}');
		
		bindToCancelQuoteClick();
		updateRequestQuoteButton();
		bindToProceedButtonClick();
		bindToRequestQuoteButtonClick();

		if ("true" == '${placeOrderForm.negotiateQuote}')
		{
			displayNegotiateQuoteDiv();
		}
	});

	function displayNegotiateQuoteDiv()
	{
		$("#requestQuoteButton").addClass('pressed');
		$('#negotiate-quote-div').show();
		$(".place-order").attr('disabled', true);
		$("#scheduleReplenishmentButton").attr('disabled', true);
		return false;
	}

	function bindToCancelQuoteClick()
	{
		$('#cancel-quote-negotiation').click(function()
		{
		  cancleQuoteNegotiationEvent();
		});
	}

	function cancleQuoteNegotiationEvent()
	{
		$("#requestQuoteButton").removeClass('pressed');
		$('#negotiate-quote-div').hide();
		$('#quoteRequestDescription').value = "";
		$("#negotiateQuote").val(false);
		updatePlaceOrderButton();
		updateRequestQuoteButton();
		updateScheduleReplenishmentButton();
	}

	function bindToProceedButtonClick()
	{
		$('#negotiateQuoteButton').click(function()
		{
			$("#negotiateQuote").val(true);
			placeOrderWithSecurityCode();
			return false;
		});
	}

	function bindToRequestQuoteButtonClick()
	{
		$('#requestQuoteButton').click(function()
		{
			$('#requestQuoteButton').addClass("pressed");
			//$('#requestQuoteButton').removeClass("possitive");
			displayNegotiateQuoteDiv();
			return false;
		});
	}

	function updateRequestQuoteButton()
	{
		var paymentType = $("#checkout_summary_paymentType_div").hasClass("complete");
		var deliveryAddress = $("#checkout_summary_deliveryaddress_div").hasClass("complete");
		var deliveryMode = $("#checkout_summary_deliverymode_div").hasClass("complete");
		var costCenter = $('#checkout_summary_costcenter_div').hasClass("complete");
		var paymentDetails = $("#checkout_summary_payment_div").hasClass("complete")
		var selectedPaymentType = $("input:radio[name='PaymentType']:checked").val() != 'CARD';
		var costCenterSelected = $("#CostCenter option:selected")[0].value != '';
		var quoteAllowed = 'true' == $("#requestQuoteButton").attr("quoteAllowed");
		
		if (quoteAllowed && paymentType && deliveryAddress && deliveryMode && (costCenter || paymentDetails) && selectedPaymentType
			&& costCenterSelected)
		{
			$("#requestQuoteButton").removeAttr('disabled');			
		}
		else
		{
			$("#requestQuoteButton").attr('disabled', true);
		}

		if ($("#requestQuoteButton").hasClass("pressed"))
		{
			$(".place-order").attr('disabled', true);
			$("#scheduleReplenishmentButton").attr('disabled', true);
		}
	}

	/*]]>*/
</script>

<div class="span-20 last" id="negotiate-quote-div">
	<div>
		<div class="item_container_holder">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h2><spring:theme code="checkout.summary.negotiateQuote.quoteReason"/></h2>
			</div>
			<div class="item_container">
				<form:textarea cssClass="text" id="quoteRequestDescription" path="quoteRequestDescription" />
			</div>
		</div>
		<div class="item_container_cancel_placeorder">
			<form:input type="hidden" name="negotiateQuote" class="negotiateQuoteClass" path="negotiateQuote"/>
			<button class="positive right pad_right negotiateQuote" id="negotiateQuoteButton">
				<spring:theme code="checkout.summary.negotiateQuote.proceed"/>
			</button>

			<dl class="terms right">
				<dt class="left">
					<a href="javascript:void();" class="edit_complete change_address_button" id="cancel-quote-negotiation"><spring:theme code="checkout.summary.negotiateQuote.cancel"/></a>
				</dt>
			</dl>
		</div>
	</div>
</div>
