(function ($) {

    Tc.Module.MetahdItem.NavButton = function (parent) {
        this.on = function (callback) {
            var self = this,
                $ctx = this.$ctx;

            $('.nav-button-holder').click(function (){
                $(this).toggleClass('active');
                $('.mod-maincategorynav').toggleClass('mobile-active');
                $("#js-products-dropdown").toggleClass('mobile-active');
                $('body').toggleClass('menu-active');
                $('.sticky-level-1').toggleClass('mobile-active');
            });

            if ( $(window).width() < 767) {

                var windowHeight = $(window).height();
                var magic = windowHeight - 138;

                $('.mod-maincategorynav').css('height', magic);


            }

            parent.on(callback);
        };

    };

})(Tc.$);
