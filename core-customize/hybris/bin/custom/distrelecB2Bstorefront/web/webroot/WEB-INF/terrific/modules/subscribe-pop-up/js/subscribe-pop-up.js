(function ($) {

	Tc.Module.SubscribePopUp= Tc.Module.extend({

		init: function ($ctx, sandbox, id) {

			this._super($ctx, sandbox, id);

			this.$inputField = this.$$('.js-emailInput');
			this.$successMessage = this.$$('.js-newsletterPopupSuccessMessage');
			this.$grecaptcha = this.$$('.js-popupNewsletterRecaptcha').find('.g-recaptcha');
			this.$submitButton = this.$$('.btn-signup');
			this.$closeButton = this.$$('.btn-close-signup');
			this.validationErrorEmail = this.$$('#tmpl-subscribe-popup-error-email').html();
			this.$subscribePopupDelayExpiration = this.$ctx.find('.btn-close-signup').data('delay-cookie-expire');
			this.$popupShownDelayExpiration = this.$ctx.find('.btn-signup').data('show-cookie-expire');
		},
		on: function(callback) {
			callback();
		},
		after: function() {
			var self = this;

			function isCookiePresent(name) {
				return !!document.cookie.match(name);
			}

			function setCookie(name, seconds, value) {
				if (seconds === 0) {
					document.cookie = name + '=' + encodeURIComponent(value) + '; path=/';
				} else {
					document.cookie = name + '=' + encodeURIComponent(value) + '; path=/' + '; expires=' + new Date(Date.now() + seconds * 1000).toUTCString();
				}
			}

			popupSubscribe = function () {
				var email = self.$inputField.val();
				var recaptchaValue = self.$grecaptcha.last().find("[id^='g-recaptcha-response']").val();

				$.ajax({
					url: '/newsletter/subscribe',
					type: 'POST',
					data: {
						"email": email,
						"g-recaptcha-response": recaptchaValue
					},
					success: function(response, textStatus, jqXHR) {
						if(response.doubleOptIn === true) {
							closePopup();
							$('.js-reminderPopupEmail').html(email);
							$('.js-doubleOptinReminderModal').modal({'backdrop': 'static'});
						}
						self.$successMessage.removeClass('hidden');
					},
					error: function(jqXHR, textStatus, errorThrown) {
					},
					complete: function() {
						var gid = self.$grecaptcha.data('gid');
						grecaptcha.reset(gid);
					}
				});
			};

			/** Check if any of cookies are present, if not set up subscribePopUpDelay with expiration
			 and popUpShownDelay with false **/
			function setSubscribeCookie() {
				if (!isCookiePresent("subscribePopUpDelay") && !isCookiePresent("popUpShownDelay")) {
					setCookie("subscribePopUpDelay", self.$subscribePopupDelayExpiration, 1);
					setCookie("popUpShownDelay", 0, "false");
				}
			}

			setSubscribeCookie();

			/** On page load, check if cookie popUpShownDelay=true is present,
			 * if it is, stop listening,
			 * if it is not, listen until subscribePopUpDelay expires, update popUpShownDelay=false and show pop-up **/
			var listenCookieChanges = setInterval(function () {
				if (isCookiePresent("popUpShownDelay=true")) {
					clearInterval(listenCookieChanges);
				}
				else if (!isCookiePresent("subscribePopUpDelay") && isCookiePresent("popUpShownDelay=false")) {					
					setCookie("popUpShownDelay", self.$popupShownDelayExpiration, "true");
					if(window.location.href.includes('unsubscribe') !== true) {
						displaySubscribePopUp();
					}
					clearInterval(listenCookieChanges);	
				}
			}, 5000);

			function displaySubscribePopUp() {
				$(".subscribe-pop-up").removeClass("hidden");
				$(".overlay-subscribe").addClass("active");
			}

			function closePopup() {
				$('.mod-subscribe-pop-up').addClass('hidden');
				$(".overlay-subscribe").removeClass("active");
			}

			function submitNewsletterForm() {
				self.$successMessage.addClass('hidden');

				var gid = self.$grecaptcha.data('gid');
				if (typeof gid !== 'number') {
					gid = grecaptcha.render(self.$grecaptcha[0],{callback:'popupSubscribe'},true);
					self.$grecaptcha.data({'gid':gid});
				}
				grecaptcha.execute(gid);
			}

			this.$closeButton.on('click', function(e) {
				e.preventDefault();
				closePopup();
			});

			// DISTRELEC-25162
			// As user types into email field, remove error box (if previously error occurred)
			this.$inputField.on('input', function () {
				var errorPopover = $(this).siblings('.popover');

				// If popover element exists in DOM, hide it and remove error class on input
				if (errorPopover.length > 0) {
					$(this).removeClass('error');
					errorPopover.hide();
				}
			});

			this.$submitButton.on('click', function(e) {
				Tc.Utils.validate($('.html-validate-email',self.$ctx), self.validationErrorEmail, 'popover', function(error) {
					if(error) {
						// Since we hide popover element as user types, we are showing it again if error occurs
						self.$inputField.siblings('.popover').show();
						e.preventDefault();
					} else {
						submitNewsletterForm();
					}
				});
			});
		}
	});

})(Tc.$);
