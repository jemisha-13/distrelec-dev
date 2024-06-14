(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class CheckoutOrderSummaryCostCenterBox
	 * @extends Tc.Module
	 */
	Tc.Module.CheckoutVoucherBox = Tc.Module.extend({

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
			callback();

			var self = this;
			var voucherForm = $('.mod-checkout-voucher-box__form');

			this.$$('.btn-voucher').on('click', function(e) {
				self.handleVoucherCode(e);
			});

			this.$$('#voucher').on('keydown' , function (e) {
				if (e.keyCode === 13) {
					self.handleVoucherCode(e);
				}
			});
			
			if ($('.mod-checkout-voucher-box .field-msgs .error').length > 0 || $('.mod-checkout-voucher-box__form #voucher').hasClass('error')) {
                voucherForm.removeClass('hidden');
			}

			$('.mod-checkout-voucher-box .head').click(function() {

				if (voucherForm.hasClass('hidden')) {
                    voucherForm.removeClass('hidden');
				} else {
                    voucherForm.addClass('hidden');
				}

			});

			if( null !== digitalData && null !== digitalData.cart && null !== digitalData.cart.price && null !== digitalData.cart.price.voucherDiscount  && digitalData.cart.price.voucherDiscount >0)
			{
				Bootstrapper.ensEvent.trigger("voucher code success");
			}
		},

		handleVoucherCode: function (e) {
			var mod = this;
			e.preventDefault();

			voucherCode = mod.$$('.voucher-code').val();

			var validation = true;
            var voucherForm = $(e.target).closest('form');
            var actUrl = voucherForm.attr('action');

			if (voucherCode === '' && voucherForm.data('form-type') === 'redeem') {
				validation = false;
				$('.mod-checkout-voucher-box__form #voucher').addClass('error');
			}

			if (voucherForm.data('form-type') === 'redeem') {
				var actUrlObj = Tc.Utils.splitUrl(actUrl);

				actUrlObj.base = actUrlObj.base + voucherCode;
				actUrl = Tc.Utils.joinUrl(actUrlObj);
			}

			if (validation) {
                voucherForm.attr('action', actUrl).submit();
			}

		}

	});

})(Tc.$);
