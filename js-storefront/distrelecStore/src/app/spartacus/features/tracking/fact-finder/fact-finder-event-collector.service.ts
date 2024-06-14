import { Injectable } from '@angular/core';
import { CxEvent, OccEndpointsService } from '@spartacus/core';
import { TmsCollector, TmsCollectorConfig, WindowObject } from '@spartacus/tracking/tms/core';
import { HttpClient } from '@angular/common/http';
import { FactFinderEvent } from '@features/tracking/events/fact-finder-event';
import { SearchExperienceService } from '@features/pages/product/core/services/search-experience.service';

@Injectable({
  providedIn: 'root',
})
export class FactFinderEventCollectorService implements TmsCollector {
  constructor(
    private http: HttpClient,
    private occEndpointsService: OccEndpointsService,
    private searchExperienceService: SearchExperienceService,
  ) {}

  init(config: any, windowObject: WindowObject): void {
    // No initialization needed
  }

  pushEvent<T extends CxEvent>(config: TmsCollectorConfig, windowObject: WindowObject, event: FactFinderEvent): void {
    if (this.searchExperienceService.isFusion()) {
      return;
    }

    const trackUrl = this.occEndpointsService.buildUrl('/fftrack', { queryParams: event });
    this.http.get<string>(trackUrl).subscribe();
  }
}
