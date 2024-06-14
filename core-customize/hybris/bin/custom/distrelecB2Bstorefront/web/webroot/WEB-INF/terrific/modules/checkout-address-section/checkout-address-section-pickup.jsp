<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<spring:message code="register.phoneNumber.placeholder" var="sNumberPlaceholder" />

<c:if test="${not empty pickupWarehouses}">
	<mod:address-list
		skin="pickup-store"
		addressList="${pickupWarehouses}"
		customerType="${cartData.b2bCustomerData.customerType eq 'B2B_KEY_ACCOUNT' or cartData.b2bCustomerData.customerType eq 'B2B' ? 'b2b' : 'b2c'}"
		addressType="pickup"
		selectedAddressId="${cartData.pickupLocation.code}"
		addressActionMode="${fn:length(pickupWarehouses) > 1 ? 'radio' : ''}"
		addressEditMode="false"
	/>
	<c:if test="${currentCountry.isocode eq 'SE' or currentCountry.isocode eq 'EE' or currentCountry.isocode eq 'LV'}">
		<div class="mobile-number">
			<div class="mobile-number__info">
				<spring:message code="addressform.mobileNumber.info" text="Please enter your mobile number. You will be notified once the products are available for pickup." />
			</div>
			<input type="text" value="${mobileNumber}" title="mobileNumber" class="mobile-number__input" placeholder="${sNumberPlaceholder}" data-placeholder="${placeholder}" />
			<a href="#" class="mat-button mat-button--action-blue mobile-number-save">
				<spring:message code="addressform.mobileNumber.save" text="Save" />
			</a>
			<div class="mobile-number__info mobile-number__info--ok hidden">
				<spring:message code="addressform.mobileNumber.save.ok" text="Mobile phone number updated" />
			</div>
			<div class="mobile-number__info mobile-number__info--error hidden">
				<spring:message code="addressform.mobileNumber.save.error" text="Error saving mobile phone number" />
			</div>
			<div class="mobile-number__info mobile-number__info--warning hidden">
				<spring:message code="address.contactPhone.invalid" text="Please enter a valid phone number" />
			</div>
		</div>
	</c:if>
</c:if>