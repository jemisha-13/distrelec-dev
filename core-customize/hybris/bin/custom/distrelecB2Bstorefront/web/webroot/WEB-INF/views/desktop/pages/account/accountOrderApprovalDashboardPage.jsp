<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout" >

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb />
	</div>

	<div class="ct">
		<div class="row">
			<div class="gu-4 nav-align-right">
				<mod:nav-content template="myaccount" skin="myaccount" expandedNav="OrderManager" activeLink="OrderApprovals" />
			</div>
			<div class="gu-8">
				<h1 class="base">${cmsPage.title}</h1>
				<mod:global-messages />
				<mod:account-list-filter template="order-history" skin="print"/>
				<mod:account-list template="approval-request" skin="approval-request" />

			</div>
		</div>
	</div>

</views:page-default>
