(function ($) {

    /**
     * Checkout Skin implementation for the module CheckoutPaymentOptionsList.
     *
     * @author Remo Brunschwiler
     * @namespace Tc.Module.CheckoutPaymentOptionsList
     * @class Basic
     * @extends Tc.Module
     * @constructor
     */
    Tc.Module.CheckoutPaymentOptionsList.Myaccount = function (parent) {

        this.on = function (callback) {

            var $option = this.$$('input[type=radio]'),
                $form = this.$$('form'),
                $ctx = this.$ctx;

            // Click on a payment method to select
            $('.mod-checkout-payment-options-list .list__item, .request-pay-invoice__btn').click(function(e) {
                e.preventDefault();

                var $target = $(e.target);
                var $closestForm = $target.closest("form");

                if(!$target.hasClass('disabled') || !$target.hasClass('list__item__option') || !$target.hasClass('request-pay-invoice')) {

                    var actUrl = $(this).find('.paymentInfo').data('form-action');
                    var val = $(this).find('.paymentInfo').data('form-method');
                    var actUrlObj = (!$target.hasClass('request-pay-invoice__btn') ? actUrl + '?paymentOption=' + val :
                        $(this).parent('.invoice-extra').siblings('.request-pay-invoice').find('.paymentInfo').data('form-action'));
                    var requestInvoiceLabel = $('.list__item.request-pay-invoice');
                    var invoiceDescription = $('.invoice-extra');
                    if(!$target.closest('.list__item').hasClass('request-pay-invoice')) {
                        $closestForm.attr('action', actUrlObj).submit();
                        if(!$target.hasClass('request-pay-invoice__btn')) {
                            invoiceDescription.addClass('hidden');
                        }

                    } else {
                       requestInvoiceLabel.addClass('list__item--active');
                       requestInvoiceLabel.siblings().removeClass('list__item--active');
                       invoiceDescription.removeClass('hidden');
                    }

                }

            });

            // Click on a saved card
            $('.mod-checkout-payment-options-list .list__saved .list__item__option').click(function(e) {
                e.preventDefault();

                var closestForm = $(e.target).closest("form");

                if ($(e.target).hasClass('btn-remove')) {
                    return false;
                }

                if(!$(this).hasClass('disabled') && !$(this).hasClass('list__item__option--expired')) {

                    $(e.target).prop('checked', 'checked');

                    var actUrl = $(this).find('.paymentInfo').data('form-action');
                    var val = $(this).find('.paymentInfo').val();
                    var actUrlObj = actUrl + '?paymentOption=creditCard&paymentInfo=' + val;

                    Tc.Utils.disableAndSubmitForm(closestForm, actUrlObj);

                }

            });

            $('.mod-checkout-payment-options-list .list__saved .list__item__option .btn-default').click(function(e) {
                e.preventDefault();

                var actionUrl = $(this).data('action-url');
                var paymentId = $(this).data('payment-id');

                $.ajax({
                    url: actionUrl + '?paymentOption=creditCard&paymentInfo=' + paymentId,
                    type: 'post',

                    success: function () {
                        window.location.reload();
                    }

                });

            });

            parent.on(callback);
        };

        this.onDialogConfirm = function (data) {
            var self = this;

            if (data.actionIdentifier === self.actionIdentifier) {
                var $ctx = self.$ctx;
                var paymentId = data.attribute;
                var $option = $('#ccPaymentInfo' + paymentId);
                var $row = $option.parents('.list__item__option');
                var $rowNewCreditCard = self.$$('.list__item__option--new');

                if ($option.is(':checked')) {
                    var $newOption = $('#new_credit_card', $ctx);
                    $newOption.prop('checked', 'checked');
                }

                $.ajax({
                    url: location.protocol + '//' + location.host + self.action + '/' + paymentId,
                    type: 'post',
                    success: function (data) {
                        $row.fadeOut(function () {
                            $row.remove();
                        });

                        $rowNewCreditCard.fadeOut(function () {
                            $rowNewCreditCard.remove();
                        });

                    }

                });

            }

        };

    };

})(Tc.$);
