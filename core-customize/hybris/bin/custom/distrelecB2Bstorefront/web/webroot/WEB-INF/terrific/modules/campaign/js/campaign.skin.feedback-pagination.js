(function($) {

	/**
	 * Campaign Skin FeedbackPagination implementation.
	 *
	 */
	Tc.Module.Campaign.FeedbackPagination = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			this.sandbox.subscribe('campaign', this);
			this.backenddata = $('#backenddata');
			this.onFeedbackTextTopChange = $.proxy(this, 'onFeedbackTextTopChange');

			parent.on(callback);

		};

		this.onFeedbackTextTopChange = function (data) {
			var $feedbackCampaign = this.$ctx;

			if(data.change) {
				$feedbackCampaign.removeClass('loading');

				var decodedFeedbackTextTop = $('<div />').html(data.feedbackTextTop).text(); // fact finder json delivers encoded html
				$feedbackCampaign.find($('.feedback-top')).empty().removeClass('loading').append(decodedFeedbackTextTop);
			} else {
				$feedbackCampaign.addClass('loading');
				$('.mod-campaign').find('.feedback-top').addClass('loading');
			}
		};


	};

})(Tc.$);
