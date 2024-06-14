import { Component, Input, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-skeleton-loader',
  templateUrl: './skeleton-loader.component.html',
  styleUrls: ['./skeleton-loader.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SkeletonLoaderComponent {
  @Input() pageName: string;
  @Input() isSkeletonLoading: boolean;

  constructor() {}
}
