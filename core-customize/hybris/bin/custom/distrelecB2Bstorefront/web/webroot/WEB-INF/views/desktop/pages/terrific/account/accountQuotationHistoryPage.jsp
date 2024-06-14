<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="text.store.dateformat" var="datePattern" />

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-account skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb />
	</div>
	<mod:global-messages />
	<div class="ct">
		<div class="row">
			<div class="gu-4 nav-align-right">
				<mod:nav-content template="myaccount" skin="myaccount" expandedNav="OrderManager" activeLink="QuotationHistory" />
			</div>
			<div class="gu-8">
				<h1 class="base page-title">${cmsPage.title}</h1>
				<mod:account-list-filter template="quotation-history" skin="print"/>
				<mod:account-list template="quotation-history" skin="quotation-history" />
			</div>
		</div>
	</div>
	<mod:print-footer/>
</views:page-default>
