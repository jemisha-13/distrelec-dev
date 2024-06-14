(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class CheckoutPaymentOptionsList
	 * @extends Tc.Module
	 */
	Tc.Module.CheckoutPaymentOptionsList = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxYesNo', this);

			this.action = '';
			this.actionIdentifier = 'removePaymentOption';
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

			// click on remove button
			$ctx.on('click', '.btn-remove', function(e) {
				var $target = $(e.target)
					,$targetParent = $target.parent()
					,paymentId = $targetParent.data('payment-id')
					, lightboxTitle = $targetParent.data('lightbox-title')
					, lightboxMessage = $targetParent.data('lightbox-message')
					, lightboxBtnDeny = $targetParent.data('lightbox-btn-deny')
					, lightboxShowBtnConfirm = $targetParent.data('lightbox-show-confirm-button')
					, lightboxBtnConf = $targetParent.data('lightbox-btn-conf')
				;

				self.action = $targetParent.data('action-url');

				// check, if creditcard should really be deleted
				self.fire(
					'yesNoAction',
					{
						actionIdentifier: self.actionIdentifier,
						attribute: paymentId,
						lightboxTitle: lightboxTitle,
						lightboxMessage: lightboxMessage,
						lightboxBtnDeny: lightboxBtnDeny,
						lightboxShowBtnConfirm: lightboxShowBtnConfirm,
						lightboxBtnConf: lightboxBtnConf
					},
					['lightboxYesNo']
				);

				return false;
			});

			callback();
		}

	});

})(Tc.$);
