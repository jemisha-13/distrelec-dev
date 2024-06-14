<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="costCenter" required="true"
			  type="de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<spring:url value="/checkout-not/summary/getCostCenters.json" var="getCostCenters"/>
<spring:url value="/checkout-not/summary/getCheckoutCart.json" var="getCheckoutCartUrl" />
<spring:url value="/checkout-not/summary/setCostCenter.json" var="setCostCenterUrl" />
<spring:url value="/checkout-not/summary/updateCostCenter.json" var="updateCostCenterUrl" />

<script type="text/javascript">

	$(document).ready(function()
	{
		bindToCostCenterSelection();
	});

	function updateCostCenterForCart(costCenterId)
	{
		$.postJSON('${updateCostCenterUrl}', {costCenterId: costCenterId});
		return false;
	}


	function bindToCostCenterSelection()
	{
		$('#CostCenter').change(function()
		{
			var costCenterId = $("#CostCenter option:selected")[0].value;
			$.postJSON('${setCostCenterUrl}', {costCenterId: costCenterId}, handleSelectSavedCostCenterSuccess);
			return false;
		});
	}

	function markCostCenterSectionAsCompleted()
	{
		if ($("#CostCenter option:selected")[0].value != '')
		{
			$('#checkout_summary_costcenter_div').addClass('complete');
		}
		else
		{
			$('#checkout_summary_costcenter_div').removeClass('complete');
		}
	}


	var  handleSelectSavedCostCenterSuccess = function(checkoutCartData) {
			markCostCenterSectionAsCompleted();
			refreshDeliveryAddressSection(checkoutCartData);
			refreshDeliveryMethodSection(checkoutCartData);
			refreshCartTotals(checkoutCartData);
			cancleQuoteNegotiationEvent();
			cancelReplenishmentEvent();

	};

	function refreshCostCenterSection(checkoutCartData)
	{
		if (checkoutCartData.costCenter != null && checkoutCartData.costCenter.code != '')
		{
			$("#CostCenter").val(checkoutCartData.costCenter.code);
			markCostCenterSectionAsCompleted();
			updateCostCenterForCart(checkoutCartData.costCenter.code);
		}
		else
		{
			$("#CostCenter").val('');
			$("#requestQuoteButton").attr('disabled', true);
			$('#checkout_summary_costcenter_div').removeClass('complete');
		}
	}
</script>

<div class="checkout_summary_flow_b" id="checkout_summary_costcenter_div">
	<div class="item_container_holder">
		<div class="title_holder">
			<div class="title">
				<div class="title-top">
					<span></span>
				</div>
			</div>
			<h2><spring:theme code="checkout.summary.costCenter.header" htmlEscape="false"/><span></span></h2>
		</div>

		<div class="item_container">

				<form:select id="CostCenter" path="costCenters" cssClass="card_date">
					<option value="" label="<spring:theme code='costCenter.title.pleaseSelect'/>"/>
					<form:options items="${costCenters}" itemValue="code" itemLabel="name"/>
				</form:select>

		</div>
	</div>
</div>
