<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="cart.pricecalcbox.tax" text="Tax" var="sTax"/>
<spring:message code="text.total" text="Total" var="sTotal"/>
<spring:message code="cart.pricecalcbox.subtotal" text="Subtotal" var="sSubtotal"/>

<c:set var="cartData" value="${cartData != null ? cartData : orderData}"/>

<div class="calc-box">
	<table class="calc-box__table">
		<tbody>
			<tr>
				<td>${sSubtotal}</td>
				<td>
                    <format:price format="currency" priceData="${currentList.subTotal}" fallBackCurrency="${currentList.subTotal.currencyIso}"/>
				</td>
				<td class="js-subTotalPrice">
                    <format:price format="price" explicitMaxFractionDigits="2" priceData="${currentList.subTotal}"/>
				</td>
			</tr>

			<tr>
				<td>${sTax}</td>
				<td>
                    <format:price format="currency" priceData="${currentList.totalTax}" fallBackCurrency="${currentList.totalTax.currencyIso}"/>
				</td>
				<td class="js-totalTax">
                    <format:price format="price" explicitMaxFractionDigits="2" priceData="${currentList.totalTax}"/>
				</td>
			</tr>

			<tr>
				<td><strong>${sTotal}</strong></td>
				<td>
					<strong>
                        <format:price format="currency" priceData="${currentList.totalPrice}" fallBackCurrency="${currentList.totalPrice.currencyIso}"/>
					</strong>
				</td>
				<td>
					<strong class="js-totalPrice">
						<format:price format="price" explicitMaxFractionDigits="2" priceData="${currentList.totalPrice}"/>
					</strong>
				</td>
			</tr>
		</tbody>
	</table>
</div>
