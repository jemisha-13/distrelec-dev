<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>


<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-error skin-layout-home" >
	<h1><c:out value="${message}" /></h1>
	<h2><c:out value="${uuid}" /></h2>
	
	<cms:slot var="feature" contentSlot="${slots['Content']}">
		<cms:component component="${feature}"/>
	</cms:slot>
	
	<c:if test="${displayStacktrace and not empty exception}">
		<div style="border: 1px red solid">
			<p><c:out value="${exception.class.name}" /><c:if test="${not empty exception.message}"> : <c:out value="${exception.message}" /></c:if></p>
			<p style="margin-left:20px">
				<c:forEach var="errorLine" items="${exception.stackTrace}">
					<c:out value="${errorLine}" /><br/>
				</c:forEach>
			</p>
		</div>
	</c:if>
</views:page-default>