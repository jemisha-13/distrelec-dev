<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="checkoutregister.b2boption" var="sB2Boption" />
<spring:message code="checkoutregister.b2coption" var="sB2Coption" />
<spring:message code="checkoutregister.customerNumberHintB2C" var="customerNumberHintB2C" />
<spring:message code="validation.checkPwd.equals" var="sPasswordNoMatch" />
<spring:message code="checkoutregister.titleHelp" var="sTitleHelp" />
<spring:message code="checkoutregister.addNumbers" var="sMoreNumber" />
<spring:message code="checkoutregister.fax" var="sFax" />
<spring:message code="standalone.reg.step3.createAcc" var="sContinue" />
<spring:message code="checkoutregister.radio.yes" var="sRadioYes" />
<spring:message code="checkoutregister.radio.no" var="sRadioNo" />
<spring:message code="logindata.Newsletter.privacyInfo" var="sPrivacy" />
<spring:message code="register.newsletter" var="sMarketingConset" />
<spring:message code="checkoutregister.terms" var="sTerms" arguments="/gtc/cms/agb" />
<spring:message code="checkoutregister.helpText" var="sHelpText" />
<spring:message code="addressform.title.mr" var="regMr" text="Mr" />
<spring:message code="addressform.title.ms" var="regMs" text="Ms" />
<spring:message code="address.title.invalid" var="sTitleInvalid" text="Please select a title" />

<c:url value="registration/b2c/async" var="sB2CsubmitAction" />
<c:set value="${registerB2BForm.registrationType eq 'CHECKOUT' ? '/login/checkout' : '/login'}" var="loginUrl" />

<spring:url value="/_ui/desktop/common/images/spinner.gif" var="spinnerUrl" />

<form:form class="form-b2c js-formB2c js-form" method="post" modelAttribute="registerForm" action="${sB2CsubmitAction}">
	<mod:loading-state skin="loading-state" template="high-priority"/>
	<input id="existingCustomer" name="existingCustomer" type="hidden" value="false" checked="checked" />
	<section class="card-wrapper col-12">
		<div class="row">
			<div class="form-b2c__title">
				<div class="inner">
					<h3>2. <spring:message code="standalone.reg.step2.b2c.title" /> </h3>
				</div>
			</div>
			<div class="form-b2c__form col-12">
				<div class="row">
					<div class="form-b2c__form__title col-12">
						<div class="form-b2c__form__field form-b2c__form__field--existing col-12 col-lg-8">
							<label id="regOrgNumberExistingAccB2C">
								<spring:message code="register.organizationlNumber.existing.account" />
							</label>
							<div class="form-b2c__form__field__custno">
								<div class="row">
									<div class="col-6 col-lg-3">
										<div class="form-check form-check-inline" data-value="Yes">
											<input type="radio" class="radio form-check-input" name="customer_type_select" id="radioSelectYes" value="true">
											<label for="radioSelectYes">
													${sRadioYes}
											</label>
										</div>
									</div>

									<div class="col-6 col-lg-3">
										<div class="form-check form-check-inline" data-value="No">
											<input type="radio" class="radio form-check-input" name="customer_type_select" id="radioSelectNo" checked value="false">
											<label for="radioSelectNo">
													${sRadioNo}
											</label>
										</div>
									</div>
								</div>
							</div>
							<div class="form-b2c__form__field__custno__input hidden">
								<label id="regFormSeminarSignupCustNumberB2C">
									<spring:message code="formSeminarSignup.customerNumber" />
								</label>
								<div class="inputPopup inputPopup--grouped">
									<div class="inputPopup__center">
										<div id="CustNumberHint" class="inputorgNumberPopup__popup">${customerNumberHintB2C}</div>
										<formUtil:formInputBox idKey="register.CustomerNumberB2C" mandatory="false" path="customerId" placeHolderKey="" maxLength="10" inputCSS="customerNumber js-validate js-validate-minimum-characters" />
										<i class="tickItem fa fa-check hidden"></i>
										<i class="tickItemError fa fa-times hidden"></i>
										<div class="field-msgs" id="registerForm-error-customerNumber-b2c">
											<div class="error hidden">
												<span id="customerNumber.error"><spring:message code="validate.error.required" /></span>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<section class="form-b2c__form__wrapper js-personaldetails col-12 col-lg-10">

						<div class="form-b2c__form__field col-12">
							<h4 id="regAboutYouTitleB2C">
								<spring:message code="standalone.reg.step3.aboutYou" />
							</h4>
							<label id="regTitleB2C">
								<spring:message code="register.title" />
							</label>
							<div class="form-b2c__form__field__title field-title">
								<div class="row">
									<div class="col-12 col-lg-5">
										<div class="row">
											<div class="col-6">
												<div class="form-check-inline">
													<form:radiobutton path="titleCode" id="inlineMr" value="mr" cssClass="js-validate title"/>
													<form:label for="inlineMr" path="titleCode">${regMr}</form:label>
												</div>
											</div>

											<div class="col-6">
												<div class="form-check-inline">
													<form:radiobutton path="titleCode" id="inlineMs" value="ms" cssClass="js-validate title"/>
													<form:label for="inlineMs" path="titleCode">${regMs}</form:label>
												</div>
											</div>

											<i class="tickItem fa fa-check hidden"></i>
											<i class="tickItem fa fa-times hidden"></i>
											<div class="field-msgs" id="registerForm-error-title-b2c">
												<div class="error hidden">
													<span id="titleCode.errorsB2C">${sTitleInvalid}</span>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

						<div class="form-b2c__form__field col-12">
							<div class="row">
								<div class="col-lg-6">
									<label id="regFirstNameB2C"><spring:message code="addressform.firstName" /></label>
									<div class="form-group">
										<formUtil:formInputBox idKey="register.firstNameB2C" path="firstName" placeHolderKey="" maxLength="35" inputCSS="name js-validate js-validate-empty" mandatory="true"/>
										<i class="tickItem fa fa-check hidden"></i>
										<i class="tickItemError fa fa-times hidden"></i>
										<div class="field-msgs" id="registerForm-error-firstName-b2c">
											<div class="error hidden">
												<span id="registerForm-error-firstName-b2c-txt"><spring:message code="register.firstName.invalid" /></span>
											</div>
										</div>
									</div>
								</div>

								<div class="col-lg-6">
									<label id="regLastNameB2C"><spring:message code="register.lastName" /></label>
									<div class="form-group">
										<formUtil:formInputBox idKey="register.lastNameB2C" path="lastName" placeHolderKey="" maxLength="35" inputCSS="name js-validate js-validate-empty" mandatory="true" />
										<i class="tickItem fa fa-check hidden"></i>
										<i class="tickItemError fa fa-times hidden"></i>
										<div class="field-msgs" id="registerForm-error-lastName-b2c">
											<div class="error hidden">
												<span id="registerForm-error-lastName-b2c-txt"><spring:message code="register.lastName.invalid" /></span>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

						<c:if test="${currentCountry.isocode eq 'IT'}">
							<div class="form-b2c__form__field form-b2c__form__field--fiscale col-12 fiscale-container">
								<div class="row">
									<div class="col-12">
										<formUtil:formLabel idKey="register.codiceFiscaleB2Cexisting" labelKey="register.codiceFiscale" path="codiceFiscale" />
										<div class="form-group">
											<div class="p-relative js-p-relative">
												<formUtil:formInputBox idKey="register.codiceFiscaleB2Cexisting" path="codiceFiscale" placeHolderKey="register.codiceFiscale.placeholder" maxLength="30" inputCSS="js-validate js-validate-minimum-characters codice" mandatory="true" />
												<i class="tickItem fa fa-check hidden"></i>
												<i class="tickItemError fa fa-times hidden"></i>
											</div>
											<div class="field-msgs" id="registerForm-error-fiscale-b2c">
												<div class="error hidden">
													<span id="registerForm-error-fiscale-b2c-txt"><spring:message code="validate.error.required" /></span>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</c:if>

						<div class="col-12">
							<div class="row">
								<div class="form-b2c__form__field col-12 ${isExportShop ? 'order-1' : ''}">
									<label id="regPhoneNumberB2C"><spring:message code="addressform.phoneNumber" /></label>
									<div class="form-group">
										<formUtil:formInputBox idKey="register.phoneNumberB2C" path="telephoneNumber" inputCSS="js-validate js-validate-empty js-libphonenumber" placeHolderKey="" maxLength="35"  mandatory="true" />
										<i class="tickItem fa fa-check hidden"></i>
										<i class="tickItemError fa fa-times hidden"></i>
										<div class="field-msgs" id="registerForm-error-phoneNumberB2C-b2c">
											<div class="error hidden">
												<span id="registerForm-error-phoneNumberB2C-b2c-txt"><spring:message code="address.contactPhone.invalid" /></span>
											</div>
										</div>
									</div>
								</div>

								<div class="form-b2c__form__field col-12 ${isExportShop ? 'order-0' : ''}">
									<c:choose>
										<c:when test="${!isExportShop}">
											<c:choose>
												<c:when test="${fn:length(countriesB2C) gt 1}">
													<formUtil:formLabel idKey="register.countryCodeB2C" labelKey="checkoutregister.country" path="countryCode"  />
													<div class="form-group-select">
														<div class="p-relative js-p-relative">
															<formUtil:formSelectBox idKey="register.countryCodeB2C" path="countryCode" mandatory="true" skipBlank="false" selectCSSClass="selectpicker ux-selectpicker js-validate js-validate-select js-country-select countrySelectExportShop custom-select select-countryCode" skipBlankMessageKey="form.select.empty"  items="${countriesB2C}" itemValue="isocode" />
															<i class="ux-selectpicker__angle-down fa fa-angle-down"></i>
															<i class="tickItem fa fa-check hidden"></i>
															<i class="tickItemError fa fa-times hidden"></i>
														</div>
														<div class="field-msgs" id="registerForm-error-countryCodeBiz-b2c">
															<div class="error hidden">
																<span id="registerForm-error-countryCodeBiz-b2c-txt"><spring:message code="validate.error.required" /></span>
															</div>
														</div>
													</div>
												</c:when>
												<c:otherwise>
													<input id="countryCode" type="hidden" value="${currentCountry.isocode}" name="countryCode" />
													<i class="tickItem fa fa-check hidden"></i>
													<i class="tickItemError fa fa-times hidden"></i>
													<div class="field-msgs" id="registerForm-error-countryCodeBiz-b2c-hidden">
														<div class="error hidden">
															<span id="registerForm-error-countryCodeBiz-b2c-hidden-txt"><spring:message code="validate.error.required" /></span>
														</div>
													</div>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${currentCountry.isocode eq 'EX'}">
													<formUtil:formLabel idKey="register.countryCodeB2C" labelKey="checkoutregister.country" path="countryCode"  />
													<div class="form-group-select">
														<div class="inputPopup">
															<div class="inputPopup__center">
																<div class="inputPopup__popup" id="countryList">
																	<spring:message code="checkoutregister.countryNotInList"/>
																</div>
															</div>
														</div>
														<div class="p-relative js-p-relative">
															<formUtil:formSelectBox idKey="register.countryCodeB2C" path="countryCode" mandatory="true" skipBlank="false" selectCSSClass="selectpicker ux-selectpicker js-validate js-country-select js-validate-select countrySelectExportShop custom-select" skipBlankMessageKey="form.select.empty"  items="${countriesB2C}" itemValue="isocode" />
															<i class="ux-selectpicker__angle-down fa fa-angle-down"></i>
															<i class="tickItem fa fa-check hidden"></i>
															<i class="tickItemError fa fa-times hidden"></i>
														</div>
														<div class="field-msgs" id="registerForm-error-countryCodeBiz-b2c">
															<div class="error hidden">
																<span id="registerForm-error-countryCodeBiz-b2c-txt"><spring:message code="validate.error.required" /></span>
															</div>
														</div>
													</div>
												</c:when>
												<c:otherwise>
													<input id="countryCode" type="hidden" value="${currentCountry.isocode}" name="countryCode" />
													<i class="tickItem fa fa-check hidden"></i>
													<i class="tickItemError fa fa-times hidden"></i>
													<div class="field-msgs" id="registerForm-error-countryCode-b2c">
														<div class="error hidden">
															<span id="registerForm-error-countryCode-b2c-txt"><spring:message code="validate.error.required" /></span>
														</div>
													</div>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</div>
							</div>
						</div>
					</section>
					<section class="form-b2c__form__wrapper col-12 col-lg-10">
						<div class="form-b2c__form__field col-12">
							<h4 id="regStep3AccountDetailsB2C">
								<spring:message code="standalone.reg.step3.accDetails" />
							</h4>
							<label id="standalone.email.label">
								<spring:message code="standalone.reg.step3.email.label" />
							</label>
							<div class="form-group has-loader">
								<div class="p-relative js-p-relative">
									<formUtil:formInputBox idKey="register.emailB2C" path="email" placeHolderKey="standalone.reg.step3.email.label" maxLength="241" inputCSS="js-validate js-validate-email js-already-exists-validation" mandatory="true" />
									<i class="tickItem fa fa-check hidden"></i>
									<i class="tickItemError fa fa-times hidden"></i>
									<span class="loading js-loading hidden">
										<img class="loading-icon img-fluid" src="/_ui/all/media/img/page-preloader.gif">
									</span>
								</div>
								
								<div class="field-msgs" id="registerForm-error-email-b2c">
									<div class="error hidden">
										<span id="email.errorsB2C"><spring:message code="address.email.invalid" /></span>
									</div>

									<div class="error-already-existing js-error-already-existing hidden">
										<span id="email.errors.already.existingB2C">
											<spring:message code="address.email.alreadyExists" arguments="${loginUrl}"/>
										</span>
									</div>
								</div>
							</div>

							<div class="inputPopup inputPopup--grouped">
								<div class="inputPopup__center">
									<div id="standalone.useThisToLogin" class="inputorgNumberPopup__popup"><spring:message code="standalone.reg.step3.primaryemail" /></div>
								</div>
							</div>
						</div>

						<div class="form-b2c__form__field col-12" id="pwd-container">
							<label id="regPasswordLabelB2C">
								<spring:message code="newcheckout.password.label" />
							</label>
							<div class="form-group form-group--password mb-2">
								<formUtil:formPasswordBox idKey="passwordB2C" inputCSS="js-validate password-field password-check" path="pwd" mandatory="true" />
								<span class="form-group__pwd-reveal">
									<i class="fas fa-eye form-group__pwd-reveal-icon hidden">&nbsp;</i>
									<i class="fas fa-eye-slash form-group__pwd-reveal-icon">&nbsp;</i>
								</span>
								<div id="b2c_registration_pwd_help_text" class="help-text">
									<p id="b2c_registration_pwd_help_text-txt"><spring:message code="register.pwd.help.text" /></p>
								</div>
								<i class="tickItem fa fa-check hidden"></i>
								<i class="tickItemError fa fa-times hidden"></i>
							</div>
						</div>

						<div class="form-b2c__form__field col-12">
							<label id="regPasswordConfirmationLabelB2C">
								<spring:message code="register.checkPwd" />
							</label>
							<div class="form-group form-group--password mb-4">
								<formUtil:formPasswordBox idKey="registercheckPwdB2C" path="checkPwd" inputCSS="js-validate passwordSecond password-check" mandatory="true" errorPath="${modelAttribute}" />
								<span class="form-group__pwd-reveal">
									<i class="fas fa-eye form-group__pwd-reveal-icon hidden">&nbsp;</i>
									<i class="fas fa-eye-slash form-group__pwd-reveal-icon">&nbsp;</i>
								</span>
								<i class="tickItem fa fa-check hidden"></i>
								<i class="tickItemError fa fa-times hidden"></i>
								<div class="field-msgs" id="registerForm-error-pwdSecond-b2c">
									<div class="error hidden">
										<span id="password.not.match.error-b2c">${sPasswordNoMatch}</span>
									</div>
								</div>
								<mod:password-strength />
							</div>
						</div>

						<div class="form-b2c__form__field form-b2c__form__field--terms col-12 col-lg-12">
							<div class="recaptcha-holder recaptcha">
								<mod:captcha template="reg" htmlClasses="form-b2c" callback="onSubmitRegB2C" />
							</div>
						</div>

					</section>
				</div>
			</div>
		</div>
	</section>

    <section class="card-wrapper section-form col-12 js-consentSection">
		<c:url value="/data-protection/cms/datenschutz" var="privacyLink"/>
		<c:url value="/general-terms-and-conditions/cms/agb?marketingPopup=false" var="termsLink"/>

		<div class="form-b2c__form half-container">
			<div class="form-b2c__form__field is-full">
				<h4 class="no-padding"><spring:message code="text.communication.preferences"/></h4>
			</div>
		</div>

        <div class="half-container">
			<div>
				<span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span>
				<formUtil:formCheckbox idKey="text.preferences.email.b2c" labelKey="text.preferences.receive.communication" path="marketingConsent" inputCSS="checkbox js-checkbox" mandatory="false"/>
			</div>
			<div id="text.preferences.newsletter.disclaimer.b2c" class="description-text"><spring:message code="text.preferences.newsletter.disclaimer"/></div>
        </div>

		<div class="half-container">
			<div>
				<ul>
					<li><span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="text.preferences.sms.standalone.b2c" labelKey="text.preferences.sms" path="smsConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></li>
					<li><span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="text.preferences.phone.standalone.b2c" labelKey="text.preferences.phone" path="phoneConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></li>
					<li><span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="text.preferences.post.standalone.b2c" labelKey="text.preferences.post" path="postConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></li>
				</ul>
			</div>
			<div id="text.preferences.sms.disclaimer.b2c" class="description-text"><spring:message code="text.preferences.sms.disclaimer"/></div>
		</div>

		<div class="half-container">
			<div><span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="personalise-communication-standalone" labelKey="text.preferences.receive.personalised" path="personalisationConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></div>
			<div id="text.preferences.personalise.content.legal.b2c" class="description-text"><spring:message code="text.preferences.personalise.content.legal"/></div>
		</div>
		<div class="half-container">
			<div><span class="js-checkboxIcon"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="profling-communication-standalone" labelKey="text.preferences.combine.data" path="profilingConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></div>
			<c:url value="/information-notice/cms/datenschutz#thirdPartyCookies" var="thirdPartyCookiesLink"/>
			<div id="text.preferences.profiling.content.legal.b2c" class="description-text"><spring:message code="text.preferences.profiling.data.legal" arguments="${thirdPartyCookiesLink}"/></div>
		</div>

		<p id="text.preferences.newsletter.disclaimer.after.register.b2c"><spring:message code="text.preferences.newsletter.disclaimer.after.register"/></p>

		<div class="half-container half-container--t_and_c">
			<div data-error-text="<spring:message code="text.preferences.terms.error"/>" class="privacy-policy"><span  class="js-checkboxIcon js-terms-check"><i class="fas fa-check-square"></i></span><formUtil:formCheckbox idKey="privacyPolicyStandaloneB2c" labelKey="text.registration.accept.privacy" labelArguments="${termsLink},${privacyLink}" path="termsOfUseOption" inputCSS="js-validate js-checkbox js-termsCheckbox" mandatory="false"/></div>
			<div id="text.registration.accept.registering.b2c" class="description-text"><spring:message code="text.registration.accept.registering"/></div>
		</div>

		<div class="form-b2c__form__field form-b2c__form__field--create col-12 col-lg-8">
			<div class="row">
				<div class="col-12 col-lg-4">
					<button class="mat-button mat-button--action-green btn-success mb-4 btn-register-customer-form js-submitB2cRegister disabled" type="submit" data-aainteraction="submit form">
						<spring:message code="standalone.reg.step3.createAcc" />
					</button>
				</div>
			</div>
		</div>
    </section>

    <section class="card-wrapper padded-content col-12">
		<c:url value="/cms/contact" var="contactLink"/>
        <h2 id="text.preferences.data.b2c"><spring:message code="text.preferences.data"/></h2>
        <p id="text.preferences.privacy.policy.b2c"><spring:message code="text.preferences.privacy.policy"/></p>
        <p id="text.preferences.privacy.policy.2.b2c"><spring:message code="text.preferences.privacy.policy.2" arguments="${contactLink}"/></p>
        <p id="text.preferences.question.b2c"><spring:message code="text.preferences.question"/></p>
        <h4 id="text.registration.control.b2c"><spring:message code="text.registration.control"/></h4>
        <p id="text.registration.change.communication.b2c"><spring:message code="text.registration.change.communication"/></p>
    </section>

	<script id="tmpl-bisnoderegb2c-validation-error-empty" type="text/template">
			<spring:message code="validate.error.required" />
	</script>
	<script id="tmpl-bisnoderegb2c-validation-error-email" type="text/template">
			<spring:message code="validate.error.email" />
	</script>

	<input type="hidden" id="custNumberB2C" name="custNumberB2C" />
	<input type="hidden" id="regTypeb2c" name="registrationType" value="${registerB2BForm.registrationType}"/>
</form:form>
