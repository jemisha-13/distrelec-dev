<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-default-full skin-layout-account skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb template="new-account" skin="new-account" />
	</div>
	<div class="order-history-my-account-holder">
		<div class="container">
			<div class="row">
				<div class="col-12">
					<mod:global-messages />
				</div>
				<div class="col-12 col-lg-9 bs-o1">
					<h1 class="base page-title">${cmsPage.title}</h1>
					<mod:account-list-filter template="order-history" skin="print order-history"/>
					<mod:account-list template="order-history" skin="order-history" />
				</div>
				<div class="col-12 col-lg-3 bs-o2">
					<mod:nav-content template="myaccount" skin="myaccount" expandedNav="OrderManager" activeLink="OrderHistory" />
				</div>
			</div>
		</div>
	</div>
	<mod:print-footer/>
</views:page-default-md-full>
