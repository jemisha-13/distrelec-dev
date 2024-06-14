<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<spring:message code="accountlist.orderHistoryList.returnStatus" var="sReturnStatus"/>

<c:choose>
	<c:when test="${orderData.status.code == 'ERP_STATUS_SHIPPED' || orderData.status.code == 'ERP_STATUS_RECIEVED'}">
		<c:set var="iconStatus" value="ok" />
	</c:when>
	<c:when test="${orderData.status.code == 'ERP_STATUS_CANCELLED'}">
		<c:set var="iconStatus" value="cancelled" />
	</c:when>
	<c:when test="${orderData.status.code == 'ERP_STATUS_PARTIALLY_SHIPPED'}">
		<c:set var="iconStatus" value="partially-shipped" />
	</c:when>
	<c:otherwise>
		<c:set var="iconStatus" value="nok" />
	</c:otherwise>
</c:choose>

<c:set var="displayOrderTime" value="date" />

<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')" var="isAdmin" />
<c:set var="li_class" value="${isAdmin ? 'entry_admin' : 'entry'}" />

<ul class="data-list">
	<li class="row-holder">
		<ul class="data cf">
			<li class="${li_class}">
				<dl>
					<dt class="label"><spring:message code="orderOverviewBox.orderDate"/></dt>
					<fmt:setLocale value="${currentLanguage.isocode}" scope="session"/>
					<dd class="value"><fmt:formatDate value="${orderData.orderDate}" dateStyle="long" timeStyle="short" type="${displayOrderTime}" /></dd>
				</dl>
			</li>
			<li class="${li_class}">
				<dl>
					<dt class="label"><spring:message code="orderOverviewBox.orderNr"/></dt>
					<dd class="value">${orderData.code}</dd>
				</dl>
			</li>
			<c:if test="${isAdmin}">
				<li class="${li_class}">
					<dl>
						<dt class="label"><spring:message code="orderOverviewBox.orderedBy"/></dt>
						<dd class="value">${orderData.placedBy}</dd>
					</dl>
				</li>
			</c:if>
			<li class="${li_class}">
				<dl>
					<dt class="label"><spring:message code="orderOverviewBox.orderReceivedVia"/></dt>
					<dd class="value">${orderData.salesApplication}</dd>
				</dl>
			</li>
			<li class="${li_class}">
				<dl>
					<dt class="label"><spring:message code="orderOverviewBox.state"/></dt>
					<dd class="icon icon--${iconStatus}">
						<i></i> <span><spring:message code="orderOverviewBox.state.${orderData.status.code}"/></span>
					</dd>
					<c:if test="${isRMARaised=='true'}">
						<dd>
							<span class="return">
								<i class="fa fa-reply" aria-hidden="true"></i>
								<span>
										${sReturnStatus}
								</span>
							</span>
						</dd>
					</c:if>
				</dl>
			</li>
		</ul>
	</li>
</ul>
