/***********************************************************************************
 *
 *    Plugin SelectBoxit
 *    -> http://gregfranko.com/jquery.selectBoxIt.js/
 *
 **********************************************************************************/

/**
 * Applies "SelectBoxit" plugin on element
 *
 * @param $field
 */
Tc.Utils.initSelectboxit = function ($field) {
    // After short delay, load selectboxit script
    setTimeout(function () {
        // Lazy Load SelectBoxIt Dropdown
        if(!Modernizr.isie7) {
            Modernizr.load([{
                load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
                complete: function () {
                    setTimeout(function () {
                        // Init selectboxit plugin
                        $field.selectBoxIt({
                            autoWidth: false,
                            isMobile: function() {
                                return false;
                            }
                        });
                    }, 200);
                }
            }]);
        }
    }, 200);
}
