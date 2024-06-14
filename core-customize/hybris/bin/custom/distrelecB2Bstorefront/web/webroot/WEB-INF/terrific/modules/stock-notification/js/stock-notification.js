(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Logo
	 * @extends Tc.Module
	 */
	Tc.Module.StockNotification = Tc.Module.extend({

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
				$ctx = this.$ctx;

            var codeProduct = []; // Product codes array

            $('.product-code').each(function() {
            	var productValue = $(this).val();
                codeProduct.push(productValue);
            });

            codeProduct.join(","); // Join product codes
            var productString = codeProduct.toString(); // Turn product codes to string to allow to send in ajax

            $('.js-stock-notification-form').on('submit', function (event) {
                event.preventDefault ? event.preventDefault() : (event.returnValue = false);

                var $emailInput = $('.stock-notification__form-content__emailinput'),
                    $stockNotificationEmail = $emailInput.val();
                
                var hasValidationErrors = self.checkOutOfStockValidation();

                if (!hasValidationErrors) {
                    $emailInput.removeClass('error');

					var outOfStockFlag = ($('.skin-layout-back-order').length ? '/backorder/zeroStock' : '/notifyZeroStock');

                    $.ajax({
                        type : "POST",
                        url : outOfStockFlag,
                        data : {
                            customerEmail : $stockNotificationEmail,
                            articleNumber : productString
                        },
                        success : function(result) {

                            if(result){
                                $(".stock-notification__form").addClass('hidden');
                                $(".stock-notification__success").removeClass('hidden');
                                $(".stock-notification__failure").addClass('hidden');

                            }else{
                                $(".stock-notification__form").addClass("hidden");
                                $(".stock-notification__success").addClass("hidden");
                                $(".stock-notification__failure").removeClass('hidden');

                            }

                        },
                        error : function(result) {
                        }
                    });
                }

            });

			callback();
		},

        checkOutOfStockValidation: function () {

			var hasValidationErrors = false,
				isValidEmail,
				isEmpty,
				$emptyValid = $('.stock-notification--error.error-empty'),
				$emailValid = $('.stock-notification--error.error-emailvalid'),
				$emailInput = $('.stock-notification__form-content__emailinput'),
				$stockNotificationEmail = $emailInput.val();

			$('.stock-notification--error').addClass('hidden');


			isEmpty = ( $emailInput.val().length < 1 );

			if (isEmpty) {
				$emptyValid.removeClass('hidden');
				hasValidationErrors = true;
				$emailInput.addClass('error');
			} else {
				$emptyValid.addClass('hidden');
				hasValidationErrors = false;
				$emailInput.removeClass('error');
			}

			isValidEmail = this.validateEmail( $stockNotificationEmail );

			if (!isEmpty) {
				if (isValidEmail) {
					$emailValid.addClass('hidden');
					hasValidationErrors = false;
					$emailInput.removeClass('error');
				} else {
					$emailValid.removeClass('hidden');
					hasValidationErrors = true;
					$emailInput.addClass('error');
				}
			}

			return hasValidationErrors;

		},

		validateEmail: function (inputText) {
			var mailformat = /^[-!#$%&'*+\./0-9=?A-Z^_`a-z{|}~]+@[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+.[-!#$%&'*+\./0-9=?A-Z^_`a-z{|}~]+$/;
			if(inputText.match(mailformat)) {
				return true;
			} else {
				return false;
			}
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
        }

	});

})(Tc.$);
