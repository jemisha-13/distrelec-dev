<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<spring:theme code="checkout.orderConfirmation.orderId" text="Order Id:" var="sOrderId" />
<spring:theme code="checkout.orderConfirmation.voucher.value" text="Value" var="voucherValueText" />
<spring:theme code="checkout.orderConfirmation.voucher.endDate" text="Value" var="voucherEndDateText" />
<spring:theme code="checkout.orderConfirmation.title" text="Order Complete" var="sOrderConfirmTitle" />
<spring:message code="service.nav.telephone" var="sCustomerNumber" />

<spring:theme code="checkout.orderConfirmation.text2" text="Keep your Order Number as a reference" var="sConfirmationText2" />

<c:choose>
	<c:when test="${orderApprovalConfirmation}">
		<spring:theme code="checkout.orderConfirmation.backToMyAccount.buttonText" text="Return to approval requests" var="sButtonText" />
		<c:url var="continueUrl" value="/my-account/order-approval" />
	</c:when>
	<c:otherwise>
		<spring:theme code="checkout.orderConfirmation.backToStore.buttonText" text="Return to store" var="sButtonText" />
		<c:url var="continueUrl" value="/" />
	</c:otherwise>
</c:choose>

<div class="card-wrapper">
	<div class="card-wrapper__item">
		<h2>
			${sOrderConfirmTitle}
		</h2>
		<spring:message code="checkout.orderConfirmation.processing.error" text="Cannot generate your order confirmation number" var="loadOrderCodeError"/>
		<div class="order-code-loading" data-order-code="${orderCode}">
			<span class="loading">
				<img class="loading-icon img-fluid" src="/_ui/all/media/img/page-preloader.gif">
				<span class="loading__text">
					<spring:message code="checkout.orderConfirmation.processing" text="Processing order number..." />
				</span>
			</span>
		</div>
		<div class="order-code" data-load-order-code-error="${loadOrderCodeError}">
			<div class="order-code__number">
				<span class="meta">${sOrderId}</span>
				<span class="big" >-</span>
			</div>
			<c:if test="${siteUid eq 'distrelec_FR'}">
				<mod:request-invoice template="checkout" skin="checkout"/>
			</c:if>
			<div class="order-code__voucher">
				<div class="voucher border-top" data-voucher-value-text="${voucherValueText}" data-voucher-end-date-text="${voucherEndDateText}">
					<spring:message code="checkout.orderConfirmation.voucher.codeText" text="Enter this code at your next purchase:" />
					<div class="voucher-code">-</div>
					<div class="voucher-value">0</div>
					<div class="voucher-end-date">-</div>
					<spring:message code="checkout.orderConfirmation.voucher.emailSent" text="An email with the voucher code was sent to you." />
				</div>
			</div>
		</div>
		<div class="item-text">
			<c:set var="userEmail" value="<a href='mailto:${userId}'>${userId}</a>"/>
			<spring:message code="service.nav.telephone" var="customerSupportPhoneNumber" />
			<p class="large-text"><spring:message code="checkout.consent.sentTo" arguments="${userEmail}" /></p>
			<p><spring:message code="checkout.consent.contact" arguments="${customerSupportPhoneNumber}"/></p>
		</div>
	</div>
	<c:if test="${user.customerType eq 'GUEST'}">
		<div class="card-wrapper__item is-guest-b2c">
			<div class="guest-b2c bg-pale-grey">
				<h2 id="order.confirm.guest.title" class="guest-b2c__title"><spring:message code="newcheckout.orderConfirmation.signup.header"/></h2>

				<div class="row">
					<div class="col-lg-6">
						<div class="guest-b2c__column is-left">
							<h3 id="order.confirm.guest.b2c.title" class="guest-b2c__subtitle"><spring:message code="newcheckout.orderConfirmation.signup.b2cHeader"/></h3>

							<p id="order.confirm.guest.b2c.subtitle" class="guest-b2c__note"><spring:message code="newcheckout.orderConfirmation.signup.b2cSubheader"/></p>

							<div class="guest-b2c__form">
								<form:form class="js-guest-b2c-form" modelAttribute="checkoutGuestRegistrationForm">
									<div class="form-group js-guest-b2c-form-group">
										<formUtil:formLabel idKey="orderConfirmMainPassword"
															labelId="order.confirm.guest.b2c.password.label"
															labelCSS="guest-b2c__form-item__label"
															labelKey="newcheckout.password.label"/>

										<div class="p-relative">
											<formUtil:formPasswordBox idKey="orderConfirmMainPassword" inputCSS="guest-b2c__form-item__input js-guest-b2c-field js-main-pass" path="password"/>

											<i class="fa fa-check tickItem js-tickItem"></i>

											<span class="form-group__pwd-reveal js-pwd-reveal">
												<i class="fas fa-eye js-eye form-group__pwd-reveal-icon hidden">&nbsp;</i>
												<i class="fas fa-eye-slash js-eye-slash form-group__pwd-reveal-icon">&nbsp;</i>
											</span>
										</div>

										<small id="order.confirm.guest.b2c.password.note" class="form-group__note">
											<spring:message code="register.pwd.help.text" />
										</small>
									</div>

									<div class="form-group js-guest-b2c-form-group">
										<formUtil:formLabel idKey="orderConfirmMainPasswordRepeat"
															labelId="order.confirm.guest.b2c.password.confirm.label"
															labelCSS="guest-b2c__form-item__label"
															labelKey="register.checkPwd"/>

										<div class="p-relative">
											<formUtil:formPasswordBox idKey="orderConfirmMainPasswordRepeat" inputCSS="guest-b2c__form-item__input js-guest-b2c-field js-confirm-pass" path="repeatPassword"/>

											<i class="fa fa-check tickItem js-tickItem"></i>

											<span class="form-group__pwd-reveal js-pwd-reveal">
												<i class="fas fa-eye js-eye form-group__pwd-reveal-icon hidden">&nbsp;</i>
												<i class="fas fa-eye-slash js-eye-slash form-group__pwd-reveal-icon">&nbsp;</i>
											</span>
										</div>

										<div class="form-group__error js-form-error-msg js-is-match-error hidden">
											<small id="order.confirm.guest.b2c.password.match.error" class="form-group__note"><spring:message code="validation.checkPwd.equals"/></small>
										</div>

										<div class="form-group__error js-form-error-be-msg hidden">
											<small id="order.confirm.guest.b2c.password.recaptcha.error" class="form-group__note js-form-error-be-msg-text"></small>
										</div>
									</div>

									<div class="form-group">
										<mod:password-strength />
									</div>

									<div class="guest-b2c__column-button">
										<button id="order.confirm.guest.b2c.submit" class="ux-btn ux-btn--red ux-btn--flex js-guest-b2c-form-submit"
												type="button" disabled><spring:message code="login.checkout.create.account.button"/><mod:loading-spinner/></button>
									</div>

									<div class="recaptcha js-recaptcha">
										<mod:captcha/>
									</div>

									<input type="hidden" name="orderCode" value="${orderCode}">
									<input type="hidden" name="email" value="${userId}">
								</form:form>
							</div>
						</div>
					</div>

					<div class="col-lg-6">
						<div class="guest-b2c__column is-right">
							<h3 id="order.confirm.guest.b2b.title" class="guest-b2c__subtitle"><spring:message code="newcheckout.orderConfirmation.signup.b2bHeader"/></h3>

							<p id="order.confirm.guest.b2b.note1" class="guest-b2c__note">
								<spring:message code="newcheckout.orderConfirmation.signup.b2bSubheader"/>
							</p>

							<p id="order.confirm.guest.b2b.note2" class="guest-b2c__note">
								<spring:message code="newcheckout.orderConfirmation.signup.b2bListHeader"/>
							</p>

							<ul class="guest-b2c__check-list">
								<li>
									<i class="fa fa-check"></i><span id="order.confirm.guest.b2b.benefit1"><spring:message code="newcheckout.orderConfirmation.signup.b2bListText1"/></span>
								</li>

								<li>
									<i class="fa fa-check"></i><span id="order.confirm.guest.b2b.benefit2"><spring:message code="newcheckout.orderConfirmation.signup.b2bListText2"/></span>
								</li>

								<li>
									<i class="fa fa-check"></i><span id="order.confirm.guest.b2b.benefit3"><spring:message code="newcheckout.orderConfirmation.signup.b2bListText3"/></span>
								</li>
							</ul>

							<div class="guest-b2c__column-button">
								<c:url value="/registration" var="continueUrl"/>
								<a id="order.confirm.guest.b2b.submit" class="ux-btn ux-btn--red ux-btn--flex" href="${continueUrl}"><spring:message code="newcheckout.orderConfirmation.signup.b2bRegister.buttonText"/></a>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</c:if>
	<c:if test="${updateProfile and user.customerType ne 'GUEST'}">
		<spring:message code="form.global.error.erpcommunication" var="sError"/>
		<spring:message code="form.select.empty" var="sSelect"/>
		<spring:message code="validate.error.dropdown" var="sMandatory"/>

		<div class="card-wrapper__item card-wrapper__item--second update-profile" data-error-message="${sError}" data-mandatory-message="${sMandatory}">
			<div class="update-profile__item">
				<h3><spring:message code="checkout.orderConfirmation.updateProfile" text="Please help us by completing your profile information" /></h3>
				<c:if test="${not empty departments}">
					<div class="update-profile__item__field">
						<label for="department"><spring:message code="register.department" text="Department" /></label>
					</div>
					<div class="update-profile__item__field">
						<select id="department" class="selectpicker validate-dropdown">
							<option value="" disabled="disabled" selected="selected">${sSelect}</option>
							<c:forEach items="${departments}" var="department">
								<option value="${department.code}">
									${department.name}
								</option>
							</c:forEach>
						</select>
					</div>
				</c:if>
				<c:if test="${not empty functions}">
					<div class="update-profile__item__field">
						<label for="function"><spring:message code="register.function" text="Function" /></label>
					</div>
					<div class="update-profile__item__field">
						<select id="function" class="selectpicker validate-selectbox">
							<option value="" disabled="disabled" selected="selected">${sSelect}</option>
							<c:forEach items="${functions}" var="function">
								<option value="${function.code}">
									${function.name}
								</option>
							</c:forEach>
						</select>
					</div>
				</c:if>
				<div class="button-container">
					<a class="btn btn-primary btn-update-profile" href="#"><spring:message code="checkout.orderConfirmation.updateProfile.submit" text="Save" /><i class="icon-arrow"></i></a>
					<span class="error"></span>
				</div>
			</div>
		</div>
	</c:if>
</div>
