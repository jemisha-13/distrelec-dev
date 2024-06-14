(function($) {

	/**
	 * Dev Skin implementation for the module Nextprevproductdetail-Compare.
	 *
	 * @author Ruben Diaz
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Nextprevproductdetail.Compare = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			
            // calling parent method
            parent.on(callback);

			var self = this;
			var $ctx = this.$ctx;
			
			self.$btnNext = self.$$('#scroll-next');
			self.$btnPrev = self.$$('#scroll-prev');
			var tableWidth = $('.tableGrid').css('width').replace("px", "");

			
			if(tableWidth <= 767){
				$('.arrow_next-compare').removeClass('active');
			}
			
			$('.arrow_prev-compare').removeClass('active');
            var amountToScroll = $('.compare-list__grid-item').width();
            var scrollContainer = $(".compare-list__grid");

            if ( $('.compare-list__grid-item').length > 4 ) {
                $('.skin-nextprevproductdetail-compare').removeClass('hidden');
            } else {
                $('.skin-nextprevproductdetail-compare').addClass('hidden');
            }

            self.$btnPrev.on('click', function(ev) {
				ev.preventDefault();
				
			    var leftPos = scrollContainer.scrollLeft();
			    
			    // move scroll to the left X px
			    scrollContainer.animate({
			        scrollLeft: leftPos - amountToScroll
			    }, 150);
			    
			    $('.arrow_next-compare').addClass('active');
			    
			});

			self.$btnNext.on('click', function(ev) {
				ev.preventDefault();

				var leftPos = scrollContainer.scrollLeft();
			    
			    $('.arrow_prev-compare').addClass('active');  

			    // move scroll to the right X px
				scrollContainer.animate({
			        scrollLeft: leftPos + amountToScroll
			    }, 150);

			});
			
			
			parent.on(callback);
		};

	};

})(Tc.$);
