<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<spring:url value="/checkout-not/summary/createUpdatePaymentDetails.json" var="createUpdatePaymentUrl" />

<script type="text/javascript">
/*<![CDATA[*/
	$(document).ready(function() {
		updateBillingAddressForm();

		$("#differentAddress").click(function() {
			updateBillingAddressForm();
		})
		
		bindCycleFocusEvent();
	})
	
	function bindCycleFocusEvent()
	{
		$('#lastInTheForm').blur(function() {
			$('#paymentDetailsForm [tabindex$="10"]').focus();
		})
	}
		
	function updateBillingAddressForm()
	{
		var editMode = ("true" == $('#newBillingAddressFields').attr('edit-mode'));
		var newAddress = $('#differentAddress').attr("checked");
		if(editMode || newAddress)
		{
			$("#newBillingAddressFields :input").removeAttr('disabled');
		}
		else
		{
			$("#newBillingAddressFields :input").attr('disabled', 'disabled');
		}
	}
/*]]>*/
</script>

<div class="title_holder">
	<div class="title">
		<div class="title-top">
			<span></span>
		</div>
	</div>
	<h2><spring:theme code="checkout.summary.paymentMethod.addEditPaymentDetails.header"/></h2>
</div>
<div class="item_container">
	<c:if test="${not empty paymentInfoData }">
		<span class="saved_card">
			<button class="form left use_saved_card_button"><spring:theme code="checkout.summary.paymentMethod.addEditPaymentDetails.useSavedCard"/></button>
			<p><spring:theme code="checkout.summary.paymentMethod.addEditPaymentDetails.useSavedCard.description"/></p>
		</span>
	</c:if>
	<form:form method="post" modelAttribute="paymentDetailsForm" action="${createUpdatePaymentUrl}" class="create_update_payment_form">
		<common:globalMessages/>
		<div class="payment_details_left_col">
			<h1><spring:theme code="checkout.summary.paymentMethod.addEditPaymentDetails.paymentCard"/></h1>
			<p><spring:theme code="checkout.summary.paymentMethod.addEditPaymentDetails.enterYourCardDetails"/></p>
			<p><spring:theme code="form.required"/></p>
			<dl>

				<form:hidden path="paymentId" class="create_update_payment_id" status="${not empty createUpdateStatus ? createUpdateStatus : ''}"/>
				<formUtil:formSelectBox idKey="payment.cardType" labelKey="payment.cardType" path="cardTypeCode" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.cardType.pleaseSelect" items="${cardTypes}" tabindex="1"/>
				<formUtil:formInputBox idKey="payment.nameOnCard" labelKey="payment.nameOnCard" path="nameOnCard" placeHolderKey="payment.nameOnCard.placeholder" inputCSS="text" mandatory="true" tabindex="2"/>
				<formUtil:formInputBox idKey="payment.cardNumber" labelKey="payment.cardNumber" path="cardNumber" placeHolderKey="payment.cardNumber.placeholder" inputCSS="text" mandatory="true" tabindex="3"/>

				<template:errorSpanField path="startMonth">
					<dt><label for="StartMonth"><spring:theme code="payment.startDate"/></label></dt>
					<dd>
						<form:select id="StartMonth" path="startMonth" cssClass="card_date" tabindex="4">
							<option value="" label="<spring:theme code='payment.month'/>"/>
							<form:options items="${months}" itemValue="code" itemLabel="name"/>
						</form:select>
					
						<form:select id="StartYear" path="startYear" cssClass="card_date" tabindex="5">
							<option value="" label="<spring:theme code='payment.year'/>"/>
							<form:options items="${startYears}" itemValue="code" itemLabel="name"/>
						</form:select>
					</dd>
	
					<dt><label for="ExpiryMonth"><spring:theme code="payment.expiryDate"/></label></dt>
					<dd>
						<template:errorSpanField path="expiryMonth">
							<form:select id="ExpiryMonth" path="expiryMonth" cssClass="card_date" tabindex="6">
								<option value="" label="<spring:theme code='payment.month'/>"/>
								<form:options items="${months}" itemValue="code" itemLabel="name"/>
							</form:select>
						</template:errorSpanField>
	
						<template:errorSpanField path="expiryYear">
							<form:select id="ExpiryYear" path="expiryYear" cssClass="card_date" tabindex="7">
								<option value="" label="<spring:theme code='payment.year'/>"/>
								<form:options items="${expiryYears}" itemValue="code" itemLabel="name"/>
							</form:select>
						</template:errorSpanField>
					</dd>
				</template:errorSpanField>
				
				<formUtil:formInputBox idKey="payment.issueNumber" labelKey="payment.issueNumber" path="issueNumber" placeHolderKey="payment.issueNumber.placeholder" inputCSS="text" mandatory="false" tabindex="8"/>
			</dl>
		</div>

		<div class="payment_details_right_col">

			<h1><spring:theme code="checkout.summary.paymentMethod.addEditPaymentDetails.billingAddress"/></h1>
			<c:if test="${!edit}">
				<p><spring:theme code="checkout.summary.paymentMethod.addEditPaymentDetails.billingAddressDiffersFromDeliveryAddress"/></p>
				<dl>
					<dt class="left">
						<form:checkbox id="differentAddress" path="newBillingAddress" tabindex="9"/>
						<label for="differentAddress"><spring:theme code="checkout.summary.paymentMethod.addEditPaymentDetails.enterDifferentBillingAddress"/></label>
					</dt>
					<dd></dd>
				</dl>
			</c:if>

			<p><spring:theme code="form.required"/></p>
			<dl id="newBillingAddressFields" edit-mode="${edit}">
				<form:hidden path="billingAddress.addressId" class="create_update_address_id" status="${not empty createUpdateStatus ? createUpdateStatus : ''}"/>
				<formUtil:formSelectBox idKey="address.title" labelKey="address.title" path="billingAddress.titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" tabindex="10"/>
				<formUtil:formInputBox idKey="address.firstName" labelKey="address.firstName" path="billingAddress.firstName" placeHolderKey="address.firstName.placeholder" inputCSS="text" mandatory="true" tabindex="11"/>
				<formUtil:formInputBox idKey="address.surname" labelKey="address.surname" path="billingAddress.lastName" placeHolderKey="address.surname.placeholder" inputCSS="text" mandatory="true" tabindex="12"/>
				<formUtil:formInputBox idKey="address.line1" labelKey="address.line1" path="billingAddress.line1" placeHolderKey="address.line1.placeholder" inputCSS="text" mandatory="true" tabindex="13"/>
				<formUtil:formInputBox idKey="address.line2" labelKey="address.line2" path="billingAddress.line2" placeHolderKey="address.line2.placeholder" inputCSS="text" mandatory="false" tabindex="14"/>
				<formUtil:formInputBox idKey="address.townCity" labelKey="address.townCity" path="billingAddress.townCity" placeHolderKey="address.townCity.placeholder" inputCSS="text" mandatory="true" tabindex="15"/>
				<formUtil:formInputBox idKey="address.postcode" labelKey="address.postcode" path="billingAddress.postcode" placeHolderKey="address.postcode.placeholder" inputCSS="text" mandatory="true" tabindex="16"/>
				<formUtil:formSelectBox idKey="address.country" labelKey="address.country" path="billingAddress.countryIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectCountry" items="${billingCountries}" itemValue="isocode" tabindex="17"/>
				<form:hidden path="billingAddress.shippingAddress"/>
				<form:hidden path="billingAddress.billingAddress"/>
			</dl>
		</div>
		<div class="save_payment_details">
			<dl>
				<dt class="left">
					<form:checkbox id="SaveDetails" path="saveInAccount" tabindex="18"/>
					<label for="SaveDetails"><spring:theme code="checkout.summary.paymentMethod.addEditPaymentDetails.savePaymentDetailsInAccount"/></label>
				</dt>
				<dd></dd>
			</dl>
			<ycommerce:testId code="checkout_useThesePaymentDetails_button">
				<button class="form left use_these_payment_details_button" tabindex="19" id="lastInTheForm">
					<spring:theme code="${edit ? 'checkout.summary.paymentMethod.addEditPaymentDetails.saveAndUseThesePaymentDetails' : 'checkout.summary.paymentMethod.addEditPaymentDetails.useThesePaymentDetails'}"/>
				</button>
			</ycommerce:testId>
		</div>
	</form:form>
</div>
