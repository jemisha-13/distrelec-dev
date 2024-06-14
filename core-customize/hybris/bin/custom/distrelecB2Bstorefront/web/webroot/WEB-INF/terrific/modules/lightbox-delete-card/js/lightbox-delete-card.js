(function($) {

	Tc.Module.LightboxDeleteCard = Tc.Module.extend({

		on: function(callback) {

            $('.list__item__option__remove .btn-remove').click(function(e) {
                e.preventDefault();

                var lightboxTitle = $(this).data('lightbox-title');
                var lightboxTitleSelector = $('#modalDeleteCard .title');
                var lightboxText = $(this).data('lightbox-message');
				var lightboxTextSelector = $('#modalDeleteCard .bd > p');
                removeUrl = $(this).data('action-url');
                paymentId = $(this).data('payment-id');

                $(lightboxTitleSelector).html(lightboxTitle);
                $(lightboxTextSelector).html(lightboxText);

                $('.mod-lightbox-delete-card .modal').removeClass('hidden');
                $('.mod-lightbox-delete-card .modal-backdrop').removeClass('hidden');

            });

            $('.mod-lightbox-delete-card .btn-cancel').click(function(e) {
                e.preventDefault();
                $('.mod-lightbox-delete-card .modal').addClass('hidden');
                $('.mod-lightbox-delete-card .modal-backdrop').addClass('hidden');
            });

            $('.mod-lightbox-delete-card .btn-confirm').click(function(e) {
                e.preventDefault();

                $.ajax({
                    url: removeUrl + '/' + paymentId,
                    type: 'POST',
                    success: function() {
                        $('.mod-lightbox-delete-card .modal').addClass('hidden');
                        $('.mod-lightbox-delete-card .modal-backdrop').addClass('hidden');
                        window.location.reload();
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        console.log(errorThrown);
                    }
                });

            });

            callback();
		}

	});

})(Tc.$);
