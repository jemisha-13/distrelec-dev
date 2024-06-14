(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Product
	 * @extends Tc.Module
	 */
	Tc.Module.Product = Tc.Module.extend({

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
			this._super($ctx, sandbox, id);
			this.onUpdateProductToggleState = $.proxy(this.onUpdateProductToggleState, this);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			callback();
		},

		after: function () {

		},

		onUpdateProductToggleState: function(data){
			$.each(data.products, function(index, product){
				$(this).find('.hidden-product-code').val();
			});
		}

	});

})(Tc.$);
