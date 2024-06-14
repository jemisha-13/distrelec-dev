import { createNgModule, Injectable, Injector } from '@angular/core';
import { TemplateConfig } from '@features/pages/page-layout/template-config';

@Injectable({
  providedIn: 'root',
})
export class PageTemplateService {
  private loadedTemplates = new Map<string, any>();

  constructor(
    protected config: TemplateConfig,
    protected injector: Injector,
  ) {}

  async getTemplateComponent(templateName: string): Promise<any> {
    try {
      const templateConfig = this.config?.layoutTemplates?.[templateName];
      if (!templateConfig?.component) {
        return null;
      }

      if (!templateConfig.lazy) {
        return templateConfig.component;
      }

      if (this.loadedTemplates.has(templateName)) {
        return this.loadedTemplates.get(templateName);
      }

      // Register the module in case of dynamic providers or components
      const module = await templateConfig.module();
      createNgModule(module, this.injector);

      const component = await templateConfig.component();
      this.loadedTemplates.set(templateName, component);
      return component;
    } catch (e) {
      console.error(e);
      return null;
    }
  }
}
