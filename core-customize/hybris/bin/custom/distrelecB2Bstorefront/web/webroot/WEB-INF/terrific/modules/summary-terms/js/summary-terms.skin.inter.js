(function($) {

    Tc.Module.SummaryTerms.Inter = function () {

        this.on = function (callback) {

            var checkoutBtn = $('.btn-checkout'),
                payPalBtn = $('.paypalVat'),
                paymentBtn = $('.payment-btn'),
                labelCheckbox = $('label[for="summaryCheckbox"]');

            checkoutBtn.addClass('disabled');
            payPalBtn.addClass('disabled');
            labelCheckbox.removeClass('active');

            if ( paymentBtn.length > 0 ) {

                if ($('#summaryCheckbox').prop('checked') === true) {
                    paymentBtn.removeClass('disabled');
                } else {
                    paymentBtn.addClass('disabled');
                }

            }

            labelCheckbox.click(function(){

                $(this).toggleClass('active');

                if ( paymentBtn.length > 0 ) {

                    if ($('#summaryCheckbox').prop('checked') === true) {
                        paymentBtn.addClass('disabled');
                    } else {
                        paymentBtn.removeClass('disabled');
                    }

                }

                if ($('#summaryCheckbox').prop('checked') === true) {
                    checkoutBtn.addClass('disabled');
                    payPalBtn.addClass('disabled');
                } else {
                    checkoutBtn.removeClass('disabled');
                    payPalBtn.removeClass('disabled');
                }

            });

            callback();

        };

    };

})(Tc.$);