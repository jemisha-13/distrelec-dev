import { Directive, ElementRef, Input, Renderer2 } from '@angular/core';
import { JsonLdScriptFactory } from '@spartacus/storefront';

/**
 * Customisation of the JsonLdDirective which will destroy the previous script tag when the schema is empty or updated
 */
@Directive({
  selector: '[appJsonLd]',
})
export class DistJsonLdDirective {
  /**
   * Writes the schema data to a json-ld script element.
   */
  @Input() set appJsonLd(schema: string | Record<string, unknown>) {
    this.generateJsonLdScript(schema);
  }

  private script?: HTMLScriptElement;

  constructor(
    protected renderer: Renderer2,
    protected jsonLdScriptFactory: JsonLdScriptFactory,
    protected element: ElementRef,
  ) {}

  /**
   * Attach the json-ld script tag to DOM with the schema data secured by encoding html tags (aka escaping)
   */
  protected generateJsonLdScript(schema: string | Record<string, unknown>): void {
    if (schema) {
      const content = this.jsonLdScriptFactory.escapeHtml(schema);
      if (!this.script) {
        this.script = this.createScript(content);
      } else if (content !== this.script.textContent) {
        this.renderer.setProperty(this.script, 'textContent', content);
      }
    } else {
      this.reset();
    }
  }

  private createScript(content: string): HTMLScriptElement {
    const script: HTMLScriptElement = this.renderer.createElement('script');
    script.type = 'application/ld+json';
    script.textContent = content;
    this.renderer.appendChild(this.element.nativeElement, script);
    return script;
  }

  private reset(): void {
    if (this.script) {
      this.renderer.removeChild(this.element.nativeElement, this.script);
      this.script = undefined;
    }
  }
}
