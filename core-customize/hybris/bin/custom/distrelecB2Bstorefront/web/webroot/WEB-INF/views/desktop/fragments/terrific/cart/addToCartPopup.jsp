<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="cart.image.missing" text="Image not found" var="sImageMissing"/>

<json:object>
	<json:property name="updatedEntry" value="${updatedEntry}"/>
	<json:object name="cartData">
		
		<%-- General Properties --%>
		<json:property name="totalItems" value="${cartData.totalItems}"/>
		<json:property name="totalItemsSingle" value="${cartData.totalItemsSingle}"/>
		<json:property name="totalPrice">
			<c:set var="price" value="${cartData.totalPrice}" />
			<format:price format="price" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		<json:property name="totalPriceLocal">
			<c:set var="price" value="${cartData.totalPrice}" />
			<format:price format="price" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		<json:property name="addedPrice" value="${price.value}"/>
		<json:property name="addedPriceLocal">
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
		<json:property name="subTotal">
			<c:set var="price" value="${cartData.subTotal}" />
			<format:price format="price" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		<json:property name="subTotalLocal">
			<c:set var="price" value="${cartData.subTotal}" />
			<format:price format="price" priceData="${price}" displayValue="${price.value}" />
		</json:property>
		<json:property name="deliveryCost">
			<format:price format="price" priceData="${cartData.deliveryCost}" displayValue="${cartData.deliveryCost.value}" />
		</json:property>
		
		
		<%-- General Properties --%>
	
		<%-- Properties only set in update cart on cart page --%>
		<%-- quantity is the difference +/- pieces when action was update quantity in cart --%>
		<json:property name="updateQuantity" value="${updateQuantity}"/>
		<%-- Properties only set in update cart on cart page --%>
	
		<%-- Properties only set in single add to cart --%>
		<%-- quantity is number of pieces in single add --%>
		<json:property name="addedQuantity" value="${addedQuantity}"/>
		<%-- Properties only set in single add to cart --%>
		
		<%-- Properties only set in add to cart bulk --%>
		<%-- addedProducts Count contains only really added Products, no error products --%>
		<json:property name="addedProductsCount" value="${addedProducts}"/>
		<json:array name="products" items="${cartData.entries}" var="cartEntry">
			<json:object>
                <json:property name="manufacturer" value="${cartEntry.product.distManufacturer.name}"/>
				<json:property name="typeName" value="${cartEntry.product.typeName}"/>
				<json:property name="code" value="${cartEntry.product.code}"/>
				<json:property name="codeErpRelevant" value="${cartEntry.product.codeErpRelevant}" />
				<json:property name="name" value="${cartEntry.product.name}"/>
				<json:property name="orderQuantityMinimum" value="${cartEntry.product.orderQuantityMinimum}"/>
				<json:property name="orderQuantityStep" value="${cartEntry.product.orderQuantityStep}"/>
				<json:property name="quantity" value="${cartEntry.quantity}"/>
				<json:property name="price">
					<c:set var="price" value="${cartEntry.basePrice}" />
					<format:price format="price" priceData="${price}" displayValue="${price.value}" />
				</json:property>
				<json:property name="priceLocal">
					<c:set var="price" value="${cartEntry.basePrice}" />
					<format:price format="price" priceData="${price}" displayValue="${price.value}" />
				</json:property>
				<json:property name="totalPrice">
					<c:set var="price" value="${cartEntry.totalPrice}" />
					<format:price format="price" priceData="${price}" displayValue="${price.value}" />
				</json:property>
				<json:property name="totalPriceLocal">
					<c:set var="price" value="${cartEntry.totalPrice}" />
					<format:price format="price" priceData="${price}" displayValue="${price.value}" />
				</json:property>
				<json:property name="currency" value="${cartEntry.totalPrice.currencyIso}"/>

				<c:set var="images" value="${cartEntry.product.productImages[0]}" />
				<c:set var="primaryImage" value="${not empty images ? images.landscape_small : ''}" />
				<c:choose>
					<c:when test="${not empty primaryImage}">
						<json:property name="thumbUrl" value="${primaryImage.url}" />
						<json:property name="thumbUrlAlt" value="${cartEntry.product.name}" />
						<c:set var="primaryImageWebp" value="${not empty images.landscape_small_webp ? images.landscape_small_webp : primaryImage}" />
						<json:property name="thumbUrlWebp" value="${primaryImageWebp.url}" />
					</c:when>
					<c:otherwise>
						<json:property name="thumbUrl" value="/_ui/all/media/img/missing_landscape_small.png" />
						<json:property name="thumbUrlAlt" value="${sImageMissing}" />
						<json:property name="thumbUrlWebp" value="/_ui/all/media/img/missing_landscape_small.png" />
					</c:otherwise>
				</c:choose>



				<json:property name="productUrl">
					<c:url value="${cartEntry.product.url}" />
				</json:property>
				<json:array name="categories" items="${cartEntry.product.categories}" var="category">
					<json:property value="${category.name}"/>
				</json:array>
				<json:array name="productLabel" items="${cartEntry.product.activePromotionLabels}" var="promotion">
					<json:object>
						<json:property name="code" value="${promotion.code}"/>
						<json:property name="label" value="${promotion.label}"/>
					</json:object>
				</json:array>
			</json:object>
		</json:array>
		<%-- Properties only set in add to cart bulk --%>
		
	</json:object>

	<c:if test="${not empty errorProducts || not empty errorMsg}">
		<json:object name="errorData">
			
			<%-- Properties only set in add to cart bulk --%>
			<json:array name="errorProducts">
				<c:forEach var="errorProduct" items="${errorProducts}">
					<json:object>
						<json:property name="errorMessage">
							<spring:theme code="${errorProduct.key}" />
						</json:property>
						<json:array name="products" var="product" items="${errorProduct.value}">
							<json:property name="productCode" value="${product.code}" />
						</json:array>
					</json:object>
				</c:forEach>
			</json:array>
			<%-- Properties only set in add to cart bulk --%>

			<c:choose>

				<c:when test="${errorMsg eq 'cart.product.error.punchout' or errorMsg eq 'sap.catalog.order.articles.error' or fn:indexOf(errorMsg, '.information') != -1}">
					<json:property name="msgType" value="info" />
					<json:property name="msgTitle">
						<spring:theme code="lightboxstatus.info.title" />
					</json:property>
				</c:when>

				<c:otherwise>
					<json:property name="msgType" value="error" />
					<json:property name="msgTitle">
						<spring:theme code="lightboxstatus.error.title" />
					</json:property>
				</c:otherwise>

			</c:choose>

			<c:choose>
				<c:when test="${errorMsg eq 'validation.error.min.order.quantity' or errorMsg eq 'validation.error.steps.order.quantity'}">
					<%-- Variables are needed for the direct order feature for quantity errors on the cart page --%>
					<json:property name="isMinQuantityOrStepError" value="true"/>
					
					<json:property name="minQuantity" value="${product.orderQuantityMinimum}"/>
					<json:property name="stepQuantity" value="${product.orderQuantityStep}"/>
					
					<json:property name="minQuantityErrorMessage">
						<spring:theme code="validation.error.min.order.quantity" arguments="${product.codeErpRelevant},${product.orderQuantityMinimum}" />
					</json:property>
					<json:property name="stepQuantityError">
						<spring:theme code="validation.error.steps.order.quantity" arguments="${product.codeErpRelevant},${product.orderQuantityStep}" />
					</json:property>
					<%-- Variables are needed for the direct order feature for quantity errors on the cart page --%>
				</c:when>
				<c:when test="${errorMsg eq 'product.notFound'}">
					<%-- Variables are needed for the direct order feature for unknown product code error on the cart page --%>
					<json:property name="isUnknownProductIdentifierError" value="true"/>
					<json:property name="unknownProductIdentifierErrorMessage">
						<spring:theme code="${errorMsg}" />
					</json:property>
					<%-- Variables are needed for the direct order feature for unknown product code error on the cart page --%>
				</c:when>
				<c:otherwise>
					<json:property name="msg">
						<spring:theme code="${errorMsg}" />
					</json:property>
				</c:otherwise>
			</c:choose>
			<json:property name="msgCode" value="${errorMsg}"/>
		</json:object>
	</c:if>
	<c:if test="${not empty replacedEntry}">
		<json:object name="modification">
			<json:property name="updatedEntry" value="${replacedEntry}" />
		</json:object>
	</c:if>
</json:object>
