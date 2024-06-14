/*
 *
 *    Gesammt Breite inklusive padding
 *
 *
 * */

Tc.Utils.widthAll = function ($elem) {
	return $elem.width() + parseInt($elem.css('paddingLeft')) + parseInt($elem.css('paddingRight'));
};

Tc.Utils.heightAll = function () {
    var $t = $(this);
    return $t.height() + parseInt($t.css('paddingBottom')) + parseInt($t.css('paddingTop'));
};