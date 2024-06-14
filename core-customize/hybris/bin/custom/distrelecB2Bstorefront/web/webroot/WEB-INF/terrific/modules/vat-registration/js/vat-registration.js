/*
* input must be 6 or 7 characters in length;
* field must be able to accept alphanumeric characters;
* no spaces.
 */

Tc.Module.VatRegistration = Tc.Module.extend({


    on: function (callback) {

        var input = $('.vat-reg__input'),
            checkbox = $('.codice-label'),
            hiddenSection = $('.vat-reg__flag'),
            inputHolder = $('.vat-reg__input-holder'),
            errorBox = $('.vat-reg__error'),
            codiceInput = $('.codice'),
            labelSmall = $('.vat-reg__label__small'),
            labelToggle = $('.vat-reg__toggle'),
            postCert = $('.vat-reg__post-cert'),
            postCertLabel = $('.toggle-cert'),
            codiceDest = $('.vat-reg__codice-dest'),
            codiceDestLabel = $('.toggle-dest'),
            orderBtn = $('#command'),
            inputRef = $('.vat-reg__input__active'),      //need to set it to something first so if user clicks place order without even touching the input it will use the default
            btnEvent = '',
            jQbtn = null,
            hasVatFlag = ($('.hasVAT').length !== 0),
            hasEmailFlag = ($('.hasEmail').length !== 0),
            optionalFlag = true,
            b2cBtn = $('.vat-reg__b2c__btn'),
            modCheckout = $('.mod-checkout-payment'),
            b2cContainer = $('.vat-reg__cc'),
            hasCC = $('.hasCC'),
            paypalBtn = $('.paypalVat'),
            existingCC = ($('.existingCC').length !== 0);

            modCheckout.addClass('vat');
            if(b2cContainer.length) {
                modCheckout.addClass('b2c_show');

                // if user already has submitted we can show it already
                var hasB2CFlag = ($('.hasVAT').length !== 0 || $('.hasEmail').length !== 0);
                if(hasB2CFlag === true) {
                    $('.mod-checkout-payment').removeClass('b2c_show');
                    hasCC.addClass('hidden');
                    b2cBtn.addClass('hidden');

                    if($('.vat-reg__b2c').length) {         //if check to stop it breaking
                        b2cContainer.addClass('active');
                    }

                }

                if(existingCC === true) {

                    var b2cBtnState = b2cBtn.attr('data-class');

                    if ( b2cBtnState == 'hidden' ) {
                        modCheckout.removeClass('hidden');
                    } else {
                        modCheckout.addClass('hidden');
                    }

                    hasCC.removeClass('hidden');
                    b2cBtn.removeClass('hidden');
                }

            }

        function regValidation(elem,paypalFlag) {

          var regex = '',
              val = elem.val(),
              len = '',
              emailSplit = '',
              emailCheck = '';

          if(elem.attr('type') === 'text') {
              regex = /^[a-zA-Z0-9]*$/; ///regex for alphanumeric
              len = val === '' || val.length < 6 || val.length > 7;
          }else {
              regex = /\S+@\S+\.\S+/;   //regex for email
              emailSplit = val.toString().split("@").pop();
              emailCheck = emailSplit.indexOf('pec') === -1 && emailSplit.indexOf('legal') === -1 && emailSplit.indexOf('cert') === -1 && emailSplit.indexOf('Sicurezzapostale') === -1;
          }

            var flag = regex.test(val); //flag returns false if regex fails

            if(flag === false || len || emailCheck) {

                optionalLoopHandler();

                if($('.hasVAT').length !== 0 || $('.hasEmail').length !== 0) {
                    passValidation(true, hasVatFlag, hasEmailFlag, optionalFlag);

                    if(typeof(paypalFlag) !== 'undefined' && paypalFlag === true) { //type of used to stop it breaking in instances where second argument is not passed
                        paypalTrigger();
                    }

                }else {
                    failVaildation();
                }

            }
            else {
                optionalLoopHandler();
                passValidation(true, hasVatFlag, hasEmailFlag, optionalFlag);
                elemAddClass(errorBox);

                if(typeof(paypalFlag) !== 'undefined' && paypalFlag === true) {    //type of used to stop it breaking in instances where second argument is not passed
                    paypalTrigger();
                }

            }

        }

        input.blur(function(){
            inputRef = $(this); //we need this so we know which input it is so we can pass it to regValidation
        });


        orderBtn.submit(function(e) {
            btnEvent = e;
            jQbtn = $(this);
            regValidation(inputRef);
        });

        paypalBtn.click(function(e){
            btnEvent = e;
            jQbtn = $(this);

            var hasPaypal = ($('.paypalVat').length ? true : false);

            regValidation(inputRef,hasPaypal);
        });

        b2cBtn.click(function(e){
            btnEvent = e;
            jQbtn = $(this);
            regValidation(inputRef);
        });

        function paypalTrigger() {
            $('.pp-iframe').html('<iframe id="pp-iframe" style="display: block; overflow-x: hidden; overflow-y: auto; margin: 0; border: 0; height: 0; width: 0;" src="/checkout/payment/hiddenPaymentForm"></iframe>');
        }

        function elemAddClass() {

            for(var i = 0; i < arguments.length; i++) {
                arguments[i].addClass('hidden');
            }

        }

        function optionalLoopHandler() {
            // certain customer types will not have these elements so adding try catch so it doesnt break

            try {
                if($('#codiceCup').val().length === 0 ||$('#codiceCig').val().length === 0) {
                    optionalFlag = true;
                }
                if($('#codiceCup').val().length === 15 ||$('#codiceCig').val().length === 10) {
                    optionalFlag = 'pass';

                    if(hasEmailFlag === true || hasVatFlag === true) {
                        optionalFlag =  'active';
                    }
                }
                if($('#codiceCup').val().length !== 15 && $('#codiceCup').val().length !== 0 || $('#codiceCig').val().length !== 10 && $('#codiceCig').val().length !== 0) {
                    optionalFlag = false;
                }

                if($('#codiceCup').val().length !== 15 || $('#codiceCup').val().length !== 0) {
                    $('.cupError').removeClass('hidden');
                }

                if($('#codiceCup').val().length === 15 || $('#codiceCup').val().length === 0) {
                    $('.cupError').addClass('hidden');
                }

                if($('#codiceCig').val().length !== 15 || $('#codiceCig').val().length !== 0) {
                    $('.cigError').removeClass('hidden');
                }

                if($('#codiceCig').val().length === 10 || $('#codiceCig').val().length === 0) {
                    $('.cigError').addClass('hidden');
                }
            } catch(e) {
                return e;
            }

        }

        function elemRemoveClass() {

            for(var i = 0; i < arguments.length; i++) {
                arguments[i].removeClass('hidden');
            }

        }

        function passValidation(exp, vat, email, codiceOpt) {
            btnEvent.preventDefault();
            inputHolder.removeClass('tick');
            inputHolder.addClass('cross');
            elemRemoveClass(errorBox,labelSmall,labelToggle);

            if(exp === true) {
                inputHolder.removeClass('cross');
                inputHolder.addClass('tick');
                inputHolder.addClass('vat-reg__input-holder--disabled');
                inputHolder.children('input').attr('disabled','disabled');

                if($('.vat-reg__b2c').length) {         //if check to stop it breaking
                    b2cContainer.addClass('active');
                }

                elemAddClass(errorBox,labelSmall,labelToggle);

                var codice_destinario = ($('#vatcodice').val().length ? $('#vatcodice').val() : null);
                var legalEmail = ($('#vatemail').val().length ? $('#vatemail').val() : null);
                var codice_cup = '';
                var codice_cig = '';

                var a = ($('#codiceCup').length ? true : false);
                if(a === true) {
                    codice_cup = ($('#codiceCup').val().length ? $('#codiceCup').val() : null);
                    codice_cig = ($('#codiceCig').val().length ? $('#codiceCig').val() : null);
                }

                    if(vat === false || email === false) {
                        btnEvent.preventDefault();
                        optionalLoopHandler();
                        var tempFlag = (hasVatFlag !== false || hasEmailFlag !== false);

                        if(tempFlag !== true && codiceOpt !== false) {
                            $.ajax({
                                url: '/checkout/payment/customerVatDetails',
                                async : false,                                
                                type: 'post',
                                data: {
                                    vat4: codice_destinario,
                                    legalEmail: legalEmail
                                },
                                success : function(result) {
                                   jQbtn.unbind("submit").submit();

                                    try {
                                       modCheckout.removeClass('b2c_show');
                                       b2cBtn.addClass('hidden');
                                       hasCC.addClass('hidden');
                                       $('.vat-reg__flag__cc').addClass('hidden');
                                    } catch(e) {
                                        return e;
                                    }

                                },
                                error : function(result) {
                                    btnEvent.preventDefault();
                                    console.error(result);
                                }
                            });
                        }

                        if(codiceOpt === false) {
                            btnEvent.preventDefault();
                        }

                    }

               $('.codice').keydown(function(){
                  b2cBtn.removeAttr('disabled');
                  modCheckout.addClass('hidden');
               });

                switch(codiceOpt) {
                    case false:
                        failVaildation();
                        btnEvent.preventDefault();  //stop button action

                        if(existingCC === true) {
                            modCheckout.addClass('hidden');
                        }

                        break;
                    case true:
                        jQbtn.unbind("submit").submit();

                        if(existingCC === true) {
                            modCheckout.removeClass('hidden');
                        }

                        break;
                    case 'pass' :
                    case 'active':
                        try {
                            $.ajax({
                                url: '/checkout/payment/codiceDetails',
                                async : false,
                                type: 'post',
                                data: {
                                    codiceCIG: codice_cig,
                                    codiceCUP: codice_cup
                                },
                                success : function(result) {
                                    jQbtn.unbind("submit").submit();
                                    modCheckout.removeClass('hidden');
                                    b2cBtn.attr('disabled','disabled');
                                },
                                error : function(result) {
                                    btnEvent.preventDefault();
                                    console.error(result);
                                }
                            });
                        } catch(e) {
                            return e;
                        }
                        break;
                }
            }

        }

        function failVaildation() {
            inputHolder.removeClass('tick');
            inputHolder.addClass('cross');
            elemRemoveClass(errorBox,labelSmall,labelToggle);
            btnEvent.preventDefault();  //stop button action
        }

        codiceDestLabel.click(function() {
            elemAddClass(codiceDest);
            elemRemoveClass(postCert);
            $('.vat-reg__input').removeClass('vat-reg__input__active');
            $('.vat-reg__post-cert').children('').addClass('vat-reg__input__active');
        });

        postCertLabel.click(function() {
            elemAddClass(postCert);
            elemRemoveClass(codiceDest);
        });

        checkbox.click(function() {
            hiddenSection.toggleClass('vat-reg__flag--active');

            if ( hiddenSection.hasClass('vat-reg__flag--active') ) {
                b2cBtn.attr('data-class','');
                modCheckout.addClass('hidden');
            } else {
                b2cBtn.attr('data-class','hidden');
                modCheckout.removeClass('hidden');
            }

        });

        callback();

    }

});