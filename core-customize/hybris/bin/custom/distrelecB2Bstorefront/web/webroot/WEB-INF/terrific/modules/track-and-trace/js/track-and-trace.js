(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Product-image-gallery
	 * @extends Tc.Module
	 */
	Tc.Module.TrackAndTrace = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxShipmentTracking', this);
			
			this.data = {};
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
			$ctx = this.$ctx,
			dataListLength = $('.mod-track-and-trace .box .data-list .row').length;

			if(dataListLength) {
				var i = 0;

                $('.mod-track-and-trace .box .data-list .row').each(function (){
                	i++;

					$('.tracking-link-' + i).click(function(e){
                        e.preventDefault();
                        $clickedLink = $(e.target);
                        self.data.trackingId = $clickedLink.text();

						var tab_id = $(this).attr('data-tab');
                        $('.row .tracking-item').removeClass('current');
                        $("#"+tab_id).removeClass('current');

                        $(this).addClass('current');
                        $("#"+tab_id).addClass('current');

                        $('body').append('<div class="modal-backdrop  in"></div>');

					});

				});

			}

			callback();
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
		}

	});

})(Tc.$);
