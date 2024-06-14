(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Register
	 * @extends Tc.Module
	 */
	Tc.Module.FormFeedback = Tc.Module.extend({

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

			//
			// Validation Error Messages
			this.validationErrorEmpty = this.$$('#tmpl-form-feedback-validation-error-empty').html();
			this.validationErrorEmail = this.$$('#tmpl-form-feedback-validation-error-email').html();
			this.validationErrorCaptcha = this.$$('#tmpl-form-feedback-validation-error-captcha').html();
			this.validationErrorTellUsMore = this.$$('#tmpl-form-feedback-validation-error-tellUsMore').html();
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {

			var self = this;

            // DISTRELEC-12787: Load Manufacturers Into Select Box - Zero Results Page
			$.ajax({
				url: '/manufacturers',
				dataType: 'JSON',
				method: 'GET'
			})

			.done(function (data) {
				// String selector for finding all manufacturer options except "Other manufacturer" option
				var allOptionsExceptOtherSelector = '#manufacturerSelectBoxItOptions > li:not([data-val="other_manufacturer"])';

				// Iterate through data from BE and append `<option>` elements based on data from BE
				$(data).each(function (index) {
					var manuName = data[index].name;
					var manuCode = data[index].code;

					$('#manufacturer').append(
						'<option value="' + manuName + '" data-code="' + manuCode + '">' + manuName + '</option>'
					);
				});

				// In addition to not load "selectboxit" earlier than jquery, we need to postpone its load little bit
				// If there is no short delay, issue is shown occasionally on page load
				setTimeout(function () {
					// Check if user is using IE11
					var isIE11 = !!window.MSInputMethodContext && !!document.documentMode;

					// Load jquery Selectboxit plugin
					Modernizr.load([{
						load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
						complete: function () {
							// Get scope element for manufacturer search
							var $manSearch = $('.js-manSearch');
							// Get "select" form element
							var $manSearchSelect = $('select.js-manSearch-select', $manSearch);
							// Get "input" which will use as search
							var $manSearchInput = $('.js-manSearch-input', $manSearch);

							// Helper function which gets value from `<option>` inside select
							// For "Other manufacturer" we need to use value form `<option>` text
							function getValueFromSelectOption (code) {
								var optionValue = code;

								if (code === 'other_manufacturer') {
									optionValue = $('option[value="' + code + '"]').text();
								}

								return optionValue;
							}

							// Init Selectboxit plugin upon normal "select" element
							$manSearchSelect.selectBoxIt({
								autoWidth : false,
								isMobile: function() {
									return false;
								}
							}).bind({
								// When dropdown is opened, show search input and add focus on it
								"open": function() {
									// Get value from select item
									var selectValue = $manSearchSelect.val();

									// If value on select exists (user already selected something)
									if (selectValue) {
										// Add this value into input
										$manSearchInput.val(getValueFromSelectOption(selectValue));
										// Show all options
										$(allOptionsExceptOtherSelector).removeClass('hidden');
									}

									// If error is occured on form, toggle helper class
									$manSearch.toggleClass('has-error', !!$('.error', $manSearch).length);
									// Show search input
									$manSearchInput.addClass('active');

									// If user is not on IE11, trigger focus on search
									if (!isIE11) {
										$manSearchInput.trigger('focus');
									}
								},
								// When dropdown is closed, hide search input
								"close": function() {
									// Hide search input
									$manSearchInput.removeClass('active');
									// If error is occured on form, toggle helper class for error
									$manSearch.toggleClass('has-error', !!$('.error', $manSearch).length);
								},
								"option-click": function () {
									// When user selects item, add this value to search input
									$manSearchInput.val(getValueFromSelectOption($manSearchSelect.val()));

									// On IE, manually hide dropdown and search input
									if (isIE11) {
										$manSearch.removeClass('ie11-drop-opened');
										$manSearchInput.removeClass('active');
									}
								}
							});

							// Get all options inside Seleectboxit element, after initialization
							var $allOptionsExceptOther = $(allOptionsExceptOtherSelector);

							if (isIE11) {
								// On IE, manually show dropdown and search input when user clicks on "selectboxit" element
								$('span.js-manSearch-select').on('click', function () {
									$manSearch.addClass('ie11-drop-opened');
									$manSearchInput.addClass('active').trigger('focus');
								});
							}

							// On focusout, after small delay close dropdown, focusout is executed when user clicks on item in dropdown
							// Without delay, dropdown will be closed without users selection
							$manSearchInput.on('focusout', function () {
								setTimeout(function () {
									$('.js-manSearch-select').data("selectBox-selectBoxIt").close();

									// On IE, manually hide dropdown and search input
									if (isIE11) {
										$manSearch.removeClass('ie11-drop-opened');
										$manSearchInput.removeClass('active');
									}
								}, 100);
							});

							// As user types, perform search if there are more than 2 chars entered
							$manSearchInput.on('input', function () {
								var $this = $(this);
								// Make all chars lowercased so we dont need to worry about type of chars
								var val = $this.val().toLowerCase();

								// If there are 2 or more chars entered, execute search
								// Else show all options
								if (val.length > 1) {
									// Iterate through each item and if value from users input matches with some chars in current item, show it
									$allOptionsExceptOther.each(function () {
										var $currentOption = $(this);
										$currentOption.toggleClass('hidden', $currentOption.text().toLowerCase().indexOf(val) < 0);
									});
								} else {
									$allOptionsExceptOther.removeClass('hidden');
								}
							});
						}
					}]);
				}, 300);
			})

			.fail(function(jqXHR, textStatus, errorThrown) {
			});

			$('.mod-form-feedback__container__form').click(function() {

                if ($('#manufacturerSelectBoxItText').attr('data-val') === 'other_manufacturer') {
                	$('.row--manufacturer').removeClass('hidden');
                	$('#manufacturerTypeOtherName').addClass('validate-empty');
                } else {
                    $('.row--manufacturer').addClass('hidden');
                    $('#manufacturerTypeOtherName').removeClass('validate-empty');
                    $('#manufacturerTypeOtherName').removeClass('error');
                }

			});

			// Validate Elements

			this.$ctx.on('click', '.btn--feedback', function(e) {
                var isValid = true;

				e.preventDefault();
				
                Tc.Utils.validate($('.validate-dropdown',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
                    if (error) {
                        isValid = false;
                    }

                });

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if (error) {
						isValid = false;
					}
				});

				Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
					if (error) {
						isValid = false;
					}
				});
		
					Tc.Utils.validate($('.validate-min-max',self.$ctx), self.validationErrorTellUsMore, 'triangle', function(error) {
						if (error) {
							isValid = false;
						}
					});
				
				
				if (isValid) {

					var gid = $('.feedback-form .g-recaptcha').data('gid');

					if (typeof gid !== 'number') {
						gid = grecaptcha.render($('.feedback-form .g-recaptcha',self.$ctx)[0],{},true);
						$('.feedback-form .g-recaptcha',self.$ctx).eq(0).data({'gid':gid});
					}

                    grecaptcha.execute(gid);

				} else {
					Tc.Utils.scrollToFirstError($('.js-feedback-form-row .error:visible', self.$ctx).first());
				}

			});

			callback();

		}

	});

})(Tc.$);
