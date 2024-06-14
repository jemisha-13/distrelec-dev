(function($) {
    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Mainmanufacturernav
     * @extends Tc.Module
     */
    Tc.Module.Mainmanufacturernav = Tc.Module.extend({
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

            this.flyoutTimeout;
            this.flyoutTimeoutDuration = 0;
            this.flyoutLeaveTimeout;

            this.$l1 = $(".l1", $ctx);
            this.$e1 = $(".e1", $ctx);

            // Flyout can be disabled per Customer
            this.enableFlyout = this.$l1.data("enable-flyout");
        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function(callback) {
            var isOCI = ($('.OCIPresent').length > 0);

            if (isOCI) {
                $(".mainnav-manufacturers-list").parents('.e1').addClass('oci-manufacturer');
            }

            if (this.enableFlyout) {
                var self = this,
                    $body = $("body");

                self.$l1.on({
                    click: function(e) {
                        e.stopPropagation();
                        clearTimeout(self.flyoutLeaveTimeout);

                        self.flyoutTimeout = setTimeout(function() {
                            $body.addClass("l1-flyout");
                        }, self.flyoutTimeoutDuration);
                    }
                });

                self.flyoutBind();
            }

            callback();
        },

        callForManufacturers: function() {
            function decodeHtml(html) {
                html = JSON.stringify(html);
                var txt = document.createElement("textarea");
                txt.innerHTML = html;
                return JSON.parse(txt.value);
            }

            var manufacturerMenuDataVm = new Vue({
                el: ".mainnav-manufacturers-list",
                data: {
                    manufacturerMenuListData: []
                },
                created: function() {
                    var self = this,
                        ajaxUrl = "/manufacturerMenu";

                    axios.get(ajaxUrl).then(function(response) {
                        self.manufacturerMenuListData = decodeHtml(
                            response.data.manufacturerData.manufacturerlist
                        );
                        $(".ajax-product-loader").hide();
                    });

                },
                methods: {
                    scollToKey: function(e) {
                        e.preventDefault();
                        var _ele = e.target;
                        var letter = _ele.getAttribute("href");
                        var target = $(".man-starts-with").filter(function() {
                            return (
                                $.trim($(this).text())
                                    .toLowerCase()
                                    .indexOf(letter) === 0
                            );
                        });

                        $(".man-wrap").animate(
                            {
                                scrollTop:
                                    $(".man-wrap").scrollTop() + $(target).position().top - 75
                            },
                            250
                        );
                    }
                }
            });
        },

        flyoutBind: function() {
            var self = this,
                manufactureNavClickedTarget = false,
                manufactureNavClickedCount = 0;

            $(this.$e1).hoverIntent(function(e) {

                var menuL1Title = $(this)
                    .find(".level1nodeTitle")
                    .data("entitle");

                if (
                    menuL1Title === "manufacturers" &&
                    manufactureNavClickedCount === 0
                ) {
                    manufactureNavClickedCount++;
                    self.callForManufacturers();
                }

                if(document.getElementById("man-feat") !== null) {
                    var count = document.getElementById("man-feat").getElementsByTagName("li").length;

                    for (var i = 0; i < count; i++) {
                        var getImage= document.getElementById("man-feat")
                            .getElementsByTagName("li")[i]
                            .children[0]
                            .getElementsByTagName("img")[0];
                        getImage.setAttribute("src", getImage.getAttribute('data-src'));
                    }
                }

                manufactureNavClickedTarget =
                    $(e.target).hasClass("man-select") ||
                    $(e.target).hasClass("man-nav");

                if (!manufactureNavClickedTarget) {
                    $(this)
                        .find(".l2")
                        .toggleClass("hidden");
                }

                $('.flyout-settings').addClass('hidden');
            }, function(){
                $(this)
                    .find(".l2")
                    .addClass("hidden");
            });

            // Level 1 Elements Events
            self.$e1.on({
                click: function(e) {

                    var menuL1Title = $(this)
                        .find(".level1nodeTitle")
                        .data("entitle");

                    if (
                        menuL1Title === "manufacturers" &&
                        manufactureNavClickedCount === 0
                    ) {
                        manufactureNavClickedCount++;
                        self.callForManufacturers();
                    }

                    // Display images for non-oci header only
                    if(document.getElementById("man-feat") !== null) {
                        var count = document.getElementById("man-feat").getElementsByTagName("li").length;

                        for (var i = 0; i < count; i++) {
                            var getImage= document.getElementById("man-feat")
                                .getElementsByTagName("li")[i]
                                .children[0]
                                .getElementsByTagName("img")[0];
                            getImage.setAttribute("src", getImage.getAttribute('data-src'));
                        }
                    }

                    manufactureNavClickedTarget =
                        $(e.target).hasClass("man-select") ||
                        $(e.target).hasClass("man-nav");
                    if (!manufactureNavClickedTarget) {
                        $(this)
                            .find(".l2")
                            .toggleClass("hidden");
                    }
                    $('.flyout-settings').addClass('hidden');
                }
            });
        },
    });
})(Tc.$);
