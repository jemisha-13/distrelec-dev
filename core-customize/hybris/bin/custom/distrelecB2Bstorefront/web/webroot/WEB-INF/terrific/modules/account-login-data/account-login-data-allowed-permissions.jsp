<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/login-data/send" />

<!-- Allowed Permissions
https://wiki.namics.com/display/distrelint/C203-LoginDataChange
this form is just for users, NOT visible for admin account
!-->
<div class="form-box">
	<form>
		<h2 class="form-title"><spring:theme code="logindata.allowedPermissions" /></h2>
		<div class="row base">
			<div class="gu-4">
				<label class="required"><spring:theme code="logindata.allowedPermissions.requestQuotations" /></label>
			</div>
			<div class="gu-4">
				<p class="value"><spring:theme code="logindata.no" /></p>
			</div>
		</div>
	</form>
</div>
