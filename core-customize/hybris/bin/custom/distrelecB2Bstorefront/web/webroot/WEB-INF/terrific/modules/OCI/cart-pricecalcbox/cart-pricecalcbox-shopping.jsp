<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="shoppinglist.calcbox.title" text="Your Cart" var="sTitle"/>
<spring:message code="cart.pricecalcbox.tax" text="Tax" var="sTax"/>
<spring:message code="cart.pricecalcbox.total.payable" text="Total Payable" var="sTotalPayable"/>

<c:set var="cartData" value="${cartData != null ? cartData : orderData}" />

<c:if test="${showTitle}">
	<div class="calc-box calc-box-header">
		<h3 class="title">
			<i class="icon-cart"></i> ${sTitle}
		</h3>
	</div>
</c:if>

<div class="calc-box calc-box-total">
	<div class="calc-row calc-row-total">
		<div class="calc-cell calc-cell-total nth-1">${sTotalPayable}</div>
		<div class="calc-cell calc-cell-total nth-4 calc-total">
			<format:price format="simple" priceData="${currentList.totalPrice}" displayValue="${currentList.totalPrice.value}"  />
		</div>
	</div>
</div>

