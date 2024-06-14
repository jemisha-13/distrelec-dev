(function($) {

	/**
	 * This module implements the accordion content skin technical information on the product detail page
	 *
	 * @namespace Tc.Module
	 * @class DetailTabsContent
	 * @skin TechnicalInformationCompare
	 * @extends Tc.Module
	 */
	Tc.Module.DetailTabsContent.TechnicalInformationCompare = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);
			var self = this;
			this.$btnShowMore = this.$$('.btn-show-more');

			// connect
			this.sandbox.subscribe('compareGrid', this);

            self.$btnShowMore.on('click', function (e) {
                e.preventDefault();
                self.showMore();
            });

		};

        this.showMore = function() {
            var self = this,
                $ctx = this.$ctx;
            $ctx.find('.row-hidden').removeClass('row-hidden');
            self.$btnShowMore.parent('.row').css('display', 'none');
	        // Update Element Heights
	        self.fire('showMoreTechnicalAttributes', {  }, ['compareGrid']);
        };

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		this.after = function () {
			// calling parent method
			parent.after();
		};

	};

})(Tc.$);
