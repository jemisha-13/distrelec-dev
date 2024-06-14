(function ($) {

	/**
	 * Bom Skin implementation for the module Productlist.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.Productlist
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Productlist.Bom = function (parent) {
		// subscribe to connector channel/s

		this.on = function (callback) {
			this.sandbox.subscribe('cartlistBulkAction', this);
			this.sandbox.subscribe('lightboxShoppinglist', this);
			this.sandbox.subscribe('lightbox', this);

			parent.on(callback);

		};

		this.onAddAllProductsToShoppingList = function (data) {
            var $allProducts = $('.skin-product-bom.active');
			var productCodesArray = []
				,productQuantityArray = []
				,productReferenceArray = []
				,hasValidationError = false
			;

            if ( $allProducts.length > 0) {

                $allProducts.each(function (index, product) {
                    productCodesArray[index] = $(product).find('.hidden-product-code').val();
                    productQuantityArray[index] = $(product).find('.numeric .ipt').val();
                    productReferenceArray[index] = $(product).find('.hidden-product-reference').val();

                    if ($(product).find('.numeric').hasClass('numeric-error')) {
                        hasValidationError = true;
                    }
                });

                if (!hasValidationError) {
                    // Open Lightbox
                    this.fire('checkUserLoggedIn', {productCodesArray: productCodesArray, productQuantityArray: productQuantityArray, productReferenceArray: productReferenceArray }, ['lightboxShoppinglist']);
                }

            }

		};

		this.onAddAllProductsToCart = function () {
            var allProducts = $('.skin-product-bom.active');
			var cartAPIproductsJson = '[{';
			var cartAPIquantity = 0;
			var hasValidationError = false;

			if ( allProducts.length > 0) {

                allProducts.each(function (index, product) {

                    var $product = $(product);
                    if (index > 0) {
                        cartAPIproductsJson += '},{';
                    }
                    if ($(product).find('.numeric').hasClass('numeric-error')) {
                        hasValidationError = true;
                    }

                    var productCode = $product.find('.hidden-product-code').val();
                    var productQty = $product.find('.ipt').val();
                    var productReference = $product.find('.product-reference').val();

                    cartAPIproductsJson += '"productCode":"' + productCode + '",';
                    cartAPIproductsJson += '"quantity":' + productQty + ',';
                    cartAPIproductsJson += '"product":null,';
                    cartAPIproductsJson += '"reference":"' + productReference + '"';
                    cartAPIquantity++;
                });

                cartAPIproductsJson += '}]';

                if (!hasValidationError) {

                    // Trigger Cart API to add to cart
                    $(document).trigger('cart', {
                        actionIdentifier: 'bomAddToCart',
                        type: 'addBulk',
                        productsJson: cartAPIproductsJson,
                        qty: cartAPIquantity
                    });
                }

			}


		};
	};

})(Tc.$);