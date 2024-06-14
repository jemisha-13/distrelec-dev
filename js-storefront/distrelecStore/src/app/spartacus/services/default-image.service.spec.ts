import { TestBed } from '@angular/core/testing';
import { SiteContextConfig } from '@spartacus/core';
import { DefaultImageService } from './default-image.service';

const ASSET_PATH = '/app/spartacus/assets/media/img';

describe('DefaultImageService', () => {
  let defaultImageService: DefaultImageService;
  let contextConfig: SiteContextConfig;

  beforeEach(() => {
    contextConfig = { context: { baseSite: [''] } } as SiteContextConfig;

    TestBed.configureTestingModule({
      providers: [DefaultImageService, { provide: SiteContextConfig, useValue: contextConfig }],
    });

    defaultImageService = TestBed.inject(DefaultImageService);
  });

  describe('Get default image', () => {
    it('Should return default image if context is null', () => {
      contextConfig.context = null;
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small.png`);
    });

    it('Should return default image if baseSite is null', () => {
      contextConfig.context.baseSite = [null];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small.png`);
    });

    it('Should return default image if baseSite is empty', () => {
      contextConfig.context.baseSite = [''];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small.png`);
    });
  });

  describe('Get localized image', () => {
    it('Should return elfa image if baseSite matches elfa', () => {
      contextConfig.context.baseSite = ['elfa'];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small-elfa.png`);
    });

    it('Should return elfa image if baseSite matches LV locale', () => {
      contextConfig.context.baseSite = ['LV'];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small-elfa.png`);
    });

    it('Should return elfa image if baseSite matches NO locale', () => {
      contextConfig.context.baseSite = ['NO'];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small-elfa.png`);
    });

    it('Should return elfa image if baseSite matches PL locale', () => {
      contextConfig.context.baseSite = ['PL'];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small-elfa.png`);
    });

    it('Should return elfa image if baseSite matches FI locale', () => {
      contextConfig.context.baseSite = ['FI'];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small-elfa.png`);
    });

    it('Should return elfa image if baseSite matches LT locale', () => {
      contextConfig.context.baseSite = ['LT'];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small-elfa.png`);
    });

    it('Should return elfa image if baseSite matches EE locale', () => {
      contextConfig.context.baseSite = ['EE'];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small-elfa.png`);
    });

    it('Should return elfa image if baseSite matches SE locale', () => {
      contextConfig.context.baseSite = ['SE'];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small-elfa.png`);
    });

    it('Should return elfa image if baseSite matches DK locale', () => {
      contextConfig.context.baseSite = ['DK'];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small-elfa.png`);
    });

    it('Should return default image if baseSite does not match elfa locale', () => {
      contextConfig.context.baseSite = ['cms'];
      expect(defaultImageService.getDefaultImage()).toBe(`${ASSET_PATH}/missing_landscape_small.png`);
    });
  });
});
