(function($) {
  /**
   * Productlist Skin DetailPage implementation for the module Productlist.
   * All products are loaded on page load. Availability and toggle states are requested for all products and
   * show more only makes the hidden products visible
   *
   */
  Tc.Module.Productlist.SearchTabular = function(parent) {
    this.on = function(callback) {
      this.sandbox.subscribe("toolItems", this);

      var $ctx = this.$ctx,
        self = this;

      this.productCodes = []; // placeholder for all product-codes in list
      var $wrapperTop = $(".wrapperScrollTop");
      var $wrapperTable = $(".wrapperScrollTable");
      var $headcol = $wrapperTable.find(".tr_header .headcol");
      var $headcolStuck = $wrapperTable.find(
        ".tabular-search-table .tr_header .headcol"
      );
      var $attrHeaderCells = $wrapperTable.find(".tr_header .long");
      var $window = $(window);

      this.requestProductToggleStates = $.proxy(
        this,
        "requestProductToggleStates"
      );
      this.stickyOffset = 53;

      adjustContentWidth();

      // Set and save initial "left" attribute on attribute header cells
      $attrHeaderCells.each(function() {
        $(this)
          .data("left", $(this).offset().left)
          .css("left", $(this).offset().left);
      });

      $("body").addClass("no-scroll-x");

      //Emulate the movement of the top scroll, based on the bottom scroll
      // For performance reasons, limit to 50 updates per second
      self.scrollTimer = true;
      self.wrapperScroll = false;

      $window.scroll(function() {
        self.wrapperScroll = false;
      });

      $window.resize(function() {
        adjustContentWidth();
      });

      function adjustContentWidth() {
        if ($(".tr_header .long").length > 1) {
          var totalwidth1 =
            parseInt(
              $(".wrapperScrollTable .divScrollTable .tabular-search-table")
                .css("width")
                .split("px")[0]
            ) +
            parseInt(
              $(".wrapperScrollTable .divScrollTable")
                .css("margin-left")
                .split("px")[0]
            );
          $(".wrapperScrollTop .divScrollTop").css("width", totalwidth1);

          var totalwidth2 = parseInt(
            $(".wrapperScrollTable")
              .css("width")
              .split("px")[0]
          );
        }
      }

      // Stripped rows
      $(".tr_content .headcol:odd").addClass("greyBg");
      $(".tr_content .headcol:even").addClass("whiteBg");

      //Add to cart buttons
      this.$btnCart = this.$ctx.find(".btn-cart");
      self.$btnCart.off("click").on("click", function(ev) {
        ev.preventDefault();
        ev.stopPropagation();

        if(digitalData.page.pageInfo.search !== undefined) {
          sessionStorage.setItem('userCartJourney', 'false');
          digitalData.page.pageInfo.search.addtocart = false;
        }

        self.onAddToCartClick(ev);
      });

      // Sorting clicks
      this.$arrowUp = this.$ctx.find(".arrow-up");
      this.$arrowDown = this.$ctx.find(".arrow-down");

      this.$arrowUp.off("click").on("click", function(ev) {
        ev.preventDefault();
        var attributeClicked = ev.delegateTarget.parentElement.getAttribute(
          "id"
        );
        self.buildSortingURL(attributeClicked, "asc");
      });

      this.$arrowDown.off("click").on("click", function(ev) {
        ev.preventDefault();
        var attributeClicked = ev.delegateTarget.parentElement.getAttribute(
          "id"
        );
        self.buildSortingURL(attributeClicked, "desc");
      });

      // availability Templates
      // doT templates not need on FR
      try {
        this.tmplStockLevel = doT.template($("#tmpl-stock_level", $ctx).html());
        this.tmplStockLevelPickupRow = doT.template(
          $("#tmpl-stock_level_pickup_row", $ctx).html()
        );
      } catch (e) {
        return e;
      }

      this.tmplStockLevelPickupHeader = $(
        "#tmpl-stock_level_pickup_header",
        $ctx
      ).html();

      //left and right key detection
      self.keyLeftorRight();

      //left and right key detection
      self.sortingDetection();

      // Load the Availability of every product in the tabular table
      self.getAvailable(0);

      //If no scroll bar, the table shifts up. (there is no empty space)
      if ($(".divScrollTop").width() <= $(".wrapperScrollTop").width() + 20) {
        $(".skin-productlist-search-tabular")
          .css("margin-top", "0px")
          .removeClass("scrolly");
        $(".wrapperScrollTop").css("visibility", "hidden");
        this.stickyOffset = 53;
      } else {
        $(".skin-productlist-search-tabular")
          .css("margin-top", "12px")
          .addClass("scrolly");
        $(".wrapperScrollTop").css("visibility", "visible");
        this.stickyOffset = 65;
      }

      // if label present, move image down 16 pixels
      $(".mod-product-label")
        .parent()
        .siblings("a")
        .find(".image-container")
        .css("margin-top", "+=16");

      $(".skin-layout-search .numeric .ipt").click(function() {
        $(this).val(""); // Empty input field on click
      });

      //

      parent.on(callback);
    };

    this.onAddToCartClick = function(ev) {
      ev.preventDefault();

      this.$btnCart.addClass("active");
      var productCode = this.$btnCart.data("product-code");

      var $target = $(ev.target),
        $product = $target.closest(".plp-filter-products__product"),
        queryParam = "";
      $target.closest(".btn-cart").addClass("item-added");

      // if toolsitem is placed on a mod-product, we take the query param from the url to send it to the backend for FactFinder Tracking
      if ($product.length === 1) {
        var url = Tc.Utils.splitUrl(document.URL);
        if (url.get !== undefined) {
          queryParam = url.get.q;
        }
      }

      // If the toolsitem cart is not placed on a mod-product, than get it from mod-hero-teaser-item
      if ($product.length === 0) {
        $product = $target.closest(".mod-hero-teaser-item");
      }

      // If the toolsitem cart is not placed on a mod-product or mod-hero-teaser-item, get it from mod-carpet-item
      if ($product.length === 0) {
        $product = $target.closest(".mod-carpet-item");
      }

      // If the toolsitem cart is not placed on a mod-product or mod-hero-teaser-item or mod-carpet-item, get it from mod-carpet-new-item
      if ($product.length === 0) {
        $product = $target.closest(".mod-carpet-new-item");
      }

      // If the toolsitem cart is not placed on a mod-product or mod-hero-teaser-item or mod-carpet-item or mod-carpet-new-item, get it from mod-carousel-teaser
      if ($product.length === 0) {
        $product = $target.closest(".mod-carousel-teaser-item");
      }

      this.triggerCart($product, queryParam);
    };

    this.triggerCart = function($product, queryParam) {
      var productCode = $product
        .find(".hidden-product-code")
        .val()
        .replace(/\D/g, "");
      var productPrice = $product.find(".hidden-price").val();
      var productQty = $product
        .find(".mod-numeric-stepper")
        .find(".ipt")
        .val();
      var productValue = parseFloat(productPrice * productQty).toFixed(2);
      var currency = digitalData.page.pageInfo.currency;
      var dyQuantity = parseInt(productQty);
      var origPosition = $product.find(".hidden-origPosition").val();
      var query = "";
      var searchQuery = $product.find(".hidden-searchQuery").val();
      if (
        typeof searchQuery != "undefined" &&
        (searchQuery === "" || searchQuery === "*")
      ) {
        query = $product.find(".hidden-categoryCodePath").val();
      } else {
        query = searchQuery;
      }
      var pos = $product.find(".hidden-pos").val();
      var origPageSize = $product.find(".hidden-origPageSize").val();
      var productFamily = $product.find(".hidden-productFamily").val();
      var productCampaign = $product.find(".hidden-productCampaign").val();
      if (productFamily === undefined || productFamily === null) {
        productFamily = false;
      }
      // Trigger Cart API to add to cart
      $(document).trigger("cart", {
        actionIdentifier: "toolsitemAddToCart",
        type: "add",
        productCodePost: productCode,
        qty: productQty, // backend magic: we send 0 and the backend automatically set it to the minimum quantity
        origPos: origPosition,
        pos: pos,
        origPageSize: origPageSize,
        prodprice: productPrice,
        queryParam: query,
        isProductFamily: productFamily,
        productCampaign: productCampaign
      });
    };

    this.keyLeftorRight = function(ev) {
      $(document).keydown(function(ev) {
        switch (ev.which) {
          case 37: // left
            var leftPos = $(".wrapperScrollTable").scrollLeft();
            $(".wrapperScrollTable").animate(
              {
                scrollLeft: leftPos - 150
              },
              200
            );

            break;

          case 39: // right
            var leftPos2 = $(".wrapperScrollTable").scrollLeft();
            $(".wrapperScrollTable").animate(
              {
                scrollLeft: leftPos2 + 150
              },
              200
            );
            break;

          default:
            return; // exit this handler for other keys
        }
        ev.preventDefault(); // prevent the default action (scroll / move caret)
      });
    };

    // function to highlight the current arrow (up/down) of the current filter
    this.sortingDetection = function() {
      var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split("&"),
        sParameterName,
        i;

      for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split("=");

        if (sParameterName[0] === "sort") {
          var filter =
            sParameterName[1] === undefined ? true : sParameterName[1];
          var filterName = filter.split(":")[0];
          var filterDirection = filter.split(":")[1];

          var elementDetected = document.getElementById(
            filterName.replace(/\+/g, " ")
          );

          if (filterDirection === "asc") {
            $(elementDetected)
              .find(".arrow-up")
              .addClass("arrow-up-selected");
          } else {
            $(elementDetected)
              .find(".arrow-down")
              .addClass("arrow-down-selected");
          }
        }
      }
    };

    this.buildSortingURL = function(attribute, direction) {
      var selectedValue = [attribute, direction];
      var urlObj = Tc.Utils.splitUrl(document.URL);

      // Prepare "sort" get param
      if (urlObj.get === undefined) {
        urlObj.get = { sort: "" };
      } else if (urlObj.get.q === undefined) {
        urlObj.get.sort = "";
      }

      // Add "sort" and "page" get parameter
      urlObj.get.sort = selectedValue[0] + ":" + selectedValue[1];
      urlObj.get.page = 1; // on sort event, user should be directed to first result-page
      if ($(".wrapperScrollTable").scrollLeft()) {
        urlObj.get.scroll = $(".wrapperScrollTable").scrollLeft();
      } else if (typeof urlObj.get.scroll !== "undefined") {
        delete urlObj.get.scroll;
      }

      location.href = Tc.Utils.joinUrl(urlObj);
    };

    /**
     *
     * @method getAvailable tabular view
     *
     * @param start
     */
    this.getAvailable = function(start) {
      var self = this,
        $ctx = this.$ctx,
        $listItems = $ctx.find(".mod-shipping-information"),
        $hiddenCode = $ctx.find(".hidden-product-code"),
        productCodes = this.productCodes,
        productCodesQuantities = [],
        productNum = [],
        i,
        len = $hiddenCode.length;

      // Gather product data for each cart list item
      for (i = start; i < len; i += 1) {
        productNum.push(i);
        this.productCodes.push($ctx.find(".hidden-product-code")[i].value);
      }

      // Perform availability request product list tabular
      $.ajax({
        url: "/availability",
        dataType: "json",
        data: {
          productCodes: this.productCodes.join(","),
          detailInfo: true
        },
        contentType: "application/json",
        success: function(data) {
          var items = data.availabilityData.products,
            item,
            item2,
            $listItem;

          $.each(productCodes, function(i) {
            var count = 0;
            var found = false;
            for (var item in items) {
              if (items[count][this.toString()] !== undefined && !found) {
                item2 = items[count][this.toString()];
                found = true;
              }

              count++;
            }

            $listItem = $listItems.eq(productNum[i]);
            var productCode = this.toString();
            self.getPopover(item2, $listItem, productCode);
          });

          $ctx.find(".loading").removeClass("loading");
        }
      });
    };

    /**
     *
     * @getPopover product list search tabular
     *
     * @param item
     * @param $listItem
     */
    this.getPopover = function(item, $listItem, productCode) {
      var self = this,
        stockLevelPickup = "",
        countLines = 0,
        $infoStock = $(".info-stock-" + productCode),
        $plpProductItem = $(
          ".plp-filter-products__product.productCode-" + productCode
        ),
        statusCode = parseInt(
          $(".leadTimeFlyout-" + productCode).data("status-code")
        );

      if (isNaN(statusCode)) {
        statusCode = 0;
      }

      if (item === undefined) {
        return true;
      }

      $.each(item.stockLevels, function(index, stockLevel) {
        // In Stock
        if (stockLevel.available > 0) {
          // short
          var $inStock = $infoStock.find(".inStockText");

          if ($inStock.data("instock-text") !== undefined) {
            var inStockText = $inStock
              .data("instock-text")
              .replace("{0}", stockLevel.available);
            $inStock.html(inStockText);
          }

          // long (flyout)
          var $inStockLong = $(".leadTimeFlyout-" + productCode).find(
            ".inStockLong"
          );
          if ($inStockLong.data("instock-text") !== undefined) {
            var inStockTextLong = $inStockLong
              .data("instock-text")
              .replace("{0}", stockLevel.available);
            $inStockLong.html(inStockTextLong);
          }

          if (stockLevel.available > 0) {
            $infoStock.find(".instock").removeClass("hidden");
            $infoStock.find(".availableToOrder").addClass("hidden");
            $infoStock
              .find(".sales-status--status-text-40-45")
              .addClass("hidden");
          }
          else {
            $infoStock.find(".availableToOrder").removeClass("hidden");
          }

          if (statusCode >= 40 && statusCode <= 45) {
            $infoStock.find(".availableToOrder").addClass("hidden");
            $infoStock
              .find(".sales-status--status-text-40-45")
              .removeClass("hidden");

            if (item.stockLevelTotal > 0) {
              $infoStock
                .find(".sales-status--status-text-40-45 .instock-41-45")
                .removeClass("hidden");
            } else {
              $infoStock
                .find(".sales-status--status-text-40-45 .outofstock-41-45")
                .removeClass("hidden");
              $plpProductItem
                .find(".plp-filter-products__cart-cta .btn-cart")
                .addClass("disabled")
                .attr("disabled", "disabled");
            }
          }

          countLines++;
        }
        // Further additional product list (Only in CH and only for warehouseCode = 7371) tabular
        var warehouseCdcCode = $(".leadTimeFlyout").data("warehouse-cdc");

        // further Stock

        if (stockLevel.available > 0) {
          // short
          var $further = $(".info-stock-" + productCode).find(".further");
          $(".info-stock-" + productCode)
            .find(".further")
            .removeClass("hidden");

          if ($further.data("further-text") !== undefined) {
            var furtherText = $further
              .data("further-text")
              .replace("{0}", stockLevel.available)
              .replace("{1}", stockLevel.deliveryTime.split(" ")[0]);
            $further.append(
              "<div class='further-text'>" + furtherText + "</div>"
            );
          }

          // long (flyout)
          var $furtherLong = $(".leadTimeFlyout-" + productCode).find(
            ".furtherLong"
          );

          if ($furtherLong.data("further-text") !== undefined) {
            var furtherLongText = $furtherLong
              .data("further-text")
              .replace("{0}", stockLevel.available)
              .replace("{1}", stockLevel.deliveryTime.split(" ")[0]);
            $furtherLong.html(furtherLongText);
            countLines++;
          }
        }

        // more stock available
        // more stock available in X weeks - tabular
        if (
          stockLevel.leadTime !== undefined &&
          stockLevel.leadTime > 0 &&
          statusCode < 40
        ) {
          var $moreStockAvailable = $(".leadTimeFlyout-" + productCode).find(
            ".moreStockAvailable"
          );

          if ($moreStockAvailable.data("morestock-text") !== undefined) {
            var moreStockAvailableText = $moreStockAvailable
              .data("morestock-text")
              .replace("{0}", stockLevel.leadTime);
            $moreStockAvailable.html(moreStockAvailableText);
          }
        }

        //More in [] week(s) --> More stock available in [ ] week(s) (In CH display this when any of the above conditions equal 0 instead) tabular
        if (
          countLines < 3 &&
          stockLevel.leadTime !== undefined &&
          stockLevel.leadTime > 0 &&
          statusCode < 40
        ) {
          var $moreStockAvailablePDP = $infoStock.find(
            ".moreStockAvailableText"
          );
          $(".info-stock")
            .find(".moreStockAvailable")
            .removeClass("hidden");

          if (
            $moreStockAvailablePDP.data("morestockavailable-text") !==
            undefined
          ) {
            var moreStockAvailableTextPDP = $moreStockAvailablePDP
              .data("morestockavailable-text")
              .replace("{0}", stockLevel.leadTime);
            $moreStockAvailablePDP.html(moreStockAvailableTextPDP);
          }
        }
      });

      // Pick up
      // For shops, display availability if
      // 1) there is an available quantity in _any_ warehouse, regardless of sales status, or
      // 2) sales status is < 40, regardless of available quantities
      if (
        item.stockLevelPickup !== undefined &&
        item.stockLevelPickup.length > 0
      ) {
        $.each(item.stockLevelPickup, function(index, stockLevelPickup) {
          if (item.stockLevelTotal > 0 || statusCode < 40) {
            var $pickUp = $infoStock.find(".pickupInStoreText");
            $infoStock.find(".pickup").removeClass("hidden");
            if ($pickUp.data("pickup-text") !== undefined) {
              var pickupText = $pickUp
                .data("pickup-text")
                .replace("{0}", stockLevelPickup.stockLevel);
              $pickUp.html(pickupText);
            }

            countLines++;

            // long (flyout) tabular
            var $pickUpLong = $(".leadTimeFlyout-" + productCode).find(
              ".pickupLong"
            );
            if ($pickUpLong.data("pickup-long-text") !== undefined) {
              var pickpupLongText = $pickUpLong
                .data("pickup-long-text")
                .replace("{0}", stockLevelPickup.stockLevel);
              $pickUpLong.html(pickpupLongText);
            }
          }
        });
      }
    };

    // this.after tabular
    this.after = function() {
      if (
        typeof digitalData !== "undefined" &&
        digitalData &&
        typeof digitalData.cart !== "undefined" &&
        digitalData.cart &&
        typeof digitalData.cart.item !== "undefined" &&
        digitalData.cart.item
      ) {
        $.each(digitalData.cart.item, function(index, item) {
          $(
            ".productCode-" + item.productInfo.productID + " .btn-cart"
          ).addClass("item-added");
        });
      }

      var urlObj = Tc.Utils.splitUrl(document.URL);
      if (
        typeof urlObj.get !== "undefined" &&
        typeof urlObj.get.scroll !== "undefined" &&
        !isNaN(parseInt(urlObj.get.scroll))
      ) {
        $(".wrapperScrollTable").scrollLeft(parseInt(urlObj.get.scroll));
      }

      // Sticky header via jQuery Plugin "waypoints"
      this.$ctx.find(".wrapperScrollTable").waypoint("sticky", {
        handler: function(direction) {
          $(".tr_header, .wrapperScrollTop").toggleClass(
            "-stuck",
            direction == "down"
          );
        },
        offset: this.stickyOffset
      });
      $(".skin-productlist-pagination-product-list-pag").waypoint("sticky", {
        handler: function(direction) {
          $(".tr_header td, .wrapperScrollTop").toggleClass("hidden");
        },
        offset: this.stickyOffset
      });
    };
  };
})(Tc.$);
