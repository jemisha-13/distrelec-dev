import { Injectable } from '@angular/core';
import { GlobalMessageService, HttpErrorHandler, OccEndpointsService, Priority } from '@spartacus/core';
import { MaintenancePageService } from '@services/maintenance-page.service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class ServiceUnavailableHandler extends HttpErrorHandler {
  responseStatus = 503; // Service Unavailable

  constructor(
    protected globalMessageService: GlobalMessageService,
    private maintenancePageRedirectService: MaintenancePageService,
    private occEndpointService: OccEndpointsService,
  ) {
    super(globalMessageService);
  }

  hasMatch(response: HttpErrorResponse): boolean {
    return super.hasMatch(response) && this.isOccEndpoint(response) && this.isNotMutableEndpoint(response);
  }

  handleError() {
    this.maintenancePageRedirectService.showMaintenanceIfActive();
  }

  getPriority(): Priority {
    return Priority.NORMAL;
  }

  private isOccEndpoint(response: HttpErrorResponse): boolean {
    return response.url.startsWith(this.occEndpointService.getBaseUrl());
  }

  private isNotMutableEndpoint(response: HttpErrorResponse): boolean {
    return !response.url.includes('/basesites/mutable');
  }
}
