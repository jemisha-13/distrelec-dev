<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="text.store.dateformat" var="datePattern" />
<h2 class="title"><spring:message code="accountlist.orderHistoryList.title" /></h2>
<%-- selectbox: order by --%>
<div class="action-box">
	<mod:account-order template="order-history" />
	<mod:productlist-tools template="order-history" />
</div>

<%-- list: user management list --%>
<ul class="data-list">
	<c:choose>
		<c:when test="${empty searchPageData.results}">
			<li class="row">
				<span class="no-orders"><spring:theme code="text.account.orderHistory.noOrders" text="You have no orders" /></span>
			</li>
		</c:when>
		<c:otherwise>
			<c:forEach items="${searchPageData.results}" var="order">
				<c:choose>
					<c:when test="${order.status.code == 'ERP_STATUS_SHIPPED' || order.status.code == 'ERP_STATUS_RECIEVED'}">
						<c:set var="iconStatus" value="ok" />
					</c:when>
					<c:otherwise>
						<c:set var="iconStatus" value="nok" />
					</c:otherwise>
				</c:choose>
				<c:url value="/my-account/open-orders/open-order-details/${order.code}" var="myAccountOrderDetailsUrl" />
				<li class="row">
					<ul class="data cf">
						<li class="entry">
							<dl>
								<dt class="label"><spring:message code="accountlist.orderHistoryList.orderNr" /></dt>
								<dd class="value">${order.code}</dd>
								<dt class="label"><spring:message code="accountlist.orderHistoryList.openOrders.startDate" /></dt>
								<dd class="value"><fmt:formatDate value="${order.placed}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" /></dd>
							</dl>
						</li>
						<li class="entry">
							<dl>
								<dt class="label"><spring:message code="accountlist.orderHistoryList.openOrders.closeDate" /></dt>
								<dd class="value"><fmt:formatDate value="${order.closeDate}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" /></dd>
								<dt class="label"><spring:message code="accountlist.orderHistoryList.orderStatus" /></dt>
								<dd class="value ${iconStatus}"><i></i> <spring:message code="orderOverviewBox.state.${order.status.code}"/></dd>
							</dl>
						</li>
						<li class="entry">
							<dl>
								<dt class="label"><spring:message code="accountlist.orderHistoryList.orderTotal" /></dt>
								<dd class="value"><format:price format="defaultSplit" displayValue="${order.total.value}" fallBackCurrency="${order.currency}" /></dd>
								<dt class="label"><spring:message code="accountlist.orderHistoryList.openOrders.reference" /></dt>
								<dd class="value">${order.orderReference}</dd>
							</dl>
						</li>
						<li class="entry">
							&nbsp;
						</li>
						<li class="entry">
							&nbsp;
						</li>
						<li class="action">
							<a href="${myAccountOrderDetailsUrl}" class="more"><i></i></a>
						</li>
					</ul>
				</li>
			</c:forEach>
		</c:otherwise>
	</c:choose>
	<li class="rowtemplate">
		<ul class="data cf">
			<li class="entry">
				<dl>
					<dt class="label"><spring:message code="accountlist.orderHistoryList.orderNr" /></dt>
					<dd class="value code"></dd>
					<dt class="label"><spring:message code="accountlist.orderHistoryList.orderDate" /></dt>
					<dd class="value orderdate"></dd>
				</dl>
			</li>
			<li class="entry">
				<dl>
					<dt class="label"><spring:message code="accountlist.orderHistoryList.orderTotal" /></dt>
					<dd class="value ordertotal"></dd>
					<dt class="label"><spring:message code="accountlist.orderHistoryList.orderStatus" /></dt>
					<dd class="value status"><i></i> </dd>
				</dl>
			</li>
			<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
				<li class="entry">
					<dl>
						<dt class="label"><spring:message code="accountlist.orderHistoryList.orderedBy" /></dt>
						<dd class="value orderedby"></dd>
					</dl>
				</li>
			</sec:authorize>
			<li class="entry">&nbsp;
			</li>
			<li class="entry">&nbsp;
			</li>
			<li class="action">
				<a href="#" class="more"><i></i></a>
			</li>
		</ul>
	</li>
</ul>
<c:if test="${searchPageData.pagination.numberOfPages > 1}">
	<div class="row row-show-more">
		<a class="show-more-link b-load-more" href="#"
		   data-action="/my-account/order-history/next"
		   data-pages-nr="${searchPageData.pagination.numberOfPages}"
		   data-page-current="0"
		   data-sort="${sortCode}"
		   data-status="${orderHistoryForm.status}"
		   data-order-number="${orderHistoryForm.orderNumber}"
		   data-from-date="${orderHistoryForm.fromDate}"
		   data-to-date="${orderHistoryForm.toDate}"
		><i></i> <span><spring:message code="accountlist.addresses.button.showMore" /></span></a>
	</div>
</c:if>