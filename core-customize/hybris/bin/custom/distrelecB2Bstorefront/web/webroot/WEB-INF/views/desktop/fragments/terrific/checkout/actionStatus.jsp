<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<json:object>
	<json:object name="action">
		<c:choose>
			<c:when test="${actionStatus}">
				<json:property name="status" value="true" />
			</c:when>
			<c:otherwise>
				<json:property name="status" value="false" />
			</c:otherwise>
		</c:choose>
	</json:object>
	<json:object name="deliveryCost">
		<json:property name="currency">
			<format:price format="currency" priceData="${cartData.deliveryCost}" fallBackCurrency="${cartData.totalPrice.currencyIso}" />
		</json:property>

        <c:choose>
            <c:when test="${cartData.deliveryCost.value gt 0}">
                <json:property name="price">
                    <format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.deliveryCost}" />
                </json:property>
            </c:when>
            <c:otherwise>
                <json:property name="price">
                    <spring:message code="checkout.summary.deliveryCost.free" />
                </json:property>
            </c:otherwise>
        </c:choose>


	</json:object>
	<json:object name="totalTax">
		<json:property name="currency">
			<format:price format="currency" priceData="${cartData.totalTax}" fallBackCurrency="${cartData.totalPrice.currencyIso}" />
		</json:property>
		<json:property name="price">
			<format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.totalTax}" />
		</json:property>
	</json:object>
	<json:object name="totalPrice">
		<json:property name="currency">
			<format:price format="currency" priceData="${cartData.totalPrice}" fallBackCurrency="${cartData.totalPrice.currencyIso}" />
		</json:property>
		<json:property name="price">
			<format:price format="price" explicitMaxFractionDigits="2" priceData="${cartData.totalPrice}" />
		</json:property>
	</json:object>		
	<json:object name="deliveryMode">
		<json:property name="name">
			<spring:message code="${cartData.deliveryMode.translationKey}" text="${cartData.deliveryMode.name}" />
		</json:property>
	</json:object>				
</json:object>
