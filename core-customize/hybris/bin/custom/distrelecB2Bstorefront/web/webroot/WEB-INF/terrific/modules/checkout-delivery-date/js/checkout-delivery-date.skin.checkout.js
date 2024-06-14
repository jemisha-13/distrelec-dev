(function($) {

	/**
	 * Checkout Skin implementation for the module Checkout-delivery-date.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.CheckoutDeliveryDate.Checkout = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			var self = this,
				$ctx = self.$ctx
			;

			self.$datePicker = $('.js-date-picker', $ctx);
			self.$datePicker.removeAttr('disabled');


			// Lazy Load Datepicker and its Region
			Modernizr.load([{
				load: [
					'/_ui/all/cache/dyn-jquery-ui-datepicker.min.js',
					'/_ui/all/cache/dyn-jquery-ui-datepicker-i18n.min.js'
				],
				complete: function () {


					var $backendData = $('#backenddata'),
					optRegional = $('.shopsettings', $backendData).data('language'),
					optDateFormat = self.$datePicker.data('format'),
					optDateMax = self.$datePicker.data('date-max'),
					optDateMin = self.$datePicker.data('date-min')
					;

					self.$datePicker.datepicker({

						beforeShowDay: $.datepicker.noWeekends,
						showButtonPanel: true,

						onSelect: function() {

							// set dateFrom max date with the selected toDate
							//$("#filter_datefrom", $ctx).datepicker('option', 'maxDate', selected);

							self.$datePicker.closest('form').trigger('submit');
							self.$datePicker.attr('disabled', 'disabled');
						}
					});

					$('#command').click(function(){
                        var toolText = $('.tooltip-text').val();
						$('.ui-datepicker-close').html("Close");
                        $('.ui-priority-primary').html(toolText);
					});

					if(optRegional == 'en') {
						optRegional = 'en-GB';
					}

					// We need to save the value to set it again
					var  fromDate = self.$datePicker.val();
					self.$datePicker.datepicker('option', $.datepicker.regional[ optRegional ] );

					//Sweden
					if (optDateFormat === 'yyyy-MM-dd'){
						optDateFormat = 'yy-mm-dd';
					}

					//Lithuania
					if (optDateFormat === 'yy-MM-dd'){
						optDateFormat = 'yy-mm-dd';
					}

					//Norway, Finland
					if (optDateFormat === 'dd.MM.yyyy'){
						optDateFormat = 'dd.mm.yy';
					}

					//Denmark
					if (optDateFormat === 'dd/MM/yy'){
						optDateFormat = 'dd/mm/yy';
					}

					//Poland
					if (optDateFormat === 'dd.MM.yy'){
						optDateFormat = 'dd.mm.yy';
					}

					//CZ
					if (optDateFormat === 'MM/dd/yy'){
						optDateFormat = 'mm/dd/yy';
					}

					//HU
					if (optDateFormat === 'yyyy.MM.dd'){
						optDateFormat = 'yy.mm.dd';
					}

					self.$datePicker.datepicker('option', 'dateFormat', optDateFormat );
					self.$datePicker.datepicker('option', 'maxDate', optDateMax );
					self.$datePicker.datepicker('option', 'minDate', optDateMin );
					self.$datePicker.datepicker('setDate', fromDate);


				}
			}]);

			self.$datePicker.on('change', function () {
				self.$datePicker.closest('form').trigger('submit');
				self.$datePicker.attr('disabled', 'disabled');
			});

			// calling parent method
			parent.on(callback);

		};

		this.after = function () {
			// calling parent method
			parent.after();

			// Do stuff here
			//...
		};

	};

})(Tc.$);
