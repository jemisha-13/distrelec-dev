<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:htmlEscape defaultHtmlEscape="true" /> 

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-wide skin-layout-account-preferences">
	<div class="md-system">
		<div id="breadcrumb" class="breadcrumb">
			<mod:breadcrumb />
		</div>

		<mod:global-messages />
		<div class="ct">
			<div class="row">
				<div class="col-12">
					<cms:slot var="feature" contentSlot="${slots.Content}">
						<cms:component component="${feature}"/>
					</cms:slot>
				</div>
			</div>
		</div>

		<div class="container-lg account-preferences">
			<div class="row">
				<div class="col-12 col-md-9 account-preferences__main">
					<mod:account-login-data template="preferences" skin="preferences" />
				</div>
				<div class="col-12 col-md-3 account-preferences__filter">
					<mod:nav-content template="myaccount" skin="myaccount" expandedNav="AccountDetails" activeLink="PreferenceCenter" />
				</div>
			</div>
		</div>
	</div>

</views:page-default>
