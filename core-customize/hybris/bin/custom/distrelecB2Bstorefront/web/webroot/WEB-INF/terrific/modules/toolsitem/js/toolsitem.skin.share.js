(function ($) {

	Tc.Module.Toolsitem.Share = function (parent) {

		this.on = function (callback) {
			var self = this;

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxShareEmail', this);
			this.sandbox.subscribe('facetActions', this); // for updating url

			// Context & Click Handler
			this.onClick = $.proxy(this, 'onClick');
			this.onClick = $.proxy(this, 'onClick');
			this.$ctx.on('click', this.onClick);

			if(this.$$('.ico-share-twitter').length > 0){
				$.getScript("//platform.twitter.com/widgets.js");
			}

			// special treatment for share and print tools (#DISTRELEC-1804)
			this.$icoPopoverSpecial = $('.ico.popover-special', this.$ctx);
			this.$icoPopoverSpecial.popoverFromHtml({
				placement: 'top'
			}).on('click.toolsitemShare', function (e) {
				e.preventDefault();
			});

			this.$icoPopoverSpecial.on('shown.bs.popover', function () {
				self.popoverCreationCallback();
			});

			// document delegate click handler: close popover unless click target matches
			$(document).on('click.documentToolsitemShare', function (ev) {
				if (!$(self.$icoPopoverSpecial).is(ev.target) && $(self.$icoPopoverSpecial).has(ev.target).length === 0 && $('.popover').has(ev.target).length === 0) {
					$(self.$icoPopoverSpecial).popover('hide');
				}
			});

			// calling parent method
			parent.on(callback);
		};


		// Event Dispatcher

		this.onClick = function (ev) {
			var $targetIco = $(ev.target).closest('a');

			if ($targetIco.hasClass('share_email')) {
				ev.preventDefault();
				this.fireShareEmail();
			}
		};

		// set new sharing url since url in browser bar is not getting updated in IE8/IE9
		this.onLoadProductsForFacetSearchCallback = function (data) {
			if (data.status) {
				var baseUrl = window.location.protocol + '//' + window.location.host;
				this.$icoPopoverSpecial.data('current-url', baseUrl + data.currentQueryUrl);
				this.$icoPopoverSpecial.data('update-urls', true);
			}
		};

		// we need to update url's after popover creation, because data attributes are not copied over on creation because of a bootstrap bug
		this.popoverCreationCallback = function () {
			var updateUrls = this.$icoPopoverSpecial.data('update-urls');
			if (updateUrls) {
				var currentQueryUrl = this.$icoPopoverSpecial.data('current-url');
				// we do currently not update the twitter url. For modern browser, it works via urlPushState. In IE8/IE9 it doesnt work

				// change url for google plus
				var newGooglePlusShareUrl = this.$$('.ico-share-google').data('base-url') + currentQueryUrl;
				this.$$('.popover.in .ico-share-google').attr('href', newGooglePlusShareUrl);
			}
		};

		this.fireShareEmail = function () {
			var currentQueryUrl = this.$icoPopoverSpecial.data('current-url');
			this.fire('showLightbox', { currentQueryUrl: currentQueryUrl}, ['lightboxShareEmail']);
		};

		this.after = function () {
			// calling parent method
			parent.after();

		};

	};

})
	(Tc.$);
