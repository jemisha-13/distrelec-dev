(function($) {

	/**
	 * Shoppingbulk Skin implementation for the module Toolsitem.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.Toolsitem
	 * @class Basic
	 * @extends Tc.Toolsitem
	 * @constructor
	 */
	Tc.Module.Toolsitem.ShoppingBulk = function (parent) {

		this.on = function (callback) {

			this.sandbox.subscribe('cartlistBulkAction', this);

			var self = this,
				$ctx = this.$ctx,
				$ico = $('.ico-list', $ctx);

			$ico.on('click', function () {
				if(!$ico.hasClass('disabled')){
					self.fire('addAllProductsToShoppingList', {}, ['cartlistBulkAction']);
				}
			});

			parent.on(callback);
		};

		// Ajax Sucess Event is fired from Shopping list lightbox (added to existing list)
		this.onAddProductsToExistingShoppingListSuccess = function(data){
			this.processSuccessfulAddToShoppinglist(data);
		};

		// Ajax Sucess Event is fired from Shopping list lightbox (added to new List)
		this.onAddedNewListAndProductsSuccess = function(data){
			this.processSuccessfulAddToShoppinglist(data);
		};

	};

})(Tc.$);
