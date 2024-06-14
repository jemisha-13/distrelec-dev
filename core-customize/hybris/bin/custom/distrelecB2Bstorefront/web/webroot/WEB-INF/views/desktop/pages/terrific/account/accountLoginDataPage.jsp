<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-account skin-layout-wide" >
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb />
	</div>
	<mod:global-messages />
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
				<mod:nav-content template="myaccount" skin="myaccount" expandedNav="AccountDetails" activeLink="LoginData" />
			</div>
			<div class="gu-8">
				<h1 class="base page-title">${cmsPage.title}</h1>
				<mod:account-login-data template="your-account" skin="your-account" />
				<mod:account-login-data template="change-name" skin="change-name" isMigratedUser="${isMigratedUser}" currentEmail="${currentEmail}" />
				<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
					<mod:account-login-data template="approved-budget" skin="approved-budget" />
					<mod:account-login-data template="allowed-permissions" skin="allowed-permissions" />
				</sec:authorize>
				<mod:account-login-data template="change-email" skin="change-email" isMigratedUser="${isMigratedUser}" currentEmail="${currentEmail}" />
				<mod:account-login-data template="change-password" skin="change-password" />
			</div>
		</div>
	</div>
</views:page-default>
