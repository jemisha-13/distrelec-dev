(function($) {

	/**
	 * Productlist Skin Favorite implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.CheckoutSuccess.Nps = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			var self = this;

			this.$submitButton = this.$$('.btn-send');
			this.$submitButton.on('click', self.onSubmitButtonClick);

			this.$topicSelect = this.$$('.selectpicker-reason');
			this.$topicSelect.on('change', self.onTopicSelectChange);

			parent.on(callback);
		};


		this.onSubmitButtonClick = function (ev) {

			var isValid = true;

			ev.preventDefault();

			var npsId = $('input:hidden[name=id]').val();
			var type = $('input:hidden[name=type]').val();
			var order = $('input:hidden[name=order]').val();
			var email = $('input:hidden[name=email]').val();
			var fname = $('input:hidden[name=fname]').val();
			var namn = $('input:hidden[name=namn]').val();
			var company = $('input:hidden[name=company]').val();
			var cnumber = $('input:hidden[name=cnumber]').val();
			var contactnum = $('input:hidden[name=contactnum]').val();
			var delivery = $('input:hidden[name=delivery]').val();
			var value = $("input:radio[name=value]:checked").val();
			var feedback = $('.feedback-textarea').val();
			var reason = $('.selectpicker-reason').val();
			var subreason = $('.selectpicker-subreason').val();

			if (value === undefined){
				isValid=false;
			}

			if (isValid){

				$('.table-radio').removeClass('radio-not-selected');

				$.ajax({
					url: '/feedback/nps',
					type: 'POST',
					dataType: 'json',
					method: 'post',
					data: {
						"id": npsId,
						"type": type,
						"order": order,
						"email": email,
						"fname": fname,
						"namn": namn,
						"company": company,
						"cnumber": cnumber,
						"contactnum": contactnum,
						"delivery": delivery,
						"value": value,
						"feedback": feedback,
						"reason": reason,
						"subreason": subreason
					},
					success: function (data, textStatus, jqXHR) {
						if (data.status === 'OK') {
							$('.nps-section').addClass('hidden');
							$('.message-success').removeClass('hidden');

			                setTimeout(function () {
			                	$('.message-success').fadeOut("slow");
			                	window.location = "/";
			                }, 5000);
						}
						else{
							$('.message-error').removeClass('hidden');
						}
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});
			}
			else{
				$('.table-radio').addClass('radio-not-selected');
			}
		};


		this.onTopicSelectChange = function(ev) {

			if ( $('.selectpicker-reason').val() === 'deliveryOfGoods' ) {
				$('.sub-reasons').removeClass('hidden');
				$('.sub-reasons').slideDown( 'fast');
			} else {
				$('.sub-reasons').slideUp( 'fast');
			}

		};

		// NPS pull user rating score from the URL

		var url = window.location.href;
		var strCheck = 'value=';
		var confirm = false;

		if ( url.indexOf(strCheck) !== -1 ) {
			confirm = true;
		}

		if ( confirm === true ) {
            var split = url.split('value=');
            var split_two = split[1].split('&');

            $('.nps_score').append(split_two[0]);
            $('.table-radio input[name=value][value=' + split_two[0] + ']').prop('checked', true);
		} else {
            $('.score-rating').toggleClass('score-rating--visible');
            $('.score-main-holder').toggleClass('score-main-holder--invisible');
		}

		// NPS Toggle Score Rating

		$('.nps-score-toggle').click(function() {
			$('.score-rating').toggleClass('score-rating--visible');
			$('.score-main-holder').toggleClass('score-main-holder--invisible');
		});

	};

})(Tc.$);
