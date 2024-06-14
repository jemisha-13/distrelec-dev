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
			<mod:cart-list template="order-detail" skin="order-detail" />
			<div class="row">
				<div class="gu-8 border-top">
					<mod:cart-toolbar template="open-order-detail" skin="open-order-detail" orderData="${orderData}" />
					<mod:order-detail-section template="open-order" orderData="${orderData}" htmlClasses="padding-right" />
				</div>
				<div class="gu-4">
					<mod:cart-pricecalcbox template="open-order" skin="open-order" orderData="${orderData}" />
				</div>
			</div>
		</div>
	</div>
</views:page-default>
