<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="sContactLink" value="/cms/contact" />

<spring:theme var="prefSuccessMessage" code="text.preferences.saved" />
<spring:theme var="prefErrorMessage" code="text.preferences.error" />
<spring:theme var="communicationError" code="text.preference.communication.error" arguments="${sContactLink}"/>

<mod:global-messages template="component" skin="component success hidden"  headline='' body='${prefSuccessMessage}' type="success"/>
<mod:global-messages template="component" skin="component error hidden"  headline='' body='${prefErrorMessage}' type="error"/>
<mod:lightbox-marketing-email-example />
<c:if test="${marketingProfileData.status == 'ERROR_FETCHING_DETAILS'}">
	<mod:global-messages template="component" skin="component error"  headline='' body='${communicationError}' type="error"/>
</c:if>

<h1 id="text.preferences.communication" class="base page-title"><spring:message code="text.preferences.communication"/></h1>
<h2 id="text.preferences.data"><spring:message code="text.preferences.data"/></h2>
<p id="text.preferences.privacy.policy" class="consent-section__paragraph"><spring:message code="text.preferences.privacy.policy"/></p>
<p id="text.preferences.question" class="consent-section__paragraph"><spring:message code="text.preferences.question"/></p>

<form:form class="consent-form js-consentForm" action="/preference-center" modelAttribute="marketingProfileData" method="PUT">

	<section class="consent-section js-consentSection" data-show-doubleoptin-message="${currentCountry.isocode eq 'DE' ? 'true' : 'false'}">
		<h4 id="text.preferences.your.communication" class="consent-section__title"><spring:message code="text.preferences.your.communication"/></h4>
		<p id="text.preferences.up.to.date" class="consent-section__description"><spring:message code="text.preferences.newsletter.disclaimer"/></p>
		<ul class="consent-section__checkbox-group form-wrapper">
			<li class="consent-section__checkbox-main"><formUtil:formCheckbox idKey="communication-email" labelKey="text.preferences.receive.communication" path="emailConsent" inputCSS="checkbox js-checkbox js-parentCheckbox js-emailCheckbox" mandatory="false"/></li>
			<li class="consent-section__checkbox-main js-parentCheckboxItem js-emailConsents">
				<input class="checkbox js-checkbox js-parentCheckbox" id="text.preferences.following.newsletter.receive" type="checkbox" name="newsletter">
				<label id="text.preferences.following.newsletter.receive.label" for="text.preferences.following.newsletter.receive"><spring:message code="text.preferences.following.newsletter.receive"/></label>
				<ul class="consent-section__checkbox-group js-childrenCheckboxes js-levelOne">
					<li class="consent-section__notice">
						<p id="text.preferences.optIn.notice"><spring:message code="text.preferences.optIn.notice"/></p>
					</li>
					<spring:message code="text.preferences.email.newsletter.example" var="newsletterEmailExample" />
					<li class="js-listItem view-example-group" data-img-src="/_ui/all/media/img/newsletterEmailExamples/${newsletterEmailExample}">
						<span>
							<formUtil:formCheckbox idKey="newsletter" labelKey="text.preferences.newsletter.offer" path="newsLetterConsent" inputCSS="checkbox js-checkbox js-checkbox js-newsletterConsent" mandatory="false" />
						</span>
						<a class="view-example-link" href="#" tabindex="0"><spring:message code="text.preferences.view.example"/></a>
					</li>
					<spring:message code="text.preferences.email.sales.example" var="salesEmailExample" />
					<li class="js-listItem view-example-group" data-img-src="/_ui/all/media/img/salesEmailExamples/${salesEmailExample}">
						<span>
							<formUtil:formCheckbox idKey="sales-clearance" labelKey="text.preferences.sales.clearance" path="saleAndClearanceConsent" inputCSS="checkbox js-checkbox" mandatory="false" />
						</span>
						<a class="view-example-link" href="#" tabindex="0"><spring:message code="text.preferences.view.example"/></a>
					</li>
					<spring:message code="text.preferences.email.knowHow.example" var="knowHowEmailExample" />
					<li class="js-listItem view-example-group" data-img-src="/_ui/all/media/img/knowHowEmailExamples/${knowHowEmailExample}">
						<span>
							<formUtil:formCheckbox idKey="know-how" labelKey="text.preferences.know.how" path="knowHowConsent" inputCSS="checkbox js-checkbox" mandatory="false" />
						</span>
						<a class="view-example-link" href="#" tabindex="0"><spring:message code="text.preferences.view.example"/></a>
					</li>
					<li class="js-listItem">
						<span>
							<formUtil:formCheckbox idKey="personalisedRecommendationConsent" labelKey="text.preferences.recommendations" path="personalisedRecommendationConsent" inputCSS="checkbox js-checkbox" mandatory="false" />
						</span>
					</li>
					<li class="js-listItem">
						<span>
							<formUtil:formCheckbox idKey="customerSurveysConsent" labelKey="text.preferences.feedback" path="customerSurveysConsent" inputCSS="checkbox js-checkbox" mandatory="false" />
						</span>
					</li>

					<c:if test="${currentCountry.isocode ne 'DE'}">
						<li class="js-parentCheckboxItem js-listItem">
							<c:set var="notEmpyObs" value="false"/>
							<c:forEach items="${categories}" var="obsolCategory">
								<c:if test="${obsolCategory.isObsolCategorySelected eq 'true'}">
									<c:set var="notEmpyObs" value="${true}" />
								</c:if>
							</c:forEach>
							<input id="obsolence" name="obsolescenceConsent" class="checkbox js-checkbox js-parentCheckbox" type="checkbox" ${notEmpyObs ? "checked" : ""} >
							<label for="obsolence" >
								<spring:message code="text.preferences.adhoc"/>
							</label>

							<ul class="consent-section__checkbox-group split-50 js-childrenCheckboxes js-levelTwo">
								<div class="connector"></div>
								<c:forEach items="${categories}" var="obsolCategory">
									<c:choose>
										<c:when test="${allCatSelected eq 'true'}">
											<c:set var="categoryOptedValue" value="checked" />
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${obsolCategory.isObsolCategorySelected eq 'true'}">
													<c:set var="categoryOptedValue" value="checked" />
												</c:when>
												<c:otherwise>
													<c:set var="categoryOptedValue" value="" />
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
									<li>
										<input class="checkbox js-checkbox js-obs" name="${obsolCategory.category.code}" id="${obsolCategory.category.code}" type="checkbox" data-category="${obsolCategory.category.nameEN}" value="${obsolCategory.isObsolCategorySelected}" ${categoryOptedValue} >
										<label for="${obsolCategory.category.code}" >${obsolCategory.category.name}</label>
									</li>
								</c:forEach>

							</ul>
						</li>
					</c:if>
				</ul>
			</li>
		</ul>

		<p class="consent-section__description"><spring:message code="text.preferences.sms.disclaimer"/></p>
		<ul class="consent-section__checkbox-group form-wrapper">
			<li class="consent-section__checkbox-main"><formUtil:formCheckbox idKey="text.preferences.sms" labelKey="text.preferences.sms" path="smsConsent" inputCSS="checkbox js-checkbox" mandatory="true"/></li>
			<li class="consent-section__checkbox-main"><formUtil:formCheckbox idKey="text.preferences.phone" labelKey="text.preferences.phone" path="phoneConsent" inputCSS="checkbox js-checkbox" mandatory="true"/></li>
			<li class="consent-section__checkbox-main"><formUtil:formCheckbox idKey="text.preferences.post" labelKey="text.preferences.post" path="postConsent" inputCSS="checkbox js-checkbox" mandatory="true"/></li>

			<li class="disclaimer-container">
				<c:url value="/my-account/my-account-information" var="myAccountLink"/>
				<p id="text.preferences.disclaimer" class="accent-paragraph">
					*<spring:message code="text.preferences.disclaimer" arguments="${myAccountLink}"/>
				</p>
			</li>
		</ul>
	</section>

	<section class="consent-section">
		<h4 id="text.preferences.personalisation" class="consent-section__title"><spring:message code="text.preferences.personalisation"/></h4>
		<p id="text.preferences.personalise.content.legal" class="consent-section__description"><spring:message code="text.preferences.personalise.content.legal"/></p>
		<div class="consent-section__checkbox-group form-wrapper">
			<formUtil:formCheckbox idKey="personalise-communication" labelKey="text.preferences.receive.personalised" path="personalisationConsent" inputCSS="checkbox js-checkbox" mandatory="false"/>
		</div>
	</section>

	<section class="consent-section">
		<h4 id="text.preferences.profiling" class="consent-section__title"><spring:message code="text.preferences.profiling"/></h4>
		<c:url value="/data-protection/cms/datenschutz#CookieListing" var="privacyLink"/>
		<p id="text.preferences.profiling.data.legal" class="consent-section__description"><spring:message code="text.preferences.profiling.data.legal" arguments="${privacyLink}"/></p>
		<div class="consent-section__checkbox-group form-wrapper">
			<formUtil:formCheckbox idKey="profling-communication" labelKey="text.preferences.combine.data" path="profilingConsent" inputCSS="checkbox js-checkbox" mandatory="false"/>
		</div>
	</section>

	<div class="consent-section__buttons">
		<button type="button" class="btn btn-primary js-submitConsentForm" ${marketingProfileData.status == 'ERROR_FETCHING_DETAILS' ? 'disabled' : ''} data-aainteraction="save preferences"><spring:theme code="text.preferences.save" /></button>
		<button type="button" class="btn btn-secondary js-cancelConsentForm" ><spring:message code="text.preferences.cancel" /></button>
	</div>
</form:form>

<c:if test="${not empty email}">
	<script id="unsubBar" type="text/javascript">
		var wally = ${email};
	</script>
</c:if>
