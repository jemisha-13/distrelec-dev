(function($) {

	/**
	 * Campaign Skin FeedbackPagination implementation.
	 *
	 */
	Tc.Module.Campaign.ProductDetailPage = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			
			var $ctx = this.$ctx,
				$dataProduct = this.$$('.data-product'),
				productNumber = $dataProduct.text(),
				ffSearchChannel = $dataProduct.data('ff-search-channel'),
				ajaxUrl = $dataProduct.data('ff-search-url'),
				ajaxPayload = {'do': 'getProductCampaigns', channel: ffSearchChannel, format: 'json', productNumber: productNumber}; // "do" is a reserved word, needs to be inside quotes

			$.ajax({ //FACT-Finder/ProductCampaign.ff?do=getProductCampaigns&channel=distrelec_7310_ch_en&format=json&productNumber=16931067
				url: ajaxUrl, 
				type: 'GET',
				dataType: 'json',
				data: ajaxPayload,
				traditional: true,
				success: function(data, textStatus, jqXHR) {
					
					if ( data.length > 0 && typeof data[0].feedbackTexts === 'object' && data[0].feedbackTexts.length > 0){
						$.each(data[0].feedbackTexts, function (index, campaign) {
							if (typeof campaign.text === 'string' && campaign.text.length > 0) {
								if (campaign.label === 'SearchResult_top'){
									$('.campaign-top-product-detail-page').html(campaign.text).addClass('loaded');
								}
								else {
									$('.campaign-bottom-product-detail-page').html(campaign.text);								
								}
							}
						});
					}
				},
				
				error: function(jqXHR, textStatus, errorThrown) {
				}
			});

			parent.on(callback);

		};
		


	};

})(Tc.$);
