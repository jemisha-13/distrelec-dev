(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Article-numbers
	 * @extends Tc.Module
	 */
	Tc.Module.ArticleNumbers = Tc.Module.extend({

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

			// Do stuff here
			//...
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var $ctx = this.$ctx
				,self = this
				,body = $('body')
                ,priceMatchSection = $('.product-section__price-match-section')
                ,priceMatchSectionCta = $('.product-section__price-match-cta')
			;

            body.on('click','.copy-button-1',function(){
                self.copyText();
			});

			body.on('click','.copy-button-alias',function(){
				self.copyTextAlias($(this));
			});

            body.on('click','.copy-button',function(){
                self.copyTextSecond();

            });

            body.on('click','.mod-product-title .title',function(){
                var pricematchnamevisit = sessionStorage.getItem("pricematch-namevisit");

                if ( !pricematchnamevisit ) {
                    self.showPriceMatch();
                    sessionStorage.setItem("pricematch-namevisit", "true");
                }

            });

            body.on('click',function(e){
                var isProdName = $(e.target).parents().andSelf().is('.mod-product-title .title'),
                    isArtNum = $(e.target).parents().andSelf().is('.mod-article-numbers'),
                    isPopup = $(e.target).parents().andSelf().is('.product-section__price-match-section');

                if ( !isProdName && !isArtNum && !isPopup ) {
                    self.hidePriceMatch();
                }

            });

            priceMatchSectionCta.on('click', function (event) {
                    event.preventDefault();

                    var priceMatchSection = $('.product-section__price-match-section'),
                        email = priceMatchSection.data( "matchemail") ,
                        subjectLine = priceMatchSection.data( "matchemailsubject") ,
                        emailBody = priceMatchSection.data( "matchemailbody") ;


                    var disPartNumber = $("#myText").text();
                    var manPartNumber = $("#copyTypeName").text();
                window.location = 'mailto:' + email + '?subject=' + subjectLine + ' ' + disPartNumber + ' | ' + manPartNumber + '&body=' +  emailBody;
            });

            $(window).scroll(function() {
                var priceMatchSection = $('.product-section__price-match-section'),
                    scroll = $(window).scrollTop();

                if (scroll >= 500 && !priceMatchSection.hasClass('hidden') ) {
                    self.hidePriceMatch();
                    return false;
                }
            });

			callback();
		},

        copyText : function() {

			var $temp = $("<input>")
                ,self = this
				,textID = $('#myText')
                ,pricematchartnumvisit = sessionStorage.getItem("pricematch-artnumvisit");

            $('#article-tooltip').addClass('active');
            setTimeout(function() {
                $('#article-tooltip').removeClass('active');
            },2000);

            $("body").append($temp);
            $temp.val(textID.text()).select();
            document.execCommand("copy");
            $temp.remove();

            if ( !pricematchartnumvisit ) {
                self.showPriceMatch();
                sessionStorage.setItem("pricematch-artnumvisit", "true");
            }

		},

        copyTextSecond : function() {

            var $temp = $("<input>")
                ,self = this
                ,textID = $('#copyTypeName')
                ,pricematchmpnvisit = sessionStorage.getItem("pricematch-mpnvisit");

            $('#article-tooltip-1').addClass('active');
            setTimeout(function() {
                $('#article-tooltip-1').removeClass('active');
            },2000);

            $("body").append($temp);
            $temp.val(textID.text()).select();
            document.execCommand("copy");
            $temp.remove();

            if ( !pricematchmpnvisit ) {
                self.showPriceMatch();
                sessionStorage.setItem("pricematch-mpnvisit", "true");
            }

        },

		copyTextAlias : function(self) {

            var $temp = $("<input>")
                ,textID = self.parent().prev();

            self.siblings('.article-tooltip').addClass('active');
            setTimeout(function() {
                self.siblings('.article-tooltip').removeClass('active');
            },2000);

            $("body").append($temp);
            $temp.val(textID.text()).select();
            document.execCommand("copy");
            $temp.remove();

            self.showPriceMatch();
        },

        showPriceMatch : function() {
            var priceMatchSection = $('.product-section__price-match-section');
            priceMatchSection.addClass('hidden');
            priceMatchSection.removeClass('hidden');
        },

        hidePriceMatch : function() {
            var priceMatchSection = $('.product-section__price-match-section');
            priceMatchSection.addClass('hidden');
        },

		after: function() {
			// Do stuff here or remove after method
			//...
		}

	});

})(Tc.$);
