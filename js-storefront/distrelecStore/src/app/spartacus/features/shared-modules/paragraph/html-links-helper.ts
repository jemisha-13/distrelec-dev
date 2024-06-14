export function containsParams(htmlContent: string): boolean {
  const regex = /<a [^>]*href=['"](.*?)['"][^>]*>/g;

  let match;
  while ((match = regex.exec(htmlContent)) !== null) {
    const href = match[1];
    if (href.includes('int_cid')) {
      return true;
    }
  }

  return false;
}
