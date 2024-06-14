(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Logo
	 * @extends Tc.Module
	 */
	Tc.Module.Logo = Tc.Module.extend({

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

			// Fixer CT
			this.$fixer = $('<span class="fixed"></span>');
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
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
		},

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		cloneLogo: function() {
			var $ctx = this.$ctx
				,self = this

				// Refs
				,$a = $('a', $ctx)
				,$img = $('.img', $a)
				;

			if ($img.length) {
				// Clone img
				var $imgFixed = $img.clone(false, false)
						.removeClass('img')
					;

				// Insert img in fixer in link
				$a.append( self.$fixer.append($imgFixed) );
			}
		}
	});

})(Tc.$);
