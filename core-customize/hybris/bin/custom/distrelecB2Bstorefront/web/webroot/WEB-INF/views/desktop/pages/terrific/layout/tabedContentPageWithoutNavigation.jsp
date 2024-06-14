<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-content-without-navigation skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<div class="spacer"></div>
	</div>
	<mod:global-messages />
	<br />
	<cms:slot var="feature" contentSlot="${slots['TopContentSlotTabedPage']}">
		<cms:component component="${feature}"/>
	</cms:slot>
	<div class="ct">
		<div class="parent">
			<mod:tabcontentpage slots="${slots}"  />
		</div>
	</div>
</views:page-default>