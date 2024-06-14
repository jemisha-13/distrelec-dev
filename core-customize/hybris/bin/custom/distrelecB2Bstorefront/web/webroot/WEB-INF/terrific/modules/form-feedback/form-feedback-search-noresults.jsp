<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/search/feedback" />

<spring:theme code="formfeedback.title" var="sFeedbackTitle" />
<spring:theme code="formfeedback.intro.noresults" var="sFeedbackIntro" />
<spring:theme code="formfeedback.nameLabel" var="sFeedbackNameLabel" />
<spring:theme code="formfeedback.emailLabel" var="sFeedbackEmailLabel" />
<spring:theme code="formfeedback.contactNumberLabel" var="sFeedbackContactNumberLabel" />
<spring:theme code="formfeedback.phoneLabel" var="sFeedbackPhoneLabel" />
<spring:theme code="formfeedback.feedbackLabel" var="sFeedbackFeedbackLabel" />
<spring:theme code="formfeedback.sendButton" var="sFeedbackSendButton" />
<spring:theme code="formfeedback.intro.looking.for" var="sFeedbackLookingFor" />
<spring:theme code="formfeedback.intro.fillInForm" var="sFeedbackFillInForm" />
<spring:theme code="formfeedback.intro.looking.forTerm" var="sFeedbackLookingForTerm" arguments="${searchTerm}" />
<spring:theme code="formfeedback.manufacturer.type" var="sFeedbackManufacturerType" />
<spring:theme code="formfeedback.product.name" var="sFeedbackProductName" />
<spring:theme code="formfeedback.tellUsMore" var="sFeedbackTellUsMore" /> 
<spring:theme code="product.manufacturer.select" var="sSelectManufacturer" />
<spring:theme code="product.manufacturer" var="sManufacturer" />
<spring:theme code="product.manufacturer.other" var="sOtherManufacturer" />
<spring:theme code="product.manufacturer.other.hint" var="sOtherManufacturerHint" />

<sec:authorize access="hasAnyRole('ROLE_OCICUSTOMERGROUP','ROLE_ARIBACUSTOMERGROUP','ROLE_CXMLCUSTOMERGROUP')">
	<c:set var="isEproc" value="true" />
</sec:authorize>

<div id='feedbackFormContent' class="mod-form-feedback__container base">
	<div class="mod-form-feedback__container__title">
		<h3>${sFeedbackLookingFor}</h3>
		<h4>${sFeedbackFillInForm}</h4>
		<h5>${sFeedbackLookingForTerm}</h5>
	</div>

	<form:form action="${action}" method="post" class="mod-form-feedback__container__form" modelAttribute="feedbackForm">
		<input type="hidden" id="searchTerm" name="searchTerm" value="${searchTerm}">
		<div class="row manSearch js-manSearch js-feedback-form-row">
			<label for="manufacturer" class="required manSearch__label">${sManufacturer}</label>

			<div class="manSearch__inner">
				<select id="manufacturer" name="manufacturer" class="validate-dropdown manSearch__select js-manSearch-select">
					<option value="placeholder" disabled selected hidden>${sSelectManufacturer}</option>
					<%-- Dev note: "other_manufacturer" is used as selector in JS as well --%>
					<option value="other_manufacturer" data-code="2000">${sOtherManufacturer}</option>
				</select>
				<input class="manSearch__input js-manSearch-input" type="text">
			</div>
		</div>

		<div class="row row--manufacturer js-feedback-form-row hidden">
			<label for="manufacturerTypeOtherName" class="required">${sOtherManufacturer}</label>
			<input type="text" name="manufacturerTypeOtherName" id="manufacturerTypeOtherName" class="field" />
			<mod:toolsitem template="toolsitem-information" skin="information" message="${sOtherManufacturerHint}" title="Info" htmlClasses="tooltip-hover active" />
		</div>

		<div class="row js-feedback-form-row">
			<label for="manufacturerType" class="required">${sFeedbackManufacturerType}</label>
			<input type="text" name="manufacturerType" id="manufacturerType" class="field validate-email validate-empty" />
		</div>

		<sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP')" >
			<div class="row js-feedback-form-row">
				<label for="email" class="required">${sFeedbackEmailLabel}</label>
				<input type="text" name="email" id="email" class="field validate-email emailField" />
			</div>	
		</sec:authorize>	
		
		<div class="row js-feedback-form-row">
			<label for="tellUsMore" id="tellUsMore-label">${sFeedbackTellUsMore}</label>
			<textarea rows="4" name="tellUsMore" id="tellUsMore" class="field validate-tellUsMore validate-min-max"  data-min="0" data-max="250"/></textarea>  
			<div class="charCountContainer">
				<div class="charCountDiv">
					<span id="characters" class="charCount">0</span><span class="charCount">/250</span>
				</div>
			</div>
		</div>	
		
		<div class="row row--recaptcha js-feedback-form-row">
			<div class="recaptcha<c:if test="${captchaError}"> error</c:if>">
				<mod:captcha htmlClasses="feedback-form" callback="onCaptchaSubmitFeedbackForm" />
			</div>
		</div>

		<div class="row">
			<button type="submit" class="btn btn-primary btn--feedback">${sFeedbackSendButton}</button>
		</div>

	</form:form>

</div>

<script id="tmpl-form-feedback-validation-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>
<script id="tmpl-form-feedback-validation-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>
<script id="tmpl-form-feedback-validation-error-captcha" type="text/template">
	<spring:message code="validate.error.captcha" />
</script>
<script id="tmpl-form-feedback-validation-error-tellUsMore" type="text/template">
	<spring:message code="validate.error.tellUsMore" />
</script> 
