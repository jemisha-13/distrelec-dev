import { Facet, FacetValue } from '@spartacus/core';

export const minMaxSelection = (
  facet: Facet,
  boundary: 'min' | 'max',
  minSelect: HTMLSelectElement,
  maxSelect: HTMLSelectElement,
): void => {
  const minSelectIndex: number = minSelect.selectedIndex === 0 ? 1 : minSelect.selectedIndex;
  const maxSelectIndex: number = maxSelect.selectedIndex === 0 ? maxSelect.options.length - 1 : maxSelect.selectedIndex;

  if (boundary === 'min') {
    facet.minValue = minSelect.value;
    if (!facet.maxValue) {
      facet.maxValue = facet.values[facet.values.length - 1].code;
    }
  } else {
    facet.maxValue = maxSelect.value;
    if (!facet.minValue) {
      facet.minValue = facet.values[0].code;
    }
  }

  facet.values.forEach((filter, i) => {
    filter.checked = i >= minSelectIndex - 1 && i <= maxSelectIndex - 1;
  });

  Array.from((boundary === 'min' ? maxSelect : minSelect).options)
    .slice(1)
    .forEach((option, i) => {
      if (boundary === 'min') {
        option.disabled = i < minSelectIndex - 1;
      } else {
        option.disabled = i > maxSelectIndex - 1;
      }
    });
};

export const resetMinMaxSelection = (facet: Facet, resetCheckboxes = false, selects: HTMLSelectElement[]): void => {
  if (resetCheckboxes) {
    facet.values.forEach((option: FacetValue) => (option.checked = false));

    selects
      ?.filter((select) => select !== undefined)
      .forEach((select) => {
        Array.from(select.options)
          .slice(1)
          .forEach((option) => {
            option.disabled = false;
          });
      });
  }
  delete facet.minValue;
  delete facet.maxValue;
};
