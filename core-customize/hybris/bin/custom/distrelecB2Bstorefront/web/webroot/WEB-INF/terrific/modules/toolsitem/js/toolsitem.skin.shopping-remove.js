(function($) {

	/**
	 * Shopping-remove Skin implementation for the module Toolsitem.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.ShoppingRemove = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			// subscribe to connector channel/s
			this.sandbox.subscribe('shoppinglist', this);

			var mod = this;

            this.$ico = this.$$('.ico');

			mod.$ico.on('click.Toolsitem.Shopping.remove', function (ev) {
				var productCode = mod.$ico.data('product-code')
					,listId = mod.$ico.data('list-id')
					,productCodesArray = []
					,listIdArray = []
					,$product = mod.$ico.closest('.mod-product')
					,$shoppingList = $product.closest('.js-shopping-list')
				;

				productCodesArray[0] = productCode;
				listIdArray[0] = listId;

				$.ajax({
					url: '/shopping/remove',
					type: 'post',
					data: {
						productCodes: productCodesArray,
						listIds: listIdArray
					},
					success: function () {
						$(document).trigger('listsChange', {
							type: 'list',
							quantity: -1
						});

						// Fire Update to Shopping List Meta Actions
						mod.fire('removeProductsFromShoppingList', { listId: listId, quantity : -1 }, ['shoppinglist']);
						// Fire Update to Product List to remove product from list visually
						mod.fire('removeSingleProductFromShoppingListAjaxSuccess', { productToRemove: $product }, ['shoppinglist']);
						// // Show recalculation layer on totals (removed product and shopping list scope as params)
						mod.enableRecalculateLayerOnItemRemove($product, $shoppingList);
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});
            });
		};

		// Function which triggers event on .ipt field which will show up recalculation layer upon totals
		this.enableRecalculateLayerOnItemRemove = function ($removedProduct, $shoppingList) {
			// Get all shopping list items on page
			var $shoppingListItems = $('.js-shopping-list-item', $shoppingList);
			// Check how many items left on page after user removed one
			var numberOfItemsLeft = $shoppingListItems.length - 1;
			// Get element on which we will trigger event which will cause recalculation layer to be shown upon totals
			var $triggerItem = $($shoppingListItems[0]);

			// If: there are items on page left, trigger event
			// Otherwise: Hide totals component
			if (numberOfItemsLeft > 0) {
				// If user clicked on first element, set trigger element to be next one
				if ($triggerItem.is($removedProduct)) {
					// Set trigger element to be next one
					$triggerItem = $($shoppingListItems[1]);
				}
				// Find .ipt field which contains qty value and trigger event "keyup" on which recalculation layer will be shown
				$triggerItem.find('.js-ipt').trigger('keyup');
			} else {
				$('.js-productlist-price-recalc-wrapper').hide();
			}
		};
	};

})(Tc.$);
