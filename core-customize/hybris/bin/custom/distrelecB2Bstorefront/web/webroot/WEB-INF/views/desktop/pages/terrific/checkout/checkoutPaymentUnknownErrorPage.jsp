<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<views:page-checkout pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-checkout" >
    <c:if test="${not empty message}">
        <spring:theme code="${message}"/>
    </c:if>
    <mod:global-messages/>
	<h1 class="base page-title">Unknown Error</h1>
	<div class="ct">
        <div class="row">
			<div class="gu-8 padding-left">
				<cms:slot var="feature" contentSlot="${slots.Content}">
				<cms:component component="${feature}"/>
				</cms:slot>
			</div>
		</div>
	</div>
</views:page-checkout>
