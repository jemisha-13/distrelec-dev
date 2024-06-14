(function ($) {

	Tc.Module.LightboxCheckoutShippingEdit = Tc.Module.extend({

		init: function ($ctx, sandbox, id) {

			this._super($ctx, sandbox, id);
			this.sandbox.subscribe('lightboxCheckoutShippingEdit', this);

		},

		on: function (callback) {

			if ($('.mod-lightbox-checkout-shipping-edit .box-address__form .box-address__form__field > span').hasClass('form_field_error')) {
                var error = $('.mod-lightbox-checkout-shipping-edit .box-address__form .box-address__form__field > span.form_field_error')[0];
                var modal = error.closest('.modal');

				$('#different').prop('checked', true);
                $(modal).removeClass('hidden');
                $(modal).prev('.modal-backdrop').removeClass('hidden');
			}

			$('.shipping-edit').click(function(e) {
				e.preventDefault();

				var lightBox = $(this).next('.mod-lightbox-checkout-shipping-edit');

				lightBox.find('.modal').removeClass('hidden');
				lightBox.find('.modal-backdrop').removeClass('hidden');
			});

			$('.mod-lightbox-checkout-shipping-edit .box-address__form__submit .box-address__form__submit__cancel').click(function(e) {
				e.preventDefault();
                $('.mod-lightbox-checkout-shipping-edit .modal').addClass('hidden');
                $('.mod-lightbox-checkout-shipping-edit .modal-backdrop').addClass('hidden');
			});

            $('.mod-lightbox-checkout-shipping-edit .modal-backdrop').click(function() {
            	$(this).addClass('hidden');
                $(this).next('.modal').addClass('hidden');
			});

			callback();
		}

	});

})(Tc.$);
