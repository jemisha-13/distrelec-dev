<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%-- This Json is used for the initial on Pageload AJAX Call to get the meta hd cart --%>

<c:set var="statusValue" value="${not empty status ? status :'ok' }" />

<json:object>
	<json:property name="status" value="${statusValue}"/>
	<c:if test="${not empty errorMsg}">
		<json:property name="errorMsg">
			<spring:theme code="${errorMsg}" text="Unknown error occurs during the request processing" />
		</json:property>	
	</c:if>
	<json:object name="cartData">
		<json:property name="totalItems" value="${cartData.totalItems}"/>
		<json:property name="totalItemsSingle" value="${cartData.totalItemsSingle}"/>
		<json:property name="totalPrice" value="${cartData.totalPrice.value}"/>
		<json:property name="discount" value="${cartData.totalDiscounts.value}"/>
		<json:property name="deliveryCostExcluded" value="${cartData.deliveryCostExcluded}"/>
		<c:choose>
			<c:when test="${not empty cartData.deliveryCost && cartData.deliveryCost.value != '' }">
				<json:property name="deliveryCost" value="${cartData.deliveryCost.value}"/>
			</c:when>
			<c:otherwise>
				<json:property name="deliveryCost" value="0.00"/>
			</c:otherwise>
		</c:choose>		
		
		<c:choose>
			<c:when test="${not empty cartData.erpVoucherInfoData && cartData.erpVoucherInfoData.valid  }">
				<json:property name="voucherValue" value="${cartData.erpVoucherInfoData.fixedValue}"/>
			</c:when>
			<c:otherwise>
				<json:property name="voucherValue" value="0.00"/>
			</c:otherwise>
		</c:choose>
		
  		<c:choose>
		   <c:when test="${not empty cartData.paymentCost}">
				<json:property name="payment">
					<format:price format="price" priceData="${cartData.paymentCost}" displayValue="${cartData.paymentCost.value}" />
				</json:property>
		   </c:when>
		   <c:otherwise>
			 <json:property name="payment" value="0"/>
		   </c:otherwise>
  		</c:choose>		
		
		<json:property name="totalPriceLocal">
			<c:set var="price" value="${cartData.totalPrice}" />
			<format:price format="price" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		<json:property name="currency">
			<c:set var="price" value="${cartData.totalPrice}" />
			<format:price format="currency" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		<json:property name="tax">
			<c:set var="price" value="${cartData.totalTax}" />
			<format:price format="price" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		<json:array name="products" items="${cartData.entries}" var="cartEntry">
			<json:object>
				<json:property name="code" value="${cartEntry.product.code}"/>
				<json:property name="name" value="${cartEntry.product.name}"/>
				<json:property name="quantity" value="${cartEntry.quantity}"/>
				<json:property name="customerReference" value="${cartEntry.customerReference}"/>
				<json:property name="price" value="${cartEntry.basePrice.value}"/>
				<json:property name="quotationId" value="${cartEntry.quotationId}"/>
				<json:property name="isquotation" value="${cartEntry.isQuotation}"/>
				<json:property name="lineNumber" value="${cartEntry.lineNumber}"/>
				<json:property name="entryNumber" value="${cartEntry.entryNumber}"/>
				<json:array name="availabilities" items="${cartEntry.availabilities}" var="availability">
					<json:property value="${availability.estimatedDate}"/>
					<json:property value="${availability.quantity}"/>
				</json:array>
				<json:property name="quotationReference" value="${cartEntry.quotationReference}"/>
				<json:property name="priceLocal">
					<c:set var="price" value="${cartEntry.basePrice}" />
					<format:price format="price" priceData="${price}" displayValue="${price.value}" />
				</json:property>
				<json:property name="totalPrice" value="${cartEntry.totalPrice.value}"/>
				<json:property name="totalPriceLocal">
					<c:set var="price" value="${cartEntry.totalPrice}" />
					<format:price format="price" priceData="${price}" displayValue="${price.value}" />
				</json:property>

				<c:set var="images" value="${cartEntry.product.productImages[0]}" />
				<c:set var="primaryImage" value="${not empty images ? images.landscape_small : ''}" />
				<c:choose>
					<c:when test="${not empty primaryImage}">
						<json:property name="thumbUrl" value="${primaryImage.url}" />
						<c:set var="primaryImageWebp" value="${not empty images.landscape_small_webp ? images.landscape_small_webp : primaryImage}" />
						<json:property name="thumbUrlWebp" value="${primaryImageWebp.url}" />
					</c:when>
					<c:otherwise>
						<json:property name="thumbUrl" value="/_ui/all/media/img/missing_landscape_small.png" />
						<json:property name="thumbUrlWebp" value="/_ui/all/media/img/missing_landscape_small.png" />
					</c:otherwise>
				</c:choose>
				<json:property name="productUrl">
					<c:url value="${cartEntry.product.url}" />
				</json:property>
				<json:array name="categories" items="${cartEntry.product.categories}" var="category">
					<json:property value="${category.name}"/>
				</json:array>
			</json:object>
		</json:array>
	</json:object>
</json:object>
