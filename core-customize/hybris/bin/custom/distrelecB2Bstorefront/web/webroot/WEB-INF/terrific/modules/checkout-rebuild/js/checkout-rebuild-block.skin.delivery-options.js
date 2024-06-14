(function($) {

    /**
     * This module implements the delivery options on checkout page
     *
     * @namespace Tc.Module
     * @class CheckoutRebuildBlock
     * @skin DeliveryOptions
     */
    Tc.Module.CheckoutRebuildBlock.DeliveryOptions = function (parent) {

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {
            // If we want to call "CheckoutRebuildBlock" on load
            // parent.on(callback);

            var self = this,
                $ctx = self.$ctx;

            // Using this flag we will distinct if we should send request for reset or not
            this.userSelectedDate = false;

            this.bindDeliveryOptions($ctx);
            this.initScheduleDatepicker($ctx);
            // Schedule component events
            this.bindSchedule($ctx);
            // Bind check/uncheck of checkbox for combining all items into one and executing ajax
            this.bindCombineAllItems($ctx);

            // This event is triggered upon successfull totals recalculation request
            $ctx.on('enableOptions', function () {
                // Enable radio selection
                $('.js-cr-delivery-option:not(.js-is-calendar)', $ctx).prop('disabled', false);
            });

            callback();
        };


        this.bindDeliveryOptions = function ($ctx) {
            var $deliveryRadios = $('.js-cr-delivery-option', $ctx);
            var CheckoutRebuild = new Tc.Module.CheckoutRebuild();
            var CheckoutRebuildOrderSummary = new Tc.Module.CheckoutRebuildBlock.OrderSummary();
            var CheckoutRebuildDeliveryDetails = new Tc.Module.CheckoutRebuildBlock.DeliveryDetails();
            var CheckoutRebuildBillingDetails = new Tc.Module.CheckoutRebuildBlock.BillingDetails();
            var $sameBillingAndDelivery = $('.js-same-billing-and-delivery');
            var $rebuildBlockPickupLocation = $('.js-rebuild-block-pickup-location');

            if ($deliveryRadios.length) {
                var $scheduleDeliveryRadio = $('.js-cr-delivery-option.js-is-calendar');

                $deliveryRadios.on('change', function () {
                    var $self = $(this);
                    var selfVal = $self.val();
                    var isValidSchedule = $self.data('valid-schedule');
                    // Close all error msgs
                    Tc.Utils.globalMessagesTriggerClose();

                    // Execute ajax for all options except for schedule, since we need to pick the date first
                    if ($self.hasClass('js-is-calendar')) {
                        // If user checks schedule checkbox, force to show calendar (since before it was hidden with JS)
                        if ($self.is(':checked')) {
                            $self.parent().find('.js-cr-schedule-datepicker-section').show(300);
                        }
                    } else {
                        // Disable radio selection, to prevent multiple selects at once
                        $deliveryRadios.not($scheduleDeliveryRadio).prop('disabled', true);

                        CheckoutRebuild.setSelectedDeliveryOption(selfVal);

                        var dataForBE = {
                            "deliveryModeCode": selfVal,
                            "warehouseCode": $self.data('warehouse-code')
                        };

                        // Execute totals recalculation
                        CheckoutRebuildOrderSummary.totalsRecalculation(dataForBE);
                        // If delivery option dont have schedule, disable schedule
                        $scheduleDeliveryRadio.prop('disabled', !isValidSchedule);

                        // If schedule is not valid for delivery option, reset schedule
                        if (!isValidSchedule) {
                            $('.js-cr-delivery-option.js-is-calendar', $ctx).prop('checked', false);
                            $('.js-cr-schedule-txt-select', $ctx).show();
                            $('.js-cr-schedule-txt-date', $ctx).hide();
                            $('.js-cr-schedule-datepicker-section', $ctx).hide();
                        }

                        // If pickup is selected
                        if (CheckoutRebuild.isPickupOption()) {
                            // Show pickup location informations
                            $rebuildBlockPickupLocation.show(300);
                            // Add class with which we will hide checkbox for marking delivery and billing addresses as same
                            CheckoutRebuildBillingDetails.isPickupSelected(true);
                            // Hide delivery details block
                            CheckoutRebuildDeliveryDetails.toggleBlockStateVisible(false);
                            // Change title to be only for billing
                            CheckoutRebuildBillingDetails.changeTitle(false);
                        } else {
                            // If not pickup:
                            // Hide pickup location informations
                            $rebuildBlockPickupLocation.hide(300);
                            // Remove class for hiding checkbox for same billing and delivery addresses
                            CheckoutRebuildBillingDetails.isPickupSelected(false);

                            if ($sameBillingAndDelivery.is(':checked') || $sameBillingAndDelivery.length === 0) {
                                // Hide delivery details block since billing and delivery addresses are same
                                CheckoutRebuildDeliveryDetails.toggleBlockStateVisible(false);
                                // Change title to be both for billing and delivery addresses
                                CheckoutRebuildBillingDetails.changeTitle(true);
                            } else {
                                // Change title to be only for billing
                                CheckoutRebuildBillingDetails.changeTitle(false);
                                // Show delivery details block
                                CheckoutRebuildDeliveryDetails.toggleBlockStateVisible(true);
                                // If Billing address is populated, enable it, otherwise disable it
                                CheckoutRebuildDeliveryDetails.toggleBlockStateDisable($('.js-billing-address-selected').val() === 'false');
                            }
                        }
                    }

                    setTimeout(function () {
                        // Update sticky position
                        Tc.Utils.triggerSticky($('.js-cr'));
                    }, 300);
                });
            }
        };


        this.initScheduleDatepicker = function ($ctx) {
            var thisMod = this;
            var $inlineScheduleDatepicker = $('.js-cr-schedule-datepicker', $ctx);

            if ($inlineScheduleDatepicker.length) {
                var $schedule = $('.js-cr-schedule', $ctx);
                var $selectedDateInput = $('.js-cr-schedule-date', $ctx);
                var $selectedDateLabel = $('.js-cr-schedule-txt-date-label', $ctx);

                var dateFormat = $inlineScheduleDatepicker.data('format').toLowerCase().replace('yyyy', 'yy');
                // Regular minimum date
                var minDate = $inlineScheduleDatepicker.data('date-min');
                // Max date which is available for selection
                var maxDate = $inlineScheduleDatepicker.data('date-max');
                // Get date which user selected
                var fromDate = $inlineScheduleDatepicker.data('delivery-date');
                // Get lang attribute
                var siteLanguage = $('.shopsettings').data('language');

                if (siteLanguage === 'en') {
                    siteLanguage = 'en-GB';
                }

                $inlineScheduleDatepicker.datepicker({
                    showOtherMonths: true,
                    selectOtherMonths: true,
                    beforeShowDay: $.datepicker.noWeekends,
                    onSelect: function(selectedDate) {
                        $selectedDateInput.val(selectedDate);
                        // Since user selected a date, change flat to true
                        thisMod.userSelectedDate = true;
                        // If there will be issue with fetching the date, add short timeout
                        $schedule.trigger('triggerSchedule');
                    }
                });

                $inlineScheduleDatepicker.datepicker('option', $.datepicker.regional[siteLanguage]);
                $inlineScheduleDatepicker.datepicker('option', 'dateFormat', dateFormat);
                $inlineScheduleDatepicker.datepicker('option', 'minDate', minDate);
                $inlineScheduleDatepicker.datepicker('option', 'maxDate', maxDate);

                if (fromDate) {
                    $inlineScheduleDatepicker.datepicker('setDate', fromDate);
                }

                $selectedDateLabel.html(thisMod.getDateFromInlineDatepicker());

                // Change day names format in datepicker (only 1 letter is visible)
                $('.js-cr-schedule-datepicker table.ui-datepicker-calendar thead th span').each(function () {
                    var $self = $(this);
                    // Get whole day name, get 1st character and add it to element
                    $self.html($self.text()[0]);
                });
            }
        };


        this.bindSchedule = function ($ctx) {
            var thisMod = this;
            var $schedule = $('.js-cr-schedule', $ctx);

            if ($schedule.length) {
                // Get main schedule element
                var $deliveryOptionSchedule = $('.js-cr-delivery-option.js-is-calendar', $ctx);
                // Get datepicker section
                var $scheduleDatepickerSection = $('.js-cr-schedule-datepicker-section', $schedule);
                // Get text element
                var $scheduleTxtSelect = $('.js-cr-schedule-txt-select', $schedule);
                // Get date text element
                var $scheduleTxtDate = $('.js-cr-schedule-txt-date', $schedule);
                // Get "Change date" link
                var $scheduleChangeButton = $('.js-cr-schedule-change', $schedule);
                // Get element which unchecks checkbox (turns off schedule option)
                var $scheduleUncheck = $('.js-cr-schedule-uncheck', $schedule);
                // Get selected date
                var selectedScheduleDate = $('.js-cr-schedule-date', $ctx).val();

                // If on page load, date is selected, set flag to true so we can reset the date later
                if (selectedScheduleDate.length) {
                    thisMod.userSelectedDate = true;
                }

                $schedule.on('triggerSchedule', function () {
                    // Close all error msgs
                    Tc.Utils.globalMessagesTriggerClose();
                    thisMod.deliveryScheduleRequest($ctx, false);
                });

                // When user clicks on "Change date" link, toggle elements
                $scheduleChangeButton.on('click', function () {
                    // Close all error msgs
                    Tc.Utils.globalMessagesTriggerClose();
                    $scheduleTxtSelect.toggle();
                    $scheduleTxtDate.toggle();
                    $scheduleDatepickerSection.show(300);
                });

                // When user unchecks schedule option, send ajax with empty param and toggle elements
                $scheduleUncheck.on('click', function () {
                    // Close all error msgs
                    Tc.Utils.globalMessagesTriggerClose();
                    $scheduleDatepickerSection.hide(300).removeClass('is-hidden-on-page-load');
                    $deliveryOptionSchedule.prop('checked', false);
                    $scheduleTxtSelect.toggle(true);
                    $scheduleTxtDate.toggle(false);

                    // If user selected date, reset it
                    if (thisMod.userSelectedDate) {
                        thisMod.deliveryScheduleRequest($ctx, true);
                        // Set flag to false until user selects date again
                        thisMod.userSelectedDate = false;
                    }
                });
            }
        };

        // Ajax for sending selected date to BE
        this.deliveryScheduleRequest = function ($ctx, isReset) {
            var thisMod = this;
            var $thisMod = thisMod.$ctx;
            // Get main schedule element
            var $schedule = $('.js-cr-schedule', $ctx);
            // Get datepicker element
            var $inlineScheduleDatepicker = $('.js-cr-schedule-datepicker', $ctx);
            // Get element which contains selected label txt
            var $selectedDateLabel = $('.js-cr-schedule-txt-date-label', $ctx);
            // Get error msg
            var $scheduleErrorMsg = $('.js-cr-schedule-error-msg', $ctx);
            // Get selected date
            var scheduleDate = $('.js-cr-schedule-date', $ctx).val();

            // Hide all remove banners
            $('.js-da-list-item-remove').hide(300);
            // Hide error msg, in case it is already shown
            $scheduleErrorMsg.hide();
            $thisMod.addClass('is-loading');

            // Send selected date to BE
            $.ajax({
                url: '/checkout/delivery/schedule',
                type: 'PUT',
                data: isReset ? '' : scheduleDate,
                success: function(response) {
                    // Get datepicker section
                    var $scheduleDatepickerSection = $('.js-cr-schedule-datepicker-section', $schedule);
                    // Get text element
                    var $scheduleTxtSelect = $('.js-cr-schedule-txt-select', $schedule);
                    // Get date text element
                    var $scheduleTxtDate = $('.js-cr-schedule-txt-date', $schedule);

                    $schedule.removeClass('is-error');

                    // If users selects disabled date (bypassing FE validation), set date given from BE
                    if (response !== scheduleDate) {
                        // Set new date from BE
                        $inlineScheduleDatepicker.datepicker('setDate', response);
                    }

                    // Update date text
                    $selectedDateLabel.html(thisMod.getDateFromInlineDatepicker());
                    Tc.Utils.ajaxUpdateOrderSummaryAvailabilityMessages();
                    $scheduleDatepickerSection.hide(300);

                    setTimeout(function () {
                        if (isReset) {
                            $scheduleTxtSelect.show();
                            $scheduleTxtDate.hide();
                        } else {
                            $scheduleTxtSelect.toggle();
                            $scheduleTxtDate.toggle();
                        }
                    }, 200);
                },
                error: function (jqXHR) {
                    $schedule.addClass('is-error');
                    Tc.Utils.checkoutRebuildHandleErrorOnResponse(jqXHR);
                },
                complete: function () {
                    $thisMod.removeClass('is-loading');
                }
            });
        };

        this.getDateFromInlineDatepicker = function () {
            var $selectedDay = $('.js-cr-schedule-datepicker .ui-datepicker-current-day');
            var dayNumber = $selectedDay.text();
            var dayName = $('.js-cr-schedule-datepicker .ui-datepicker-calendar thead th').eq($selectedDay.index()).find('span').attr('title');
            var monthName = $('.js-cr-schedule-datepicker .ui-datepicker-month').text();
            var year = $('.js-cr-schedule-datepicker .ui-datepicker-year').text();

            return dayName + ' ' + dayNumber + ', ' + monthName + ' ' + year;
        };

        this.bindCombineAllItems = function ($ctx) {
            var $thisMod = this.$ctx;
            var $combineCheckbox = $('.js-combine-all-items', $ctx);

            $combineCheckbox.on('change', function () {
                var $self = $(this);
                var isChecked = $self.is(':checked');

                $thisMod.addClass('is-loading');
                $self.prop('disabled', true);
                // Close all error msgs
                Tc.Utils.globalMessagesTriggerClose();

                // Send state to BE
                $.ajax({
                    url: '/checkout/delivery/complete',
                    type: 'PUT',
                    data: isChecked ? 'true' : 'false',
                    complete: function () {
                        $self.prop('disabled', false);
                        Tc.Utils.ajaxUpdateOrderSummaryAvailabilityMessages();
                        $thisMod.removeClass('is-loading');
                    },
                    error: function (data) {
                        Tc.Utils.checkoutRebuildHandleErrorOnResponse(data);
                    }
                });
            });
        };
    };

})(Tc.$);
