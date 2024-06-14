(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class FormSupportCallmeback
	 * @extends Tc.Module
	 */
	Tc.Module.FormSupportCallmeback = Tc.Module.extend({

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

			// validation helpers
			this.validationErrorDropdown = this.$$('#tmpl-support-validation-error-dropdown').html();
			this.validationErrorEmpty = this.$$('#tmpl-support-validation-error-empty').html();
			this.validationErrorEmail = this.$$('#tmpl-support-validation-error-email').html();
			this.validationErrorCaptcha = this.$$('#tmpl-support-validation-error-captcha').html();
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

			// Lazy Load SelectBoxIt Dropdown
			if(!Modernizr.touch && !Modernizr.isie7) {
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						self.$$('.selectpicker').selectBoxIt({
							autoWidth: false
						});
					}
				}]);
			}


			// Validate Elements
			$ctx.on('click', '.btn-primary', function(e) {

				var isValid = true;

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if(error) { e.preventDefault(); isValid = false; }
				});

				Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if(error) { e.preventDefault(); isValid = false; }
				});

				Tc.Utils.validate($('.validate-dropdown',self.$ctx), self.validationErrorDropdown, 'triangle', function(error) {
					if(error) { e.preventDefault(); isValid = false; }
				});

				Tc.Utils.validate($('.captcha', self.$ctx), self.validationErrorCaptcha, 'triangle', function(error) {
					if(error) { e.preventDefault(); isValid = false; }
				});

				if(isValid === true) {
					self.closest('form').submit();
				} else {
					// Scroll to the first error
					Tc.Utils.scrollToFirstError($ctx);
				}
			});

			callback();
		}

	});

})(Tc.$);
