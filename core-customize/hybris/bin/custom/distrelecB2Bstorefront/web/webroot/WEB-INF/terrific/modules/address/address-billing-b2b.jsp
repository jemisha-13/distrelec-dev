<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>


<c:url value="/checkout/address/update/b2b/billing" var="changeAddressUrl" />

<c:set var="userType" value="${cartData.b2bCustomerData.customerType}"/>
<div class="box box-address box-address-${address.id} address-billing-b2b">
	<div class="row">
		<c:choose>
			<c:when test="${fn:length(billingAddressList) gt 1}">
				<div class="col-1 box-address__input">
					<input id="address-${address.id}" type="radio" class="radio-big" name="${addressType}-${customerType}" value="${address.id}" ${address.id eq selectedAddressId ? 'checked' : ''} />
					<label for="address-${address.id}">&nbsp;</label>
				</div>
				<div class="col-11 box-address__preview">
					<c:if test="${not empty address.companyName}">
						<p>
							<c:out value="${address.companyName}" />
						</p>
					</c:if>
					<c:if test="${not empty address.companyName2}">
						<p>
							<c:out value="${address.companyName2}" />
						</p>
					</c:if>
					
					<c:if test="${not empty address.line1}">
						<p>
							<c:out value="${address.line1}" />
							<c:out value=" ${address.line2}" />
						</p>
					</c:if>
					<c:if test="${not empty address.postalCode}">
						<p>
							<c:out value="${address.postalCode}" />
							<c:out value=" ${address.town}" />
						</p>
					</c:if>
					<c:if test="${not empty address.country.name}">
						<p>
							<c:out value="${address.country.name}" />
						</p>
					</c:if>
				</div>
				<c:if test="${!isExportShop}">
					<c:if test="${not empty userType && userType ne 'B2B_KEY_ACCOUNT'}">
						<div class="col-12 box-address__edit">
							<a href="#" class="address__edit__link">
								<spring:message code="checkout.address.buttonChange" text="Change" />
							</a>
						</div>
					</c:if>
				</c:if>
			</c:when>
			<c:otherwise>
				<div class="col-12 box-address__preview">
					<c:if test="${not empty address.companyName}">
						<p>
							<c:out value="${address.companyName}" />
						</p>
					</c:if>
					<c:if test="${not empty address.companyName2}">
						<p>
							<c:out value="${address.companyName2}" />
						</p>
					</c:if>
					<c:if test="${not empty address.line1}">
						<p>
							<c:out value="${address.line1}" />
							<c:out value=" ${address.line2}" />
						</p>
					</c:if>
					<c:if test="${not empty address.postalCode}">
						<p>
							<c:out value="${address.postalCode}" />
							<c:out value=" ${address.town}" />
						</p>
					</c:if>
					<c:if test="${not empty address.country.name}">
						<p>
							<c:out value="${address.country.name}" />
						</p>
					</c:if>
				</div>

				<c:if test="${not isExportShop}">
					<c:if test="${showBillingEdit eq true}">
						<div class="col-12 box-address__edit">
							<c:if test="${not empty userType && userType ne 'B2B_KEY_ACCOUNT'}">
								<a href="#" class="address__edit__link">
									<spring:message code="checkout.address.buttonChange" text="Change" />
								</a>
							</c:if>
						</div>
					</c:if>
				</c:if>
			</c:otherwise>
		</c:choose>

		<form:form class="col-12 js-billing-form box-address__form${showBillingEdit ? ' hidden' : '' }" method="post" modelAttribute="B2BBillingAddressForm"  id="B2BBillingAddressForm"  action="${changeAddressUrl}">
			<input type="hidden" id="addressId" name="addressId" value="${address.id}" />
			<input type="hidden" id="billingAddress" name="billingAddress" value="true" />
			<input type="hidden" id="shippingAddress" name="shippingAddress" value="${address.shippingAddress}" />
			
			<div class="box-address__form__field">
				<input type="text" id="companyName" name="companyName" placeholder="${address.companyName}" value="${empty address.companyName ? companyName : address.companyName}" class="disabled" readonly />
				<label for="companyName">&nbsp;</label>
			</div>
			<div class="box-address__form__field">
				<formUtil:formInputBox idKey="companyName2" path="companyName2" placeHolderKey="register.company2" inputCSS="validate-empty" mandatory="false"  value="${address.companyName2}" />
				<label for="companyName2">&nbsp;</label>
			</div>
			<div class="box-address__form__field">
				<formUtil:formInputBox idKey="line1" path="line1" placeHolderKey="register.strName" inputCSS="validate-empty" mandatory="true" value="${address.line1}" maxLength="60" />
				<label for="line1">&nbsp;</label>
			</div>
			<div class="box-address__form__field">
				<formUtil:formInputBox idKey="line2" path="line2" placeHolderKey="register.strNumber" inputCSS="validate-empty" mandatory="true" value="${address.line2}" maxLength="10" />
				<label for="line2">&nbsp;</label>
			</div>
			<div class="box-address__form__field">
				<formUtil:formInputBox idKey="postalCode" path="postalCode" placeHolderKey="register.postalCode" mandatory="true" inputCSS="validate-empty" maxLength="10" value="${address.postalCode}" />
				<label for="postalCode">&nbsp;</label>
			</div>
			<div class="box-address__form__field">
				<formUtil:formInputBox idKey="town" path="town" placeHolderKey="register.town" inputCSS="validate-empty" mandatory="true" value="${address.town}" maxLength="60" />
				<label for="town">&nbsp;</label>
			</div>
			<div class="box-address__form__select <c:if test="${siteUid eq 'distrelec_FR'}">disable-select</c:if>">
				<formUtil:formSelectBox idKey="countryCode" path="countryIso" mandatory="true" skipBlank="false" selectedValue="${address.country.isocode}" selectCSSClass="selectpicker validate-dropdown countrySelectExportShop js-libphonenumber-isocode-select" skipBlankMessageKey="form.select.empty"  items="${countries}"  itemValue="isocode" />
			</div>
			<div class="box-address__form__field js-form-group">
				<formUtil:formInputBox idKey="phoneNumber" path="phoneNumber" placeHolderKey="register.phoneNumber.placeholder" inputCSS="validate-empty js-libphonenumber" mandatory="true" value="${address.phone}"  />
				<label for="phoneNumber">&nbsp;</label>

				<div class="js-form-group-error" hidden>
					<div class="form_field_error js-is-FE mt-0">
						<div class="field-msgs">
							<div class="error">
								<span id="phone.number.error"><spring:message code="form.phone.error.for.country"/></span>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="box-address__form__field js-form-group">
				<formUtil:formInputBox idKey="mobileNumber" path="mobileNumber" placeHolderKey="register.mobileNumber.placeholder" inputCSS="validate-empty js-libphonenumber" mandatory="false" value="${address.cellphone}" />
				<label for="mobileNumber">&nbsp;</label>

				<div class="js-form-group-error" hidden>
					<div class="form_field_error js-is-FE mt-0">
						<div class="field-msgs">
							<div class="error">
								<span id="mobile.number.error"><spring:message code="form.phone.error.for.country"/></span>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="box-address__form__field">
				<formUtil:formInputBox idKey="faxNumber" path="faxNumber" placeHolderKey="register.faxNumber.placeholder" inputCSS="validate-empty" mandatory="false" value="${address.fax}" />
				<label for="faxNumber">&nbsp;</label>
			</div>

			<c:if test="${not empty countries[0].regions}">
				<c:choose>
					<c:when test="${empty address.region}">
						<c:set var="selectedRegion" scope="request" value="${regionCode}" />
					</c:when>
					<c:otherwise>
						<c:set var="selectedRegion" scope="request" value="${address.region.isocode}"/>
					</c:otherwise>
				</c:choose>
				<div class="box-address__form__select">
					<formUtil:formSelectBox idKey="regionIso" path="regionIso" mandatory="true" skipBlank="false" selectedValue="${selectedRegion}" selectCSSClass="selectpicker validate-dropdown countrySelectExportShop" skipBlankMessageKey="form.select.empty"  items="${countries[0].regions}"  itemValue="isocode" />
				</div>
			</c:if>

			<div class="box-address__form__submit">
				<c:choose>
					<c:when test="${fn:length(billingAddresses) gt 0}">
						<button type="submit" class="mat-button mat-button--action-green">
							<spring:message code="lightboxshopsettings.save" text="Save changes" />
						</button>
						<a href="#" class="box-address__form__submit__cancel">
							<spring:message code="addressform.buttonCancel" />
						</a>
					</c:when>
					<c:otherwise>
						<button type="submit" class="mat-button mat-button--action-green">
							<spring:message code="lightboxshopsettings.save" text="Save changes" />
						</button>
					</c:otherwise>
				</c:choose>
			</div>
		</form:form>
	</div>
</div>
