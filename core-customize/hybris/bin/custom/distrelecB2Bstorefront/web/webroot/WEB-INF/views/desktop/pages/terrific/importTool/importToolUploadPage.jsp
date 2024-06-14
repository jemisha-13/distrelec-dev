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

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-import-tool skin-layout-wide cmscontentpage">


	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb template="product" skin="product" />
	</div>

	<div class="container">

		<mod:global-messages />

		<h1 class="base page-title">${cmsPage.title}</h1>
		<cms:slot var="feature" contentSlot="${slots.Content}">
			<cms:component component="${feature}" />
		</cms:slot>

		<mod:bom-data-import />

	</div>


</views:page-default-md-full>