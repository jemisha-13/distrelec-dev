<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:url value="/my-account/company/edit/employee/${employeeCustomerId}" var="submitActionUrl" />
<c:url value="/my-account/company/user-management" var="cancelUrl" />
<c:url value="/my-account/company/deactivate/user/${employeeCustomerId}" var="deactivateUserUrl" />
<c:url value="/my-account/company/delete/user/${employeeCustomerId}" var="deleteUserUrl" />

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-register skin-layout-wide">
	
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>
	
	<mod:global-messages/>
	
	<div class="row">
		<div class="gu-12">
			<h1 class="base page-title"><spring:theme code="text.account.company.userManagement.edit.employee" /></h1>
		</div>
		<div class="gu-12 padding-left">
			<mod:account-user-detail-form cancelUrl="${cancelUrl}" deactivateUserUrl="${deactivateUserUrl}" submitActionUrl="${submitActionUrl}" deleteUserUrl="${deleteUserUrl}" />
		</div>
	</div>
</views:page-default>
