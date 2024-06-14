(function ($) {
    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Checkout-rebuild-block
     * @extends Tc.Module
     */
    Tc.Module.CheckoutRebuildBlock = Tc.Module.extend({
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
        init: function ($ctx, sandbox, id) {
            // call base constructor
            this._super($ctx, sandbox, id);
            this.$ctx = $ctx;
        },


        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function (callback) {
            var $ctx = this.$ctx,
                self = this;

            callback();
        },


        /**
         * Hook function to trigger your events.
         *
         * @method after
         * @return void
         */
        after: function () {
            var $ctx = this.$ctx,
                self = this;

            this.bindMultipleAddress($ctx);
            this.bindOptionalFields($ctx);
        },


        toggleBlockStateDisable: function (disabled) {
            var $thisMod = this.$ctx;
            var $rebuildBlockContent = $('.js-rebuild-block-content', $thisMod);

            if (disabled) {
                $rebuildBlockContent.hide(300);
            } else {
                $rebuildBlockContent.show(300);
            }

            setTimeout(function () {
                if ($thisMod) {
                    $thisMod.toggleClass('is-disabled', disabled);
                }
            }, 300);
        },


        toggleBlockStateVisible: function (visible) {
            var self = this;

            if (self.$ctx) {
                self.$ctx.toggle(visible);
            }
        },


        // Function which toggles visibility of content and information modes
        toggleEditableForm: function ($button, show) {
            var $editableForm = $button.closest('.js-cr-editableform');

            // If editable form is parent of button, toggle visibility of it (Billing address - form and info modes)
            // Else delivery address item is parent (radio list items with form in sibling item)
            if ($editableForm.length) {
                var $editableFormForm = $('.js-cr-editableform-form', $editableForm);
                var $editableFormInfo = $('.js-cr-editableform-info', $editableForm);

                if (show) {
                    // Toogle content mode (form)
                    $editableFormForm.hide(300);
                    // Toogle information mode (informations and edit link)
                    $editableFormInfo.show(300);
                } else {
                    // Toogle content mode (form)
                    $editableFormForm.show(300);
                    // Toogle information mode (informations and edit link)
                    $editableFormInfo.hide(300);
                }

                setTimeout(function () {
                    // Add class which indicates that form have visible "Cancel" button
                    $editableForm.addClass('is-cancellable');
                    $editableForm.toggleClass('is-info-mode', show);
                }, 300);
            } else {
                var id = '';
                var $deliveryAddressItemForm = $button.closest('.js-da-list-item-form');
                var $deliveryAddressItem = $button.closest('.js-da-list-item');

                if ($deliveryAddressItemForm.length) {
                    id = $deliveryAddressItemForm.data('shipping-id');
                }

                if ($deliveryAddressItem.length) {
                    id = $deliveryAddressItem.data('shipping-id');
                }

                var $toggledListItemForm = $('.js-da-list-item-form[data-shipping-id="' + id + '"]');
                var $toggledListItem = $('.js-da-list-item[data-shipping-id="' + id + '"]');

                if (show) {
                    // Toogle information mode (informations and edit link)
                    $toggledListItemForm.hide(300);
                    // Toogle content mode (form)
                    $toggledListItem.show(300);
                } else {
                    // Toogle information mode (informations and edit link)
                    $toggledListItemForm.show(300);
                    // Toogle content mode (form)
                    $toggledListItem.hide(300);
                }
            }

            setTimeout(function () {
                // Update sticky position
                Tc.Utils.triggerSticky($('.js-cr'));
            }, 300);
        },


        // Bind click on submit button and executes ajax request
        bindMultipleAddress: function ($ctx) {
            var thisMod = this;
            // Get billing form element
            var $multipleAddressRadio = $('.js-multiple-address-radio', $ctx);

            $multipleAddressRadio.unbind('change').on('change', function () {
                var $clickedRadio = $(this);
                var addressType = $clickedRadio.data('address-type');

                // Close all error msgs
                Tc.Utils.globalMessagesTriggerClose();

                // Prevent multiple clicks
                $multipleAddressRadio.prop('disabled', true);

                // Show loading animation
                thisMod.$ctx.addClass('is-loading');

                // Hide all remove banners
                $('.js-da-list-item-remove').hide(300);

                $.ajax({
                    url: '/checkout/delivery/select/' + addressType,
                    type: 'PUT',
                    data: $clickedRadio.val(),
                    success: function(response) {

                        if (addressType === 'shipping') {
                            if (!$clickedRadio.data('address-is-valid')) {
                                Tc.Utils.handleUnvalidDeliveryAddressSelection($clickedRadio.closest('.js-da-list-item'));
                            }
                        }

                        setTimeout(function () {
                            window.CheckoutDeliveryPage.canContinueCheck();
                        }, 200);
                    },
                    error: function (data) {
                        Tc.Utils.checkoutRebuildHandleErrorOnResponse(data);

                        setTimeout(function () {
                            window.CheckoutDeliveryPage.canContinueCheck();
                        }, 200);
                    },
                    complete: function () {
                        // Prevent multiple clicks
                        $multipleAddressRadio.prop('disabled', false);
                        // Hide loading animation
                        thisMod.$ctx.removeClass('is-loading');
                    }
                });
            });
        },

        bindOptionalFields: function ($ctx) {
            var $optionalField = $('.js-optional-field', $ctx);

            function checkAndApplyStyles ($field) {
                var toggleClassState = $field.val().replace(/\s/g, '').length > 0;

                $field.toggleClass('is-populated', toggleClassState);

                if ($field.is('select')) {
                    $field.siblings('.selectboxit-container').find('.js-optional-field').toggleClass('is-populated', toggleClassState);
                }
            }

            $optionalField.each(function () {
                var $currentField = $(this);
                checkAndApplyStyles($currentField);
            });

            $optionalField.on('change', function () {
                var $currentField = $(this);
                checkAndApplyStyles($currentField);
            });
        }
    });

})(Tc.$);

Tc.Utils.handleUnvalidDeliveryAddressSelection = function ($scope) {
    var $field = $('.js-da-input', $scope);

    if (!$field.data('address-is-valid')) {
        var shippingId = $scope.data('shipping-id');
        var $relatedForm = $('.js-da-list-item-form[data-shipping-id="' + shippingId + '"]');
        // Last inline validation element in form
        var $lastIVfield = $('.js-iv-field', $relatedForm).last();

        $relatedForm.addClass('hide-edit');
        $('.js-da-edit', $scope).trigger('click');
        $lastIVfield.trigger('change');
    }
};
