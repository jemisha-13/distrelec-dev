<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-error skin-layout-wide" >
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb />
	</div>
	<div class="ct base">
		<h1 class="padding-left" ><c:out value="${message}" /></h1>
		<cms:slot var="feature" contentSlot="${slots['Content']}">
			<div class="padding-left">
				<cms:component component="${feature}"/>
			</div>
		</cms:slot>
		<c:if test="${displayStacktrace and not empty exception}">
			<div class="padding-left" style="border: 1px red solid">
				<p><c:if test="${not empty exception.message}">: <c:out value="${exception.message}" /></c:if></p>
				<p>
					<c:forEach var="errorLine" items="${exception.stackTrace}">
						<c:out value="${errorLine}" /><br/>
					</c:forEach>
				</p>
			</div>
		</c:if>
	</div>
</views:page-default>