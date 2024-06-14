<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-content-without-navigation skin-layout-wide">

	<div class="container">
		<div id="breadcrumb" class="breadcrumb">
			<div class="spacer"></div>
		</div>
		<mod:global-messages />
	</div>

	<div class="ct">
		<div class="parent">
		  <div class="left">
			  <cms:slot var="feature" contentSlot="${slots['Content']}">
			  	<cms:component component="${feature}" />
			  </cms:slot>
		  </div>
		  <div class="right">
			<cms:slot var="feature" contentSlot="${slots['ProductBoxRight']}">
				<cms:component component="${feature}" />
			</cms:slot>
		  </div>
		</div>
		<div class="gu-12 bottom ProductBoxBottom">
			<cms:slot var="feature" contentSlot="${slots['ProductBoxBottom']}">
				<cms:component component="${feature}" />
			</cms:slot>
		</div>	
	</div>
</views:page-default>