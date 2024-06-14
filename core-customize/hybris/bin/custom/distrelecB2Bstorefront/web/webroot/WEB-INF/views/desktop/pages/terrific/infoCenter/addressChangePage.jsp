<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:theme code="formOfflineAddressChange.title" var="sOfflineAddressChangeTitle" />

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-import-tool skin-layout-content-without-navigation skin-layout-wide" >

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>
	
	<mod:global-messages/>
	
	<div class="ct">
		<div class="row">
			<div class="gu-4 gu-padding-left cf nav-align-right">
				<mod:nav-content />
				<cms:slot var="feature" contentSlot="${slots.TeaserContent}">
					<cms:component component="${feature}"/>
				</cms:slot>
			</div>
			<div class="gu-8">
				<h1 class="base page-title">${sOfflineAddressChangeTitle}</h1>
				
				<cms:slot var="feature" contentSlot="${slots.TopContent}">
					<cms:component component="${feature}"/>
				</cms:slot>
				
				<mod:form-offline-address-change/>

				<cms:slot var="feature" contentSlot="${slots.BottomContent}">
					<cms:component component="${feature}"/>
				</cms:slot>
			</div>
		</div>
	</div>
</views:page-default>