(function($) {

	Tc.Module.AccountList.QuotationHistory = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */

		this.on = function (callback) {
            // calling parent method
            parent.on(callback);

			var self = this;
			var quotesModal =  $('#modalQuotation');
			var limitMessage = $('.limit-quote-message');
			var confirmationMessage = $('.confirmation-quote-message');
			var errorMessage = $('.error-quote-message');
			var loadingState = $('.skin-loading-state-loading-state');

			// subscribe to connector channel/s
			this.sandbox.subscribe('myaccount', this);
			
			// bind click event to Add to cart buttons
			this.$$('.btn-add-to-cart').click(function(ev) {
				ev.preventDefault();
				var $a = $(ev.target).closest('a'),
					quotationId = $a.data('quotationId'),
					errorText = $('.data-list').data('error-text');
				if (!isNaN(quotationId) && !$a.attr('disabled')) {
					loadingState.removeClass('hidden');
					$.ajax({
						url: '/cart/addquotation',
						type: 'GET',
						data: {quotationId:quotationId},
						dataType: 'json',
						success: function (data, textStatus, jqXHR) {
							if (typeof data.status!=='undefined' && data.status==='ok') {
								window.location.href = '/cart';
							} else {
								if (typeof data.errorMsg!=='undefined' && data.errorMsg.length) {
									errorText = data.errorMsg;
								}
								loadingState.addClass('hidden');
								alert(errorText);
							}
						},
						error: function (jqXHR, textStatus, errorThrown) {
							loadingState.addClass('hidden');
							alert(errorText);
						}
					});
				}
			});

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

					if(!$(this).hasClass('active')) {
						var loadingState = $('.skin-loading-state-loading-state');
						loadingState.removeClass('hidden');

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

		this.onPaginationChange = function(data) {
			var $theForm = $('form#quotationHistoryForm');
		    if ($theForm.length === 1) {
		    	$theForm.find('input#page').val(1);
		    	$theForm.find('input#pageSize').val(data.pageSize);
		    	$theForm.submit();
		    }
		};

	};

})(Tc.$);
