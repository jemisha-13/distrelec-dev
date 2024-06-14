(function($) {

	/**
	 * Productlist Skin Shopping implementation for the module Productlist Order.
	 * All products are loaded on page load. Availability is only requested for the x visible products and
	 * gets loaded for the next x products when clicked on show more.
	 *
	 */
	Tc.Module.ProductlistTools.Shopping = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			this.onHideShoppingListDownloadTool = $.proxy(this, 'onHideShoppingListDownloadTool');

			// subscribe to connector channel/s
			this.sandbox.subscribe('shoppinglistBulkAction', this);

			parent.on(callback);

		};


		//
		// Checkboxes
		//

		// Select all checkboxes
		this.onHideShoppingListDownloadTool = function(data){
			var mod = this;
			this.$$('.skin-toolsitem-download').fadeOut(function(){
				mod.$$('.skin-toolsitem-download').addClass('hidden');
			});
		};
	};

})(Tc.$);
