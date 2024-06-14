(function ($) {

	Tc.Module.MetahdItem.Account = function (parent) {

		this.on = function (callback) {
			var self = this,
				$ctx = this.$ctx;

			this.emailStore = localStorage.getItem("emailStore");
			if( this.emailStore !== null) { // Populates email input field with stored value
				$('#metahd-account-login').val(this.emailStore);
			}

			$('#metahd-account-remember').click(function(){ // Click function to store Email value into browser local storage
				$('#metahd-account-remember').prop( 'checked', $(this).is(":checked") );
				localStorage.setItem("emailStore", $('#metahd-account-login').val() );
				localStorage.setItem("LoginRememberMe", $(this).prop( 'checked') );
			});

			this.$popover = null;
			this.popoverTimer = null;
			this.popoverHideDelay = 4000;

			// subscribe to connector channel/s
			this.sandbox.subscribe('metaHDCompare', this);
			this.sandbox.subscribe('shoppinglist', this);
			this.sandbox.subscribe('favoritelist', this);

			this.showPopover = $.proxy(this.showPopover, this);
			this.hidePopover = $.proxy(this.hidePopover, this);
			this.onCompareChange = $.proxy(this.onCompareChange, this);
			this.onListsChange = $.proxy(this.onListsChange, this);
			this.rememberme = localStorage.getItem("LoginRememberMe");

			$(document).on('listsChange', this.onListsChange);

			// https://jira.namics.com/browse/DISTRELEC-2894
			// IE9 bug: submit is not triggering as expected in flyout unless the submit button receives focus once before
			// to reproduce, avoid hovering the submit button and just hit enter in any account input (in all other tested browsers the form submit is triggered as expected)
			if ($('html').is('.ie9')) {
				// as a symptomatic workaround we manually focus the submit button on pw keydown (enter only)
				$('#metahd-account-password', $ctx).on('keydown.ie9account', function(e) {
					if (e.keyCode === 13) {
						$('.btn-login', $ctx).focus();
					}
				});
			}

			this.$$('#loginForm').on({
				'submit': function (ev){
					ev.preventDefault();
					$('#metahd-account-login').val( $('#metahd-account-login').val().trim() );
					$('#metahd-account-password').val( $('#metahd-account-password').val().trim() );
					this.submit();
				}
			});

			this.$$('#metahd-account-login').on({
				'focus': function() {
					//here we should remove the Blur handler from the flyout box
					$('.skin-metahd-item-account').addClass('clicked');
				},
				'blur': function() {
					$('.skin-metahd-item-account').removeClass('clicked'); 
				}
			});	

			this.$$('#metahd-account-password').on({
				'focus': function() {
					$('.skin-metahd-item-account').addClass('clicked');
				},
				'blur': function() {
					$('.skin-metahd-item-account').removeClass('clicked'); 
				}
			});

			this.$$('.link-inactive').on({
				'click': function(e) {
					e.preventDefault();
				}
			});

            $(".flyout-close").click(function(){
                $('.skin-metahd-item-account').removeClass('hover');
            });

            $('.skin-metahd-item-account .mobile-holder span').click(function (){
            	$('.flyout').toggleClass('active');
            	$('.skin-metahd-item-account .mobile-holder').toggleClass('active');
                $('body').toggleClass('menu-active');
			});

            if ( $(window).width() < 767) {

            	var windowHeight = $(window).height();
            	var magic = windowHeight - 138;

                $('.skin-metahd-item-account .flyout').css('max-height', magic);

            }

            $( window ).resize(function() {

                if ( $(window).width() > 767) {
                    $('.flyout').removeClass('active');
                    $('.skin-metahd-item-account .mobile-holder').removeClass('active');
                    $('body').removeClass('menu-active');
                }

            });

			parent.on(callback);
		};

		this.onCompareChange = function (obj) {
			this.showPopover('compare', obj.quantityChange);
		};

		this.onListsChange = function (ev, listData) {
			this.showPopover(listData.type, listData.quantity);
		};

		//
		// Show a popover indicating a user action
		this.showPopover = function (type, quantityDifference) {
			var content = parseInt(quantityDifference) > 0 ? '<span class="sign">+</span> ' + quantityDifference : '<span class="sign">-</span> ' + parseInt(quantityDifference) * -1,
				$popoverEl;

			this.$popover = this.$$('.popover-origin').popover({
				content: content + '<span class="ico-' + type + '-popover"><i></i></span>', placement: 'bottom', trigger: 'manual', html: true
			})
				.popover('show');

			// force the popover width and position to be as designed
			$popoverEl = this.$$('.popover');
			$popoverEl.css({
				width: this.$ctx.width() + 2
			}).offset({
				left: this.$ctx.offset().left - 1,
				top: $popoverEl.offset().top + 3 // review JW to adjust popover to Flyouts
			});

			this.popoverTimer = setTimeout(this.hidePopover, this.popoverHideDelay);
			this.$popover.next().on('mouseenter', this.hidePopover);
		};

		this.hidePopover = function () {
			clearTimeout(this.popoverTimer);
			this.$popover.popover('destroy');
		};

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		this.after = function() {
			// Do stuff here or remove after method
			//...
			if ( this.rememberme === 'true' ) {
				$('#j_remember').prop( 'checked' , true );
				$('#metahd-account-remember').prop( 'checked' , true );
				$("#j_remember").val(this.emailStore);
				$("#metahd-account-login").val(this.emailStore);
			} else {
				$('#j_remember').prop( 'checked' , false );
				$('#metahd-account-remember').prop( 'checked' , false );
				localStorage.setItem("emailStore", '');
				$("#j_username").val('');
				$("#metahd-account-login").val('');
			}

		};

	};

})(Tc.$);
