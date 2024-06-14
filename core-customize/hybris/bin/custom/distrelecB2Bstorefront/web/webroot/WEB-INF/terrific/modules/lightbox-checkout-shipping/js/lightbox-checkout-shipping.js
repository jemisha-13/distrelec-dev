(function ($) {

	Tc.Module.LightboxCheckoutShipping = Tc.Module.extend({

		init: function ($ctx, sandbox, id) {

			this._super($ctx, sandbox, id);
			this.sandbox.subscribe('lightboxCheckoutShipping', this);

		},

		on: function (callback) {

			if ($('.mod-lightbox-checkout-shipping .box-address__form .box-address__form__field > span').hasClass('form_field_error') &&
				!$('.mod-lightbox-checkout-shipping-edit .box-address__form .box-address__form__field > span').hasClass('form_field_error')) {
				$('#different').prop('checked', true);
                $('.mod-lightbox-checkout-shipping .modal').removeClass('hidden');
                $('.mod-lightbox-checkout-shipping .modal-backdrop').removeClass('hidden');
			}

			$('.shipping-add-new').click(function(e) {
				e.preventDefault();
				$('.mod-lightbox-checkout-shipping .modal').removeClass('hidden');
				$('.mod-lightbox-checkout-shipping .modal-backdrop').removeClass('hidden');
			});

			$('.box-address__form__submit .box-address__form__submit__cancel').click(function(e) {
				e.preventDefault();
                $('.mod-lightbox-checkout-shipping .modal').addClass('hidden');
                $('.mod-lightbox-checkout-shipping .modal-backdrop').addClass('hidden');
			});

            $('.mod-lightbox-checkout-shipping .modal-backdrop').click(function() {
                $(this).addClass('hidden');
                $(this).next('.modal').addClass('hidden');
            });

			callback();
		}

	});

})(Tc.$);
