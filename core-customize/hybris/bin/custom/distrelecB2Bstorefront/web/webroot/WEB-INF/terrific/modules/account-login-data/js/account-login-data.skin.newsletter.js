$(document).ready(function(e){
	toggleYMarketingConsent(e, $);
});

function toggleYMarketingConsent($) {

	if ( $(this).prop('checked') === true ) {
		$('#npsConsentemail').prop('disabled', false);
		$('#npsConsentemail').prop('checked', true);
		$('#npsConsentemail').parent().removeClass('disabled');
		$('#npsConsentemail').removeAttr('disabled');
	} else {
		$('#npsConsentemail').prop('disabled', true);
		$('#npsConsentemail').prop('checked', false);
		$('#npsConsentemail').parent().addClass('disabled');
		$('#npsConsentemail').prop('disabled', 'disabled');
	}
}

(function($) {

	Tc.Module.AccountLoginData.Newsletter = function (parent) {

		this.on = function (callback) {

			var self = this;
	
			// Lazy Load SelectBoxIt Dropdown
			if(!Modernizr.touch && !Modernizr.isie7) {
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						 self.$$('select').selectBoxIt({
							autoWidth : false
						});
					}
				}]);
			}
			
			self.$$('#newsletter-yes, #newsletter-no').on('click', function() {
				$('.newsletter-options').toggleClass('hidden', $('#newsletter-no').is(':checked'));
			});

			// Validation Errors
			self.validationErrorEmpty = this.$$('#tmpl-newsletter-validation-error-empty').html();
			self.validationErrorCheckbox = this.$$('#tmpl-newsletter-validation-error-checkboxgroup').html();

			// Validate Elements
			self.$ctx.on('click', '.btn-change', function(e) {

				// Validation
				var isValid = true;

				if ($('.js-newsletter-yes').is(':checked')){
					Tc.Utils.validate($('.validate-dropdown',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
						if(error) { e.preventDefault(); isValid = false; }
					});

					Tc.Utils.validate($('.validate-checkbox-group',self.$ctx), self.validationErrorCheckbox, 'triangle', function(error) {
						if(error) { e.preventDefault(); isValid = false; }
					});
				}
			});

            self.$ctx.on('change', '#marketingConsentemail', function(e) {
				toggleYMarketingConsent.call(this, $);
			});

	        // calling parent method
	        parent.on(callback);
		};
    };
})(Tc.$);
