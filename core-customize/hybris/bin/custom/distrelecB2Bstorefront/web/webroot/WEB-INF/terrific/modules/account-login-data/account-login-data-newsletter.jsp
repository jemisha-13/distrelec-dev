<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="action" value="/login-data/send" />

<c:if test="${updateNewsletterForm.marketingConsent && !doubleOptinActivated}">
	<c:set var="disableOption" value="disabled" />
</c:if>
<c:if test="${!updateNewsletterForm.marketingConsent}">
	<c:set var="newsletterOptionsClass" value="hidden" />
</c:if>

<spring:theme var="readUpdateError" code="text.account.consent.read.update.error"  />
<spring:theme var="doubleOptInHeader" code="text.account.consent.doubleoptin.header"  />
<spring:theme var="doubleOptInBody" code="text.account.consent.doubleoptin.body"  />

<div id="preferences" class="form-box newsletter">
	<form:form action="update-newsletter" modelAttribute="updateNewsletterForm" method="post">
		<strong><spring:message code="text.marketing.communication" /></strong>

		<div class="row base newsletter-want">
			<div>
				<p><spring:theme code="logindata.Newsletter.iWant" /></p>
			</div>

			<div class="gu-2">
				<formUtil:formCheckbox idKey="register.statisticsConsent" labelKey="register.statisticsConsent" path="marketingConsent" inputCSS="checkbox" mandatory="false"/>
			</div>
			<div class="gu-2">
				<formUtil:formCheckbox idKey="register.phoneMarketingOption" labelKey="register.phoneMarketingOption" path="subscribePhoneMarketing" inputCSS="checkbox" mandatory="false"/>
			</div>


			<div class="gu-8">

				<c:if test="${consentStatus eq 'error'}">
					<mod:global-messages template="component" skin="component"  headline='' body='${readUpdateError}' type="error"/>
				</c:if>

				<c:if test="${consentStatus eq 'info'}">
					<mod:global-messages template="component" skin="component"  headline='${doubleOptInHeader}' body='${doubleOptInBody}' type="info"/>
				</c:if>

			</div>

			<div class="gu-8">
				<h2 class="form-title"><spring:theme code="text.personalise.your.experience" /></h2>
				<p><spring:theme code="logindata.Newsletter.privacyInfo" /></p>
			</div>
		</div>
		<div class="newsletter-options ${newsletterOptionsClass}">
			<div class="row base">
				<div class="gu-4">
					<formUtil:formLabel idKey="newsletter.industry" labelKey="newsletter.industry" path="division" mandatory="true"/>
				</div>
				<div class="gu-4 industry">
					<formUtil:formSelectBox idKey="newsletter.industryCode" path="division" mandatory="false" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${divisions}" selectedValue="${updateNewsletterForm.division}" />
				</div>
			</div>

				<%-- newsletter-selection --%>
			<div class="row base">
				<div class="gu-4">
					<formUtil:formLabel idKey="newsletter.topic" labelKey="newsletter.topic" path="role" mandatory="true"/>
				</div>
				<div class="gu-4">
					<formUtil:formSelectBox idKey="newsletter.role" path="role" mandatory="true" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${roles}" selectedValue="${updateNewsletterForm.role}"/>
				</div>
			</div>

		</div>

		<div class="row">
			<div class="gu-4 label-box">&nbsp;</div>
			<div class="gu-4 field">
				<button class="btn btn-primary btn-change" type="submit"><i></i><spring:theme code="logindata.buttonChange" /></button>
			</div>
		</div>
	</form:form>
</div>

<c:if test="${not empty email}">
	<script id="unsubBar" type="text/javascript">
		var wally = ${email};
	</script>
</c:if>

<script id="tmpl-newsletter-validation-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>
<script id="tmpl-newsletter-validation-error-checkboxgroup" type="text/template">
	<spring:message code="validate.error.checkboxgroup" />
</script>
