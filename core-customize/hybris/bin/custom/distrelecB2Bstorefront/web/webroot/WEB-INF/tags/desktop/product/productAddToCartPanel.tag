<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="allowAddToCart" required="true" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<div class="prod_add_to_cart">
	<form id="addToCartForm" class="add_to_cart_form" action="<c:url value="/cart/add"/>" method="post">
		<c:if test="${product.purchasable}">
			<label for="qty"><spring:theme code="basket.page.quantity" /></label>
			<input type="text" size="1" id="qty" name="qty" class="qty" value="1">
		</c:if>
		<input type="hidden" name="productCodePost" value="${product.code}"/>

		<c:if test="${product.purchasable and product.stockLevel le 0}">
			<c:set var="productStockLevel"><spring:theme code="product.variants.out.of.stock"/></c:set>
		</c:if>
		<c:if test="${product.stockLevel gt 0}">
			<c:set var="productStockLevel">${product.stockLevel}&nbsp;<spring:theme code="product.variants.in.stock"/></c:set>
		</c:if>
		<c:if test="${product.stockLevelStatus.code eq 'inStock' and product.stockLevel le 0}">
			<c:set var="productStockLevel"><spring:theme code="product.variants.available"/></c:set>
		</c:if>

		<ycommerce:testId code="productDetails_productInStock_label">
			<p class="stock_message">${productStockLevel}</p>
		</ycommerce:testId>

		<c:set var="buttonType">button</c:set>
		<c:if test="${allowAddToCart and product.purchasable and product.stockLevelStatus.code ne 'outOfStock' }">
			<c:set var="buttonType">submit</c:set>
		</c:if>

		<spring:theme code="text.addToCart" var="addToCartText"/>
		<button type="${buttonType}" class="positive large <c:if test="${fn:contains(buttonType, 'button')}">out-of-stock</c:if>">
			<spring:theme code="text.addToCart" var="addToCartText"/>
			<spring:theme code="basket.add.to.basket" />
		</button>

	</form>
</div>
