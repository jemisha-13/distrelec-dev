(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Register
	 * @extends Tc.Module
	 */
	Tc.Module.FormSeminarSignup = Tc.Module.extend({

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
			this.validationErrorEmpty = this.$$('#tmpl-form-seminar-signup-validation-error-empty').html();
			this.validationErrorDropdown = this.$$('#tmpl-form-seminar-signup-validation-error-dropdown').html();
			this.validationErrorEmail = this.$$('#tmpl-form-seminar-signup-validation-error-email').html();
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

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if(error) { e.preventDefault(); }
				});

				Tc.Utils.validate($('.validate-dropdown',self.$ctx), self.validationErrorDropdown, 'triangle', function(error) {
					if(error) { e.preventDefault(); }
				});

				Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if(error) { e.preventDefault(); }
				});
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
