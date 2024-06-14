(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Product-image-gallery
	 * @extends Tc.Module
	 */
	Tc.Module.CalPlusBanner = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxCalibration', this);

			// bind handlers to module context
			this.initializeModule = $.proxy(this.initializeModule, this);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {

			var self = this;
			
			// subscribe to connector channel/s
			this.sandbox.subscribe('toolItems', this);
			this.sandbox.subscribe('lightboxCalibration', this);
			
			// Context & Click Handler
			this.onClick = $.proxy(this, 'onClick');
			this.$ctx.on('click', this.onClick);
			
			$('.skin-layout-cart').find('.right-recommendations').css('margin-top', '31px');

			callback();
		}

	});

})(Tc.$);

