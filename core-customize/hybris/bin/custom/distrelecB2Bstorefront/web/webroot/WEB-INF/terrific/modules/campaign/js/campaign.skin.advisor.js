(function($) {

	/**
	 * Campaign Skin Advisor implementation.
	 *
	 */
	Tc.Module.Campaign.Advisor = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			this.sandbox.subscribe('campaign', this);
			this.onAdvisorCampaignChange = $.proxy(this, 'onAdvisorCampaignChange');
			this.onHideAdvisorCampaignChange = $.proxy(this, 'onHideAdvisorCampaignChange');

			parent.on(callback);

		};

		this.onAdvisorCampaignChange = function (data) {
			// remove on pageload loaded campaign
			if($('.js-initial-campaign')){
				$('.js-initial-campaign').remove();
			}

			var tmplAdvisor = "{{~ it :item:id}}" +
                "<p>{{= item.questionText}}</p>" +
                "{{? item.advisorAnswers}}" +
                "<ul>" +
                "{{~ item.advisorAnswers :it:id}}" +
                "<li>" +
                "<a href=\"{{= it.queryUrl}}\" name=\"${fn:toLowerCase(currentCountry.isocode)}.faf-c-a.${namicscommerce:encodeURI(fn:toLowerCase(answer.text))}.-\">" +
                "{{? it.text && it.image }}" +
                "<div class=\"classImage\">" +
                "<img style=\"display: inline\" src=\"{{= it.image}}\" />" +
                "<div class=\"ff-advisor-campaign\">{{= it.text}} <i></i></div>" +
                "</div>" +
                "{{?? !it.text && it.image }}" +
                "<div class=\"classImage\">" +
                "<img style=\"display: inline\" src=\"{{= it.image}}\" />" +
                "</div>" +
                "{{??}}" +
                "{{= it.text}} <i></i>" +
                "{{?}}" +
                "</a>" +
                "</li>" +
                "{{~}}" +
                "</ul>" +
                "{{?}}" +
                "{{~}}";

			var advisorTemplate = doT.template(tmplAdvisor);

			var $advisorCampaign = $(advisorTemplate(data.advisorCampaign.advisorQuestions));
			this.$$('.advisorBox').empty().append($advisorCampaign);

			this.$ctx.removeClass('loading');
		};

		this.onHideAdvisorCampaignChange = function () {
			this.$ctx.addClass('loading');
		};


	};

})(Tc.$);
