<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>

<spring:url value="/checkout/summary/placeOrder" var="placeOrderUrl" />
<spring:theme code="replenishmentScheduleForm.activateDaily"  var="Daily"/>
<spring:theme code="replenishmentScheduleForm.activateWeekly"  var="Weekly"/>
<spring:theme code="replenishmentScheduleForm.activateMonthly"  var="Monthly"/>
<spring:theme code="text.store.dateformat.datepicker.selection" text="mm/dd/yy" var="dateForForDatePicker"/>
<script type="text/javascript">
	/*         */
	$(document).ready(function() {
		$("#replenishmentStartDate").datepicker({dateFormat: '${dateForForDatePicker}'});

		bindToScheduleReplenishmentButtonClick();
		bindToCancelReplenishmentClick();
		bindToPlaceReplenishmentButtonClick();
		if ('${placeOrderForm.replenishmentOrder}' == "true"){
			scheduleReplenishmentDiv();
		}else{
			$('#replenishment-schedule-div').hide();
		}
	});
	
	function bindToScheduleReplenishmentButtonClick(){
		$('#scheduleReplenishmentButton').click(function(){
			scheduleReplenishmentDiv();
			return false;
		});
	}
		
	function scheduleReplenishmentDiv(){
		$("input:radio[name='replenishmentFrequency'][value="+'${placeOrderForm.replenishmentRecurrence}'+"]").attr('checked',true);
		$("#nDays option[value="+'${placeOrderForm.nDays}'+"]").attr('selected', 'selected');
		$("#daysoFWeek option[value="+'${placeOrderForm.nthDayOfMonth}'+"]").attr('selected', 'selected');
		$("#requestQuoteButton").attr('disabled', true);
		
		$("#scheduleReplenishmentButton").addClass('pressed');
		//$('#scheduleReplenishmentButton').removeClass("possitive");
		$('#replenishment-schedule-div').show();
		$(".place-order").attr('disabled', true);
		$("#placeReplenishmentOrderButton").removeAttr('disabled');
		return false;
	}
	
	function updateScheduleReplenishmentButton(){
		var paymentType = $("#checkout_summary_paymentType_div").hasClass("complete");
		var deliveryAddress = $("#checkout_summary_deliveryaddress_div").hasClass("complete");
		var deliveryMode = $("#checkout_summary_deliverymode_div").hasClass("complete");
		var costCenter = $('#checkout_summary_costcenter_div').hasClass("complete");
		var paymentDetails = $("#checkout_summary_payment_div").hasClass("complete")

		if (paymentType && deliveryAddress && deliveryMode && (costCenter || paymentDetails)){
			$("#scheduleReplenishmentButton").removeAttr('disabled');
		}else{
			$("#scheduleReplenishmentButton").attr('disabled', true);
		}
		
		if($("#scheduleReplenishmentButton").hasClass("pressed")) {
			$(".place-order").attr('disabled', true);
			$("#requestQuoteButton").attr('disabled', true);
			$("#placeReplenishmentOrderButton").removeAttr('disabled');
		}
	}
	
	
	function bindToPlaceReplenishmentButtonClick()
	{
		$('#placeReplenishmentOrderButton').click(function()
		{
			$("#replenishmentOrder").attr('value',true);
			placeOrderWithSecurityCode();
			return false;
		});
	}
	
	function bindToCancelReplenishmentClick(){
		$('#cancel-place-replenishmentOrder').click(function(){
			cancelReplenishmentEvent();
		});
	}

	function cancelReplenishmentEvent() {
			$("#replenishmentOrder").val(false);
			$("#scheduleReplenishmentButton").removeClass('pressed');
			$('#replenishment-schedule-div').hide();
			updatePlaceOrderButton();
			updateRequestQuoteButton();
			updateScheduleReplenishmentButton();
	}

	/*   */
</script>

<script type="text/javascript">
	$(document).ready(function() {
		$('.replenishmentfrequencyD').click(function() {
			$('.scheduleform').each(function() {
				$(this).removeClass('scheduleformfilled');
			});
			$('.scheduleformD').addClass('scheduleformfilled');
			
		});
		$('.replenishmentfrequencyW').click(function() {
			$('.scheduleform').each(function() {
				$(this).removeClass('scheduleformfilled');
			});
			$('.scheduleformW').addClass('scheduleformfilled');
			
		});
		$('.replenishmentfrequencyM').click(function() {
			$('.scheduleform').each(function() {
				$(this).removeClass('scheduleformfilled');
			});
			$('.scheduleformM').addClass('scheduleformfilled');
			
		});


	});

</script>
 <div class="span-20 last place-order-top" id="replenishment-schedule-div">
	<div class="item_container_holder">
		<div class="title_holder">
			<div class="title">
				<div class="title-top">
					<span></span>
				</div>
			</div>
			<h2>
				<spring:theme code="checkout.summary.replenishmentScheduleForm.title" />
			</h2>
		</div>
	
	
		<div class="item_container">
			<div class="scheduleform_left">
				<div class="replenishmentFrequency_left"><formUtil:formInputBox idKey="replenishmentStartDate" labelKey="replenishmentScheduleForm.startDate" placeHolderKey="replenishmentScheduleForm.startDate.placeholder" path="replenishmentStartDate" inputCSS="date" mandatory="true" /></div>
			</div>

			<div class="scheduleform scheduleformD">
				<div class="replenishmentFrequency"><form:radiobutton path="replenishmentRecurrence" name="replenishmentFrequency" label="${Daily}" value="DAILY" class="replenishmentfrequencyD" /></div>

				<label class="label_small"><spring:theme code="replenishmentScheduleForm.daily.days" /></label>
				<form:select id="nDays" path="nDays" cssClass="card_date">
				<form:options items="${nDays}" />
				</form:select>
				<label><spring:theme code="replenishmentScheduleForm.days"/></label>
			</div>

			<div class="scheduleform scheduleformW">
				<div class="replenishmentFrequency"><form:radiobutton path="replenishmentRecurrence" name="replenishmentFrequency" label="${Weekly}" value="WEEKLY" class="replenishmentfrequencyW" /></div>

				<div class="div_nWeeks1">
					<label class="label"><spring:theme code="replenishmentScheduleForm.weekly.daysOfWeek" /></label>
					<form:select id="daysOfWeek" path="nDaysOfWeek" cssClass="card_date" multiple="true">
						<form:options items="${daysOfWeek}" itemLabel="name" itemValue="code" />
					</form:select>
				</div>

				<div class="div_nWeeks2">
					<label class="label_weeks"><spring:theme code="replenishmentScheduleForm.weekly.weeks" /></label>
					<form:select id="nWeeks" path="nWeeks" cssClass="card_date">
						<form:options items="${nthWeek}" />
					</form:select>
					<label><spring:theme code="replenishmentScheduleForm.weeks"/></label>
				</div>

			</div>

			<div class="scheduleform scheduleformM">
				<div class="replenishmentFrequency"><form:radiobutton path="replenishmentRecurrence" name="replenishmentFrequency" label="${Monthly}" value="MONTHLY" class="replenishmentfrequencyM" /></div>

				<label class="label_small"><spring:theme code="replenishmentScheduleForm.monthly.day" /></label>
				<form:select id="nthDayOfMonth" path="nthDayOfMonth" cssClass="card_date">
				<form:options items="${nthDayOfMonth}" />
				</form:select>
				<label><spring:theme code="replenishmentScheduleForm.month"/></label>
			</div>

		</div>
	</div>
	<div class="item_container_cancel_placeorder">
		<form:input type="hidden" id="replenishmentOrder" path="replenishmentOrder" />
		<button type="submit" class="positive right pad_right place-order" id="placeReplenishmentOrderButton">
				<spring:theme code="checkout.summary.placeOrder"/>
		</button>
		<dl class="terms right">
			<dt class="left">
				<a href="javascript:void();" class="edit_complete change_address_button" id="cancel-place-replenishmentOrder">
				<spring:theme code="checkout.summary.negotiateQuote.cancel"/></a>
			</dt>
		</dl>
	</div>
</div>
