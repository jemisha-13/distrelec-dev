<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/terrific/user" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-product skin-layout-wide">

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>

	<mod:global-messages/>

	<div class="ct">
		<div class="row">
			<!-- div class="gu-4">
				<mod:nav-content />
			</div -->
			<div class="gu-12">
				<h1 class="base page-title">${cmsPage.title}</h1>
				
				<cms:slot var="feature" contentSlot="${slots.TopContent}">
					<cms:component component="${feature}"/>
				</cms:slot>
				
				<mod:survey template="survey-error"/>
			</div>
		</div>
	</div>

	<mod:print-footer />
</views:page-default>
