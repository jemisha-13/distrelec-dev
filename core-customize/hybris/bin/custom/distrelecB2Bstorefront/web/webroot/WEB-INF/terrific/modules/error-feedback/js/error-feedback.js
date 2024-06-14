(function ($) {

	/**
	 * Error feedback implementation.
	 *
	 * @namespace Tc.Module
	 * @class Facets
	 * @extends Tc.Module
	 */
	Tc.Module.ErrorFeedback = Tc.Module.extend({

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
				self=this,
				$errorLink=self.$$('.error-link'),
				$errorReport=self.$$('.error-report');
				$errorSubmit=self.$$('.error-submit a'),
				$errorCloser=self.$$('.closer');
			
				this.validationErrorRadio = this.$$('#tmpl-validation-error-radio').html();
				this.validationErrorCheckboxgroup = this.$$('#tmpl-validation-error-checkboxgroup').html();
				this.validationErrorEmpty = this.$$('#tmpl-validation-error-empty').html();
				this.validationErrorEmail = this.$$('#tmpl-validation-error-email').html();
				this.validationErrorLength = this.$$('#tmpl-validation-error-length').html();

				$errorReport.draggable();
				
				$errorLink.click(function(ev) {
					ev.preventDefault();
					var off=$errorLink.offset();
					$errorReport.css({left:'1005px',top:off.top.toString()+'px'}).toggleClass('hidden');
				});
				
				$errorSubmit.click(function(ev) {
					ev.preventDefault();
					var hasValidationErrors = false;

					Tc.Utils.validate($('.validate-radio-group',self.$ctx), self.validationErrorRadio, 'triangle', function(error) {
						if (error) { hasValidationErrors = true; }
					});

					Tc.Utils.validate($('.validate-checkbox-group',self.$ctx), self.validationErrorCheckboxgroup, 'triangle', function(error) {
						if (error) { hasValidationErrors = true; }
					});

					Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
						if (error) { hasValidationErrors = true; }
					});

					Tc.Utils.validate($('.validate-email',self.$ctx), self.validationErrorEmail, 'triangle', function(error) {
						if (error) { hasValidationErrors = true; }
					});

					Tc.Utils.validate($('.validate-max-length', self.$ctx), self.validationErrorLength, 'triangle', function(error) {
						if (error) { hasValidationErrors = true; }
					});

					if (!hasValidationErrors) {
						var errorArray = [];
						$errorReport.find('.error-reason input:checked').each(function() {
							errorArray.push($(this).val());
						});
						
						$.ajax({
							url: '/errorfeedback',
							type: 'POST',
							data: {'errorfeedback':JSON.stringify({
								productId: $errorReport.data('productid'),
								errorReason: errorArray.join(', '),
								errorDescription: $errorReport.find('.error-description').val(),
								customerName: $errorReport.find('.customer-name').val(),
								customerEmailId: $errorReport.find('.customer-email').val()
							})},
							success: function (data) {
								$errorReport.find('.error-sent.success').removeClass('hidden').find('.closer').mouseenter();
							},
							error: function (data,status,error) {
								$errorReport.find('.error-sent.fail').removeClass('hidden').find('.closer').mouseenter();
							},
							complete: function(data) {
								$errorReport.find('.error-submit').addClass('hidden');
							}
						});
					}
				});
				
				$errorCloser.click(function() {
					$errorReport.addClass('hidden');
				});
				
				callback();
				}
	});

})(Tc.$);