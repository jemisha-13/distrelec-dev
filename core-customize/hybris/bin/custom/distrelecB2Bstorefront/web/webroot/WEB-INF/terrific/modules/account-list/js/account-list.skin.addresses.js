(function($) {

	/**
	 * Addresses Skin implementation for the module AccountList.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.AccountList
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.AccountList.Addresses = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */

		this.on = function (callback) {
            // calling parent method
            parent.on(callback);
            
			// subscribe to channel(s)
			this.sandbox.subscribe('lightboxYesNo', this);

			this.onShowMoreButtonClick = $.proxy(this, 'onShowMoreButtonClick');

			var self = this;

			this.$showMoreLink = this.$$('.b-show-more');
			this.$showMoreLink.on('click', self.onShowMoreButtonClick);
			
			this.$btnSetAsDefault = this.$$('.btn-set-default');
			this.action = this.$btnSetAsDefault.data('action');
			
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
					, addressType = $link.data('address-type')
				;
				
				
				self.fire(
					'yesNoAction',
					{
						actionIdentifier: self.actionIdentifier,
						attribute: lightboxAddressId + ',' + isBilling + ',' + isShipping ,
						addressId: lightboxAddressId,
						isShipping: isShipping,
						isBilling: isBilling,
						lightboxTitle: lightboxTitle,
						addressType: addressType,
						lightboxMessage: lightboxMessage,
						lightboxBtnDeny: lightboxBtnDeny,
						lightboxShowBtnConfirm: lightboxShowBtnConfirm,
						lightboxBtnConf: lightboxBtnConf
					},
					['lightboxYesNo']
				);
				return false;
			});			
			
		};

		//
		// Load More
		//
		this.onShowMoreButtonClick = function(ev) {

			ev.preventDefault();
			var pageSize = $(ev.target).closest('a').data('page-size')
				, hiddenAddresses = this.$$('.data-list .row.paged')
				, addressesToShow = hiddenAddresses.slice(0, pageSize)
				;

			$.each(addressesToShow, function (index, address) {
				var $address = $(address);

				$address.removeClass('paged');
				$address.hide().slideDown();
			});

			if (hiddenAddresses.length <= pageSize) {
				this.$$('.row-show-more').slideUp();
			}
		};
		
		
		this.onDialogConfirm = function(ev) {
			var self = this;
			
			var splitted = ev.attribute.split(',');
			var addressId = splitted[0];
			var isBilling = splitted[1];
			var isShipping = splitted[2];
			
			$.ajax({
				url: self.action + addressId + '&billing=' + isBilling + '&shipping=' + isShipping,
				type: 'POST',
				success: function(data, textStatus, jqXHR) {
					window.location.reload();
				},
				error: function(jqXHR, textStatus, errorThrown) {
				}
			});				
			
		};

		this.onDialogCancel = function(ev) {
		};
		
		


	};

})(Tc.$);
