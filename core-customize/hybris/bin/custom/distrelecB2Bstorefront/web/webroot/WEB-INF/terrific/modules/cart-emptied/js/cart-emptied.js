(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class CartEmptied
	 * @extends Tc.Module
	 */
	Tc.Module.CartEmptied = Tc.Module.extend({

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

		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {

			// Display "cart emptied" message if appropriate
			if (location.hash==='#emptied') {
				$('.mod-cart-emptied').removeClass('hidden').find('span').click(function() {
					$.ajax({
						url: '/cart/restore',
						dataType: 'json',
						method: 'post',
						success: function (data, textStatus, jqXHR) {
							// reload cart-page on success
							window.location.href = '/cart';
							window.location.reload();
						},
						error: function (jqXHR, textStatus, errorThrown) {
							alert('Could not restore cart');
						}
					});
					
				});
			}

			callback();
		}
	});
})(Tc.$);
