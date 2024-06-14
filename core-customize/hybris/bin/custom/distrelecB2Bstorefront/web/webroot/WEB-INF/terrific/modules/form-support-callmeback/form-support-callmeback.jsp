<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>

<%-- vars for action, labels --%>
<c:set value="support" var="modelAttribute" />

<div class="form-box border-top border-bottom base padding">
	<h3><spring:theme code="support.intro" /></h3>
	<p class="required"><spring:theme code="support.required" /></p>

	<form:form method="post" modelAttribute="${modelAttribute}" action="${action}">
		<%-- title --%>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="support.title" labelKey="support.salutation" path="title" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formSelectBox idKey="support.title" path="title" skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" items="${titles}"/>
			</div>
		</div>

		<%-- firstname --%>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="support.firstname" labelKey="support.firstname" path="firstname" mandatory="false"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="support.firstname" path="firstname" placeHolderKey="support.firstname.placeholder" />
			</div>
		</div>

		<%-- lastname --%>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="support.lastname" labelKey="support.lastname" path="lastname" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="support.lastname" path="lastname" placeHolderKey="support.lastname.placeholder" inputCSS="validate-empty" mandatory="true"/>
			</div>
		</div>

		<%-- email address --%>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="support.email" labelKey="support.email" path="email" mandatory="true" stars="**"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="support.email" path="email" placeHolderKey="support.email.placeholder" />
			</div>
		</div>

		<%-- phone number --%>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="support.phone" labelKey="support.phone" path="phone" mandatory="true" stars="**"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="support.phone" path="phone" placeHolderKey="support.phone.placeholder" />
			</div>
		</div>

		<%-- comment --%>
		<div class="row">
			<div class="gu-4">
				<formUtil:formLabel idKey="support.comment" labelKey="support.comment" path="comment" mandatory="false"/>
			</div>
			<div class="gu-4">
				<textarea rows="5" cols="50" class="field" name="comment" id="support.comment"><c:out value="${support.comment}" /></textarea>
			</div>
		</div>

		<%-- please contact by --%>
		<div class="row row-radio">
			<div class="gu-4">
				<formUtil:formLabel idKey="support.pleaseContactBy" labelKey="support.pleaseContactBy" path="contactBy" mandatory="true"/>
			</div>
			<div class="gu-4">
				<form:radiobutton id="support.contactByEmail" path="contactBy" value="email" cssClass="radio" />
				<formUtil:formLabel idKey="support.contactByEmail" labelKey="support.email" path="contactBy" mandatory="false"/>
				<br />
				<form:radiobutton id="support.contactByPhone" path="contactBy" value="phone" cssClass="radio" />
				<formUtil:formLabel idKey="support.contactByPhone" labelKey="support.phone" path="contactBy" mandatory="false"/>
			</div>
		</div>

		<%-- captcha --%>
		<div class="row">
			<div class="gu-4">
				<label for="captchaAnswer"><spring:theme code="support.captcha" /> *</label>
			</div>
			<div class="gu-4 recaptcha<c:if test="${captchaError}"> error</c:if>">
				<mod:captcha/>
			</div>
		</div>


		<%-- submit --%>
		<div class="row">
			<div class="gu-4">&nbsp;</div>
			<div class="gu-4">
				<div class="field">
					<button class="btn btn-primary" type="submit"><i></i><spring:theme code="support.send" /></button>
				</div>
			</div>
		</div>
	</form:form>

	<script id="tmpl-support-validation-error-dropdown" type="text/template">
		<spring:message code="validate.error.dropdown" />
	</script>

	<script id="tmpl-support-validation-error-empty" type="text/template">
		<spring:message code="validate.error.required" />
	</script>

	<script id="tmpl-support-validation-error-email" type="text/template">
		<spring:message code="validate.error.email" />
	</script>

	<script id="tmpl-support-validation-error-captcha" type="text/template">
		<spring:message code="validate.error.captcha" />
	</script>
</div>