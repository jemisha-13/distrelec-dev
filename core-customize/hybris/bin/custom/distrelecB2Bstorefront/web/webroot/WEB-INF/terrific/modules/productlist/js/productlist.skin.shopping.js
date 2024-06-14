(function($) {

	/**
	 * Productlist Skin Shopping implementation for the module Productlist Order.
	 * All products are loaded on page load. Availability is only requested for the x visible products and
	 * gets loaded for the next x products when clicked on show more.
	 *
	 */
	Tc.Module.Productlist.Shopping = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			this.onAddSelectedProductsToFavoriteList = $.proxy(this.onAddSelectedProductsToFavoriteList, this);
			this.onAddToFavoriteAjaxSuccess = $.proxy(this.onAddToFavoriteAjaxSuccess, this);
			this.onRemoveSelectedProductsFromList = $.proxy(this.onRemoveSelectedProductsFromList, this);
			this.onRemoveSelectedProductsAjaxSuccess = $.proxy(this.onRemoveSelectedProductsAjaxSuccess, this);
			this.onRemoveSingleProductFromShoppingListAjaxSuccess = $.proxy(this.onRemoveSingleProductFromShoppingListAjaxSuccess, this);
			this.onAddSelectedProductsToCart = $.proxy(this.onAddSelectedProductsToCart, this);
			this.onShowMoreButtonClick = $.proxy(this, 'onShowMoreButtonClick');

			// subscribe to connector channel/s
			this.sandbox.subscribe('shoppinglistBulkAction', this);
			this.sandbox.subscribe('shoppinglist', this);
			this.sandbox.subscribe('favoritelist', this);
			this.sandbox.subscribe('lightboxShoppinglist', this);
			this.sandbox.subscribe('metaHDCompare', this);

			this.$$('.btn-show-more').on('click', this.onShowMoreButtonClick);

			parent.on(callback);

		};


		//
		// Checkboxes
		//

		// Select all checkboxes
		this.onSelectAllCheckboxChange = function(data){
			this.fire('selectAllProductsCheckboxChange', { isSelected : data.isSelected }, ['shoppinglist']);
		};

		// Select one checkbox
		this.onSelectSingleProductCheckboxChange = function(data){
			var totalProducts = this.$$('.productlist .mod-product')
				,checkedProducts = this.$$('.productlist .select-product:checked')
				;
			if(!data.isSelected){
				// if one single product gets deselected, selectAll checkbox should get unselected
				this.fire('selectAllStateChange', { allProductsSelected : false }, ['shoppinglistBulkAction']);

				// if no products are selected, bulkActionsDropdown should not be visible
				if(checkedProducts.length === 0){
					this.fire('singleProductsSelectedStateChange', { noProductsSelected : true }, ['shoppinglistBulkAction']);
				}
			}
			else{
				// if all products are selected, selectAll checkbox should get selected
				if(totalProducts.length == checkedProducts.length){
					this.fire('selectAllStateChange', { allProductsSelected : true }, ['shoppinglistBulkAction']);
				}

				// at least one product is selected, bulkActionsDropdown should be visible
				this.fire('singleProductsSelectedStateChange', { noProductsSelected : false }, ['shoppinglistBulkAction']);
			}
		};


		//
		// Show More
		//

		this.onShowMoreButtonClick = function (ev) {
			ev.preventDefault();
			var pageSize = $(ev.target).closest('a').data('page-size')
				, hiddenProducts = parent.$$('.list .mod-product.paged')
				, productsToShow = hiddenProducts.slice(0, pageSize)
				, productCodesArray = []
			;

			$.each(productsToShow, function (index, product) {
				var $product = $(product);

				$product.removeClass('paged');
				$product.hide().slideDown();
				productCodesArray[index] = $product.find('.hidden-product-code').val();
			});

			// Methods are defined in Productlist Parent Class
			parent.requestProductToggleStates (productCodesArray);
			parent.requestProductAvailabilityStates (productCodesArray, productsToShow);

			// fire event to deselect select all checkbox on shopping list page
			this.fire('selectAllStateChange', { allProductsSelected: false }, ['shoppinglistBulkAction']);

			if (hiddenProducts.length <= pageSize) {
				$(ev.target).closest('.row-show-more').slideUp();
			}
		};


		//
		// Add Selected to a List (Favorite, Shopping)
		//

		// Add Selected to Favorite
		this.onAddSelectedProductsToFavoriteList = function(data){
			var mod = this;

			var checkedProducts = this.$$('.productlist .select-product:checked');
			var productCodesArray = [];

			checkedProducts.each(function( index ) {
				productCodesArray[index] = $(this).closest('.mod-product').find('.hidden-product-code').val();
			});

			$.ajax({
				url: '/shopping/favorite/add',
				type: 'post',
				data: {
					productCodes: productCodesArray
				},
				success: function (data, textStatus, jqXHR) {
					mod.onAddToFavoriteAjaxSuccess(data.favoriteListCount, productCodesArray.length);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					// TODO ERROR Behaviour
				}
			});
		};

		// Add Selected to Facorite Success
		this.onAddToFavoriteAjaxSuccess = function(favoriteListCount, addedProductsCount){
			// Trigger Metahd
			$(document).trigger('listsChange', {
				type: 'favorite',
				quantity: addedProductsCount
			});

			// Fire Update to Shopping List Meta Actions
			this.fire('updateFavoriteProductCount', { listItemCount : favoriteListCount }, ['favoritelist']);
		};

		// Add Selected to Compare
		this.onAddSelectedProductsToCompareList = function(data){

			var mod = this;

			var checkedProducts = this.$$('.productlist .select-product:checked');
			var productCodesArray = [];

			checkedProducts.each(function( index ) {
				productCodesArray[index] = $(this).closest('.mod-product').find('.hidden-product-code').val();
			});

			$.ajax({
				url: '/compare/add',
				type: 'post',
				data: {
					productCodes: productCodesArray
				},
				dataType: 'json',
				success: function(response) {
					// trigger compareChange
					mod.fire('compareChange', {
						'compareProductsData': response.compareProductsData,
						'quantityChange': productCodesArray.length
					}, ['metaHDCompare']);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					// Ajax Call error
				}
			});
		};

		// Add Selected to Shoppinglist
		this.onAddSelectedProductsToShoppingList = function(data){
			var checkedProducts = this.$$('.productlist .select-product:checked')
				,productCodesArray = []
				,productQuantityArray = []
				,productReferenceArray = []
			;

			checkedProducts.each(function( index ) {
				var product = $(this).closest('.mod-product')
					,productCode = product.find('.hidden-product-code').val()
					,productQuantity = product.find('.numeric .ipt').val()
					,productReference = product.find('hidden-product-reference').val()
					;

				productCodesArray[index] = productCode;
				productQuantityArray[index] = productQuantity;
				productReferenceArray[index] = productReference;
			});

			// Open Lightbox
			this.fire('checkUserLoggedIn', {productCodesArray: productCodesArray, productQuantityArray: productQuantityArray, productReferenceArray: productReferenceArray}, ['lightboxShoppinglist']);
		};


		//
		// Add Selected or All to Cart Bulk (from Shoppinglist)
		//

		this.onAddSelectedProductsToCart = function(data){
			var  checkedProducts = this.$$('.productlist .select-product:checked')
				,allProducts = this.$$('.mod-product')
			;

			// add checked products
			if(checkedProducts.length > 0) {
				this.addToCartBulk(checkedProducts, 'checked');
			} else {
				this.addToCartBulk(allProducts, 'all');
			}
		};


		// Add to Cart Bulk Action
        this.addToCartBulk = function(products, type) {

            var  cartAPIproductsJson = '[{'
                ,cartAPIquantity = 0
            ;

            // iterate over each product
            products.each(function( index, product ) {

                var  $product = (type === 'checked') ? $(this).closest('.mod-product') : $(product)
                    ,productCode = $product.find('.hidden-product-code').val()
                    ,productQty = parseInt($product.find('.js-num-stepper .js-ipt').val())
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
                actionIdentifier: 'shoppingListAddToCart',
                type: 'addBulk',
                productsJson: cartAPIproductsJson,
                qty: cartAPIquantity
            });
        };


		//
		// Remove from List Bulk
		//

		// Remove selected Products
		this.onRemoveSelectedProductsFromList = function(data){
			var mod = this;

			var checkedProducts = this.$$('.productlist .select-product:checked')
				,productsInListCount = this.$$('.productlist .mod-product').length
				,allSelectableProducts = this.$$('.productlist .mod-product:not(.skin-product-not-buyable)')
				,productCodesArray = []
				,listIdArray = []
			;

			listIdArray[0] = this.$ctx.data('list-id');

			checkedProducts.each(function( index ) {
				var $product = $(this).closest('.mod-product');
				productCodesArray[index] = $product.find('.hidden-product-code').val();
				$product.slideUp(400, function() { $(this).remove(); } );
			});

			// Call Server to remove products from list
			$.ajax({
				url: '/shopping/remove',
				type: 'post',
				data: {
					productCodes: productCodesArray,
					listIds: listIdArray
				},
				success: function () {
					mod.onRemoveSelectedProductsAjaxSuccess(checkedProducts, listIdArray, productsInListCount, allSelectableProducts);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					// Ajax Error
				}
			});
		};

		// Remove selected Products Success
		this.onRemoveSelectedProductsAjaxSuccess = function(checkedProducts, listIdArray, productsInListCount, allSelectableProducts){
			// Trigger Metahd
			$(document).trigger('listsChange', {
				type: 'list',
				quantity: -checkedProducts.length
			});

			// Fire Update to Shopping List Meta Actions
			this.fire('removeProductsFromShoppingList', { listId: listIdArray[0], quantity : -checkedProducts.length }, ['shoppinglist']);

			// Disable select all checkbox and order dropdown if all selectable products were deleted
			if(checkedProducts.length == allSelectableProducts.length){
				this.fire('toggleShoppingListToolsDisabledState', { disabled: true }, ['shoppinglistBulkAction']);
			}
			// Show empty List text if list is really empty
			if(checkedProducts.length == productsInListCount){
				this.$$('.empty-list').slideDown();
				this.fire('hideShoppingListDownloadTool', { }, ['shoppinglistBulkAction']);
			}
		};

		// Event comes from Toolsitem Shopping-remove
		this.onRemoveSingleProductFromShoppingListAjaxSuccess = function(data){
			var productsInListCount = this.$$('.productlist .mod-product').length;
			// Disable select all checkbox and order dropdown and toolsitem download if it is the last product
			if(productsInListCount == 1){
				this.fire('toggleShoppingListToolsDisabledState', { disabled: true }, ['shoppinglistBulkAction']);
				this.$$('.empty-list').slideDown();
				this.fire('hideShoppingListDownloadTool', { }, ['shoppinglistBulkAction']);
			}
			// Remove item from List
			data.productToRemove.slideUp(400, function() { $(this).remove(); } );
		};
	};

})(Tc.$);
