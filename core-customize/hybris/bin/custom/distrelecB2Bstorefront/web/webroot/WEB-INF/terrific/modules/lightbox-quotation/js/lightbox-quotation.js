(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Lightboxquotation
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxQuotation = Tc.Module.extend({

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


			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxQuotation', this);
			this.sandbox.subscribe('lightboxQuotationConfirmation', this);
			this.sandbox.subscribe('lightboxLoginRequired', this);
			this.sandbox.subscribe('lightboxStatus', this);

			// set module variables
			this.$modal = $('.modal', $ctx);
			this.$numeric = $('.numeric', $ctx);// + -
			this.$ipt = $('.ipt', $ctx);// quantity
			this.$btnConfirm = $('.btn-send', $ctx);
			this.$priceTotal = $('.price-total', $ctx);
			this.price = this.$priceTotal.text();
		},


		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			callback();

			// multiply list-price with min-quantity defined by BE
			var self = this;
			self.updateTotalPrice(self.$ipt.val());
		},



		/**
		*
		* @method initHandler
		*
		*/
		initHandler: function() {
			var self = this;

			// Clear out previous values
			this.$modal.find('textarea').val('');
			$('#company').val('');
			$('#company').removeClass('error');
			$('#firstName').val('');
			$('#firstName').removeClass('error');
			$('#lastName').val('');
			$('#lastName').removeClass('error');
			$('#phone').val('');
			$('#phone').removeClass('error');
			$('#email').val('');
			$('#email').removeClass('error');

			// send button
			self.$btnConfirm.off('click').on('click', function(e) {
				// validation
				
				Tc.Utils.validate($('.validate-empty',self.$ctx), "", 'error', function(error) {
					if(error) {
						e.preventDefault();
					}
					else{
						self.sendForm();
					}
				});
				
			});


			// + -
			Tc.Utils.numericStepper(self.$numeric, {
				error: function(value, minErrorMsg) {// disable send if quantity below quotation threshold
					self.$btnConfirm.attr('disabled','disabled');
				},
				warning: function(value, minErrorMsg) {// enable send if its only a warning, value has been autocorrected
					self.updateTotalPrice(value);
					self.$btnConfirm.removeAttr('disabled');
				},
				success: function(value) {// reset disable
					self.updateTotalPrice(value);
					self.$btnConfirm.removeAttr('disabled');
				}
			});
		},


		/**
		*
		* @method validateOciAribaFields
		*
		* @param 
		*/
		validateOciAribaFields: function(e) {	
			
			Tc.Utils.validate($('.validate-empty', this.$ctx), "", 'triangle', function(error) {
				if(error) {
					e.preventDefault();
				} else {
					return true;
				}
			});
			Tc.Utils.validate($('.validate-email', this.$ctx), "", 'triangle', function(error) {
				if(error) {
					e.preventDefault();
				} else {
					return true;
				}
			});
		},
			
			
			
		/**
		*
		* @method onCheckUserLoggedIn
		*
		* @param data
		*/
		onCheckUserLoggedIn: function(data) {
			var self = this,
				$usersettings = $('.usersettings', self.$backendData);

			if ($usersettings.length > '0' && $usersettings.data('login').toString() === 'true') {
				self.openModal();
			} else {
				self.fire('openModal', ['lightboxLoginRequired']);
			}
		},


		/**
		*
		* @method onLoginSuccessful
		*
		*/
		onLoginSuccessful: function() {
			this.openModal();
		},


		/**
		*
		* @method openModal
		*
		*/
		openModal: function() {
			var self = this;

			self.initHandler();
			self.$modal.modal();
		},


		/**
		*
		* @method hideModal
		*
		*/
		hideModal: function() {
			this.$modal.modal('hide');
		},


		/**
		*
		* @method sendForm
		*
		*/
		sendForm: function() {
			var self = this,
				postData = Tc.Utils.getFormData(self.$modal);

			self.$btnConfirm.attr('disabled', 'disabled');

			var url = Tc.Utils.splitUrl(document.URL);

			$.ajax({
				url: url.base + '/quoteProductPrice',//'/_ui/all/data/quotation-post.json',
				type: 'post',
				data: postData,
				success: function() {
					self.sentFormSuccessful();
				},
				error: function(jqXHR, textStatus, errorThrown) {
					self.$btnConfirm.removeAttr('disabled');
					self.hideModal();
					self.fire('error', {// use status lightbox on general error
						title: jqXHR.status + ' ' + textStatus,
						boxTitle: textStatus,
						boxMessage: errorThrown
					}, ['lightboxStatus']);
				}
			});
		},


		/**
		*
		* @method sentFormSuccessful
		*
		*/
		sentFormSuccessful: function() {
			var self = this;

			self.hideModal();
			self.$btnConfirm.removeAttr('disabled');
			self.fire('confirm', ['lightboxQuotationConfirmation']);
		},


		/**
		*
		* @method updateTotalPrice
		*
		* param @quantity
		*/
		updateTotalPrice: function(quantity) {
			var self = this;


			if ((isFinite(quantity) && quantity > 0) && typeof self.price !== 'undefined') {

				var	priceDelimiter = ',',
					priceNormalized,
					bulkRate;

				//Temp save the price delimiter (point or comma)
				if(self.price.indexOf(',') == -1) {
					priceDelimiter = '.';
				}

				// Translate the price into a format with a point. Remove ' if there is one.
				priceNormalized = self.price.replace(',', '.').replace('\'',"");

				// Calculate the bulk rate
				bulkRate = parseFloat(quantity * priceNormalized).toFixed(2);

				// Set the bulk rate, convert the price back into the original format
				self.$priceTotal.text(bulkRate.replace('.', priceDelimiter));
			}
		}
	});

})(Tc.$);
