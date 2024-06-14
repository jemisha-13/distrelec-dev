export const formatPrice = (price: string): string => {
  const splitDecimalPrice = price.split('.');
  const wholeNumber = splitDecimalPrice[0];

  let trailingDigitsAfterDecimal = splitDecimalPrice[1] ?? '00';

  // will return the number itself if it't >= 2 digits or if its for exmaple 1.0 it will add an extra 0 to be 1.00
  trailingDigitsAfterDecimal = trailingDigitsAfterDecimal.padEnd(2, '0');

  if (trailingDigitsAfterDecimal.length > 4) {
    trailingDigitsAfterDecimal = parseFloat(`0.${trailingDigitsAfterDecimal}`).toFixed(4).split('.')[1];
  }

  return `${wholeNumber}.${trailingDigitsAfterDecimal}`;
};
