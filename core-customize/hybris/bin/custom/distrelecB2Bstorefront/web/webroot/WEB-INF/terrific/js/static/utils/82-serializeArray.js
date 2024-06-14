/**
 * Gets serialized array data but with included unchecked checkboxes
 *
 * @param $form
 */

Tc.Utils.serializeArray = function ($form) {
    var data = $form.serializeArray();

    $('input[type=checkbox]', $form).map(function() {
        if (!this.checked) {
            data.push({
                name: this.name,
                value: "false"
            });
        }
    });

    return data;
}
