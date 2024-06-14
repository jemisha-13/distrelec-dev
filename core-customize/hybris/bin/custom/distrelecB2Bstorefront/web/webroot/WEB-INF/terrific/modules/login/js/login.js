(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Login
	 * @extends Tc.Module
	 */
	Tc.Module.Login = Tc.Module.extend({

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


			// validation helpers for login checkout
			this.validationErrorEmpty = this.$$('#tmpl-login-validation-error-empty').html();
			this.validationErrorEmail = this.$$('#tmpl-login-validation-error-email').html();

			this.rememberme = localStorage.getItem("LoginRememberMe");
			this.emailStore = localStorage.getItem("emailStore");

			this.$captcha = this.$$('#loginForm').find('.recaptcha');

			if(this.emailStore !== null) { // Populates email input field with stored value
				$('#j_username').val(this.emailStore);
			}

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
				self = this
			;

			/**
			 * Call recaptcha on checkout login
			 *
			 * @method on
			 * @return void
			 */
			recaptchaForCheckoutLogin = function() {
				if (typeof self.$captcha !== 'undefined') {
					if (self.$captcha.length > 0) {
						e.preventDefault();
						var gid = self.$captcha.eq(0).data('gid');
						if (typeof gid !== 'number') {
							gid = grecaptcha.render(self.$captcha[0],{},true);
							self.$captcha.eq(0).data({'gid':gid});
						}
						grecaptcha.execute(gid);
					}
				}
			};

			// delegate module click handler: validation
			$ctx.on('click', '.b-login', function(e) {

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if(error) { e.preventDefault(); }
				});

				Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if(error) { e.preventDefault(); }
				});

				//DISTRELEC-9087
				$('#j_username').val( $('#j_username').val().trim() );
				$('#j_password').val( $('#j_password').val().trim() );


				// only validate captcha if shown (currently after 3 failed submits)
				recaptchaForCheckoutLogin();
			});

            // delegate module click handler: validation
            $ctx.on('click', '.js-row-submit .js-login-continue', function(e) {

                Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
                    if(error) { e.preventDefault(); }
                });

                Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
                    if(error) { e.preventDefault(); }
                });

                //DISTRELEC-9087
                $('#j_username').val( $('#j_username').val().trim() );
                $('#j_password').val( $('#j_password').val().trim() );

                // only validate captcha if shown (currently after 3 failed submits)
				recaptchaForCheckoutLogin();
            });

            $ctx.on('click', '#j_remember', function(e) {
                $('#j_remember').prop( 'checked', $(this).is(":checked") );
                localStorage.setItem("LoginRememberMe", $(this).prop( 'checked') );

                var inputEmail = document.getElementById("j_username");
                localStorage.setItem("emailStore", inputEmail.value);
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
			// code...
		}

	});

})(Tc.$);
