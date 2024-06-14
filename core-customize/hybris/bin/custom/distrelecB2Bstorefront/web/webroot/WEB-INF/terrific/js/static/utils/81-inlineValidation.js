/***********************************************************************************
 *    Validation which takes in place based on data-attributes on each form field
 *
 *      -> HTML Structure:
 *      <div class="js-inline-validation">
 *          <form class="js-iv-form">
 *              <div class="js-iv-item">
 *                  <div class="p-relative">
 *                      <input class="js-iv-field" type="text">
 *
 *                      <span class="js-iv-field-attrs" data-iv-triggers="change" data-iv-errors="empty" hidden></span>
 *
 *                      <i class="tickItem fa fa-check"></i>
 *                      <i class="tickItemError fa fa-times"></i>
 *                  </div>
 *
 *                  <div class="js-iv-errors">
 *                      <div class="js-iv-errors-empty" hidden>
 *                          Please enter your company name.
 *                      </div>
 *                  </div>
 *              </div>
 *          </form>
 *      </div>
 *
 *      -> HTML elements description:
 *          -> "js-inline-validation"
 *              -> Optional data attribute: data-iv-validate-previous-elements="true"
 *                  -> shows errors on ALL previous fields and scrolls window to 1st error in form
 *          -> "js-iv-form"
 *              -> Main form item (we can have multiple different forms on each page which have its own inline validation)
 *          -> "js-iv-item"
 *              -> Each field element should be in its own "js-iv-item" element
 *          -> "p-relative"
 *              -> Wrapper on based which we are positioning icons
 *          -> "js-iv-field"
 *              -> main form element (inputs, selects, textareas etc.)
 *              -> "select" needs to have additional classes for SelectBoxit and Google Libphonenumber plugins
 *                  -> "js-selectboxit" and "js-libphonenumber-isocode-select"
 *          -> "js-iv-field-attrs"
 *              -> Element which contain attributes, since we cannot add custom attrs through spring <form:input />
 *              -> "data-iv-triggers"
 *                  -> data attribute which contains list of events which will trigger validation
 *                  -> Example: data-iv-triggers="input focusout change", multiple events are divided with space
 *              -> "data-iv-errors"
 *                  -> data attribute which contains list of errors which can appear on element
 *                  -> Example: data-iv-errors="empty password phonenumber", multiple events are divided with space
 *          -> "js-iv-errors"
 *              -> Holds all error messages
 *          -> "js-iv-errors-XXXX"
 *              -> each error message
 *              -> XXXX - each error message, naming like in data-iv-errors
 *                  -> example "js-iv-errors-empty"
 *
 *    Usage see:
 *    - Checkout billing/delivery page
 *
 **********************************************************************************/

/**
 * Applies inline validation to element
 *
 * @param $scope - element which contains/wraps group of inline validation elements
 */
Tc.Utils.bindInlineValidation = function ($scope) {
    // Get main inline validation scope element
    var $inlineValidation = $('.js-inline-validation', $scope);

    // If inline validation element exists
    if ($inlineValidation.length) {
        // Get form
        var $ivForm = $('.js-iv-form', $inlineValidation);
        // Get all fields (inputs, selects, textareas...)
        var $ivFields = $('.js-iv-field', $ivForm);

        // Iterate through each field
        $ivFields.each(function () {
            // Get field in current iteration
            var $currentField = $(this);
            // For multiple forms on page, we need to find scope of current item
            var $scopeInlineValidation = $currentField.closest($inlineValidation);
            // Find all validation fields inside scope
            var $scopeIvFields = $scopeInlineValidation.find($ivFields);
            // Get flag on based which we will toggle functionality to check or not check previous elements in form
            var validatePrevElements = $scopeInlineValidation.data('iv-validate-previous-elements');
            // Get flag which shows us if we should execute validation immediately on page load
            var validateOnPageLoad = $scopeInlineValidation.data('iv-trigger-on-page-load');
            // When we have data from BE (which are valid), we can show success state immediately on them on page load
            var validateSuccessOnlyOnPageLoad = $scopeInlineValidation.data('iv-trigger-success-only-on-page-load');
            // Get sibling element which contains custom data-attributes
            var $currentFieldAttrs = $currentField.siblings('.js-iv-field-attrs');
            // Collect triggers value from "data-iv-trigger" so we know when to fire validation
            var triggers = $currentFieldAttrs.data('iv-triggers');

            // Executes validation on page load if TRUE
            if (validateOnPageLoad) {
                // Show/hide errors on field
                Tc.Utils.handleInlineValidationErrors($currentField, false);
            } else {
                // Executes only success validations on page load if TRUE
                if (validateSuccessOnlyOnPageLoad) {
                    if (Tc.Utils.handleInlineValidationErrors($currentField, true)) {
                        // Show/hide errors on field
                        Tc.Utils.handleInlineValidationErrors($currentField, false);
                    }
                }
            }

            // Bind trigger value on element
            $currentField.on(triggers, function () {
                // Get current field which is triggered
                var $currentTriggeredField = $(this);

                // Since fields can have other events binded, we are putting short timeout just to be sure that inline validation occurs last
                setTimeout(function () {
                    // Show/hide errors on field
                    Tc.Utils.handleInlineValidationErrors($currentTriggeredField, false);

                    // If flag for prev erros is TRUE, check all prev fields
                    if (validatePrevElements) {
                        // Validate all fields which are before triggered one (so user wouldn't miss any field)
                        Tc.Utils.handleValidationForPreviousElements($scopeIvFields, $currentTriggeredField);
                    }
                }, 100);
            });
        });
    }
}


/**
 * Function for showing/hiding error messages on element based on list in "data-iv-errors" attribute
 *
 * @param $field - field on which we are executing validation
 * @param returnValidation - flag which indicates if we will just return true/false or we will execute validation on element
 */
Tc.Utils.handleInlineValidationErrors = function ($field, returnValidation) {
    // Get sibling element which contains custom data-attributes
    var $fieldAttrs = $field.siblings('.js-iv-field-attrs');
    // Collect which errors should be shown on field
    var errors = $fieldAttrs.data('iv-errors');

    if (errors) {
        errors = errors.split(' ');

        // Iterate through errors
        for (var i=0; i < errors.length; i++) {
            var errorMsg = errors[i];

            switch (errorMsg) {
                case 'empty':
                    if (returnValidation) {
                        return Tc.Utils.handleInlineValidationErrorEmpty($field, returnValidation);
                    } else {
                        Tc.Utils.handleInlineValidationErrorEmpty($field, returnValidation);
                    }
                    break;
                case 'regex':
                    if (returnValidation) {
                        return Tc.Utils.handleInlineValidationErrorRegex($field, returnValidation);
                    } else {
                        Tc.Utils.handleInlineValidationErrorRegex($field, returnValidation);
                    }
                    break;
                case 'phonenumber':
                    if (returnValidation) {
                        return Tc.Utils.handleInlineValidationErrorPhonenumber($field, returnValidation);
                    } else {
                        Tc.Utils.handleInlineValidationErrorPhonenumber($field, returnValidation);
                    }
                    break;
                case 'email':
                    if (returnValidation) {
                        return Tc.Utils.handleInlineValidationErrorEmail($field, returnValidation);
                    } else {
                        Tc.Utils.handleInlineValidationErrorEmail($field, returnValidation);
                    }
                    break;
                case 'legalEmail':
                    if (returnValidation) {
                        return Tc.Utils.handleInlineValidationErrorLegalEmail($field, returnValidation);
                    } else {
                        Tc.Utils.handleInlineValidationErrorLegalEmail($field, returnValidation);
                    }
                    break;
                case 'postalCode':
                    if (returnValidation) {
                        return Tc.Utils.handleInlineValidationErrorPostalCode($field, returnValidation);
                    } else {
                        Tc.Utils.handleInlineValidationErrorPostalCode($field, returnValidation);
                    }
                    break;
                case 'password':
                    // Tc.Utils.handleInlineValidationErrorPassword($field);
                    break;
                default:
                    break;
            }
        }

        Tc.Utils.handleFormSubmitState($field);
    }
}


/**
 * Showing and hiding specific error messages based on flag
 *
 * @param $field -> form element which we are validating
 * @param errorType -> specific error type (empty, password, phonenumber...)
 * @param flag -> true: success, false: error
 */
Tc.Utils.toggleInlineValidationErrorMsg = function ($field, errorType, flag) {
    var $fieldGroup = $field.closest('.js-iv-item');

    // If field have class which will remove inline validation styles when field is empty (optional fields with inline validation), then remove inline validation styles
    if ($field.hasClass('js-iv-remove-error-on-blank') && $field.val().replace(/\s/g, '').length === 0) {
        $fieldGroup.removeClass('has-success has-error');
        $field.removeClass('success error');
        $('.js-iv-errors-' + errorType, $fieldGroup).hide();
    } else {
        $fieldGroup.toggleClass('has-success', flag).toggleClass('has-error', !flag);
        $field.toggleClass('success', flag).toggleClass('error', !flag);
        $('.js-iv-errors-' + errorType, $fieldGroup).toggle(!flag);
    }
}


/**
 * Showing and hiding "empty" error messages based on flag
 *
 * @param $field -> form element which we are validating
 * @param returnValidation - flag which indicates if we will just return true/false or we will execute validation on element
 */
Tc.Utils.handleInlineValidationErrorEmpty = function ($field, returnValidation) {
    var isValidValue;

    if (!!$field.val()) {
        isValidValue = $field.val().replace(/\s/g, '') !== '';
    } else {
        isValidValue = false;
    }

    if (returnValidation) {
        return isValidValue;
    } else {
        // If field is not empty, show success
        // Otherwise show error
        Tc.Utils.toggleInlineValidationErrorMsg($field, 'empty', isValidValue);
    }
}


/**
 * Showing and hiding "regex" error messages based on flag
 *
 * @param $field -> form element which we are validating
 * @param returnValidation - flag which indicates if we will just return true/false or we will execute validation on element
 */
Tc.Utils.handleInlineValidationErrorRegex = function ($field, returnValidation) {
    // Get regex from localised text property
    var regex = new RegExp('^' + $field.siblings('.js-iv-field-attrs').data('iv-regex') + '$');
    // Check if regex is valid
    var regexValid = regex.test($field.val());

    // If pattern allows everything, we are checking if there is min number of chars
    if ($field.val().replace(/\s/g, '').length === 0) {
        regexValid = false;
    }

    if (returnValidation) {
        return regexValid;
    } else {
        // If regex is valid
        Tc.Utils.toggleInlineValidationErrorMsg($field, 'regex', regexValid);
    }
}


/**
 * Showing and hiding phone error messages based on flag
 *
 * @param $field -> form element which we are validating
 * @param returnValidation - flag which indicates if we will just return true/false or we will execute validation on element
 */
Tc.Utils.handleInlineValidationErrorPhonenumber = function ($field, returnValidation) {
    var isValidValue = $field.val().replace(/\s/g, '') !== '';

    if (returnValidation) {
        if (isValidValue) {
            return Tc.Utils.isPhoneNumberValidByGoogleLibphonenumber($field);
        } else {
            return false;
        }
    } else {
        if (isValidValue) {
            Tc.Utils.toggleInlineValidationErrorMsg($field, 'phonenumber', Tc.Utils.isPhoneNumberValidByGoogleLibphonenumber($field));
        } else {
            Tc.Utils.toggleInlineValidationErrorMsg($field, 'phonenumber', false);
        }
    }
}


/**
 * Showing and hiding "email" error messages based on flag
 *
 * @param $field -> form element which we are validating
 * @param returnValidation - flag which indicates if we will just return true/false or we will execute validation on element
 */
Tc.Utils.handleInlineValidationErrorEmail = function ($field, returnValidation) {
    // Regex for email
    var regex = /\S+@\S+\.\S+/;
    // If value is passing regex for email, add TRUE
    var isValidValue = regex.test($field.val());

    if (returnValidation) {
        return isValidValue;
    } else {
        Tc.Utils.toggleInlineValidationErrorMsg($field, 'email', isValidValue);
    }
}


/**
 * Showing and hiding "legalEmail" error messages based on flag
 *
 * @param $field -> form element which we are validating
 * @param returnValidation - flag which indicates if we will just return true/false or we will execute validation on element
 */
Tc.Utils.handleInlineValidationErrorLegalEmail = function ($field, returnValidation) {
    // Regex for email
    var regex = /\S+@\S+\.\S+/;
    // Get domain part of email (after "@" char)
    var emailSplit = $field.val().toString().split("@").pop();
    // Email legal needs to contain some specific words "pec", "legal", "cert" or "Sicurezzapostale"
    var emailCheck = emailSplit.indexOf('pec') !== -1 || emailSplit.indexOf('legal') !== -1 || emailSplit.indexOf('cert') !== -1 || emailSplit.indexOf('Sicurezzapostale') !== -1;
    // If value is passing regex for email, add TRUE
    var isValidValue = regex.test($field.val()) && emailCheck;

    if (returnValidation) {
        return isValidValue;
    } else {
        Tc.Utils.toggleInlineValidationErrorMsg($field, 'legalEmail', isValidValue);
    }
}


Tc.Utils.handleInlineValidationErrorPassword = function ($field, $confirmField) {
    // ...
}


Tc.Utils.handleInlineValidationErrorPostalCode = function ($field, returnValidation) {
    var fieldValue = $field.val();
    var regexValid;

    // If pattern allows everything, we are checking if there is min number of chars
    if (fieldValue.length === 0) {
        regexValid = false;
    } else {
        // For GUEST and BIZ, user needs to select country from dropdown, so postalCode needs to be validated by users selection
        var $postalIsocodeTranslations = $('.js-postalCode-isocode-translations');
        // Get regex from localised text property
        var regex = new RegExp('^' + $field.siblings('.js-iv-field-attrs').data('iv-postal-code-pattern') + '$');

        if ($postalIsocodeTranslations.length) {
            var $isocodeSelect = $field.closest('form').find('.js-postalCode-isocode-select');
            var isoCode = !!$isocodeSelect.val() ? $isocodeSelect.val() : $('body').data('isocode');
            var postalRegex = $postalIsocodeTranslations.data('postal-validation-pattern_' + isoCode.toLowerCase());
            var postalErrorMsg = $postalIsocodeTranslations.data('postal-validation-msg_' + isoCode.toLowerCase());

            if (!postalRegex) {
                postalRegex = $postalIsocodeTranslations.data('postal-validation-pattern_default');
            }

            if (!postalErrorMsg) {
                postalErrorMsg = $postalIsocodeTranslations.data('postal-validation-msg_default');
            }

            regex = new RegExp('^' + postalRegex + '$');
            $field.closest('.js-iv-item').find('.js-iv-errors-postalCode').html(postalErrorMsg);
        }

        // For specific countries we are reformating postal code into "xxx xx" format
        // User can enter value with or without space (e.g. "12345" or "123 45", both will be valid)
        if (digitalData.page.pageInfo.countryCode === 'SE' || digitalData.page.pageInfo.countryCode === 'CZ' || digitalData.page.pageInfo.countryCode === 'SK') {
            fieldValue = fieldValue.replace(/\s/g, '');

            // If there are more than 3 chars, add blank space after 3rd char
            if (fieldValue.length > 3) {
                fieldValue = fieldValue.substr(0, 3) + ' ' + fieldValue.substr(3);
            }

            // Display formatted value in field
            $field.val(fieldValue);
        }

        // Check if regex is valid
        regexValid = regex.test(fieldValue);
    }

    if (returnValidation) {
        return regexValid;
    } else {
        // If regex is valid
        Tc.Utils.toggleInlineValidationErrorMsg($field, 'postalCode', regexValid);
    }
}


/**
 * Showing and hiding error messages for skipped fields (if user skip field in population we need to throw error on prev field which is not populated)
 *
 * @param $allFields -> all fields in form
 * @param $field - current field, since we need to throw errors until we get in iteration till current element
 */
Tc.Utils.handleValidationForPreviousElements = function ($allFields, $field) {
    var currentFieldReached = false;
    var errorOccured = false;

    $allFields.each(function () {
        var $currentField = $(this);

        if ($currentField.is($field)) {
            currentFieldReached = true;
        } else {
            if (!currentFieldReached) {
                Tc.Utils.handleInlineValidationErrors($currentField, false);

                if (!errorOccured) {
                    if ($currentField.hasClass('error')) {
                        errorOccured = true;
                        Tc.Utils.scrollToFirstError($currentField, 500, 300);
                    }
                }
            }
        }
    });
}


/**
 * Enabling and disabling submit button based on number of total fields and success fields
 *
 * @param $ivField -> current field in form
 */
Tc.Utils.handleFormSubmitState = function ($ivField) {
    setTimeout(function () {
        // Get scope for which we are checking total fields and success fields
        var $scopeForm = $ivField.closest('.js-iv-form');
        // Get submit button inside scope
        var $ivFormSubmit = $scopeForm.find('.js-iv-form-submit');
        // Get total number of fields which exists in form
        var totalFields = $scopeForm.find('.js-iv-field:input').length;
        // Get total number of success fields which exists in form
        var successFields = $scopeForm.find('.js-iv-field.success:input').length;

        // If total number of fields is equal to success fields, enable button, otherwise disable it
        $ivFormSubmit.prop('disabled', successFields < totalFields);
    }, 200);
}


/**
 * Function for removing inline validation message and styles if element is empty
 *
 * @param $field -> field in form
 */
Tc.Utils.removeInlineValidationStylesFromElement = function ($field) {
    if ($field.hasClass('js-iv-field')) {
        $field.removeClass('success error');

        // For selects, we need to remove also classes from selectboxit element
        if ($field.is('select')) {
            $field.siblings('span.selectboxit-container').find('.js-iv-field').removeClass('success error');
        }
    }
}


/**
 * Function for removing error or success states on field as user types
 * When populating LAST item (not last in row, last in population), as user types if he enters success data, we are enabling SAVE button immediately
 *
 * @param $ctx -> scope where we will bind event
 */
Tc.Utils.lastPopulatedFieldCheck = function ($ctx) {
    var $ivFields = $('.js-iv-field', $ctx);

    // As user types, remove error or success states on field
    // When populating LAST item (not last in row, last in population), as user types if he enters success data, we are enabling SAVE button immediately
    $ivFields.on('input', function () {
        var $currentField = $(this);

        $currentField.removeClass('success error');
        $currentField.closest('.js-iv-item').removeClass('has-success has-error');

        var $ivScope = $currentField.closest('.js-inline-validation');
        var $ivScopeFields = $ivScope.find($ivFields);
        var $ivScopeFieldsSuccess = $('.js-iv-field.success:input', $ivScope);
        var $ivScopeSaveButton = $('.js-iv-form-submit', $ivScope);
        var numberOfErrorFields = $ivScopeFields.length - $ivScopeFieldsSuccess.length;

        // If all other fields are SUCCESS, it means this is the last one, we will trigger inline validation on it if true and enable submit button
        if (numberOfErrorFields === 1) {
            $ivScopeSaveButton.prop('disabled', !Tc.Utils.handleInlineValidationErrors($currentField, true));

            if (Tc.Utils.handleInlineValidationErrors($currentField, true)) {
                Tc.Utils.handleInlineValidationErrors($currentField, false);
            }
        }
    });
}
