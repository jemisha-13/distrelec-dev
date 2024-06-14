<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/login-data/send" />
<spring:theme code="logindata.changeName" var="sChangeName" />
<spring:theme code="logindata.buttonChange" var="sButtonChange" />

<c:choose>
	<c:when test="${isMigratedUser}">
		<c:set value="updateMigratedProfileForm" var="formModelAttribute"/>
		<c:set value="/my-account/migrated/update-profile" var="formAction"/>
	</c:when>
	<c:otherwise>
		<c:set value="updateProfileForm" var="formModelAttribute"/>
		<c:set value="/my-account/update-profile" var="formAction"/>
	</c:otherwise>
</c:choose>

<!-- Change Name !-->
<div class="form-box">
	<form:form action="${formAction}" modelAttribute="${formModelAttribute}" method="post">
		<h2 class="form-title">${sChangeName}</h2>
		<div class="row base">
			<div class="gu-4">
				<formUtil:formLabel idKey="select-title" labelKey="logindata.changeName.title" path="titleCode" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formSelectBox idKey="select-title" path="titleCode" mandatory="true" skipBlank="true" items="${titleData}" selectCSSClass="selectpicker field" />
			</div>
		</div>
		<div class="row base">
			<div class="gu-4">
				<formUtil:formLabel idKey="firstName" labelKey="logindata.changeName.firstName" path="firstName" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="firstName" path="firstName" placeHolderKey="logindata.changeName.firstName.placeholder" mandatory="true" />
			</div>
		</div>
		<div class="row base">
			<div class="gu-4">
				<formUtil:formLabel idKey="lastName" labelKey="logindata.changeName.lastName" path="lastName" mandatory="true"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="lastName" path="lastName" placeHolderKey="logindata.changeName.lastName.placeholder" mandatory="true" />
			</div>
		</div>
		<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP')">
			<div class="row base">
				<div class="gu-4">
					<formUtil:formLabel idKey="select-department" labelKey="logindata.changeName.department" path="departmentCode" mandatory="false"/>
				</div>
				<div class="gu-4">
					<formUtil:formSelectBox idKey="select-department" path="departmentCode" mandatory="false" skipBlank="false" items="${departmentData}" selectCSSClass="selectpicker field" skipBlankMessageKey="form.select.empty" />
				</div>
			</div>
			<div class="row base">
				<div class="gu-4">
					<formUtil:formLabel idKey="select-function" labelKey="logindata.changeName.function" path="functionCode" mandatory="false"  />
				</div>
				<div class="gu-4">
					<formUtil:formSelectBox idKey="select-function" path="functionCode" mandatory="false" skipBlank="false" items="${functionData}" selectCSSClass="selectpicker field" skipBlankMessageKey="form.select.empty"/>
				</div>
			</div>
		</sec:authorize>
		<c:if test="${isMigratedUser}">
			<div class="row base">
				<div class="gu-4">
					<formUtil:formLabel idKey="migratedUserEmail" labelKey="logindata.changeName.migratedUser.emailAddress" path="email" mandatory="true" />
				</div>
				<div class="gu-4">
					<formUtil:formInputBox idKey="migratedUserEmail" path="email" placeHolderKey="logindata.changeName.emailAddress.placeholder" />
				</div>
			</div>
		</c:if>
		<div class="row base">
			<div class="gu-4">
				<formUtil:formLabel idKey="phone" labelKey="logindata.changeName.phone" path="phone" mandatory="true" stars="**" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="phone" path="phone" mandatory="false" placeHolderKey="logindata.changeName.phone.placeholder" />
			</div>
		</div>
		<div class="row base">
			<div class="gu-4">
				<formUtil:formLabel idKey="mobilePhone" labelKey="logindata.changeName.mobilePhone" path="mobilePhone" mandatory="true" stars="**"/>
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="mobilePhone" path="mobilePhone" mandatory="false" placeHolderKey="logindata.changeName.mobilePhone.placeholder" />
			</div>
		</div>
		<div class="row base">
			<div class="gu-4">
				<formUtil:formLabel idKey="fax" labelKey="logindata.changeName.fax" path="fax" mandatory="false" stars="" />
			</div>
			<div class="gu-4">
				<formUtil:formInputBox idKey="fax" path="fax" mandatory="false" placeHolderKey="logindata.changeName.fax.placeholder" />
			</div>
		</div>
		<div class="row">
			<div class="gu-4 label-box">&nbsp;</div>
			<div class="gu-4 field">
				<button class="btn btn-primary btn-change" type="submit"><i></i>${sButtonChange}</button>
			</div>
		</div>
	</form:form>
</div>
