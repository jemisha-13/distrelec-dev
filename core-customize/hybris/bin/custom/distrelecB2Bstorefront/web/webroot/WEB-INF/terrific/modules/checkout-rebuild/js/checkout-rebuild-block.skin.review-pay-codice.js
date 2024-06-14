(function($) {

    /**
     * This module implements the Codice Destinario block on checkout page
     *
     * @namespace Tc.Module
     * @class CheckoutRebuildBlock
     * @skin ReviewPayCodice
     */
    Tc.Module.CheckoutRebuildBlock.ReviewPayCodice = function (parent) {
        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {
            // If we want to call "CheckoutRebuildBlock" on load
            // parent.on(callback);

            var self = this,
                $ctx = self.$ctx;

            // Bind events for triggering inline validation
            Tc.Utils.bindInlineValidation($ctx);
            this.bindCUPCIG($ctx);
            // For updating window.CheckoutReviewPayPage.codiceItalyValid on page load
            Tc.Utils.vatRequest($ctx, false);

            callback();
        };

        this.bindCUPCIG = function ($ctx) {
            var $codicePosta = $('.js-codice-posta', $ctx);

            if ($codicePosta.length) {
                var $cupCigCheckbox = $('.js-codice-cup-cig-checkbox', $codicePosta);
                var $codicePostaFields = $('.js-codice-posta-field', $codicePosta);
                var typeTimer;

                var handleVatRequest = function () {
                    Tc.Utils.vatRequest($ctx, false);
                };

                $cupCigCheckbox.on('change', function () {
                    var isChecked = $cupCigCheckbox.is(':checked');

                    $cupCigCheckbox.prop('disabled', true);
                    $('.js-codice-cup-cig-content', $codicePosta).toggle(isChecked);

                    if (!isChecked) {
                        Tc.Utils.vatRequest($ctx, true);
                    }

                    setTimeout(function () {
                        $cupCigCheckbox.prop('disabled', false);
                    }, 500);
                });

                // As user types, remove "success" and disable save button
                $codicePostaFields.on('input', function () {
                    var $currentField = $(this);
                    $currentField.removeClass('success error');
                    $currentField.closest('.js-iv-item').removeClass('has-success has-error');

                    clearTimeout(typeTimer);
                    typeTimer = setTimeout(handleVatRequest, 500);
                });

                $('.js-codice-posta-button', $codicePosta).on('click', function () {
                    $('.js-codice-block, .js-posta-block', $ctx).toggle();
                });
            }
        };
    };

})(Tc.$);

Tc.Utils.vatRequest = function ($ctx, isReset) {
    var $codicePostaForm = $('.js-codice-posta-form', $ctx);
    var $vat4 = $('input[name="vat4"]', $codicePostaForm);
    var $legalEmail = $('input[name="legalEmail"]', $codicePostaForm);
    var $codiceCUP = $('input[name="codiceCUP"]', $codicePostaForm);
    var $codiceCIG = $('input[name="codiceCIG"]', $codicePostaForm);
    var enablePurchase = false;

    var dataForBE = {
        vat4: '',
        legalEmail: '',
        codiceCUP: '',
        codiceCIG: ''
    };

    if ($vat4.length) {
        if (Tc.Utils.handleInlineValidationErrorRegex($vat4, true)) {
            dataForBE.vat4 = $vat4.val();
            enablePurchase = true;
        }
    }

    if ($legalEmail.length) {
        if (Tc.Utils.handleInlineValidationErrorLegalEmail($legalEmail, true)) {
            dataForBE.legalEmail = $legalEmail.val();
            enablePurchase = true;
        }
    }

    if ($vat4.length > 0 && $legalEmail.length > 0) {
        if (!Tc.Utils.handleInlineValidationErrorRegex($vat4, true) && !Tc.Utils.handleInlineValidationErrorLegalEmail($legalEmail, true)) {
            enablePurchase = false;
        }
    }

    if (isReset) {
        $codiceCIG.val('').trigger('change');
        $codiceCUP.val('').trigger('change');
    } else {
        if (Tc.Utils.handleInlineValidationErrorRegex($codiceCUP, true)) {
            dataForBE.codiceCUP = $codiceCUP.val();
        }

        if (Tc.Utils.handleInlineValidationErrorRegex($codiceCIG, true)) {
            dataForBE.codiceCIG = $codiceCIG.val();
        }
    }

    window.CheckoutReviewPayPage.codiceItalyValid = enablePurchase;

    if (enablePurchase) {
        $.ajax({
            url: '/checkout/review-and-pay/vat',
            type: 'POST',
            data: dataForBE
        });
    }

    window.CheckoutReviewPayPage.updatePayment();
    window.CheckoutReviewPayPage.purhaseButtonStateCheck();
};
