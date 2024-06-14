<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:theme code="base.close" text="Close" var="sClose" />
<spring:theme code="metahd.cart" text="Cart" var="sCart" />
<spring:theme code="metahd.cart-new" text="Cart" var="sCartNew" />
<spring:theme code="metahd.cart.emptyCart" text="Your shopping cart is empty." var="sEmptyCart" />
<spring:theme code="metahd.cart.tooManyProducts" text="You have {{=it.totalItems}} products in your cart, {{=it.totalItemsSingle}} single items." var="sTooManyProducts" />
<spring:theme code="metahd.cart.view.checkout" text="Go to Cart" var="sViewCheckout" />
<spring:theme code="metahd.cart.viewCart" text="View Cart" var="sViewCart" />
<spring:theme code="metahd.cart.priceExclVAT" text="Prices excl. taxes" var="sPriceExclVAT" />
<spring:theme code="metahd.cart.priceInclVAT" text="Prices incl. taxes" var="sPriceInclVAT" />
<spring:theme code="metahd.cart.oneClickBuy" text="1-click Buy" var="sOneClickBuy" />
<spring:theme code="metahd.cart.items" text="Items" var="sItems" />
<spring:theme code="metahd.cart.item" text="Item" var="sItem" />
<spring:theme code="metahd.cart.quantity" text="Quantity" var="sQuantity" />
<c:url var="goToCartUrl" value="/cart"/>
<c:url var="oneClickBuyUrl" value="/oneclickbuy"/>

<div class="menuitem-wrapper menuitem-wrapper--${currentCountry.isocode}">
	<a class="menuitem popover-origin" href="${goToCartUrl}">
		<span class="vh">${sCartNew}</span>
		<span class="icon-cart"><i class="fas fa-shopping-cart"></i></span>
		<span class="label">
			<span class="label__text">${sCart}</span>
			(<span class="product-count">0</span>)
		</span>
	</a>
	<section class="flyout">
		<div class="flyout-body">
			<div class="hd">
				<span class="product-count bold"></span>&nbsp;<span class="products">${sItems}</span><span class="product hidden">${sItem}</span>
			</div>
			<div class="bd"><%-- Container for the Dot Templates --%></div>
			<div class="flyout__fade">
				<div class="cart-buttons">
					<a href="${goToCartUrl}" class="btn btn-primary btn-go-to-cart">${sViewCheckout} <i class="fa fa-angle-right" aria-hidden="true"></i></a>
				</div>
			</div>
		</div>
	</section>
</div>
<%-- Flyout Body Templates --%>
<script id="template-metahd-item-cart-is-empty" type="text/x-template-dotjs">

</script>
<script id="template-metahd-item-cart-product-item" type="text/x-template-dotjs">
	<div data-href="{{=it.productUrl}}">
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
	</div>
</script>