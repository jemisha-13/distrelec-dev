<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-compare skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb template="product" skin="product" />
	</div>

	<div class="container">
		<mod:global-messages htmlClasses="global-messages__compare"/>
	</div>

	<div class="ct ct__compare container">
		<h1 class="base page-title">${cmsPage.title}</h1>

		<div class="row">
			<div class="col-sm-12">
				<c:choose>
					<c:when test="${not empty compareProducts}">
						<mod:compare-list currentList="${compareProducts}" />
					</c:when>
					<c:otherwise>
						<spring:message code="compare.list.empty" />
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<c:if test="${not empty compareProducts}">
			<div class="col-sm-12">
				<mod:productlist-tools template="compare"/>
			</div>
		</c:if>

		<mod:print-footer/>
	</div>
</views:page-default-md-full>
