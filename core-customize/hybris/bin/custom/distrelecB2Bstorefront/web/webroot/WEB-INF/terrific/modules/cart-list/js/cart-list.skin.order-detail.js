(function ($) {

	/**
	 * OrderDetail Skin implementation for the module CartList.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.CartList
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.CartList.OrderDetail = function (parent) {
		// subscribe to connector channel/s
		this.sandbox.subscribe('cartlistBulkAction', this);
		this.sandbox.subscribe('lightboxShoppinglist', this);

		// CartList add to Cart bulk action
		// -> triggered by toolsitem.skin.cart-bulk

		this.onAddAllProductsToCart = function(data){
			var allProducts = this.$$('.cart-list .mod-cart-list-item')
				,cartAPIproductsJson = '[{'
				,cartAPIquantity = 0
			;

            // iterate over each product
            allProducts.each(function( index, product ) {

                var  $product = $(product)
                    ,productCode = $product.find('.hidden-product-code').val()
                    ,productQty = parseInt($product.find('.js-base').data("item-quantity"))
                ;

                // gather product json and webtrekk data
                if(!$(product).hasClass('paged') || type !== 'checked') {

                    if(index > 0){
                        cartAPIproductsJson += '},{';
                    }

                    // build cartAPI productCode & quantity json
                    cartAPIproductsJson += '"productCode":"' + productCode + '",';
                    cartAPIproductsJson += '"quantity":' + productQty + ',';
                    cartAPIproductsJson += '"product":null,';
                    cartAPIproductsJson += '"reference":""';

                    // increae for each product,
                    cartAPIquantity++;
                }
            });

            cartAPIproductsJson += '}]';

            // Trigger Cart API to add to cart
            $(document).trigger('cart', {
                actionIdentifier: 'orderDetailAddToCart',
                type: 'addBulk',
                productsJson: cartAPIproductsJson,
                qty: cartAPIquantity
            });
        };

		this.onAddAllProductsToShoppingList = function(data){
			var allProducts = this.$$('.cart-list .mod-cart-list-item')
				,productCodesArray = []
				,productQuantityArray = []
				,productReferenceArray = []
			;

			allProducts.each(function(index, product) {
				var productCode = $(product).find('.hidden-product-code').val()
					,productQuantity = parseInt($(product).find('.cell-numeric .qty').text())
					,productReference = $(product).find('.hidden-product-reference').val()
					;

				productCodesArray[index] = productCode;
				productQuantityArray[index] = productQuantity;
				productReferenceArray[index] = productReference;

			});


			// Open Lightbox
			this.fire('checkUserLoggedIn', {productCodesArray: productCodesArray, productQuantityArray: productQuantityArray, productReferenceArray: productReferenceArray}, ['lightboxShoppinglist']);
		};
	};

})(Tc.$);
