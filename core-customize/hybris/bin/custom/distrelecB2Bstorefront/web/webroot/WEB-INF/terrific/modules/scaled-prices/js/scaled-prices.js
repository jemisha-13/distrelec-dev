(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Scaled-prices
	 * @extends Tc.Module
	 */
	Tc.Module.ScaledPrices = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxQuotation', this);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var self = this,
				$btnBulkDiscount = $('.btn-bulk-discount', this.$ctx),
				$lnkMorePrices = $('.lnk-more-prices', this.$ctx),
				$lnkLessPrices = $('.lnk-less-prices', this.$ctx),
				$priceTableOverlay = $('.price-table-overlay', this.$ctx),
				$priceTableHead = $('.head', this.$ctx)
			;

			$btnBulkDiscount.off('click.quotation').on('click.quotation', function(ev) {
				ev.preventDefault();

				// Quotation lightbox
				// logged in state required
				self.fire('checkUserLoggedIn', {
					$btn: $btnBulkDiscount
				}, ['lightboxQuotation']);
			});
			
			$lnkMorePrices.on('click', function(ev) {
				ev.preventDefault();
				$priceTableHead.addClass('overlay');
				$priceTableOverlay.slideDown(200);
			});

			$lnkLessPrices.on('click', function(ev) {
				ev.preventDefault();
				$priceTableOverlay.slideUp(200,function(){$priceTableHead.removeClass('overlay');});
			});

			callback();
		}

	});

})(Tc.$);
