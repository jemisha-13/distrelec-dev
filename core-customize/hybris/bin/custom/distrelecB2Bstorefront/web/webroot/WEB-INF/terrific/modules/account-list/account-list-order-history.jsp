<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>


<spring:message code="text.store.dateformat" var="datePattern" />
<spring:message code="accountlist.orderHistoryList.orderNr" var="sOrderNumber"/>
<spring:message code="accountlist.orderHistoryList.orderTotal" var="sOrderTotal" />
<spring:message code="accountlist.orderHistoryList.orderedBy" var="sOrderBy" />
<spring:message code="accountlist.orderHistoryList.invoiceNr" var="sOrderInvoiceNumber" />
<spring:message code="accountlist.orderHistoryList.orderStatus" var="sOrderStatus"/>
<spring:message code="accountlist.orderHistoryList.orderDate" var="sOrderDate"/>
<spring:message code="accountlist.orderHistoryList.orderDetail" var="sOrderDetail"/>
<spring:message code="accountlist.orderHistoryList.returnStatus" var="sReturnStatus"/>

<h2 class="title"><spring:message code="accountlist.orderHistoryList.title" /></h2>

<%-- selectbox: order by --%>
<div class="action-box">
	<mod:account-order template="order-history" />
	<mod:productlist-tools template="order-history" />
</div>

<%-- determine pageSize since BE is not able to store it --%>
<c:set var="resultCount" value="${fn:length(searchPageData.results)}" />
<c:choose>
	<c:when test="${resultCount > 25 && resultCount <= 50}">
		<c:set var="pageSize" value="50" />
	</c:when>
	<c:when test="${resultCount > 10 && resultCount <= 25}">
		<c:set var="pageSize" value="25" />
	</c:when>
	<c:otherwise>
		<c:set var="pageSize" value="10" />
	</c:otherwise>
</c:choose>

<div class="data-list-header">
	<div class="data-list-header__item date">
		${sOrderDate}:
	</div>
	<div class="data-list-header__item number">
		${sOrderNumber}:
	</div>
	<div class="data-list-header__item by">
		${sOrderBy}:
	</div>
	<div class="data-list-header__item invoice">
		${sOrderInvoiceNumber}:
	</div>
	<div class="data-list-header__item status">
		${sOrderStatus}:
	</div>
	<div class="data-list-header__item total">
		${sOrderTotal}:
	</div>
</div>

<%-- list: user management list --%>
<div class="data-list order-history"
	data-current-query-url="/my-account/order-history"
	data-page="${searchPageData.pagination.currentPage}"
	data-page-size="${pageSize}"

	data-sort="${sortCode}"
	data-status="${orderHistoryForm.status}"
	data-order-number="${orderHistoryForm.orderNumber}"
	data-from-date="${orderHistoryForm.fromDate}"
	data-to-date="${orderHistoryForm.toDate}"
>
	<c:choose>
		<c:when test="${empty searchPageData.results}">
			<div class="row">
				<span class="no-orders"><spring:theme code="text.account.orderHistory.noOrders" text="You have no orders" /></span>
			</div>
		</c:when>
		<c:otherwise>
			<c:forEach items="${searchPageData.results}" var="order" varStatus="loop">
				<c:choose>
					<c:when test="${order.status.code == 'ERP_STATUS_SHIPPED' || order.status.code == 'ERP_STATUS_RECIEVED'}">
						<c:set var="iconStatus" value="ok" />
					</c:when>
					<c:when test="${order.status.code == 'ERP_STATUS_CANCELLED'}">
						<c:set var="iconStatus" value="cancelled" />
					</c:when>
					<c:when test="${order.status.code == 'ERP_STATUS_PARTIALLY_SHIPPED'}">
						<c:set var="iconStatus" value="partially-shipped" />
					</c:when>
					<c:otherwise>
						<c:set var="iconStatus" value="nok" />
					</c:otherwise>
				</c:choose>
				<c:url value="/my-account/order-history/order-details/${order.code}" var="myAccountOrderDetailsUrl" />
				<div class="row-holder">
					<div class="row-holder__item">
						<div class="column date">
							<fmt:formatDate value="${order.placed}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" />
						</div>
						<div class="column number">
							${order.code}
						</div>
						<div class="column by">
							<c:choose>
								<c:when test="${not empty order.deliveryDate && not empty order.invoiceIds}">
									${sOrderBy}
									${order.userFullName}
									<fmt:formatDate value="${order.deliveryDate}" dateStyle="short" type="date" pattern="${datePattern}" />
								</c:when>
								<c:when test="${not empty order.deliveryDate}">
									${order.userFullName}

									<spring:message code="accountlist.orderHistoryList.estDeliveryDate" />
									<fmt:formatDate value="${order.deliveryDate}" dateStyle="short" type="date" pattern="${datePattern}" />

								</c:when>
								<c:when test="${not empty order.invoiceIds}">
									${order.userFullName}
								</c:when>
								<c:otherwise>
									${order.userFullName}
								</c:otherwise>
							</c:choose>
						</div>
						<div class="column invoice">
							<c:choose>
								<c:when test="${not empty order.deliveryDate && not empty order.invoiceIds}">
									${order.invoiceIds}
								</c:when>
								<c:when test="${not empty order.deliveryDate}">
									&nbsp;
								</c:when>
								<c:when test="${not empty order.invoiceIds}">
									${order.invoiceIds}
								</c:when>
								<c:otherwise>
									&nbsp;
								</c:otherwise>
							</c:choose>
						</div>
						<div class="column status">
							<c:choose>
								<c:when test="${not empty order.status.code}">
									<span class="value value--${iconStatus}">
										<i>

										</i>
										<span>
											<spring:message code="orderOverviewBox.state.${order.status.code}"/>
										</span>
									</span>
								</c:when>
								<c:otherwise>
									<span class="value value--${iconStatus}">
										<i>

										</i>
										<span>
											n/a
										</span>
									</span>
								</c:otherwise>
							</c:choose>
							<c:if test="${order.rmaStatus=='true'}">
								<span class="return">
									<i class="fa fa-reply" aria-hidden="true"></i>
									<span>
										${sReturnStatus}
									</span>
								</span>
							</c:if>
						</div>
						<div class="column total">
							<format:price format="defaultSplit" displayValue="${order.total.value}" fallBackCurrency="${order.currency}" priceData="${order.total}"/>
						</div>
					</div>
					<div class="row-holder__item">
						<div class="row">
							<div class="col-12 col-md-9">
								<mod:cart-toolbar orderHistoryData="${order}" orderIndex="${loop.index}" template="order-history" skin="order-history" tag="div" />
							</div>
							<div class="col-12 col-md-3">
								<div class="item action">
									<a href="${myAccountOrderDetailsUrl}" class="mat-button mat-button--action-red">
								<span>
										${sOrderDetail}
								</span>
										<i class="fa fa-angle-right" aria-hidden="true"></i>
									</a>
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</div>


<c:if test="${not empty searchPageData.results}">
	<%-- ProductListPagination --%>
	<mod:productlist-pagination myAccountSearchPageData="${searchPageData}" template="myaccount" skin="my-account" />
</c:if>