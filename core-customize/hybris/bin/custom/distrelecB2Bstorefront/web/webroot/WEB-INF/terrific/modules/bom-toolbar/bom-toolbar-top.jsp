<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isEShopGroup" value="false"/>
<sec:authorize access="hasAnyRole('ROLE_B2BEESHOPGROUP','ROLE_OCICUSTOMERGROUP','ROLE_ARIBACUSTOMERGROUP','ROLE_CXMLCUSTOMERGROUP')">
	<c:set var="isEShopGroup" value="true"/>
</sec:authorize>

<div class="bd cf">
    <div class="_right">
		<c:if test="${fn:length(matchingProducts) gt 0}">
			<a href="#" class="btn btn-primary btn-add-cart"><i></i><spring:message code="bomdataimport.review.add.cart" text="Add to Cart" /></a>
			<c:if test="${not isEShopGroup}">
				<a href="#" class="btn btn-secondary btn-add-shopping"><i></i><spring:message code="bomdataimport.review.add.shoppinglist" text="Add to shoppinglist" /></a>
			</c:if>
		</c:if>
    </div>
</div>
