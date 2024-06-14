<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>


<div class="c-center">
	<div class="c-center-content">
		<div class="c-center-vertical">
			<p><spring:message code="compare.item.product" /></p>
			<h3 class="title">${product.name}</h3>
			<c:set var="formattedDate">
				<fmt:formatDate value="${product.endOfLifeDate}" />
			</c:set>
			<c:choose>
				<c:when test="${not empty product.endOfLifeDate}">
					<p><spring:message code="compare.item.expired.on" arguments="${formattedDate}" /><br></p>
					<c:if test="${not empty product.replacementReason}">
						<p><spring:message code="compare.item.expired.reason" arguments="${product.replacementReason}" /></p>
					</c:if>
				</c:when>
				<c:when test="${not product.buyable}">
					<p><spring:message code="compare.item.phaseOut.reason" arguments="${formattedDate}" /><br></p>
				</c:when>
			</c:choose>
		</div>
	</div>
</div>

