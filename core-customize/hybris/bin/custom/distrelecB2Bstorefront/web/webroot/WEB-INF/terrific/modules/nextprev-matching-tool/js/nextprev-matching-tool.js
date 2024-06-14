(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Nextprevproductdetail
	 * @extends Tc.Module
	 */
	Tc.Module.NextprevMatchingTool = Tc.Module.extend({

		/**
		 * Initialize.
		 *
		 * @method init
		 * @return {void}
		 * @constructor
		 * @param {jQuery} $ctx the jquery context
		 * @param {Sandbox} sandbox the sandbox to get the resources from
		 * @param {Number} id the unique module id
		 */
		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

			$('.arrow_prev').removeClass('active');
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var $ctx = this.$ctx,
				self = this;
			
			self.$btnNext = self.$$('#scroll-next');
			self.$btnPrev = self.$$('#scroll-prev');
			var tableWidth = $('.table-import-matching').css('width').replace("px", "");
			
			if(tableWidth < 1000){
				$('.arrow_next').removeClass('active');
			}

			self.$btnPrev.on('click', function(ev) {
			    var leftPos = $('.matching-table').scrollLeft();
			    
			    // move scroll to the left 200px
			    $(".matching-table").animate({
			        scrollLeft: leftPos - 200
			    }, 150);
			    
			    $('.arrow_next').addClass('active');
			    
			    // remove the arrow when we reach the original position on the left.
			    if ($('.matching-table').scrollLeft() <= 200){
			    	$('.arrow_prev').removeClass('active');
			    }
			    
			});
			
			self.$btnNext.on('click', function(ev) {
			    var leftPos = $('.matching-table').scrollLeft();
			    
			    $('.arrow_prev').addClass('active');  
			    
			    // move scroll to the right 200px
			    $(".matching-table").animate({
			        scrollLeft: leftPos + 200
			    }, 150);	
			    
			    var newLeftPos = $('.matching-table').scrollLeft();
			    var rightPos = tableWidth-newLeftPos;
			    
			    // remove the arrow when we reach the maximum position on the right.
			    if (rightPos < 1200){
			    	$('.arrow_next').removeClass('active');
			    }			    
			    
			});

            digitalData.page.pageInfo.bomStep = 2;
			
			callback();
		}

	});

})(Tc.$);
