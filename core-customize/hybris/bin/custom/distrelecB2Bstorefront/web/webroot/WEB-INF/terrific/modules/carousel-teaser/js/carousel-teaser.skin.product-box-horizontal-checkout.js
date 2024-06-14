(function($) {

	Tc.Module.CarouselTeaser.ProductBoxHorizontalCheckout = function (parent) {

		this.on = function (callback) {
			
			var $ctx = this.$ctx,
				self = this;
			
			this.$closeLink = this.$$('.close-link');
			this.$neverAgain = this.$$('.never-again');
			
			this.$closeLink.on('click', function(e) {
				e.preventDefault();
				$('.product-box-horizontal').addClass('hidden');
			});
			
			this.$neverAgain.on('click', function(e) {
				e.preventDefault();
				self.onNeverAgainClicked(e);
				
			});			
			
			parent.on(callback);
		};

		this.onNeverAgainClicked = function (ev) { 
			
			var self = this;

			$.ajax({
				url: '/my-account/product-box',
				type: 'get',
				data: {
					value: false
				},					
				success: function (data, textStatus, jqXHR) {
					$('.product-box-horizontal').addClass('hidden');
				},
				error: function (jqXHR, textStatus, errorThrown) {
					// Ajax Fail
				}
			});
			
		};

	};

})(Tc.$);

