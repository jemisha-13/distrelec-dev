(function ($) {

	/**
	 * List Skin implementation for the module Toolsitem.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.Shopping = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxShoppinglist', this);
			this.sandbox.subscribe('shoppinglist', this);
			this.sandbox.subscribe('toolsitemShopping', this);

			var self = this
				,$icoList = this.$$('.ico-list')
				,productCode = $icoList.data('product-code')
				,productCodesArray = []
				;

			$icoList.on('click', function () {
				if(!$icoList.hasClass('disabled')){
					var productQuantity = $icoList.data('product-min-order-quantity') 
						,productQuantityArray = []
						,productReferenceArray = []
						,productReference = $icoList.data('product-reference')
					;

					productCodesArray[0] = productCode;
					productQuantityArray[0] = productQuantity;
					productReferenceArray[0] = productReference; 

					self.fire('checkUserLoggedIn', {productCodesArray: productCodesArray, productQuantityArray: productQuantityArray, productReferenceArray: productReferenceArray}, ['lightboxShoppinglist']);
				}
			});
		};

		this.onQuantityUpdate = function (data) {
			var $icoList = this.$$('.ico-list');

			// am I the toolsitem who should get the update?
			if($icoList.data('product-code') === data.productCode){
				$icoList.data('product-min-order-quantity', data.newQuantity);
			}
		};

		// Ajax Sucess Event is fired from Shopping list lightbox (added to existing list)
		this.onAddProductsToExistingShoppingListSuccess = function (data) {
			this.processSuccessfulAddToShoppinglist(data);
		};
		// Ajax Sucess Event is fired from Shopping list lightbox (added to new List)
		this.onAddedNewListAndProductsSuccess = function (data) {
			this.processSuccessfulAddToShoppinglist(data);
		};

		this.processSuccessfulAddToShoppinglist = function (data) {
			var $icoList = this.$$('.ico-list');

			// Are we the Icon that should get the active class?
			if ($.inArray($icoList.data('product-code'), data.productCodes) !== -1) {
				$icoList.addClass('active');
			}
		};

	};

})(Tc.$);
