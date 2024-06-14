(function($) {

	/**
	 * ApprovalRequest Skin implementation for the module AccountList.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.AccountList
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.AccountList.ApprovalRequest = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */

		this.on = function (callback) {
            // calling parent method
            parent.on(callback);

			var self = this;

			this.$showMoreLink = this.$$('.b-load-more');
			this.$showMoreLink.on('click', self.onLoadMoreButtonClick);
		};

		//
		// Load More
		//
		this.onLoadMoreButtonClick = function(ev) {
			var self = this
				,$dataList = $('.data-list', self.$ctx)
				,$rowTemplateHtml = $('.rowtemplate', self.$ctx)
				,$target = $(ev.target)
				,action = $target.data('action')
				,pageIndex = parseInt($target.data('page-current'))
				,nextPageNr = (pageIndex+1)
				,pagesNr = parseInt($target.data('pages-nr'))
				,sort = $target.data('sort')
				,status = $target.data('status')
				,orderNumber = $target.data('order-number')
				,fromDate = $target.data('from-date')
				,toDate = $target.data('to-date')
				,dataSend = '?page=' + nextPageNr + '&sort=' + sort + '&status=' + status + '&orderNumber=' + orderNumber + '&fromDate=' + fromDate + '&toDate=' + toDate
			;

			$target.data('page-current', nextPageNr);

			if(nextPageNr < pagesNr) {
				$.getJSON( action + dataSend, function( data ) {
					var orders = data.orders;
					$.each(orders, function(index, order) {
						var $orderRow = $rowTemplateHtml.clone();
						$orderRow.find('.code').html(order.orderCode);
						$orderRow.find('.orderdate').html(order.orderDate);
						$orderRow.find('.ordertotal').html(order.orderTotal);
						$orderRow.find('.status').addClass(order.iconStatus).append(' ' + order.orderStatus);
						$orderRow.find('.action > a').attr('href',order.detailUrl);
						$orderRow.removeClass('rowtemplate').hide().addClass('row').slideDown('fast');
						$orderRow.appendTo($dataList);
					});
				});
				if(nextPageNr == pagesNr-1) {
					// hide load more button
					$target.closest('.row-show-more').slideUp();
				}
			} else {
				// hide load more button
				$target.closest('.row-show-more').slideUp();
			}

			ev.preventDefault();
		};

	};

})(Tc.$);
