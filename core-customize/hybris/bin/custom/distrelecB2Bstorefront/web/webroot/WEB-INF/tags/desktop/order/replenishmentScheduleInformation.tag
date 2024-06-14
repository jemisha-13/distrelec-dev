<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<%--
	~ /*
	~  * [y] hybris Platform
	~  *
	~  * Copyright (c) 2000-2011 hybris AG
	~  * All rights reserved.
	~  *
	~  * This software is the confidential and proprietary information of hybris
	~  * ("Confidential Information"). You shall not disclose such Confidential
	~  * Information and shall use it only in accordance with the terms of the
	~  * license agreement you entered into with hybris.
	~  *
	~  */
--%>

<div class="span-12">
	<div class="item_container_holder">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
					<span></span>
					</div>
				</div>
				<h2><spring:theme code="text.account.orderHistory.replenishment"/></h2>
			</div>
			<div class="item_container">

			<p><spring:theme code="text.account.orderHistory.replenishment.startFrom" arguments="${order.triggerData.activationTime}"/></p>

			<p><spring:theme code="text.account.orderHistory.replenishment.schedule" arguments="${order.triggerData.displayTimeTable}"/></p>

			<p>
				<c:url value="/my-account/my-replenishment/${order.jobCode}" var="scheduleUrl"/>
				<a href="${scheduleUrl}"><spring:theme code="text.account.orderHistory.replenishment.link"/></a>
			</p>
		</div>
	</div>
</div>
