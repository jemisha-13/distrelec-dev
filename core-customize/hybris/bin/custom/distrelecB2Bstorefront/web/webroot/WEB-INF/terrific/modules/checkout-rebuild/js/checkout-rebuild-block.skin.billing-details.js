(function($) {

    /**
     * This module implements the delivery options on checkout page
     *
     * @namespace Tc.Module
     * @class CheckoutRebuildBlock
     * @skin BillingDetails
     */
    Tc.Module.CheckoutRebuildBlock.BillingDetails = function (parent) {

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {
            // If we want to call "CheckoutRebuildBlock" on load
            // parent.on(callback);

            var self = this,
                $ctx = self.$ctx;

            this.$ctx = $ctx;
            this.formResetData = {};
            this.sameBillingAndShippingSubmitted = false;
            this.CheckoutRebuildBlock = new Tc.Module.CheckoutRebuildBlock();
            this.CheckoutRebuildDeliveryDetails = new Tc.Module.CheckoutRebuildBlock.DeliveryDetails();

            // Get select element on which we will init selectboxit plugin
            var $selectboxit = $('.js-selectboxit', $ctx);
            // Get select element which changes isocode
            var $libphoneNumberIsocodeSelect = $('.js-libphonenumber-isocode-select', $ctx);
            // For GUEST and BIZ, user needs to select country from dropdown, so postalCode needs to be validated by users selection
            var $postalIsocodeTranslations = $('.js-postalCode-isocode-translations');
            var $postalCodeIsocodeSelect = $('.js-postalCode-isocode-select', $ctx);

            // Bind events for triggering inline validation
            Tc.Utils.bindInlineValidation($ctx);
            // Initialize "SelectBoxit" plugin
            Tc.Utils.initSelectboxit($selectboxit);
            // When user clicks on the submit button, trigger ajax
            self.bindBillingForm($ctx);
            // Editing the form
            self.bindEditableForm($ctx);
            // Checkbox for same billing and shipping
            self.bindSameBillingShippingCheckbox($ctx);
            // Set as default
            self.bindBillingAddressSetDefault($ctx);

            // As isocode changes, validate phone field
            $libphoneNumberIsocodeSelect.on('change', function () {
                var $changedSelect = $(this);
                // Get visible select element (on other pages we can have more of them in different tabs)
                var $visibleLibphoneNumberSelect = $changedSelect.closest('form').find('.js-libphonenumber');

                // If value is not empty, execute validation
                if ($visibleLibphoneNumberSelect.val().trim() !== '') {
                    Tc.Utils.handleInlineValidationErrorPhonenumber($visibleLibphoneNumberSelect);
                }
            });

            if ($postalIsocodeTranslations.length) {
                // As isocode changes, validate postalCode field
                $postalCodeIsocodeSelect.on('change', function () {
                    var $changedSelect = $(this);
                    // Get visible select element (on other pages we can have more of them in different tabs)
                    var $postalCodeField = $changedSelect.closest('form').find('.js-postal-code');

                    // If value is not empty, execute validation
                    if ($postalCodeField.val().trim() !== '') {
                        Tc.Utils.handleInlineValidationErrorPostalCode($postalCodeField);
                    }
                });
            }

            // As user types (last populating field) if he enters success data, we are enabling SAVE button immediately
            Tc.Utils.lastPopulatedFieldCheck($ctx);

            callback();
        };


        // Function for changing between titles for billing only and for both billing and delivery details
        this.changeTitle = function (showBillingAndDeliveryTitles) {
            var self = this;
            var $dynamicTitle = $('.js-billing-form-dynamic-title', self.$ctx);
            var billingTitle = $dynamicTitle.data('billing-title');
            var billingDeliveryTitle = $dynamicTitle.data('billing-delivery-title');
            $('.js-rebuild-block-title', self.$ctx).html(showBillingAndDeliveryTitles ? billingDeliveryTitle : billingTitle);
        };


        // Hiding sameBillingShipping checkbox if pickup
        this.isPickupSelected = function (isSelected) {
            $(this.$ctx).toggleClass('is-pickup-selected', isSelected);
        };


        // Bind click on submit button and executes ajax request
        this.bindBillingForm = function ($ctx) {
            var thisMod = this;
            // Get billing form element
            var $billingForm = $('.js-billing-form', $ctx);
            // Get submit button element
            var $billingFormSubmitButton = $('.js-billing-form-submit', $billingForm);
            var $delveryDetailsBlock = $('.mod.js-rebuild-block-delivery-details');

            // Add data which are loaded from BE to resetData so we can refer to it later
            thisMod.formResetData = Tc.Utils.serializeArray($billingForm);

            $billingFormSubmitButton.on('click', function () {
                var $clickedButton = $(this);
                // Get customer type from BE
                var customerType = $billingForm.data('customer-type');
                // For B2E and GUEST we don't have delivery form
                var shippingNotAvailable = customerType === 'B2E' || customerType === 'GUEST';

                // Close all error msgs
                Tc.Utils.globalMessagesTriggerClose();

                // Hide all remove banners
                $('.js-da-list-item-remove').hide(300);

                // Prevent multiple clicks
                $clickedButton.prop('disabled', true);

                // Show loading animation
                thisMod.$ctx.addClass('is-loading');
                $delveryDetailsBlock.addClass('is-loading');

                // For b2b key account, we will use b2b
                if (customerType === 'B2B_KEY_ACCOUNT') {
                    customerType = 'B2B';
                }

                // Format Swedish, Czech and Slovak postal code before form submit (xxx xx)
                if (digitalData.page.pageInfo.countryCode === 'SE' || digitalData.page.pageInfo.countryCode === 'CZ' || digitalData.page.pageInfo.countryCode === 'SK') {
                    var $postalCode = $('.js-postal-code', $billingForm);
                    var postalCode = $postalCode.val();

                    if (postalCode.length === 5) {
                        $postalCode.val(postalCode.substring(0, 3) + " " + postalCode.substring(3));
                    }
                }

                $.ajax({
                    url: '/checkout/delivery/billing/' + customerType.toLowerCase(),
                    type: 'POST',
                    data: $billingForm.serialize(),
                    success: function(response) {
                        var $sameBillingDelivery = $('.js-same-billing-and-delivery', $ctx);
                        // Check if billing and shipping addresses are same
                        var isShippingSameAsBilling = $sameBillingDelivery.is(':checked');
                        // Get data from billing form
                        var $serializedArrayFromForm = Tc.Utils.serializeArray($billingForm);
                        var $deliveryDetailsBlock = $('.js-rebuild-block-delivery-details');

                        // Set data from success form, so we can reuse it for resetting data on "Cancel" button click
                        thisMod.formResetData = $serializedArrayFromForm;
                        // Populate form information in block
                        thisMod.populateEditableFormInfos(response);
                        // Populate form data with data from response
                        thisMod.populateEditableForm(response, false);
                        // On success show info mode and hide content (show informations, hide form)
                        thisMod.CheckoutRebuildBlock.toggleEditableForm($clickedButton, true);

                        // If delivery details are on the page, toggle them
                        if (!shippingNotAvailable) {
                            // Logic for handling case where user is switching between checking and unchecking checkbox for same billing and shipping
                            // When delivery is selected and it is NOT default, if checkbox is checked then we are resetting delivery selection (like BE is doing)
                            if (isShippingSameAsBilling) {
                                if (!thisMod.sameBillingAndShippingSubmitted) {
                                    var $daItemSelectedRadio = $('.js-da-list .js-da-input:checked');

                                    thisMod.sameBillingAndShippingSubmitted = true;

                                    if ($daItemSelectedRadio.length) {
                                        $daItemSelectedRadio.prop('checked', false);
                                    }
                                }
                            } else {
                                thisMod.CheckoutRebuildDeliveryDetails.toggleBlockStateVisible(true);
                                thisMod.CheckoutRebuildDeliveryDetails.toggleBlockStateDisable(false);
                                thisMod.sameBillingAndShippingSubmitted = false;
                            }
                        }

                        $('.js-billing-address-selected', $ctx).val('true');

                        // Copying company name and country from billing into delivery form
                        if ($deliveryDetailsBlock.length) {
                            var companyName = $billingForm.find('input[name="companyName"]').val();
                            var countryIso = $billingForm.find('select[name="countryIso"]').val();
                            var $deliveryCompanyField = $deliveryDetailsBlock.find('.js-cr-editableform input[name="companyName"]');
                            var $deliveryCountryField = $deliveryDetailsBlock.find('.js-cr-editableform select[name="countryIso"]');

                            if ($deliveryCompanyField.length) {
                                $deliveryCompanyField.val(companyName);
                            }

                            if ($deliveryCountryField.length) {
                                $deliveryCountryField.val(countryIso);
                                $deliveryCountryField.data('selectBox-selectBoxIt').refresh();
                            }
                        }

                        setTimeout(function () {
                            window.CheckoutDeliveryPage.isBillingAndShippingAddress = $sameBillingDelivery.length === 0 ? true : isShippingSameAsBilling;
                            window.CheckoutDeliveryPage.canContinueCheck();
                        }, 300);
                    },
                    error: function (data) {
                        Tc.Utils.checkoutRebuildHandleErrorOnResponse(data, $billingForm);
                    },
                    complete: function () {
                        // Enable button again
                        $clickedButton.prop('disabled', false);
                        // Hide loading animation
                        thisMod.$ctx.removeClass('is-loading');
                        $delveryDetailsBlock.removeClass('is-loading');
                    }
                });
            });
        };


        // Function for binding "click" on button which closes info mode (informations and edit link) and shows content (form)
        this.bindEditableForm = function ($ctx) {
            var thisMod = this;
            var $editableFormEdit = $('.js-cr-editableform-edit', $ctx);
            var $editableFormCancel = $('.js-cr-editableform-cancel', $ctx);

            $editableFormEdit.on('click', function () {
                var $clickedButton = $(this);
                // Close info mode, and show content
                thisMod.CheckoutRebuildBlock.toggleEditableForm($clickedButton, false);
                // Hide all remove banners
                $('.js-da-list-item-remove').hide(300);

                setTimeout(function () {
                    window.CheckoutDeliveryPage.canContinueCheck();
                }, 300);
            });

            $editableFormCancel.on('click', function () {
                var $clickedButton = $(this);
                // Close form, and show info
                thisMod.CheckoutRebuildBlock.toggleEditableForm($clickedButton, true);
                // Reset the form with previously saved values
                thisMod.populateEditableForm(thisMod.formResetData, false);

                setTimeout(function () {
                    window.CheckoutDeliveryPage.canContinueCheck();
                }, 300);
            });
        };


        // Populate js template with data
        this.populateEditableFormInfos = function (data) {
            var $thisMod = this.$ctx;
            var tmplEditableForm = doT.template($('#tmpl-editable-form', $thisMod).html());
            $('.js-cr-editableform-info-list', $thisMod).html(tmplEditableForm(data));
        };


        // Populate form with data
        this.populateEditableForm = function (formData, isReset) {
            var $thisMod = this.$ctx;

            if (!$thisMod.length) {
                $thisMod = $thisMod.$ctx;
            }

            // Get delivery form element
            var $editableForm = $('.js-cr-editableform form', $thisMod);

            $.each(formData, function(key, value) {
                var itemName;
                var itemValue;

                // Since we have 2 types of data, we need to identify which is forwarder
                // 1st type is coming from BE {addressId: '123', title: 'Mr'...}
                // 2nd type is from serialized form data [{0: {name: 'addressId', value: 'true'}}, {1: {name: 'title', value: 'Mr'}}...]
                if (formData[0]) {
                    itemName = formData[key].name;
                    itemValue = formData[key].value;

                } else {
                    itemName = key;
                    itemValue = value;
                }

                if (itemName === 'id') {
                    itemName = 'addressId';
                }

                if (itemName === 'country') {
                    itemName = 'countryIso';
                    itemValue = itemValue.isocode;
                }

                // When fetching data from response, if mobile phone is filled use it, if not, use phone1
                if (itemName === 'cellphone' || itemName === 'phone1') {
                    itemValue = formData.cellphone ? formData.cellphone : formData.phone1;
                    itemName = 'contactPhone';
                }

                var $targetField = $('[name="' + itemName + '"]:input', $editableForm);

                // To be done in future: Once BE changes name of all phone fields to be same across all forms, remove this and adjust jsp and IF check above
                if (itemName === 'contactPhone' && !$targetField.length) {
                    itemName = 'phoneNumber';
                    $targetField = $('[name="' + itemName + '"]:input', $editableForm);
                }

                if (isReset) {
                    itemValue = '';
                }

                if ($targetField.length) {
                    if ($targetField.is(':checkbox')) {
                        // Since flag can be bool or string, we need to double check this
                        var isChecked = itemValue === 'true' || itemValue === true;
                        $targetField.prop('checked', isChecked);
                    } else {
                        // Inject value into field
                        $targetField.val(itemValue);

                        // If element is select item
                        if ($targetField.is('select')) {
                            if (isReset) {
                                // We need to manually refresh selectboxit since otherwise it will show validation messages
                                $targetField.data('selectBox-selectBoxIt').refresh();
                            } else {
                                // Trigger change so we can see new value (Selectboxit plugin will be refreshed on trigger)
                                $targetField.trigger('change');
                            }
                        }
                    }
                }

                // On reset we need to remove validation from elements which are empty now
                if (isReset) {
                    Tc.Utils.removeInlineValidationStylesFromElement($targetField);
                }
            });

            setTimeout(function () {
                // Get last field in form
                var $lastField = $('.js-iv-field:input', $editableForm).last();

                if (!isReset) {
                    // Trigger inline validation on last element
                    $lastField.last().trigger('change');
                } else {
                    // Enable/disable submit button
                    Tc.Utils.handleFormSubmitState($lastField);
                }
            }, 300);
        };


        this.bindSameBillingShippingCheckbox = function ($ctx) {
            var thisMod = this;
            var $sameBillingShippingCheckbox = $('.js-same-billing-and-delivery', $ctx);
            var $delveryDetailsBlock = $('.mod.js-rebuild-block-delivery-details');

            $sameBillingShippingCheckbox.on('change', function () {
                var $changedCheckbox = $(this);
                var isChecked = $changedCheckbox.is(':checked');

                // Close all error msgs
                Tc.Utils.globalMessagesTriggerClose();

                // Prevent multiple clicks
                $changedCheckbox.prop('disabled', true);

                // Show loading animation
                thisMod.$ctx.addClass('is-loading');
                $delveryDetailsBlock.addClass('is-loading');

                $.ajax({
                    url: '/checkout/delivery/select/billingAsShipping',
                    type: 'POST',
                    data: {
                        addressId: $('.js-billing-form input[name="addressId"]', $ctx).val(),
                        billingAndShippingAddress: isChecked
                    },
                    success: function() {
                        var $daListItems = $('.js-da-list-item');
                        // Change title and visibility of delivery details section based on checked state
                        thisMod.changeTitle(isChecked);
                        thisMod.CheckoutRebuildDeliveryDetails.toggleBlockStateVisible(!isChecked);
                        thisMod.CheckoutRebuildDeliveryDetails.toggleBlockStateDisable($('.js-billing-address-selected', $ctx).val() === 'false');

                        if (isChecked) {
                            $('.js-da-input:checked', $daListItems).prop('checked', false);
                        } else {
                            setTimeout(function () {
                                if ($daListItems.length === 1) {
                                    $('.js-da-input', $daListItems).prop('checked', true).trigger('change');
                                } else {
                                    if ($daListItems.length > 1) {
                                        var $defaultAddressItem = $('.js-da-list-item.is-default-address');

                                        if ($defaultAddressItem.length) {
                                            $('.js-da-input', $defaultAddressItem).prop('checked', true).trigger('change');
                                        }
                                    }
                                }
                            }, 1);
                        }

                        setTimeout(function () {
                            window.CheckoutDeliveryPage.isBillingAndShippingAddress = isChecked;
                            window.CheckoutDeliveryPage.canContinueCheck();
                        }, 300);
                    },
                    error: function (data) {
                        Tc.Utils.checkoutRebuildHandleErrorOnResponse(data);
                    },
                    complete: function () {
                        // Enable button again
                        $changedCheckbox.prop('disabled', false);
                        // Hide loading animation
                        thisMod.$ctx.removeClass('is-loading');
                        $delveryDetailsBlock.removeClass('is-loading');
                    }
                });
            });
        };


        this.bindBillingAddressSetDefault = function ($scope) {
            var thisMod = this;
            var $baList = $('.js-ba-list', $scope);

            if ($baList.length) {
                var $baListItem = $('.js-ba-list-item', $baList);
                var $baSetDefaultBtn = $('.js-ba-set-default', $baList);

                $baSetDefaultBtn.unbind('click').on('click', function () {
                    var $clickedButton = $(this);
                    var $scopeAddressItem = $clickedButton.closest($baListItem);
                    var $input = $scopeAddressItem.find('.js-ba-input');
                    var shippingId = $input.val();

                    // Show loading animation
                    thisMod.$ctx.addClass('is-loading');
                    // Prevent multiple clicks
                    $clickedButton.prop('disabled', true);
                    // Remove class for hiding "Set as default" button on all addresses
                    $baListItem.removeClass('is-default-address');

                    $.ajax({
                        url: '/checkout/delivery/billing/set-default-address',
                        type: 'PUT',
                        data: shippingId,
                        success: function(response) {
                            // Add class on current address so we can hide "Set as default" button
                            $scopeAddressItem.addClass('is-default-address');
                        },
                        error: function (data) {
                            Tc.Utils.checkoutRebuildHandleErrorOnResponse(data);
                        },
                        complete: function () {
                            // Enable button again
                            $clickedButton.prop('disabled', false);
                            // Hide loading animation
                            thisMod.$ctx.removeClass('is-loading');
                        }
                    });
                });
            }
        };
    };
})(Tc.$);
