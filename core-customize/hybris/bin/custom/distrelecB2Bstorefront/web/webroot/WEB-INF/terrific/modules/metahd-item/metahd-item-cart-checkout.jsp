<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:theme code="base.close" text="Close" var="sClose" />
<spring:theme code="metahd.cart" text="Cart" var="sCart" />
<spring:theme code="metahd.cart-new" text="Cart" var="sCartNew" />
<spring:theme code="metahd.cart.emptyCart" text="Your shopping cart is empty." var="sEmptyCart" />
<spring:theme code="metahd.cart.tooManyProducts" text="You have {{=it.totalItems}} products in your cart, {{=it.totalItemsSingle}} single items." var="sTooManyProducts" />
<spring:theme code="metahd.cart.goToCart" text="Go to Cart" var="sGoToCart" />
<spring:theme code="metahd.cart.viewCart" text="View Cart" var="sViewCart" />
<spring:theme code="metahd.cart.priceExclVAT" text="Prices excl. taxes" var="sPriceExclVAT" />
<spring:theme code="metahd.cart.priceInclVAT" text="Prices incl. taxes" var="sPriceInclVAT" />
<spring:theme code="metahd.cart.oneClickBuy" text="1-click Buy" var="sOneClickBuy" />
<spring:theme code="metahd.cart.items" text="Items" var="sItems" />
<spring:theme code="metahd.cart.item" text="Item" var="sItem" />
<spring:theme code="metahd.cart.quantity" text="Quantity" var="sQuantity" />
<c:url var="goToCartUrl" value="/cart"/>
<c:url var="oneClickBuyUrl" value="/oneclickbuy"/>

<div class="minicart">
	<div class="minicart__items">
		<p class="minicart__items__count">
			<i class="fa fa-shopping-cart"></i> <span>&nbsp;</span> ${sItems}
		</p>
	</div>
	<div class="minicart__price">
		<span class="minicart__price__currency">&nbsp;</span>
		<span class="minicart__price__figure">&nbsp;</span>
	</div>
	<a class="minicart__link" href="${goToCartUrl}">(View Cart)</a>
</div>

<script id="template-metahd-item-cart-is-empty" type="text/x-template-dotjs"></script>
<script id="template-metahd-item-cart-product-item" type="text/x-template-dotjs">
	<a href="{{=it.productUrl}}" class="product-row">
		<div class="product-wrapper">
			<picture>
				<source sourceset="{{=it.thumbUrlWebp}}">
				<img class="product-image" src="{{=it.thumbUrl}}" alt="{{=it.name}} ({{=it.quantity}})" />
			</picture>
			<p>
				{{=it.name}}
				<span class="qty">${sQuantity} {{=it.quantity}}</span>
			</p>
		</div>
	</a>
</script>
