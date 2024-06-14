<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:theme code="lightboxshopsettings.title" text="Welcome at Distrelec" var="sTitle" />
<spring:theme code="lightboxshopsettings.introText" text="Hi there, we'd like to set these settings for your" var="sIntroText" />
<spring:theme code="lightboxshopsettings.cookie-info" text="Cookies help us deliver our services. By using this website you agree to our use of cookies." var="sCookieInfo" />
<spring:theme code="lightboxshopsettings.change_settings" text="Change Settings" var="sChangeSettings" />
<spring:theme code="lightboxshopsettings.channel-label" text="Customer" var="sChannelLabel" />
<spring:theme code="lightboxshopsettings.country-label" text="Country" var="sCountryLabel" />
<spring:theme code="lightboxshopsettings.language-label" text="Language" var="sLanguageLabel" />
<spring:theme code="lightboxshopsettings.infotext" text="" var="sInfotext" />
<spring:theme code="lightboxYesNo.cancel" text="Cancel" var="sCancel" />
<spring:theme code="lightboxshopsettings.save" text="Save" var="sSave" />
<spring:theme code="lightboxshopsettings.redirect" text="go to" var="sRedirect" />
<spring:message code="servicenav.SiteSettingsTitle" text="Site Settings Title" var="sSiteSettingsTitle" />

<c:choose>
	<c:when test="${currentChannel.type == 'B2C'}">
		<spring:message code="service.nav.vat.incl" var="inclOrExclTax" text="incl. VAT"/>
	</c:when>
	<c:otherwise>
		<spring:message code="service.nav.vat.excl" var="inclOrExclTax" text="excl. VAT"/>
	</c:otherwise>
</c:choose>
<div class="fsettingsContainer">
	<ul>
		<li class="border red fsettings">
			<a class="settings settings--${currentCountry.isocode}" href="#" data-country-code="${currentCountry.isocode}">
				<i class="fas fa-chevron-down"></i><span class="language">${fn:toUpperCase(currentLanguage.isocode)}</span><span class="seperator">|</span><span>${currentCurrency.isocode}&nbsp;${inclOrExclTax}</span><span class="seperator">|</span><strong class="current-country-name">${currentCountry.name}</strong><span class="flag flag--${currentCountry.isocode}"></span>
			</a>
		</li>
	</ul>
	<div class="formSettingsContainer shopsetings-popup transparent">
		<script id="template-lightboxshopsettings" type="text/x-template-dotjs">
			<section class="flyout-settings">
				<div class="flyout-settings__holder">
					<div class="hd">
						<h3>${sSiteSettingsTitle}</h3>
						<span class="flyout-close">
							<i class="fa fa-times" aria-hidden="true"></i>
						</span>
					</div>
					<form:form action="/_s/shopsettings" method="post">
						<div class="bd">
							<div class="bd__item">
								<label for="select-country">${sCountryLabel}</label>
								<select id="select-country" name="country" ${disabled} class="selectpicker"></select>
							</div>
							<div class="bd__item">
								<label for="select-language">${sLanguageLabel}</label>
								<select id="select-language" name="language" class="selectpicker selectpicker--language"></select>
							</div>
							<div class="bd__item">
								<label for="select-channel">${sChannelLabel}</label>
								<select id="select-channel" name="channel" class="selectpicker selectpicker--channel"></select>
							</div>
						</div>
						<div class="ft">
							<div class="ft-holder">
								<c:choose>
									<c:when test="${isInitialVisit}">
										<p class="infotext">${sCookieInfo}</p>
									</c:when>
									<c:otherwise>
										<p class="infotext">${sInfotext}</p>
									</c:otherwise>
								</c:choose>
							</div>
							<input type="submit" class="btn btn-primary btn-save js-toggle-hook" value="${sSave}" data-label-default="${sSave}" data-label-redirect="${sRedirect}" />
						</div>
					</form:form>
				</div>
			</section>
		</script>

		<script id="template-lightboxshopsettings-option" type="text/x-template-dotjs">
			<option data-icon="icon-{{=it.name}}" value="{{=it.key}}">{{=it.keyLabel}}</option>
		</script>

	</div>
</div>
<script id="template-servicenav-settings" type="text/x-template-dotjs">
	<ul>
		<li class="border red">
			<a class="settings" href="#"><strong>{{=it.countryLabel}}</strong><span class="seperator">/</span>{{=it.language}}<span class="seperator">/</span>{{=it.currencyLabel}}<span class="seperator">/</span>{{=it.channelLabel}}<i></i></a>
		</li>
	</ul>
</script>
