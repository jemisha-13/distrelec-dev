<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<spring:theme code="text.addToCart" var="addToCartText"/>
<c:url value="${product.url}" var="productUrl"/>

<ycommerce:testId code="product_wholeProduct">
	<div class="prod_grid">
		<div class="thumb">
			<a href="${productUrl}">
				<product:productPrimaryImage product="${product}" format="thumbnail"/>
			</a>
			<c:if test="${not empty product.potentialPromotions and not empty product.potentialPromotions[0].productBanner}">
				<img class="promo" src="${product.potentialPromotions[0].productBanner.url}" alt="${product.potentialPromotions[0].description}" title="${product.potentialPromotions[0].description}"/>
			</c:if>
		</div>
		<div class="details">
			<ycommerce:testId code="product_productName">
				<a href="${productUrl}">
					<p class="strong prod_grid-name">${product.name}</p>
				</a>
			</ycommerce:testId>
		</div>
		<div class="cart">
			<ycommerce:testId code="product_productPrice">
				<p class="price"><format:price priceData="${product.price}"/></p>
			</ycommerce:testId>
			<c:set var="buttonType">submit</c:set>
			<c:if test="${product.stockLevelStatus.code eq 'outOfStock' }">
				<c:set var="buttonType">button</c:set>
				<spring:theme code="text.addToCart.outOfStock" var="addToCartText"/>
			</c:if>
			<form id="addToCartForm${product.code}" action="<c:url value="/cart/add" />" method="post" class="add_to_cart_form">
				<input type="hidden" name="productCodePost" value="${product.code}"/>
				<c:if test="${not empty product.averageRating}">
					<span class="stars large" style="display: inherit;">
						<span style="width: <fmt:formatNumber maxFractionDigits="0" value="${product.averageRating * 24}" />px;"></span>
					</span>
				</c:if>
				<ycommerce:testId code="product_addProduct_button">
					<button type="${buttonType}" class="positive large <c:if test="${fn:contains(buttonType, 'button')}">out-of-stock</c:if>">
						<theme:image code="img.addToCartIcon" alt="${addToCartText}" title="${addToCartText}"/>
					</button>
				</ycommerce:testId>
			</form>
		</div>
	</div>
</ycommerce:testId>
