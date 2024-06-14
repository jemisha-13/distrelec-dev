<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--
	Assigning these strings to variables is optional, but it helps make the markup readable.
	However, the tag can also be used directly without a var attribute.
--%>
<spring:theme code="base.products" text="Products" var="sProducts" />
<spring:theme code="base.categories" text="Categories" var="sCategories" />
<spring:theme code="base.manufacturers" text="Manufacturers" var="sManufacturers" />
<spring:theme code="base.show-all" text="Show All" var="sShowAll" />
<spring:theme code="base.add-to-cart" text="Add To Cart" var="sAddToCart" />
<spring:theme code="base.increase-quantity" text="Increase Quantity" var="sIncreaseQuantity" />
<spring:theme code="base.decrease-quantity" text="Decrease Quantity" var="sDecreaseQuantity" />
<spring:theme code="base.categories.in" text="in" var="sIn" />

<spring:theme code="metahd.suggest.searchTerms" text="Search Terms" var="sSearchTerms" />
<spring:theme code="metahd.suggest.Art-Nr" text="Type" var="sType" />
<spring:theme code="product.articleNumber" text="Art-Nr" var="sArtNr" />
<spring:theme code="metahd.suggest.manufacturerBrands" text="Manufacturer / Brands" var="sManufacturerBrands" />
<spring:theme code="product.energy.efficiency.class" text="Energy efficiency class" var="sEEC" />

<c:url var="searchUrl" value="/search/?q="/>

<div id="suggest-target"></div>

<%-- search result template --%>
<script id="template-suggest" type="text/x-template-dotjs">
	<div class="container-suggest row">
		<div class="content">
			<div class="left">
				<div class="suggest-section terms">
					<h3>${sSearchTerms}</h3>
					<ul class="js-results"></ul>
				</div>
				<div class="suggest-section cats">
					<h3>${sCategories}</h3>
					<ul class="js-results"></ul>
				</div>
				<div class="suggest-section mans">
					<h3>${sManufacturerBrands}</h3>
					<ul class="js-results"></ul>
				</div>
			</div>
			<div class="right">
				<div class="suggest-section prods">
					<h3>${sProducts}
						<span class="suggest-section__text">
							${sIn}
							<span class="suggest-category-text">&nbsp;</span>
						</span>
					</h3>
					<div class="js-results"></div>
				</div>
			</div>
		</div>
	</div>
</script>

<script id="template-suggest-row-terms" type="text/x-template-dotjs">

		<div data-href="/search?q={{=it.name}}&queryFromSuggest=true">
			<span class="suggestion ellipsis" title="{{=it.name}}">{{=it.name}}</span>
		</div>

</script>

<script id="template-suggest-row-product" type="text/x-template-dotjs">

	<div data-href="{{=it.uri}}?queryFromSuggest=true">
		<div class="product-wrapper">
			<div class="product-image">
				{{?it.imgUri}}<img src="{{=it.imgUri}}">{{?}}
			</div>
			<div class="product-row">
				<span class="product-name ellipsis title-text" title="{{=it.name}}">  {{=it.name}}  </span>
				<span class="product-price text-highlight">{{=it.currencyIso}} <span class="inner-price">{{=it.price}}</span></span>
			</div>

			<div class="product-row last">
				<div class="product-details">
					<span class="ellipsis"><span class="bold">${sArtNr}</span> {{=it.productCodeErp}}</span>
					<span class="ellipsis" title="{{=it.typeName}}"><span class="bold">${sType}</span> {{=it.typeName}}</span>
				</div>
				<div class="product-cart">
					<form:form class="suggest-row-cart mod mod-metahd-suggest-item-cart-logic" method="GET">

						<!-- Used in metahd-suggest-item-cart-logic module -->
						<input type="hidden" class="hidden-product-code" value="{{=it.productCodeErp}}" />
						<input type="hidden" class="hidden-product-price" value="{{=it.price}}" />
						<!-- End metahd-suggest-item-cart-logic module -->

						<div class="add-cart-content">
							<div class="numeric"
		                        data-min="{{=it.minQuantity}}"
		                        data-step="{{=it.step}}"
		                        data-min-error='<spring:message code="buyingSection.error.min.order.quantity" arguments="{{=it.productCodeErp}},{{=it.minQuantity}}" htmlEscape="true"/>'
		                        data-step-error='<spring:message code="buyingSection.error.step.order.quantity" arguments="{{=it.step}}" htmlEscape="true"/>'
		                    >
								<button class="btn numeric-btn numeric-btn-down hook-cart-decrease disabled" title="${sDecreaseQuantity}"><i class="fa fa-minus"></i></button>
		                        <input type="text" name="countItems" class="ipt" placeholder="{{=it.minQuantity}}" value="{{=it.minQuantity}}">
								<button class="btn numeric-btn numeric-btn-up hook-cart-increase" title="${sIncreaseQuantity}"><i class="fa fa-plus"></i></button>
		                        <div class="numeric-popover popover top">
		                            <div class="arrow"></div>
		                            <div class="popover-content"></div>
		                        </div>
		                    </div>
							<button type="submit" class="hook-cart-add"
									data-aainteraction="add to cart searchbox"
									data-product-code="{{=it.productCodeErp}}"
									title="${sAddToCart}"><i class="fa fa-shopping-cart"></i></button>
						</div>
					</form:form>
				</div>
			</div>
		</div>
		{{? it.energyEfficiencyData.length === 2 }}
			<mod:energy-efficiency-label skin="suggestion"
										 productCode="{{= it.productCode }}"
										 productEnergyEfficiency="{{= it.energyEfficiencyData[0] }}"
										 productManufacturer="{{= it.manufacturer }}"
										 productType="{{= it.typeName }}"
										 productEnergyPower="{{= it.energyEfficiencyData[1] }}"
			/>
		{{?}}
	</div>
</script>

<script id="template-suggest-row-category" type="text/x-template-dotjs">

		<div data-href="{{=it.uri}}?queryFromSuggest=true">
			<span class="suggestion ellipsis" title="{{=it.name}}">{{=it.name}}
				<i class="fas fa-angle-right"></i>
			</span>
		</div>

</script>

<script id="template-suggest-row-manufacturer" type="text/x-template-dotjs">

		<div data-href="{{=it.uri}}?queryFromSuggest=true">
			<div class="suggestion">
				<span class="manufacturer-name ellipsis">{{=it.name}}
					<i class="fas fa-angle-right"></i>
				</span>
				{{?it.imgUri}}<img src="{{=it.imgUri}}" />{{?}}
			</div>
		</div>

</script>
