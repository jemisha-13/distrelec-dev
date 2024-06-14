(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class ShoppinglistTitle
	 * @extends Tc.Module
	 */
	Tc.Module.VerifyAddress = Tc.Module.extend({

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
			var $verifyAddressCheckbox = $('.verify-address-checkbox');

			$verifyAddressCheckbox.on('click', function(){
				if($verifyAddressCheckbox.is(":checked")){
					$('.field-required').css('visibility', 'hidden');
				}
			});
			
			callback();
		}

	});

})(Tc.$);