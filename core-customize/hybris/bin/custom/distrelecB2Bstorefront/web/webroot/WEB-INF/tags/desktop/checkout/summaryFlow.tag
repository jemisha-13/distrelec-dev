<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:url value="/checkout-not/summary/getCheckoutCart.json" var="getCheckoutCartUrl" />

<script type="text/javascript">
/*<![CDATA[*/

	$(document).ready(function()
	{
		getCheckoutCartDataAndRefreshPage();
	});

	/* Extend jquery with a postJSON method */
	jQuery.extend({
	   postJSON: function( url, data, callback) {
		  return jQuery.post(url, data, callback, "json");
	   }
	});

	$.blockUI.defaults.overlayCSS = {};
	$.blockUI.defaults.css = {};
	
	function refreshCartTotals(checkoutCartData)
	{
		$('#cart_totals_div').html($('#cartTotalsTemplate').tmpl(checkoutCartData));
	}
	
	function refreshCartItems(checkoutCartData)
	{
		$('#cart_items_tbody').html($('#cartItemsTemplate').tmpl(checkoutCartData));
	}
	
	function refreshPage(checkoutCartData)
	{
		//update delivery address, delivery method and payment sections, and cart totals section
		refreshDeliveryAddressSection(checkoutCartData);
		refreshDeliveryMethodSection(checkoutCartData);
		refreshPaymentDetailsSection(checkoutCartData);
		refreshCostCenterSection(checkoutCartData);
		refreshPaymentTypeSection(checkoutCartData);
		refreshCartTotals(checkoutCartData);
		updatePlaceOrderButton();
		updateRequestQuoteButton();
		updateScheduleReplenishmentButton();
	}

	function getCheckoutCartDataAndRefreshPage()
	{
		$.getJSON('${getCheckoutCartUrl}', function(checkoutCartData) {refreshPage(checkoutCartData);});
	}

/*]]>*/
</script>

<div class="checkout_summary_flow">
	<checkout:summaryFlowPaymentType />
	<checkout:summaryFlowCostCenter costCenter="${costCenter}" />
	<checkout:summaryFlowPayment />

	<checkout:summaryFlowDeliveryAddress deliveryAddress="${deliveryAddress}" costCenter="${costCenter}"/>
	<checkout:summaryFlowDeliveryMode deliveryMode="${deliveryMode}" />
</div>
