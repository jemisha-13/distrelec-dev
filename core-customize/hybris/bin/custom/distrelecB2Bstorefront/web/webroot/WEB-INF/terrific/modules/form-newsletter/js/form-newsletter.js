(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class FormNewsletter
	 * @extends Tc.Module
	 */
	Tc.Module.FormNewsletter = Tc.Module.extend({

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
			this.validationErrorEmpty = this.$$('#tmpl-form-newsletter-error-empty').html();
			this.validationErrorEmail = this.$$('#tmpl-form-newsletter-error-email').html();
			this.validationErrorDropdown = this.$$('#tmpl-form-newsletter-error-dropdown').html();
			this.validationErrorCheckbox = this.$$('#tmpl-form-newsletter-error-checkbox').html();
			this.validationErrorCheckboxGroup = this.$$('#tmpl-form-newsletter-error-checkboxgroup').html();
			this.validationErrorCaptcha = this.$$('#tmpl-form-newsletter-error-captcha').html();
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

			// delegate module click handler: validate email
			$ctx.on('click', '.b-save', function(e) {

				var isValid = true;
				e.preventDefault();

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if(error) {isValid = false; }
				});

				Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if(error) {isValid = false; }
				});

				Tc.Utils.validate($('.validate-dropdown',self.$ctx), self.validationErrorDropdown, 'triangle', function(error) {
					if(error) {isValid = false;  }
				});

				Tc.Utils.validate($('.validate-checkbox', self.$ctx), self.validationErrorCheckbox, 'triangle', function(error) {
					if(error) {isValid = false;  }
				});

				Tc.Utils.validate($('.validate-checkbox-group', self.$ctx), self.validationErrorCheckboxGroup, 'triangle', function(error) {
					if(error) {isValid = false;  }
				});

				Tc.Utils.validate($('.captcha', self.$ctx), self.validationErrorCaptcha, 'triangle', function(error) {
					if(error) {isValid = false;  }
				});

				// DISTRELEC-17904
				var inputField = $('.validate-email');
				if (inputField && inputField.length) {
					var inputVal = inputField.val();
					if (inputVal) {
						inputField.val(inputVal.trim());
					}
				}

                $.ajax({
                    type: "HEAD",
                    dataType: 'jsonp',
                    timeout:  5000,
                    url: document.location.pathname + "?param=" + new Date(),
                    error: function() {
                        //internet is down
                        $('.newsletter-wrapper .downtime-error').addClass('hidden');
                        $('.newsletter-wrapper .downtime-error').first().removeClass('hidden');
                    	return false;
					},
                    success: function() {
                        //internet connection working
                    	return true;
                    }
                });

				if(isValid === true) {
					Bootstrapper.ensEvent.trigger("newsletter subscription success");
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
		}

	});

})(Tc.$);
