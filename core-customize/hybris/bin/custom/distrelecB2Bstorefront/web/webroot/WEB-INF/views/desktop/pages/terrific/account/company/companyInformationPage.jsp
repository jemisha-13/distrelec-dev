<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-account skin-layout-wide skin-layout-wide" >

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb />
	</div>
	
	<mod:global-messages/>
	
	<div class="ct">
		<div class="row"> 
			<div class="gu-4 nav-align-right">
				<mod:nav-content template="myaccount" skin="myaccount" expandedNav="AccountDetails" activeLink="CompanyInfo" />
				<cms:slot var="feature" contentSlot="${slots.TeaserContent}">
					<cms:component component="${feature}"/>
				</cms:slot>
			</div>
			<div class="gu-8">
				<h1 class="base page-title">${cmsPage.title}</h1>
				<cms:slot var="feature" contentSlot="${slots.Content}">
					<cms:component component="${feature}"/>
				</cms:slot>
				<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP')">
					<mod:account-company-information />
				</sec:authorize>
			</div>
		</div>
	</div>

</views:page-default>