(function($) {

    /**
     * This module implements the delivery options on checkout page
     *
     * @namespace Tc.Module
     * @class CheckoutRebuildBlock
     * @skin OrderSummary
     */
    Tc.Module.CheckoutRebuildBlock.ReviewPayMethod = function (parent) {
        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {
            // If we want to call "CheckoutRebuildBlock" on load
            // parent.on(callback);

            var self = this,
                $ctx = self.$ctx;

            this.bindPaymentMethodSelection($ctx);
            this.bindCreditCardActions($ctx);
            this.bindOrderReference($ctx);
            this.bindFutureInvoiceRequest($ctx);

            callback();
        };

        this.bindPaymentMethodSelection = function ($ctx) {
            var $thisMod = this.$ctx;
            var $paymentOption = $('.js-payment-option', $ctx);

            if ($paymentOption.length) {
                $paymentOption.on('change', function () {
                    var $currentOption = $(this);
                    var currentValue = $currentOption.val();
                    $thisMod.addClass('is-loading');
                    // Disable button until request is finished
                    $('.js-purchase-button').prop('disabled', true);

                    // Close all error msgs
                    Tc.Utils.globalMessagesTriggerClose();

                    if (window.CheckoutReviewPayPage.purchaseClicked) {
                        setTimeout(function () {
                            Tc.Utils.resetPurchaseIframe();
                        }, 100);
                    }

                    $.ajax({
                        url: '/checkout/review-and-pay/select/payment',
                        type: 'PUT',
                        data: currentValue,
                        success: function() {
                            var $purchaseButton = $('.js-purchase-button');

                            if (currentValue.indexOf('Invoice') !== -1) {
                                currentValue = 'Invoice';
                            }

                            // Updating label of button as user is changing payment methods
                            var purchaseButtonTranslation = $purchaseButton.attr('data-translation-' + currentValue);
                            $purchaseButton.html(purchaseButtonTranslation);
                        },
                        error: function (error) {
                            Tc.Utils.checkoutRebuildHandleErrorOnResponse(error);
                        },
                        complete: function () {
                            setTimeout(function () {
                                $thisMod.removeClass('is-loading');
                            }, 50);

                            window.CheckoutReviewPayPage.updatePayment();
                            window.CheckoutReviewPayPage.purhaseButtonStateCheck();
                        }
                    });
                });
            }
        };

        this.bindCreditCardActions = function ($ctx) {
            var $thisMod = this.$ctx;
            var $creditCardItem = $('.js-cc-item', $ctx);
            var $creditCardSelection = $('.js-cc-selection', $ctx);
            var $creditCardRemove = $('.js-cc-remove', $ctx);
            var $creditCardSetDefault = $('.js-cc-set-as-default', $ctx);

            $creditCardSelection.on('change', function () {
                var $currentOption = $(this);
                var currentValue = $currentOption.val();
                $thisMod.addClass('is-loading');

                // Close all error msgs
                Tc.Utils.globalMessagesTriggerClose();

                // Disable button until request is finished
                $('.js-purchase-button').prop('disabled', true);

                if (window.CheckoutReviewPayPage.purchaseClicked) {
                    Tc.Utils.resetPurchaseIframe();
                }

                $.ajax({
                    url: '/checkout/review-and-pay/select/paymentInfo',
                    type: 'PUT',
                    data: currentValue,
                    success: function() {
                        if (currentValue === 'NEW_CARD') {
                            window.CheckoutReviewPayPage.updatePayment();

                            setTimeout(function () {
                                var creditCardValid = window.CheckoutReviewPayPage.paymentOption;

                                if (window.CheckoutReviewPayPage.paymentOptionTitle === 'CreditCard') {
                                    creditCardValid = window.CheckoutReviewPayPage.creditCardSelected;
                                }

                                if (window.CheckoutReviewPayPage.codiceItalyValid && window.CheckoutReviewPayPage.summaryCheckedFrance && creditCardValid) {
                                    // Trigger iframe
                                    $('.js-purchase-button').trigger('click');

                                    if ($(window).width() <= 991) {
                                        $([document.documentElement, document.body]).animate({
                                            scrollTop: $('.js-cc-form-payment-wrapper').offset().top
                                        }, 1000);
                                    }
                                }
                            }, 300);
                        }
                    },
                    error: function (error) {
                        Tc.Utils.checkoutRebuildHandleErrorOnResponse(error);
                    },
                    complete: function () {
                        setTimeout(function () {
                            $thisMod.removeClass('is-loading');
                        }, 50);

                        window.CheckoutReviewPayPage.updatePayment();
                        window.CheckoutReviewPayPage.purhaseButtonStateCheck();
                    }
                });
            });

            $creditCardSetDefault.unbind('click').on('click', function () {
                var $clickedButton = $(this);
                var $scopeCreditCardItem = $clickedButton.closest($creditCardItem);
                var $input = $scopeCreditCardItem.find($creditCardSelection);
                var creditCardId = $input.val();

                // Show loading animation
                $thisMod.addClass('is-loading');
                // Prevent multiple clicks
                $clickedButton.prop('disabled', true);
                // Remove class for hiding "Set as default" button on all addresses
                $creditCardItem.removeClass('is-default-address');

                $.ajax({
                    url: '/checkout/review-and-pay/payment/set-default-paymentInfo',
                    type: 'PUT',
                    data: creditCardId,
                    success: function() {
                        // Add class on current address so we can hide "Set as default" button
                        $scopeCreditCardItem.addClass('is-default-address');
                    },
                    error: function (data) {
                        Tc.Utils.checkoutRebuildHandleErrorOnResponse(data);
                    },
                    complete: function () {
                        // Enable button again
                        $clickedButton.prop('disabled', false);
                        // Hide loading animation
                        $thisMod.removeClass('is-loading');
                    }
                });
            });

            $creditCardRemove.unbind('click').on('click', function () {
                var $clickedButton = $(this);
                var $scopeCreditCardItemCol = $clickedButton.closest('.js-cc-item-col');
                var $input = $scopeCreditCardItemCol.find($creditCardSelection);
                var creditCardId = $input.val();

                // Show loading animation
                $thisMod.addClass('is-loading');
                // Prevent multiple clicks
                $clickedButton.prop('disabled', true);


                if (window.CheckoutReviewPayPage.purchaseClicked) {
                    setTimeout(function () {
                        Tc.Utils.resetPurchaseIframe();
                    }, 100);
                }

                $.ajax({
                    url: '/checkout/review-and-pay/paymentInfo',
                    type: 'DELETE',
                    data: creditCardId,
                    success: function() {
                        // Animate removal of item
                        $scopeCreditCardItemCol.hide(300);

                        setTimeout(function() {
                            // After animation, remove item from DOM
                            $scopeCreditCardItemCol.remove();

                            // Update sticky position
                            Tc.Utils.triggerSticky($('.js-cr'));
                        }, 300);
                    },
                    error: function (data) {
                        Tc.Utils.checkoutRebuildHandleErrorOnResponse(data);
                    },
                    complete: function () {
                        // Enable button again
                        $clickedButton.prop('disabled', false);
                        // Hide loading animation
                        $thisMod.removeClass('is-loading');

                        // Due to animation, we need to wait until element is removed from DOM
                        setTimeout(function() {
                            window.CheckoutReviewPayPage.updatePayment();
                            window.CheckoutReviewPayPage.purhaseButtonStateCheck();
                        }, 310);
                    }
                });
            });
        };

        this.bindOrderReference = function ($ctx) {
            var $orderReference = $('.js-order-reference', $ctx);
            var typeTimer;

            function orderReferenceRequest() {
                $.ajax({
                    url: '/checkout/review-and-pay/projectNumber',
                    type: 'PUT',
                    data: $orderReference.val()
                });
            }

            $orderReference.on('input', function () {
                clearTimeout(typeTimer);
                typeTimer = setTimeout(orderReferenceRequest, 2000);
            });
        };

        this.bindFutureInvoiceRequest = function ($ctx) {
            var $futureInvoicing = $('.js-future-invoicing', $ctx);

            if ($futureInvoicing.length) {
                $('.js-future-invoicing-request-link', $futureInvoicing).on('click', function () {
                    var $invoiceRequest = $('.js-future-invoicing-request', $futureInvoicing);

                    $.ajax({
                        url: '/checkout/review-and-pay/request/invoice',
                        type: 'POST',
                        success: function () {
                            $invoiceRequest.hide(300);
                            $('.js-future-invoicing-requested', $futureInvoicing).show(300);

                            setTimeout(function () {
                                $invoiceRequest.remove();
                            }, 400);
                        },
                        error: function (data) {
                            Tc.Utils.checkoutRebuildHandleErrorOnResponse(data);
                        }
                    });
                });
            }
        };
    };

})(Tc.$);

Tc.Utils.resetPurchaseIframe = function () {
    var $paymentFormWrapper = $('.js-cc-form-payment-wrapper');
    var $paymentForm = $('.js-cc-form-payment', $paymentFormWrapper);
    $paymentForm.attr('src', '');
    $paymentFormWrapper.removeClass('is-loaded');
    $('.js-cr-product-list-wrapper').removeClass('is-collapsed');
    $('.js-cr-order-summary-cta').show(300);
    window.CheckoutReviewPayPage.purchaseClicked = false;
    window.CheckoutReviewPayPage.resetIframe = true;
    Tc.Utils.triggerSticky($('.js-cr'));
    $('.mod.mod-summary-terms.skin-summary-terms-inter').show();
};
