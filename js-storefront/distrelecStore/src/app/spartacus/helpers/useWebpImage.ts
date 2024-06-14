import { Occ } from '@spartacus/core';

export function UseWebpImage(format: string, productImageData?: Occ.Image[], productImage?: Occ.Image): string {
  if (productImageData && productImage) {
    let productImageWebp: Occ.Image;
    const webpImg = productImageData.find((img) => img.format === format);
    webpImg ? (productImageWebp = { ...productImage, url: webpImg.url }) : (productImageWebp = { ...productImage });
    return productImageWebp.url;
  } else {
    return '/app/spartacus/assets/media/img/missing_landscape_small.png';
  }
}
