(function($) {

	/**
	 * Product Skin Bom implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.Product.Bom = function (parent) {

		this.on = function (callback) {

            var self = this;

			// subscribe to connector channel/s
			this.sandbox.subscribe('cartlistBulkAction', this);
			this.$numeric = this.$$('.numeric');
			var mod = this;

            // + -
            Tc.Utils.numericStepper(self.$numeric, {
                error: function(value, minErrorMsg) {// disable send if quantity below quotation threshold
					$(self.$numeric).parents('.skin-product-bom').addClass('error');
                    self.checkProductValidation();
                },
                warning: function(value, minErrorMsg) {// enable send if its only a warning, value has been autocorrected
                    $(self.$numeric).parents('.skin-product-bom').removeClass('error');
                    setTimeout(function(){
                        self.checkProductValidation();
                    }, 500);
                },
                success: function(value) {// reset disable
                    $(self.$numeric).parents('.skin-product-bom').removeClass('error');
                    setTimeout(function(){
                        self.checkProductValidation();
                    }, 500);
                }
            });


			parent.on(callback);
		};

        this.checkProductValidation = function () {

            var	$allBomProducts = $('.skin-product-bom'),
                isQuantiryError = false,
                $errorContainer= $('.skin-global-messages-component.bom'),
                $errorContent= $('.skin-global-messages-component.bom .messages-component__label'),
                $errorMessage = '';

            $.each($allBomProducts, function (index, item) {

                if ( $(item).find('.numeric').hasClass('numeric-error') ) {
                    isQuantiryError = true;
                    $(item).addClass('error');

                    var message = $(item).find('.popover-content').html();
                    $errorMessage = $errorMessage + message +'</br>';

                } else {
                    $(item).removeClass('error');
                }

                if ( $(item).hasClass('active')  ) {

                    if ( $(item).find('.numeric').hasClass('numeric-error') ) {
                        $('.btn-add-cart').attr('disabled', 'disabled');
                        $('.btn-add-shopping').attr('disabled', 'disabled');
                    } else {
                        $('.btn-add-cart').removeAttr('disabled', 'disabled');
                        $('.btn-add-shopping').removeAttr('disabled', 'disabled');
                    }

                }

            });

            if (isQuantiryError) {
                $($errorContent).html( $errorMessage );
                $errorContainer.removeClass('hidden');
                // Since calls are queued, we need to add .stop() function, otherwise user won't be able to scroll
                $('html, body').stop(true, false).animate({
                    scrollTop: $(".skin-product-bom.error").offset().top - 150
                }, 1000);
            } else {
                $errorContainer.addClass('hidden');
            }

            return isQuantiryError;

        };

	};

})(Tc.$);
