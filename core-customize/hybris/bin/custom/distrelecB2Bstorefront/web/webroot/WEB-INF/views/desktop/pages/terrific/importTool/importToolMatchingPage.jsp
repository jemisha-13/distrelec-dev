<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-import-tool skin-layout-wide" >

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb template="product" skin="product" />
	</div>

	<div class="container">

		<mod:global-messages />

		<h1 class="base page-title">
			<spring:message code="import-tool.matching.title" />
		</h1>

		<cms:slot var="feature" contentSlot="${slots.Content}">
			<cms:component component="${feature}" />
		</cms:slot>

		<c:if test="${not empty fileLines}">
			<mod:import-tool-matching title="${cmsPage.title}" fileLines="${fileLines}"/>
			<mod:nextprev-matching-tool />
		</c:if>

	</div>

</views:page-default-md-full>