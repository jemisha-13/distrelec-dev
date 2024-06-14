(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class CartUiLogic
	 * @extends Tc.Module
	 */
	Tc.Module.MetahdSuggestItemCartLogic = Tc.Module.extend({

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
		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

			this.onSubmitFail = $.proxy(this.onSubmitFail, this);

			this.$numeric = this.$$('.numeric');
			this.$ipt = this.$numeric.find('.ipt');

			this.productCode = this.$$('.hidden-product-code').val();
			this.productCodeErp = this.$$('.hidden-product-code-erp').val();
			this.manufacturer = this.$$('.hidden-manufacturer').val();
			this.productPrice = this.$$('.hidden-product-price').val();
			this.productName = this.$$('.hidden-product-name').val();
			this.typeName = this.$$('.hidden-product-type-name').val();

		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {

			Tc.Utils.numericStepper(this.$numeric);

			this.$$('.hook-cart-add').on('click', $.proxy(this.onAddToCart, this));

			$('.mod-metahd-suggest-item-cart-logic').click(function(e) {
				e.preventDefault();
			});

            $('.numeric .ipt').click( function(){
            	$(this).val('');
            });

			callback();
		},

		onAddToCart: function(ev) {
			ev.preventDefault();

			if (this.$numeric.hasClass("numeric-error")) {
				return false;
			}
			// hide possible warning popover
			this.$numeric.removeClass("numeric-warning");

			// Trigger Add to cart
			$(document).trigger('cart', {
				actionIdentifier: 'metaHdAddToCart'
				,type: 'add'
				,productCodePost: this.productCode
				,qty: this.$ipt.val()
				,fail: this.onSubmitFail
			});
		}

	});

})(Tc.$);
