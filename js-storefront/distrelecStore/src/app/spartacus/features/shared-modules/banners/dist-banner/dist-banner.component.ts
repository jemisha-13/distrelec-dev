import { Component, ElementRef, Input, OnInit } from '@angular/core';
import { CmsBannerComponent } from '@spartacus/core';
import { CmsComponentData } from '@spartacus/storefront';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'app-dist-banner',
  templateUrl: './dist-banner.component.html',
  styleUrls: ['./dist-banner.component.scss'],
})
export class DistBannerComponent implements OnInit {
  @Input() componentData?: CmsBannerComponent;
  @Input() index = 0;

  data$: Observable<CmsBannerComponent>;
  faAngleRight = faAngleRight;
  isSbcProject: boolean;

  constructor(
    private component: CmsComponentData<CmsBannerComponent>,
    private elementRef: ElementRef,
  ) {}

  ngOnInit() {
    this.data$ = this.componentData ? of(this.componentData) : this.component.data$;
    this.isSbcProject = this.elementRef.nativeElement.parentNode.classList.contains('sbc-projects-components');
  }
}
