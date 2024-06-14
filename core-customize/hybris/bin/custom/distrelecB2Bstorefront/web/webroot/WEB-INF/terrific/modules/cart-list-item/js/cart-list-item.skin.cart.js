(function($) {
	/**
	 * OrderDetail Skin implementation for the module CartListItem.
	 *
	 */
	Tc.Module.CartListItem.Cart = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. parent.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			var self = this;
			var $ctx = this.$ctx;

			if($('.numeric-popover').hasClass('active_moq')) {

				setTimeout(function(){
					$('.numeric-popover').removeClass('active_moq');
				}, 3000);

			}

			$('.numeric-btn').click(function(){

				if($('.numeric').hasClass('numeric-error')) {
					$('.mod-checkout-proceed > .btn-checkout').attr('disabled','disabled');
				} else {
					$('.mod-checkout-proceed > .btn-checkout').removeAttr('disabled');
				}

			});

			$('.btn-wrapper > .ipt').blur(function(){

				if($('.numeric').hasClass('numeric-error')) {
					$('.mod-checkout-proceed > .btn-checkout').attr('disabled','disabled');
				} else {
					$('.mod-checkout-proceed > .btn-checkout').removeAttr('disabled');
				}

			});

			$('.toolbar__add-reference').click(function (){

				$(this).next('.toolbar__input').addClass('toolbar__input--active');
			});

			parent.on(callback);
		};

        $(".toolbar__edit__text").on('click', function () {
            $(this).parent().siblings('.toolbar__input').addClass('toolbar__input--active');
        });

        $(".toolbar__input__btn").on('click', function () {

            var inputClass = $(this).prev('.ipt-reference'),
				inputValue = inputClass.val();

            $('.toolbar__input').removeClass('toolbar__input--active');

            if(inputClass.val().length === 0 ) {
                $(this).parent().siblings('.toolbar__edit').removeClass('toolbar__edit--active');
                $(this).parent().siblings('.toolbar__add-reference').removeClass('hidden');
            } else {
                $(this).parent().siblings('.toolbar__edit').addClass('toolbar__edit--active');
                $(this).parent().siblings('.toolbar__edit').find('.toolbar__edit__reference').html(inputValue);
                $(this).parent().siblings('.toolbar__add-reference').addClass('hidden');
            }

        });


	};
})(Tc.$);
