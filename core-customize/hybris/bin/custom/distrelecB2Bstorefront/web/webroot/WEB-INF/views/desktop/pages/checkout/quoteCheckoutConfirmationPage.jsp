<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
			<c:choose>
				<c:when test="${orderData.status == 'CANCELLED'}">
					<h1>
						<spring:theme code="order.quoteRequest.cancelled.confirmation"  arguments="${orderData.code}"/>
					</h1>
				</c:when>
				<c:otherwise>
					<h1>
						<spring:theme code="order.quoteRequest.confirmation"/>
					</h1>
					<p>
						<spring:theme code="order.quoteRequest.thankYou"/>
					</p>
				</c:otherwise>
			</c:choose>
				
		</div>
		<div class="span-20 last">
			<div class="span-10 top-content-slot cms_banner_slot">
				<cms:slot var="feature" contentSlot="${slots.TopContent}">
					<cms:component component="${feature}"/>
				</cms:slot>
			</div>
		</div>
		<div class="span-20 last delivery_stages">
		
		    <div class="span-10">
				<order:paymentMethodItem order="${orderData}"/>
			</div>
			
			<div class="span-5">
				<order:deliveryAddressItem order="${orderData}"/>
			</div>

			<div class="span-5 last">
				<order:deliveryMethodItem order="${orderData}"/>
			</div>

			
		</div>
		<div class="span-20 last">
			<order:orderDetailsItem order="${orderData}"/>
			<div class="span-12">
				<order:receivedPromotions order="${orderData}"/>
			</div>
			<div class="span-8 right last">
				<order:orderTotalsItem order="${orderData}" containerCSS="positive"/>
			</div>
			<c:if test="${not empty orderData.b2BComment && orderData.status=='REJECTED_QUOTE' || orderData.status=='PENDING_QUOTE'}">
				<div class="span-8 left last">
					<div class="item_container_holder positive">
						<div class="title_holder">
							<div class="title">
								<div class="title-top">
									<span></span>
								</div>
							</div>
							<h2>
								<spring:theme code="order.quoteReason"/>
							</h2>
						</div>
						<div class="item_container">
							<ul class="pad_none">
								<li>${orderData.b2BComment.comment}</li>
							</ul>
						</div>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</template:page>
