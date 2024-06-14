(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Checkout-progressbar
	 * @extends Tc.Module
	 */
	Tc.Module.CheckoutProgressbar = Tc.Module.extend({

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
			this.sandbox.subscribe('openOrder', this);
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

			this.$$('a.disabled').on('click', function(ev){
				ev.preventDefault();
			});

			// TODO Kristian: check what logic below actually do!!!
			var activeStep = $('.mod-checkout-progressbar__steps__item--active .link > span').text();

			if (activeStep !== undefined) {
                $('.mod-checkout-progressbar__indicator__percentage').addClass('mod-checkout-progressbar__indicator__percentage--step-' + activeStep);
            }

			callback();
		},


		// TODO Kristian: check what logic below actually do!!!
		//
		// User changes openOrderType
		onAddOrderToExistingOpenOrderChange: function(data){
			if(data.addedToExistingOpenOrder){
				this.$$("li[data-stepinfoonly='checkout.progress.detail'] .activePassiveStep").attr('disabled', 'disabled');
			}
			else{
				this.$$("li[data-stepinfoonly='checkout.progress.detail'] .activePassiveStep").removeAttr('disabled');
			}

		}

	});

})(Tc.$);
