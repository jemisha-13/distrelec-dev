(function($) {

	/**
	 * Dev Skin implementation for the module Layout.
	 *
	 * @author Nabil Benothman
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Survey.Form = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			var $ctx = this.$ctx,
				self = this;

			//
			// Validation Error Messages
			this.validationErrorDropdown = this.$$('#tmpl-survey-validation-error-dropdown').html();
			this.validationErrorEmpty = this.$$('#tmpl-survey-validation-error-empty').html();
			this.validationErrorEmail = this.$$('#tmpl-survey-validation-error-email').html();
			this.validationErrorCaptcha = this.$$('#tmpl-survey-validation-error-captcha').html();
			this.validationErrorCheckbox = this.$$('#tmpl-survey-validation-error-checkbox').html();
			this.validationErrorLength = this.$$('#tmpl-survey-validation-error-length').html();

			 
			//for some reason .captcha-img looses its attribute 'src' when toggleing hidden class
			$('.captcha-img').attr('src', '/captcha');			
			
			//
			// Validate Elements
			$ctx.on('click', '.btn-survey', function(e) {
				
				var hasValidationErrors = false;

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-dropdown',self.$ctx), self.validationErrorDropdown, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.captcha', self.$ctx), self.validationErrorCaptcha, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-checkbox', self.$ctx), self.validationErrorCheckbox, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-length', self.$ctx), self.validationErrorLength, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				e.preventDefault();
				if(hasValidationErrors){
					// Scroll to the first error
					Tc.Utils.scrollToFirstError(self.$ctx);
				} else {
					var gid = $('.g-recaptcha',self.$ctx).eq(0).data('gid');
					if (typeof gid !== 'number') {
						gid = grecaptcha.render($('.g-recaptcha',self.$ctx)[0],{},true);
						$('.g-recaptcha',self.$ctx).eq(0).data({'gid':gid});
					}
					grecaptcha.execute(gid);
				}
			});
			
			
			$ctx.on('change', '.type-of-customer', function(e) {
				var typeOfCustomer = $("#type-of-customer").val();
				if (typeOfCustomer === "b2b"){
					$('#form-b2b').removeClass('hidden');
					$('#form-b2c').addClass('hidden');
				}
				else{
					$('#form-b2c').removeClass('hidden');
					$('#form-b2b').addClass('hidden');					
				}
 				
				//for some reason .captcha-img looses its attribute 'src' when toggleing hidden class
				$('.captcha-img').attr('src', '/captcha');
			});
			
			//
			// SelectBoxIt Dropdown
			if(!Modernizr.touch && !Modernizr.isie7) {

				// Lazy Load dropdown
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						self.$$('.selectpicker').selectBoxIt({
							autoWidth : false
						});
					}
				}]);
			}

			// calling parent method
			parent.on(callback);
		};

		this.after = function () {
			// calling parent method
			parent.after();
		};


	};

})(Tc.$);
