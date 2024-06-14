<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/education/register" />

<spring:theme code="formEducationMeetsExperience.option.electrotech" var="sProfileAreaElectrotech" />
<spring:theme code="formEducationMeetsExperience.option.polymech" var="sProfileAreaPolymech" />
<spring:theme code="formEducationMeetsExperience.option.informatics" var="sProfileAreaInformatics" />
<spring:theme code="formEducationMeetsExperience.option.automatics" var="sProfileAreaAutomatics" />
<spring:theme code="formEducationMeetsExperience.option.electroinstallation" var="sProfileAreaElectroinstallation" />
<spring:theme code="formEducationMeetsExperience.option.telematics" var="sProfileAreaTelematics" />

<spring:theme code="formEducationMeetsExperience.fileSizeDeniedMessage" var="sFileSizeDeniedMessage" />
<spring:theme code="formEducationMeetsExperience.fileSizeMessage" var="sFileSizeMessage" />
<spring:theme code="formEducationMeetsExperience.fileTypeDeniedMessage" var="sFileTypeDeniedMessage" />
<spring:theme code="formEducationMeetsExperience.fileTypeMessage" var="sFileTypeMessage" />

<spring:theme code="formEducationMeetsExperience.registration" var="sEducationMeetsExperience" />
<spring:theme code="formEducationMeetsExperience.profile" var="sEducationMeetsExperienceProfile" />
<spring:theme code="formEducationMeetsExperience.institution" var="sEducationMeetsExperienceInstitution" />
<spring:theme code="formEducationMeetsExperience.success" var="sRequestSuccess" />
<spring:theme code="formEducationMeetsExperience.failed" var="sRequestFailed" />
<spring:theme code="formEducationMeetsExperience.buttonText" var="sEducationMeetsExperienceButton" />
<spring:message code="text.store.dateformat.datepicker.selection" var="sDateFormat" />
<c:set var="sCaptchaLabel" value="Captcha" />

<c:if test="${not empty status}">
	<c:if test="${status}"><p class="status status-success padding">${sRequestSuccess}</p></c:if>
	<c:if test="${!status}"><p class="status status-failed padding">${sRequestFailed}</p></c:if>
</c:if>

<form:form action="${action}" method="post" modelAttribute="educationRegistrationForm" enctype="multipart/form-data">
	<div class="education-meets-experience-box base padding">
		<h3 class="base">${sEducationMeetsExperience}</h3>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-profile-area" labelKey="formEducationMeetsExperience.profileArea" path="profileArea" mandatory="false" />
			</div>
			<div class="gu-4">
				<formUtil:formSelectBox idKey="education-meets-experience-profile-area" path="profileArea" mandatory="true" skipBlank="false" 
										selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" 
										items="${profileAreas}" itemValue="code" itemLabel="name" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-contact-lastname" labelKey="formEducationMeetsExperience.contactLastName" path="contactLastName" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-contact-lastname" path="contactLastName" placeHolderKey="formEducationMeetsExperience.contactLastName.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-contact-firstname" labelKey="formEducationMeetsExperience.contactFirstName" path="contactFirstName" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-contact-firstname" path="contactFirstName" placeHolderKey="formEducationMeetsExperience.contactFirstName.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-contact-mobile-number" labelKey="formEducationMeetsExperience.contactMobileNumber" path="contactMobileNumber" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-contact-mobile-number" path="contactMobileNumber" placeHolderKey="formEducationMeetsExperience.contactMobileNumber.placeholder" mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>		
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-contact-phone-number" labelKey="formEducationMeetsExperience.contactPhoneNumber" path="contactPhoneNumber" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-contact-phone-number" path="contactPhoneNumber" placeHolderKey="formEducationMeetsExperience.contactPhoneNumber.placeholder" mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-contact-address-1" labelKey="formEducationMeetsExperience.contactAddress1" path="contactAddress1" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-contact-address-1" path="contactAddress1" placeHolderKey="formEducationMeetsExperience.contactAddress1.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-contact-address-2" labelKey="formEducationMeetsExperience.contactAddress2" path="contactAddress2" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-contact-address-2" path="contactAddress2" placeHolderKey="formEducationMeetsExperience.contactAddress2.placeholder" mandatory="false" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-contact-zip" labelKey="formEducationMeetsExperience.contactZip" path="contactZip" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-contact-zip" path="contactZip" placeHolderKey="formEducationMeetsExperience.contactZip.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-contact-place" labelKey="formEducationMeetsExperience.contactPlace" path="contactPlace" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-contact-place" path="contactPlace" placeHolderKey="formEducationMeetsExperience.contactPlace.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-contact-country" labelKey="formEducationMeetsExperience.contactCountry" path="contactCountry" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formSelectBox idKey="education-meets-experience-contact-country" path="contactCountry" mandatory="true" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${countries}" itemValue="isocode"/>
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-contact-email" labelKey="formEducationMeetsExperience.contactEmail" path="contactEmail" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-contact-email" path="contactEmail" placeHolderKey="formEducationMeetsExperience.contactEmail.placeholder" mandatory="true" inputCSS="${inputCSS} validate-email" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-contact-email-repeat" labelKey="formEducationMeetsExperience.contactEmailRepeat" path="contactEmailRepeat" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-contact-email-repeat" path="contactEmailRepeat" placeHolderKey="formEducationMeetsExperience.contactEmailRepeat.placeholder" mandatory="true" inputCSS="${inputCSS} validate-email-repeat" />
			</div>
		</div>
		<div class="application-info">
			<spring:theme code="formEducationMeetsExperience.applicationInfo" />
			<br/>
			<spring:theme code="formEducationMeetsExperience.applicationInfo2" />
		</div>
		<div class="row">
			<ul>
				<!-- 
				<li class="award">
					<div class="content">
						<h3 class="base upload"><spring:theme code="formEducationMeetsExperience.eTalent2014" /></h3>
						<form:input
							type="file"
							path="eTalent2014File"
							data-file-types="doc,docx,pdf,zip,rar"
							data-max-file-size="20971520"
							data-file-type-denied-message="${sFileTypeDeniedMessage}"
							data-file-type-message="${sFileTypeMessage}"
							data-file-size-denied-message="${sFileSizeDeniedMessage}"
							data-file-size-message="${sFileSizeMessage}"
						/>
						<a href="#" class="btn btn-primary btn-select eTalent2014File-select" type="submit"><spring:theme code="formEducationMeetsExperience.eTalent2014.upload" text="Zeugnis Upload" /></a>
					</div>
				</li>
				-->
				<li class="award" style="float: none;margin: 0px auto">
					<div class="content">
						<h3 class="base upload"><spring:theme code="formEducationMeetsExperience.studyExchange" /></h3>
						<form:input
							type="file" 
							path="studyExchangeFile"
							data-file-types="doc,docx,pdf,zip,rar"
							data-max-file-size="20971520"
							data-file-type-denied-message="${sFileTypeDeniedMessage}"
							data-file-type-message="${sFileTypeMessage}"
							data-file-size-denied-message="${sFileSizeDeniedMessage}"
							data-file-size-message="${sFileSizeMessage}"
						/>
						<a href="#" class="btn btn-primary btn-select studyExchangeFile-select" type="submit"><spring:theme code="formEducationMeetsExperience.studyExchange.upload" text="Zertifikat Upload" /></a>
					</div>
				</li>
				<!-- 
				<li class="award">
					<div class="content">
						<h3 class="base upload"><spring:theme code="formEducationMeetsExperience.motivation" /></h3>
						<form:input
							type="file"
							path="motivationFile"
							data-file-types="doc,docx,pdf,zip,rar"
							data-max-file-size="20971520"
							data-file-type-denied-message="${sFileTypeDeniedMessage}"
							data-file-type-message="${sFileTypeMessage}"
							data-file-size-denied-message="${sFileSizeDeniedMessage}"
							data-file-size-message="${sFileSizeMessage}"
						/>
						<a href="#" class="btn btn-primary btn-select motivationFile-select" type="submit"><spring:theme code="formEducationMeetsExperience.motivation.upload" text="Bewerbungsschreiben Upload" /></a>
					</div>
				</li>
				-->
			</ul>
		</div>
		<div class="filename hidden"></div>
		<div class="errors hidden">
			<p></p>
		</div>
		<h3 class="base topspace">${sEducationMeetsExperienceInstitution}</h3>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-institution-name" labelKey="formEducationMeetsExperience.institutionName" path="institutionName" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-institution-name" path="institutionName" placeHolderKey="formEducationMeetsExperience.institutionName.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-institution-address-1" labelKey="formEducationMeetsExperience.institutionAddress1" path="institutionAddress1" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-institution-address-1" path="institutionAddress1" placeHolderKey="formEducationMeetsExperience.institutionAddress1.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-institution-address-2" labelKey="formEducationMeetsExperience.institutionAddress2" path="institutionAddress2" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-institution-address-2" path="institutionAddress2" placeHolderKey="formEducationMeetsExperience.institutionAddress2.placeholder" mandatory="false" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-institution-zip" labelKey="formEducationMeetsExperience.institutionZip" path="institutionZip" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-institution-zip" path="institutionZip" placeHolderKey="formEducationMeetsExperience.institutionZip.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-institution-place" labelKey="formEducationMeetsExperience.institutionPlace" path="institutionPlace" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-institution-place" path="institutionPlace" placeHolderKey="formEducationMeetsExperience.institutionPlace.placeholder" mandatory="true" inputCSS="${inputCSS} validate-empty" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-institution-country" labelKey="formEducationMeetsExperience.institutionCountry" path="institutionCountry" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formSelectBox idKey="education-meets-experience-institution-country" path="institutionCountry" mandatory="true" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${countries}" itemValue="isocode"/>
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-institution-phone-number" labelKey="formEducationMeetsExperience.institutionPhoneNumber" path="institutionPhoneNumber" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-institution-phone-number" path="institutionPhoneNumber" placeHolderKey="formEducationMeetsExperience.institutionPhoneNumber.placeholder" mandatory="false" inputCSS="${inputCSS}" />
			</div>
		</div>		
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-institution-email" labelKey="formEducationMeetsExperience.institutionEmail" path="institutionEmail" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-institution-email" path="institutionEmail" placeHolderKey="formEducationMeetsExperience.institutionEmail.placeholder" mandatory="true" inputCSS="${inputCSS} validate-email" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="education-meets-experience-institution-email-repeat" labelKey="formEducationMeetsExperience.institutionEmailRepeat" path="institutionEmailRepeat" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="education-meets-experience-institution-email-repeat" path="institutionEmailRepeat" placeHolderKey="formEducationMeetsExperience.institutionEmailRepeat.placeholder" mandatory="true" inputCSS="${inputCSS} validate-email-repeat" />
			</div>
		</div>
		<div class="row recaptcha">
				<div class="gu-4 label-box"></div>
				<div class="gu-4 field">
					<mod:captcha tabindex="4"/>
				</div>
		</div>
		<div class="row row-button">
			<div class="gu-4 label-box">
				&nbsp;
			</div>
			<div class="gu-4 field">
				<button type="submit" name="submit" class="btn btn-primary btn-send">${sEducationMeetsExperienceButton}<i></i></button>
			</div>
		</div>
	</div>
</form:form>

<script id="tmpl-form-education-meets-experience-validation-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>
<script id="tmpl-form-education-meets-experience-validation-error-dropdown" type="text/template">
	<spring:message code="validate.error.dropdown" />
</script>
<script id="tmpl-form-education-meets-experience-validation-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>
<script id="tmpl-form-education-meets-experience-validation-error-email-repeat" type="text/template">
	<spring:message code="validation.checkEmail.equals" />
</script>