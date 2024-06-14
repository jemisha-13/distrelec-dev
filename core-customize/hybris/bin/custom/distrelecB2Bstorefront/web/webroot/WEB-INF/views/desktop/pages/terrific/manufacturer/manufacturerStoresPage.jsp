<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-manufacturer skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb />
	</div>
	<mod:global-messages/>
	<div class="ct">
		<div class="row">
			<div class="gu-12">
				<h1 class="base page-title">${cmsPage.title}</h1>
				<mod:manufacture-store template="overview-alphabet" manufacturers="${manufacturers}" skin="overview-alphabet"/>
				<cms:slot var="feature" contentSlot="${slots['Content']}">
					<cms:component component="${feature}" />
				</cms:slot>
				<mod:manufacture-store template="overview-list" manufacturers="${manufacturers}" skin="overview-list"/>
			</div>
		</div>
	</div>
</views:page-default>