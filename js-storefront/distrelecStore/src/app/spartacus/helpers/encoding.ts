// Helper functions for serializing and deserializing URLs

export function encode(value: string): string {
  // The main difference from default behaviour is that spaces should be encoded as + instead of %20
  // If there is a + character in the value, it should stay encoded as %2B
  return encodeURIComponent(value).replace(/%20/g, '+');
}

export function decode(value: string): string {
  return decodeURIComponent(value.replace(/\+/g, '%20').replace(/%(?![A-Fa-f0-9]{2})/g, '%25'));
}
