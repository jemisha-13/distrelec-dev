<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:message code="newcheckout.loginTitle" text="Login to your account" var="sLoginTitle"/>

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-wide skin-layout-login">
	<div class="container">
		<div class="row">
			<div class="col-sm-12">
				<div id="breadcrumb" class="breadcrumb breadcrumb__login">
					<mod:breadcrumb/>
				</div>
			</div>
		</div>

		<div class="row">
			<mod:global-messages/>
		</div>
	</div>
	<div class="box-wrapper">
		<h1 class="base page-title box-wrapper__title">${sLoginTitle}</h1>
		<mod:login htmlClasses="skin-login-main" />
	</div>

</views:page-default-md-full>