(function($) {

    /**
     * This module implements the delivery options on checkout page
     *
     * @namespace Tc.Module
     * @class CheckoutRebuildBlock
     * @skin OrderSummary
     */
    Tc.Module.CheckoutRebuildBlock.OrderSummary = function (parent) {
        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {
            // If we want to call "CheckoutRebuildBlock" on load
            // parent.on(callback);

            var self = this,
                $ctx = self.$ctx;

            self.handleProductList($ctx);
            self.bindPurchaseButton($ctx);
            self.bindSummaryCheckbox($ctx);

            callback();
        };

        this.handleProductList = function ($ctx) {
            var $productListWrapper = $('.js-cr-product-list-wrapper', $ctx);

            if ($productListWrapper.length) {
                var $productList = $('.js-cr-product-list', $productListWrapper);
                var $productListItems = $('.js-cr-product-list-item', $productList);
                var heightOfList = $productList.height();
                var heightOfItems = 0;

                $productListItems.each(function () {
                    heightOfItems += $(this).outerHeight(true);
                });

                $productListWrapper.toggleClass('has-overflow', heightOfItems > heightOfList);
            }
        };

        this.totalsRecalculation = function (data) {
            var thisMod = this;
            var $thisMod = thisMod.$ctx;
            var $deliveryOptionsMod = $('.js-rebuild-block-delivery-options');
            var $totalsTemplate = $('.js-cr-totals-template', $thisMod);
            var $loader = $('.js-cr-totals-loader', $thisMod);
            var tmplOrderSummaryTotals = doT.template($('#tmpl-order-summary-totals', $thisMod).html());

            $loader.addClass('is-loading');
            $deliveryOptionsMod.addClass('is-loading');

            // Hide all remove banners
            $('.js-da-list-item-remove').hide(300);

            $.ajax({
                url: '/checkout/delivery/update',
                type: 'POST',
                data: data,
                success: function(response) {
                    $totalsTemplate.html(tmplOrderSummaryTotals(response));
                    // Update availability messages
                    Tc.Utils.updateOrderSummaryAvailabilityMessages(response.entries);
                    setTimeout(function () {
                        window.CheckoutDeliveryPage.canContinueCheck();
                    }, 200);
                },
                error: function (error) {
                    Tc.Utils.checkoutRebuildHandleErrorOnResponse(error);
                    setTimeout(function () {
                        window.CheckoutDeliveryPage.canContinueCheck();
                    }, 200);
                },
                complete: function () {
                    setTimeout(function () {
                        $loader.removeClass('is-loading');
                        $deliveryOptionsMod.removeClass('is-loading');
                        $('.js-rebuild-block-delivery-options').trigger('enableOptions');
                    }, 50);
                }
            });
        };

        this.bindPurchaseButton = function ($ctx) {
            var $purchaseButton = $('.js-purchase-button', $ctx);

            $purchaseButton.on('click', function () {
                var $clickedButton = $(this);
                var paymentOptionTitle = window.CheckoutReviewPayPage.paymentOptionTitle;
                var $summaryCheckbox = $('.js-summary-checkbox', $ctx);
                var $FRSummaryCheckbox = $('.mod.mod-summary-terms.skin-summary-terms-inter');
                var canBePurchased = true;
                window.CheckoutReviewPayPage.purchaseClicked = true;

                if ($summaryCheckbox.length) {
                    canBePurchased = $summaryCheckbox.is(':checked');
                    $FRSummaryCheckbox.hide();
                }

                if (!window.CheckoutReviewPayPage.codiceItalyValid) {
                    canBePurchased = false;
                }

                if (canBePurchased) {
                    $clickedButton.prop('disabled', true);

                    // Credit cards and PayPal
                    if (paymentOptionTitle === 'CreditCard' || paymentOptionTitle === 'PayPal') {
                        var $paymentFormWrapper = $('.js-cc-form-payment-wrapper', $ctx);
                        var $paymentForm = $('.js-cc-form-payment', $paymentFormWrapper);
                        var paymentFormSrcVar = $paymentForm.data("src1");

                        $paymentForm.unbind('load').on('load', function() {
                            if (!window.CheckoutReviewPayPage.resetIframe) {
                                if (paymentOptionTitle === 'CreditCard') {
                                    $paymentFormWrapper.addClass('is-loaded');

                                    setTimeout(function () {
                                        Tc.Utils.triggerSticky($('.js-cr'));
                                    }, 300);
                                }

                                $('.js-cr-product-list-wrapper', $ctx).addClass('is-collapsed');
                                $('.js-cr-order-summary-cta', $ctx).hide(300);
                            }
                        });

                        window.CheckoutReviewPayPage.resetIframe = false;

                        if (paymentOptionTitle === 'CreditCard') {
                            $paymentForm.attr('src', paymentFormSrcVar);
                        } else {
                            // PayPal iframe needs to be inject through JS since then browser won't cache its url
                            // When you land on PayPal page, if you go back, browser will redirect you back to PayPal (if not using this approach)
                            $('.js-pp-iframe').html('<iframe id="pp-iframe" src="' + paymentFormSrcVar + '"></iframe>');
                        }
                    } else {
                        // Invoice
                        $('.js-hidden-invoice-form', $ctx).submit();
                    }
                }
            });
        };

        this.bindSummaryCheckbox = function ($ctx) {
            var $summaryCheckbox = $('.js-summary-checkbox', $ctx);

            $summaryCheckbox.on('change', function () {
                var $summaryCheckboxChecked = $(this).is(':checked');

                window.CheckoutReviewPayPage.summaryCheckedFrance = $summaryCheckboxChecked;
                window.CheckoutReviewPayPage.updatePayment();
                window.CheckoutReviewPayPage.purhaseButtonStateCheck();
            });
        };
    };

})(Tc.$);

Tc.Utils.ajaxUpdateOrderSummaryAvailabilityMessages = function () {
    $.ajax({
        url: '/checkout/delivery/order-summary',
        type: 'GET',
        success: function(response) {
            Tc.Utils.updateOrderSummaryAvailabilityMessages(response.entries);
        }
    });
};

Tc.Utils.updateOrderSummaryAvailabilityMessages = function (entries) {
    var $productList = $('.js-cr-product-list');
    var deliveryMsg = $productList.data('delivery-msg');
    var deliveryAndMsg = $productList.data('delivery-and-msg');
    var deliveryMsgNoStock = $productList.data('delivery-msg-no-stock');

    for(var i=0; i < entries.length; i++) {
        var entry = entries[i];
        var productCode = entry.product.codeErpRelevant;
        var $productItem = $('.js-product-item-delivery-message-' + productCode);
        var availabilities = entry.availabilities;
        var availabilitiesMessage = deliveryMsgNoStock;

        if (availabilities.length) {
            availabilitiesMessage = deliveryMsg;

            if (availabilities.length > 1) {
                for (var j=0; j < availabilities.length; j++) {
                    // If last item
                    if (j+1 === availabilities.length) {
                        availabilitiesMessage += ' ' + deliveryAndMsg + ' <strong>' + availabilities[j].formattedEstimatedDate + '</strong>';
                    } else {
                        availabilitiesMessage += ' <strong>' + availabilities[j].formattedEstimatedDate + '</strong>';
                    }
                }
            } else {
                availabilitiesMessage += ' <strong>' + availabilities[0].formattedEstimatedDate + '</strong>';
            }

            availabilitiesMessage += '.';
        }

        $productItem.html(availabilitiesMessage);
    }
};
