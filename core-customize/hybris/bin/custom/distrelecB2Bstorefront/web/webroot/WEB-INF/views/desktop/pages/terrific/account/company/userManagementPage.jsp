<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-account skin-layout-wide" >
	
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb />
	</div>
	
	<mod:global-messages/>

	<div class="ct">
		<div class="row">
			<div class="gu-8">
				<cms:slot var="feature" contentSlot="${slots.Content}">
					<cms:component component="${feature}"/>
				</cms:slot>
			</div>
		</div>
		<div class="row">
			<div class="gu-4 nav-align-right">
				<mod:nav-content template="myaccount" skin="myaccount" expandedNav="OrderManager" activeLink="UserManagement" />
			</div>
			<div class="gu-8">
				<h1 class="base page-title">${cmsPage.title}</h1>
				<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP')">
					<mod:account-list-filter template="user-management" skin="user-management" />
					<mod:account-list template="user-management" skin="user-management" />
				</sec:authorize>
			</div>
		</div>
	</div>
</views:page-default>