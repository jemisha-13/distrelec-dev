import { Component } from '@angular/core';
import { faList } from '@fortawesome/free-solid-svg-icons';
import { DistBreakpointService } from '@services/breakpoint.service';
import { UserIdService } from '@spartacus/core';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-header-options',
  templateUrl: './options.component.html',
  styleUrls: ['./options.component.scss'],
})
export class HeaderOptionsComponent {
  faList = faList;
  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  userId$: Observable<string> = this.userIdService.getUserId();

  constructor(
    private breakpointService: DistBreakpointService,
    private userIdService: UserIdService,
  ) {}
}
