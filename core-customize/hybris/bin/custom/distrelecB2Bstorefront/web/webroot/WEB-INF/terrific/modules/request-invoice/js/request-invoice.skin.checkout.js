(function($) {

    Tc.Module.RequestInvoice.Checkout = function (parent) {

        this.on = function (callback) {

            axios.defaults.headers.common = {
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            };

                new Vue({
                    el: '#requestInvoiceRoot',

                    methods: {

                        clickInvoiceBtn: function() {

                            var reqInvoiceUrl = '/my-account/request-invoice-payment-mode';

                            axios.post(reqInvoiceUrl)
                                .then(function (response) {
                                    window.location.reload();
                                });
                        }

                    }

                });

            parent.on(callback);

        };

    };

})(Tc.$);
