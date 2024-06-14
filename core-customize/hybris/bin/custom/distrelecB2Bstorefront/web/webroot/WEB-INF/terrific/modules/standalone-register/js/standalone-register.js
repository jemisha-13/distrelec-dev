(function($) {

    Tc.Module.StandaloneRegister = Tc.Module.extend({

        on: function(callback) {

            var $ctx = this.$ctx;

            // String selector for consent checkboxes
            var consentCheckboxesSelector = '.js-consentSection .js-checkbox';
            var consentCheckboxesIcon = '.js-checkboxIcon';
            var validation = true;

            // On page load, set datalayer variable to empty array
            digitalData.page.pageInfo.regPrefAdded = [];

            // When user clicks on the consent checkbox
            $ctx.on('change', consentCheckboxesSelector, function() {
                // Cleanup datalayer variable so we can pickup new ones
                digitalData.page.pageInfo.regPrefAdded = [];

                // Iterate through consent checkboxes
                $(consentCheckboxesSelector).each(function(i, el) {
                    var $el = $(el);

                    // If checkbox is checked and it is visible (we have 2 forms, b2b and b2c, only one can be visible)
                    if ($el.is(':checked') && $el.is(':visible')) {
                        // Add consent item into datalayer so we can pickup it up in Ensighten
                        digitalData.page.pageInfo.regPrefAdded.push($el.attr('name'));
                    }
                });
            });

            // Once we click on any checkbox in consent section, toggle class on parent based on its status is checked or unchecked
            $(consentCheckboxesSelector, $ctx).on('change', function () {
                var $changedCheckbox = $(this);
                $changedCheckbox.parent().toggleClass('active', $changedCheckbox.is(':checked'));
            });

            // If user clicks on "tick" icon in terms and conditions, disable button and change main checkbox to unchecked and trigger change
            $(consentCheckboxesIcon).click(function(){
                var $clickedIcon = $(this);

                if ($clickedIcon.hasClass('js-terms-check')) {
                    validation = true;
                    $('.btn-register-customer-form:visible').toggleClass('disabled', validation);
                }

                $clickedIcon.siblings('.js-checkbox').prop('checked', false).trigger('change');
            });

            /**
             *  As user changes the inputs
             *  Update validation and disable submit button accordingly
             **/
            $ctx.on('change', function() {
                if ( !($('.js-termsCheckbox:visible').is(':checked')) ) {
                    validation = true;
                } else {
                    $('.js-form input:visible').each(function() {
                        if ($(this).attr('type') !== 'radio' && !$(this).hasClass('js-optional')) {
                            if ($(this).hasClass('error') || $(this).val() === "" ) {
                                validation = true;
                                return false;
                            } else {
                                validation = false;
                            }
                        }
                    });
                }

                validateTitle($('.field-title:visible'));

                $('.btn-register-customer-form:visible').toggleClass('disabled', validation);
            });

            /**
             * When user clicks on the field
             * Validate previous fields that have "validate" class to it
             **/
            $ctx.on('change', '.field-title:visible', function () {
                validateTitle($(this));
            });

            /**
             * When user clicks on the field
             * Validate previous fields that have "validate" class to it
             **/
            $ctx.on('focusin', '.js-validate:visible', function () {
                var $focusedElement = $(this);
                var isOrder0 = $focusedElement.closest('.order-0');
                var isOrder1 = $focusedElement.closest('.order-1');
                var elementIndex = $('.js-validate:visible').index($(this));

                // Since for BIZ we are reordering items with CSS class, in this case we need to reduce index
                if (isOrder0.length) {
                    elementIndex--;
                }

                // Since for BIZ we are reordering items with CSS class, in this case we need to add it by 2
                if (isOrder1.length) {
                    elementIndex+=2;
                }

                validatePreviousFieldCorrect(elementIndex, $('.js-validate:visible'));
            });

            /**
             * When user changes input value
             * Validate if any input fields are empty when they click out
             **/
            $ctx.on('change', '.js-validate-email', function () {
                validateEmailViaRegex($(this));
            });

            /**
             * When user changes input value
             * Validate if any input fields are empty when they click out
             **/
            $ctx.on('input', '.js-validate-email', function () {
                validateEmailViaRegexRemoveErrorOnly($(this));
            });

            /**
             * When user focuses out of field
             * Send request to BE (entered email as parameter) and get response if email already exists in database
             **/
            $ctx.unbind('focusout').on('focusout', '.js-already-exists-validation', function () {
                var $focusedOutField = $(this);

                // Since we need to wait until email validation is done, we are doing small delay for code execution
                setTimeout(function () {
                    // If: email validation is succeed, check if email is already registered
                    // Else: email validation didn't pass, remove error message for "already existing validation" in case it was shown before
                    if ($focusedOutField.hasClass('success')) {
                        validateIfEmailAlreadyExists($focusedOutField);
                    } else if (!$focusedOutField.hasClass('error')) {
                        removeInputError($focusedOutField, 'div.js-error-already-existing');
                    }
                }, 100);
            });

            /**
             *  When user clicks on the first password input
             *  Validate if minimum char is met on inputs when they click out
             *  And if the second password input has value
             *  Throw an error there
             **/
            $ctx.on('input', '.password-field:visible', function () {
                validatePdwInputField(6, $(this), $('.passwordSecond:visible'));
            });

            /**
             *  When user types repeat email, Validate that passwords match
             **/
            $ctx.on('input', '.passwordSecond:visible', function () {
                validatePwdInputMatches(6, $('.password-field:visible'), $(this));
            });

            /**
             *  When user changes the select field, validate it
             **/
            $ctx.on('change', '.js-validate-select:visible', function () {
                validateSelectField($(this));
            });

            /**
             *  When user is on BIZ,
             *  And they click anywhere on the page,
             *  If the field isn't selected, throw validation error
             **/
            $ctx.unbind('click').on('click', function (e) {
                var $clickedItem = $(this);

                if ($('.countrySelectExportShop:visible').children("option:selected").val() === '' && !$('.js-country-select:visible', $clickedItem).is(':focus')) {
                    validateSelectField($('.countrySelectExportShop:visible'));
                }
            });

            /**
             * When user changes value, validate that the field is regex correct
             * Otherwise highlight the input as correct
             * And for the phone field, validate if it is correct when it has values
             */
            $ctx.on('change', '.js-validate-empty', function () {
                var thisInput = $(this);
                var isValidValue = thisInput.val().trim() !== '';

                if (!isValidValue) {
                    displayInvalidInputError(thisInput);
                } else {
                    if (thisInput.hasClass('js-libphonenumber')) {
                        validatePhoneNumber($('.js-libphonenumber:visible'));
                        validateSelectField($('.validate-select:visible'));
                    } else {
                        removeInputError(thisInput);
                    }
                }
            });

            /**
             * As the user types, remove error msg if value is not empty
             */
            $ctx.on('input', '.js-validate-empty', function () {
                var thisInput = $(this);

                if (thisInput.val().trim() !== '') {
                    removeInputErrorMessageOnly(thisInput);
                }
            });

            $ctx.on('change', '.js-validate-minimum-characters', function () {
                var minChar = 6;
                if ($(this).hasClass('codice')) {
                    minChar = 16;
                }
                validateMinCharInput($(this), minChar);
            });

            /**
             * When user changes the country on registration,
             * Update the body tag
             * And validate the mobile number
             */
            $('.js-country-select').on('change', function(){
                var selectedCountry = $('.js-country-select:visible').children("option:selected").val();
                var phoneNumberInput = $('.js-libphonenumber:visible');
                $('body').data('isocode', selectedCountry);
                validatePhoneNumber(phoneNumberInput);
            });

            function validateSelectField(selectedOption) {
                if (selectedOption.children("option:selected").val() === '') {
                    displayInvalidInputError(selectedOption);
                } else {
                    removeInputError(selectedOption);
                }
            }

            function validateTitle(validateTitleForm) {
                var firstInput = $(validateTitleForm.find('input')[0]);
                var secondInput = $(validateTitleForm.find('input')[1]);

                if (!firstInput.is(':checked') && !secondInput.is(':checked')) {
                    validateTitleForm.parent().find('div.error').removeClass("hidden");
                    validateTitleForm.find('.fa-times').removeClass('hidden');
                    validation = true;
                } else {
                    validateTitleForm.parent().find('div.error').addClass("hidden");
                    validateTitleForm.find('.fa-times').addClass('hidden');
                    validateTitleForm.find('.fa-check').removeClass('hidden');
                }
            }

            function removeInputError(thisInput, errorMsgSelector) {
                // Define parent element (element which holds group for each form element)
                var $parent = thisInput.parent();

                // If parent is "js-p-relative"
                // (wrapper for field and icon, to resolve possible issue if text above/below is wrapped into multiple rows, icon will have wrong position)
                if ($parent.hasClass('js-p-relative')) {
                    // Set parent to be parent of ".js-p-relative" -> it will be ".form-group / .form-group-select"
                    $parent = $parent.parent();
                }

                if (!errorMsgSelector) {
                    errorMsgSelector = 'div.error';
                }

                thisInput.removeClass('error');
                thisInput.addClass('success');
                thisInput.siblings('.fa-times').addClass("hidden");
                thisInput.siblings('.fa-check').removeClass("hidden");

                $parent.find(errorMsgSelector).addClass("hidden");
            }

            function removeInputErrorMessageOnly(thisInput, errorMsgSelector) {
                // Define parent element (element which holds group for each form element)
                var $parent = thisInput.parent();

                // If parent is "js-p-relative"
                // (wrapper for field and icon, to resolve possible issue if text above/below is wrapped into multiple rows, icon will have wrong position)
                if ($parent.hasClass('js-p-relative')) {
                    // Set parent to be parent of ".js-p-relative" -> it will be ".form-group / .form-group-select"
                    $parent = $parent.parent();
                }

                if (!errorMsgSelector) {
                    errorMsgSelector = 'div.error';
                }

                thisInput.removeClass('error');
                thisInput.siblings('.fa-times').addClass("hidden");

                $parent.find(errorMsgSelector).addClass("hidden");
            }

            function displayInvalidInputError(thisInput, errorMsgSelector) {
                // Define parent element (element which holds group for each form element)
                var $parent = thisInput.parent();

                // If parent is "js-p-relative"
                // (wrapper for field and icon, to resolve possible issue if text above/below is wrapped into multiple rows, icon will have wrong position)
                if ($parent.hasClass('js-p-relative')) {
                    // Set parent to be parent of ".js-p-relative" -> it will be ".form-group / .form-group-select"
                    $parent = $parent.parent();
                }

                if (!errorMsgSelector) {
                    errorMsgSelector = 'div.error';
                }

                thisInput.addClass('error');

                if (thisInput.hasClass('success')) {
                    $parent.find('i.fa-check').addClass("hidden");
                    thisInput.removeClass('success');
                }

                $parent.find('.fa-times').removeClass("hidden");
                $parent.find(errorMsgSelector).removeClass("hidden");
            }

            // Use try and catch block when isValidNumberForRegion throws an error for validating number
            function validatePhoneNumber(input) {
                try {
                    // Define parent element (element which holds group for each form element)
                    var $parent = input.parent();
                    // Get isocode from body element
                    var isoCode = $('body').data('isocode');
                    // Get country isocode from users choice in "select" form element
                    var selectedCountryIsocode = $('.js-country-select:visible').val();

                    // For BIZ (EX), we need to get value from users selection on country select field element
                    if (isoCode === 'EX') {
                        // If users choice is Norther Ireland (XI), then we use GB (same logic in backend)
                        isoCode = selectedCountryIsocode === 'XI' ? 'GB' : selectedCountryIsocode;
                    }

                    // If isocode is Norther Ireland (XI), then we use GB (same logic in backend)
                    if (isoCode === 'XI') {
                        isoCode = 'GB';
                    }

                    // If parent is "js-p-relative"
                    // (wrapper for field and icon, to resolve possible issue if text above/below is wrapped into multiple rows, icon will have wrong position)
                    if ($parent.hasClass('js-p-relative')) {
                        // Set parent to be parent of ".js-p-relative" -> it will be ".form-group / .form-group-select"
                        $parent = $parent.parent();
                    }

                    // For BIZ (EX), we need to get value from users selection on country select field element
                    if (libphonenumber.isValidNumberForRegion(input.val(), isoCode)) {
                        $parent.find('div.error').addClass("hidden");
                        removeInputError(input);
                    } else {
                        displayInvalidInputError(input);
                    }
                } catch (error) {
                    displayInvalidInputError(input);
                }
            }

            // Function for validating email value
            function validateEmailViaRegex(input) {
                var regex = /\S+@\S+\.\S+/,   //regex for email
                    flag = regex.test(input.val()),
                    isLegalEmail = input.hasClass('js-legalEmail'); //flag returns false if regex fails

                if (isLegalEmail) {
                    // For "legal" email, for valid email, domain of email should contain some specific words like "pec", "legal" etc.
                    // https://wiki.distrelec.com/pages/viewpage.action?pageId=28641672
                    var regexLegal = /\S+@\S+\.\S+/,   //regex for email
                        emailSplit = input.val().toString().split("@").pop(),
                        emailCheck = emailSplit.indexOf('pec') !== -1 || emailSplit.indexOf('legal') !== -1 || emailSplit.indexOf('cert') !== -1 || emailSplit.indexOf('Sicurezzapostale') !== -1,
                        flagLegal = regexLegal.test(input.val()); //flag returns false if regex fails

                    if(flagLegal === true && emailCheck === true) {
                        removeInputError(input);
                    } else {
                        displayInvalidInputError(input);
                    }
                } else {
                    if (flag) {
                        removeInputError(input);
                    } else {
                        displayInvalidInputError(input);
                    }
                }
            }

            // Function for removing error message on email field
            function validateEmailViaRegexRemoveErrorOnly(input) {
                var regex = /\S+@\S+\.\S+/,   //regex for email
                    flag = regex.test(input.val()),
                    isLegalEmail = input.hasClass('js-legalEmail'); //flag returns false if regex fails

                if (input.val().trim() !== '') {
                    removeInputErrorMessageOnly(input);
                }
            }

            function validateMinCharInput(input, minCharacters) {
                if (input.val().length < minCharacters) {
                    displayInvalidInputError(input);
                } else {
                    removeInputError(input);
                }
            }

            function validatePdwInputField(minCharacters, pwdField, pwdFieldRepeat) {
                // Define parent element (element which holds group for each form element)
                var $parent = pwdField.parent();

                // If parent is "js-p-relative"
                // (wrapper for field and icon, to resolve possible issue if text above/below is wrapped into multiple rows, icon will have wrong position)
                if ($parent.hasClass('js-p-relative')) {
                    // Set parent to be parent of ".js-p-relative" -> it will be ".form-group / .form-group-select"
                    $parent = $parent.parent();
                }

                $parent.find('i.fa-eye').addClass("right");
                $parent.find('i.fa-eye-slash').addClass("right");

                if (pwdField.val().length < minCharacters) {
                    displayInvalidInputError(pwdField);
                    $parent.find('div.help-text').addClass("error");
                } else {
                    removeInputError(pwdField);
                }

                if (pwdFieldRepeat.val() !== '') {
                    validatePwdInputMatches(minCharacters, pwdField, pwdFieldRepeat);
                }
            }

            function validatePwdInputMatches(minCharacters, pwdField, pwdFieldRepeat) {
                // Define parent element (element which holds group for each form element)
                var $parent = pwdFieldRepeat.parent();

                // If parent is "js-p-relative"
                // (wrapper for field and icon, to resolve possible issue if text above/below is wrapped into multiple rows, icon will have wrong position)
                if ($parent.hasClass('js-p-relative')) {
                    // Set parent to be parent of ".js-p-relative" -> it will be ".form-group / .form-group-select"
                    $parent = $parent.parent();
                }

                $parent.find('i.fa-eye').addClass("right");
                $parent.find('i.fa-eye-slash').addClass("right");


                if (pwdField.val() !== pwdFieldRepeat.val()) {
                    displayInvalidInputError(pwdFieldRepeat);
                } else {
                    if (pwdFieldRepeat.val() === '' || (pwdFieldRepeat.val().length < minCharacters)) {
                        displayInvalidInputError(pwdFieldRepeat);
                    } else {
                        removeInputError(pwdFieldRepeat);
                    }
                }
            }

            // Validate the fields that have class 'validate'
            // Navigate user on the first error input
            // Apart from inputs, validate select and title inputs if present
            function validatePreviousFieldCorrect(index, objectOfInputs) {
                var isInvalidFieldIndex = [];
                objectOfInputs.each(function(i, previousInput) {
                    if (index === i) {
                        if (isInvalidFieldIndex.length > 0) {
                            $("html, body").animate({scrollTop: $(objectOfInputs[isInvalidFieldIndex[0]]).offset().top - 300}, 500);
                        }
                        return false;
                    }

                    if ($(previousInput).val() === "") {
                        isInvalidFieldIndex.push(i);

                        if ($(previousInput).hasClass('password-check')) {
                            validatePdwInputField(6, $(previousInput), $('.passwordSecond:visible'));
                        } else if ($(previousInput).hasClass('passwordSecond')) {
                            validatePwdInputMatches(6, $('.password-field:visible'), $(previousInput));
                        }  else {
                            displayInvalidInputError($(previousInput));
                        }

                    } else if ($(previousInput).hasClass('js-validate-select')) {
                        validateSelectField($(previousInput));
                    } else if ($(previousInput).hasClass('title')) {
                        validateTitle($('.field-title:visible'));
                    }
                });
            }

            // Function which executes request towards BE and returns HTTPS status on based which we see if email is already registered or not
            var validateIfEmailAlreadyExists = function ($emailField) {
                // Get loader element
                var $loader = $emailField.parent().find('.js-loading');
                // Disable email field
                $emailField.attr('disabled', true);

                // Show loader element
                $loader.removeClass('hidden');

                $.ajax({
                    url: '/registration/validateUid',
                    type: 'POST',
                    dataType: 'json',
                    data: {
                        "uid": $emailField.val()
                    },
                    complete: function(xhr) {
                        switch (xhr.status) {
                            case 200:
                                // Status when email is not in database and user can register with it
                                removeInputError($emailField, 'div.js-error-already-existing');
                                break;
                            case 409:
                                // Status when email is already registered
                                displayInvalidInputError($emailField, 'div.js-error-already-existing');
                                break;
                            default:
                                break;
                        }

                        // Since request can be executed very fast, we can have situation that loading animation won't be visible
                        // So in addition to see it at least for half a second, we are adding timeout
                        setTimeout(function () {
                            // Hide loader
                            $loader.addClass('hidden');
                            // Enable email field again
                            $emailField.attr('disabled', false);
                        }, 500);
                    }
                });
            };

            // Copied from B2C and B2B js files to reduce duplications
            $ctx.on('click', '.js-form .form-group__pwd-reveal', function() {
                var pwdField = $(this).prev('input'),
                    pwdFieldIcon = $(this).find('.form-group__pwd-reveal-icon'),
                    pwdFieldIconOpen = $(this).find('.form-group__pwd-reveal-icon.fa-eye'),
                    pwdFieldIconClose = $(this).find('.form-group__pwd-reveal-icon.fa-eye-slash');
                pwdFieldIcon.addClass('hidden');

                if (pwdField.attr('type') === 'password') {
                    pwdField.attr('type', 'text');
                    pwdFieldIconOpen.removeClass('hidden');
                } else {
                    pwdField.attr('type', 'password');
                    pwdFieldIconClose.removeClass('hidden');
                }

            });

            callback();
        }

    });

})(Tc.$);
