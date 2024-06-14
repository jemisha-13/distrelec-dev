import { DefaultImageService } from '@services/default-image.service';

export default class MockDefaultImageService implements Partial<DefaultImageService> {
  getDefaultImageMedium(): string {
    return '/app/spartacus/assets/media/img/missing_landscape_medium.png';
  }
}
