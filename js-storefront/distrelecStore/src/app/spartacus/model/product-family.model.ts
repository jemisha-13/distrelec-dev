export interface FamilyBullet {
  key: number;
  value: string;
}

export interface ExternalSource {
  key: string;
  value: {
    format: string;
    url: string;
  };
}

export interface VideoRef {
  brightcoveVideoIdL: string;
  languages: string[];
  youtubeUrl: string;
}

export interface ProductFamilyData {
  code: string;
  familyApplications: string[];
  familyBullets: FamilyBullet[];
  familyCategoryCode: string;
  familyDatasheet: ExternalSource[];
  familyImage: ExternalSource[];
  familyManufacturerImage: ExternalSource[];
  familyMedia: ExternalSource[];
  familyVideo: VideoRef[];
  introText: string;
  name: string;
  nameEN: string;
  seoMetaDescription: string;
  seoMetaTitle: string;
  seoSections: any[];
  url: string;
}
