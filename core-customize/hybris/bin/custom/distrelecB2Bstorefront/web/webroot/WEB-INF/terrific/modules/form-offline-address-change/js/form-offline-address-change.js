(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Register
	 * @extends Tc.Module
	 */
	Tc.Module.FormOfflineAddressChange = Tc.Module.extend({

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

			//
			// Validation Error Messages
			this.validationErrorEmpty = this.$$('#tmpl-form-offline-address-change-validation-error-empty').html();
			this.validationErrorCaptcha = this.$$('#tmpl-form-offline-address-change-validation-error-captcha').html();
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {

			var self = this;

			//
			// Validate Elements
			this.$ctx.on('click', '.btn-primary', function(e) {

				e.preventDefault();
				var isValid = true;

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if (error) {isValid = false;}
				});

				if (isValid === true) {
					var gid = $('.g-recaptcha',self.$ctx).eq(0).data('gid');
					if (typeof gid !== 'number') {
						gid = grecaptcha.render($('.g-recaptcha',self.$ctx)[0],{},true);
						$('.g-recaptcha',self.$ctx).eq(0).data({'gid':gid});
					}
					grecaptcha.execute(gid);
				} else {
					// Scroll to the first error
					Tc.Utils.scrollToFirstError(self.$ctx);
				}
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
