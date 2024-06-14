<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<dt>
	<ycommerce:testId code="miniCart_items_label">
		<spring:theme text="items" code="cart.items" arguments="${totalItems}"/>
	</ycommerce:testId>
	-
</dt>
<dd>
	<ycommerce:testId code="miniCart_total_label">
		<c:if test="${totalDisplay == 'TOTAL'}">
			<format:price priceData="${totalPrice}"/>
		</c:if>
		<c:if test="${totalDisplay == 'SUBTOTAL'}">
			<format:price priceData="${subTotal}"/>
		</c:if>
		<c:if test="${totalDisplay == 'TOTAL_WITHOUT_DELIVERY'}">
			<format:price priceData="${totalNoDelivery}"/>
		</c:if>
	</ycommerce:testId>
</dd>
