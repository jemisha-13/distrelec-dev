<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<json:object>
	<json:object name="cartData">
		<json:property name="total" value="${cartData.totalPrice.value}"/>
		<json:array name="products" items="${cartData.entries}" var="cartEntry">
			<json:object>
				<json:property name="code" value="${cartEntry.product.code}"/>
				<json:property name="name" value="${cartEntry.product.name}"/>
				<json:property name="quantity" value="${cartEntry.quantity}"/>
				<json:property name="price" value="${cartEntry.basePrice.value}"/>
				<json:array name="categories" items="${cartEntry.product.categories}" var="category">
					<json:property value="${category.name}"/>
				</json:array>
			</json:object>
		</json:array>
	</json:object>

	<json:property name="cartPopupHtml" escapeXml="false">
		<spring:theme code="text.addToCart" var="addToCartText"/>
		<c:url value="/cart" var="cartUrl"/>
		<c:url value="/cart/checkout" var="checkoutUrl"/>

		<div class="title">
			<theme:image code="img.addToCartIcon" alt="${addToCartText}" title="${addToCartText}"/>
			<h3> <spring:theme code="basket.added.to.basket" /></h3>
			<a href="#" class="close" id="add_to_cart_close"></a>
		</div>

		<div class="cart_modal_popup">
			<div class="cart_popup_error_msg"><spring:theme code="${errorMsg}" /></div>
			<div class="prod_image">
				<product:productPrimaryImage product="${product}" format="cartIcon"/>
			</div>
			<div class="prod_info">
				<p>${product.name}</p>
				<p class="prod_options">
					<c:forEach items="${product.baseOptions}" var="baseOptions">
						<c:forEach items="${baseOptions.selected.variantOptionQualifiers}" var="baseOptionQualifier">
							<c:if test="${baseOptionQualifier.qualifier eq 'style' and not empty baseOptionQualifier.image.url}">
								<span class="prod_color">
									<spring:theme code="product.variants.colour"/>&nbsp;
									<img src="${baseOptionQualifier.image.url}" alt="${baseOptionQualifier.value}" title="${baseOptionQualifier.value}"/>
								</span>
							</c:if>
							<c:if test="${baseOptionQualifier.qualifier eq 'size'}">
								<span class="prod_size"><spring:theme code="product.variants.size"/>&nbsp;${baseOptionQualifier.value}</span>
							</c:if>
						</c:forEach>
					</c:forEach>
				</p>
				<p class="prod_quantity"><spring:message code="popup.cart.quantity.added"/>&nbsp;${quantity}</p>
				<div class="prod_price">
					<p><format:price priceData="${entry.basePrice}"/></p>
				</div>
			</div>
		</div>

		<div class="links">
			<a href="${cartUrl}" class="neutral large">
				<spring:theme code="basket.view.basket" />
			</a>
			<a href="${checkoutUrl}" class="positive large">
				<theme:image code="img.addToCartIcon" alt="${addToCartText}" title="${addToCartText}"/>
				<spring:theme code="checkout.checkout" />
			</a>
		</div>
	</json:property>
</json:object>
