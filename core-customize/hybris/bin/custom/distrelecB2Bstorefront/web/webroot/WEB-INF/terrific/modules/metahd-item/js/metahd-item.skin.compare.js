(function ($) {

	/**
	 * Product Skin Bom implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.MetahdItem.Compare = function (parent) {


		this.on = function (callback) {

			// compare item is only visible if at least one item has been added to the compare list, also hidden after last one has been removed
			this.isFlyoutVisible = false;

			this.$popover = null;
			this.popoverTimer = null;
			this.popoverHideDelay = 4000;

			this.rowTemplate = doT.template($('#template-metahd-compare-row', this.$ctx).html());
			this.$flyout = this.$$('.flyout');
			this.$body = this.$flyout.find('.bd');
			this.isFlyoutVisible = !this.$ctx.hasClass('hidden');

			// connect
			this.sandbox.subscribe('metaHDCompare', this);
			this.sandbox.subscribe('lightboxStatus', this);

			this.hidePopover = $.proxy(this.hidePopover, this);

			var self = this,
				$btnClose = self.$$('.btn-close');

			// json get user's compare list
			$.ajax({
				url: '/compare/metaCompareProducts',
				type: 'get',
				cache: false, // internet explorer need this! 
				dataType: 'json',
				success: function (response) {
					self.updateFlyout(response);
				},
				error: function (jqXHR, textStatus, errorThrown) {
				}
			});

			parent.on(callback);
		};

		// sandbox listener for 'add to compare' events
		// param: obj
		this.onCompareChange = function (obj) {


			this.updateFlyout(obj);
			this.showPopover(obj.quantityChange);
		};

		// update module visible state
		this.updateVisibleState = function () {
			if (this.isFlyoutVisible === true) {
				this.$ctx.removeClass('hidden');
			}
			else {
				this.$ctx.addClass('hidden');
			}
		};

		// show a popover indicating a user action (popover-origin must be visible before positioning is possible)
		this.showPopover = function (quantity) {
			var content = parseInt(quantity) > 0 ? '<span class="sign">+</span> ' + quantity : '<span class="sign">-</span> ' + parseInt(quantity) * -1,
				$popoverEl, $roguePopoverMarkup, popupCacheEmpty = null;

			if (this.$popover && this.$popover.data) { // Review:KR: DISTRELEC-1297

				this.$popover.data().popover = popupCacheEmpty;
				$roguePopoverMarkup = this.$ctx.find('.popover');
				$roguePopoverMarkup.remove();
			}

			this.$popover = this.$$('.popover-origin').popover({
				content: content + '<span class="ico-compare-popover"><i></i></span>',
				placement: 'bottom',
				trigger: 'manual',
				html: true
			})
				.popover('show');


			// force the popover width and position to be as designed
			$popoverEl = this.$$('.popover');
			$popoverEl.css({
				width: this.$ctx.width() + 2
			}).offset({
					left: this.$ctx.offset().left - 1,
					top: $popoverEl.offset().top + 3 // review JW to adjust popover to Flyouts
				});

			this.popoverTimer = setTimeout(this.hidePopover, this.popoverHideDelay);
			this.$popover.next().on('mouseenter', this.hidePopover);
		};

		this.hidePopover = function () {
			clearTimeout(this.popoverTimer);
			this.$popover.popover('destroy');
		};

		// update flyout dynamically
		// param: json
		this.updateFlyout = function (json) {
			var self = this;
			self.$body.empty();

			// parsing json response, replace using doT
			if (Boolean(json.compareProductsData)) {
				// update visible state

				if (json.compareProductsData.products.length > 0) {
					self.isFlyoutVisible = true;
					self.fire('setVisibleState', {visible: true}, ['metaHDCompare']);
				}
				else {
					self.isFlyoutVisible = false;
					self.fire('setVisibleState', {visible: false}, ['metaHDCompare']);
				}

				self.updateVisibleState();


				$.each(json.compareProductsData, function (compareDataKey, compareDataValue) {// {compareProductsData}
					for (var i = 0, l = json.compareProductsData.products.length; i < l; i++) {// [products]
						self.$body.append(self.rowTemplate(json.compareProductsData.products[i]));
					}
				});
			}

			// bind module body handler: click on row = redirect to product detail page
			self.$body.on('click.compareFlyoutItem', '.compare-row', function (e) {
				e.preventDefault();

				var $item = $(this),
					url = $item.data('uri');

				if (typeof url !== 'undefined') {
					location.href = url;
				}
			});
		};
	};

})(Tc.$);
