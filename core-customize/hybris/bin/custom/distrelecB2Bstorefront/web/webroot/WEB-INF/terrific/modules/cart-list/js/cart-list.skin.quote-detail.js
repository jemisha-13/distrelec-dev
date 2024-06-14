(function ($) {

	/**
	 * QuoteDetail Skin implementation for the module CartList.
	 *
	 * @author Céline Müller/Erik Fries
	 * @namespace Tc.Module.CartList
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.CartList.QuoteDetail = function (parent) {
		// subscribe to connector channel/s
		this.sandbox.subscribe('cartlistBulkAction', this);
		this.sandbox.subscribe('lightboxShoppinglist', this);

		// CartList add Quotation to Cart bulk action
		// -> triggered by order-overview-box

		this.onAddQuoteToCart = function(data){
			var allProducts = this.$$('.cart-list .mod-cart-list-item')
				,cartAPIproductsJson = '[{'
				,cartAPIquantity = 0
				,quoteId = this.$$('.cart-list').data('quote-id')
			;
				
			// Trigger Cart API to add to cart
			$(document).trigger('cart', {
				actionIdentifier: 'quoteDetailAddToCart',
				type: 'addQuote',
				quoteId: quoteId,
				productsJson: cartAPIproductsJson,
				qty: cartAPIquantity
			});
		};

        var quotesModal =  $('#modalQuotation');
		var limitMessage = $('.limit-quote-message');
		var confirmationMessage = $('.confirmation-quote-message');
		var errorMessage = $('.error-quote-message');

        var openModal = function() {
            $('body').append('<div class="modal-backdrop"></div>');
            quotesModal.show();
        };

        var removeModal = function(e) {
            e.preventDefault();
            $('.modal-backdrop').addClass('hidden');
			quotesModal.hide();
        };

        $('.quoteformclose').click(function(){
            $('.modal-backdrop').remove();
            quotesModal.hide();
        });

        $('.btn-close').click(function(e){
            removeModal(e);
        });

        $('body').on('click','.modal-backdrop',function(e){
            removeModal(e);
        });

		$('.resubmit').click(function(e){
			e.preventDefault();
			var self = $(this);

			if(!$(this).hasClass('limitPopUp')) {
				var resubmittedText = $(this).data('resubmitted-text');
				var quoteID = $(this).data('quote-id');
				var loadingState = $('.skin-loading-state-loading-state');
				loadingState.removeClass('hidden');

				if(!$(this).hasClass('active')) {

					$.ajax({
						url: '/request-quotation/resubmit-quotation/' + quoteID,
						type: 'post',

						success: function (response) {
							loadingState.addClass('hidden');
							
							switch(response.status) {

								case 'LIMIT_EXCEEDED':
									limitMessage.removeClass('hidden');
									confirmationMessage.addClass('hidden');
									errorMessage.addClass('hidden');
									quotesModal.removeClass('error');
									openModal();
									break;
								case 'SUCCESSFUL':
									confirmationMessage.removeClass('hidden');
									errorMessage.addClass('hidden');
									limitMessage.addClass('hidden');
									openModal();
									self.html(resubmittedText);
									self.addClass('active');
									break;
								case 'FAILED':
									errorMessage.removeClass('hidden');
									confirmationMessage.addClass('hidden');
									limitMessage.addClass('hidden');
									quotesModal.addClass('error');
									openModal();
									break;
								default:
									return response.status;
							}

						}

					});

				}
			} else {
				limitMessage.removeClass('hidden');
				confirmationMessage.addClass('hidden');
				errorMessage.addClass('hidden');
                openModal();
			}

		});

	};

})(Tc.$);
