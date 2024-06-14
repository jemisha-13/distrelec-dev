(function ($) {

	/**
	 * Cart Skin implementation for the module Toolsitem.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.Cart = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			this.$icoCart = this.$ctx.find('.ico-cart');

			// Context & Click Handler
			this.onClick = $.proxy(this, 'onClick');
			this.$ctx.on('click', this.$icoCart, this.onClick);

			// calling parent method
			parent.on(callback);

		};
		this.onClick = function (ev) {
			ev.preventDefault();

			this.$icoCart.addClass('active');
			var $target = $(ev.target)
				, $product = $target.closest('.mod-product')
				, queryParam = ""
				;

			// if toolsitem is placed on a mod-product, we take the query param from the url to send it to the backend for FactFinder Tracking
			if($product.length === 1){
				var url = Tc.Utils.splitUrl(document.URL);
				if(url.get !== undefined){
					queryParam = url.get.q;
				}
			}

			// If the toolsitem cart is not placed on a mod-product, than get it from mod-hero-teaser-item
			if ($product.length === 0) {
				$product = $target.closest('.mod-hero-teaser-item');
			}

			// If the toolsitem cart is not placed on a mod-product or mod-hero-teaser-item, get it from mod-carpet-item
			if ($product.length === 0) {
				$product = $target.closest('.mod-carpet-item');
			}

			// If the toolsitem cart is not placed on a mod-product or mod-hero-teaser-item or mod-carpet-item, get it from mod-carpet-new-item
			if ($product.length === 0) {
				$product = $target.closest('.mod-carpet-new-item');
			}

			// If the toolsitem cart is not placed on a mod-product or mod-hero-teaser-item or mod-carpet-item or mod-carpet-new-item, get it from mod-carousel-teaser
			if ($product.length === 0) {
				$product = $target.closest('.mod-carousel-teaser-item');
			}
			this.triggerCart($product, queryParam);

		};

        this.triggerCart = function ($product, queryParam) {

            var productCode = $product.find('.hidden-product-code').val().replace(/\D/g,'');

            var productPrice = 1;
            var productQty = 1;
            var productValue = parseFloat(productPrice * productQty).toFixed(2);
            var currency = digitalData.page.pageInfo.currency;
            var dyQuantity = parseInt(productQty);

            // Trigger Cart API to add to cart

			if(!document.getElementsByTagName('body')[0].classList.contains('skin-layout-compare')) {
				$(document).trigger('cart', {
					actionIdentifier: 'toolsitemAddToCart',
					type: 'add',
					productCodePost: productCode,
					qty: 0 // backend magic: we send 0 and the backend automatically set it to the minimum quantity
				});
			}
        };

		this.after = function () {
			// calling parent method
			parent.after();
		};
	};

})(Tc.$);
