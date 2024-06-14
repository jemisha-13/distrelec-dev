(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Register
	 * @extends Tc.Module
	 */
	Tc.Module.FormCatalogOrder = Tc.Module.extend({

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
			this.validationErrorEmpty = this.$$('#tmpl-form-catalog-order-validation-error-empty').html();
			this.validationErrorDropdown = this.$$('#tmpl-form-catalog-order-validation-error-dropdown').html();
			this.validationErrorEmail = this.$$('#tmpl-form-catalog-order-validation-error-email').html();
			this.validationErrorCaptcha = this.$$('#tmpl-form-catalog-order-validation-error-captcha').html();
			this.validationErrorCheckboxGroup = this.$$('#tmpl-form-catalog-order-validation-error-checkboxgroup').html();
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

			// Lazy Load SelectBoxIt Dropdown
			if(!Modernizr.touch && !Modernizr.isie7) {
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						self.$$('.selectpicker').selectBoxIt({
							autoWidth : false
						});
					}
				}]);
			}

			//
			// Validate Elements
			this.$ctx.on('click', '.btn-primary', function(e) {

				e.preventDefault();
				var isValid = true;

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if (error) { isValid = false; }
				});

				Tc.Utils.validate($('.validate-dropdown',self.$ctx), self.validationErrorDropdown, 'triangle', function(error) {
					if (error) { isValid = false; }
				});

				Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if (error) { isValid = false; }
				});

				Tc.Utils.validate($('.validate-checkbox-group', self.$ctx), self.validationErrorCheckboxGroup, 'triangle', function(error) {
					if (error) { isValid = false; }
				});

				if(isValid === true) {
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