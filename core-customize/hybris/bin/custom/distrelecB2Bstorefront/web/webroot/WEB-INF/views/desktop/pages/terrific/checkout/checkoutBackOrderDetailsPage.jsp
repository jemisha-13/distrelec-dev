<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="totalProducts" value="${fn:length(backOrderNotProfitableList)}" />
<c:set var="url" value="/checkout/backorderDetails/updateBackOrder"/>
<c:set var="cartUrl" value="/cart"/>
<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<spring:message code="backorder.itemTotal" var="sItemTotalText" arguments="${totalProducts}" text="{0} items in your cart cannot be shipped to your location for operational reasons" />
<spring:message code="backorder.page.title" var="sPageTitle" text="Important, we need to make changes to your cart" />
<spring:message code="backorder.page.returnCart" var="sReturnCart" text="Cancel and go back to Cart" />
<spring:message code="b2bunit.save" var="sSaveChanges" />

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-back-order skin-layout-cart skin-layout-wide skin-layout-nonavigation">
		<div class="back-order-holder">
			<div class="container">
				<div class="row">
					<div class="col-12">
						<div class="back-order-holder__title">
							<h1>
								${sPageTitle}
							</h1>
							<p class="item-count">
								${sItemTotalText}
							</p>
						</div>
					</div>
					<div class="col-12 col-lg-9">
						<div class="back-order-holder__content">
							<c:choose>
								<c:when test="${isOCI eq false}">
									<c:forEach var="orderEntry" items="${backOrderNotProfitableList}">
										<mod:back-order-item orderEntry="${orderEntry}"/>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<c:forEach var="orderEntry" items="${backOrderNotProfitableList}">
										<mod:back-order-item template="oci" skin="oci" orderEntry="${orderEntry}"/>
									</c:forEach>
								</c:otherwise>
							</c:choose>

						</div>
					</div>

					<mod:back-order-item-save  htmlClasses="col-12 col-lg-3"/>

				</div>
			</div>
		</div>
</views:page-default-md-full>
