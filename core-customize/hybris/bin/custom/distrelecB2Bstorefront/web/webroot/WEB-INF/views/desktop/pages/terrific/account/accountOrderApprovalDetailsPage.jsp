<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-account-detail skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb />
	</div>
	<mod:global-messages />
	<div class="ct">
		<div class="row">
			<h1 class="base page-title">${cmsPage.title}</h1>
			<c:set var="cType" value="b2c" />
			<c:if test="${not empty orderData.customerType}">
				<c:set var="cType" value="${fn:toLowerCase(fn:substring(orderData.customerType,0,3))}" />
			</c:if>
			<c:if test="${cType == 'b2b'}">
				<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
					<mod:checkout-order-budget-approval-bar template="myaccount" skin="admin" orderData="${orderData}" />
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
					<mod:checkout-order-budget-approval-bar template="myaccount" skin="user" orderData="${orderData}" />
				</sec:authorize>
			</c:if>
			<mod:cart-list template="approval-request" skin="order-detail skin-cart-list-approval-request" />
			<div class="row">
				<div class="gu-8 border-top">
					<mod:cart-toolbar template="order-detail" skin="order-detail" orderData="${orderData}" />
					<mod:order-detail-section orderData="${orderData}" htmlClasses="padding-right" />
				</div>
				<div class="gu-4">
					<mod:cart-pricecalcbox template="approval-request" skin="approval-request" orderData="${orderData}" />
				</div>
			</div>
		</div>
	</div>
</views:page-default>
