import { Component, ElementRef, Input, OnChanges, OnInit, Renderer2, ViewEncapsulation } from '@angular/core';
import { DistIcon, DistIconCategory, DistIconType } from '@model/icon.model';

export interface ISvgPath {
  d: string;
  fill: string;
}

@Component({
  selector: 'app-icon',
  templateUrl: './icon.component.html',
  styleUrls: ['./icon.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class DistIconComponent implements OnInit, OnChanges {
  @Input() iconName: string;
  @Input() iconType?: DistIconType = 'svg';
  @Input() iconCategory?: DistIconCategory = DistIconCategory.ICON;
  @Input() iconAltText?: string;
  @Input() icon?: DistIcon;
  @Input() class?: string;
  @Input() width?: string;
  @Input() height?: string;
  @Input() size?: 'sm' | 'md' | 'lg' | 'xl' | 'xxl';

  url: string;

  private mediaUrl = '/app/spartacus/assets/media/icons';

  constructor(
    private el: ElementRef,
    private renderer: Renderer2,
  ) {}

  ngOnInit() {
    this.url = `${this.mediaUrl}/${this.iconCategory}/${this.iconName}.${this.iconType}`;
  }

  ngOnChanges() {
    this.buildSvgElement();
  }

  private buildSvgElement(): void {
    if (!this.icon) {
      return;
    }

    const elements = this.icon.elements;
    const svg = this.renderer.createElement('svg', 'svg');
    this.renderer.setAttribute(svg, 'xmlns', 'http://www.w3.org/2000/svg');

    svg.setAttribute('viewBox', this.icon.viewbox);
    svg.setAttribute('fill', this.icon.colour);
    svg.setAttribute('width', this.width ?? this.icon.dimensions[0].toString());
    svg.setAttribute('height', this.height ?? this.icon.dimensions[1].toString());

    if (this.class || this.size) {
      svg.setAttribute('class', `${this.class ?? ''} ${this.size ?? ''}`);
    }

    elements?.forEach((element) => {
      if (this.isAndSvgPath(element.properties)) {
        const svgElement = this.createEmptySvgElement(element.tagName);
        this.mapThroughPathProperties(element, svg, svgElement);
      } else if (this.isGroupOfPaths(element)) {
        element.children.forEach((path: any) => {
          const svgElement = this.createEmptySvgElement(path.tagName);
          this.mapThroughPathProperties(path, svg, svgElement);
        });
      }
    });

    this.renderer.setProperty(this.el.nativeElement, 'innerHTML', '');
    this.renderer.appendChild(this.el.nativeElement, svg);
  }

  private isAndSvgPath(property): property is ISvgPath {
    return 'd' in property && 'fill' in property;
  }

  private isGroupOfPaths(element): boolean {
    return element.tagName === 'g';
  }

  private mapThroughPathProperties(path, svg, svgElement): void {
    for (const prop in path.properties) {
      if (prop === 'fill' && this.class) {
        svgElement.setAttribute('fill', 'currentColor');
      } else {
        svgElement.setAttribute(prop, path.properties[prop]);
      }
    }
    svg.appendChild(svgElement);
  }

  private createEmptySvgElement(tagName): SVGAElement {
    return this.renderer.createElement(tagName, 'svg');
  }
}
