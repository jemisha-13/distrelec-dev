(function($) {

    Tc.Module.CheckoutProceed = Tc.Module.extend({

        on: function(callback) {

            var warningMessage = $(".mod-warning-component .mod-global-messages").length,
                scrollValue,
                scrollTopValue;

            if (warningMessage === 0 ) {
                scrollValue = 72;
                scrollTopValue = 60;
            } else if (warningMessage === 1) {
                scrollValue = 138;
                scrollTopValue = 80;
            } else if (warningMessage === 2) {
                scrollValue = 200;
                scrollTopValue = 100;
            }

            if($(window).innerWidth() >= 1113) {

                $(document).scroll(function() {
                    var top = $(window).scrollTop() - scrollTopValue;
                    var bottom = $(document).height() - top - 1242;
                    var stickyTotals = $('.cart-side-sticky');
                    var styleTop;

                    var cartHolderHeight = $('.cart-holder').height(),
                        cartSideStickyHeight = $('.cart-side-sticky--stuck').height(),
                        topLimit = (cartHolderHeight - cartSideStickyHeight);

                    if (warningMessage === 0) {
                        styleTop = top;
                    }
                    else if(warningMessage === 1) {
                        styleTop = top - 65;
                    } else if (warningMessage === 2) {
                        styleTop = top - 130;
                    }

                    if (top >= scrollValue && bottom > 0) {
                        stickyTotals.addClass('cart-side-sticky--stuck');

                        if ( styleTop < topLimit ) {
                            stickyTotals.css('top', styleTop);
                        }

                    } else if (top < scrollValue && bottom > 0) {
                        stickyTotals.removeClass('cart-side-sticky--stuck');
                        stickyTotals.removeAttr('style');
                    }

                });

            }

            callback();
        }

    });

})(Tc.$);
