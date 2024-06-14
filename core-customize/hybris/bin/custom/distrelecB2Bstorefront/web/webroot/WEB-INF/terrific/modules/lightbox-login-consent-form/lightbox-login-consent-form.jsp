<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<div class="modal js-preferenceConsentModal" id="modalLog" tabindex="-1" style="display:block" data-is-de="${currentCountry.isocode eq 'DE' ? 'true' : 'false'}">
	<section class="padded-content">
		<h3><spring:message code="text.login.interrupt"/></h3>
		<p><spring:message code="text.login.${user.registeredAsGuest ? 'guest' : 'colleague'}"/></p>
		<p><spring:message code="text.login.dont.worry"/></p>
	</section>

	<form:form class="consent-form" action="/preference-center" modelAttribute="marketingProfileData" method="PUT">
		<section class="section-form">
			<c:url value="/data-protection/cms/datenschutz" var="privacyLink"/>
			<c:url value="/general-terms-and-conditions/cms/agb?marketingPopup=false" var="termsLink"/>
			<div class="half-container">
				<div><formUtil:formCheckbox idKey="privacyPolicyPopup" labelKey="text.registration.accept.privacy" labelArguments="${termsLink},${privacyLink}" path="termsAndConditionsConsent" inputCSS="checkbox js-checkbox js-termsCheckbox" mandatory="false"/></div>
				<div class="description-text"><spring:message code="text.registration.accept.registering"/></div>
			</div>

			<div class="half-container">
				<div><formUtil:formCheckbox idKey="text.preferences.email.popup" labelKey="text.preferences.receive.communication" path="emailConsent" inputCSS="checkbox js-checkbox js-emailCheckbox" mandatory="false"/></div>
				<div id="text.preferences.newsletter.disclaimer.popup" class="description-text"><spring:message code="text.preferences.newsletter.disclaimer"/></div>
			</div>

			<div class="half-container">
                <div>
					<ul>
						<li><formUtil:formCheckbox idKey="text.preferences.sms.popup" labelKey="text.preferences.sms" path="smsConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></li>
						<li><formUtil:formCheckbox idKey="text.preferences.phone.popup" labelKey="text.preferences.phone" path="phoneConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></li>
						<li><formUtil:formCheckbox idKey="text.preferences.post.popup" labelKey="text.preferences.post" path="postConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></li>
					</ul>
				</div>
				<div id="text.preferences.sms.disclaimer.popup" class="description-text"><spring:message code="text.preferences.sms.disclaimer"/></div>
			</div>

			<div class="half-container">
				<div><formUtil:formCheckbox idKey="personalise-communication-popup" labelKey="text.preferences.receive.personalised" path="personalisationConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></div>
				<div id="text.preferences.personalise.content.legal.popup" class="description-text"><spring:message code="text.preferences.personalise.content.legal"/></div>
			</div>
			<div class="half-container">
				<div><formUtil:formCheckbox idKey="profling-communication-popup" labelKey="text.preferences.combine.data" path="profilingConsent" inputCSS="checkbox js-checkbox" mandatory="false"/></div>
				<c:url value="/information-notice/cms/datenschutz#thirdPartyCookies" var="thirdPartyCookiesLink"/>
				<div id="text.preferences.profiling.data.legal.popup" class="description-text"><spring:message code="text.preferences.profiling.data.legal" arguments="${thirdPartyCookiesLink}"/></div>
			</div>

			<p id="text.preferences.newsletter.disclaimer.after.register.modal"><spring:message code="text.preferences.newsletter.disclaimer.after.register"/></p>

			<button id="popup-complete-account-button" data-aainteraction="complete account setup" class="btn btn-primary js-submitConsentForm" type="button"><spring:message code="text.login.complete.account"/></button>
			<a id="popup-logout-button" data-aainteraction="cancel account setup" class="btn btn-secondary" href="/logout"><spring:message code="text.preferences.cancel"/></a>
		</section>
	</form:form>

	<section class="padded-content">
		<c:url value="/cms/contact" var="contactLink"/>
		<h2 id="text.preferences.data.popup"><spring:message code="text.preferences.data"/></h2>
		<p id="text.preferences.privacy.policy.popup"><spring:message code="text.preferences.privacy.policy"/></p>
		<p id="text.preferences.privacy.policy.2.popup"><spring:message code="text.preferences.privacy.policy.2" arguments="${contactLink}"/></p>
		<p id="text.preferences.question.popup"><spring:message code="text.preferences.question"/></p>
		<h4 id="text.registration.control.popup"><spring:message code="text.registration.control"/></h4>
		<p id="text.registration.change.communication.popup"><spring:message code="text.registration.change.communication"/></p>
	</section>
</div>
