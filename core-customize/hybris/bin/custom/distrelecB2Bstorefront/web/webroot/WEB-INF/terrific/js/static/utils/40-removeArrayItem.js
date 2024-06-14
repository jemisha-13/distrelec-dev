
/**
 * Removes an item from an array
 *
 *  @param object {object|array}
 *  return {object|array}
 */

Tc.Utils.removeArrayItem = function (array, num) {
	var newArray = [];

	array[num] = null;

	$.each(array, function (i) {
		if (array[i] !== null) {
			newArray.push(array[i]);
		}
	});

	return newArray;
};

