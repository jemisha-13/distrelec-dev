(function($) {

	Tc.Module.Register.Form = function (parent) {

		this.on = function (callback) {

			var $ctx = this.$ctx;
			var self = this;

			this.validationErrorDropdown = this.$$('#tmpl-registration-validation-error-dropdown').html();
			this.validationErrorEmpty = this.$$('#tmpl-registration-validation-error-empty').html();
			this.validationErrorEmail = this.$$('#tmpl-registration-validation-error-email').html();
			this.validationErrorCaptcha = this.$$('#tmpl-registration-validation-error-captcha').html();
			this.validationErrorCheckbox = this.$$('#tmpl-registration-validation-error-checkbox').html();
			this.validationErrorLength = this.$$('#tmpl-registration-validation-error-length').html();
			this.validationErrorMinMaxDigits = this.$$('#tmpl-registration-validation-min-max-digits').html();
			this.validationErrorVatItaly = this.$$('#tmpl-registration-validation-error-vat-italy').html();
			this.euCountries = ["AT", "BE", "BG", "HR", "CZ", "DK", "EE", "FI", "FR", "DE", "GR", "HU", "IE", "IT", "LV", "LI", "LT", "LU", "MT", "MC", "NL", "PL", "PT", "RO", "SK", "SI", "ES", "SE", "CY", "GB"];

			if (window.location.pathname.indexOf("b2b") > -1 ){
				$('#form-b2b').removeClass('hidden');
				$('#form-b2c').addClass('hidden');
				$("#type-of-customer").val("b2b");
			}

			else if (window.location.pathname.indexOf("b2c") > -1 ){
				$('#form-b2c').removeClass('hidden');
				$('#form-b2b').addClass('hidden');
				$("#type-of-customer").val("b2c");
			}

			// Reload Captcha Image
			self.fire('reloadCaptcha', {}, ['captcha']);

			// Format Swedish, Czech and Slovak postal code
			$ctx.on('blur', '#register\\.postalCode', function(e) {
				if ($('#register\\.countryCode').val()==='SE' || $('#register\\.countryCode').val()==='CZ' || $('#register\\.countryCode').val()==='SK') {
					var postcode=$('#register\\.postalCode').val().replace(/[^0-9]/g,''); // Only digits allowed

					$('#register\\.postalCode').val(postcode);

					if (postcode.length === 5) {
						$('#register\\.postalCode').val(postcode.substring(0, 3) + " " + postcode.substring(3));
					}

				}

			});

			$ctx.on('click', '.btn-register', function(e) {

				e.preventDefault();

				// Format Swedish, Czech and Slovak postal code

				if ($('#register\\.countryCode').val()==='SE' || $('#register\\.countryCode').val()==='CZ' || $('#register\\.countryCode').val()==='SK') {
					var postcode=$('#register\\.postalCode').val();

					if (postcode.length === 5) {
						$('#register\\.postalCode').val(postcode.substring(0, 3) + " " + postcode.substring(3));
					}

				}

				// Format Swedish organizational number
				if ($('#register\\.countryCode').val()==='SE') {

					if (typeof $('#register\\.organizationalNumber').val() !== 'undefined' && $('#register\\.organizationalNumber').val().indexOf('-')!=-1) {
						$('#register\\.organizationalNumber').val($('#register\\.organizationalNumber').val().replace(/-/,''));
					}

				}

				//DISTRELEC-8969
				if ($('#register\\.countryCode').val() !== 'CH') {

					if ($('#register\\.vatId').val() !== undefined ){
						$('#register\\.vatId').val($('#register\\.vatId').val().replace(/ /g,''));
					}

				}

				//DISTRELEC-9087
				$('input[type=text], input[type=password]').each(function() {
					$(this).val($(this).val().trim());
				});

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

				Tc.Utils.validate($('.validate-length-ch', self.$ctx), self.validationErrorLength, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-min-max-digits', self.$ctx), self.validationErrorMinMaxDigits, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-vat-italy', self.$ctx), self.validationErrorVatItaly, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});


				if(hasValidationErrors) {
					Tc.Utils.scrollToFirstError(self.$ctx);
				} else {
					var skipCaptcha = $(this).data('skipcaptcha');
					if (skipCaptcha) {
						$(this).parents('form').submit();
					} else {
						var gid = $('.g-recaptcha',self.$ctx).eq(0).data('gid');
						if (typeof gid !== 'number') {
							gid = grecaptcha.render($('.g-recaptcha',self.$ctx)[0],{},true);
							$('.g-recaptcha',self.$ctx).eq(0).data({'gid':gid});
						}
						grecaptcha.execute(gid);
					}
				}
			});

			$ctx.on('click', '.btn-register-b2c-customer', function(e) {

				e.preventDefault();

				// Format Swedish postal code

				if ($('#register\\.countryCode').val()==='SE') {

					if ($('#register\\.postalCode').val().match(/^[0-9]{5}$/)) {
						$('#register\\.postalCode').val($('#register\\.postalCode').val().substring(0, 3) + " " +$('#register\\.postalCode').val().substring(3));
					}

					if (typeof $('#register\\.organizationalNumber').val() !== 'undefined' && $('#register\\.organizationalNumber').val().indexOf('-')!=-1) {
						$('#register\\.organizationalNumber').val($('#register\\.organizationalNumber').val().replace(/-/,''));
					}

				}

				//DISTRELEC-9087
				$('input[type=text], input[type=password]').each(function() {
					$(this).val($(this).val().trim());
				});

				var hasValidationErrors = false;

				Tc.Utils.validate($('.validate-empty-b2c',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-email-b2c',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-dropdown-b2c',self.$ctx), self.validationErrorDropdown, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.captcha', self.$ctx), self.validationErrorCaptcha, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-checkbox-b2c', self.$ctx), self.validationErrorCheckbox, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});



				if(hasValidationErrors) {
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

			$ctx.on('click', '.btn-register-b2b-customer', function(e) {

				e.preventDefault();

				// Format Swedish postal code

				if ($('#register\\.countryCode').val()==='SE') {

					if ($('#register\\.postalCode').val().match(/^[0-9]{5}$/)) {
						$('#register\\.postalCode').val($('#register\\.postalCode').val().substring(0, 3) + " " +$('#register\\.postalCode').val().substring(3));
					}

					if (typeof $('#register\\.organizationalNumber').val() !== 'undefined' && $('#register\\.organizationalNumber').val().indexOf('-')!=-1) {
						$('#register\\.organizationalNumber').val($('#register\\.organizationalNumber').val().replace(/-/,''));
					}

				}

				//DISTRELEC-9087
                $('input[type=text], input[type=password]').each(function() {
                    $(this).val($(this).val().trim());
                });

                var hasValidationErrors = false;

				Tc.Utils.validate($('.validate-empty-b2b',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-email-b2b',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-dropdown-b2b',self.$ctx), self.validationErrorDropdown, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.captcha', self.$ctx), self.validationErrorCaptcha, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				Tc.Utils.validate($('.validate-checkbox-b2b', self.$ctx), self.validationErrorCheckbox, 'triangle', function(error) {
					if(error) { hasValidationErrors = true; }
				});

				if(hasValidationErrors) {
					Tc.Utils.scrollToFirstError(self.$ctx);
				} else {
					var gid = $('.g-recaptcha',self.$ctx).eq(1).data('gid');
					if (typeof gid !== 'number') {
						gid = grecaptcha.render($('.g-recaptcha',self.$ctx)[1],{},true);
						$('.g-recaptcha',self.$ctx).eq(1).data({'gid':gid});
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

				// Reload Captcha Image
				self.fire('reloadCaptcha', {}, ['captcha']);
			});

			$ctx.on('change', '.countrySelectExportShop', function(ev) {

				var countryCode = $(this).val();

				//if is NOT an EU country
				if (self.euCountries.indexOf(countryCode) == -1){
					$('.warning-message-export-shop').addClass('hidden');
					$('.vat-id-field-export').addClass('hidden');
					$('input[name=vatId]').val('');
				}
				else{
					$('.warning-message-export-shop').removeClass('hidden');
					$('.vat-id-field-export').removeClass('hidden');
				}

				$.ajax({
					url: '/_s/shopsettings-async',
					type: 'get',
					data: {
						country: countryCode
					},
					success: function (data, textStatus, jqXHR) {
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});
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
