export const MockWindowRef = {
  nativeWindow: {
    location: { origin: 'https://distrelec-ch.local:4200', href: 'https://distrelec-ch.local:4200', pathname: "", search: "" },
  },
  isBrowser(): boolean {
    return true;
  },
};
