(function($) {

	/**
	 * Productlist Skin Favorite implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.Productlist.Favorite = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// subscribe to connector channel/s
			this.sandbox.subscribe('favoritelist', this);

			this.onShowMoreButtonClick = $.proxy(this, 'onShowMoreButtonClick');
			this.$$('.btn-show-more').on('click', this.onShowMoreButtonClick);

			parent.on(callback);

		};

		this.onRemoveProductFromFavoriteList = function(data){
			var mod = this;
			this.$$('.mod-product#'+data.productCode).slideUp(400, function() {
				$(this).remove();
				// show empty list message if list is empty
				if(mod.$$('.mod-product').length === 0){
					mod.$$('.empty-list').addClass('active');

					// Disable order dropdown if all selectable products were deleted
					mod.fire('toggleOrderDropdownDisabledState', { disabled: true }, ['favoritelist']);
					// Disable Download Tools Item
					mod.fire('hideFavoriteListDownloadTool', { }, ['favoritelist']);
				}
			});

			// Fire Update to Shopping List Meta Actions
			this.fire('updateFavoriteProductCount', { quantity : "-1" }, ['favoritelist']);
		};

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

			parent.requestProductToggleStates (productCodesArray);
			parent.requestProductAvailabilityStates (productCodesArray, productsToShow);

			// fire event to deselect select all checkbox on shopping list page
			this.fire('selectAllStateChange', { allProductsSelected: false }, ['shoppinglistBulkAction']);

			if (hiddenProducts.length <= pageSize) {
				$(ev.target).closest('.row-show-more').slideUp();
			}
		};

	};

})(Tc.$);
