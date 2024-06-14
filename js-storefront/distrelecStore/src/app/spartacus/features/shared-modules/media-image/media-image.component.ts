import { Component, Input, OnInit } from '@angular/core';
import { CmsService, Page } from '@spartacus/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
  selector: 'app-media-image',
  templateUrl: './media-image.component.html',
  styleUrls: ['./media-image.component.scss'],
})
export class MediaImageComponent implements OnInit {
  @Input() mediaData;
  @Input() priority: string;
  containsWidthAndHeight = true;
  loadingPriority = false;
  isCategoryPage$: Observable<boolean> = this.cmsService.getCurrentPage().pipe(
    filter(Boolean),
    map((pageData: Page) => pageData.template === 'CategoryPageTemplate'),
  );

  constructor(private cmsService: CmsService) {}

  ngOnInit() {
    this.containsWidthAndHeight = this.mediaData.width && this.mediaData.height;
    if (this.priority === 'true') {
      this.loadingPriority = true;
    }
  }
}
