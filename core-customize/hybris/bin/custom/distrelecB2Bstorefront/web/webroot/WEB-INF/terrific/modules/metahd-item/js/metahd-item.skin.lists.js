(function($) {

	/**
	 * Product Skin Bom implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.MetahdItem.Lists = function (parent) {

		this.on = function (callback) {

			this.$popover = null;
			this.popoverTimer = null;
			this.popoverHideDelay = 4000;

			this.onListsChange = $.proxy(this.onListsChange, this);
			this.hidePopover = $.proxy(this.hidePopover, this);
			this.onAddedNewListAndProductsSuccess = $.proxy(this.onAddedNewListAndProductsSuccess, this);
			this.onShoppingListNameUpdate = $.proxy(this.onShoppingListNameUpdate, this);

			// connect to channels Channel
			this.sandbox.subscribe('shoppinglist', this);
			this.sandbox.subscribe('lightboxShoppinglist', this);

			this.uiTpl = doT.template(this.$$('#template-metahd-new-list').html());

			$(document).on('listsChange', this.onListsChange);

			parent.on(callback);
		};

		this.onListsChange = function (ev, listData) {
			this.showPopover(listData.type, listData.quantity);
		};
	
		
		
		//
		// Show a popover indicating a user action
		this.showPopover = function (type, quantityDifference) {
			var content = parseInt(quantityDifference) > 0 ? '<span class="sign">+</span> ' + quantityDifference : '<span class="sign">-</span> ' + parseInt(quantityDifference) * -1,
				$popoverEl;

			this.$popover = this.$$('.popover-origin').popover({
				content: content + '<span class="ico-' + type + '-popover"><i></i></span>', placement: 'bottom', trigger: 'manual', html: true
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

		this.onListNameUpdate = function (data) {
			var mod = this
				, $shoppingList = this.$$('.shopping-lists')
				, $shoppingListItem = this.$$('.list', $shoppingList)
				;

			// loop over shopping list items
			$.each($shoppingListItem, function () {
				var $item = $(this)
					, $listName = $item.find('.listname')
					, dataListId = $listName.data('list-id')
					;
				// check if received data id matches with existing in list
				if (data.listId == dataListId) {
					// set content to new name
					$listName.find('span').text(data.listName);
				}
			});
		};

		this.onAddedNewListAndProductsSuccess = function (data) {
			var maxDisplayedListCount = 5
				, list = this.$$('.ui-list')
				;
			// Parse Template
			list.append(this.uiTpl(data));

			var listItems = this.$$('.list', list);

			// Sort newly added item to be alphabetically
			listItems.sort(function (a, b) {
				var upA = $(a).text().trim().replace('\\r\\n', '').toUpperCase();
				var upB = $(b).text().trim().replace('\\r\\n', '').toUpperCase();
				if (upA < upB) {
					return -1;
				}
				else if (upA > upB) {
					return 1;
				}
				else {
					return 0;
				}
			}).appendTo(list);

			// only display first 5 lists
			this.$$('.list', list).slice(maxDisplayedListCount).remove();
		};
	};

})(Tc.$);
