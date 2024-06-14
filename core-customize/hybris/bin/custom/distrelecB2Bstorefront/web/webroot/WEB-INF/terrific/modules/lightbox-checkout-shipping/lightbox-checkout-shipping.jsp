<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<spring:theme code="checkout.address.addNewAddress.buttonText" text="Add shipping address" var="sTitle" />

<div class="modal-backdrop hidden">&nbsp;</div>

<div class="modal hidden fade" id="#addressModal" tabindex="-1" role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title">${sTitle}</h5>
			</div>
			<div class="modal-body">
			<c:set var = "customerType" value = "${cartData.b2bCustomerData.customerType.code}"/>
				<c:choose>
					<c:when test="${customerType eq 'B2B' || customerType eq 'b2b' || customerType eq 'B2B_KEY_ACCOUNT'}">
						<c:url value="/checkout/detail/create/b2b/shipping" var="changeAddressUrl" />

						<form:form class="col-12 box-address__form" method="post" modelAttribute="B2BShippingAddressForm"  id="B2BShippingAddressForm"  action="${changeAddressUrl}">
							<input type="hidden" id="addressId" name="addressId" value="${address.id}" />
							<input type="hidden" id="shippingAddress" name="shippingAddress" value="true" />

							<div class="box-address__form__field">
								<input type="text" id="companyName" name="companyName" placeholder="<spring:message code="checkoutregister.companyName" /> *" value="${companyName}" />
								<label for="companyName">&nbsp;</label>
							</div>
							<div class="box-address__form__field">
								<formUtil:formInputBox idKey="companyName2" path="companyName2" placeHolderKey="register.company2" inputCSS="validate-empty" mandatory="false" maxLength="35"  value="${empty address.companyName2 ? companyName2 : address.companyName2}" />
								<label for="companyName2">&nbsp;</label>
							</div>
							
							<div class="box-address__form__field">
								<formUtil:formInputBox idKey="line1" path="line1" placeHolderKey="register.strName" inputCSS="validate-empty" mandatory="true" value="${address.line1}" />
								<label for="line1">&nbsp;</label>
							</div>
							<div class="box-address__form__field">
								<formUtil:formInputBox idKey="line2" path="line2" placeHolderKey="register.strNumber" inputCSS="validate-empty" mandatory="true" value="${address.line2}" />
								<label for="line2">&nbsp;</label>
							</div>
							<div class="box-address__form__field">
								<formUtil:formInputBox idKey="postalCode" path="postalCode" placeHolderKey="register.postalCode" mandatory="true" inputCSS="validate-empty" maxLength="10" value="${address.postalCode}" />
								<label for="postalCode">&nbsp;</label>
							</div>
							<div class="box-address__form__field">
								<formUtil:formInputBox idKey="town" path="town" placeHolderKey="register.town" inputCSS="validate-empty" mandatory="true" value="${address.town}" />
								<label for="town">&nbsp;</label>
							</div>
							<div class="box-address__form__field">
								<formUtil:formInputBox idKey="phoneNumber" path="phoneNumber" placeHolderKey="register.phoneNumber.placeholder" inputCSS="validate-empty" mandatory="true" value="${address.phone}"  />
								<label for="phoneNumber">&nbsp;</label>
							</div>
                            <div class="box-address__form__field">
         						<formUtil:formInputBox idKey="mobileNumber" path="mobileNumber" placeHolderKey="register.mobileNumber.placeholder" inputCSS="validate-empty" mandatory="false" value="${address.cellphone}"  />
         						<label for="mobileNumber">&nbsp;</label>
        					</div>							
							<div class="box-address__form__select <c:if test="${siteUid eq 'distrelec_FR'}">disable-select</c:if>">
								<formUtil:formSelectBox idKey="countryCode" path="countryIso" mandatory="true" skipBlank="false" selectedValue="${currentCountry.isocode}" selectCSSClass="selectpicker validate-dropdown countrySelectExportShop" skipBlankMessageKey="form.select.empty"  items="${countries}"  itemValue="isocode" />
							</div>
							<c:if test="${not empty countries[0].regions}">
								<c:choose>
									    <c:when test="${empty address.region}">
									        <c:set var = "selectedRegion" scope = "request" value = "${regionCode}"/>
									    </c:when>
									    <c:otherwise>
									        <c:set var = "selectedRegion" scope = "request" value = "${address.region.isocode}"/>
									    </c:otherwise>
									</c:choose>
								<div class="box-address__form__select">
									<formUtil:formSelectBox idKey="regionIso" path="regionIso" mandatory="true" skipBlank="false" selectedValue="${selectedRegion}" selectCSSClass="selectpicker validate-dropdown countrySelectExportShop" skipBlankMessageKey="form.select.empty"  items="${countries[0].regions}"  itemValue="isocode" />
								</div>
							</c:if>

							<div class="box-address__form__submit">
								<button type="submit" class="mat-button mat-button--action-green">
									<spring:message code="lightbox.openOrder.editSettings.button.save" text="Save changes" />
								</button>
								<a href="#" class="box-address__form__submit__cancel"><spring:message code="addressform.buttonCancel" /></a>
							</div>
						</form:form>
					</c:when>
					<c:otherwise>
						<c:url value="/checkout/detail/create/b2c/shipping" var="changeAddressUrl" />
                        <c:if test="${not empty b2CShippingForm}">
                            <form:form class="col-12 box-address__form" method="post" modelAttribute="b2CShippingForm" id="b2CShippingForm" action="${changeAddressUrl}">
                                <input type="hidden" id="addressId" name="addressId" value="${address.id}" />
                                <input type="hidden" id="shippingAddress" name="shippingAddress" value="true" />

                                <div class="box-address__form__select">
                                    <formUtil:formSelectBox idKey="titleCode" path="titleCode" mandatory="true" skipBlank="false" selectCSSClass="custom-select" skipBlankMessageKey="form.select.empty.mandatory" items="${titles}" selectedValue="${address.titleCode}"/>
                                </div>
                               <div class="box-address__form__field">
                                    <formUtil:formInputBox idKey="firstName" path="firstName" placeHolderKey="checkoutreigster.modal.firstName" inputCSS="validate-empty" mandatory="true" value="${address.firstName}"/>
                                    <label for="firstName">&nbsp;</label>
                                </div>
                                <div class="box-address__form__field">
                                    <formUtil:formInputBox idKey="lastName" path="lastName" placeHolderKey="checkoutregister.lastName" inputCSS="validate-empty" mandatory="true" value="${address.lastName}"/>
                                    <label for="lastName">&nbsp;</label>
                                </div>
								
                                <div class="box-address__form__field">
                                    <formUtil:formInputBox idKey="line1" path="line1" placeHolderKey="register.strName" inputCSS="validate-empty" mandatory="true" value="${address.line1}" />
                                    <label for="line1">&nbsp;</label>
                                </div>
                                <div class="box-address__form__field">
                                    <formUtil:formInputBox idKey="line2" path="line2" placeHolderKey="register.strNumber" inputCSS="validate-empty" mandatory="true" value="${address.line2}" />
                                    <label for="line2">&nbsp;</label>
                                </div>
                                <div class="box-address__form__field">
                                    <formUtil:formInputBox idKey="postalCode" path="postalCode" placeHolderKey="register.postalCode" inputCSS="validate-empty" mandatory="true" maxLength="10" value="${address.postalCode}" />
                                    <label for="postalCode">&nbsp;</label>
                                </div>
                                <div class="box-address__form__field">
                                    <formUtil:formInputBox idKey="town" path="town" placeHolderKey="register.town" inputCSS="validate-empty" mandatory="true" value="${address.town}" />
                                    <label for="town">&nbsp;</label>
                                </div>
                                <div class="box-address__form__field">
                                    <formUtil:formInputBox idKey="contactPhone" path="contactPhone" placeHolderKey="register.phoneNumber.placeholder" inputCSS="validate-empty" mandatory="true" value="${address.phone}"  />
                                    <label for="phoneNumber">&nbsp;</label>
                                </div>
                                <div class="box-address__form__field">
									<formUtil:formInputBox idKey="mobileNumber" path="mobileNumber" placeHolderKey="register.mobileNumber.placeholder" inputCSS="validate-empty" mandatory="false" value="${address.cellphone}"  />
         							<label for="mobileNumber">&nbsp;</label>
        						</div>
                                <div class="box-address__form__select <c:if test="${siteUid eq 'distrelec_FR'}">disable-select</c:if>">
									<formUtil:formSelectBox idKey="countryCode" path="countryIso" mandatory="true" skipBlank="false" selectedValue="${currentCountry.isocode}" selectCSSClass="selectpicker validate-dropdown countrySelectExportShop" skipBlankMessageKey="form.select.empty"  items="${countries}"  itemValue="isocode" />
                                </div>
                                <c:if test="${not empty countries[0].regions}">
                                	<c:choose>
									    <c:when test="${empty address.region}">
									        <c:set var = "selectedRegion" scope = "request" value = "${regionCode}"/>
									    </c:when>
									    <c:otherwise>
									        <c:set var = "selectedRegion" scope = "session" value = "${address.region.isocode}"/>
									    </c:otherwise>
									</c:choose>
                                    <div class="box-address__form__select">
                                        <formUtil:formSelectBox idKey="regionIso" path="regionIso" mandatory="true" skipBlank="false" selectedValue="${selectedRegion}" selectCSSClass="selectpicker validate-dropdown countrySelectExportShop" skipBlankMessageKey="form.select.empty"  items="${countries[0].regions}"  itemValue="isocode" />
                                    </div>
                                </c:if>
                                <div class="box-address__form__submit">
                                    <button type="submit" class="mat-button mat-button--action-green">
                                        <spring:message code="checkout.deliveryPage.save.address" text="Save changes" />
                                    </button>
                                    <a href="#" class="box-address__form__submit__cancel"><spring:message code="addressform.buttonCancel" /></a>
                                </div>
                            </form:form>
                    	</c:if>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</div>
