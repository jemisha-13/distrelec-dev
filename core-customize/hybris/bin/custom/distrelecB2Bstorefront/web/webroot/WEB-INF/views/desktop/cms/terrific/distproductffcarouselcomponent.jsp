<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="false"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- CarouselData is delivered later via AJAX --%>
<spring:message code="cart.recommendations.title" var="sTitle"/>

<c:choose>
	<c:when test="${component.recommendationType eq 'ALSOBOUGHT'}">
		<mod:product-card
				template="product-alsobought"
				skin="product-alsobought"
				title="${component.title}"
				maxNumberToDisplay="4"

		/>
	</c:when>
	<c:when test="${(component.recommendationType eq 'RECOMMENDATION') and ( fn:containsIgnoreCase(request.getRequestURI(), '/cart') )}">
		<mod:product-card
				template="product-recommendations-cart"
				skin="product-recommendations-cart"
				maxNumberToDisplay="4"
				title="${sTitle}"
		/>
	</c:when>		
	<c:otherwise>
		<mod:product-card
				template="product-recommendations"
				skin="product-recommendations"
				maxNumberToDisplay="4"
				title="${component.title}"
		/>
	</c:otherwise>


</c:choose>



