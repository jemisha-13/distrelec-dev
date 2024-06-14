<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/terrific/user" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<spring:theme var="emailAddressUpdateHeader" code="text.account.consent.emailaddressupdate.header"  />
<spring:theme var="emailAddressUpdateBody" code="text.account.consent.emailaddressupdate.body"  />
<spring:theme var="deEmailAddressUpdateBody" code="text.account.consent.de.emailaddressupdate.body"  />


<c:choose>
	<c:when test="${isMigratedUser}">
		<c:set value="updateLoginForm" var="formModelAttribute"/>
		<c:set value="/my-account/update-login" var="formAction"/>
	</c:when>
	<c:otherwise>
		<c:set value="updateEmailForm" var="formModelAttribute"/>
		<c:set value="/my-account/update-email" var="formAction"/>
	</c:otherwise>
</c:choose>

<!-- Change E-Mail !-->
<div class="form-box">
	<form:form action="${formAction}" modelAttribute="${formModelAttribute}" method="post">
		<c:choose>
			<c:when test="${isMigratedUser}">
				<h2 class="form-title"><spring:theme code="logindata.changeLogin.migratedUser.sectionTitle" /></h2>
				<div class="row base">
					<div class="gu-4">
						<formUtil:formLabel idKey="emailAddress" labelKey="logindata.changeLogin.migratedUser.login" path="login" mandatory="false" />
					</div>
					<div class="gu-4">
						<p>${currentEmail}</p>
					</div>
				</div>
				<div class="row base">
					<div class="gu-4">
						<formUtil:formLabel idKey="newLogin" labelKey="logindata.changeLogin.migratedUser.newLogin" path="login" mandatory="true" />
					</div>
					<div class="gu-4">
						<formUtil:formInputBox idKey="newLogin" path="login"  placeHolderKey="logindata.changeLogin.migratedUser.newLogin.placeholder" />
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<h2 class="form-title"><spring:theme code="logindata.changeLogin.newUsers.sectionTitle" /></h2>
				<div class="row base">
					<div class="row base">
						<div class="gu-4">
							<formUtil:formLabel idKey="emailAddress" labelKey="logindata.changeLogin.newUsers.emailAddress" path="email" mandatory="false" />
						</div>
						<div class="gu-4">
							<p>${currentEmail}</p>
						</div>
					</div>
					<div class="gu-4">
						<formUtil:formLabel idKey="newEmail" labelKey="logindata.changeLogin.newUsers.newEmail" path="email" mandatory="true" />
					</div>
					<div class="gu-4">
						<formUtil:formInputBox idKey="newEmail" path="email"  placeHolderKey="logindata.changeLogin.newUsers.newEmail.placeholder" />
					</div>
				</div>
			</c:otherwise>
		</c:choose>
		<div class="row base">
			<div class="gu-4">
				<formUtil:formLabel idKey="password" labelKey="logindata.changeLogin.newUsers.password" path="password" mandatory="true" />
			</div>
			<div class="gu-4">
				<formUtil:formPasswordBox idKey="password" path="password" placeHolderKey="logindata.changeLogin.newUsers.password.placeholder" />
			</div>
		</div>

		<div class="row">
			<div class="gu-8">

				<c:if test="${emailChangeStatus eq 'success'}">
					<c:choose>
						<c:when test="${currentCountry.isocode eq 'DE'}">
							<mod:global-messages template="component" skin="component"  headline='${emailAddressUpdateHeader}' body='${deEmailAddressUpdateBody}' type="success"/>
						</c:when>
						<c:otherwise>
							<mod:global-messages template="component" skin="component"  headline='${emailAddressUpdateHeader}' body='${emailAddressUpdateBody}' type="success"/>
						</c:otherwise>
					</c:choose>
				</c:if>

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