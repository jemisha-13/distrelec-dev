(function($) {

    Tc.Module.StandaloneRegister.B2c = function (parent) {

        this.on = function (callback) {

            var $ctx = this.$ctx;
            var	errorSelf = this;

            this.validationErrorEmail = this.$$('#tmpl-bisnoderegb2c-validation-error-email').html();
            this.validationErrorEmpty = this.$$('#tmpl-bisnoderegb2c-validation-error-empty').html();

            axios.defaults.headers.common = {
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            };
            
            if(window.location.href.indexOf('/registration/checkout') > -1) {
                digitalData.page.pageInfo.registration.checkout="yes";
            } else {
            	digitalData.page.pageInfo.registration.checkout="no";
            }

            // Set Existing Customer Value to Null
            digitalData.user[0].registration.existing = '';

            if((document.querySelectorAll('.bd.error').length !== 0)) {
                document.querySelectorAll('.skin-standalone-register-b2c')[0].classList.remove('hidden');
                document.querySelectorAll('.skin-standalone-register-b2c')[0].classList.add('show');
                document.querySelectorAll('.skin-standalone-register-b2b')[0].classList.remove('show');
                document.querySelectorAll('.skin-standalone-register-b2b')[0].classList.add('hidden');
                document.getElementById('inlineRadio1').checked = true;
                document.querySelectorAll('label[for="inlineRadio2"]')[0].click();
                document.querySelectorAll('.form-b2b__companydetails.card-wrapper')[0].classList.add('hidden');

                if(document.getElementById('codiceFiscale') !== null) {
                    document.getElementById('register.codiceFiscaleB2Cexisting').value = localStorage.getItem('fiscale');
                }

                if(localStorage.getItem('datalayerReg') !== null) {
                    digitalData.user[0] = JSON.parse(localStorage.getItem('datalayerReg'));
                }

                if(localStorage.getItem('datalayerPageInfo') !== null) {
                    digitalData.page.pageInfo.register = JSON.parse(localStorage.getItem('datalayerPageInfo'));
                }

            }
            var customerNumInput = $('.form-b2c__form__title .field');
            var formWrapperInput = $('.form-b2c__form__wrapper .form-b2c__form__field .field');

            // Show Hide Customer Num Field B2C

            $('.form-b2c__form__field__custno .form-check-input').click(function() {
                var custNoField = $('.form-b2c__form__field__custno__input');
                var fiscale = $('.fiscale-container');

                if ($('#radioSelectYes').is(':checked')) {
                    custNoField.removeClass('hidden');

                    $('#existingCustomer').val('true');

                    digitalData.user[0].registration.existing = 'yes';

                    fiscale.addClass('hidden');

                    if (document.getElementById('codiceFiscale') !== null) {
                        document.getElementById('register.codiceFiscaleB2Cexisting').classList.remove('validate-empty');
                    }

                } else {
                    custNoField.addClass('hidden');

                    $('#existingCustomer').val('false');

                    digitalData.user[0].registration.existing = 'no';

                    fiscale.removeClass('hidden');

                    if (document.getElementById('codiceFiscale') !== null) {
                        document.getElementById('register.codiceFiscaleB2Cexisting').classList.add('validate-empty');
                    }
                }

            });

            // Form Submit B2C

            $('.form-b2c .btn-success').click(function(e) {
                e.preventDefault();

                $('.ajax-product-loader').removeClass('d-none');

                var validation = false;

                if (window.location.href.indexOf('/registration/checkout') > -1) {
                    document.getElementById('regTypeb2c').value = "checkout";
                }

                if ($('#radioSelectYes').is(':checked')) {
                    $('#existingCustomer').val('true');
                    digitalData.user[0].registration.existing = 'yes';
                } else {
                    $('#existingCustomer').val('false');
                    digitalData.user[0].registration.existing = 'no';
                }

                // Remove all existing error messages

                if (validation === true) {
                    $('.ajax-product-loader').addClass('d-none');
                    var errorElement = $('.field.error'),
                        isBeGlobalErrorAvailable = $('.standalone-register-holder .be-global-error .error').length;

                    if ( errorElement.length > 0 ) {
                        $("html, body").animate({scrollTop: errorElement.offset().top - 120}, 500);
                    } else {
                        $("html, body").animate({scrollTop: 0}, 500);
                    }

                    if ( errorElement.length > 1 && isBeGlobalErrorAvailable < 1) {
                        $('.standalone-register-holder .fe-global-error').removeClass('hidden');
                    }

                } else {

                    // Add Step 2 Active
                    var gid = $('.mod-captcha.form-b2c .g-recaptcha', self.$ctx).eq(0).data('gid');

                    if (typeof gid !== 'number') {
                        gid = grecaptcha.render($('.mod-captcha.form-b2c .g-recaptcha', self.$ctx)[0], {}, true);
                        $('.mod-captcha.form-b2c .g-recaptcha', self.$ctx).eq(0).data({'gid': gid});
                    }

                    localStorage.setItem('datalayerReg', JSON.stringify(digitalData.user[0]));
                    localStorage.setItem('datalayerPageInfo', JSON.stringify(digitalData.page.pageInfo.registration));

                    $('#registerB2CForm').find('input').each(function (i, v) {
                        $(v).val($(v).val().replace(/<[^>]*>/g, ""));
                    });

                    grecaptcha.reset(gid);
                    grecaptcha.execute(gid);

                    // Since recaptcha is added dynamically in the DOM, we need to wait a little bit so we can target it
                    setTimeout(function () {
                        var captchaIframe;
                        // Captcha contains different title for every language so we need to identify which iframe is for recaptcha
                        $('iframe[title]').each(function () {
                            var $currentIframe = $(this);
                            var title = $currentIframe.attr('title');
                            // if "recaptcha" is substring of title we found correct iframe
                            if (title.toLowerCase().indexOf('recaptcha') !== -1) {
                                captchaIframe = $currentIframe;
                            }
                        });

                        // We need to manually add event on recaptcha overlay (it don't have built in event for this)
                        // Once user clicks on it, recaptcha will be closed and we are hiding loading overlay
                        captchaIframe.parent().siblings('div').on('click', function () {
                            // Hide loading overlay
                            $('.ajax-product-loader').addClass('d-none');
                        });
                    }, 1000);
                }

            });

            parent.on(callback);
        };
    };

})(Tc.$);
