import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-article-tooltip',
  templateUrl: './article-tooltip.component.html',
  styleUrls: ['./article-tooltip.component.scss'],
})
export class ArticleTooltipComponent {
  @Input() copiedText: string;

  constructor() {}
}
