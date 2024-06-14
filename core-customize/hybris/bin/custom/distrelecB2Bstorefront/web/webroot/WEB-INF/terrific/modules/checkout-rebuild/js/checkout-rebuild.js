(function ($) {
    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Checkout-rebuild
     * @extends Tc.Module
     */
    Tc.Module.CheckoutRebuild = Tc.Module.extend({
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
        init: function ($ctx, sandbox, id) {
            var self = this;

            // call base constructor
            self._super($ctx, sandbox, id);

            self.$ctx = self.$ctx;

            if (self.$ctx) {
                // Object used to save delivery mode which user selected
                self.selectedDeliveryMode = '';
                // Button which redirects user to "Review and pay" page
                var $continueToReviewPayButton = $('.js-continue-to-review-pay', $ctx);

                // Checkbox which shows if billing and shipping addresses are the same
                var $billingAndShippingAddress = $('input.js-same-billing-and-delivery');
                // On page load we are setting this to null since some users dont have this checkbox
                var isBillingAndShippingAddress = null;
                // Get customer type
                var customerType = $('.js-billing-form', $ctx).data('customer-type');
                // For B2E and GUEST we don't have delivery form
                var shippingNotAvailable = customerType === 'B2E' || customerType === 'GUEST';

                // If checkbox exists on page
                if ($billingAndShippingAddress.length) {
                    // Set value TRUE/FALSE based on is checkbox checked or unchecked
                    isBillingAndShippingAddress = $billingAndShippingAddress.is(':checked');
                }

                // Set selected delivery mode on page load so we know which mode is selected
                self.setSelectedDeliveryOption($('input[type="radio"].js-cr-delivery-option:checked', self.$ctx).val());

                // Create object which will have informations about if user populated everything before he/she can proceed to next page
                window.CheckoutDeliveryPage = {
                    isBillingAndShippingAddress: isBillingAndShippingAddress,
                    canContinueCheck: function () {
                        var self = this;

                        setTimeout(function () {
                            // Get checked delivery option
                            var $deliveryCheckedOption = $('input[name="deliveryOption"]:checked');
                            // If user has selected delivery option, return TRUE
                            var isDeliveryOption = !!$deliveryCheckedOption.length;
                            // Getting value from hidden input which on page load have value from BE if billing address is populated or not
                            var isBillingAddress = $('.js-billing-address-selected', $ctx).val() === 'true';
                            // Returns TRUE if shipping address is selected, otherwise return FALSE
                            var isShippingAddress = $('input[name="shippingAddressItem"]', $ctx).is(':checked');

                            // If delivery option is selected and option is "Pick up"
                            if (isDeliveryOption && $deliveryCheckedOption.val() === 'SAP_A1') {
                                // Set shipping to TRUE since we are not showing it on page for "Pick up"
                                isShippingAddress = true;
                            }

                            // If checkbox exists on page
                            if ($billingAndShippingAddress.length) {
                                // If checkbox exists on page (value is TRUE or FALSE, not NULL)
                                if (self.isBillingAndShippingAddress !== null) {
                                    // If checkbox for same addresses is checked, mark shipping as TRUE
                                    if (self.isBillingAndShippingAddress) {
                                        isShippingAddress = true;
                                    }
                                }
                            }

                            // For B2E and GUEST, we dont have shipping addresses, so we are forcing shipping flag to TRUE so button can be enabled
                            if (shippingNotAvailable) {
                                isShippingAddress = true;
                            }

                            // If all forms are hidden, that means that user is not in "edit mode"
                            var editModeClosed = $('form.js-iv-form:visible', $ctx).length === 0;

                            // Create bool which check if all conditions are fulfilled
                            var canContinue = isDeliveryOption && isBillingAddress && isShippingAddress && editModeClosed;
                            // Enable/disable button based on state above
                            $continueToReviewPayButton.prop('disabled', !canContinue);
                        }, 100);
                    },
                };

                // Note: "codiceItalyValid" is updated on page load in "checkout-rebuild-block.skin.review-pay-codice.js" if codice is visible (Italy only)
                // Note: "summaryCheckedFrance" is updated as usec checks/unchecks summary radio (France only)
                window.CheckoutReviewPayPage = {
                    paymentOptionTitle: '',
                    paymentOption: false,
                    creditCardSelected: false,
                    codiceItalyValid: true,
                    summaryCheckedFrance: $('.js-summary-checkbox').length === 0,
                    purchaseClicked: false,
                    resetIframe: false,
                    updatePayment: function () {
                        this.paymentOption = $('input.js-payment-option').is(':checked');
                        this.creditCardSelected = $('input.js-cc-selection').is(':checked');

                        if (this.paymentOption) {
                            this.paymentOptionTitle = $('input.js-payment-option:checked').val();
                        }
                    },
                    purhaseButtonStateCheck: function () {
                        var self = this;

                        // Short delay just to be sure that DOM objects are updated
                        setTimeout(function () {
                            var creditCardValid = self.paymentOption;
                            var enablePaymentButton = false;

                            if (self.paymentOptionTitle === 'CreditCard') {
                                creditCardValid = self.creditCardSelected;
                            }

                            enablePaymentButton = self.codiceItalyValid && self.summaryCheckedFrance && creditCardValid;

                            $('.js-purchase-button').prop('disabled', !enablePaymentButton);
                        }, 100);
                    }
                };

                window.CheckoutReviewPayPage.updatePayment();
                window.CheckoutReviewPayPage.purhaseButtonStateCheck();

                setTimeout(function () {
                    Tc.Utils.bindSticky($ctx);
                }, 100);

                // On page load, since we have edit mode, we need to check if button should be disabled when edit mode is opened
                window.CheckoutDeliveryPage.canContinueCheck();

                // When user wants to edit something, we will scroll to desired edit section
                self.handleScrollToEdit($(self.$ctx));
                // Gett content height from iframe so we can set height on our element
                self.handleIframeDynamicHeight($(self.$ctx));
                // If Double Opt In Msg is shown, hide it after a while
                self.handleDoubleOptInMsg();
            }
        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function (callback) {
            var $ctx = this.$ctx,
                self = this;

            // Code...

            callback();
        },


        /**
         * Hook function to trigger your events.
         *
         * @method after
         * @return void
         */
        after: function () {
            var $ctx = this.$ctx,
                self = this;

            // Code...
        },


        getSelectedDeliveryOption: function () {
            return this.selectedDeliveryMode;
        },


        setSelectedDeliveryOption: function (selectedDeliveryOption) {
            this.selectedDeliveryMode = selectedDeliveryOption;
        },


        isPickupOption: function () {
            return this.getSelectedDeliveryOption() === 'SAP_A1';
        },


        handleScrollToEdit: function ($ctx) {
            var scrollToValue = $ctx.data('scroll-to');
            var $scrollToSection = $('#' + scrollToValue + 'DetailsBlock', $ctx);

            if ($scrollToSection.length) {
                setTimeout(function () {
                    if (scrollToValue === 'delivery') {
                        $('.js-da-list-item.is-selected-address').find('.js-da-edit').trigger('click');
                    }

                    $('html, body').animate({
                        scrollTop: $scrollToSection.offset().top - 200
                    }, 300);
                }, 1000);
            }
        },


        handleIframeDynamicHeight: function ($ctx) {
            var $iframeWrapper = $('.js-cc-form-payment-wrapper', $ctx);

            if ($iframeWrapper.length) {
                window.addEventListener('message', function (e) {
                    // Get the sent data
                    var data = e.data;

                    if (data.dynamicHeight) {
                        $iframeWrapper.height(data.dynamicHeight);
                    }
                });
            }
        },


        handleDoubleOptInMsg: function () {
            var $doubleOptInMessage = $('.js-doubleOptInInfoMessage');

            if ($doubleOptInMessage.length) {
                setTimeout(function () {
                    $doubleOptInMessage.hide(300);
                }, 5000);
            }
        }
    });
})(Tc.$);

Tc.Utils.checkoutRebuildHandleErrorOnResponse = function (data, $form) {
    // If in response we have "field" key, we should iterate through fields which triggered error
    // Otherwise we need to show global messages

    if (data.responseJSON) {
        if (data.responseJSON[0].field) {
            if ($form.length) {
                // Get errors from response
                var errors = data.responseJSON;

                // Iterate through each error from BE
                $.each(errors, function (name, val) {
                    // Get name of field which throws error
                    var errorFieldName = val.field;
                    // Get error element in form
                    var $errorField = $form.find('[name="' + errorFieldName + '"]');
                    // Trigger inline validation on error field
                    Tc.Utils.handleInlineValidationErrors($errorField);

                    // Since Libphone plugin throws different states then BE validation, we will force here to choose it based on BE
                    if ($errorField.hasClass('js-libphonenumber')) {
                        Tc.Utils.toggleInlineValidationErrorMsg($errorField, 'phonenumber', false);
                    }
                });

                setTimeout(function () {
                    window.CheckoutDeliveryPage.canContinueCheck();
                }, 300);
            }
        } else {
            Tc.Utils.globalMessagesTemplate(data.responseJSON);

            setTimeout(function () {
                // Update sticky position
                Tc.Utils.triggerSticky($('.js-cr'));
            }, 200);
        }
    }
};
