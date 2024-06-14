<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:accountNav selected="${cmsPage.label}"/>
	<div class="span-20 right last">

		<div class="span-20 your_order">
			<p class="span-5">
				<span id="replenishment_confirmaton_number">
					<spring:theme code="order.replenishment.confirmation.number" arguments="${scheduleData.jobCode}"/>
				</span>
			</p>
		</div>

		<div class="span-20 last">
			<order:replenishmentOrderDetailsItem order="${scheduleData}"/>
		</div>

		<div class="span-20 last">
			<div class="span-7 left">
				<div class="item_container_holder positive">
					<div class="title_holder">
						<div class="title">
							<div class="title-top">
								<span></span>
							</div>
						</div>
						<h2>
							<spring:theme code="order.replenishment.schedule.title"
										  text="Your replenishment schedule"/>
						</h2>
					</div>
					<div class="item_container">
						${scheduleData.triggerData.displayTimeTable}
					</div>
				</div>

				<div class="span-7 left last">
					<order:paymentMethodItem order="${scheduleData}"/>
				</div>
			</div>

			<div class="span-7">
				<order:deliveryAddressItem order="${scheduleData}"/>
			</div>

			<div class="span-6 right last">
				<order:replenishmentOrderTotalsItem order="${scheduleData}" containerCSS="positive"/>
			</div>
		</div>

		<c:if test="${scheduleData.active}">
			<spring:url value="/my-account/my-replenishment/detail/confirmation/cancel/${scheduleData.jobCode}/"
						var="cancelReplenishmentUrl"/>
			<div class="last">
				<a href="${cancelReplenishmentUrl}">
					<ycommerce:testId code="replenishmentHistory_cancel_link">
						<button class="positive right pad_right place-order">
							<spring:theme code="text.account.replenishment.cancel.button"
										  text="Cancel Replenishment"/>
						</button>
					</ycommerce:testId>
				</a>
			</div>
		</c:if>

		<div class="span-20 last">
			<div class="item_container_holder">
				<div class="title_holder">
					<div class="title">
						<div class="title-top">
							<span></span>
						</div>
					</div>
					<h2>
						<spring:theme code="text.account.replenishment.orders" text="Replenishment Orders"/>
					</h2>
				</div>
				<div class="item_container">
					<c:if test="${not empty searchPageData.results}">
						<p>
							<spring:theme code="text.account.replenishment.viewReplenishmentOrders"
										  text="View your replenishment orders"/>
						</p>
						<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"
										searchPageData="${searchPageData}"
										searchUrl="/my-account/my-replenishment/${scheduleData.jobCode}?sort=${searchPageData.pagination.sort}&show=${param.show}"
										msgKey="text.account.replenishment.page"/>

						<form>
							<table id="order_history">
								<thead>
								<tr>
									<th id="header1">
										<spring:theme code="text.account.replenishment.orderNumber"
													  text="Order Number"/>
									</th>
									<th id="header2">
										<spring:theme code="text.account.replenishment.orderStatus"
													  text="Order Status"/>
									</th>
									<th id="header3">
										<spring:theme code="text.account.replenishment.purchaseOrderNumber"
													  text="P.O.No"/>
									</th>
									<th id="header4">
										<spring:theme code="text.account.replenishment.datePlaced" text="Date Placed
										"/>
									</th>
									<th id="header5">
										<spring:theme code="text.account.replenishment.actions" text="Actions"/>
									</th>
								</tr>
								</thead>
								<tbody>
								<c:forEach items="${searchPageData.results}" var="order">
									<c:url value="/my-account/my-replenishment/${scheduleData.jobCode}/${order.code}" var="replenishmentActionLink"/>
									<tr>
										<td headers="header1">
											<ycommerce:testId code="replenishmentHistory_orderNumber_link">
												<a href="${replenishmentActionLink}">${order.code}</a>
											</ycommerce:testId>
										</td>
										<td headers="header2">
											<ycommerce:testId code="replenishmentHistory_orderStatus_label">
												<p>${order.statusDisplay}</p>
											</ycommerce:testId>
										</td>
										<td headers="header3">
											<ycommerce:testId code="replenishmentHistory_purchaseOrderNumber_label">
												<p>${order.purchaseOrderNumber}</p>
											</ycommerce:testId>
										</td>
										<td headers="header4">
											<ycommerce:testId code="replenishmentHistory_orderDate_label">
												<p>${order.placed}</p>
											</ycommerce:testId>
										</td>
										<td headers="header5">
											<ycommerce:testId code="replenishmentHistory_Actions_links">
												<ul class="updates">
													<li><a
														href="${replenishmentActionLink}">
														<spring:theme code="text.view" text="View"/>
													</a></li>
												</ul>
											</ycommerce:testId>
										</td>
									</tr>
								</c:forEach>
								</tbody>
							</table>
						</form>
						<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"
										searchPageData="${searchPageData}"
										searchUrl="/my-account/my-replenishment/${scheduleData.jobCode}?sort=${searchPageData.pagination.sort}&show=${param.show}"
										msgKey="text.account.replenishment.page"/>
					</c:if>
					<c:if test="${empty searchPageData.results}">
						<p>
							<spring:theme code="text.account.replenishment.noOrders" text="You have no orders"/>
						</p>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</template:page>

