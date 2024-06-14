<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-password skin-layout-login skin-layout-wide" >

	<div id="breadcrumb" class="breadcrumb breadcrumb__forgot-password">
		<mod:breadcrumb/>
	</div>

	<div class="container">
		<div class="row">
			<mod:global-messages/>
		</div>
	</div>

	<div class="box-wrapper">
		<h1 class="base page-title box-wrapper__title">${cmsPage.title}</h1>
		<mod:login template="resend-password" skin="resend-password" />
	</div>
	
</views:page-default-md-full>