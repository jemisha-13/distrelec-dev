(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Register
	 * @extends Tc.Module
	 */
	Tc.Module.Register = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxGuestB2b', this);
			this.sandbox.subscribe('lightboxLoginRequired', this);
			this.sandbox.subscribe('captcha', this);

			// validation helpers
			this.validationErrorDropdown = this.$$('#tmpl-registration-validation-error-dropdown').html();
			this.validationErrorEmpty = this.$$('#tmpl-registration-validation-error-empty').html();
			this.validationErrorEmail = this.$$('#tmpl-registration-validation-error-email').html();
			this.validationErrorCaptcha = this.$$('#tmpl-registration-validation-error-captcha').html();
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
				shopSettings = $('#backenddata .shopsettings').data();
			
			$ctx.on('click', '.btn-register-b2b', function(ev) {
				ev.preventDefault();
				// Show Lightbox
				self.fire('openModal', ['lightboxGuestB2b']);
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
