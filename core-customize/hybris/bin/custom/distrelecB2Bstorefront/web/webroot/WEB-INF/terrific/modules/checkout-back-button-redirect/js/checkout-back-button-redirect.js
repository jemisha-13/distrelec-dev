(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class CheckoutBackButtonRedirect
	 * @extends Tc.Module
	 */
	Tc.Module.CheckoutBackButtonRedirect = Tc.Module.extend({

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

		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {

			if($(document).find('body').hasClass('skin-layout-checkout')){
				$.ajax({
					url: '/checkout/validate',
					type: 'POST',
					dataType: 'json',
					method: 'post',
					success: function (data, textStatus, jqXHR) {
						if(data.redirectPage !== ""){
							window.location.replace(data.redirectPage);
						}
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});
			}

			var loadingState = $('.skin-loading-state-loading-state');
			$('#command').submit(function() {
				loadingState.removeClass('hidden');
			});


			callback();
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
