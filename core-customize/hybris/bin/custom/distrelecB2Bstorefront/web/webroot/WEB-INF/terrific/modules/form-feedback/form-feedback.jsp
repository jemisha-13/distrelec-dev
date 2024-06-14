<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="cssErrorClass" value="" />

<spring:theme code="formfeedback.title" var="sFeedbackTitle" />
<spring:theme code="formfeedback.intro" var="sFeedbackIntro" />
<spring:theme code="formfeedback.nameLabel" var="sFeedbackNameLabel" />
<spring:theme code="formfeedback.emailLabel" var="sFeedbackEmailLabel" />
<spring:theme code="formfeedback.contactNumberLabel" var="sFeedbackContactNumberLabel" />
<spring:theme code="formfeedback.phoneLabel" var="sFeedbackPhoneLabel" />
<spring:theme code="formfeedback.feedbackLabel" var="sFeedbackFeedbackLabel" />
<spring:theme code="formfeedback.sendButton" var="sFeedbackSendButton" />

<div class="feedback-box border-top border-bottom base padding">
	<p class="form-intro">${sFeedbackIntro}</p>

	<form:form action="${action}" method="post" modelAttribute="feedback">
		<c:choose>
			<c:when test="${user.uid ne 'anonymous'}">
				<c:set var="address" value="${user.defaultShippingAddress}" />
				<c:if test="${not empty user.defaultBillingAddress}">
					<c:set var="address" value="${user.defaultBillingAddress}" />
				</c:if>
				<div class="row">
					<div class="gu-4 label-box">
						<label for="name">${sFeedbackNameLabel} <span class="mandatory"> *</span></label>
					</div>
					<div class="gu-4">
						<input type="text" name="name" id="name" class="field validate-empty" value="${user.firstName}${not empty user.lastName ? '&nbsp;' : ''}${user.lastName}" />
					</div>
				</div>
				<div class="row">
					<div class="gu-4 label-box">
						<label for="email">${sFeedbackEmailLabel} <span class="mandatory"> *</span></label>
					</div>
					<div class="gu-4">
						<input type="text" name="email" id="email" class="field validate-email" value="${user.email}" />
					</div>
				</div>
				<div class="row">
					<div class="gu-4 label-box">
						<label for="contactnumber">${sFeedbackPhoneLabel}</label>
					</div>
					<div class="gu-4">
						<%-- address.contactNumber: Adresse hat kein Attribut contactNumber --%>
						<input type="text" name="contactnumber" id="contactnumber" class="field" value="" />
					</div>
				</div>
			</c:when>
			<c:when test="${not empty sentFeedback}">
				<div class="row">
					<div class="gu-4 label-box">
						<label for="a_name">${sFeedbackNameLabel} <span class="mandatory"> *</span></label>
					</div>
					<div class="gu-4">
						<input type="text" name="name" id="a_name" class="field validate-empty" value="${fn:escapeXml(sentFeedback.name)}" />
					</div>
				</div>
				<div class="row">
					<div class="gu-4 label-box">
						<label for="a_email">${sFeedbackEmailLabel} <span class="mandatory"> *</span></label>
					</div>
					<div class="gu-4">
						<input type="text" name="email" id="a_email" class="field validate-email" value="${sentFeedback.email}" />
					</div>
				</div>
				<div class="row">
					<div class="gu-4 label-box">
						<label for="a_phone">${sFeedbackPhoneLabel}</label>
					</div>
					<div class="gu-4">
						<input type="text" name="phone" id="a_phone" class="field" value="${sentFeedback.phone}" />
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="row">
					<div class="gu-4 label-box">
						<label for="ae_name">${sFeedbackNameLabel} <span class="mandatory"> *</span></label>
					</div>
					<div class="gu-4">
						<input type="text" name="name" id="ae_name" class="field validate-empty" />
					</div>
				</div>
				<div class="row">
					<div class="gu-4 label-box">
						<label for="ae_email">${sFeedbackEmailLabel} <span class="mandatory"> *</span></label>
					</div>
					<div class="gu-4">
						<input type="text" name="email" id="ae_email" class="field validate-email"/>
					</div>
				</div>
				<div class="row">
					<div class="gu-4 label-box">
						<label for="ae_phone">${sFeedbackPhoneLabel}</label>
					</div>
					<div class="gu-4">
						<input type="text" name="phone" id="ae_phone" class="field" />
					</div>
				</div>
			</c:otherwise>
		</c:choose>
		<div class="row">
			<div class="gu-4 label-box">
				<label for="feedback">${sFeedbackFeedbackLabel} <span class="mandatory"> *</span></label>
			</div>
			<div class="gu-4">
				<textarea rows="5" cols="50" class="field validate-empty" name="feedback" id="feedback">${not empty sentFeedback ? fn:escapeXml(sentFeedback.feedback) : ''}</textarea>
			</div>
		</div>
		<div class="row row--recaptcha">
			<div class="recaptcha<c:if test="${captchaError}"> error</c:if>">
				<mod:captcha htmlClasses="feedback-form" callback="onCaptchaSubmitFeedbackForm" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4 label-box">
				&nbsp;
			</div>
			<div class="gu-4 field">
				<button type="submit" class="btn btn-primary btn--feedback">${sFeedbackSendButton}<i></i></button>
			</div>
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