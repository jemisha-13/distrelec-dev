<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ attribute name="modelAttribute" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!-- remove formElement -->
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>


<div class="item_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2><spring:theme code="register.new.customer" /></h2>
	</div>

	<div class="item_container">
	<p><spring:theme code="register.description"/></p>
	<p class="required"><spring:theme code="form.required"/></p>
	<form:form method="post" modelAttribute="${modelAttribute}" action="${action}">
	
			<formElement:formSelectBox idKey="register.title" labelKey="register.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titles}"/>
			<formElement:formInputBox idKey="register.firstName" labelKey="register.firstName" path="firstName" inputCSS="text" mandatory="true"/>
			<formElement:formInputBox idKey="register.lastName" labelKey="register.lastName" path="lastName" inputCSS="text" mandatory="true"/>
			<formElement:formInputBox idKey="register.email" labelKey="register.email" path="email" inputCSS="text" mandatory="true"/>
			<formElement:formPasswordBox idKey="password" labelKey="register.pwd" path="pwd" inputCSS="text password strength" mandatory="true"/>
			<formElement:formPasswordBox idKey="register.checkPwd" labelKey="register.checkPwd" path="checkPwd" inputCSS="text password" mandatory="true" errorPath="${modelAttribute}"/>
			<formElement:formInputBox idKey="register.additionalAddress" labelKey="register.addAddrField" path="additionalAddress" inputCSS="text" mandatory="false"/>
			<formElement:formInputBox idKey="register.streetName" labelKey="register.strName" path="streetName" inputCSS="text" mandatory="true"/>
			<formElement:formInputBox idKey="register.streetNumber" labelKey="register.strNumber" path="streetNumber" inputCSS="text" mandatory="false"/>
			<formElement:formInputBox idKey="register.postalCode" labelKey="register.postalCode" path="postalCode" inputCSS="text" mandatory="true"/>
			<formElement:formInputBox idKey="register.town" labelKey="register.town" path="town" inputCSS="text" mandatory="true"/>
			<formElement:formSelectBox idKey="register.countryCode" labelKey="register.country" path="countryCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${countries}" itemValue="isocode"/>
			<formElement:formInputBox idKey="register.phoneNumber" labelKey="register.phoneNumber" path="phoneNumber" inputCSS="text" mandatory="false"/>
			<formElement:formInputBox idKey="register.mobileNumber" labelKey="register.mobileNumber" path="mobileNumber" inputCSS="text" mandatory="false"/>
			<formElement:formInputBox idKey="register.faxNumber" labelKey="register.faxNumber" path="faxNumber" inputCSS="text" mandatory="false"/>
			<formUtil:formCheckbox idKey="register.termsOfUseOption" labelKey="register.tOfUse" path="termsOfUseOption" inputCSS="checkbox" labelCSS="checkbox" mandatory="true"/>
			<formUtil:formCheckbox idKey="register.dataProtection" labelKey="register.dataPtcn" path="dataProtection" inputCSS="checkbox" mandatory="true"/>
			<formUtil:formCheckbox idKey="register.newsletterOption" labelKey="register.newsletter" path="marketingConsent" inputCSS="checkbox" mandatory="false"/>
		
		<span style="display: block; clear: both;">
		
				<button class="form"><spring:theme code='${actionNameKey}'/></button>
			
		</span>
	
	</form:form>
	</div>
</div>
