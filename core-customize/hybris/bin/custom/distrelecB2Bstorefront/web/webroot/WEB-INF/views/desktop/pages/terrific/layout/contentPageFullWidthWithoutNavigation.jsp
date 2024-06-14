<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-full-width-content-without-navigation skin-layout-wide fullwidthwithoutnavigation">

	<div id="breadcrumb" class="breadcrumb">
		<div class="spacer"></div>
	</div>

	<mod:global-messages />

	<div class="ct">
		<div class="row i-am-full-width">
			<div class="">
				<cms:slot var="feature" contentSlot="${slots['Content']}">
					<cms:component component="${feature}" />
				</cms:slot>
			</div>
		</div>
	</div>
	
</views:page-default>