(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class CarpetNew
	 * @extends Tc.Module
	 */
	Tc.Module.CarpetNew = Tc.Module.extend({

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

			// subscribe to connector channel/s
			this.sandbox.subscribe('toolItems', this);

			// bind handlers to module context
			this.requestProductToggleStates = $.proxy(this.requestProductToggleStates, this);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {

			this.requestProductToggleStates();

			callback();
		},


		requestProductToggleStates: function () {
			var allCarpetElements = this.$$('.carpet-items .mod-carpet-item')
				, productCodesArray = []
				, mod = this
			;

			$.each(allCarpetElements, function (index, carpetElement) {
				var productCode = $(carpetElement).data('product-id');
				if (productCode !== undefined && productCode !== '') {
					productCodesArray[index] = productCode;
				}
			});

			if(productCodesArray.length > 0){
				$.ajax({
					url: '/checkToggles',
					type: 'post',
					data: {
						productCodes: productCodesArray
					},
					success: function (data, textStatus, jqXHR) {
						mod.fire('updateToolItemStates', { products: data.products }, ['toolItems']);
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});
			}
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function () {
			// Do stuff here or remove after method
			//...
		}

	});

})(Tc.$);
