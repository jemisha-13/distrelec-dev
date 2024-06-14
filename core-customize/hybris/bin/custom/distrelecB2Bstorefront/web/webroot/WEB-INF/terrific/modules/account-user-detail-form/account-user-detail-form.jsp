<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<c:set var="cssErrorClass" value="" />
<spring:theme code="userdetailform.userDetails" var="sUserDetails" />
<spring:theme code="userdetailform.userDetails.status" var="sStatus" />
<spring:theme code="userdetailform.userDetails.status.active" var="sActive" />
<spring:theme code="userdetailform.userDetails.status.inactive" var="sInactive" />
<spring:theme code="userdetailform.userDetails.status.resendActivationMail" var="sResendActivationMail" />

<spring:theme code="userdetailform.approvedBudget" var="sApprovedBudget" />
<spring:theme code="userdetailform.approvedBudget.residualBudget" var="sResidualBudget" />

<spring:theme code="userdetailform.allowedPermissions" var="sAllowedPermissions" />
<spring:theme code="userdetailform.allowedPermissions.requestQuotations" var="sRequestQuotations" />
<spring:theme code="userdetailform.allowedPermissions.requestQuotationsInfo" var="sRequestQuotationsInfo" />

<spring:theme code="userdetailform.buttonCancel" var="sButtonCancel" />
<spring:theme code="userdetailform.buttonDeactivateUser" var="sButtonDeactivateUser" />
<spring:theme code="userdetailform.buttonDeleteUser" var="sButtonDeleteUser" text="Delete" />
<spring:theme code="userdetailform.buttonSave" var="sButtonSave" />

<spring:theme code="userdetailform.deleteLightbox.title" var="lightboxTitle" text="Please confirm" />
<spring:theme code="userdetailform.deleteLightbox.message" var="lightboxMessage" text="Are you sure to delete this employee?" />
<spring:theme code="userdetailform.deleteLightbox.confirm" var="lightboxConfirmButtonText" text="Yes" />
<spring:theme code="userdetailform.deleteLightbox.cancel" var="lightboxCancelButtonText" text="No" />


<form:form method="post" modelAttribute="employeeRegisterB2BForm" action="${submitActionUrl}">
<%-- Change Name --%>
<div class="form-box">
	<h2 class="form-title">${sUserDetails}</h2>
	<div class="row base">
		<div class="gu-4">
			<formUtil:formLabel idKey="select-title" labelKey="userdetailform.userDetails.title" path="titleCode" mandatory="true" />
		</div>
		<div class="gu-4">
			<formUtil:formSelectBox idKey="select-title" path="titleCode" mandatory="true" skipBlank="false" selectCSSClass="selectpicker" skipBlankMessageKey="form.select.empty" items="${titles}" />
		</div>
	</div>
	<div class="row base">
		<div class="gu-4">
			<formUtil:formLabel idKey="userdetailform.userDetails.firstName" labelKey="userdetailform.userDetails.firstName" path="firstName" mandatory="true" />
		</div>
		<div class="gu-8">
			<formUtil:formInputBox idKey="register.firstName" path="firstName" placeHolderKey="userdetailform.userDetails.firstName.placeholder" maxLength="35" mandatory="true" />
		</div>
	</div>
	<div class="row base">
		<div class="gu-4">
			<formUtil:formLabel idKey="userdetailform.userDetails.lastName" labelKey="userdetailform.userDetails.lastName" path="lastName" mandatory="true" />
		</div>
		<div class="gu-8">
			<formUtil:formInputBox idKey="register.lastName" path="lastName" placeHolderKey="userdetailform.userDetails.lastName.placeholder" maxLength="35" mandatory="true" />
		</div>
	</div>
	<div class="row base">
		<div class="gu-4">
			<formUtil:formLabel idKey="select-department" labelKey="userdetailform.userDetails.department" path="departmentCode" mandatory="false" />
		</div>
		<div class="gu-8">
			<formUtil:formSelectBox idKey="select-department" path="departmentCode" mandatory="false" skipBlank="false" selectCSSClass="selectpicker" skipBlankMessageKey="form.select.empty" items="${departments}" />
		</div>
	</div>
	<div class="row base">
		<div class="gu-4">
			<formUtil:formLabel idKey="select-function" labelKey="userdetailform.userDetails.function" path="functionCode" mandatory="false" />
		</div>
		<div class="gu-8">
			<formUtil:formSelectBox idKey="select-function" path="functionCode" mandatory="false" skipBlank="false" selectCSSClass="selectpicker" skipBlankMessageKey="form.select.empty" items="${functions}" />
		</div>
	</div>
	<c:choose>
		<c:when test="${createNewEmployee}">
			<div class="row base">
				<div class="gu-4">
					<formUtil:formLabel idKey="userdetailform.userDetails.login" labelKey="userdetailform.userDetails.loginEmail" path="email" mandatory="true" />
				</div>
				<div class="gu-8">
					<formUtil:formInputBox idKey="register.email" path="email" placeHolderKey="userdetailform.userDetails.eMail.placeholder" maxLength="241" mandatory="true" />
				</div>
			</div>
		</c:when>
		<c:when test="${not empty employeeUid and employeeUid ne employeeRegisterB2BForm.email}">
			<div class="row base">
				<div class="gu-4">
					<formUtil:formLabel idKey="userdetailform.userDetails.login" labelKey="userdetailform.userDetails.login" path="login" mandatory="true" />
				</div>
				<div class="gu-8">
					<p>${employeeUid}</p>
				</div>
			</div>
				<div class="row base">
			<div class="gu-4">
					<formUtil:formLabel idKey="userdetailform.userDetails.eMail" labelKey="userdetailform.userDetails.eMail" path="email" mandatory="true" />
				</div>
				<div class="gu-8">
 					<formUtil:formInputBox idKey="register.email" path="email" placeHolderKey="userdetailform.userDetails.eMail.placeholder" maxLength="241" mandatory="true" />
				</div>
			</div>	
		</c:when>
		<c:otherwise>
			<div class="row base">
				<div class="gu-4">
					<formUtil:formLabel idKey="userdetailform.userDetails.login" labelKey="userdetailform.userDetails.loginEmail" path="loginEmail" mandatory="true" />
				</div>
				<div class="gu-8">
					<formUtil:formInputBox idKey="register.email" path="email" placeHolderKey="userdetailform.userDetails.eMail.placeholder" maxLength="241" mandatory="true" />
				</div>
			</div>
		</c:otherwise>
	</c:choose>
	<div class="row base">
		<div class="gu-4">
			<formUtil:formLabel idKey="userdetailform.userDetails.phone" labelKey="userdetailform.userDetails.phone" path="phoneNumber" mandatory="true" stars="**" />
		</div>
		<div class="gu-8">
			<formUtil:formInputBox idKey="register.phoneNumber" path="phoneNumber" placeHolderKey="userdetailform.userDetails.phone.placeholder" maxLength="30" mandatory="false" />
		</div>
	</div>
	<div class="row base">
		<div class="gu-4">
			<formUtil:formLabel idKey="userdetailform.userDetails.mobilePhone" labelKey="userdetailform.userDetails.mobilePhone" path="mobileNumber" mandatory="true" stars="**" />
		</div>
		<div class="gu-8">
			<formUtil:formInputBox idKey="register.mobileNumber" path="mobileNumber" placeHolderKey="userdetailform.userDetails.mobilePhone.placeholder" maxLength="30" mandatory="false" />
		</div>
	</div>
	<div class="row base">
		<div class="gu-4">
			<formUtil:formLabel idKey="userdetailform.userDetails.fax" labelKey="userdetailform.userDetails.fax" path="faxNumber" mandatory="false" stars="" />
		</div>
		<div class="gu-8">
			<formUtil:formInputBox idKey="register.faxNumber" path="faxNumber" placeHolderKey="userdetailform.userDetails.fax.placeholder" maxLength="30" mandatory="false" />
		</div>
	</div>
	<c:if test="${not createNewEmployee}">
	<spring:theme code="userdetailform.userDetails" var="sUserDetails" />
		<div class="row base">
			<div class="gu-4">${sStatus}</div>
			<div class="gu-8">
				<c:if test="${employeeStatus}">${sActive}</br></c:if>
				<c:if test="${not employeeStatus}">${sInactive}</br></c:if>
				<a href="/my-account/company/resend/activation/${employeeCustomerId}">${sResendActivationMail}</a>
			</div>
		</div>
	</c:if>
</div>

<%-- Approved Budget --%>
<div class="form-box form-approved-budget">
	<div class="row">
		<div class="gu-4">
			<h2 class="form-title">${sApprovedBudget}*</h2>
		</div>
		<div class="gu-8 base">
			<formUtil:formCheckbox idKey="register.budgetWithoutLimit" labelKey="userdetailform.approvedBudget.withoutBudgetLimit" path="budgetWithoutLimit" inputCSS="b-limit" />
		</div>
	</div>
	<div class="row base">
		<div class="gu-4">
			<formUtil:formLabel idKey="userdetailform.approvedBudget.perOrder" labelKey="userdetailform.approvedBudget.perOrder" path="budgetPerOrder" mandatory="false" />
		</div>
		<div class="gu-1">
			<span class="currency">${budgetCurrency}</span>
		</div>
		<div class="gu-7">
			<formUtil:formInputBox idKey="register.budgetPerOrder" path="budgetPerOrder" placeHolderKey="userdetailform.approvedBudget.perOrder.placeholder" mandatory="false" inputCSS="b-toggle" />
		</div>
	</div>
	<div class="row base">
		<div class="gu-4">
			<formUtil:formLabel idKey="userdetailform.approvedBudget.yearly" labelKey="userdetailform.approvedBudget.yearly" path="yearlyBudget" mandatory="false" />
		</div>
		<div class="gu-1">
			<span class="currency">${budgetCurrency}</span>
		</div>
		<div class="gu-7">
			<formUtil:formInputBox idKey="register.yearlyBudget" path="yearlyBudget" placeHolderKey="userdetailform.approvedBudget.yearly.placeholder" mandatory="false" inputCSS="b-toggle" />
		</div>
	</div>
	<c:if test="${not empty residualBudget}">
		<div class="row base">
			<div class="gu-4">
				<label class="required">${sResidualBudget}</label>
			</div>
			<div class="gu-8">
				<p class="value${residualBudget < 0 ? ' neg' : ''}"><format:price format="defaultSplit" displayValue="${residualBudget}" fallBackCurrency="${budgetCurrency}" /></p>
			</div>
		</div>
	</c:if>
</div>
<div class="form-box form-allowed-permissions">
	<h2 class="form-title">${sAllowedPermissions}*</h2>
	<div class="row base">
		<div class="gu-4">
			<label class="required">${sRequestQuotations}</label>
		</div>
		<div class="gu-8">
			<formUtil:formRadioButton idKey="request_qutations-yes" name="requestQuotationPermission" path="requestQuotationPermission" inputCSS="radio" value="1" labelKey="userdetailform.yes" />
			<formUtil:formRadioButton idKey="request_qutations-no-yes" name="requestQuotationPermission" path="requestQuotationPermission" inputCSS="radio" value="0" labelKey="userdetailform.no" />
			<p>${sRequestQuotationsInfo}</p>
		</div>
	</div>
</div>
<div class="form-box">
	<div class="row">
		<div class="gu-6">
			<a href="${cancelUrl}" class="btn btn-secondary btn-cancel"><i></i>${sButtonCancel}</a>
			<c:if test="${not createNewEmployee}">
				<c:if test="${employeeStatus}">
					<a href="${deactivateUserUrl}" class="btn btn-secondary btn-deactivate-user">${sButtonDeactivateUser}</a>
				</c:if>
				<a href="#" class="btn btn-secondary btn-delete-user"
					data-action="${deleteUserUrl}"
					data-customer-id="${employeeCustomerId}"
					data-lightbox-title="${lightboxTitle}"
					data-lightbox-message="${lightboxMessage}"
					data-lightbox-btn-cancel="${lightboxCancelButtonText}"
					data-lightbox-show-confirm-button="true"
					data-lightbox-btn-conf="${lightboxConfirmButtonText}"
				>${sButtonDeleteUser}</a>
			</c:if>
		</div>
		<div class="gu-6">
			<button type="submit" class="btn-primary btn-save"><i></i>${sButtonSave}</button>
		</div>
	</div>
</div>
</form:form>

