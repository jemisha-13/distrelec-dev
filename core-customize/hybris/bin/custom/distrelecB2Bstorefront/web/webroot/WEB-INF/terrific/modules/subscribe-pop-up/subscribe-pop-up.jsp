<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:theme code="newslettersubscribe.teaser" text="" var="sTeaser" />
<spring:theme code="newslettersubscribe.placeholder" text="" var="sPlaceholder" />
<spring:theme code="newslettersubscribe.subscribe" text="" var="sSubscribe" />
<spring:theme code="subscribe.popup.title" text="" var="subscribeTitle" />
<spring:theme code="subscribe.popup.info.notice" text="" var="subscribeInfoNotice" />
<spring:theme code="text.preferences.newsletter.disclaimer" text="" var="subscribeInfoUpdate" />
<spring:theme code="subscribe.popup.thankyou" text="" var="subscribeThankYou" />
<spring:theme code="text.preferences.newsletter.email" text="" var="subscribeEmailAddress" />
<spring:theme code="text.preferences.data" text="" var="yourDataYourChoice" />
<spring:theme code="subscribe.popup.privacy.policy" text="" var="privacyPolicy" />
<spring:theme code="text.preferences.newsletter.email" text="" var="sLabelText" />

<div class="subscribe-pop-up hidden">
	<div class="subscribe-pop-up__header">
		<div class="subscribe-pop-up__title">
			<i class="far fa-envelope"></i>
			<h2 id="popupSubInfoTitle">${subscribeTitle}</h2>
		</div>
		<button type="button" class="btn-close-signup" aria-label="Close" data-delay-cookie-expire=${subscribePopupDelay}>
			<i class="far fa-times-circle"></i>
		</button>
	</div>
	<div class="subscribe-pop-up__info">
		<p id="popupSubInfoNotice" class="p_bold">${subscribeInfoNotice}</p>
		<p id="popupSubInfoUpdate" >${subscribeInfoUpdate}</p>
	</div>
	<form:form>
		<div class="row subscribe-pop-up__form">
			<div class="input">
				<label for="email" class="hidden">${sLabelText}</label>
				<input required value="${user.email}" type="email" name="email" id="email" maxlength="255" placeholder="${subscribeEmailAddress}" class="js-emailInput base input-subscribe html-validate-email" >
			</div>
			<div class="submit">
				<button data-aainteraction="newsletter subscribe" name="${sSubscribe}" type="button" class="btn-signup" data-show-cookie-expire=${popupShownDelay}>${sSubscribe}</button>
			</div>
			<span id="subscribe.popup.thankyou" class="js-newsletterPopupSuccessMessage success-message hidden">
				<spring:message code="subscribe.popup.thankyou"/>
			</span>
		</div>
	</form:form>
	<p id="popupSubDataChoice" class="p_bold">${yourDataYourChoice}</p>
	<p id="popupSubPrivacyPolicy">${privacyPolicy}</p>
</div>
<div class="overlay-subscribe"></div>

<script id="tmpl-subscribe-popup-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>
<mod:captcha htmlClasses="js-popupNewsletterRecaptcha"/>
