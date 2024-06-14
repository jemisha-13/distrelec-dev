<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-default-full skin-layout-account-detail skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb template="new-account" skin="new-account" />
	</div>
	<div class="order-detail-my-account-holder">
		<div class="container">
			<div class="row">
				<div class="col-12">
					<mod:global-messages />
				</div>
				<div class="col-12 col-lg-9 bs-o1 order-detail-my-account-holder__content">
					<h1 class="base page-title">${cmsPage.title}</h1>
					<div class="orderdetail-toolbar">
						<a class="btn btn-secondary btn-back" href="<c:url value="/my-account/order-history" />"><spring:message code="text.back" text="Back" /><i></i></a>
						<mod:cart-toolbar template="order-detail" skin="order-detail" orderData="${orderData}" />
					</div>
					<mod:order-overview-box />
					<div class="orderdetail-main">
						<mod:order-detail-section orderData="${orderData}" />
					</div>
					<mod:cart-list template="order-detail" skin="order-detail" />
					<div class="orderdetail-total">
						<div class="orderdetail-total__item">
							<c:if test="${currentSalesOrg.erpSystem eq 'SAP' and orderData.status.code ne 'ERP_STATUS_SHIPPED' and orderData.status.code ne 'ERP_STATUS_CANCELLED'}">
								<div class="estimated-date-info">
									<p>
										<spring:message code="cart.list.deliverydate.estimated.info" text="Estimated"/>
									</p>
								</div>
							</c:if>
						</div>
						<div class="orderdetail-total__item">
							<mod:cart-pricecalcbox orderData="${orderData}" showTitle="false"/>
						</div>
					</div>
				</div>
				<div class="col-12 col-lg-3 bs-o2 d-print-none">
					<mod:nav-content template="myaccount" skin="myaccount" expandedNav="OrderManager" activeLink="OrderHistory" />
				</div>
			</div>
		</div>
	</div>
	<mod:print-footer/>
</views:page-default-md-full>

