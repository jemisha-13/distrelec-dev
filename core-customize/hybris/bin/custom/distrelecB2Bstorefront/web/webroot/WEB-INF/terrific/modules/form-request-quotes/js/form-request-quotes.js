(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Register
     * @extends Tc.Module
     */
    Tc.Module.FormRequestQuotes = Tc.Module.extend({

        /**
         * Initialize.
         *
         * @method init
         * @return {void}
         * @constructor
         * @param {jQuery} $ctx the jquery context
         * @param {Sandbox} sandbox the sandbox to get the resources from
         * @param {Number} id the unique module id
         */
        init: function($ctx, sandbox, id) {
            // call base constructor
            this._super($ctx, sandbox, id);
        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */

        on: function(callback) {
            var defaultRow =  $('.quote-form__requirements__row').eq(0);
            var hasErrors = true;
            var quotesModal =  $('#modalQuotation');
            var maxRows = $('#maxRows').val();
            var quoteBtn = $('.requestQuotes');
            var limitMessage = $('.limit-quote-message');
            var confirmationMessage = $('.confirmation-quote-message');
            var errorMessage = $('.error-quote-message');

            //jstl doesn't support maxlength on formtags
            $('#reference').attr('maxlength','125');
            $('#customerNo,#companyName,.valueId').attr('disabled','disabled');


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

            if(quoteBtn.hasClass('limitPopUp')) {
                limitMessage.removeClass('hidden');
                confirmationMessage.addClass('hidden');
                errorMessage.addClass('hidden');
                openModal();
            }

            $('.add-row > a').click(function(e){
                e.preventDefault();

                if($('.quote-form__requirements__row').length < maxRows) {
                    var getLatestRowId = $('.quote-form__requirements__row').last().find('.valueId').val();
                    var clonedRow = defaultRow.clone();
                    getLatestRowId ++;

                    clonedRow.find('.grouped__rowxs').attr('value', getLatestRowId);
                    clonedRow.html().replace(/0/g ,getLatestRowId);


                    var rowLength = $('.quote-form__requirements__row').length;
                    if(rowLength === 1) {
                        $('.remove-item:first').addClass('disabled');
                    } else {
                        $('.remove-item:not(:first)').removeClass('disabled');
                    }

                    $('.quote-form__requirements-list').append(
                        '<div class="quote-form__requirements__row">' +
                        clonedRow.html() +
                        '</div>'
                    );

                    rowLength = $('.quote-form__requirements__row').length;

                    if(rowLength > 1) {
                        $('.remove-item').removeClass('disabled');
                    }

                }

            });

            var mapRowIds = function() {

                var newCount = 0;
                $('.quote-form__requirements__row').each(function() {
                    newCount++;
                    $(this).children().find('.valueId').val(newCount);
                });

            };

            $(document).on('click', '.remove-item', function(e){
                e.preventDefault();
                var rowLength = $('.quote-form__requirements__row').length;
                if(rowLength > 1) {
                    $(this).closest('.quote-form__requirements__row').remove();
                    rowLength = $('.quote-form__requirements__row').length;
                }

                if(rowLength === 1) {
                    $('.remove-item:first').addClass('disabled');
                } else {
                    $('.remove-item').removeClass('disabled');
                }

                //re order id's when deleting
                mapRowIds();

            });

            $('label[for="quote-no"]').click(function(){
                var input = $(this).closest('input');
                input.prop('checked', true);
                $(this).parent().siblings('.grouper__grouped').find('input').prop('checked', false);
            });

            $("label[for='isTenderProcess']").click(function(){
                var input = $(this).closest('input');
                input.prop('checked', true);
                $(this).parent().siblings('.grouper__grouped').find('input').prop('checked', false);
            });

            quoteBtn.click(function(e){
                e.preventDefault();

                $('.grouped.mandatory > input.mandatory').each(function () {
                    if($(this).val() === '') {
                        $(this).addClass('error');
                        $(this).parents('.quote-form__requirements-list').siblings('.error-message')
                            .removeClass('hidden');
                        hasErrors = true;
                    } else {
                        $(this).removeClass('error');
                        hasErrors = false;
                    }

                    if($(this).hasClass('qty') && $(this).val() === '0') {
                        $(this).addClass('error');
                        hasErrors = true;
                    }

                });

                var customerErrorFlag = false;
                $('.quote-form__customer input').each(function() {

                    if($(this).val() === '') {
                        $(this).addClass('error');
                        $('.error-message-customer').removeClass('hidden');
                        customerErrorFlag = true;
                        hasErrors = true;
                    } else {
                        $(this).removeClass('error');
                        hasErrors = false;
                    }

                });

                if(customerErrorFlag === false) {
                    $('.error-message-customer').addClass('hidden');
                }


                $('.quote-form__requirements__row > .grouped.empty').each(function() {
                        $(this).children().removeClass('error');
                        $('.error-message').addClass('hidden');
                });

                $('.quote-form__requirements__row > .grouped.mandatory').each(function() {

                    var mpnField = $(this).find('.mpn-field');
                    var articleField = $(this).find('.article-field');
                    var qty = $(this).find('qty');
                    $('.error-message').removeClass('hidden');

                    if((mpnField.val() !== '' || articleField.val() !== '') && qty.val() !== '0') {
                        mpnField.removeClass('error');
                        articleField.removeClass('error');
                        hasErrors = false;
                    } else {
                        mpnField.addClass('error');
                        articleField.addClass('error');
                        $(this).parents('.quote-form__requirements-list').siblings('.error-message')
                            .removeClass('hidden');
                        hasErrors = true;
                    }

                });

                if(hasErrors === false && !$('input').hasClass('error') && $('.grouped.mandatory').length > 0) {
                    $('.error-message').addClass('hidden');

                    var disabledCustomerInputs = $('#customerNo,#companyName,.valueId');

                    disabledCustomerInputs.removeAttr('disabled');  // need to do this so they serialize

                    var customerSerialize = $('.quote-form__customer').find('input, select').serialize();
                    var requirementsSerialize = $('.grouped.mandatory').find('input').serialize();
                    var extrasSerialize = $(".quote-form__comments").find("textarea, input").serialize();

                    disabledCustomerInputs.attr('disabled','disabled');

                    if(!quoteBtn.hasClass('limitPopUp')) {
                        $.ajax({
                            url: '/request-quotation',
                            async : false,
                            type: 'post',
                            data: customerSerialize + '&' + requirementsSerialize + '&' + extrasSerialize,
                            success : function() {
                                $('#quotationdata textarea.validate-empty').val('');
                                $('#quotationdata input.validate-empty:not(.disabled,[type="hidden"],[type="radio"], .quote-form__customer input)').val('');
                                $('.grouped').removeClass('mandatory');
                                $('.grouped').addClass('empty');
                                confirmationMessage.removeClass('hidden');
                                errorMessage.addClass('hidden');
                                limitMessage.addClass('hidden');
                                openModal();
                            },
                            error : function(err) {
                                console.log(err);
                            }
                        });
                    } else {
                        limitMessage.removeClass('hidden');
                        confirmationMessage.addClass('hidden');
                        errorMessage.addClass('hidden');
                        openModal();
                    }

                }
            });

            $('.quote-form__requirements__row > .grouped input').keyup(function() {

                quoteBtn.removeAttr('disabled');
                var self = $(this);

                $(this).siblings(".validate-empty:not(.valueId)").each(function(){

                    if(self.val() !== '' || $(this).val() !== '') {
                        self.parent().removeClass('empty');
                        self.parent().addClass('mandatory');
                    }


                    // if mandatory fields are empty remove mandatory class from parent and replace with empty so we don't submit row in form
                    // self is for the keyup function <= (this)
                    // this is for the siblings
                    var mandatoryCheck = ($(this).hasClass('mandatory') || $(self).hasClass('mandatory'));
                    var alternateCheck = ($(this).hasClass('alternate-mandatory') || $(self).hasClass('alternate-mandatory'));

                    if((mandatoryCheck && alternateCheck) && ($(this).val() === '' && (self).val() === '')) {
                        self.parent().removeClass('mandatory');
                        self.parent().addClass('empty');
                    }

                });

            });

            $('.quoteformclose').click(function(){
                $('.modal-backdrop').remove();
                quotesModal.hide();
            });

            $('body').on('click','.modal-backdrop',function(e){
                removeModal(e);
            });

            callback();
        },


        /**
         * Hook function to trigger your events.
         *
         * @method after
         * @return void
         */
        after: function() {
        }

    });

})(Tc.$);
