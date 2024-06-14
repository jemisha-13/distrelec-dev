(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Product-tools
	 * @extends Tc.Module
	 */
	Tc.Module.ProductTools = Tc.Module.extend({
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

			// subscribe to connector channel/s
			this.sandbox.subscribe('toolItems', this);

			this.onUpdateToolItemStates = $.proxy(this.onUpdateToolItemStates, this);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var $ctx = this.$ctx,
				self = this;

			// Do stuff here
			//...

			callback();
		},

		onUpdateToolItemStates: function(data){
			var mod = this;
			$.each(data.products, function (index, product) {
				if (product.productId == mod.$ctx.find('.tools-bar').data('product-id')) {
					$.each(product.productToggles, function (index, productToggle) {
						var toolsItemClass = "";

						if(productToggle == "FAVORITE"){
							toolsItemClass = ".ico-fav";
						}
						else if(productToggle == "COMPARE"){
							toolsItemClass = ".ico-compare";
						}
						else if(productToggle == "SHOPPING"){
							toolsItemClass = ".ico-list";
						}
						else if(productToggle == "CART"){
							toolsItemClass = ".ico-cart";
						}
						mod.$ctx.find('.tools-bar').find(toolsItemClass).addClass('active');
					});
				}
			});
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
			// Do stuff here or remove after method
			//...
		}

	});

})(Tc.$);
