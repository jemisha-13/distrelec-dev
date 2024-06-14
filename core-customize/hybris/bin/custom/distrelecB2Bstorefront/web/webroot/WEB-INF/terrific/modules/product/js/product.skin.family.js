(function($) {

    Tc.Module.Product.Family = function (parent) {

        if(sessionStorage.getItem('back-to-pfp') !== null) {
            sessionStorage.removeItem('back-to-pfp');
        }

        this.on = function (callback) {

            var $ctx = this.$ctx,
                self = this;

            $('.scroll-to').click(function(){
               var scrollLocation = $(this).data('scroll');

               if(scrollLocation === 'top') {
                   $('html, body').animate(({
                       scrollTop: 0
                   }), 'slow');
               } else {
                   $('html, body').animate(({
                       scrollTop: $("#" + scrollLocation).offset().top - 90
                   }), 'slow');
               }

            });

            $('.teaser-link, .plp-filter-controllbar__apply-filter:not(.disabled)').click(function(){
               sessionStorage.setItem('back-to-pfp', true);
            });

            parent.on(callback);
        };

    };

})(Tc.$);
