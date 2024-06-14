<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<div class="modal base" tabindex="-1">
	<div class="hd">
		<div class="-left"> <h3 class="title"><spring:message code="lightboxloginrequired.need.to.register" /></h3> </div>
		<div class="-right"> <a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="lightboxloginrequired.close" /></a> </div>
	</div>
	<c:url value="/j_spring_security_check" var="loginAction">
		<c:param name="addFavoriteProduct" value="${product.code}" />
	</c:url>
	<form:form id="metaLoginForm_add_favourite" action="${loginAction}" method="post" modelAttribute="metaLoginForm" cssClass="addFavoriteLoginForm">
		<div class="bd">
			<div class="error-box">
				<h4><spring:message code="lightboxloginrequired.error" />!</h4>
				<p><spring:message code="lightboxloginrequired.message.error" /></p>
			</div>
			<div class="form-row">
				<formUtil:formLabel idKey="j_username" labelKey="lightboxloginrequired.login" path="" mandatory="false"/>
				<%-- Validation is just standard not-empty because of cr DISTRELEC-2406 migrated users do not have an email address --%>
				<div class="field"><input name="j_username" id="j_username_add_favourite" class="field validate-empty" tabindex="1" type="text" value=""></div>
			</div>
			<div class="form-row">
				<formUtil:formLabel idKey="j_password" labelKey="lightboxloginrequired.password" path="" mandatory="false"/>
				<div class="field"><input name="j_password" id="j_password_add_favourite" class="field validate-empty" tabindex="2" type="password" value=""></div>
			</div>
			<div class="form-row">
				<form:checkbox id="j_remember_login-required_add_favourite" class="checkbox-big" path="_spring_security_remember_me" tabindex="3" />
				<formUtil:formLabel idKey="j_remember_login-required" labelKey="login.remember-login" path="j_remember" mandatory="false"/>
			</div>
		</div>
		<div class="ft">
			<div class="-left">
				<a class="link" href="/registration"><spring:message code="lightboxloginrequired.register.now" /></a><br/>
				<a class="link" href="/login/pw/request"><spring:message code="lightboxloginrequired.forgot.password" /></a>
			</div>
			<input type="submit" class="btn btn-primary btn-login" data-aainteraction="login button" value="<spring:message code='lightboxloginrequired.login' />" tabindex="4" />
		</div>
	</form:form>
</div>
<script id="tmpl-lightbox-login-validation-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>
<script id="tmpl-lightbox-login-validation-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>
