<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="metaDescription" required="false" %>
<%@ attribute name="metaKeywords" required="false" %>
<%@ attribute name="pageCss" required="false" fragment="true" %>
<%@ attribute name="pageScripts" required="false" fragment="true" %>

<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="analytics" tagdir="/WEB-INF/tags/shared/analytics" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="${currentLanguage.isocode}">
	<head>
		<title>
			${not empty pageTitle ? pageTitle : not empty cmsPage.title ? cmsPage.title : 'Accelerator Title'}
		</title>

		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<meta name="description" content="${metaDescription}"/>

		<spring:theme code="img.favIcon" text="/" var="favIconPath"/>
		<c:url value="${favIconPath}" var="faviconURL" />
		<link rel="shortcut icon" href="${faviconURL}" type="image/x-icon"/>

		<%-- CSS Files Are Loaded First as they can be downloaded in parallel --%>
		<template:styleSheets />

		<%-- Inject any additional CSS required by the page --%>
		<jsp:invoke fragment="pageCss"/>
		
		<analytics:googleAnalytics />
		<analytics:jirafe />
	</head>

	<c:url value="/_ui/desktop/common/css/hybris.cms.live.edit.css" var="liveEditCssPathUrl"/>
	<c:url value="/_ui/desktop/common/js/hybris.cms.live.edit.js" var="liveEditJsPathUrl"/>
	<cms:body cssClass="language-${currentLanguage.isocode}" liveEditCssPath="${liveEditCssPathUrl}" liveEditJsPath="${liveEditJsPathUrl}">

		<%-- Load JavaScript required by the site --%>
		<template:javaScript />
		
		<%-- Inject any additional JavaScript required by the page --%>
		<jsp:invoke fragment="pageScripts"/>

		<%-- Inject the page body here --%>
		<jsp:doBody/>

	</cms:body>

	<template:debugFooter />
</html>
