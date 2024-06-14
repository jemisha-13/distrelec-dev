<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:message code="error-bad-request.title" var="sErrorTitle" />
<spring:message code="error-bad-request.subtitle" var="sErrorSubTitle" />

<views:page-default-404 pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-error skin-layout-wide skin-layout-page-not-found" >

	<mod:global-messages />

	<div class="error-holder">
		<h1>${sErrorTitle}</h1>
		<p>${sErrorSubTitle}</p>
	</div>

	<mod:metahd skin="notfound" template="notfound" tag="nav" hasCompareProducts="${hasCompareProducts}" />
	<mod:metahd-suggest skin="notfound" htmlClasses="gu-12" />

</views:page-default-404>