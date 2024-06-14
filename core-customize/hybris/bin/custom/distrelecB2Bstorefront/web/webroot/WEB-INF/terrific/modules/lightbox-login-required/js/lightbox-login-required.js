(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Lightboxloginrequired
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxLoginRequired = Tc.Module.extend({

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
			
            // subscribe to connector channel/s
            this.sandbox.subscribe('lightboxLoginRequired', this);
            this.sandbox.subscribe('lightboxStatus', this);

            // set module variables
            this.$modal = $('.modal', $ctx);
            this.$btnConfirm = $('.btn-login', $ctx);

			// Validation Errors
            this.validationErrorEmpty = this.$$('#tmpl-lightbox-login-validation-error-empty').html();
            this.validationErrorEmail = this.$$('#tmpl-lightbox-login-validation-error-email').html();
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

			// Click Submit Form
            self.$btnConfirm.off('click').on('click', function (e) {

            	var valid = true;

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if(error) { e.preventDefault(); valid = false; }
				});

				Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if(error) { e.preventDefault(); valid = false;  }
				});

				//DISTRELEC-9087
				$('#j_username').val( $('#j_username').val().trim() ); 
				$('#j_password').val( $('#j_password').val().trim() ); 				
				
                if(valid) {
	                return true;
                }
	            else{
	                // Recalculate modal height
	                Tc.Utils.calculateModalHeight(self.$modal);
	                return false;
                }

            });

            // Clear Popovers on focus
			this.$$('input').on('focus', function() {
				self.hidePopover($(this));
			});

			callback();
		},

		hidePopover: function($element) {
			Tc.Utils.hidePopover($element);
		},

        /**
         *
         * @method onOpenModal
         *
         */
        onOpenModal: function () {
            var self = this;
            self.$ctx.find('.error-box').css('display', 'none');
            self.$ctx.find('.field').val('');
            self.$modal.modal();

	        // Set modal height
	        Tc.Utils.calculateModalHeight(self.$modal);

            $(window).on('keydown.LightboxLoginRequired', function (e) {
                if (e.keyCode === 13) {
                    self.$btnConfirm.trigger('click');
                }
            });
        },

        /**
         *
         * @method hideModal
         *
         */
        hideModal: function () {
            this.$modal.modal('hide');
            $(window).off('keydown.LightboxLoginRequired');
        },

       /**
		 *
		 * @method onLightboxConfirm - keystroke extension
		 *
		 */
        onLightboxConfirm: function(){
        	Tc.Utils.lightboxConfirm(this);
        }
});

})(Tc.$);
