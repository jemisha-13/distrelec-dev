import { Component, Input } from '@angular/core';

import { CategoryPageData } from '@model/category.model';
import { DefaultImageService } from '@services/default-image.service';

interface CategoryData {
  name: string;
  url: string;
  thumbnailUrl?: string;
}

@Component({
  selector: 'app-category-thumbs',
  templateUrl: './category-thumbs.component.html',
  styleUrls: ['./category-thumbs.component.scss'],
})
export class CategoryThumbsComponent {
  @Input() categoryData: CategoryPageData;
  missingImgSrc = this.defaultImage.getDefaultImage();

  constructor(private defaultImage: DefaultImageService) {}

  get subCategories(): CategoryData[] {
    if (!this.categoryData?.subCategories) {
      return [];
    }

    return [...this.categoryData.subCategories]
      .sort((a, b) => a.name.localeCompare(b.name))
      .map(({ name, url, images }) => ({
        name,
        url,
        thumbnailUrl: images?.find(({ key }) => key === 'portrait_small')?.value.url,
      }));
  }
}
