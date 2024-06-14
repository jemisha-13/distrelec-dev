<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-content-without-navigation skin-layout-wide cmscontentpage cmscontentpage__wo-navigation">

	<div class="page-wrapper">
		<div id="breadcrumb" class="breadcrumb">
		</div>
		<mod:global-messages />

		<cms:slot var="feature" contentSlot="${slots['Content']}">
			<cms:component component="${feature}" />
		</cms:slot>
	</div>


</views:page-default-md-full>