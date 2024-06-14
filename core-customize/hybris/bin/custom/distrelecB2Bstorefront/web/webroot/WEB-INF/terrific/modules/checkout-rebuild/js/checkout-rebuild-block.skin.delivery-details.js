(function($) {

    /**
     * This module implements the delivery options on checkout page
     *
     * @namespace Tc.Module
     * @class CheckoutRebuildBlock
     * @skin DeliveryDetails
     */
    Tc.Module.CheckoutRebuildBlock.DeliveryDetails = function (parent) {

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
            // Used when user clicks on "Edit" button, we are saving current form data
            this.editDeliveryAddressFormResetData = {};
            // Used when user clicked on "Edit" button while other form is still opened, one form can be opened at once
            this.editDeliveryAddressFormResetDataAlt = false;
            this.CheckoutRebuildBlock = new Tc.Module.CheckoutRebuildBlock();
            // Get count from BE of how many addresses we have
            this.deliveryAddressCounter = $('.js-delivery-address-count', $ctx).val();

            // Get select element on which we will init selectboxit plugin
            var $selectboxit = $('.js-selectboxit', $ctx);
            // Get select element which changes isocode
            var $libphoneNumberIsocodeSelect = $('.js-libphonenumber-isocode-select', $ctx);

            // Bind events for triggering inline validation
            Tc.Utils.bindInlineValidation($ctx);
            // Initialize "SelectBoxit" plugin
            Tc.Utils.initSelectboxit($selectboxit);
            // When user clicks on the submit button, trigger ajax
            self.bindDeliveryForm($ctx);
            // Bind set as default and remove actions
            self.bindDeliveryAddressSetDefaultRemoveEdit($ctx);
            // Bind editing for new address and bind cancel for both new address and existing
            self.bindEditableForm($ctx);

            self.invalidDeliveryAddressCheck($ctx);

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

            // When new delivery address is added or removed, we are triggering this event
            $ctx.on('deliveryAddressUpdate', function () {
                // Get number of delivery address items
                var addressCounter = self.deliveryAddressCounter;

                // Short timeout because of element creation/removal in DOM
                setTimeout(function () {
                    // If this is the only address, select it if it is not already selected
                    if (addressCounter === 1) {
                        // Get address item
                        var $lastAddressItem = $('.js-multiple-address-radio', $ctx);

                        // If address is not selected, select it
                        if (!$lastAddressItem.is(':checked')) {
                            $lastAddressItem.prop('checked', true).trigger('change');
                        }
                    }
                }, 50);
            });

            // As user types (last populating field) if he enters success data, we are enabling SAVE button immediately
            Tc.Utils.lastPopulatedFieldCheck($ctx);

            callback();
        };


        // Function for binding "click" on button which closes info mode (informations and edit link) and shows content (form)
        this.bindEditableForm = function ($ctx) {
            var thisMod = this;
            var $editableFormEdit = $('.js-cr-editableform-edit', $ctx);
            var $editableFormCancel = $('.js-cr-editableform-cancel', $ctx);

            // In delivery used only for add new address link, for editing exisiting one we use "js-da-edit" class
            $editableFormEdit.unbind('click').on('click', function () {
                var $clickedButton = $(this);
                // Hide all remove banners
                $('.js-da-list-item-remove').hide(300);
                // Close info mode, and show content
                thisMod.toggleDeliveryEditableForm($clickedButton, false);

                setTimeout(function () {
                    window.CheckoutDeliveryPage.canContinueCheck();
                }, 300);
            });

            // Used both for new address and editing existing one
            $editableFormCancel.unbind('click').on('click', function () {
                var $clickedButton = $(this);
                // Close form, and show info
                thisMod.toggleDeliveryEditableForm($clickedButton, true);

                // When user cancelled form which he wanted to edit, we need to revert values
                if ($clickedButton.closest('.js-da-list-item-form').length) {
                    if (thisMod.editDeliveryAddressFormResetDataAlt) {
                        thisMod.populateEditableForm($clickedButton.closest('form'), thisMod.editDeliveryAddressFormResetDataAlt);
                    } else {
                        thisMod.populateEditableForm($clickedButton.closest('form'), thisMod.editDeliveryAddressFormResetData);
                    }

                    thisMod.editDeliveryAddressFormResetDataAlt = false;
                }

                setTimeout(function () {
                    window.CheckoutDeliveryPage.canContinueCheck();
                }, 300);
            });
        };


        // Bind click on submit button and executes ajax request
        this.bindDeliveryForm = function ($ctx) {
            var thisMod = this;
            // Get delivery form element
            var $deliveryForm = $('.js-delivery-form', $ctx);
            // Get submit button element
            var $deliveryFormSubmitButton = $('.js-delivery-form-submit', $deliveryForm);

            $deliveryFormSubmitButton.unbind('click').on('click', function () {
                var $clickedButton = $(this);
                // Get form which has been submitted
                var $scopeDeliveryForm = $clickedButton.closest($deliveryForm);
                // Serialize data for the BE
                var $serializedDataForBE = $scopeDeliveryForm.serialize();
                // Get customer type from BE
                var customerType = $scopeDeliveryForm.data('customer-type');

                // Close all error msgs
                Tc.Utils.globalMessagesTriggerClose();

                // Hide all remove banners
                $('.js-da-list-item-remove').hide(300);

                // Prevent multiple clicks
                $clickedButton.prop('disabled', true);

                // Show loading animation
                thisMod.$ctx.addClass('is-loading');

                // For b2b key account, we will use b2b
                if (customerType === 'B2B_KEY_ACCOUNT') {
                    customerType = 'B2B';
                }

                // Format Swedish, Czech and Slovak postal code before form submit (xxx xx)
                if (digitalData.page.pageInfo.countryCode === 'SE' || digitalData.page.pageInfo.countryCode === 'CZ' || digitalData.page.pageInfo.countryCode === 'SK') {
                    var $postalCode = $('.js-postal-code', $scopeDeliveryForm);
                    var postalCode = $postalCode.val();

                    if (postalCode.length === 5) {
                        $postalCode.val(postalCode.substring(0, 3) + " " + postalCode.substring(3));
                    }
                }

                $.ajax({
                    url: '/checkout/delivery/shipping/' + customerType.toLowerCase(),
                    type: 'POST',
                    data: $serializedDataForBE,
                    success: function(response) {
                        // If parent element is delivery address list, then this is EDIT form
                        var $scopeDeliveryAddressListItemForm = $clickedButton.closest('.js-da-list-item-form');

                        // If user submits EDIT form
                        // Else user submitted form for creating NEW address
                        if ($scopeDeliveryAddressListItemForm.length) {
                            // Get id of address
                            var id = $scopeDeliveryAddressListItemForm.data('shipping-id');
                            // Find item which holds address item form
                            var $scopeAddressListItem = $('.js-da-list-item[data-shipping-id="' + id + '"]');
                            // Update address informations in radio
                            thisMod.updateDeliveryAddressItemRadioInfo(response, $scopeAddressListItem);
                            // Since we successfully submitted form, enable "Edit" button (for cases wher we are hiding it due to unvalid address)
                            $clickedButton.closest('.hide-edit').removeClass('hide-edit');
                        } else {
                            // Increase counter by 1
                            thisMod.deliveryAddressCounter++;
                            // Trigger event for address counter
                            thisMod.$ctx.trigger('deliveryAddressUpdate');

                            // Create new element in DOM
                            thisMod.createDeliveryAddressItem(response);

                            // Setting short timeout just to be sure that all prev operations are being done (updating DOM with new element)
                            setTimeout(function () {
                                var $newlyCreatedDeliveryListItem = $('.js-da-list-item-form[data-shipping-id="' + response.id + '"]');
                                // Bind selecting of delivery address
                                new Tc.Module.CheckoutRebuildBlock(thisMod.$ctx).bindMultipleAddress($ctx);
                                // Bind forms submit
                                thisMod.bindDeliveryForm($ctx);
                                // Bind set as default and remove actions
                                thisMod.bindDeliveryAddressSetDefaultRemoveEdit($ctx);
                                // Bind editing for new address and bind cancel for both new address and existing
                                thisMod.bindEditableForm($ctx);
                                // Populate form which was created with templates with data from response
                                thisMod.populateEditableForm($('form', $newlyCreatedDeliveryListItem), response);
                                // Remove all values from form for adding new address
                                thisMod.populateEditableForm($scopeDeliveryForm, response, true);
                                // Bind events for triggering inline validation
                                Tc.Utils.bindInlineValidation($newlyCreatedDeliveryListItem);
                                // Select this new address (in BE it is already set on creation)
                                $('.js-da-list-item[data-shipping-id="' + response.id + '"] .js-da-input').prop('checked', true);
                                // As user types (last populating field) if he enters success data, we are enabling SAVE button immediately
                                Tc.Utils.lastPopulatedFieldCheck($newlyCreatedDeliveryListItem);
                            }, 100);
                        }

                        // On success show info mode and hide content (show informations, hide form)
                        thisMod.toggleDeliveryEditableForm($clickedButton, true);
                        setTimeout(function () {
                            window.CheckoutDeliveryPage.canContinueCheck();
                        }, 300);
                    },
                    error: function (data) {
                        Tc.Utils.checkoutRebuildHandleErrorOnResponse(data, $scopeDeliveryForm);

                        setTimeout(function () {
                            window.CheckoutDeliveryPage.canContinueCheck();
                        }, 300);
                    },
                    complete: function () {
                        // Enable button again
                        $clickedButton.prop('disabled', false);
                        // Hide loading animation
                        thisMod.$ctx.removeClass('is-loading');
                    }
                });
            });
        };


        // Toggle disabled/enabled state for each block
        this.toggleBlockStateDisable = function (disabled) {
            var $thisMod = this.$ctx;
            new Tc.Module.CheckoutRebuildBlock($thisMod).toggleBlockStateDisable(disabled);
        };


        // Toggle disabled/enabled state for each block
        this.toggleBlockStateVisible = function (visible) {
            var $thisMod = this.$ctx;
            new Tc.Module.CheckoutRebuildBlock($thisMod).toggleBlockStateVisible(visible);
        };


        this.createDeliveryAddressItem = function (data) {
            var thisMod = this;
            var $thisMod = thisMod.$ctx;
            var $deliveryAddressList = $('.js-da-list', $thisMod);

            var tmplDeliveryDetailsAddressItem = doT.template($('#tmpl-delivery-details-address-item', $thisMod).html());
            $deliveryAddressList.append(tmplDeliveryDetailsAddressItem(data));
        };


        this.updateDeliveryAddressItemRadioInfo = function (data, $scope) {
            var thisMod = this;
            var $thisMod = thisMod.$ctx;

            var tmplDeliveryDetailsAddressItem = doT.template($('#tmpl-delivery-details-address-item-radio-info', $thisMod).html());
            $('.js-da-list-item-info', $scope).html(tmplDeliveryDetailsAddressItem(data));
        };


        this.bindDeliveryAddressSetDefaultRemoveEdit = function ($scope) {
            var thisMod = this;
            var $daList = $('.js-da-list', $scope);

            if ($daList.length) {
                // Get customer type from BE
                var customerType = $daList.data('customer-type');
                var $daListItem = $('.js-da-list-item', $daList);
                var $daSetDefaultBtn = $('.js-da-set-default', $daList);
                var $daRemoveBtn = $('.js-da-remove', $daList);
                var $daEditBtn = $('.js-da-edit', $daList);

                $daSetDefaultBtn.unbind('click').on('click', function () {
                    var $clickedButton = $(this);
                    var $scopeAddressItem = $clickedButton.closest($daListItem);
                    var $input = $scopeAddressItem.find('.js-da-input');
                    var shippingId = $input.val();

                    // Show loading animation
                    thisMod.$ctx.addClass('is-loading');
                    // Prevent multiple clicks
                    $clickedButton.prop('disabled', true);
                    // Remove class for hiding "Set as default" button on all addresses
                    $daListItem.removeClass('is-default-address');

                    // Hide all remove banners
                    $('.js-da-list-item-remove').hide(300);

                    $.ajax({
                        url: '/checkout/delivery/shipping/set-default-address',
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

                $daRemoveBtn.unbind('click').on('click', function () {
                    var $clickedButton = $(this);

                    // Get address list
                    var $scopeAddressList = $clickedButton.closest($daList);
                    // Get address list item
                    var $scopeAddressItem = $clickedButton.closest($daListItem);
                    // Get input from which we will extract ID
                    var $input = $scopeAddressItem.find('.js-da-input');
                    // Get shipping ID
                    var shippingId = $input.val();
                    // Find banner which contains removal button
                    var $removeItem = $('.js-da-list-item-remove', $scopeAddressItem);

                    // Hide all remove banners, only one can be visible at once
                    $('.js-da-list-item-remove').not($removeItem).hide(300);

                    // If banner is already created in DOM, just show it
                    // Else create new banner with templates and inject it into DOM
                    if ($removeItem.length) {
                        $removeItem.show(300);
                    } else {
                        // get template element
                        var tmplDeliveryDetailsAddressItemRemove = doT.template($('#tmpl-delivery-details-address-item-remove', $scope).html());
                        // Append data to template
                        $scopeAddressItem.append(tmplDeliveryDetailsAddressItemRemove({
                            id: shippingId
                        }));

                        // Short timeout just to make sure that DOM has been updated
                        setTimeout(function () {
                            // Find newly created element in DOM
                            $removeItem = $('.js-da-list-item-remove[data-shipping-id="' + shippingId + '"]');
                            // Get confirm button for removal
                            var $removeItemConfirm = $('.js-da-list-item-remove-confirm', $removeItem);
                            // Get cancel button
                            var $removeItemCancel = $('.js-da-list-item-remove-cancel', $removeItem);

                            // Show element for removing address
                            $removeItem.show(300);

                            // When user clicks on confirm button, remove address
                            $removeItemConfirm.on('click', function () {
                                // Show loading animation
                                thisMod.$ctx.addClass('is-loading');
                                // Prevent multiple clicks
                                $clickedButton.prop('disabled', true);

                                $.ajax({
                                    url: '/checkout/delivery/shipping',
                                    type: 'DELETE',
                                    data: shippingId,
                                    success: function(response) {
                                        // Animate removal of item
                                        $scopeAddressItem.hide(300);

                                        setTimeout(function() {
                                            // After animation, remove item from DOM
                                            $scopeAddressItem.remove();
                                            // Decrease counter by 1
                                            thisMod.deliveryAddressCounter--;
                                            // Trigger event for address counter
                                            thisMod.$ctx.trigger('deliveryAddressUpdate');
                                            // Enable/disable continue button
                                            window.CheckoutDeliveryPage.canContinueCheck();

                                            // If removed address was selected, select first one in list
                                            if ($input.is(':checked')) {
                                                $scopeAddressList.find($daListItem).first().find('.js-multiple-address-radio').prop('checked', true).trigger('change');
                                            }

                                            // Update sticky position
                                            Tc.Utils.triggerSticky($('.js-cr'));
                                        }, 300);

                                        // Remove related item with form from DOM
                                        $scopeAddressItem.siblings('.js-da-list-item-form[data-shipping-id="' + shippingId + '"]').remove();
                                    },
                                    error: function (data) {
                                        Tc.Utils.checkoutRebuildHandleErrorOnResponse(data);
                                        // Enable/disable continue button
                                        window.CheckoutDeliveryPage.canContinueCheck();
                                    },
                                    complete: function () {
                                        // Enable button again
                                        $clickedButton.prop('disabled', false);
                                        // Hide loading animation
                                        thisMod.$ctx.removeClass('is-loading');
                                    }
                                });
                            });

                            // When user clicks on cancel button, hide remove banner
                            $removeItemCancel.on('click', function () {
                                $removeItem.hide(300);

                                setTimeout(function () {
                                    // Update sticky position
                                    Tc.Utils.triggerSticky($('.js-cr'));
                                }, 300);
                            });
                        }, 100);
                    }
                });

                $daEditBtn.unbind('click').on('click', function () {
                    var $clickedButton = $(this);
                    // Find item which holds address
                    var $scopeAddressListItem = $clickedButton.closest('.js-da-list-item');
                    // Get id of address
                    var id = $scopeAddressListItem.data('shipping-id');
                    // Find item which holds address item form
                    var $scopeAddressListItemForm = $('.js-da-list-item-form[data-shipping-id="' + id + '"]');
                    // Find other forms which are edited
                    var $otherEditedForms = $scopeAddressListItem.siblings('.js-da-list-item-form:visible').not($scopeAddressListItemForm);

                    // If other form is opened, save value into temp object, so we can reuse it on cancel
                    if ($otherEditedForms.length) {
                        thisMod.editDeliveryAddressFormResetDataAlt = thisMod.editDeliveryAddressFormResetData;
                        // Close all other forms (only 1 form can be edited at once) and trigger click on "Cancel" button, so data will be reset
                        $otherEditedForms.find('.js-cr-editableform-cancel').trigger('click');
                    } else {
                        thisMod.editDeliveryAddressFormResetDataAlt = false;
                    }

                    // Hide all remove banners
                    $('.js-da-list-item-remove').hide(300);
                    // Save edited form data in temp object, so we can refer to it later
                    thisMod.editDeliveryAddressFormResetData = Tc.Utils.serializeArray($scopeAddressListItemForm.find('form'));
                    $scopeAddressListItemForm.find('.js-iv-field').each(function () {
                        var $self = $(this);
                        // Show inline validation on elements
                        Tc.Utils.handleInlineValidationErrors($self);
                    });
                    // Show form, hide radio
                    thisMod.toggleDeliveryEditableForm($clickedButton, false);

                    setTimeout(function () {
                        window.CheckoutDeliveryPage.canContinueCheck();
                    }, 300);
                });
            }
        };


        // Populate form with data
        this.populateEditableForm = function ($scope, formData, isReset) {
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
                if (itemName === 'mobilePhone' || itemName === 'phone1') {
                    itemValue = formData.cellphone ? formData.cellphone : formData.phone1;
                    itemName = 'contactPhone';
                }

                var $targetField = $('[name="' + itemName + '"]:input', $scope);

                // To be done in future: Once BE changes name of all phone fields to be same across all forms, remove this and adjust jsp and IF check above
                if (itemName === 'contactPhone' && !$targetField.length) {
                    itemName = 'phoneNumber';
                    $targetField = $('[name="' + itemName + '"]:input', $scope);
                }

                if (isReset && itemName !== 'shippingAddress') {
                    if (itemName === 'countryIso') {
                        if ($targetField.is('select')) {
                            itemValue = '';
                        }
                    } else {
                        itemValue = '';
                    }
                }

                if ($targetField.length) {
                    if ($targetField.is(':checkbox')) {
                        // Since flag can be bool or string, we need to double check this
                        var isChecked = itemValue === 'true' || itemValue === true;
                        $targetField.prop('checked', isChecked);
                    } else {
                        $targetField.val(itemValue);

                        if ($targetField.is('select')) {
                            if (isReset) {
                                // We need to manually refresh selectboxit since otherwise it will show validation msgs
                                $targetField.data('selectBox-selectBoxIt').refresh();
                            } else {
                                $targetField.trigger('change');
                            }
                        }
                    }

                    // On reset we need to remove validation from elements which are empty now
                    if (isReset) {
                        Tc.Utils.removeInlineValidationStylesFromElement($targetField);
                    }
                }
            });

            setTimeout(function () {
                // Get last field in form
                var $lastField = $('.js-iv-field:input', $scope).last();

                if (!isReset) {
                    // Trigger inline validation on last element
                    $lastField.last().trigger('change');
                } else {
                    // Enable/disable submit button
                    Tc.Utils.handleFormSubmitState($lastField);
                }
            }, 300);
        };


        this.toggleDeliveryEditableForm = function ($button, hideForm) {
            var thisMod = this;
            var $addNewDeliveryAddressComponent = $button.closest('.js-cr-editableform');
            var $deliveryAddressList = $('.js-da-list', thisMod.$ctx);
            var $deliveryAddressListItems = $('.js-da-list-item', $deliveryAddressList);
            var $deliveryAddressListItemsForm = $('.js-da-list-item-form', $deliveryAddressList);

            if ($addNewDeliveryAddressComponent.length) {
                thisMod.CheckoutRebuildBlock.toggleEditableForm($button, hideForm);

                if (hideForm) {
                    $deliveryAddressList.show(300);
                } else {
                    $deliveryAddressList.hide(300);
                }
            } else {
                $addNewDeliveryAddressComponent = $button.closest('.js-rebuild-block-content').find('.js-cr-editableform');
                // Find item which holds address
                var $scopeAddressListItem = $button.closest('.js-da-list-item');
                // Get id of address
                var id = $scopeAddressListItem.data('shipping-id');
                // Find item which holds address item form
                var $scopeAddressListItemForm = $('.js-da-list-item-form[data-shipping-id="' + id + '"]');
                // Find other forms which are edited
                var $otherEditedForms = $scopeAddressListItem.siblings('.js-da-list-item-form:visible').not($scopeAddressListItemForm);

                if (hideForm) {
                    $deliveryAddressListItems.show(300);
                    $deliveryAddressListItemsForm.hide(300);
                    $addNewDeliveryAddressComponent.show(300);
                } else {
                    $deliveryAddressListItems.hide(300);
                    $otherEditedForms.hide(300);
                    $scopeAddressListItemForm.show(300);
                    $addNewDeliveryAddressComponent.hide(300);
                }
            }

            setTimeout(function () {
                // Update sticky position
                Tc.Utils.triggerSticky($('.js-cr'));
            }, 300);
        };

        this.invalidDeliveryAddressCheck = function ($ctx) {
            var $selectedAddress = $('.js-da-list-item.js-is-selected-address', $ctx);

            if ($selectedAddress.length) {
                Tc.Utils.handleUnvalidDeliveryAddressSelection($selectedAddress);
            }
        };
    };

})(Tc.$);
