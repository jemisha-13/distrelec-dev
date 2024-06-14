<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<spring:url value="/checkout-not/summary/placeOrder" var="placeOrderUrl" />
<spring:url value="/checkout-not/summary/negotiateQuote" var="negotiateQuoteUrl" />
<template:page pageTitle="${pageTitle}">

<script type="text/javascript">
/*<![CDATA[*/
	function placeOrderWithSecurityCode()
	{
		var securityCode = $("#SecurityCode").val();
		$(".securityCodeClass").val(securityCode);
		document.getElementById("placeOrderForm1").submit();
	}

	$(document).ready(function()
	{

		$('#negotiate-quote-div').hide();
		
		$("#Terms1").click(function() {
			var terms1enable = $('#Terms1').attr("checked");
			if(terms1enable == undefined || terms1enable == 'false'){
				$('#Terms2').attr("checked",false);
			} else {
				$('#Terms2').attr("checked",true);
			}
		});
		
		$("#Terms2").click(function() {
			var terms2enable = $('#Terms2').attr("checked");
			if(terms2enable == undefined || terms2enable == 'false')
			{
				$('#Terms1').attr("checked",false);
			} else {
				$('#Terms1').attr("checked",true);
			}
		});
	});
	

	$(document).ready(function() {
		updatePlaceOrderButton();
	})

	function updatePlaceOrderButton()
	{
		var paymentType = $("#checkout_summary_paymentType_div").hasClass("complete");
		var deliveryAddress = $("#checkout_summary_deliveryaddress_div").hasClass("complete");
		var deliveryMode = $("#checkout_summary_deliverymode_div").hasClass("complete");
		var costCenter = $('#checkout_summary_costcenter_div').hasClass("complete");
		var paymentDetails = $("#checkout_summary_payment_div").hasClass("complete")

		if (paymentType && deliveryAddress && deliveryMode && (costCenter || paymentDetails))
		{
			$(".place-order").removeAttr('disabled');
		}
		else
		{
			$(".place-order").attr('disabled', true);
		}
	}

/*]]>*/
</script>
	<div id="breadcrumb" class="breadcrumb"></div>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>

	<div class="span-4 side-content-slot cms_banner_slot">

	</div>

	<div class="span-24 last">

		<div class="span-24 last">
			<checkout:summaryFlow />
		</div>

		<div class="span-24 last place-order-top">
		
		<div class="span-4 banner_left">
			<cms:slot var="feature" contentSlot="${slots.SideContent}">
				<cms:component component="${feature}"/>
			</cms:slot>
		</div>
		
			<div class="span-20 last placeorder_right">
				<form:form action="${placeOrderUrl}" id="placeOrderForm1" modelAttribute="placeOrderForm">
					<form:input type="hidden" name="securityCode" class="securityCodeClass" path="securityCode"/>
					<button type="submit" class="positive right pad_right place-order" onclick="placeOrderWithSecurityCode();return false;">
						<spring:theme code="checkout.summary.placeOrder"/>
					</button>
					<button type="submit" class="positive right pad_right request-quote" id="requestQuoteButton">
						<spring:theme code="checkout.summary.negotiateQuote"/>
					</button>
					<button type="submit" class="positive right pad_right schedule-replenishment" id="scheduleReplenishmentButton">
						<spring:theme code="checkout.summary.scheduleReplenishment"/>
					</button>
					<dl class="terms left">
						<dt class="left">
							<form:checkbox id="Terms1" name="Terms1" path="termsCheck" />
							<label for="Terms1"><spring:theme code="checkout.summary.placeOrder.readTermsAndConditions"/></label>
						</dt>
						<dd></dd>
					</dl>
					 <cart:negotiateQuote/>
					 <cart:replenishmentScheduleForm/>
				</form:form>
			
	
			<div class="span-20 last">
				<checkout:summaryCartItems/>
			</div>
	
			<div class="span-12">
				<cart:cartPromotions cartData="${cartData}"/>
			</div>
	
			<div class="span-8 right last place-order-cart-total">
				<checkout:summaryCartTotals/>
			</div>
	
			<div class="span-20 place-order-bottom">
				<form:form action="${placeOrderUrl}" id="placeOrderForm2" modelAttribute="placeOrderForm">
					<form:input type="hidden" name="securityCode" class="securityCodeClass" path="securityCode"/>
					<button type="submit" class="positive right pad_right place-order"  onclick="placeOrderWithSecurityCode();return false;">
						<spring:theme code="checkout.summary.placeOrder"/>
					</button>
					<dl class="terms right">
						<dt class="left">
							<form:checkbox id="Terms2" name="Terms2" path="termsCheck" />
							<label id="terms2" for="Terms2"><spring:theme code="checkout.summary.placeOrder.readTermsAndConditions"/></label>
						</dt>
						<dd></dd>
					</dl>
				</form:form>
			</div>
		</div>
	</div>
</template:page>
