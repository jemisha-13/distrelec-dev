(function($) {

    Tc.Module.FormRequestQuotes.Cart = function () {

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {

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
                $('.import-tool-modal').removeClass('current');
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

            $('.requestQuotes').click(function(e){
                e.preventDefault();
                var loadingState = $('.skin-loading-state-loading-state');
                loadingState.removeClass('hidden');

                $.ajax({
                    url: '/cart/requestQuote',
                    type: 'post',

                    success : function(response) {
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
                                quotesModal.removeClass('error');
                                openModal();
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

                    },
                    error : function(err) {
                        return err;
                    }
                });

            });

            callback();
        };


    };

})(Tc.$);
