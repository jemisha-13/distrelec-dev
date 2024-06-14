export function IsCheckoutPages(page): boolean {
  if (page === 'CheckoutPageTemplate' || page === 'OrderConfirmationPageTemplate') {
    this.isSticky = false;
  }
  return page === 'CheckoutPageTemplate' || page === 'OrderConfirmationPageTemplate';
}
