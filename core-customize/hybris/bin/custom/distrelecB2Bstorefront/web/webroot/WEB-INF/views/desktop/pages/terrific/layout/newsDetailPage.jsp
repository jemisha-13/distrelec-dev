<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-content-with-navigation skin-layout-wide" >

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>

	<mod:global-messages/>

	<div class="ct">
		<div class="row">
			<div class="gu-8">
				<cms:slot var="feature" contentSlot="${slots.Content}">
					<cms:component component="${feature}"/>
				</cms:slot>
			</div>
			<div class="gu-4">
			</div>
		</div>
	</div>

</views:page-default>