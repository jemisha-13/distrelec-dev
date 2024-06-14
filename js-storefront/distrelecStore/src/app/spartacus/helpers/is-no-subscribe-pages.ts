export function IsNoSubscribePages(page): boolean {
  return (
    page === 'RegisterPageTemplate' ||
    page === 'AccountPageTemplate' ||
    page === 'CartPageTemplate' ||
    page === 'LoginPageTemplate'
  );
}
