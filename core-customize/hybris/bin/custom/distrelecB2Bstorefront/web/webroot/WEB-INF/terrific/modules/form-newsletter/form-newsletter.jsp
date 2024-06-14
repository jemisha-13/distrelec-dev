<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/terrific/template" %>

<c:set var="modelAttribute" value="newsletterRegisterForm" />
<c:url value="/newsletter/subscribe" var="action" />
<spring:theme code="newsletterform.captchaLabel" text="Captcha" var="sCaptchLabel" />

<form:form method="post" modelAttribute="${modelAttribute}" action="${action}" cssClass="box-newsletter__form">
	<form:hidden id="newsletter.updateToken" path="updateToken" />
	<div class="row base box-newsletter__form__row">
		<div class="col-sm-6">
			<formUtil:formLabel idKey="newsletter.title" labelKey="newsletter.title" path="titleCode" mandatory="true"/>
		</div>
		<div class="col-sm-6 box-newsletter__form__row__field">
			<formUtil:formSelectBox idKey="newsletter.title" path="titleCode" mandatory="true" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${titles}"/>
		</div>
	</div>
	<div class="row base box-newsletter__form__row">
		<div class="col-sm-6">
			<formUtil:formLabel idKey="newsletter.firstName" labelKey="newsletter.firstName" path="firstName" mandatory="true"/>
		</div>
		<div class="col-sm-6 box-newsletter__form__row__field">
			<formUtil:formInputBox idKey="newsletter.firstName" path="firstName" placeHolderKey="newsletter.firstName.placeholder" inputCSS="validate-empty" mandatory="true"/>
		</div>
	</div>
	<div class="row base box-newsletter__form__row">
		<div class="col-sm-6">
			<formUtil:formLabel idKey="newsletter.lastName" labelKey="newsletter.lastName" path="lastName" mandatory="true"/>
		</div>
		<div class="col-sm-6 box-newsletter__form__row__field">
			<formUtil:formInputBox idKey="newsletter.lastName" path="lastName" placeHolderKey="newsletter.lastName.placeholder" inputCSS="validate-empty" mandatory="true"/>
		</div>
	</div>
	<div class="row base box-newsletter__form__row">
		<div class="col-sm-6">
			<formUtil:formLabel idKey="newsletter.email" labelKey="newsletter.email" path="email" mandatory="true"/>
		</div>
		<div class="col-sm-6 box-newsletter__form__row__field">
			<formUtil:formInputBox idKey="newsletter.email" path="email" placeHolderKey="newsletter.email.placeholder" inputCSS="validate-email" mandatory="true"/>
		</div>
	</div>

	<div class="row base box-newsletter__form__row">
		<div class="col-sm-6">
			<formUtil:formLabel idKey="newsletter.industry" labelKey="newsletter.industry" path="division" mandatory="true"/>
		</div>
		<div class="col-sm-6 box-newsletter__form__row__field">
			<formUtil:formSelectBox idKey="newsletter.industryCode" path="division" mandatory="true" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${divisions}"/>
		</div>
	</div>

	<%-- newsletter-selection --%>
	<div class="row base box-newsletter__form__row">
		<div class="col-sm-6">
			<formUtil:formLabel idKey="newsletter.topic" labelKey="newsletter.topic" path="role" mandatory="true"/>
		</div>
		<div class="col-sm-6 box-newsletter__form__row__field">
			<formUtil:formSelectBox idKey="newsletter.role" path="role" mandatory="true" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${roles}"/>
		</div>
	</div>

	<%-- Captcha --%>
	<div class="row row-captcha base">
		<div class="col-sm-6">
		</div>
		<div class="col-sm-6 recaptcha<c:if test="${captchaError}"> error</c:if>">
			<mod:captcha/>
		</div>
	</div>

	<%-- Checkboxes --%>
	<div class="row base row-checkbox box-newsletter__form__row">
		<div class="col-sm-6">&nbsp;</div>
		<div class="col-sm-6 box-newsletter__form__row__field">
			<div class="checkboxes">
				<div class="check-option">
					<formUtil:formCheckbox idKey="register.marketingConsent" inputCSS="validate-checkbox checkbox-big" labelKey="register.newsletter" path="marketingConsent" mandatory="true" />
				</div>
			</div>
		</div>
	</div>

	<div class="row border-top border-bottom box-newsletter__form__row">
		<div class="col-sm-6">&#160;</div>
		<div class="col-sm-6 box-newsletter__form__row__field">
			<button type="submit" class="btn btn-primary b-save"><spring:message code="newsletter.submit" /><i></i></button>
		</div>
	</div>
</form:form>

<script id="tmpl-form-newsletter-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>
<script id="tmpl-form-newsletter-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>
<script id="tmpl-form-newsletter-error-dropdown" type="text/template">
	<spring:message code="validate.error.dropdown" />
</script>
<script id="tmpl-form-newsletter-error-checkbox" type="text/template">
	<spring:message code="validate.error.checkbox" />
</script>
<script id="tmpl-form-newsletter-error-checkboxgroup" type="text/template">
	<spring:message code="validate.error.checkboxgroup" />
</script>
<script id="tmpl-form-newsletter-error-captcha" type="text/template">
	<spring:message code="validate.error.captcha" />
</script>