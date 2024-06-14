<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="false"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<c:if test="${not empty productReferencesCarouselData}">
	<mod:product-card
			template="product-consistentwith"
			skin="product-consistentwith"
			title="${component.title}"
			maxNumberToDisplay="4"
			carouselData="${productReferencesCarouselData}"
	/>
</c:if>

