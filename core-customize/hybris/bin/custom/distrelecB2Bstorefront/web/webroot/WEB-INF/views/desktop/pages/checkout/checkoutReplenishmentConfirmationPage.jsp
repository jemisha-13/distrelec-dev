<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<template:page pageTitle="${pageTitle}">

	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<div class="span-4 side-content-slot cms_banner_slot">
		<cms:slot var="feature" contentSlot="${slots.SideContent}">
			<cms:component component="${feature}"/>
		</cms:slot>
	</div>
	<div class="span-20 right last">
		<div class="span-20 your_order">
			<h1>
				<spring:theme code="order.replenishment.confirmation"/>
			</h1>

			<p>
				<spring:theme code="order.replenishment.thankYou"/>
			</p>

			<p class="span-5">
				<span id="replenishment_confirmaton_number">
					<spring:theme code="order.replenishment.confirmation.number" arguments="${orderData.jobCode}"/>
				</span>
			</p>
		</div>

		<div class="span-20 last">
			<order:replenishmentOrderDetailsItem order="${orderData}"/>
			<div class="span-7 left">
				<div class="item_container_holder positive">
					<div class="title_holder">
						<div class="title">
							<div class="title-top">
								<span></span>
							</div>
						</div>
						<h2>
							<spring:theme code="order.replenishment.schedule.title" text="Your replenishment
							schedule"/>
						</h2>
					</div>
					<div class="item_container">
						${orderData.triggerData.displayTimeTable}
					</div>
				</div>

				<div class="span-7 left last">
					<order:paymentMethodItem order="${orderData}"/>
				</div>
			</div>

			<div class="span-7">
				<order:deliveryAddressItem order="${orderData}"/>
			</div>

			<div class="span-6 right last">
				<order:replenishmentOrderTotalsItem order="${orderData}" containerCSS="positive"/>
			</div>

			<div class="span-20 last">
				<spring:url value="${continueUrl}" var="continueShoppingUrl"/>
				<spring:url value="${scheduleUrl}" var="scheduleDetailsUrl"/>
				<form:form action="#" method="get">
					<button type="submit" class="form right"
							onclick="window.location = '${continueShoppingUrl}'; return false">
						<spring:theme text="Continue Shopping" code="cart.page.continue"/>
					</button>
				</form:form>

				<form:form action="#" method="get">
					<button type="submit" class="form left"
							onclick="window.location = '${scheduleDetailsUrl}'; return false">
						<spring:theme text="Schedule Order Details" code="cart.page.continue.scheduleurl"/>
					</button>
				</form:form>
			</div>
		</div>
	</div>
</template:page>
