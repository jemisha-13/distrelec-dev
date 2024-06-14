(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class CheckoutCompleteDeliveryCheckbox
	 * @extends Tc.Module
	 */
	Tc.Module.CheckoutCompleteDeliveryCheckbox = Tc.Module.extend({

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
			
			completeDelivery = false;

			$('#completeDelivery').click(function() {

				if ($(this).prop('checked') === true) {
					completeDelivery = true;
				}

                $.ajax({
                    url: '/checkout/payment/setCompleteDelivery',
                    type: 'POST',
                    data: {
                    	completeDelivery: completeDelivery
                    }
                });

			});

			callback();
		}
	});

})(Tc.$);
