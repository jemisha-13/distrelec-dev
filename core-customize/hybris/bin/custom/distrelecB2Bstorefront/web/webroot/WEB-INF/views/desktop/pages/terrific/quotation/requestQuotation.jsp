<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-login skin-layout-wide skin-layout-quotes-form">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>
	<mod:global-messages/>
	<h1 class="base page-title">${cmsPage.title}</h1>
	<span class="page-subtitle"><spring:message code="quote.request.subtitle" /></span>
	<mod:form-request-quotes />
	<mod:lightbox-quotation-confirmation template="form" />
</views:page-default>