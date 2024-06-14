(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class AddressForm
     * @extends Tc.Module
     */
    Tc.Module.AddressForm = Tc.Module.extend({

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

			// subscribe to channel(s)
			this.sandbox.subscribe('lightboxYesNo', this);

			this.$formBox = this.$$('.form-box');
	        this.customerType = this.$formBox.data('customer-type');
	        this.customerChannel = this.$formBox.data('customer-channel');
	        this.addressType = this.$formBox.data('address-type');
	        this.$deleteButton = this.$$('.btn-delete');
	        this.$saveButton = this.$$('.btn-save');
	        this.actionIdentifier = 'deleteAddress';
	        this.action = this.$deleteButton.data('action');
	        this.addressId = this.$deleteButton.data('address-id');
	        this.backUrl = this.$deleteButton.data('back-url');
			this.$inputFields = this.$$('input,select,.selectboxit-container');

	        // validation helpers
		    this.validationErrorEmpty = this.$$('#tmpl-address-form-validation-error-empty').html();
		    this.validationErrorDropdown = this.$$('#tmpl-address-form-validation-error-dropdown').html();
        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function(callback) {
			var $ctx = this.$ctx,
				self = this;

			if(this.$formBox.hasClass('readonly')) {
				$.each(this.$inputFields, function(index, field) {
					$(field).attr('disabled','disabled');
					$(field).addClass('disabled');
				});
				this.$saveButton.hide();
			}

			this.$deleteButton.click( function(ev) {
				var $link = $(ev.target).closest('a')
					, lightboxTitle = $link.data('lightbox-title')
					, lightboxMessage = $link.data('lightbox-message')
					, lightboxBtnDeny = $link.data('lightbox-btn-deny')
					, lightboxShowBtnConfirm = $link.data('lightbox-show-confirm-button')
					, lightboxBtnConf = $link.data('lightbox-btn-conf')
				;
				self.fire(
					'yesNoAction',
					{
						actionIdentifier: self.actionIdentifier,
						attribute: self.addressId,
						lightboxTitle: lightboxTitle,
						lightboxMessage: lightboxMessage,
						lightboxBtnDeny: lightboxBtnDeny,
						lightboxShowBtnConfirm: lightboxShowBtnConfirm,
						lightboxBtnConf: lightboxBtnConf
					},
					['lightboxYesNo']
				);
				return false;
			});

			// Lazy Load SelectBoxIt Dropdown
			if(!Modernizr.touch && !Modernizr.isie7) {
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						self.$$('#title').selectBoxIt({
							autoWidth : false
						});
						self.$$('#countryCode').selectBoxIt({
							autoWidth : false
						});
						self.$$('#regionCode').selectBoxIt({
							autoWidth : false
						});						
					}
				}]);
			}

	        // delegate module click handler: validation
	        $ctx.on('click', '.btn-primary', function(e) {
	        	
				// Format Swedish, Czech and Slovak  postal code
				
				if ($('#countryCode').val()==='SE' || $('#countryCode').val()==='CZ' || $('#countryCode').val()==='SK') {
					var postcode=$('#postcode').val().replace(/[^0-9]/g,''); // Only digits allowed
					if (postcode.length === 5) {
						$('#postcode').val(postcode.substring(0, 3) + " " + postcode.substring(3));
					}
				}

				Tc.Utils.validate($('.validate-empty',self.$ctx), self.validationErrorEmpty, 'triangle', function(error) {
					if(error) { e.preventDefault(); }
				});

				Tc.Utils.validate($('.validate-dropdown',self.$ctx), self.validationErrorDropdown, 'triangle', function(error) {
					if(error) { e.preventDefault(); }
				});

	        });
	        
			this.$btnSetAsDefault = this.$$('.btn-set-default');
			//this.action = this.$btnSetAsDefault.data('action');
			
			this.$btnSetAsDefault.click( function(ev) {
				var $link = $(ev.target).closest('a')
					, lightboxTitle = $link.data('lightbox-title')
					, lightboxMessage = $link.data('lightbox-message')
					, lightboxBtnDeny = $link.data('lightbox-btn-deny')
					, lightboxShowBtnConfirm = $link.data('lightbox-show-confirm-button')
					, lightboxBtnConf = $link.data('lightbox-btn-conf')
					, lightboxAddressId = $link.data('address-id')
					, isShipping = $link.data('is-shipping')
					, isBilling = $link.data('is-billing')
				;
				
				if (addressType === 'shipping'){
					isShipping = true;
					isBilling = false;
				}
				else{
					isShipping = false;
					isBilling = true;					
				}
				
				self.fire(
					'yesNoAction',
					{
						actionIdentifier: 'setAsDefault',
						attribute: lightboxAddressId + ',' + isBilling + ',' + isShipping ,
						addressId: lightboxAddressId,
						isShipping: isShipping,
						isBilling: isBilling,
						lightboxTitle: lightboxTitle,
						lightboxMessage: lightboxMessage,
						lightboxBtnDeny: lightboxBtnDeny,
						lightboxShowBtnConfirm: lightboxShowBtnConfirm,
						lightboxBtnConf: lightboxBtnConf
					},
					['lightboxYesNo']
				);
				return false;
			});
	        

			callback();

        },
        

        
        
        
        // onDialogConfirm address-form
		onDialogConfirm: function (data) {
			if(data.actionIdentifier === this.actionIdentifier){
				window.location.href = this.action + this.addressId;
			}
			else{  //set as default
				
				var self = this;
				
				var splitted = data.attribute.split(',');
				var addressId = splitted[0];
				var isBilling = splitted[1];
				var isShipping = splitted[2];
				
				$.ajax({
					url: '/my-account/set-default-address?addressCode=' + addressId + '&billing=' + isBilling + '&shipping=' + isShipping,
					type: 'POST',
					success: function(data, textStatus, jqXHR) {
						window.location.reload();
					},
					error: function(jqXHR, textStatus, errorThrown) {
					}
				});		
				
			}
		},

		onDialogCancel: function (data) {
		}
    });

})(Tc.$);
