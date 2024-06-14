<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="keyAccountUser" value="${cartData.b2bCustomerData.customerType eq 'B2B_KEY_ACCOUNT'}" />
<spring:message code="checkout.billing.selectDifferent" text="Select a different address" var="sSelectDifferent" />

<div class="title">
	<h1><spring:message code="checkout.address.b2b.billing.address" text="B2B Billing Address" /></h1>
</div>

<mod:address
	htmlClasses="billing-multiple-section"
	template="billing-b2b-multiple"
	address="${cartData.billingAddress}"
	customerType="${cartData.b2bCustomerData.customerType}"/>

<c:if test="${fn:length(billingAddressList) > 1}">
	<div class="col-12 box-address__edit box-address__edit--main">
		<ul class="box-address__edit__list">
			<li class="box-address__edit__list__item">
				<input type="radio" name="billTo" id="different" />
				<label for="different">${sSelectDifferent}</label>
			</li>
		</ul>
	</div>
	<div class="different-address-holder hidden">
		<mod:address-list
				skin="billing"
				addressList="${billingAddressList}"
				customerType="b2b"
				addressType="billing-multiple"
				selectedAddressId="${cartData.billingAddress.id}"
				addressActionMode="radio"
				addressEditMode="${keyAccountUser or isExportShop ? 'false' : 'true'}"
		/>
	</div>
</c:if>