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
	Tc.Module.Toolsitem.ReturnItems = function (parent) {
 
		this.on = function (callback) {
			
			this.sandbox.subscribe('cartlistBulkAction', this);

			// calling parent method
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
