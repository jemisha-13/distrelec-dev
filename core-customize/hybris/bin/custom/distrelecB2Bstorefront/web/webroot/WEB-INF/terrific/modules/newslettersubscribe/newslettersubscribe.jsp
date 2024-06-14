<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:theme code="text.preferences.newsletter.signup" text="" var="sTeaser" />
<spring:theme code="text.preferences.newsletter.email" text="" var="sPlaceholder" />
<spring:theme code="newslettersubscribe.subscribe" text="" var="sSubscribe" />
<spring:theme code="text.preferences.newsletter.updates" text="" var="sLabelText" />

<div class="row">

	<div class="col-md-10 col-lg-6 newsletter-content">
		<div class="newsletter-content__holder">
			<h3 id="footerNewsletterHeaderTeaser" class="newsletter-teaser">${sTeaser}</h3>
			<form:form>
				<div class="row">
					<div id="footerNewsletterInfoMessage" class="info-message js-hide-on-submit"><spring:message code="text.preferences.newsletter.updates"/></div>
					<div class="col-md-8">
						<label for="newsletterFooterEmail" class="hidden">${sLabelText}</label>
						<input required type="email" name="email" id="newsletterFooterEmail" maxlength="255" placeholder="${sPlaceholder}" class="js-newsletterSubscribeFooterInput js-hide-on-submit base input-newsletter html-validate-email col-12">
					</div>
					<div class="col-md-4">
						<button data-aainteraction="newsletter subscribe" name="${sSubscribe}" type="button" class="btn-signup col-12 js-hide-on-submit"><i class="fas fa-user"></i>${sSubscribe}</button>
					</div>
					<span id="footerNewsletterSuccessMessage" class="js-newsletterFooterSuccessMessage success-message hidden">
						<spring:message code="subscribe.popup.thankyou"/>
					</span>
				</div>
			</form:form>
		</div>
	</div>

</div>


<script id="tmpl-newslettersubscribe-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>
<mod:captcha htmlClasses="js-footerNewsletterRecaptcha js-hide-on-submit"/>
