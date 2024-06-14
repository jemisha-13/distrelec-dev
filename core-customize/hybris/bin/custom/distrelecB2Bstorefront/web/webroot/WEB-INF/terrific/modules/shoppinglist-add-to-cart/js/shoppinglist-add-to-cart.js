(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class ShoppinglistAddToCart
	 * @extends Tc.Module
	 */
	Tc.Module.ShoppinglistAddToCart = Tc.Module.extend({

		/**
		 * Initialize.
		 *
		 * @method init
		 * @return {void}
		 * @constructor
		 * @param {jQuery} $ctx the jquery context
		 * @param {Sandbox} sandbox the sandbox to get the resources from
		 * @param {Number} id the unique module id
		 */
		init: function ($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

			this.onSelectAllCheckboxChange = $.proxy(this.onSelectAllCheckboxChange, this);
			this.onSingleProductsSelectedStateChange = $.proxy(this.onSingleProductsSelectedStateChange, this);
			this.onAddToCartButtonClick = $.proxy(this.onAddToCartButtonClick, this);

			// subscribe to connector channel/s
			this.sandbox.subscribe('shoppinglistBulkAction', this);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {
			var mod = this;

			this.$$('.btn-cart').on('click', mod.onAddToCartButtonClick);

			callback();
		},

		onSelectAllCheckboxChange: function(data){
			if(data.isSelected){
				this.$$('.btn-cart .all-items').removeClass('active');
				this.$$('.btn-cart .selected-items').addClass('active');
			}
			else{
				this.$$('.btn-cart .all-items').addClass('active');
				this.$$('.btn-cart .selected-items').removeClass('active');
			}
		},

		onSingleProductsSelectedStateChange: function(data){
			if(data.noProductsSelected){
				this.$$('.btn-cart .all-items').addClass('active');
				this.$$('.btn-cart .selected-items').removeClass('active');
			}
			else{
				this.$$('.btn-cart .all-items').removeClass('active');
				this.$$('.btn-cart .selected-items').addClass('active');
			}
		},

		onAddToCartButtonClick: function(){
			var url = window.location.origin,
				cartUrl = url + '/cart';

			this.fire('addSelectedProductsToCart', ['shoppinglistBulkAction']);

            setTimeout(function(){
                window.location.replace(cartUrl);
            }, 1000);
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function () {
		}

	});

})(Tc.$);
