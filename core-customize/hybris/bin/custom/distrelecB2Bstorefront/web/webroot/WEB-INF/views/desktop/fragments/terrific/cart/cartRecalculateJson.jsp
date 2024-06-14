<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<json:object>
	<json:object name="cartData">
		<json:property name="totalItems" value="${cartData.totalItems}"/>
		<json:property name="totalPrice">
			<c:set var="price" value="${cartData.totalPrice}" />
			<format:price format="price" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		<json:property name="totalPriceLocal">
			<c:set var="price" value="${cartData.totalPrice}" />
			<format:price format="price" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		<json:property name="subtotal">
			<c:set var="price" value="${cartData.subTotal}" />
			<format:price format="price" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		<json:property name="discount">
			<c:set var="price" value="${cartData.totalDiscounts}" />
			<format:price format="price" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		<c:choose>
		   <c:when test="${not empty cartData.deliveryCost}">
				<json:property name="delivery">
					<format:price format="price" priceData="${cartData.deliveryCost}" displayValue="${cartData.deliveryCost.value}" />
				</json:property>
		   </c:when>
		   <c:otherwise>
			 <json:property name="delivery" value="0"/>
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
  		<json:property name="tax">
			<c:set var="price" value="${cartData.totalTax}" />
			<format:price format="price" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		
		<json:array name="products" items="${cartData.entries}" var="cartEntry">
			<json:object>
				<json:property name="code" value="${cartEntry.product.code}"/>
				<json:property name="name" value="${cartEntry.product.name}"/>
				<json:property name="quantity" value="${cartEntry.quantity}"/>
				<json:property name="basePrice">
					<format:price format="price" priceData="${cartEntry.basePrice}"/>
				</json:property>
				<json:property name="baseListPrice">
					<format:price format="price" priceData="${not empty cartEntry.baseListPrice ? cartEntry.baseListPrice : cartEntry.basePrice}"/>
				</json:property>
                <json:property name="showListPriceLine" value="${cartEntry.baseListPrice.value gt cartEntry.basePrice.value}"/>
				<json:property name="price-currency" value="${price.currencyIso}"/>
				<json:property name="totalPrice">
					<c:set var="price" value="${cartEntry.totalPrice}" />
					<format:price format="price" priceData="${price}" displayValue="${price.value}" />
				</json:property>
				<json:property name="totalListPrice">
					<c:set var="price" value="${not empty cartEntry.totalListPrice ? cartEntry.totalListPrice : cartEntry.totalPrice}" />
					<format:price format="price" priceData="${price}" displayValue="${price.value}" />
				</json:property>
				<c:set var="format" value="cartIcon" />

				<c:set var="images" value="${cartEntry.product.productImages[0]}" />
				<c:set var="primaryImage" value="${not empty images ? images.landscape_small : ''}" />
				<c:choose>
					<c:when test="${not empty primaryImage}">
						<json:property name="thumbUrl" value="${primaryImage.url}" />
						<c:set var="primaryImageWebp" value="${not empty images.landscape_small_webp ? images.landscape_small_webp : primaryImage}" />
						<json:property name="thumbUrlWebp" value="${primaryImageWebp.url}" />
					</c:when>
					<c:otherwise>
						<json:property name="thumbUrl" value="/_ui/all/media/img/missing_portrait_small.png" />
						<json:property name="thumbUrlWebp" value="/_ui/all/media/img/missing_portrait_small.png" />
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
	<c:if test="${not empty errorMsg}">
		<json:object name="errorData">
			<json:property name="msg"><spring:theme code="${errorMsg}" /></json:property>
			<json:property name="msgCode" value="${errorMsg}"/>
		</json:object>
	</c:if>
</json:object>
