/**
 * Check if page is being edited in Smartedit
 */

Tc.Utils.isEditedInSmartedit = function() {
    try {
        return $("iframe#ySmartEditFrame", parent.document).length > 0;
    }catch (e) {
        return false;
    }
};
